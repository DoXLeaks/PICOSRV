package kr.rth.picoserver.specialmine;

import kr.rth.picoserver.Database;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class tabCompleter implements TabCompleter {
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String s, String[] args) {
        if( !sender.isOp() ) {
            return null;
        }
        ArrayList<String> res = new ArrayList<>();
        if(args.length == 1) {
            res.add("추가");
            res.add("삭제");
            res.add("위치설정");
        }
        if(args.length == 2 && args[0].equals("삭제")) {
            ArrayList<Map<String, Object>> dbres1 = null;
            try {
                dbres1 = Database.getInstance().execute("SELECT * FROM special_mine", null);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            for( var i : dbres1  ) {
                String op = Bukkit.getOfflinePlayer(UUID.fromString((String) i.get("uuid"))).getName();
                if (op != null) res.add(op);
            }
        }
        if(args.length == 2 && args[0].equals("추가")) {
            return null;
        }
        return res;
    }
}
