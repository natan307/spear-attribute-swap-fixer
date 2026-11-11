package id.ezra.spearswap;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Iterator;
import java.util.UUID;

public class SpearSwapFixerPlugin extends JavaPlugin {

    private final SwipeTracker tracker = new SwipeTracker();
    private TickerAccess ticker;
    private AttackSpeedBoost boost;
    private boolean enabledFlag;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        enabledFlag = getConfig().getBoolean("enabled", true);
        ticker = new TickerAccess(getLogger());
        boost = new AttackSpeedBoost(this);

        getServer().getPluginManager().registerEvents(new SpearEvents(this, tracker, ticker, boost), this);

        // every tick: advance the clock and keep everyone in the zero-cooldown state topped up
        Bukkit.getScheduler().runTaskTimer(this, () -> {
            tracker.advance();
            // drop the attack-speed boost as soon as the zero-cooldown state ends
            for (UUID id : boost.appliedIds()) {
                if (enabledFlag && tracker.isActive(id)) continue;
                Player owner = Bukkit.getPlayer(id);
                if (owner != null) boost.clear(owner); else boost.forget(id);
            }
            if (!enabledFlag) return;
            Iterator<UUID> it = tracker.activeIds();
            while (it.hasNext()) {
                UUID id = it.next();
                Player p = Bukkit.getPlayer(id);
                if (p == null) {
                    tracker.disarm(id);
                } else if (tracker.isActive(id)) {
                    ticker.fill(p);
                }
            }
        }, 1L, 1L);

        getLogger().info("Spear Attribute Swap Fixer " + (enabledFlag ? "ON" : "OFF"));
    }

    @Override
    public void onDisable() {
        tracker.clear();
        for (Player p : Bukkit.getOnlinePlayers()) boost.clear(p);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length == 1 && args[0].equalsIgnoreCase("credits")) {
            sender.sendMessage("§8" + Watermark.reveal());
            return true;
        }
        enabledFlag = !enabledFlag;
        if (!enabledFlag) tracker.clear();
        getConfig().set("enabled", enabledFlag);
        saveConfig();
        sender.sendMessage("§b[SpearSwap] §fZero-cooldown swipe: "
                + (enabledFlag ? "§aON" : "§cOFF"));
        return true;
    }

    boolean isOn() {
        return enabledFlag;
    }
}
