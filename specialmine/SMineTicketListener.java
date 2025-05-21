package kr.rth.picoserver.specialmine;

import kr.rth.picoserver.Database;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import static kr.rth.picoserver.util.transText.transText;

public class SMineTicketListener implements Listener {

    @EventHandler
    public void onInteractAtMineTicket(PlayerInteractEvent event) throws SQLException {
        if (!event.getAction().isRightClick()) return;
        if (event.getHand() != EquipmentSlot.HAND) return;
        ItemStack item = event.getItem();
        if (item == null) return;
        if (item.getItemMeta() == null) return;
        String displayName = item.getItemMeta().getDisplayName();
        if (!ChatColor.stripColor(displayName).contains("특별광산 이용권")) return;
        Player player = event.getPlayer();

        ArrayList<Object> query = new ArrayList<>();
        query.add(player.getUniqueId().toString());
        ArrayList<Map<String, Object>> dbResult = null;
        try {
            dbResult = Database.getInstance().execute("SELECT * FROM special_mine WHERE uuid = ?", query);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        if(dbResult.isEmpty()) {
            item.setAmount(item.getAmount() - 1);
            Database.getInstance().execute("INSERT INTO special_mine VALUES(?)", query);
            player.playSound(player, Sound.UI_TOAST_CHALLENGE_COMPLETE, 1 ,1f);
            player.sendTitle("§x§F§2§C§6§D§E§l§oV§x§F§4§D§0§E§4§l§oI§x§F§6§D§9§E§9§l§oP §x§F§9§E§3§E§F§l§oM§x§F§B§E§C§F§4§l§oI§x§F§D§F§6§F§A§l§oN§x§F§F§F§F§F§F§l§oE", transText("특별 광산 입장 권한이 지급 되었습니다"));
            player.sendMessage(transText("&f \uE13B&f 특별광산 입장 권한이 지급 되었습니다"));
        } else {
            player.playSound(player, Sound.ENTITY_VILLAGER_NO, 1 ,3f);
            player.sendMessage(transText("&f \uE13B&f 이미 특별광산 권한을 가지고 있습니다"));
        }
    }
}
