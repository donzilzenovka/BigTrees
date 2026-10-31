# BigTrees 0.3 (Minecraft 1.7.10)

Maintenance fork of BigTrees by Karob, based on vidaj's 1.7.10 branch.

## Build

Uses GTNH's RetroFuturaGradle 2.0.2 instead of ForgeGradle 1.2. The mod
still targets Minecraft 1.7.10 / Forge 10.13.4.1614 and Java 8 bytecode,
so it can be used in modern Java / lwjgl3ify 1.7.10 installations.

Use a supported JDK for Gradle (Java 21 recommended), with a Java 8
toolchain available for compilation. On Windows:

```bat
gradlew.bat build
```

The compiled mod jar is placed in `build/libs/`. Do not put the
sources or dev jar in the modpack.

## Maintenance changes

- Existing tree configs load using the correct algorithm-specific defaults.
- Invalid tree configs identify the offending file.
- Non-CFG files in the tree config directory are ignored.

This migration has not yet been verified by a successful build or in-game test.
