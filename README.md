# ⚡ TicTacToe

A neon-themed TicTacToe game for Android with smooth animations, persistent score tracking, and a full adaptive app icon. Built with pure Android SDK — no third-party game libraries.

---

## Screenshots


| Game Board | Winner State | Draw State |
|:---:|:---:|:---:|
| <img width="340" height="714" alt="image" src="https://github.com/user-attachments/assets/6650beec-266d-4bc1-8ec1-595117162ac1" /> |<img width="343" height="714" alt="image" src="https://github.com/user-attachments/assets/8d64f6d7-8339-4cd9-8861-eba569f9e4f7" /> | <img width="337" height="723" alt="image" src="https://github.com/user-attachments/assets/2901ce93-c240-491a-84cf-7e9d50f09029" />
 |

---

## Features

- **Cyberpunk neon theme** — deep navy background with neon cyan, pink, and yellow accents
- **Smooth animations** — staggered intro, spring-pop marks, shake on invalid tap, win flash, draw pulse, and count-up scores
- **Winning highlight** — the three winning cells flash and pulse when the game ends
- **Persistent score tracking** — X wins, O wins, and draws are tracked across rounds
- **Adaptive app icon** — vector-based foreground and background layers for Android 8.0+
- **Clean architecture** — game logic fully separated in `TicTacToe.java` with no Android dependencies

---

## Project Structure

```
app/
└── src/
    └── main/
        ├── java/com/example/tictactoe/
        │   ├── MainActivity.java        # UI, animation, user interaction
        │   └── TicTacToe.java           # Pure game logic (no Android deps)
        ├── res/
        │   ├── layout/
        │   │   └── activity_main.xml    # Main screen layout
        │   ├── drawable/
        │   │   ├── ic_launcher_foreground.xml   # Adaptive icon foreground
        │   │   └── ic_launcher_background.xml   # Adaptive icon background
        │   ├── mipmap-anydpi-v26/
        │   │   ├── ic_launcher.xml              # Adaptive icon manifest
        │   │   └── ic_launcher_round.xml        # Round variant
        │   └── values/
        │       ├── colors.xml           # Cyberpunk neon color palette
        │       └── styles.xml           # App theme + CellButton style
        └── AndroidManifest.xml
```

---

## Tech Stack

| Component | Detail |
|---|---|
| Language | Java |
| Min SDK | API 21 (Android 5.0 Lollipop) |
| Target SDK | API 36 (Android 14) |
| UI | XML layouts + Material Components |
| Animations | `ObjectAnimator`, `ValueAnimator`, `ArgbEvaluator`, View `.animate()` |
| Icon format | Adaptive Icon (Android 8.0+) via Vector Drawable |

---

## Color Palette

| Name | Hex | Usage |
|---|---|---|
| Deep Navy | `#0A0E1A` | Background |
| Card Dark | `#131929` | Cell & card backgrounds |
| Neon Cyan | `#00F5FF` | Player X, grid bars, accents |
| Neon Pink | `#FF2D78` | Player O, reset scores button |
| Neon Yellow | `#FFE600` | Draw state, score label |
| Divider | `#1E2D4A` | Subtle grid lines |

---

## Animations Reference

| Trigger | Animation |
|---|---|
| App launch | Title slides in from top; cells cascade in with spring-overshoot |
| Cell tap (valid) | Mark pops in with `OvershootInterpolator(2f)` scale |
| Cell tap (occupied) | Cell shakes horizontally (`ObjectAnimator` on `translationX`) |
| Game won | Winning cells scale-pulse + color flash × 3; status label pulses |
| Game draw | Status text oscillates to neon yellow × 3 |
| Score update | Number counts up with `ValueAnimator`; score bounces with `BounceInterpolator` |
| New Game | Cells stagger-fade out, clear, then stagger-fade back in |
| Button press | 92% squeeze then bounce back |

---

## Getting Started

### Prerequisites

- Android Studio Hedgehog (2023.1.1) or newer
- JDK 17+
- Android SDK with API 36 platform installed

### Installation

1. Clone or download this repository.

2. Open Android Studio → **File → Open** → select the project root folder.

3. Let Gradle sync finish.

4. Ensure your `build.gradle (app)` includes the Material Components dependency:

```groovy
dependencies {
    implementation 'com.google.android.material:material:1.11.0'
    implementation 'androidx.cardview:cardview:1.0.0'
}
```

5. Run on a device or emulator:  
   **Run → Run 'app'** or press `Shift + F10`.

### Package name

If your package name differs from `com.example.tictactoe`, update the top line of both Java files:

```java
package com.your.package.name;
```

And update `AndroidManifest.xml` accordingly.

---

## How to Play

1. Player **X** always goes first.
2. Tap any empty cell to place your mark.
3. First player to get **3 in a row** (horizontal, vertical, or diagonal) wins.
4. If all 9 cells are filled with no winner, the round is a **draw**.
5. Tap **NEW GAME** to reset the board — scores are preserved.
6. Tap **RESET SCORES** to wipe scores and start fresh.

---

## Architecture Notes

### `TicTacToe.java`
Pure Java game logic class with zero Android imports. Responsible for:
- Board state (`char[3][3]`)
- Move validation and placement
- Win/draw detection across rows, columns, and diagonals
- Returning flat indices of winning cells for UI highlight
- Accumulating scores across rounds

### `MainActivity.java`
Handles everything visual:
- Binds views and wires click listeners
- Delegates all game decisions to `TicTacToe`
- Drives all animations (intro, mark pop, shake, win flash, draw pulse, count-up)
- Reads winning cell indices from `TicTacToe` to highlight the correct buttons

This separation means the game logic can be unit-tested independently of Android.

---

## Customization

### Changing colors
Edit `res/values/colors.xml`. The three key accent colors are:

```xml
<color name="neon_cyan">#00F5FF</color>   <!-- Player X -->
<color name="neon_pink">#FF2D78</color>   <!-- Player O -->
<color name="neon_yellow">#FFE600</color> <!-- Draw / scores -->
```

### Changing animation speed
In `MainActivity.java`, search for `.setDuration(...)` calls. For example, to slow down the mark-pop animation:

```java
// In animateCellMark()
btn.animate().scaleX(1f).scaleY(1f)
    .setDuration(500)   // default is 350
    ...
```

### Adding sound effects
Add a `SoundPool` in `MainActivity.java` and trigger sounds inside `onCellClicked()`, `highlightWinnerCells()`, and `animateDrawPulse()`.

---

## Contributing

1. Fork the repository
2. Create a feature branch: `git checkout -b feature/my-feature`
3. Commit your changes: `git commit -m "Add my feature"`
4. Push the branch: `git push origin feature/my-feature`
5. Open a Pull Request

---

## License

```
MIT License

Copyright (c) 2024

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
```

---

*Built with ❤️ and neon lights.*
