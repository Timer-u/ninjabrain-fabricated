# AGENTS.md — Ninjabrain-Fabricated

## Build

- **Java 25**, Gradle 9.5.1 (wrapper: `./gradlew` or `gradlew.bat`)
- Build: `./gradlew build` (runs compile, remap, jar; no tests exist)
- Run client: `./gradlew runClient`
- Only `ninjabrain-fabricated/` is the active project. Root `pom.xml` is the original desktop app (irrelevant).

## Source Sets

`splitEnvironmentSourceSets()` is active:
- `src/main/` — common code (algorithm, mod initializer)
- `src/client/` — client-only code (mixins, keybinds, auto-detection, HUD)
- Mixin configs are split too: `ninjabrain-fabricated.mixins.json` (main, empty) and `ninjabrain-fabricated.client.mixins.json` (client, active).

## Throw Detection Architecture

1. `EnderEyeItemMixin` (`use` HEAD) captures player position at throw time.
2. `AutoDetectionHandler` listens to `ENTITY_LOAD` for `EyeOfEnder` entities, tracks them in a map.
3. Each client tick it polls `eye.getDeltaMovement()` until non-zero, then derives: `angle = -Math.toDegrees(Math.atan2(vx, vz))`.
4. Measurement recorded = stored player pos + derived angle.
5. Works for both shatter (item drop) and break (no drop) cases — does NOT rely on `ENTITY_UNLOAD`.

## Config & Dependencies

- **Fabric API** (hard dep), **Cloth Config** (hard dep), **Mod Menu** (recommended, optional).
- Config file: `.minecraft/config/ninjabrain-fabricated.json` (Gson, not Cloth's built-in).
- Mod Menu entrypoint: `org.dpdns.timerverse.ninjabrain.client.config.ModMenuIntegration` (implements `ModMenuApi`).
- Config screen: `NinjabrainConfigScreen` (Cloth Config builder).

## Keybindings

- Reset, Undo, Lock/Unlock — **unbound by default** (`-1`), must be set in Options > Controls > Ninjabrain.
- Increment (Up), Decrement (Down) — default bound.

## HUD Overlay

- Always-on, injected via `InGameHudMixin` on `Hud.extractRenderState()` TAIL.
- Not F3-dependent, no third-party HUD required.
- Fully replaced MiniHUD dependency (none exists).

## Known Quirks

- `DiscretizedDensity.java`: LSP plugins may show false-positive errors; the actual file compiles cleanly.
- No tests. CI (`./gradlew build`) verifies compilation only.
- Fabric Loom 1.17-SNAPSHOT, Minecraft 26.2, Fabric Loader >=0.19.3.
- Mod is `"environment": "client"` only.
