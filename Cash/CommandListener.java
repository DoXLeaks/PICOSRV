package kr.rth.picoserver.Cash;

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
    public String getHelpWithBalance(int i, boolean isOp) {
        ArrayList<String> res = new ArrayList<>();
        res.add("&f");
        res.add(transText("&f \uE145 &f보유중인 캐시: &x&C&6&D&E&F&1[balance] &f\uE144".replace("[balance]", numberWithComma(i))));
        res.add("&f");
        if(isOp) {
            res.add("&f \uE145 &f/캐시 [지급/차감/확인] (닉네임) (금액)");
        }

        return ChatColor.translateAlternateColorCodes('&', String.join("\n", res));
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        //if (!( sender instanceof Player)) {
        //    sender.sendMessage("&f \uE145 &f플레이어만 이 명령어를 사용 할 수 있어요.");
        //    return false;
        //}

        if( args.length == 2 ) {
            if( args[0].equalsIgnoreCase( "확인") ) {
                if(Bukkit.getOfflinePlayer(args[1]) == null) {
                    sender.sendMessage("&f \uE145 &f해당 플레이어는 존재하지 않습니다");
                    return false;
                }
                ArrayList<Object> q2 = new ArrayList<>();
                q2.add(Bukkit.getOfflinePlayer(args[1]).getUniqueId().toString());
                ArrayList<Map<String, Object>> dbRes2 = null;
                try {
                    dbRes2 = Database.getInstance().execute("SELECT * FROM cash WHERE uuid=?", q2);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                if(dbRes2.size()< 1) {
                    sender.sendMessage("&f \uE145 &f해당 플레이어는 존재하지 않습니다");
                    return false;
                }
                ArrayList<String> res = new ArrayList<>();
                res.add("&f");
                res.add("&f \uE145 &f[nick] 님의 캐시 잔액: &x&C&6&D&E&F&1[balance] &f\uE144".replace("[balance]", numberWithComma(((Long) dbRes2.get(0).get("balance")).intValue())).replace("[nick]", args[1]));
                res.add("&f");

                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', String.join("\n", res)));

                return false;
            }
        }

        if( args.length == 3 ) {
            if (Bukkit.getOfflinePlayer(args[1]) == null) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&&f \uE143 &f해당 플레이어는 존재하지 않습니다"));
                return false;
            }
            if (parseInt(args[2]) < 1) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&f \uE143 &f명령어를 다시 한번 확인해주세요"));
                return false;
            }
            OfflinePlayer reciver = Bukkit.getOfflinePlayer(args[1]);
            if (args[0].equalsIgnoreCase("지급") && sender.isOp()) {
                ArrayList<Object> q3 = new ArrayList<>();
                q3.add(parseInt(args[2]));
                q3.add(reciver.getUniqueId().toString());


                try {
                    Database.getInstance().execute("UPDATE cash SET balance = balance + ? WHERE uuid = ? ", q3);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                if (reciver.isOnline()) {
                    (reciver.getPlayer()).sendMessage(ChatColor.translateAlternateColorCodes('&', "&f \uE145 &f캐시 충전이 완료 되었습니다 &x&C&6&D&E&F&1+[amount] &f\uE144").replace("[nickname]", reciver.getName()).replace("[amount]", numberWithComma(parseInt(args[2]))));
                }
                sender.sendMessage("done");
                return false;
            }
            if (args[0].equalsIgnoreCase("차감") && sender.isOp()) {
                ArrayList<Object> q3 = new ArrayList<>();
                q3.add(parseInt(args[2]));
                q3.add(reciver.getUniqueId().toString());


                try {
                    Database.getInstance().execute("UPDATE cash SET balance = balance - ? WHERE uuid = ? ", q3);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                if (reciver.isOnline()) {
                    (reciver.getPlayer()).sendMessage(ChatColor.translateAlternateColorCodes('&', "&f \uE145 &f관리자에 의해 캐시가 회수 되었습니다 &x&E&8&B&3&B&3-[amount] &f\uE144").replace("[nickname]", reciver.getName()).replace("[amount]", numberWithComma(parseInt(args[2]))));
                }
                sender.sendMessage("done");
                return false;
            }
        }

        if (!(sender instanceof Player p)) {
            sender.sendMessage("&f \uE145 &f플레이어만 이 명령어를 사용 할 수 있어요.");
            return false;
        }

        ArrayList<Object> q = new ArrayList<>();
        q.add( p.getUniqueId().toString() );
        ArrayList<Map<String, Object>> dbres1;
        try {
            dbres1 = Database.getInstance().execute("SELECT * FROM cash WHERE uuid=?", q);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        Integer balance =( (Long) dbres1.get(0).get("balance")).intValue();

        p.sendMessage( getHelpWithBalance( balance, p.isOp() ) );
        return false;


    }
}
