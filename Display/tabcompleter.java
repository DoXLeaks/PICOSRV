package kr.rth.picoserver.Display;

import kr.rth.picoserver.Database;
import org.bukkit.Particle;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.ArmorStand;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class tabcompleter implements TabCompleter {
    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] args) {
        if(!commandSender.isOp()) {
            return null;
        }
        ArrayList<String> res = new ArrayList<>();

        if( args.length == 1 ) {
            res.add("삭제");
            res.add("생성");
            res.add("설정");
            res.add("이동");
        }
        if(args.length == 2) {
            if(!(args[0].equalsIgnoreCase("생성"))) {
                try {
                    ArrayList<Map<String, Object>> dbRes = Database.getInstance().execute("SELECT id FROM displayList", null);
                    for (Map<String, Object> i : dbRes) {
                            res.add(
                                (String) i.get("id")
                            );

                    }
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        if(args.length == 3) {
            if(args[0].equalsIgnoreCase("설정")){
                res.add("이름설정");
                res.add("아이템설정");
                res.add("줄수설정");
                res.add("장비설정");
                res.add("파티클설정");
            }
        }
        if(args.length == 4) {
            if(args[2].equalsIgnoreCase("장비설정")){
                res.add("머리");
                res.add("몸통");
                res.add("바지");
                res.add("신발");
            }
        }
        if(args.length == 4 && args[0].equalsIgnoreCase("설정") && args[2].equalsIgnoreCase("파티클설정")) {
            for(var i : Particle.values() ) {
                res.add(i.toString());
            }
            res.add("NONE");
        }




        return res;
    }
}
