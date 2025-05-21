package kr.rth.picoserver.defualtItem;

import com.google.gson.Gson;
import kr.rth.picoserver.Database;
import kr.rth.picoserver.Stat.Stat;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import static java.lang.Integer.parseInt;
import static kr.rth.picoserver.util.ParseLore.parseLore;
import static kr.rth.picoserver.util.getFreeInventorySpace.getFreeInventorySpace;
import static kr.rth.picoserver.util.itemStackSerializer.itemStackDeSerializer;
import static kr.rth.picoserver.util.transText.transText;

public class commandListener implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        Player p = (Player) sender;
        if (args.length == 1 && sender.isOp() && args[0].equals("설정")) {
            Object dbRes1 = null;
            try {
                dbRes1 = Database.getInstance().execute("SELECT * FROM keyv WHERE name = 'defaultItem'", null).get(0).get("data");
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            HashMap<String, String> a = new HashMap<>();
            Gson gson = new Gson();
            a = gson.fromJson((String) dbRes1, a.getClass());
            Inventory openingInv = Bukkit.createInventory(null, 54, "기본템 수정");
            for (String i : a.keySet()) {
                openingInv.setItem(parseInt(i), itemStackDeSerializer(a.get(i)));
            }

            p.openInventory(openingInv);
            return false;
        }

        ArrayList<Object> q1 = new ArrayList<>();
        q1.add(p.getUniqueId().toString());
        try {
            if (!p.isOp() && 0 < (Integer) new ArrayList<>(Database.getInstance().execute("SELECT EXISTS ( SELECT * FROM defaultitemlog WHERE uuid = ? AND at > DATE_SUB( NOW(), INTERVAL 10 MINUTE ))", q1).get(0).values()).get(0)) {
                p.sendTitle("§x§F§9§E§5§B§1§l§oP§x§F§A§E§8§B§A§l§oi§x§F§A§E§B§C§2§l§oc§x§F§B§E§E§C§B§l§oo §x§F§C§F§1§D§4§l§oO§x§F§C§F§3§D§C§l§on§x§F§D§F§6§E§5§l§ol§x§F§E§F§9§E§E§l§oi§x§F§E§F§C§F§6§l§on§x§F§F§F§F§F§F§l§oe", transText("&f기본템은 10분 간격으로 받을 수 있습니다"));
                p.playSound(p, Sound.BLOCK_LANTERN_BREAK, 1, 1f);
                return false;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        Object dbRes1 = null;
        try {
            dbRes1 = Database.getInstance().execute("SELECT * FROM keyv WHERE name = 'defaultItem'", null).get(0).get("data");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        HashMap<String, String> a = new HashMap<>();
        Gson gson = new Gson();
        a = gson.fromJson((String) dbRes1, a.getClass());

        if (getFreeInventorySpace(p) < a.size()) {
            p.sendTitle("§x§F§9§E§5§B§1§l§oP§x§F§A§E§8§B§A§l§oi§x§F§A§E§B§C§2§l§oc§x§F§B§E§E§C§B§l§oo §x§F§C§F§1§D§4§l§oO§x§F§C§F§3§D§C§l§on§x§F§D§F§6§E§5§l§ol§x§F§E§F§9§E§E§l§oi§x§F§E§F§C§F§6§l§on§x§F§F§F§F§F§F§l§oe", transText("&f인벤토리 공간이 부족합니다 &7([n]칸 필요)".replace("[n]", Integer.toString(a.size()))));
            p.playSound(p, Sound.BLOCK_LANTERN_BREAK, 1, 1f);
            return false;
        }
        ArrayList<Object> q = new ArrayList<>();
        q.add(p.getUniqueId().toString());
        try {
            Database.getInstance().execute("INSERT INTO defaultitemlog VALUES(?,now()) ", q);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        for (String i : a.values()) {
            ItemStack item = itemStackDeSerializer(i);
            if (item.getType().name().endsWith("HELMET")) {
                if (p.getInventory().getHelmet() == null) {
                    p.getInventory().setHelmet(item);
                } else {
                    p.getInventory().addItem(item);
                }
                continue;
            }
            if (item.getType().name().endsWith("CHESTPLATE")) {
                if (p.getInventory().getChestplate() == null) {
                    p.getInventory().setChestplate(item);
                } else {
                    p.getInventory().addItem(item);
                }
                continue;
            }
            if (item.getType().name().endsWith("LEGGINGS")) {
                if (p.getInventory().getLeggings() == null) {
                    p.getInventory().setLeggings(item);
                } else {
                    p.getInventory().addItem(item);
                }
                continue;
            }
            if (item.getType().name().endsWith("BOOTS")) {
                if (p.getInventory().getBoots() == null) {
                    p.getInventory().setBoots(item);
                } else {
                    p.getInventory().addItem(item);
                }
                continue;
            }
            p.getInventory().addItem(item);
            p.setMaxHealth(20 + (parseLore(Stat.HEALTH.getLabel(), p, false)));


        }
        p.sendTitle("§x§F§9§E§5§B§1§l§oP§x§F§A§E§8§B§A§l§oi§x§F§A§E§B§C§2§l§oc§x§F§B§E§E§C§B§l§oo §x§F§C§F§1§D§4§l§oO§x§F§C§F§3§D§C§l§on§x§F§D§F§6§E§5§l§ol§x§F§E§F§9§E§E§l§oi§x§F§E§F§C§F§6§l§on§x§F§F§F§F§F§F§l§oe", transText("&f기본 아이템이 지급 되었습니다"));
        Bukkit.getScheduler().scheduleSyncDelayedTask(Bukkit.getPluginManager().getPlugin("PICOSERVER"), new Runnable() {
            @Override
            public void run() {
                p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_XYLOPHONE, 5f, 0.89f);
            }
        }, 0L); //20 Tick (1 Second) delay before run() is called
        Bukkit.getScheduler().scheduleSyncDelayedTask(Bukkit.getPluginManager().getPlugin("PICOSERVER"), new Runnable() {
            @Override
            public void run() {
                p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_XYLOPHONE, 5f, 1.33f);
            }
        }, 2L); //20 Tick (1 Second) delay before run() is called

        return false;
    }
}
