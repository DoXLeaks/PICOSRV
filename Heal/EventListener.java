package kr.rth.picoserver.Heal;

import kr.rth.picoserver.PICOSERVER;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;

public class EventListener implements Listener {
//    @EventHandler
    public void onDeath(PlayerDeathEvent e) {
        CommandExcutor.cooldown.remove(e.getEntity().getUniqueId());
    }

    @EventHandler
    public void PlayerCommandEvent(PlayerCommandPreprocessEvent e) {
        if(e.getMessage().startsWith("/heal") ) {
            e.setCancelled(true);
//            e.getPlayer().performCommand("PICOSERVER:heal");
//            Bukkit.dispatchCommand(e.getPlayer(), "heal");
            PICOSERVER.dispatchCommand(e.getPlayer(), "PICOSERVER:heal");
        }

    }
    @EventHandler
    public void onF(PlayerSwapHandItemsEvent e) {
        if(e.getPlayer().isSneaking()) {
//            Bukkit.dispatchCommand(e.getPlayer(), "PICOSERVER:heal");
            PICOSERVER.dispatchCommand(e.getPlayer(), "PICOSERVER:heal");

//            Bukkit.dispatchCommand()
//            e.getPlayer().performCommand();
        }
    }
}
