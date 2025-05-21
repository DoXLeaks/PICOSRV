package kr.rth.picoserver.commandWhitelist;

import kr.rth.picoserver.Database;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.sql.SQLException;
import java.util.ArrayList;

public class CommandExcutor implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if( args.length != 2 || !sender.isOp() ){
            return true;
        }

        if(args[0].equalsIgnoreCase("추가")) {
            ArrayList<Object> q=  new ArrayList<>();
            q.add(args[1]);
            Long isRegistered = null;

            try {
                if ( 0 < (Integer) new ArrayList<>(Database.getInstance().execute("SELECT EXISTS ( SELECT * FROM commandWhitelist WHERE name = ? )  ", q).get(0).values()).get(0)){
                    sender.sendMessage("이미 등록된 명령어입니다.");
                    return true;
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

            try {
                Database.getInstance().execute("INSERT INTO commandWhitelist VALUES(?)", q);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            sender.sendMessage("등록되었습니다.");


            return true;
        }
        if(args[0].equalsIgnoreCase("제거")) {
            ArrayList<Object> q=  new ArrayList<>();
            q.add(args[1]);
            Long isRegistered = null;

            try {
                if ( 1 > (Integer) new ArrayList<>(Database.getInstance().execute("SELECT EXISTS ( SELECT * FROM commandWhitelist WHERE name = ? )  ", q).get(0).values()).get(0)){
                    sender.sendMessage("없는 명령어입니다.");
                    return true;
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            try {
                Database.getInstance().execute("DELETE FROM commandWhitelist WHERE name = ?", q);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            sender.sendMessage("완료되었습니다.");


            return true;
        }


        return true;
    }
}
