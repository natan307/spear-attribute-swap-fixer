package id.ezra.spearswap;

import org.bukkit.entity.Player;

import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** Keeps track of which players are currently in the zero-cooldown state. */
final class SwipeTracker {
    private static final long FOREVER = Long.MAX_VALUE;

    private final Map<UUID, Long> until = new ConcurrentHashMap<>();
    private long now;

    void advance() {
        now++;
    }

    void arm(Player p, int ticks) {
        until.put(p.getUniqueId(), ticks <= 0 ? FOREVER : now + ticks);
    }

    void disarm(UUID id) {
        until.remove(id);
    }

    boolean isActive(UUID id) {
        Long end = until.get(id);
        if (end == null) return false;
        if (now > end) {
            until.remove(id);
            return false;
        }
        return true;
    }

    Iterator<UUID> activeIds() {
        return until.keySet().iterator();
    }

    void clear() {
        until.clear();
    }
}
