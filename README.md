# Blindfold Chess

An Android app for practicing **blindfold chess** — playing without seeing the board — paired with a custom C++ chess engine written from scratch.

The app helps players build the mental visualization skills blindfold chess requires: tracking piece positions, calculating lines, and recognizing patterns without a visible board. It combines a playable chess engine, a set of visualization drills, and puzzle training in one package.

## Features

- **Play** — Play a full game against the built-in native engine, with configurable game variants and side selection.
- **Train** — A dedicated trainer with several blindfold-specific exercises:
  - Coordinate recognition
  - Knight move calculation
  - Same-color square identification
  - Same-diagonal square identification
  - "Sister square" recognition
  - Square color identification
  - Timed drills with configurable move/question counts
- **Tactics puzzles** — Solve tactical puzzles pulled from a local puzzle database (FEN + solution + rating + theme).
- **History** — Games are saved locally (with move logs and board snapshots) and can be reviewed or replayed move-by-move afterward.
- **Board themes** — Over 20 selectable board skins (wood, marble, metal, horsey, etc.), stored per-user in preferences.
- **Sound** — In-game sound effects via a dedicated sound manager.

## Project structure

```
blindfold-chess/
├── android/          # Android application (Kotlin)
│   └── app/
│       ├── src/main/java/...   # UI, trainer, history, settings, Room database
│       └── src/main/cpp/       # JNI bridge + copy of the engine, built via CMake
└── engine/           # Standalone build of the chess engine (C++)
    ├── src/          # Bitboards, move generation, search, evaluation, transposition table
    └── main.cpp      # UCI entry point for engine-only builds/testing
```

The chess engine lives in two places: `engine/` is a standalone command-line build (useful for testing the engine in isolation via the UCI protocol), and `android/app/src/main/cpp/` contains the same engine compiled into a native library (`libblindfoldchess.so`) that the app talks to through JNI (see `Engine.kt`).

## The engine

A bitboard-based chess engine implemented in C++:

- Precomputed attack tables and bitboard move generation
- Alpha-beta search with a transposition table (Zobrist hashing)
- Standalone UCI (Universal Chess Interface) support for testing outside the app
- Exposed to Kotlin via JNI (`initEngine`, `setPosition`, `getBestMove`, `getBoard`, `makeMove`, `isCheckmate`, `isDraw`)

## Getting started

### Android app

Requirements: Android Studio, NDK/CMake support (for the native engine), min SDK 26.

```bash
cd android
./gradlew assembleDebug
```

Or open the `android/` folder directly in Android Studio and run the `app` configuration on a device or emulator.

### Standalone engine

The engine can be built and run independently of the Android app, useful for testing search/evaluation changes without rebuilding the app:

```bash
cd engine
cmake -B build
cmake --build build
./build/chess   # speaks UCI over stdin/stdout
```

## Tech stack

- **App**: Kotlin, Android Jetpack (Navigation Component, Room, ViewBinding, Fragments)
- **Engine**: C++17, CMake, JNI
