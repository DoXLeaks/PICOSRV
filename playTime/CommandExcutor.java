package kr.rth.picoserver.playTime;

import kr.rth.picoserver.Database;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;

import static kr.rth.picoserver.util.numberWithComma.numberWithComma;

public class CommandExcutor implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        Player p = (Player) sender;

        ArrayList<Object> q1 = new ArrayList<>();
        if( args.length == 0 ) {
            q1.add(p.getUniqueId().toString());
            ArrayList<Map<String, Object>> dbRes1 = null;
            try {
                dbRes1 = Database.getInstance().execute("SELECT * FROM playTime WHERE uuid=?", q1);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            Integer playMinutes = ((Long) dbRes1.get(0).get("playTime")).intValue();
            ArrayList<String> res = new ArrayList<>();
            res.add("&f");
            res.add("&6    [ &f접속 시간 &6]");
            res.add("&f");
            res.add(" ▸ &f총 &e[hour]시간 [min]분 &f플레이 &f".replace("[hour]", numberWithComma(
                playMinutes / 60)).replace("[min]", String.valueOf(playMinutes % 60)));
            res.add("&f");
            p.sendMessage(ChatColor.translateAlternateColorCodes('&' ,String.join("\n", res)) );
        }
        if ( args.length == 1 ) {
            if(!(sender.isOp()) ){
                return false;
            }
            if(Bukkit.getOfflinePlayer(args[0]) == null ) {
                p.sendMessage("그런 유저는 없습니다.");
                return  false;
            }
            q1.add(Bukkit.getOfflinePlayer(args[0]).getUniqueId().toString());
            ArrayList<Map<String, Object>> dbRes1 = null;
            try {
                dbRes1 = Database.getInstance().execute("SELECT * FROM playTime WHERE uuid=?", q1);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            if( dbRes1.isEmpty() ) {
                p.sendMessage("그런 유저는 없습니다.");
                return  false;
            }
            Integer playMinutes = ((Long) dbRes1.get(0).get("playTime")).intValue();
            ArrayList<String> res = new ArrayList<>();
            res.add("&f");
            res.add("&6    [ &f접속 시간 &6]");
            res.add("&f");
            res.add(" ▸ &f총 &e[hour]시간 [min]분 &f플레이 &f".replace("[hour]", numberWithComma(
                playMinutes / 60)).replace("[min]", String.valueOf(playMinutes % 60)));
            res.add("&f");
            p.sendMessage(ChatColor.translateAlternateColorCodes('&' ,String.join("\n", res)) );







        }

        return false;




    }
}
