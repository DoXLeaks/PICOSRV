package kr.rth.picoserver.menu;

import com.google.gson.Gson;
import kr.rth.picoserver.Database;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import static kr.rth.picoserver.util.itemStackSerializer.itemStackSerializer;

public class EventListener implements Listener {
    @EventHandler
    public void onInvClose(InventoryCloseEvent e) throws SQLException {
        if( !(e.getInventory().getHolder() instanceof menuInv closedInv) ) return;

        if(!closedInv.isEdit) return;

        HashMap<String,String  > a= new HashMap<>();
        for( int i = 0; i < closedInv.getInventory().getSize(); i ++ ) {
            ItemStack stack = closedInv.getInventory().getItem(i);
            if(stack != null) {
                a.put(Integer.toString(i), itemStackSerializer(stack));
            }

        }
        Gson gson = new Gson();
        ArrayList<Object> q=  new ArrayList<>();
        q.add( gson.toJson(a) );
        q.add( closedInv.menuId );

        Database.getInstance().execute("UPDATE menu SET invContent = ? WHERE id = ?", q);


    }
    @EventHandler
    public void invCLick(InventoryClickEvent e) throws SQLException {
        if(e.getClickedInventory() == null) return;
        if( !(e.getView().getTopInventory().getHolder() instanceof menuInv menuInv) ) return;

        if(e.getClickedInventory().getType().equals(InventoryType.PLAYER)){
            if (!menuInv.isEdit) {
                e.setCancelled(true);
            }
            return;
        }

        if(!menuInv.isEdit) {
            e.setCancelled(true);
        }else {
            return;
        }
        ArrayList<Object> q=  new ArrayList<>();
        q.add(menuInv.menuId);
        var dbRes1 = Database.getInstance().execute("SELECT * FROM menu WHERE id = ?", q).get(0);
        Gson gson = new Gson();
        HashMap<String, String> a = new HashMap<>();
        a = gson.fromJson((String) dbRes1.get("commandList"), a.getClass());
        if(a.get(Integer.toString(e.getSlot())) != null) {
            if(!e.getWhoClicked().isOp()) {
                try {
                    e.getWhoClicked().setOp(true);
                    ((Player) e.getWhoClicked()).performCommand(a.get(Integer.toString(e.getSlot())));
                }finally {
                    e.getWhoClicked().setOp(false);
                }

            }else {
                ((Player) e.getWhoClicked()).performCommand(a.get(Integer.toString(e.getSlot())));
            }
            ((Player) e.getWhoClicked()).playSound(e.getWhoClicked(), Sound.UI_BUTTON_CLICK, 2f, 10f);

        }
    }
}
