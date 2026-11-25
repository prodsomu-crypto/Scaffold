package org.studiotouch.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val Bg = Color(0xFF17191F)
private val Panel = Color(0xFF222630)
private val Panel2 = Color(0xFF2B303B)
private val Accent = Color(0xFF77D5C5)
private val TextMain = Color(0xFFE9EDF2)
private val TextMuted = Color(0xFF9AA4B2)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { StudioTouchApp() }
    }
}

@Composable
fun StudioTouchApp() {
    var playing by remember { mutableStateOf(false) }
    var activeTab by remember { mutableStateOf("Sequencer") }
    var showProject by remember { mutableStateOf(false) }
    var tempoMenuExpanded by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val preferences = remember(context) {
        context.getSharedPreferences("studiotouch_project", 0)
    }
    var tempo by remember { mutableIntStateOf(120) }
    var projectRestored by remember { mutableStateOf(false) }
    val tracks = listOf("Lead", "Bass", "Drums", "Pad")
    val patterns = remember {
        mutableStateListOf(
            mutableStateListOf(true, true, false, false, true, true, false, false),
            mutableStateListOf(true, false, true, false, true, false, true, false),
            mutableStateListOf(true, false, true, false, true, false, true, false),
            mutableStateListOf(false, false, true, true, false, false, true, true)
        )
    }

    LaunchedEffect(Unit) {
        tempo = preferences.getInt("tempo", 120).coerceIn(40, 240)
        preferences.getString("patterns", null)
            ?.split("|")
            ?.forEachIndexed { rowIndex, savedRow ->
                if (rowIndex < patterns.size) {
                    savedRow.forEachIndexed { columnIndex, value ->
                        if (columnIndex < patterns[rowIndex].size) {
                            patterns[rowIndex][columnIndex] = value == '1'
                        }
                    }
                }
            }
        projectRestored = true
    }

    val patternSnapshot = patterns.map { row ->
        row.joinToString("") { enabled -> if (enabled) "1" else "0" }
    }
    LaunchedEffect(tempo, patternSnapshot, projectRestored) {
        if (projectRestored) {
            preferences.edit()
                .putInt("tempo", tempo)
                .putString("patterns", patternSnapshot.joinToString("|"))
                .apply()
        }
    }

    MaterialTheme(colorScheme = darkColorScheme(
        background = Bg, surface = Panel, primary = Accent,
        onBackground = TextMain, onSurface = TextMain
    )) {
        Column(Modifier.fillMaxSize().background(Bg).padding(10.dp)) {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text("STUDIOTOUCH", color = Accent, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Text("Untitled project", color = TextMain, fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
                }
                TextButton(onClick = { showProject = !showProject }) { Text("PROJECT") }
                TextButton(onClick = { playing = !playing }) {
                    Text(if (playing) "STOP" else "PLAY", color = Accent, fontWeight = FontWeight.Bold)
                }
            }
            Spacer(Modifier.height(8.dp))
            Row(
                Modifier.fillMaxWidth().background(Panel, RoundedCornerShape(12.dp)).padding(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Box {
                    Text(
                        "$tempo BPM  ▾",
                        color = TextMain,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable { tempoMenuExpanded = true }
                    )
                    DropdownMenu(
                        expanded = tempoMenuExpanded,
                        onDismissRequest = { tempoMenuExpanded = false }
                    ) {
                        listOf(80, 90, 100, 110, 120, 128, 140, 160, 180, 200).forEach { bpm ->
                            DropdownMenuItem(
                                text = { Text("$bpm BPM") },
                                onClick = {
                                    tempo = bpm
                                    tempoMenuExpanded = false
                                }
                            )
                        }
                    }
                }
                Text("4/4", color = TextMuted)
                Text("BAR 01", color = TextMuted)
                Text(if (playing) "● PLAYING" else "■ STOPPED", color = if (playing) Accent else TextMuted)
                Text("UNDO  ↶", color = TextMuted)
            }
            Spacer(Modifier.height(10.dp))
            if (showProject) {
                Card(colors = CardDefaults.cardColors(containerColor = Panel)) {
                    Column(Modifier.padding(14.dp)) {
                        Text("PROJECT EXPLORER", color = Accent, fontWeight = FontWeight.Bold)
                        listOf("Songs", "Channels", "Instruments", "Effects", "Samples", "Wavetables").forEach {
                            Text("›  $it", color = TextMain, modifier = Modifier.fillMaxWidth().clickable { showProject = false }.padding(vertical = 8.dp))
                        }
                    }
                }
            } else {
                Text("SEQUENCER", color = TextMuted, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(6.dp))
                Row(Modifier.horizontalScroll(rememberScrollState())) {
                    Text("CHANNEL", color = TextMuted, modifier = Modifier.width(84.dp).padding(8.dp), fontSize = 10.sp)
                    (1..8).forEach { Text("P$it", color = TextMuted, modifier = Modifier.width(43.dp).padding(8.dp), fontSize = 10.sp) }
                }
                tracks.forEachIndexed { row, name ->
                    Row(
                        Modifier.fillMaxWidth().padding(vertical = 3.dp)
                            .background(Panel, RoundedCornerShape(8.dp))
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(name, color = TextMain, fontSize = 12.sp, fontWeight = FontWeight.Medium,
                            modifier = Modifier.width(84.dp).padding(start = 8.dp))
                        Row(Modifier.horizontalScroll(rememberScrollState())) {
                            patterns[row].forEachIndexed { col, enabled ->
                                Box(
                                    Modifier.padding(horizontal = 3.dp).size(width = 37.dp, height = 34.dp)
                                        .background(if (enabled) Accent.copy(alpha = 0.85f) else Panel2, RoundedCornerShape(5.dp))
                                        .border(1.dp, if (enabled) Accent else Color(0xFF3B414D), RoundedCornerShape(5.dp))
                                        .clickable { patterns[row][col] = !patterns[row][col] },
                                    contentAlignment = Alignment.Center
                                ) { Text(if (enabled) "●" else "+", color = if (enabled) Bg else TextMuted, fontSize = 12.sp) }
                            }
                        }
                    }
                }
                Spacer(Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("Sequencer", "Piano Roll", "Automation", "Mixer").forEach { tab ->
                        FilterChip(
                            selected = activeTab == tab,
                            onClick = { activeTab = tab },
                            label = { Text(tab, fontSize = 11.sp) }
                        )
                    }
                }
                Card(
                    modifier = Modifier.fillMaxWidth().weight(1f),
                    colors = CardDefaults.cardColors(containerColor = Panel)
                ) {
                    Column(Modifier.fillMaxSize().padding(12.dp)) {
                        Text(activeTab.uppercase(), color = Accent, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        Spacer(Modifier.height(8.dp))
                        when (activeTab) {
                            "Piano Roll" -> {
                                Text("Touch-editable note grid prototype", color = TextMuted, fontSize = 12.sp)
                                Spacer(Modifier.height(8.dp))
                                (0..7).forEach { r ->
                                    Row {
                                        Text("C${5-r}", color = TextMuted, modifier = Modifier.width(30.dp), fontSize = 10.sp)
                                        repeat(8) { c ->
                                            Box(Modifier.weight(1f).padding(2.dp).height(22.dp)
                                                .background(if ((r == 2 && c in 1..3) || (r == 4 && c in 5..6)) Accent else Panel2, RoundedCornerShape(3.dp)))
                                        }
                                    }
                                }
                            }
                            "Automation" -> Text("Automation curve editor — planned", color = TextMuted)
                            "Mixer" -> Text("Channel mixer and effect chains — planned", color = TextMuted)
                            else -> Text("Tap pattern blocks above to toggle them.", color = TextMuted)
                        }
                    }
                }
            }
            Spacer(Modifier.height(8.dp))
            Row(
                Modifier.fillMaxWidth().background(Panel, RoundedCornerShape(12.dp)).padding(8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("C", color = TextMuted)
                listOf("C", "D", "E", "F", "G", "A", "B", "C").forEach { note ->
                    Box(Modifier.weight(1f).padding(2.dp).height(54.dp)
                        .background(Color(0xFFE7EAF0), RoundedCornerShape(5.dp))
                        .clickable { AudioEngine.previewMidi(noteMidi = listOf(60, 62, 64, 65, 67, 69, 71, 72)[listOf("C", "D", "E", "F", "G", "A", "B", "C").indexOf(note)] ) }, contentAlignment = Alignment.BottomCenter) {
                        Text(note, color = Bg, fontSize = 10.sp, modifier = Modifier.padding(bottom = 5.dp))
                    }
                }
                Text("♪", color = Accent)
            }
            Text("Keyboard tone preview • patterns and tempo auto-save locally • full song playback is next",
                color = TextMuted, fontSize = 9.sp, modifier = Modifier.align(Alignment.CenterHorizontally).padding(top = 5.dp))
        }
    }
}
