package kr.rth.picoserver.itemUpgrade;

import kr.rth.picoserver.Database;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import javax.xml.crypto.Data;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class tabCompleter implements TabCompleter {
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String s, String[] args) {
        if( ! sender.isOp()) return null;
        ArrayList<String > res = new ArrayList<>();
        if(args.length == 1){
            res.add("create");
            res.add("modify");
            res.add("specialcoin");
            res.add("normalcoin");
        }
        if(args.length == 2 && !args[0].equalsIgnoreCase("create")){
            try {
                for( var i:  Database.getInstance().execute("SELECT id FROM itemUpgrade", null)){
                    res.add((String) i.get("id"));
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        if(args.length == 3 && args[0].equalsIgnoreCase("modify")){
            res.add("winpercent");
            res.add("losepercent");
            res.add("breakpercent");

            res.add("winitem");
            res.add("targetitem");

            res.add("requiredcoin");
            res.add("price");
        }




        return res;
    }
}
