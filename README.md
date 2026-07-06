# Ninjabrain-Fabricated

A Fabric mod for Minecraft 26.2 (1.21+) that ports the [Ninjabrain Bot](https://github.com/Ninjabrain1/Ninjabrain-Bot) stronghold triangulation algorithm into a fully automatic client-side mod. Uses Bayesian inference to predict stronghold locations from ender eye throws — no manual angle input needed.

## Requirements

- Minecraft 26.2
- Fabric Loader >=0.19.3
- Fabric API
- Cloth Config >=26.2

Optional:
- Mod Menu (for an in-game config screen)

## Installation

1. Install Fabric Loader for Minecraft 26.2
2. Download Fabric API and Cloth Config, place them in your `mods` folder
3. Place `ninjabrain-fabricated.jar` in your `mods` folder
4. (Optional) Install Mod Menu for configuration screen
5. Launch the game

## Usage — Fully Automatic

1. Throw an ender eye
2. Walk to where it lands
3. Wait for the eye to pop — the measurement is recorded **automatically**
4. Repeat for at least 2 throws (ideally 100+ blocks apart) for triangulation

The overlay appears in the top-left corner after the first measurement.

## Overlay

The stronghold overlay is drawn automatically on screen when at least one throw is recorded. It shows:
- Number of recorded throws
- Top predicted stronghold chunks (rank, 8x8 coordinates, certainty, distance in blocks)
- Best prediction's 4x4 chunk coordinates

## Configuration

- With Mod Menu installed, click **Mods → Ninjabrain-Fabricated → Config**
- Or edit `config/ninjabrain-fabricated.json` manually

Settings:
- **Standard Deviation** — Angle measurement uncertainty (default 0.05)
- **Alt Standard Deviation** — Alternative std dev for toggled throws (default 0.10)
- **Crosshair Correction** — User calibration offset (default 0.0)
- **Number of Predictions** — How many top predictions to show (default 5)
- **Advanced Statistics** — Enable closest-stronghold correction (default off)

## Key Binds

| Key | Action | Description |
|-----|--------|-------------|
| **R** | Reset | Clear all throws and fossil data |
| **Z** | Undo | Remove the last throw |
| **L** | Lock/Unlock | Lock measurements to prevent accidental changes |
| **↑ (Up)** | Increment | Increase correction on the last throw (+0.01°, +1 increment) |
| **↓ (Down)** | Decrement | Decrease correction on the last throw (-0.01°, -1 increment) |

Configurable in **Options → Controls → Ninjabrain**.

## Features

- Fully automatic throw detection — captures player position and derives throw angle from the eye entity's flight trajectory
- Bayesian triangulation from 2+ throws
- Top predictions sorted by probability
- 4x4 and 8x8 chunk coordinate display
- Throw undo, reset, and manual angle correction
- Measurement lock
- Client-side only — no server mod required
- No dependencies beyond Fabric API and Cloth Config

## Algorithm

Bayesian inference over all 128,672 possible stronghold positions across 8 rings, updating the posterior distribution after each throw.

## License

GNU General Public License v3.0

---

> **Disclaimer**: Portions of this project's code and documentation were generated with the assistance of AI. They may contain inaccuracies or incomplete information. Please review all content carefully before use and make adjustments as needed based on the actual situation.
