# Typing Race Simulator

A Java simulation of a typing race between several "typists," each with their own accuracy rating, racing to complete a passage of text. Built as an Object-Oriented Programming coursework project (ECS414U), in two parts: a text-based console simulation, and a full Swing GUI version with race configuration, per-typist setup, and a results/stats screen.

Each typist advances character by character every turn, but accuracy introduces risk: a typist can mistype and slide backwards, or "burn out" from typing too fast for too long and lose a turn recovering. The simulation models this with randomised per-turn checks rather than a fixed typing speed, so races are different every time.

## Project structure

```
.
├── Part1/    # Text-based console simulation
│   ├── Typist.java
│   └── TypingRace.java
└── Part2/    # Swing GUI simulation
    ├── Typist.java
    ├── TypingRace.java
    └── DocumentSizeFilter.java
```

## Part 1 — Console simulation

A command-line race between three typists, printed to the terminal and redrawn every turn.

- Each typist is shown on their own lane, with a marker for their current position, burnout state (`[zz]`) and whether they just mistyped (`[<]`).
- Progress, mistypes and burnout are all driven by each typist's accuracy rating: higher accuracy means fewer mistypes but a greater chance of burning out from pushing too hard, modelled as a burnout chance that builds up the more a typist types.
- A live words-per-minute (WPM) estimate is shown for each typist.
- The race ends — and the winner is printed — as soon as one typist reaches the end of the passage.

### How to compile and run

```bash
cd Part1
javac Typist.java TypingRace.java
java TypingRace
```

`TypingRace.java` includes a ready-to-run `main` method that sets up a three-typist race over a 20-character passage. Edit the typists, their accuracy values, or the passage length directly in `main` to try different setups.

## Part 2 — GUI simulation

A full Swing interface built on top of the same core simulation logic, adding:

- **Race configuration screen** — choose a passage (short/medium/long, or write a custom one up to 200 characters) and the number of racing typists (2–6).
- **Difficulty options** — toggle switches for *Autocorrect*, *Caffeine mode* and *Night shift*, intended to adjust the race's risk/reward balance (currently wired into the configuration screen but not yet affecting the simulation itself — see Known limitations).
- **Per-typist setup screen** — for each typist, choose a typing style, keyboard type, a custom single-character symbol, and accessories (wrist support, energy drink, noise-cancelling headphones).
- **Live race view** — each typist gets their own scrollable text lane showing progress through the passage in real time.
- **Results screen** — shows each typist's WPM, best WPM across races, accuracy percentage, and burnout count, plus a running history of every past race (position, WPM, accuracy, burnouts). Winning a race gives a typist a small permanent accuracy boost, and the results screen lets you restart and race again with the same typists.

### How to compile and run

```bash
cd Part2
javac Typist.java TypingRace.java DocumentSizeFilter.java
java TypingRace
```

This runs `TypingRace.main`, which opens the race configuration window.

## Dependencies

- Java Development Kit (JDK) 11 or higher
- Part 1 has no external dependencies
- Part 2 uses Java Swing and AWT (both included in the standard JDK) and `java.util` collections (`HashMap`, `List`, `ArrayList`)

## Known limitations

- The Autocorrect, Caffeine mode and Night shift checkboxes on the configuration screen are collected and passed through the GUI flow, but aren't yet read by the simulation logic (`advanceTypist`/`turn`), so they currently have no effect on race outcomes.
- The starter code for Part 1 (and the core simulation logic it's based on) was provided with some deliberately incomplete and buggy sections as part of the coursework; most of this has been implemented and fixed, but the project was built as a learning exercise rather than a production tool.

## Author

**Rahul Rai**
