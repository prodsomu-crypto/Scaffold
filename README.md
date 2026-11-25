# StudioTouch — Android music-studio prototype

An independent Android implementation inspired by the documented workflow of KiraStudio. This is **not** KiraStudio, does not contain its source code or proprietary assets, and is not feature-complete.

## Current prototype
- Touch-first dark workspace
- Sequencer grid with tappable pattern cells
- Project panel placeholder
- Piano-roll grid placeholder
- Transport UI placeholder
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
This initial commit is a UI prototype. It does not yet generate or play sound, save projects, or export audio.
