package kr.rth.picoserver.HealthBar;

import kr.rth.picoserver.PICOSERVER;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.server.PluginEnableEvent;
//import me.neznamy.tab.api.

import static kr.rth.picoserver.HealthBar.HealthBarManager.update;

public class EventListener implements Listener {
    @EventHandler
    public void onEnable(PluginEnableEvent e) {
        for( var i : Bukkit.getOnlinePlayers()){
            update(i.getPlayer());
        }
    }
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
//        TAB
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(PICOSERVER.getInstance(), () -> {
            update(e.getPlayer());
        }, 10L);
    }
    @EventHandler
    public void onHealthChange(EntityDamageEvent e) {
        if(e.getEntity() instanceof Player) {
            update((Player) e.getEntity());
        }
    }

    @EventHandler
    public void onHealthChange(EntityRegainHealthEvent e) {
//        TAB
        if(e.getEntity() instanceof Player) {
            update((Player) e.getEntity());
        }
    }
}