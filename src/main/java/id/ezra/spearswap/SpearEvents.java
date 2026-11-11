package id.ezra.spearswap;

import com.destroystokyo.paper.event.player.PlayerAttackEntityCooldownResetEvent;
import io.papermc.paper.event.player.PrePlayerAttackEntityEvent;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerAnimationEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.ItemStack;

final class SpearEvents implements Listener {

    private final SpearSwapFixerPlugin plugin;
    private final SwipeTracker tracker;
    private final TickerAccess ticker;
    private final AttackSpeedBoost boost;

    SpearEvents(SpearSwapFixerPlugin plugin, SwipeTracker tracker, TickerAccess ticker, AttackSpeedBoost boost) {
        this.boost = boost;
        this.plugin = plugin;
        this.tracker = tracker;
        this.ticker = ticker;
    }

    // ---- swipe in: arm; swipe out to a non-spear: disarm ----

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void hotbar(PlayerItemHeldEvent e) {
        if (!plugin.isOn()) return;
        Player p = e.getPlayer();
        ItemStack target = p.getInventory().getItem(e.getNewSlot());
        if (counts(target)) {
            tracker.arm(p, plugin.getConfig().getInt("zero-cooldown-ticks", 60));
            boost.apply(p, plugin.getConfig().getDouble("attack-speed-boost", 1000));
            ticker.fill(p);
        } else {
            tracker.disarm(p.getUniqueId());
            boost.clear(p);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void handSwap(PlayerSwapHandItemsEvent e) {
        if (!plugin.isOn() || !plugin.getConfig().getBoolean("hand-swap-triggers", false)) return;
        Player p = e.getPlayer();
        if (counts(e.getMainHandItem())) {
            tracker.arm(p, plugin.getConfig().getInt("zero-cooldown-ticks", 60));
            boost.apply(p, plugin.getConfig().getDouble("attack-speed-boost", 1000));
            ticker.fill(p);
        } else {
            tracker.disarm(p.getUniqueId());
            boost.clear(p);
        }
    }

    // ---- while active: attacks are never on cooldown ----

    @EventHandler(priority = EventPriority.LOWEST)
    public void swing(PlayerAnimationEvent e) {
        topUp(e.getPlayer());
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void preAttack(PrePlayerAttackEntityEvent e) {
        topUp(e.getPlayer());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void noReset(PlayerAttackEntityCooldownResetEvent e) {
        Player p = e.getPlayer();
        if (plugin.isOn() && tracker.isActive(p.getUniqueId())) e.setCancelled(true);
    }

    @EventHandler
    public void quit(PlayerQuitEvent e) {
        tracker.disarm(e.getPlayer().getUniqueId());
        boost.forget(e.getPlayer().getUniqueId());
    }

    private void topUp(Player p) {
        if (plugin.isOn() && tracker.isActive(p.getUniqueId())) ticker.fill(p);
    }

    private boolean counts(ItemStack item) {
        if (item == null) return false;
        Material m = item.getType();
        if (!m.name().endsWith("_SPEAR")) return false;
        if (!plugin.getConfig().getBoolean("lunge-only", false)) return true;
        for (Enchantment ench : item.getEnchantments().keySet()) {
            if (ench.getKey().getKey().equals("lunge")) return true;
        }
        return false;
    }
}
