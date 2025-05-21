package kr.rth.picoserver.menu;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public class menuInv implements InventoryHolder {
    Inventory inv ;
    String menuId ;
    Boolean isEdit;

    @Override
    public Inventory getInventory() {
        return inv;
    }
    public menuInv (String menuId, String Title, Integer size, Boolean isEdit) {
        this.menuId = menuId;
        this.isEdit = isEdit;

        if(size == 0) {
            inv = Bukkit.createInventory(this , InventoryType.HOPPER, Title);

        }else{
            inv = Bukkit.createInventory(this, size, Title);
        }
    }
}
