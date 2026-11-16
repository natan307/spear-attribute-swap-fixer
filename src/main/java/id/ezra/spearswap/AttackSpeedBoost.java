package id.ezra.spearswap;

import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The spear jab/lunge cooldown check runs on the CLIENT using the attack speed attribute. A huge
 * temporary attack-speed modifier is synced to the client and makes the cooldown time ~0 ticks.
 *
 * Works on Paper (transient modifier) and on Spigot/Bukkit (regular modifier, always removed again).
 */
final class AttackSpeedBoost {
    /** Paper only: modifier that is never saved to player data. Null on Spigot/Bukkit. */
    private static final Method ADD_TRANSIENT = findTransient();

    private final NamespacedKey key;
    private final Plugin plugin;
    private final Set<UUID> applied = ConcurrentHashMap.newKeySet();

    AttackSpeedBoost(Plugin plugin) {
        this.plugin = plugin;
        this.key = new NamespacedKey(plugin, "zero-cooldown");
    }

    private static Method findTransient() {
        try {
            return AttributeInstance.class.getMethod("addTransientModifier", AttributeModifier.class);
        } catch (NoSuchMethodException e) {
            return null;
        }
    }

    void apply(Player p, double amount) {
        AttributeInstance inst = p.getAttribute(Attribute.ATTACK_SPEED);
        if (inst == null) return;
        if (hasOurs(inst)) {
            applied.add(p.getUniqueId());
            return;
        }
        AttributeModifier mod = new AttributeModifier(key, amount, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlotGroup.ANY);
        try {
            if (ADD_TRANSIENT != null) ADD_TRANSIENT.invoke(inst, mod);
            else inst.addModifier(mod);
            applied.add(p.getUniqueId());
        } catch (InvocationTargetException | IllegalAccessException | IllegalArgumentException e) {
            plugin.getLogger().warning("Could not apply attack speed modifier: " + e);
        }
    }

    void clear(Player p) {
        AttributeInstance inst = p.getAttribute(Attribute.ATTACK_SPEED);
        if (inst != null) {
            for (AttributeModifier m : new ArrayList<>(inst.getModifiers())) {
                if (key.equals(m.getKey())) {
                    try {
                        inst.removeModifier(m);
                    } catch (Exception ignored) {
                    }
                }
            }
        }
        applied.remove(p.getUniqueId());
    }

    private boolean hasOurs(AttributeInstance inst) {
        for (AttributeModifier m : inst.getModifiers()) {
            if (key.equals(m.getKey())) return true;
        }
        return false;
    }

    void forget(UUID id) {
        applied.remove(id);
    }

    Set<UUID> appliedIds() {
        return applied;
    }
}
