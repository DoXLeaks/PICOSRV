package kr.rth.picoserver.playingReward;

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
import java.util.Map;
import java.util.UUID;

import static java.lang.Integer.parseInt;
import static kr.rth.picoserver.util.itemStackSerializer.itemStackDeSerializer;

public class commandListener implements CommandExecutor {
    public commandListener() {
        try {
            if(new ArrayList<>(Database.getInstance().execute("SELECT EXISTS( SELECT * FROM keyv WHERE name = 'playingRewardInv' )", null).get(0).values()).get(0) == null) {
                Database.getInstance().execute("INSERT INTO keyv VALUES('playingRewardInv', '{}')", null);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        Bukkit.getServer().getScheduler().runTaskTimerAsynchronously(PICOSERVER.getInstance(),
            () -> {
                String dbRes1;
                try {
                    dbRes1 = (String) Database.getInstance().execute("SELECT * FROM keyv WHERE name = 'playingRewardInv'", null).get(0).get("data");

                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                Gson gson = new Gson();
                HashMap<String, String> invCtc = new HashMap<>();
                invCtc = gson.fromJson(dbRes1, invCtc.getClass());
                ArrayList<Map<String, Object>> dbRes2 = null;
                try {
                    dbRes2 = Database.getInstance().execute("SELECT * FROM playTime WHERE playTime % 60 = 0;", null);
//                    dbRes2 = Database.getInstance().execute("SELECT * FROM playTime;", null);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }

                for(Map<String, Object> i : dbRes2) {
                    Player p = Bukkit.getPlayer(UUID.fromString((String) i.get("uuid")));
                    if(p == null) {continue;}
                    if(p.isOnline()){
                        for( String ii : invCtc.values() ) {
//                            p.getInventory().addItem( );
                            Inventory inv1 = mailboxes.getInstance().getMailbox(p.getUniqueId().toString());
                            boxInv aasasd = (boxInv) inv1.getHolder();
                            aasasd.getInventory().addItem(itemStackDeSerializer(ii));
                            aasasd.save();
                        }
                        Bukkit.getScheduler().scheduleSyncDelayedTask(Bukkit.getPluginManager().getPlugin("PICOSERVER"),
                            () -> p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_XYLOPHONE, 5f, 0.89f), 0L); //20 Tick (1 Second) delay before run() is called
                        Bukkit.getScheduler().scheduleSyncDelayedTask(Bukkit.getPluginManager().getPlugin("PICOSERVER"),
                            () -> p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_XYLOPHONE, 5f, 1.33f), 2L); //20 Tick (1 Second) delay before run() is called
                        p.sendTitle(ChatColor.translateAlternateColorCodes('&', "§x§f§b§9§8§0§3§l§oP§x§f§6§a§2§0§b§l§oi§x§f§2§a§d§1§3§l§oc§x§e§d§b§7§1§b§l§oo §x§e§8§c§2§2§3§l§oO§x§e§6§c§b§3§3§l§on§x§e§6§d§2§4§b§l§ol§x§e§6§d§9§6§2§l§oi§x§e§6§e§1§7§a§l§on§x§e§6§e§8§9§2§l§oe"), ChatColor.translateAlternateColorCodes('&', "&f플레이 타임 보상이 우편함으로 지급 되었습니다"));
                    }
                }
            }, 0, 60 * 20L);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        String dbRes1 = null;
        if(!(sender instanceof  Player ) || !sender.isOp() ) {
            return false;
        }
        try {
            dbRes1 = (String) Database.getInstance().execute("SELECT * FROM keyv WHERE  name = 'playingRewardInv'", null).get(0).get("data");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        HashMap<String, String> invCtc = new HashMap<>();
        Gson gson = new Gson();
        invCtc = gson.fromJson(dbRes1, invCtc.getClass());
        Inventory inv = Bukkit.createInventory(null, 54, "접속보상설정");
        for( String i: invCtc.keySet() ) {
            inv.setItem(parseInt(i), itemStackDeSerializer(invCtc.get(i) ) );
        }
        ((Player) sender).openInventory(inv);
        return false;
    }
}
