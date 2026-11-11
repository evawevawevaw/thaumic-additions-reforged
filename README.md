# Thaumic Additions: port to Minecraft 1.21.1

Unofficial port of Thaumic Additions by Zeith from Minecraft 1.7.10 to Minecraft 1.21.1 on NeoForge. It is an addon for [Thaumcraft 4 Reforged](https://github.com/notlikethiss/thaumcraft-4-reforged).

The goal is to recreate the original gameplay as closely as possible: the infuser and its activator, the condenser, crystallizer, restorer, thaumic and dark generators, the hyper node creator, seals, the void chest, tile boosters, vis conduits, magic tanks, adaminite and mithrillium gear, foci, upgrades, tainted crops and the eldritch monolith. The technical side is rebuilt on modern APIs, with JEI, REI and EMI recipe integration.

> Thaumic Additions and its assets belong to Zeith. This port is provided under the MIT license for its own code; third-party material keeps its original terms, see `THIRD_PARTY_NOTICES.md`.

## Requirements

- Minecraft 1.21.1, NeoForge, Java 21
- [Thaumcraft 4 Reforged](https://github.com/notlikethiss/thaumcraft-4-reforged/releases/tag/1.21.1) 1.1.2 or newer (required)
- TerraBlender and Curios (required)
- JEI, REI or EMI (optional)

Versions used for the build:

- Mod 0.1.0
- Thaumcraft 4 Reforged 1.1.3
- TerraBlender 1.21.1-4.1.0.1
- Curios 9.5.1+1.21.1
- JEI 19.57.0.451

## Releases

Builds are published on the [Releases](https://github.com/evawevawevaw/thaumic-additions-reforged/releases) page. The mod version comes from `mod_version` in `gradle.properties`.

## Differences from the original

Granum is now crafted from Herba + Victus.
Energy generation rebalanced to 60 FE/tick. The condenser burns one crystal per 600 working ticks, down from 24,000.
Energy system: NeoForge Energy replaces the old RF/RJ.
Cisterns: added new recipes following your layout. The original registrations had none. The reinforced cistern takes a regular cistern in the center.
Filler books now show recipes. In the original, the book’s GUI was empty.
Hypernode Maker: all 36 slots are usable, not just the 27 that were displayed.
Seals: the two-symbol tilling seal, previously unreachable, now works. Added protected-area checks.
Teleportation goes straight between dimensions instead of building and removing a temporary obsidian frame.
GUIs are modernized, with EMI/JEI/REI integration.
The old bugs that lost, duplicated, or drove resource stocks negative were deliberately not reproduced.

## Building

Thaumcraft 4 Reforged is not bundled. Download its jar from the [release page](https://github.com/notlikethiss/thaumcraft-4-reforged/releases/tag/1.21.1), put it into `libs/` as `thaumcraft-4-reforged-1.1.3-1.21.1.jar` and run:

    ./gradlew build          # build the mod jar into build/libs
    ./gradlew runClient      # start a development client
    ./gradlew runServer      # start a development server
    ./gradlew verifyTransport # run the machine logic checks

## Project layout

    src/main/java/thaumicadditions/
      advancement/   advancements
      block/         blocks
      blockentity/   block entities
      client/        screens, renderers, HUD, particles and JEI/REI/EMI integration
      command/       commands
      compat/        compatibility with other mods
      config/        configuration
      crafting/      recipes and the infuser
      entity/        entities
      event/         event handlers
      item/          items, armor, foci and upgrades
      machine/       machine logic: upgrades, fluids, energy
      magic/         vis and essentia transport
      menu/          container menus
      registry/      content registration
      research/      research entries
      seal/          seals
      world/         world generation
    src/main/resources/   textures, models, sounds and lang files
    src/verification/     machine logic checks

## Credits

- [Zeith](https://www.curseforge.com/members/zeith/projects) for the original [Thaumic Additions](https://www.curseforge.com/minecraft/mc-mods/thaumic-additions/files/all?page=1&pageSize=20&version=1.7.10&showAlphaFiles=hide)
- [notlikethiss](https://github.com/notlikethiss) for [Thaumcraft 4 Reforged](https://github.com/notlikethiss/thaumcraft-4-reforged)
- The NeoForge and TerraBlender teams, the Curios and JEI authors

## License

[MIT](LICENSE) for the code of this port. See `THIRD_PARTY_NOTICES.md` for third-party material.
