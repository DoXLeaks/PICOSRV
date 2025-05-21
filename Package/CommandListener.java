package kr.rth.picoserver.Package;

import com.google.gson.Gson;
import kr.rth.picoserver.Database;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static java.lang.Integer.parseInt;
import static kr.rth.picoserver.util.itemStackSerializer.itemStackDeSerializer;

public class CommandListener implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        Player p = (Player) sender;
        if( !p.isOp()) return false;
        if( args.length == 2 && args[0].equalsIgnoreCase("생성") ) {
            ArrayList<Object> q = new ArrayList<>();
            q.add( args[1] );
            try {
                if( 0 <  (Integer) new ArrayList<>(Database.getInstance().execute("SELECT EXISTS ( SELECT * FROM package WHERE id = ?  )", q).get(0).values()).get(0)) {
                    p.sendMessage("이미 있는 이름입니다.");
                    return false;
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            try {
                Database.getInstance().execute("INSERT INTO package VALUES (?,'{}', null )", q);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            p.sendMessage("done");
            return false;
        }
        if( args.length == 2 && args[0].equalsIgnoreCase("아이템설정") ) {
            ArrayList<Object> q = new ArrayList<>();
            q.add( args[1] );
            ItemStack item = p.getItemInHand();
            if(item == null) return false;
            try {
                if( 1 > (Integer) new ArrayList<>(Database.getInstance().execute("SELECT EXISTS ( SELECT * FROM package WHERE id = ?  )", q).get(0).values()).get(0)) {
                    p.sendMessage("존재하지 않는 패키지입니다.");
                    return false;
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            if (item.getItemMeta() == null) {
                p.sendMessage("해당 아이템에는 Lore이 없습니다.");
                return true;
            }
            if (!item.getItemMeta().hasLore()) {
                p.sendMessage("해당 아이템에는 Lore이 없습니다.");
                return true;
            }
            q.add(0, item.getItemMeta().getLore().get(0));
            try {
                Database.getInstance().execute("UPDATE package SET itemName = ? WHERE id = ?", q);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            p.sendMessage("done");
            return false;

        }
        if( args.length == 2 && args[0].equalsIgnoreCase("보상설정") ) {
            ArrayList<Object> q = new ArrayList<>();
            q.add( args[1] );
            try {
                if( 1 > (Integer) new ArrayList<>(Database.getInstance().execute("SELECT EXISTS ( SELECT * FROM package WHERE id = ?  )", q).get(0).values()).get(0)) {
                    p.sendMessage("존재하지 않는 패키지입니다.");
                    return false;
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            Map<String, Object> dbRes1 = null;
            try {
                dbRes1 = Database.getInstance().execute("SELECT  * FROM package WHERE  id = ?", q).get(0);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            Inventory openingInv = Bukkit.createInventory(null, 54, "패키지보상설정 | [id]".replace("[id]", args[1]));
            HashMap<String, String> invContent= new HashMap<>();
            Gson gson = new Gson();
            invContent = gson.fromJson((String) dbRes1.get("invContent"), invContent.getClass());
            for(String i : invContent.keySet() ) {
                openingInv.setItem(parseInt(i), itemStackDeSerializer(invContent.get(i)));
            }
            p.openInventory(openingInv);
            return false;

        }
        return false;
    }

}
