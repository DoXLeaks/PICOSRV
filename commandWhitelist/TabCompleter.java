package kr.rth.picoserver.commandWhitelist;

import kr.rth.picoserver.Database;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.lang.reflect.Array;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TabCompleter implements org.bukkit.command.TabCompleter {
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String s, String[] args) {
        if( !sender.isOp() ) {
                return null;
        }
        ArrayList<String> res = new ArrayList<>();
        if( args.length == 1 ) {
            res.add("추가 ");
            res.add("삭제");
        }
        if( args.length == 2 && args[0].equalsIgnoreCase("삭제") ) {
            ArrayList<Map<String, Object>> dbRes1 = null;
            try {
                dbRes1 = Database.getInstance().execute("SELECT * FROM commandWhitelist", null);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            for ( Map<String, Object> i : dbRes1 ) {
                if( args[1].trim().equalsIgnoreCase("")) {
                    res.add((String) i.get("name"));
                    continue;
                }
                if(( (String) i.get("name")).startsWith(args[1])) {
                    res.add((String) i.get("name"));

                }

            }
        }
        return res;
    }
}
