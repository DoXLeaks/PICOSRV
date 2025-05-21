package kr.rth.picoserver.credit;

import kr.rth.picoserver.Database;
import org.bukkit.OfflinePlayer;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;

public class Credit {
    public static Integer get(OfflinePlayer p ) {
        Integer credit = 0;
        ArrayList<Object> q = new ArrayList<>();
        q.add(p.getUniqueId().toString());
        ArrayList<Map<String, Object>> dbres1 = null;
        try {
            dbres1 = Database.getInstance().execute("SELECT * FROM credit WHERE uuid = ?", q);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        if(!dbres1.isEmpty()){
            credit = ((Long) dbres1.get(0).get("balance")).intValue();
        }
        return credit;
    }
    public static void set(OfflinePlayer p, Integer value ) {
        ArrayList<Object> q = new ArrayList<>();
        q.add(value);
        q.add(p.getUniqueId().toString());
        try {
            Database.getInstance().execute("UPDATE credit SET balance = ? WHERE uuid =?", q);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public static void sub(OfflinePlayer p, Integer value ) {
        ArrayList<Object> q = new ArrayList<>();
        q.add(value);
        q.add(p.getUniqueId().toString());
        try {
            Database.getInstance().execute("UPDATE credit SET balance =  balance - ? WHERE uuid =?", q);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public static void add(OfflinePlayer p, Integer value ) {
        ArrayList<Object> q = new ArrayList<>();
        q.add(value);
        q.add(p.getUniqueId().toString());
        try {
            Database.getInstance().execute("UPDATE credit SET balance = balance + ? WHERE uuid =?", q);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
