package id.ezra.spearswap;

import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The spear jab/lunge cooldown check runs on the CLIENT using the attack speed attribute, so changing
 * the server-side ticker alone is not enough. A huge temporary attack-speed modifier is synced to the
 * client and makes the cooldown time ~0 ticks, so the jab (and therefore the lunge dash) is always ready.
 */
final class AttackSpeedBoost {
    private final NamespacedKey key;
    private final Set<UUID> applied = ConcurrentHashMap.newKeySet();

    AttackSpeedBoost(Plugin plugin) {
        this.key = new NamespacedKey(plugin, "zero-cooldown");
    }

    void apply(Player p, double amount) {
        AttributeInstance inst = p.getAttribute(Attribute.ATTACK_SPEED);
        if (inst == null) return;
        try {
            inst.addTransientModifier(new AttributeModifier(key, amount, AttributeModifier.Operation.ADD_NUMBER));
        } catch (IllegalArgumentException alreadyThere) {
            // modifier already applied, nothing to do
        }
        applied.add(p.getUniqueId());
    }

    void clear(Player p) {
        AttributeInstance inst = p.getAttribute(Attribute.ATTACK_SPEED);
        if (inst != null) {
            try {
                inst.removeModifier(key);
            } catch (Exception ignored) {
            }
        }
        applied.remove(p.getUniqueId());
    }

    void forget(UUID id) {
        applied.remove(id);
    }

    Set<UUID> appliedIds() {
        return applied;
    }
}
