package kr.rth.picoserver.posionSet;

import com.google.gson.Gson;
import kr.rth.picoserver.Database;
import kr.rth.picoserver.Team.teams;
import kr.rth.picoserver.etc.GodMode;
import kr.rth.picoserver.util.RegionChecker;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import static kr.rth.picoserver.util.itemStackSerializer.itemStackSerializer;
import static kr.rth.picoserver.util.random.generateRandomNumber;
import static kr.rth.picoserver.util.transText.transText;

public class EventListener implements Listener {
    static HashMap<Player, Long> cooldown = new HashMap<>();

    @EventHandler
    public void onInvClose(InventoryCloseEvent e) {
        if (!e.getView().getTitle().startsWith("셋 설정 | ")) return;
        HashMap<Integer, String> a = new HashMap<>();
        Inventory closedInv = e.getInventory();
        String id = e.getView().getTitle().replace("셋 설정 | ", "");
        Gson gson = new Gson();
        for (int i = 0; i < closedInv.getSize(); i++) {
            ItemStack item = closedInv.getItem(i);
            if (item != null) {
                a.put(i, itemStackSerializer(item));
            }
        }
        ArrayList<Object> q = new ArrayList<>();
        q.add(gson.toJson(a));
        q.add(id);
        try {
            Database.getInstance().execute("UPDATE posionset SET `sets` = ? WHERE id = ?", q);
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }


    }

    @EventHandler(priority = EventPriority.LOW)
    public void onDamage(EntityDamageByEntityEvent e) {
        if (!(e.getDamager() instanceof Player attacker)) return;
        if (!(e.getEntity() instanceof Player victim)) return;
        if (GodMode.isGodMode) return;

        if (RegionChecker.isPvpDisabledInPlayerRegion(victim)) return;
        if (!teams.isAttackable(attacker, victim)) return;
        if (victim.getNoDamageTicks() > 9) return;
        String mainHandItemName = "";
        String offHandItemName = "";

        if (attacker.getInventory().getItemInMainHand().getItemMeta() != null) {
            mainHandItemName = ChatColor.stripColor(attacker.getInventory().getItemInMainHand().getItemMeta().getDisplayName());
        }
        if (attacker.getInventory().getItemInOffHand().getItemMeta() != null) {
            offHandItemName = ChatColor.stripColor(attacker.getInventory().getItemInOffHand().getItemMeta().getDisplayName());
        }
        if (mainHandItemName.contains("봉인된 창")
                || offHandItemName.contains("봉인된 창")
                || mainHandItemName.contains("봉인된 지휘봉")
                || offHandItemName.contains("봉인된 지휘봉")
                || mainHandItemName.contains("봉인된 대검")
                || offHandItemName.contains("봉인된 대검")) {
            if (!(4 > generateRandomNumber(0, 100))) {
                return;
            }
            if (cooldown.get(attacker) != null) {
                if (cooldown.get(attacker) > System.currentTimeMillis()) {
                    return;
                } else {
                    cooldown.remove(attacker);
                }
            }

            Looper.cooldown.put(victim, System.currentTimeMillis() + 3000);
            cooldown.put(attacker, System.currentTimeMillis() + 7 * 1000);
            victim.clearActivePotionEffects();
            victim.sendTitle(transText("&f\uE00E"), transText("&f착용중인 장비의 모든 포션 효과가 3초간 봉인됩니다"));
            victim.getWorld().playSound(victim.getLocation(), Sound.ENTITY_ZOMBIE_ATTACK_WOODEN_DOOR, 1f, 0.1f);
        }
    }
}
