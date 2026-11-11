# Spear Attribute Swap Fixer

A server-side Paper plugin. Swipe your hotbar to a spear and the spear's jab attack, including the Lunge dash, has no attack cooldown for a short, configurable window. If you do not swipe, the normal vanilla cooldown applies.

## How it works

- Swiping the hotbar to a spear starts a zero-cooldown window.
- During the window, a temporary attack speed modifier is applied to the player. The spear cooldown check runs on the client using the attack speed attribute, so the modifier is what makes the jab available. The server-side attack cooldown counter is also kept full.
- The modifier is removed when the window ends, when the player swipes to a non-spear item, when they leave, or when the plugin is disabled.

## Vanilla Lunge rules still apply

The plugin only removes the cooldown, so Lunge is still blocked by:
- Hunger of 6 points or less
- Water, riding a mount, or gliding with an elytra
- Charge attacks (Lunge only works on the jab)

## Compatibility

- Paper (and forks such as Purpur) 1.21.11 to 26.3
- Java 21
- Not for Spigot or Bukkit. Not tested on Folia.

## Installation

1. Download the jar from the Releases page or Modrinth.
2. Put it in your server's `plugins/` folder.
3. Restart the server.

## Commands

| Command | Permission | Description |
|---|---|---|
| `/spearswapfixer` (aliases `/ssf`, `/spearswarpfixer`) | `spearswapfixer.use` (default: op) | Toggle on or off |

## Config

```yaml
enabled: true
attack-speed-boost: 1000      # temporary attack speed bonus; must be large, do not set to 0
zero-cooldown-ticks: 60       # window after a swipe (20 ticks = 1 second); 0 = until the player swipes away
hand-swap-triggers: false     # also trigger with the offhand swap key
lunge-only: false             # only count spears that have the Lunge enchantment
```

## Building

Requires Java 21 and Maven.

```sh
mvn clean package
# output: target/SpearAttributeSwapFixer-2.1.0.jar
```

## Credits

Inspired by Paper Attribute Swap Fixer by SalmonBlock.

## AI disclosure

Parts of this project were written with the help of AI (Claude).

## License

[LGPL-3.0-only](LICENSE)
