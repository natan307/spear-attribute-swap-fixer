# Spear Attribute Swap Fixer

Paper 1.21.11 – 26.3. Swipe your hotbar to a spear and attacks/lunges have **zero cooldown**
for a configurable window (spam allowed). No swipe = normal vanilla cooldown.

- Command: `/spearswapfixer` (aliases `/ssf`, `/spearswarpfixer`) — permission `spearswapfixer.use` (op)
- Lunge needs >= 6 hunger points, and no water/elytra/riding (vanilla rules)
- Config: `attack-speed-boost`, `zero-cooldown-ticks`, `hand-swap-triggers`, `lunge-only`
- Build: `mvn clean package` (Java 21) -> `target/SpearAttributeSwapFixer-2.1.0.jar`
