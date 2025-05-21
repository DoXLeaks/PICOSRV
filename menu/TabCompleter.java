package kr.rth.picoserver.menu;

import kr.rth.picoserver.Database;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TabCompleter implements org.bukkit.command.TabCompleter {
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String s, String[] args) {
        if( !sender.isOp() ) return null;
        ArrayList<String> res = new ArrayList<>();
        if(args.length == 1) {
            res.add("생성");
            res.add("삭제");
            res.add("설정");
        }
        if( args.length == 2 && !args[0].equalsIgnoreCase("생성") ) {
            ArrayList<Map<String, Object>> dbDbres1 = null;
            try {
                dbDbres1 = Database.getInstance().execute("SELECT id FROM menu", null);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            for(Map<String, Object> i : dbDbres1 ){
                res.add((String) i.get("id"));
            }
        }
        if(args.length == 3 && args[0].equalsIgnoreCase("설정") ) {
            res.add("명령어설정");
            res.add("명령어삭제");
            res.add("줄수설정");
            res.add("표시명설정");
            res.add("아이템설정");
        }

        return res;
    }
}
