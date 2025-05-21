package kr.rth.picoserver.posionSet;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import kr.rth.picoserver.Database;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class tabCompleter implements TabCompleter {
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String s, String[] args) {
        if(!sender.isOp()){
            return null;
        }
        Player p = (Player) sender;
        ArrayList<String> res = new ArrayList<>();
        if( args.length == 1 ) {
            res.add("생성");
            res.add("설정");
            res.add("삭제");
        }
        if(args.length == 2 && !args[0].equals("생성")) {
            ArrayList<Map<String, Object>> dbRes1 = null;
            try {
                dbRes1 = Database.getInstance().execute("SELECT * FROM posionset", null);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            for( var i : dbRes1) {
                res.add((String) i.get("id"));
            }
//                    Po
        }
        if(args.length == 3&& args[0].equals("설정") ) {
            res.add("셋설정");
            res.add("효과삭제");
            res.add("효과추가");
            res.add("월드추가");
            res.add("월드삭제");
            res.add("블랙리스트월드추가");
            res.add("블랙리스트월드삭제");
        }
        if(args.length == 4 && args[0].equals("설정") && args[2].equals("효과추가") ) {
            for( var i : PotionEffectType.values()){
                if( args[3].trim().equals("")  ) {
                    res.add(i.getName());
                }else {
                    if(i.getName().toLowerCase().startsWith(args[3].trim().toLowerCase())){
                        res.add(i.getName());
                    }
                }
            }
        }
        Gson gson = new Gson();
        if(args.length == 4 && args[0].equals("설정") && args[2].equals("효과삭제") ) {
            ArrayList<LinkedTreeMap<String, String>> effects = new ArrayList<>();
            ArrayList<Object> q = new ArrayList<>();
            q.add(args[1]);
            try {
                effects = gson.fromJson(
                        (String) Database.getInstance().execute("SELECT * FROM posionset  WHERE id = ?", q).get(0).get("posionEffects"),
                        effects.getClass()
                );
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            for(var i : effects) {
                if( args[3].trim().equals("")  ) {
                    res.add(i.get("POSION"));
                }else {
                    if(i.get("POSION").toLowerCase().startsWith(args[3].trim().toLowerCase())){
                        res.add(i.get("POSION"));
                    }
                }

            }
        }
        if(args.length == 4 && args[0].equals("설정") && (args[2].equals("월드추가") || args[2].equals("블랙리스트월드추가"))) {
            for( var i : Bukkit.getWorlds()){
                res.add(i.getName());
            }
        }
        if(args.length == 4 && args[0].equals("설정") && args[2].equals("월드삭제") ) {
            ArrayList<String> worlds = new ArrayList<>();
            ArrayList<Object> q = new ArrayList<>();
            q.add(args[1]);
            try {
                worlds = gson.fromJson(
                        (String) Database.getInstance().execute("SELECT * FROM posionset  WHERE id = ?", q).get(0).get("active_worlds"),
                        worlds.getClass()
                );
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            res.addAll(worlds);
        }
        if(args.length == 4 && args[0].equals("설정") && args[2].equals("블랙리스트월드삭제") ) {
            ArrayList<String> worlds = new ArrayList<>();
            ArrayList<Object> q = new ArrayList<>();
            q.add(args[1]);
            try {
                worlds = gson.fromJson(
                        (String) Database.getInstance().execute("SELECT * FROM posionset  WHERE id = ?", q).get(0).get("inactive_worlds"),
                        worlds.getClass()
                );
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            res.addAll(worlds);
        }
        return res;
    }
}
