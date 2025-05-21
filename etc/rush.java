package kr.rth.picoserver.etc;

import com.google.gson.Gson;
import kr.rth.picoserver.Database;
import kr.rth.picoserver.PICOSERVER;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static kr.rth.picoserver.util.itemStackSerializer.itemStackDeSerializer;
import static kr.rth.picoserver.util.itemStackSerializer.itemStackSerializer;
import static kr.rth.picoserver.util.transText.transText;

public class rush implements CommandExecutor, Listener {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if(args[0].equals("보상설정")){
            Player p = (Player) sender;
            ArrayList<Object> q=  new ArrayList<>();
            q.add("rushReward");
            q.add(itemStackSerializer(p.getInventory().getItemInMainHand()));
            q.add(q.get(1));
            try {
                Database.getInstance().execute("INSERT INTO keyv (name, data) VALUES(?, ?) ON DUPLICATE KEY UPDATE data = ?",q );
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            sender.sendMessage("done");
            return false;
        }
        if( args[0] .equals("위치설정")) {
            ArrayList<Object> q=  new ArrayList<>();
            Player p = (Player) sender;
            Gson gson = new Gson();
            q.add("rushpos" + args[1].trim());
            q.add(gson.toJson(p.getLocation().serialize()));
            q.add(q.get(1));
            try {
                Database.getInstance().execute("INSERT INTO keyv (name, data) VALUES(?,?) ON DUPLICATE KEY UPDATE data = ?",q );
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            sender.sendMessage("done.");
        }
        return false;
    }

    @EventHandler
    public void onmove(PlayerMoveEvent e) throws SQLException {

//        if( Math.round(e.getFrom().getX()) == Math.round(e.getTo().getX()) && Math.round(e.getFrom().getY()) == Math.round(e.getTo().getY()) && Math.round(e.getFrom().getZ()) == Math.round(e.getTo().getZ())) return;
        if (Math.floor(e.getFrom().getX()) == Math.floor(e.getTo().getX()) && (Math.floor(e.getTo().getY()) == Math.floor(e.getFrom().getY()) || Math.floor(e.getFrom().getY()) < Math.floor(e.getTo().getY())) && Math.floor(e.getFrom().getZ()) == Math.floor(e.getTo().getZ()))  return;

        if(e.getTo().getWorld().getBlockAt(e.getTo().getBlockX(),e.getTo().getBlockY() -1, e.getTo().getBlockZ()).getType().equals(Material.YELLOW_STAINED_GLASS)) {
            var dbres1 = Database.getInstance().execute("SELECT * FROM keyv WHERE name = 'rushpos1' OR name = 'rushpos2' OR name = 'rushpos3' OR name = 'rushpos4'",null);
            if(dbres1.size()< 4) {
                return;
            }
            for(var i : dbres1) {
                Map<String, Object> locData= new HashMap<>();
                Gson gson = new Gson();
                locData = gson.fromJson(
                        (String) i.get("data"),
                        locData.getClass()
                );
                Location tloc = Location.deserialize(locData);
                if(Math.floor(tloc.getY()) == Math.floor( e.getTo().getY()) && Math.floor(tloc.getX()) ==  Math.floor(e.getTo().getX()) &&  Math.floor(tloc.getZ()) ==  Math.floor(e.getTo().getZ())) {
                    if(i.get("name").equals("rushpos1")) {
                        ArrayList<Object> q = new ArrayList<>();
                        q.add(e.getPlayer().getUniqueId().toString());

                        if ( 0 <  (Integer) new ArrayList<>(Database.getInstance().execute("SELECT EXISTS ( SELECT * FROM rushcurr WHERE id = ? AND now = 4 )", q).get(0).values()).get(0)) {
                            e.getPlayer().sendTitle("","§x§F§A§E§D§C§B러쉬 보상이 지급 되었습니다");
                            Database.getInstance().execute("INSERT INTO rushcurr (id, now) VALUES(?, 1) ON DUPLICATE KEY UPDATE now = 1", q);
                            Bukkit.getScheduler().scheduleAsyncDelayedTask(PICOSERVER.getInstance(), () -> {
                                e.getPlayer().sendTitle("","§x§F§A§E§D§C§B러쉬 1% 진행중");
                            }, 20L);
                            e.getPlayer().playSound(e.getPlayer(), Sound.ENTITY_PLAYER_LEVELUP, 1f, 1f);
//                            (
                            e.getPlayer().getInventory().addItem(
                                    itemStackDeSerializer((String) Database.getInstance().execute("SELECT * FROM keyv WHERE name = 'rushReward'", null).get(0).get("data"))

                            );
                            for(var ii : Bukkit.getOnlinePlayers()) {
                                ii.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(transText("&f \uE143 누군가 핫플 러쉬를 돌고 있습니다... ".replace("[p]", e.getPlayer().getName()))));
                            }
//                            );
                            return;
                        }
                        Database.getInstance().execute("INSERT INTO rushcurr (id, now) VALUES(?, 1) ON DUPLICATE KEY UPDATE now = 1", q);
                        e.getPlayer().sendTitle("","§x§F§A§E§D§C§B러쉬 1% 진행중");
                        e.getPlayer().playSound(e.getPlayer(), Sound.BLOCK_BELL_USE, 1f, 1f);
                        return;
                    }
                    if(i.get("name").equals("rushpos2")) {
                        ArrayList<Object> q = new ArrayList<>();
                        q.add(e.getPlayer().getUniqueId().toString());
                        if (1 >  (Integer) new ArrayList<>(Database.getInstance().execute("SELECT EXISTS ( SELECT * FROM rushcurr WHERE id = ? AND now = 1 )", q).get(0).values()).get(0)) {
                            return;
                        }
                        Database.getInstance().execute("UPDATE rushcurr SET now = 2 WHERE id = ?", q);
                        e.getPlayer().sendTitle("","§x§F§A§E§D§C§B러쉬 25% 진행중");
                        e.getPlayer().playSound(e.getPlayer(), Sound.BLOCK_BELL_USE, 1f, 1f);
                    }
                    if(i.get("name").equals("rushpos3")) {
                        ArrayList<Object> q = new ArrayList<>();
                        q.add(e.getPlayer().getUniqueId().toString());
                        if (1 >  (Integer) new ArrayList<>(Database.getInstance().execute("SELECT EXISTS ( SELECT * FROM rushcurr WHERE id = ? AND now = 2 )", q).get(0).values()).get(0)) {
                            return;
                        }
                        Database.getInstance().execute("UPDATE rushcurr SET now = 3 WHERE id = ?", q);
                        e.getPlayer().sendTitle("", "§x§F§A§E§D§C§B러쉬 50% 진행중");
                        e.getPlayer().playSound(e.getPlayer(), Sound.BLOCK_BELL_USE, 1f, 1f);
                    }
                    if(i.get("name").equals("rushpos4")) {
                        ArrayList<Object> q = new ArrayList<>();
                        q.add(e.getPlayer().getUniqueId().toString());
                        if (1 >  (Integer) new ArrayList<>(Database.getInstance().execute("SELECT EXISTS ( SELECT * FROM rushcurr WHERE id = ? AND now = 3 )", q).get(0).values()).get(0)) {
                            return;
                        }
                        Database.getInstance().execute("UPDATE rushcurr SET now = 4 WHERE id = ?", q);
                        e.getPlayer().sendTitle("","§x§F§A§E§D§C§B러쉬 75% 진행중");
                        e.getPlayer().playSound(e.getPlayer(), Sound.BLOCK_BELL_USE, 1f, 1f);

                    }


                }
            }

        }

    }
}
