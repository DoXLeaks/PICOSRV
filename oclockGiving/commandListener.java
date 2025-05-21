package kr.rth.picoserver.oclockGiving;

import com.google.gson.Gson;
import kr.rth.picoserver.Database;
import kr.rth.picoserver.MailBox.boxInv;
import kr.rth.picoserver.MailBox.mailboxes;
import kr.rth.picoserver.PICOSERVER;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import static java.lang.Integer.parseInt;
import static kr.rth.picoserver.util.itemStackSerializer.itemStackDeSerializer;

public class commandListener implements CommandExecutor {
    public commandListener() {
        long currentTimeMillis = System.currentTimeMillis();
        long oneHourMillis = 60 * 60 * 1000; // 1시간(밀리초 단위)
        long nextHourMillis = (currentTimeMillis / oneHourMillis + 1) * oneHourMillis;
        long delay = nextHourMillis - currentTimeMillis;

        Bukkit.getServer().getScheduler().runTaskTimerAsynchronously(PICOSERVER.getInstance(), () -> {
            try {
                if ((Integer) new ArrayList<>(Database.getInstance().execute("SELECT EXISTS( SELECT * FROM keyv WHERE name = 'oclockInv' )", null).get(0).values()).get(0) < 1) {
                    Database.getInstance().execute("INSERT INTO keyv VALUES('oclockInv', '{}')", null);
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

            String dbRes1;
            try {
                dbRes1 = (String) Database.getInstance().execute("SELECT * FROM keyv WHERE  name = 'oclockInv'", null).get(0).get("data");
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            Gson gson = new Gson();
            HashMap<String, String> invCtc = new HashMap<>();
            invCtc = gson.fromJson(dbRes1, invCtc.getClass());
            HashMap<String, String> finalInvCtc = invCtc;
            for (Player gp : Bukkit.getServer().getOnlinePlayers()) {
                for (String i : finalInvCtc.values()) {
                    Inventory inv1 = mailboxes.getInstance().getMailbox(gp.getUniqueId().toString());
                    boxInv holder = (boxInv) inv1.getHolder();
                    holder.getInventory().addItem(itemStackDeSerializer(i));
                    holder.save();
                }
                gp.playSound(gp.getLocation(), Sound.BLOCK_NOTE_BLOCK_XYLOPHONE, 5f, 0.89f); //20 Tick (1 Second) delay before run() is called
                runDelayedTask(() -> gp.playSound(gp.getLocation(), Sound.BLOCK_NOTE_BLOCK_XYLOPHONE, 5f, 1.33f), 2L); //20 Tick (1 Second) delay before run() is called
                gp.sendTitle(ChatColor.translateAlternateColorCodes('&', "§x§f§b§e§a§4§e§l§oP§x§f§1§e§e§5§f§l§oi§x§e§6§f§2§7§0§l§oc§x§d§c§f§7§8§1§l§oo §x§d§1§f§b§9§2§l§oO§x§c§6§f§7§a§3§l§on§x§b§b§e§b§b§4§l§ol§x§b§0§d§f§c§5§l§oi§x§a§4§d§3§d§7§l§on§x§9§9§c§7§e§8§l§oe"), ChatColor.translateAlternateColorCodes('&', "&f&o정각 보상이 우편함으로 지급 되었습니다"));
            }

        }, delay / 50, oneHourMillis / 50);
    }

    private void runDelayedTask(Runnable task, long delay) {
        Bukkit.getScheduler().runTaskLater(PICOSERVER.getInstance(), task, delay);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        String dbRes1;
        if(!(sender instanceof  Player ) || !sender.isOp() ) {
            return false;
        }
        try {
            dbRes1 = (String) Database.getInstance().execute("SELECT * FROM keyv WHERE  name = 'oclockInv'", null).get(0).get("data");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        HashMap<String, String> invCtc = new HashMap<>();
        Gson gson = new Gson();
        invCtc = gson.fromJson(dbRes1, invCtc.getClass());
        Inventory inv = Bukkit.createInventory(null, 54, "정각보상설정");
        for( String i: invCtc.keySet() ) {
            inv.setItem(parseInt(i), itemStackDeSerializer(invCtc.get(i) ) );
        }
        ((Player) sender).openInventory(inv);
        return false;
    }
}
