package kr.rth.picoserver.posionSet;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import kr.rth.picoserver.Database;
import org.bukkit.Bukkit;
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

public class ComandListener implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if( !sender.isOp() ) {
            return false;
        }
        Player p = (Player) sender;

        if(args.length == 2) {
            if(args[0].equals("생성")) {
                ArrayList<Object> q = new ArrayList<>();
                q.add(args[1]);
                try {
                    if( 0 <  (Integer) new ArrayList<>(Database.getInstance().execute("SELECT EXISTS ( SELECT * FROM posionset WHERE id = ?)", q).get(0).values()).get(0)) {
                        p.sendMessage("이미있는이름.");
                        return false;
                    }
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                try {
                    Database.getInstance().execute("INSERT INTO posionset VALUES(?,'{}','[]', '[]', '[]')", q);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                p.sendMessage("done.");
                kr.rth.picoserver.posionSet.Looper.getInstance().reload();
                return false;
            }
            if(args[0].equals("삭제")) {
                ArrayList<Object> q = new ArrayList<>();
                q.add(args[1]);
                try {
                    if( 1 >  (Integer) new ArrayList<>(Database.getInstance().execute("SELECT EXISTS ( SELECT * FROM posionset WHERE id = ?)", q).get(0).values()).get(0)) {
                        p.sendMessage("없는이름.");
                        return false;
                    }
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                try {
                    Database.getInstance().execute("DELETE FROM posionset WHERE id = ?", q);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                p.sendMessage("done.");
                kr.rth.picoserver.posionSet.Looper.getInstance().reload();
                return false;
            }
        }
        if(args.length == 3) {
            if( args[0].equals("설정") ) {
                ArrayList<Object> q = new ArrayList<>();
                q.add(args[1]);
                try {
                    if( 1 >  (Integer) new ArrayList<>(Database.getInstance().execute("SELECT EXISTS ( SELECT * FROM posionset WHERE id = ?)", q).get(0).values()).get(0)) {
                        p.sendMessage("그런 이름 없음.");
                        return false;
                    }
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                Gson gson = new Gson();
                if( args[2].equals("셋설정") ){
                    HashMap<String, String> invContent = new HashMap<>();
                    try {
                        invContent = gson.fromJson(
                                (String) Database.getInstance().execute("SELECT * FROM posionset WHERE id = ? ", q).get(0).get("sets")

                                ,invContent.getClass()
                        );
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                    Inventory openingInv = Bukkit.createInventory(null, 9, "셋 설정 | [id]".replace("[id]", args[1]));
                    for( String i : invContent.keySet()){
                        openingInv.setItem(parseInt(i), itemStackDeSerializer(invContent.get(i)));
                    }
                    p.openInventory(openingInv);
                    kr.rth.picoserver.posionSet.Looper.getInstance().reload();
                    return false;
                }

            }

        }
        if(args.length == 6) {
            if( args[0].equals("설정") ) {
                ArrayList<Object> q = new ArrayList<>();
                q.add(args[1]);
                try {
                    if( 1 >  (Integer) new ArrayList<>(Database.getInstance().execute("SELECT EXISTS ( SELECT * FROM posionset WHERE id = ?)", q).get(0).values()).get(0)) {
                        p.sendMessage("그런 이름 없음.");
                        return false;
                    }
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                Gson gson = new Gson();
                if( args[2].equals("효과추가") ){
                    ArrayList<LinkedTreeMap<String, String>> effects = new ArrayList<>();

                    try {
                        effects = gson.fromJson(
                                (String) Database.getInstance().execute("SELECT * FROM posionset  WHERE id = ?", q).get(0).get("posionEffects"),
                                effects.getClass()

                        );
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                    LinkedTreeMap<String, String> a =new LinkedTreeMap<>();
                    a.put("POSION", args[3]);
                    a.put("DURATION", args[4]);
                    a.put("AMP", args[5]);
                    effects.add(a);
                    q.add(0, gson.toJson(effects));
                    try {
                        Database.getInstance().execute("UPDATE posionset SET posionEffects = ? WHERE id = ?",q);
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                    p.sendMessage("Done.");
                    kr.rth.picoserver.posionSet.Looper.getInstance().reload();

                    return false;
                }

            }
        }
        if(args.length == 4) {
            if( args[0].equals("설정") ) {
                ArrayList<Object> q = new ArrayList<>();
                q.add(args[1]);
                try {
                    if( 1 >  (Integer) new ArrayList<>(Database.getInstance().execute("SELECT EXISTS ( SELECT * FROM posionset WHERE id = ?)", q).get(0).values()).get(0)) {
                        p.sendMessage("그런 이름 없음.");
                        return false;
                    }
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                Gson gson = new Gson();
                if( args[2].equals("효과삭제") ){
                    ArrayList<LinkedTreeMap<String, String>> effects = new ArrayList<>();

                    try {
                        effects = gson.fromJson(
                                (String) Database.getInstance().execute("SELECT * FROM posionset  WHERE id = ?", q).get(0).get("posionEffects"),
                                effects.getClass()

                        );
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                    LinkedTreeMap<String, String> selected= null;
                    for(var i : effects) {
                         if (i.get("POSION").equals(args[3])) {
                             selected = i;
                        }
                    }
                    effects.remove(selected);
                    q.add(0, gson.toJson(effects));
                    try {
                        Database.getInstance().execute("UPDATE posionset SET posionEffects = ? WHERE id = ?",q);
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                    p.sendMessage("Done.");
                    kr.rth.picoserver.posionSet.Looper.getInstance().reload();

                    return false;
                }

            }
        }

        if(args.length == 4) {
            if( args[0].equals("설정") ) {
                ArrayList<Object> q = new ArrayList<>();
                q.add(args[1]);
                try {
                    if( 1 >  (Integer) new ArrayList<>(Database.getInstance().execute("SELECT EXISTS ( SELECT * FROM posionset WHERE id = ?)", q).get(0).values()).get(0)) {
                        p.sendMessage("그런 이름 없음.");
                        return false;
                    }
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                Gson gson = new Gson();
                if( args[2].equals("월드추가") ){
                    ArrayList<String> worlds = new ArrayList<>();

                    try {
                        worlds = gson.fromJson(
                                (String) Database.getInstance().execute("SELECT * FROM posionset  WHERE id = ?", q).get(0).get("active_worlds"),
                                worlds.getClass()
                        );
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                    worlds.add(args[3]);
                    q.add(0, gson.toJson(worlds));
                    try {
                        Database.getInstance().execute("UPDATE posionset SET active_worlds = ? WHERE id = ?",q);
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                    p.sendMessage("Done.");
                    kr.rth.picoserver.posionSet.Looper.getInstance().reload();

                    return false;
                }

            }
        }
        if(args.length == 4) {
            if( args[0].equals("설정") ) {
                ArrayList<Object> q = new ArrayList<>();
                q.add(args[1]);
                try {
                    if( 1 >  (Integer) new ArrayList<>(Database.getInstance().execute("SELECT EXISTS ( SELECT * FROM posionset WHERE id = ?)", q).get(0).values()).get(0)) {
                        p.sendMessage("그런 이름 없음.");
                        return false;
                    }
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                Gson gson = new Gson();
                if( args[2].equals("블랙리스트월드추가") ){
                    ArrayList<String> worlds = new ArrayList<>();

                    try {
                        worlds = gson.fromJson(
                                (String) Database.getInstance().execute("SELECT * FROM posionset  WHERE id = ?", q).get(0).get("inactive_worlds"),
                                worlds.getClass()
                        );
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                    worlds.add(args[3]);
                    q.add(0, gson.toJson(worlds));
                    try {
                        Database.getInstance().execute("UPDATE posionset SET inactive_worlds = ? WHERE id = ?",q);
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                    p.sendMessage("Done.");
                    kr.rth.picoserver.posionSet.Looper.getInstance().reload();

                    return false;
                }

            }
        }
        if(args.length == 4) {
            if( args[0].equals("설정") ) {
                ArrayList<Object> q = new ArrayList<>();
                q.add(args[1]);
                try {
                    if( 1 >  (Integer) new ArrayList<>(Database.getInstance().execute("SELECT EXISTS ( SELECT * FROM posionset WHERE id = ?)", q).get(0).values()).get(0)) {
                        p.sendMessage("그런 이름 없음.");
                        return false;
                    }
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                Gson gson = new Gson();
                if( args[2].equals("월드삭제") ){
                    ArrayList<String> worlds = new ArrayList<>();
                    try {
                        worlds = gson.fromJson(
                                (String) Database.getInstance().execute("SELECT * FROM posionset WHERE id = ?", q).get(0).get("active_worlds"),
                                worlds.getClass()

                        );
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                    worlds.remove(args[3]);
                    q.add(0, gson.toJson(worlds));
                    try {
                        Database.getInstance().execute("UPDATE posionset SET active_worlds = ? WHERE id = ?",q);
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                    p.sendMessage("Done.");
                    kr.rth.picoserver.posionSet.Looper.getInstance().reload();

                    return false;
                }

            }
        }
        if(args.length == 4) {
            if( args[0].equals("설정") ) {
                ArrayList<Object> q = new ArrayList<>();
                q.add(args[1]);
                try {
                    if( 1 >  (Integer) new ArrayList<>(Database.getInstance().execute("SELECT EXISTS ( SELECT * FROM posionset WHERE id = ?)", q).get(0).values()).get(0)) {
                        p.sendMessage("그런 이름 없음.");
                        return false;
                    }
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                Gson gson = new Gson();
                if( args[2].equals("블랙리스트월드삭제") ){
                    ArrayList<String> worlds = new ArrayList<>();
                    try {
                        worlds = gson.fromJson(
                                (String) Database.getInstance().execute("SELECT * FROM posionset WHERE id = ?", q).get(0).get("inactive_worlds"),
                                worlds.getClass()

                        );
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                    worlds.remove(args[3]);
                    q.add(0, gson.toJson(worlds));
                    try {
                        Database.getInstance().execute("UPDATE posionset SET inactive_worlds = ? WHERE id = ?",q);
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                    p.sendMessage("Done.");
                    kr.rth.picoserver.posionSet.Looper.getInstance().reload();

                    return false;
                }

            }
        }
        return false;
    }
}
