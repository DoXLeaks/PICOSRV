package kr.rth.picoserver.credit;

import kr.rth.picoserver.Database;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;

import static java.lang.Integer.parseInt;
import static kr.rth.picoserver.util.numberWithComma.numberWithComma;
import static kr.rth.picoserver.util.transText.transText;

public class CommandListener implements CommandExecutor {
    private String getHelpWithBalance(int i, boolean isOp) {
        ArrayList<String> res = new ArrayList<>();
        res.add("&f");
        res.add(transText("&f  &f보유중인 크래딧: &x&D&B&C&D&F&0[balance] &f\uE152".replace("[balance]", numberWithComma(i))));
        res.add("&f");
        if(isOp) {
            res.add("&f  &f/크래딧 [지급/차감/확인] (닉네임) (금액)");
        }

        return ChatColor.translateAlternateColorCodes('&', String.join("\n", res));
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        //if (!( sender instanceof Player)) {
        //    sender.sendMessage("&f  &f플레이어만 이 명령어를 사용 할 수 있어요.");
        //    return false;
        //}
        if( args.length == 2 ) {
            if( args[0].equalsIgnoreCase( "확인") ) {
                if(Bukkit.getOfflinePlayer(args[1]) == null) {
                    sender.sendMessage("&f  &f해당 플레이어는 존재하지 않습니다");
                    return false;
                }
                ArrayList<Object> q2 = new ArrayList<>();
                q2.add(Bukkit.getOfflinePlayer(args[1]).getUniqueId().toString());
                ArrayList<Map<String, Object>> dbRes2 = null;
                try {
                    dbRes2 = Database.getInstance().execute("SELECT * FROM credit WHERE uuid=?", q2);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                if(dbRes2.size()< 1) {
                    sender.sendMessage("&f  &f해당 플레이어는 존재하지 않습니다");
                    return false;
                }
                ArrayList<String> res = new ArrayList<>();
                res.add("&f");
                res.add("&f  &f[nick] 님의 크래딧 잔액: &x&D&B&C&D&F&0[balance] &f".replace("[balance]", numberWithComma(((Long) dbRes2.get(0).get("balance")).intValue())).replace("[nick]", args[1]));
                res.add("&f");

                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', String.join("\n", res)));

                return false;
            }
        }

        if( args.length == 3 ) {
            if (Bukkit.getOfflinePlayer(args[1]) == null) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&f  &f해당 플레이어는 존재하지 않습니다"));
                return false;
            }
            if (parseInt(args[2]) < 1) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&f  &f명령어를 다시 한번 확인해주세요"));
                return false;
            }
            OfflinePlayer reciver = Bukkit.getOfflinePlayer(args[1]);
            if (args[0].equalsIgnoreCase("지급") && sender.isOp()) {
                ArrayList<Object> q3 = new ArrayList<>();
                q3.add(parseInt(args[2]));
                q3.add(reciver.getUniqueId().toString());


                try {
                    Database.getInstance().execute("UPDATE credit SET balance = balance + ? WHERE uuid = ? ", q3);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                if (reciver.isOnline()) {
                    (reciver.getPlayer()).sendMessage(ChatColor.translateAlternateColorCodes('&', "&f  &f크래딧 충전이 완료 되었습니다 &x&D&B&C&D&F&0+[amount] &f\uE152").replace("[nickname]", reciver.getName()).replace("[amount]", numberWithComma(parseInt(args[2]))));
                }
                sender.sendMessage("done");
                return false;
            }
            if (args[0].equalsIgnoreCase("차감") && sender.isOp()) {
                ArrayList<Object> q3 = new ArrayList<>();
                q3.add(parseInt(args[2]));
                q3.add(reciver.getUniqueId().toString());


                try {
                    Database.getInstance().execute("UPDATE credit SET balance = balance - ? WHERE uuid = ? ", q3);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                if (reciver.isOnline()) {
                    (reciver.getPlayer()).sendMessage(ChatColor.translateAlternateColorCodes('&', "&f  &f관리자에 의해 크래딧이 회수 되었습니다 &x&E&8&B&3&B&3-[amount] &f\uE152").replace("[nickname]", reciver.getName()).replace("[amount]", numberWithComma(parseInt(args[2]))));
                }
                sender.sendMessage("done");
                return false;
            }
        }

        if (!(sender instanceof Player p)) {
            sender.sendMessage("&f  &f플레이어만 이 명령어를 사용 할 수 있어요.");
            return false;
        }
        ArrayList<Object> q = new ArrayList<>();
        q.add( p.getUniqueId().toString() );
        ArrayList<Map<String, Object>> dbres1;
        try {
            dbres1 = Database.getInstance().execute("SELECT * FROM credit WHERE uuid=?", q);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        Integer balance =( (Long) dbres1.get(0).get("balance")).intValue();

        sender.sendMessage( getHelpWithBalance( balance, sender.isOp() ) );
        return false;
    }
}
