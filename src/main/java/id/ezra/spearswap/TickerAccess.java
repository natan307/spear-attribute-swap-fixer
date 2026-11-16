package id.ezra.spearswap;

import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.logging.Logger;

/** Reflection wrapper around the player's attack strength ticker (the attack cooldown counter). */
final class TickerAccess {
    /** Big enough that the cooldown scale is clamped to 1.0 for any weapon. */
    static final int FULL = 100_000;

    private final Logger log;
    private Method handleGetter;
    private Field ticker;
    private boolean broken;

    TickerAccess(Logger log) {
        this.log = log;
    }

    void fill(Player player) {
        if (broken) return;
        try {
            if (ticker == null && !resolve(player)) return;
            ticker.setInt(handleGetter.invoke(player), FULL);
        } catch (Exception ignored) {
            // transient failure, try again on the next call
        }
    }

    private boolean resolve(Player player) {
        try {
            handleGetter = player.getClass().getMethod("getHandle");
            Object handle = handleGetter.invoke(player);
            for (Class<?> c = handle.getClass(); c != null; c = c.getSuperclass()) {
                for (Field f : c.getDeclaredFields()) {
                    if (f.getType() == int.class && f.getName().equals("attackStrengthTicker")) {
                        f.setAccessible(true);
                        ticker = f;
                        return true;
                    }
                }
            }
            log.info("Server-side cooldown counter not accessible on this server (normal on Spigot); using the attack speed modifier only.");
        } catch (Exception e) {
            log.info("Cooldown counter reflection unavailable (" + e.getMessage() + "); using the attack speed modifier only.");
        }
        broken = true;
        return false;
    }
}
