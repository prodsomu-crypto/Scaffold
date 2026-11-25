# StudioTouch — Android music-studio prototype

An independent Android implementation inspired by the documented workflow of KiraStudio. This is **not** KiraStudio, does not contain its source code or proprietary assets, and is not feature-complete.

## Current prototype
- Touch-first dark workspace
- Touch-editable 4-track pattern grid
- Tempo preset selector
- Local auto-save and restore for tempo and pattern steps
- On-screen piano keyboard with a synthesized single-oscillator note preview
- Project panel and Piano Roll, Automation, and Mixer layout placeholders
- GitHub Actions workflow to build and upload a debug APK

## Build on GitHub (phone-friendly)
1. Open **Actions** and allow workflows if GitHub asks.
2. The **Build Android APK** workflow runs on each push to `main`, or can be started manually.
3. Open the completed workflow run and download the `StudioTouch-debug-apk` artifact.
4. Extract the artifact ZIP on your phone, then install `app-debug.apk`.

## Development roadmap
1. Sequencer data model, song structure, patterns, tempo/time signatures, undo/redo.
2. Piano roll: touch note creation, resize/move, velocity, snapping, selection, copy/paste.
3. Audio engine: low-latency playback, synthesis voices, mixing and scheduling.
4. Instrument generators: generic waveforms, noise, samples, SoundFont, wavetable and retro-style generators.
5. Envelopes and automation: ADSR, custom curves, sequences, LFO, automation tracks.
6. Effects: volume, pan, filters, EQ, reverb, chorus, delay, bit crusher, compressor and effect chains.
7. Sample/wavetable editors, project explorer, autosave and project file format.
8. WAV/OGG and MIDI export; import and compatibility tests.
9. Mobile gestures: tap, swipe-pan, pinch-zoom, long-press selection, precision slider gestures, configurable toolbar and pop-up piano.

## Tech
- Kotlin, Jetpack Compose, Material 3
- Planned native audio engine: C++/NDK
- GitHub Actions debug APK build

## Status
The current build can preview notes from the keyboard and auto-save tempo and step patterns locally on the device. The sequencer transport is still a UI control: timed multi-track playback, instrument editing, effects, automation, project-file import/export, and audio export are not implemented yet.
