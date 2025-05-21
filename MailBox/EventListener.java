package kr.rth.picoserver.MailBox;


import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import static kr.rth.picoserver.util.getFreeInventorySpace.getFreeInventorySpace;

public class EventListener implements Listener {
    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();
        if(e.getClickedInventory() == null) return;


        if(! ChatColor.stripColor(e.getView().getTitle()).trim().startsWith(":offset_-48::apdlf:")) return;
        if(e.getClick().equals(ClickType.NUMBER_KEY)){
            e.setCancelled(true);
            return;
        }
        if( !(e.getInventory().getHolder() instanceof  boxInv) && ChatColor.stripColor(e.getView().getTitle()).trim().startsWith(":offset_-48::apdlf:") ){
            e.setCancelled(true);
            e.getView().close();
            return;
        }
        if(!(e.getInventory().getHolder() instanceof  boxInv)) return;
        if(e.getClickedInventory().equals(p.getInventory())) {
            e.setCancelled(true);
            return;
        }
        ItemStack clickedItem = e.getCurrentItem();
        if(clickedItem == null) return;
        if(clickedItem.getType().equals(Material.AIR)) return;
        e.setCancelled(true);
        boxInv boxinv = (boxInv) e.getView().getInventory(1).getHolder();
        if(getFreeInventorySpace(p) < 1) {
            p.playSound(p, Sound.BLOCK_LANTERN_BREAK, 1, 1f);
            return;
        }
        boxinv.getInventory().setItem(e.getSlot(), null);
        p.getInventory().addItem(clickedItem);
        p.playSound(p, Sound.ENTITY_BEE_POLLINATE, 2, 1f);
        p.playSound(p, Sound.BLOCK_HONEY_BLOCK_STEP, 2, 1f);
        boxinv.save();
    }
}
