# Minecraft Jetpack Plugin

Skyguard Jetpack is a Minecraft plugin that adds a protected jetpack Elytra with flight boosting, Elytra-style gliding, Protection IV, a crafting recipe, and a `/jetpack` command for obtaining the item.

## Install

1. Build the plugin with Java 21 and Maven:
   ```bash
   mvn package
   ```
2. Copy the generated `target/plugin.jar` file into your Paper server's `plugins` directory.
3. Start or restart the server.
4. Confirm that the plugin is enabled in the server console.

## Use

1. Give yourself a jetpack with:
   ```text
   /jetpack
   ```
2. Wear the Jetpack in your chestplate slot.
3. Start gliding normally like an Elytra.
4. While sneaking, right-click in the air or on a block to activate the boost.
5. The boost launches you forward and upward and automatically starts gliding.
6. Boosts have a 1.5-second cooldown.
7. Players with the `jetpack.use` permission can use `/jetpack`. This permission is granted to operators by default.

The Jetpack can also be crafted with this recipe:

```text
I F I
C E C
I F I
```

Recipe ingredients:

- `I`: Iron Ingot
- `F`: Firework Rocket
- `C`: Copper Ingot
- `E`: Elytra

---

More Minecraft plugin projects by EnderDevelopment: https://github.com/EnderDevelopment

Project page: https://dash.enderdevelopment.com/community/12b793c2-59a4-4fe2-b490-babc9e9913e3?utm_source=github&utm_medium=readme&utm_campaign=auto_publish&utm_content=12b793c2-59a4-4fe2-b490-babc9e9913e3
