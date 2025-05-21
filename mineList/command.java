package kr.rth.picoserver.mineList;

import com.google.gson.Gson;
import kr.rth.picoserver.Database;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.sql.SQLException;
import java.util.HashMap;

import static java.lang.Integer.parseInt;
import static kr.rth.picoserver.util.itemStackSerializer.itemStackDeSerializer;

public class command implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if( !sender.isOp() ) {
            return false;
        }
        Player p = (Player) sender;

        HashMap<String, String> invContent=new HashMap<>();
        Gson gson = new Gson();
        try {
            invContent = gson.fromJson(
                    (String) Database.getInstance().execute("SELECT * FROM keyv WHERE name = 'minelistInv'", null).get(0).get("data"),
                    invContent.getClass()

            );
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        Inventory openingInv = Bukkit.createInventory(null, 54, "마리보상설정");
        for( String i : invContent.keySet() ) {
            openingInv.setItem(parseInt(i), itemStackDeSerializer(invContent.get(i)));
        }
        p.openInventory(openingInv);

        return false;
    }
}
