package kr.rth.picoserver.defualtItem;

import com.google.gson.Gson;
import kr.rth.picoserver.Database;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import static kr.rth.picoserver.util.itemStackSerializer.itemStackSerializer;

public class EventListener implements Listener {
    @EventHandler
    public void onClose(InventoryCloseEvent e) {
        if(!(e.getView().getTitle().trim().equals("기본템 수정" ) && e.getPlayer().isOp()) ) return;
        Player p = (Player) e.getPlayer();
        Inventory closedInv = e.getInventory();

        HashMap<Integer, String> a=  new HashMap<>();
        for(int i = 0; i < closedInv.getSize(); i ++) {
            ItemStack stack = closedInv.getItem(i);
            if(stack != null) {
                a.put(i, itemStackSerializer(stack));
            }
        }
        Gson gson = new Gson();
        ArrayList<Object> q =  new ArrayList<>();
        q.add(gson.toJson(a));
        try {
            Database.getInstance().execute("UPDATE keyv SET data = ? WHERE name = 'defaultItem'", q);
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }


    }
}
