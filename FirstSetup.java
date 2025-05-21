package kr.rth.picoserver;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.exceptions.PermissionException;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPreLoginEvent;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;

public class FirstSetup implements Listener {
    static JDA jda = JDABuilder.createDefault("MTIwMzE3MDEyODUxMDg1MzEyMA.GPEEgU.EFuDR_OGzr2ErwAeThrqcxsGByrkJreAgu9yc0").build();
    public static void updateNickname(String guildId, String userId, String newNickname) {
        try {
            jda.awaitReady();  // 봇이 준비될 때까지 기다립니다.

            Guild guild = jda.getGuildById(guildId);
            if (guild != null) {
                Member member = guild.getMemberById(userId);
                if (member != null) {
                    guild.modifyNickname(member, newNickname).queue();
                }
            }
        } catch (PermissionException e) {
            System.out.println("권한이 없어 닉네임을 변경할 수 없습니다.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @EventHandler
    public void onJoin(PlayerPreLoginEvent e) throws SQLException {
//            e.disallow();
//            e.dis

        if(Bukkit.getPlayer(e.getUniqueId()) != null) {
            e.disallow(PlayerPreLoginEvent.Result.KICK_OTHER, "계정강탈 불가.");
//        e.disallow();

        }


//
    }
    @EventHandler
    public void updateNick(PlayerJoinEvent e ){
        Bukkit.getScheduler().runTaskAsynchronously(PICOSERVER.getInstance(), new Runnable() {
            @Override
            public void run() {
                ArrayList<Map<String, Object>> dbRes1 = null;

                ArrayList<Object> q = new ArrayList<>();
                q.add(e.getPlayer().getUniqueId().toString());
                try {
                    dbRes1 = Database.getInstance().execute("SELECT * FROM authorizeduser WHERE uuid = ?",q );

                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                if (dbRes1 == null || dbRes1.isEmpty()) {
                    Bukkit.getLogger().warning("No results found for UUID: " + e.getPlayer().getUniqueId());
                    return;
                }
                updateNickname("1019910169792106547", String.valueOf(dbRes1.get(0).get("dsid")), e.getPlayer().getName());
            }
        });
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) throws SQLException {
        e.getPlayer().playSound(e.getPlayer(), Sound.ENTITY_PLAYER_LEVELUP, 1f, 2f);

        if( !e.getPlayer().isOp() ){
            Bukkit.getScheduler().scheduleSyncDelayedTask(Bukkit.getPluginManager().getPlugin("PICOSERVER"), new Runnable() {
                @Override
                public void run() {
                    e.getPlayer().setGameMode(GameMode.ADVENTURE);
                }
            }, 5L);
        }

        ArrayList<Object> q = new ArrayList<>();
        q.add( e.getPlayer().getUniqueId().toString() );
        if( !(((Integer) new ArrayList<>(Database.getInstance().execute("SELECT EXISTS(SELECT * FROM cash WHERE uuid=?)", q).get(0).values()).get(0)) > 0)) {
            q.clear();
            q.add( e.getPlayer().getUniqueId().toString() );
            q.add(0);
            Database.getInstance().execute("INSERT INTO cash VALUES(?,?)", q);
        }

        q = new ArrayList<>();
        q.add( e.getPlayer().getUniqueId().toString() );
        if( !(((Integer) new ArrayList<>(Database.getInstance().execute("SELECT EXISTS(SELECT * FROM credit WHERE uuid=?)", q).get(0).values()).get(0)) > 0)) {
            q.clear();
            q.add( e.getPlayer().getUniqueId().toString() );
            q.add(0);
            Database.getInstance().execute("INSERT INTO credit VALUES(?,?)", q);
        }

        q = new ArrayList<>();
        q.add( e.getPlayer().getUniqueId().toString() );
        if( !(((Integer) new ArrayList<>(Database.getInstance().execute("SELECT EXISTS(SELECT * FROM mileage WHERE uuid=?)", q).get(0).values()).get(0)) > 0)) {
            q.clear();
            q.add( e.getPlayer().getUniqueId().toString() );
            q.add(0);
            Database.getInstance().execute("INSERT INTO mileage VALUES(?,?)", q);
        }

        q = new ArrayList<>();
        q.add( e.getPlayer().getUniqueId().toString() );
        if( !(((Integer) new ArrayList<>(Database.getInstance().execute("SELECT EXISTS(SELECT * FROM afk_point WHERE uuid=?)", q).get(0).values()).get(0)) > 0)) {
            q.clear();
            q.add( e.getPlayer().getUniqueId().toString() );
            q.add(0);
            Database.getInstance().execute("INSERT INTO afk_point VALUES(?,?)", q);
        }


        q = new ArrayList<>();
        q.add( e.getPlayer().getUniqueId().toString() );
        if( !( (Integer) new ArrayList<>(Database.getInstance().execute("SELECT EXISTS(SELECT * FROM playTime WHERE uuid=?)", q).get(0).values()).get(0) > 0)) {
            q.add(0);
            Database.getInstance().execute("INSERT INTO playTime VALUES(?,?)", q);
        }



//        ScoreboardManager manager=  Bukkit.getScoreboardManager();
//        Scoreboard board = manager.getMainScoreboard();
//        if(board.getTeam("disableCollision") == null) {
//            Bukkit.dispatchCommand( Bukkit.getConsoleSender(), "team add disableCollision");
//            Bukkit.dispatchCommand( Bukkit.getConsoleSender(), "team modify disableCollision collisionRule never");
//        }
//        if (! (board.getTeam("disableCollision").hasPlayer(e.getPlayer()) )) {
//            Bukkit.dispatchCommand( Bukkit.getConsoleSender(), "team join disableCollision [p]".replace("[p]", e.getPlayer().getName()));
//
//        }
//        e.getPlayer().setScoreboard(board);
//        board.getTeam("disableCollision").addPlayer(e.getPlayer());
    }
}
