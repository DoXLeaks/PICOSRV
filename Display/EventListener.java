package kr.rth.picoserver.Display;

import com.google.gson.Gson;
import kr.rth.picoserver.Database;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.ArmorStand;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.inventory.Inventory;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static kr.rth.picoserver.util.itemStackSerializer.itemStackDeSerializer;
import static kr.rth.picoserver.util.itemStackSerializer.itemStackSerializer;

public class EventListener implements Listener {
    @EventHandler
    public void onKill(EntityDeathEvent e) {
        if(!(e.getEntity() instanceof ArmorStand as)) {
            return;
        }
        if( as.getCustomName() != null && as.getCustomName().trim().startsWith("전시품") ){
            try{
            e.getEntity().setHealth(e.getEntity().getMaxHealth());

            }
            catch(Exception eas) {}

        }
    }
    @EventHandler
    public void onKill(EntityDamageEvent e) {
        if(!(e.getEntity() instanceof ArmorStand as)) {
            return;
        }
        if( as.getCustomName() != null && as.getCustomName().trim().startsWith("전시품") ){
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onClickInventory (InventoryClickEvent e){
        if(!(e.getView().getTitle().startsWith("§0"))) {
            return;
        }
        e.setCancelled(true);
    }
    @EventHandler
    public void inventoryCloseEvent(InventoryCloseEvent e) throws SQLException {
        if( (!e.getView().getTitle().startsWith("수정모드 | ") || (!e.getPlayer().isOp()) ) ) {
            return;
        }

        String id = e.getView().getTitle().replace("수정모드 | ", "");
        ArrayList<Object> q2 = new ArrayList<>();
        q2.add(id);
        ArrayList<Map<String, Object>> dbRes1 = null;

        if( (Integer) new ArrayList<>(Database.getInstance().execute("SELECT EXISTS(SELECT * from displayList WHERE id=?)", q2).get(0).values()).get(0) < 1) {
            return;
        }
        HashMap<String, String> body = new HashMap<>();
        Inventory inv = e.getInventory();
        Gson gson = new Gson();

        for( int i = 0; i < e.getInventory().getSize(); i ++) {
            if(
                    inv.getItem(i) != null
            ) {
                body.put(String.valueOf(i), itemStackSerializer(inv.getItem(i)));
            }
        }
        ArrayList<Object> q = new ArrayList<>();
        q.add( gson.toJson(body) );
        q.add(id);

        try {
            Database.getInstance().execute("UPDATE displayList SET invContent=? WHERE id=?", q);
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }


    }


    @EventHandler
    public void onInteractEntity(PlayerInteractAtEntityEvent e) {
        if(!(e.getRightClicked() instanceof ArmorStand as)) {
            return;
        }

        if( !(as.getCustomName() != null && as.getCustomName().trim().startsWith("전시품")) ){
            return;
        }
        String id = as.getCustomName().trim().replace("전시품|", "");
        e.setCancelled(true);
        ArrayList<Object> q2 = new ArrayList<>();
        q2.add(id);
        ArrayList<Map<String, Object>> dbRes2 = null;
        try {
            dbRes2 = Database.getInstance().execute("SELECT * from displayList WHERE id=? ", q2);
        } catch (SQLException ea) {
            throw new RuntimeException(ea);
        }

        Gson gson = new Gson();
        HashMap<String, String> a = new HashMap<>();
        a = gson.fromJson((String) dbRes2.get(0).get("invContent"), a.getClass() );
        Inventory inv = Bukkit.createInventory(null, (int) dbRes2.get(0).get("invSize"), ChatColor.translateAlternateColorCodes('&',"&0" + dbRes2.get(0).get("title")));

        for( String i : a.keySet() ) {
            inv.setItem(Integer.parseInt(i), itemStackDeSerializer(a.get(i) ));
        }

        e.getPlayer().openInventory(inv);
        e.getPlayer().playSound(e.getPlayer(), Sound.ENTITY_PLAYER_LEVELUP, 100f, 1f);



//        for( String i : (TreeMap<String, Object>) Variables.getVariable("display::invContent::*", null, false) )


    }
}
