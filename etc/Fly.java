package kr.rth.picoserver.etc;

import kr.rth.picoserver.Database;
import kr.rth.picoserver.PICOSERVER;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;

import static java.lang.Integer.parseInt;
import static kr.rth.picoserver.util.transText.transText;

public class Fly implements Listener, CommandExecutor {
   // static Fly instance;
    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        if(e.getPlayer().isOp() ) return;
        if(e.getPlayer().isFlying()) {
            if(!e.getPlayer().getWorld().getName().equals("plotworld")) {
                setFlying(e.getPlayer(), false);
                return;
            }
            if(getRemainingFlying(e.getPlayer())< 1) {
                setFlying(e.getPlayer(), false);
            }
        }
    }
   // @EventHandler
    public void onMoveMap(PlayerInteractEvent e) {
        if (e.getItem() == null){
            return;
        }
//        if( !( e.getItem().getType().equals(Material.FISHING_ROD) && e.getPlayer().getWorld().getName().equals("plotworld") ) && (e.getAction().equals(Action.RIGHT_CLICK_BLOCK) || e.getAction().equals(Action.RIGHT_CLICK_AIR))) {
//            e.setCancelled(true);
//            return;
//        }
        if(e.getItem().getItemMeta() == null) return;
        if(e.getItem().getType().name().endsWith("CANDLE") ){
            e.setCancelled(true);
        }
        if(e.getItem().getItemMeta().getDisplayName().trim().equals("")) return;
        if(!ChatColor.stripColor(e.getItem().getItemMeta().getDisplayName().trim()).trim().equals("[ 플라이 쿠폰 ]")) {
            return;
        }

        e.setCancelled(true);
        registerFly(e.getPlayer());
        subRemainingFlying(e.getPlayer(), -3600);
        Integer remainValue = getRemainingFlying(e.getPlayer());
        Integer Hour = remainValue / 3600;
        Integer Minutes = remainValue % 3600 / 60;
        Integer Seconds = remainValue % 3600 % 60;
        e.getPlayer().sendTitle("§x§d§7§f§3§f§d§oFLY", transText("&f&o플라이 충전이 완료 되었습니다")
                .replace("[h]", Hour.toString())
                .replace("[m]", Minutes.toString())
                .replace("[s]", Seconds.toString()));
        Player p = e.getPlayer();
        Bukkit.getScheduler().scheduleSyncDelayedTask(Bukkit.getPluginManager().getPlugin("PICOSERVER"), new Runnable() {
            @Override
            public void run() {
                p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 5f, 3f);
            }
        }, 0L); //20 Tick (1 Second) delay before run() is called
        Bukkit.getScheduler().scheduleSyncDelayedTask(Bukkit.getPluginManager().getPlugin("PICOSERVER"), new Runnable() {
            @Override
            public void run() {p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 5f, 1.5f);
            }
        }, 2L); //20 Tick (1 Second) delay before run() is called
        Bukkit.getScheduler().scheduleSyncDelayedTask(Bukkit.getPluginManager().getPlugin("PICOSERVER"), new Runnable() {
            @Override
            public void run() {
                p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 5f, 1f);
            }
        }, 4L); //20 Tick (1 Second) delay before run() is called

        PICOSERVER.dispatchCommand(e.getPlayer(), "플라이");
        e.getItem().setAmount(e.getItem().getAmount() - 1);
    }

    @EventHandler
    public void onMoveMap(PlayerChangedWorldEvent e) {

        if(!e.getPlayer().getWorld().getName().equals("SuperiorWorld")){
            e.getPlayer().setAllowFlight(false);
            return;
        }
        if(e.getPlayer().isOp() ) return;
        Bukkit.getScheduler().scheduleAsyncDelayedTask(PICOSERVER.getInstance(), () -> {
            e.getPlayer().setAllowFlight(true);

        },5L);
    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
//        Player p = (Player) sender;
//        p.setAllowFlight(true);
        if(sender.isOp()){

            if(args.length == 3){
                if(Bukkit.getOfflinePlayer(args[1]) == null) {
                    sender.sendMessage(transText("&f ● &b( 플라이 ) 존재하지 않는 플레이어입니다."));
                    return false;
                }
                if(args[0].equals("지급")){
                    registerFly(Bukkit.getOfflinePlayer(args[1]) );
                    subRemainingFlying(Bukkit.getOfflinePlayer(args[1]) , -parseInt(args[2]) * 3600);
                    PICOSERVER.dispatchCommand((Player) sender, "플라이 확인 [p]".replace("[p]", Bukkit.getOfflinePlayer(args[1]).getName()));
                }
                if(args[0].equals("차감")){
                    registerFly(Bukkit.getOfflinePlayer(args[1]) );
                    subRemainingFlying(Bukkit.getOfflinePlayer(args[1]) , parseInt(args[2]) * 3600);
                    PICOSERVER.dispatchCommand((Player) sender, "플라이 확인 [p]".replace("[p]", Bukkit.getOfflinePlayer(args[1]).getName()));
                }
                return false;
            }
            if( args.length == 2) {
                if(args[0].equals("확인")){
                    if(Bukkit. getOfflinePlayer(args[1]) == null) {
                        sender.sendMessage(transText("&f ● &b( 플라이 ) 존재하지 않는 플레이어입니다."));
                        return false;
                    }
                    Integer remainValue = getRemainingFlying(Bukkit.getOfflinePlayer(args[1]));
                    Integer Hour = remainValue / 3600;
                    Integer Minutes = remainValue % 3600 / 60;
                    Integer Seconds = remainValue % 3600 % 60;
                    sender.sendMessage(transText("\n&f ● &b( 플라이 ) &f[p]님의 시간 | &f[h]시간 [m]분 [s]초 남음\n&f"
                            .replace("[p]", Bukkit.getOfflinePlayer(args[1]).getName())
                            .replace("[h]", Hour.toString())
                            .replace("[m]", Minutes.toString())
                            .replace("[s]", Seconds.toString())
                    ));
                    return false;
                }

            }
        }
        Integer remainValue = getRemainingFlying((Player) sender);
        Integer Hour = remainValue / 3600;
        Integer Minutes = remainValue % 3600 / 60;
        Integer Seconds = remainValue % 3600 % 60;
        sender.sendMessage(transText("\n&f ● &b( 플라이 ) &f[h]시간 [m]분 [s]초 남음\n&f"
                .replace("[h]", Hour.toString())
                .replace("[m]", Minutes.toString())
                .replace("[s]", Seconds.toString())
        ));



        return false;
    }
    public static void registerFly( OfflinePlayer p) {
        ArrayList<Object> q = new ArrayList<>();
        q.add(p.getUniqueId().toString());
        try {
            Database.getInstance().execute("INSERT INTO flying (uuid) VALUES(?) ON DUPLICATE KEY UPDATE uuid = uuid ", q);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void setFlying( Player p, Boolean value  ) {
        registerFly(p);
        if (value) {
            p.setAllowFlight(value);
            p.setFlying(value);
        } else {
            p.setFlying(value);
            p.setAllowFlight(value);
        }
    }

    public static Integer getRemainingFlying( OfflinePlayer p  ) {
        registerFly(p);
        ArrayList<Object> q = new ArrayList<>();
        q.add(p.getUniqueId().toString());

        ArrayList<Map<String, Object>> dbres1= null;
        try {
            dbres1 = Database.getInstance().execute("SELECT * FROM flying WHERE uuid = ? ", q);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        if(dbres1.isEmpty()){
            return 0;
        }
        return ((Long) dbres1.get(0).get("timeHas")).intValue();
    }
    public static void subRemainingFlying( OfflinePlayer p, Integer value  ) {
        registerFly(p);
        ArrayList<Object> q = new ArrayList<>();
        q.add(value);
        q.add(p.getUniqueId().toString());

        try {
            Database.getInstance().execute("UPDATE flying SET timeHas = timeHas - ? WHERE uuid =? ", q);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }



    private Fly() {

        Bukkit.getScheduler().scheduleAsyncRepeatingTask(PICOSERVER.getInstance(), () -> {
            for(Player i : Bukkit.getOnlinePlayers()) {
                if(i.isOp() ) continue;
                if(i.isFlying()) {
                    subRemainingFlying(i, 1);
                    Integer remainValue = getRemainingFlying(i);
                    if( remainValue < 1){
                        setFlying(i, false);
                    }
                    Integer Hour = remainValue / 3600;
                    Integer Minutes = remainValue % 3600 / 60;
                    Integer Seconds = remainValue % 3600 % 60;
                    i.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText("§x§a§d§f§3§f§d[ 플라이 ]§f [h]시간 [m]분 [s]초 남음"
                            .replace("[h]", Hour.toString())
                            .replace("[m]", Minutes.toString())
                            .replace("[s]", Seconds.toString())
                            ));
//                    i.getRemaining

                }
            }
        }, 20L, 20L);
    }


   // public static Fly getInstance() {
      //  if(instance == null ) {
      //      instance = new Fly();
        }
    //    return instance;
  //  }
//}
