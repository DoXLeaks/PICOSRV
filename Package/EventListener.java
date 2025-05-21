package kr.rth.picoserver.Package;

import com.google.gson.Gson;
import kr.rth.picoserver.Database;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static kr.rth.picoserver.util.getFreeInventorySpace.getFreeInventorySpace;
import static kr.rth.picoserver.util.itemStackSerializer.itemStackDeSerializer;
import static kr.rth.picoserver.util.itemStackSerializer.itemStackSerializer;
import static kr.rth.picoserver.util.transText.transText;

public class EventListener implements Listener {
    @EventHandler
    public void onInvClose(InventoryCloseEvent e){
        if(! e.getPlayer().isOp()) return;
        if(!e.getView().getTitle().startsWith("패키지보상설정 | ")) return;
        String id = e.getView().getTitle().replace("패키지보상설정 | ", "").trim();
        Player p = (Player) e.getPlayer();

        ArrayList<Object> q = new ArrayList<>();
        q.add( id );
        try {
            if( 1 > (Integer) new ArrayList<>(Database.getInstance().execute("SELECT EXISTS ( SELECT * FROM package WHERE id = ?  )", q).get(0).values()).get(0)) {
                p.sendMessage("존재하지 않는 패키지입니다.");
                return ;
            }
        } catch (SQLException ae) {
            throw new RuntimeException(ae);
        }
        Inventory closedInv = e.getInventory();
        HashMap<Integer, String> invContent = new HashMap<>();
        Gson gson = new Gson();
        for( int i = 0; i < closedInv.getSize(); i ++  ) {
            ItemStack stack = closedInv.getItem(i);
            if( stack != null) {
                invContent.put(i, itemStackSerializer(stack));
            }
        }
        q.add(0, gson.toJson(invContent));
        try {
            Database.getInstance().execute("UPDATE package SET invContent = ? WHERE id = ?", q);
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }

    }
    @EventHandler
    public void interact(PlayerInteractEvent e){
        if(!(e.getAction().equals(Action.RIGHT_CLICK_BLOCK) || e.getAction().equals(Action.RIGHT_CLICK_AIR))) return;
        if((e.getItem() == null || e.getItem().getItemMeta() == null )) return;
        List<String> itemLore = e.getItem().getItemMeta().getLore();
        if(itemLore == null) return;
        if (itemLore.isEmpty()) return;
        if (!ChatColor.stripColor(itemLore.get(0)).contains("패키지")) return;
        if (e.getHand() == EquipmentSlot.OFF_HAND) return;
        ArrayList<Object> q = new ArrayList<>();
        q.add(itemLore.get(0));

        ArrayList<Map<String, Object>> dbRes1 = null;
        try {
            dbRes1 = Database.getInstance().execute("SELECT * FROM package WHERE itemName = ?", q);
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
        if(dbRes1.isEmpty()) return;
        e.setCancelled(true);
        HashMap<String, String> a = new HashMap<>();
        Gson gson = new Gson();
        a=  gson.fromJson((String) dbRes1.get(0).get("invContent"), a.getClass());

        if((getFreeInventorySpace(e.getPlayer())) < a.size()) {
            e.getPlayer().sendTitle("§x§a§c§a§d§f§2§o§lP§x§b§9§b§7§f§4§o§lA§x§c§7§c§2§f§6§o§lC§x§d§4§c§c§f§8§o§lK§x§e§1§d§6§f§9§o§lA§x§e§f§e§1§f§b§o§lG§x§f§c§e§b§f§d§o§lE", transText("&f&o인벤토리 공간이 부족합니다 &7&o([n]칸 필요)".replace("[n]", Integer.toString(a.size()))));
            e.getPlayer().playSound(e.getPlayer(), Sound.BLOCK_LANTERN_BREAK, 1, 1f);
            return;
        }
        e.getItem().setAmount(e.getItem().getAmount() - 1);
        e.getPlayer().playSound(e.getPlayer(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 1, 1f);
        for(String i : a.values()) {
            e.getPlayer().getInventory().addItem(itemStackDeSerializer(i));
        }
        e.getPlayer().sendTitle("§x§a§c§a§d§f§2§o§lP§x§b§9§b§7§f§4§o§lA§x§c§7§c§2§f§6§o§lC§x§d§4§c§c§f§8§o§lK§x§e§1§d§6§f§9§o§lA§x§e§f§e§1§f§b§o§lG§x§f§c§e§b§f§d§o§lE", transText("&f&o패키지가 인벤토리로 지급 되었습니다"));
    }
}
