package kr.rth.picoserver.playingReward;

import com.google.gson.Gson;
import kr.rth.picoserver.Database;
import org.bukkit.ChatColor;
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
    public void onInvClose(InventoryCloseEvent e) throws SQLException {
        if(! (e.getPlayer().isOp())  || !(ChatColor.stripColor(e.getView().getTitle()).trim().startsWith("사전예약보상설정") ) ) return;
        Inventory closedInv = e.getInventory() ;
        HashMap<Integer, String> invCtc = new HashMap<>();

        for ( int i = 0; i < closedInv.getSize(); i ++ ) {
            ItemStack stack = closedInv.getItem(i);
            if( stack != null) {
                invCtc.put(i, itemStackSerializer( stack) );
            }
        }

        Gson gson = new Gson();
        ArrayList<Object> a = new ArrayList<>();
        a.add(
        gson.toJson(invCtc)
        );

        Database.getInstance().execute("UPDATE keyv SET data = ? WHERE name = 'prereversedRewardInv'", a);



    }
}
