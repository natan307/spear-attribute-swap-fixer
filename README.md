# Spear Attribute Swap Fixer

A server-side Paper plugin. Every time you swipe your hotbar to a spear, you get **one** spear jab with no attack cooldown, so the Lunge dash is instantly available. After that jab, the normal vanilla cooldown applies until your next swipe. Without a swipe, nothing changes.

## How it works

1. Swiping the hotbar to a spear arms one free jab.
2. A temporary attack speed modifier is applied to the player. The spear cooldown check runs on the client using the attack speed attribute, so the modifier is what makes the jab (and the Lunge dash on it) available. The server-side attack cooldown counter is also filled for that attack.
3. As soon as the jab is used, the modifier is removed and the normal cooldown takes over.
4. If the free jab is not used, it expires after a configurable time (default 2 seconds), or when the player swipes to a non-spear item, leaves the server, or the plugin is disabled.
5. Swiping to a spear again arms another free jab.

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
zero-cooldown-ticks: 40       # how long the free jab stays available after a swipe (20 ticks = 1 second); 0 = until used or the player swipes away
attack-speed-boost: 1000      # temporary attack speed bonus; must be large, do not set to 0
hand-swap-triggers: false     # also trigger with the offhand swap key
lunge-only: false             # only count spears that have the Lunge enchantment
```

## Building

Requires Java 21 and Maven.

```sh
mvn clean package
# output: target/SpearAttributeSwapFixer-2.2.0.jar
```

## Credits

Inspired by Paper Attribute Swap Fixer by SalmonBlock.

## AI disclosure

Parts of this project were written with the help of AI (Claude).

## License

[LGPL-3.0-only](LICENSE)
