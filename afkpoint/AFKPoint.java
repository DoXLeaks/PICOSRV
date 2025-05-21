package kr.rth.picoserver.afkpoint;

import kr.rth.picoserver.Database;
import org.bukkit.OfflinePlayer;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class AFKPoint {
    public static Integer get(OfflinePlayer p ) {
        int afkPoint = 0;
        ArrayList<Object> q = new ArrayList<>();
        q.add(p.getUniqueId().toString());
        ArrayList<Map<String, Object>> dbres1;
        try {
            dbres1 = Database.getInstance().execute("SELECT * FROM afk_point WHERE uuid = ?", q);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        if(!dbres1.isEmpty()){
            afkPoint = ((Long) dbres1.get(0).get("balance")).intValue();
        }
        return afkPoint;
    }
    public static void set(OfflinePlayer p, Integer value ) {
        ArrayList<Object> q = new ArrayList<>();
        q.add(value);
        q.add(p.getUniqueId().toString());
        try {
            Database.getInstance().execute("UPDATE afk_point SET balance = ? WHERE uuid =?", q);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public static void sub(OfflinePlayer p, Integer value ) {
        ArrayList<Object> q = new ArrayList<>();
        q.add(value);
        q.add(p.getUniqueId().toString());
        try {
            Database.getInstance().execute("UPDATE afk_point SET balance =  balance - ? WHERE uuid =?", q);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void add(OfflinePlayer p, Integer value ) {
        ArrayList<Object> q = new ArrayList<>();
        q.add(value);
        q.add(p.getUniqueId().toString());
        try {
            Database.getInstance().execute("UPDATE afk_point SET balance = balance + ? WHERE uuid = ?", q);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void add(List<UUID> uuids, Integer value) {
        ArrayList<Object> q = new ArrayList<>();
        q.add(value);
        q.addAll(uuids.stream().map(UUID::toString).toList());
        String statement = String.format("UPDATE afk_point SET balance = balance + ? WHERE uuid IN (%s)",
                uuids.stream().map(uuid -> "?").collect(Collectors.joining(", "))); //This is safe with SQL Injection, see the code
        try {
            Database.getInstance().execute(statement, q);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
