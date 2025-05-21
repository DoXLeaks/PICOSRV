package kr.rth.picoserver.playTime;

import kr.rth.picoserver.Database;
import kr.rth.picoserver.PICOSERVER;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.util.ArrayList;


public class looper {
    static looper instance;
    private looper() {
        Bukkit.getScheduler().runTaskTimerAsynchronously(PICOSERVER.getInstance(), () -> {
            for ( Player p : Bukkit.getOnlinePlayers() ) {
                ArrayList<Object> q = new ArrayList<>();
                q.add(p.getUniqueId().toString());
                try {
                    Database.getInstance().execute("UPDATE playTime SET playTime = playTime + 1 WHERE uuid=?", q);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        }, 0, 20 * 60);
    }
    public static looper getInstance(){
        if(instance == null){ //최초 한번만 new 연산자를 통하여 메모리에 할당한다.
            instance = new looper();
        }
        return instance;
    }
}
