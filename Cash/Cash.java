package kr.rth.picoserver.Cash;

import kr.rth.picoserver.Database;
import org.bukkit.OfflinePlayer;
import org.checkerframework.checker.units.qual.A;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;

public class Cash {
    public static Integer get(OfflinePlayer p ) {
        Integer cash = 0;
        ArrayList<Object> q = new ArrayList<>();
        q.add(p.getUniqueId().toString());
        ArrayList<Map<String, Object>> dbres1 = null;
        try {
            dbres1 = Database.getInstance().execute("SELECT * FROM  cash WHERE uuid = ?", q);
        } catch (SQLException e) 
        {
            throw new RuntimeException(e);
        }
        if(!dbres1.isEmpty()){
            cash = ((Long) dbres1.get(0).get("balance")).intValue();
        }
        return cash;
    }
    public static void set(OfflinePlayer p, Integer value ) {
        Integer cash = 0;
        ArrayList<Object> q = new ArrayList<>();
        q.add(value);
        q.add(p.getUniqueId().toString());
        try {
            Database.getInstance().execute("UPDATE cash SET balance = ? WHERE uuid =?", q);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public static void sub(OfflinePlayer p, Integer value ) {
        Integer cash = 0;
        ArrayList<Object> q = new ArrayList<>();
        q.add(value);
        q.add(p.getUniqueId().toString());
        try {
            Database.getInstance().execute("UPDATE cash SET balance =  balance - ? WHERE uuid =?", q);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public static void add(OfflinePlayer p, Integer value ) {
        Integer cash = 0;
        ArrayList<Object> q = new ArrayList<>();
        q.add(value);
        q.add(p.getUniqueId().toString());
        try {
            Database.getInstance().execute("UPDATE cash SET balance =  balance + ? WHERE uuid =?", q);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


}
