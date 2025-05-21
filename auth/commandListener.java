package kr.rth.picoserver.auth;

import kr.rth.picoserver.Database;
import kr.rth.picoserver.PICOSERVER;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;

import static kr.rth.picoserver.FirstSetup.updateNickname;
import static kr.rth.picoserver.util.transText.transText;

public class commandListener implements CommandExecutor {

    UnauthorizedPlayerRegistry unauthorizedPlayerRegistry;

    public commandListener(UnauthorizedPlayerRegistry unauthorizedPlayerRegistry) {
        this.unauthorizedPlayerRegistry = unauthorizedPlayerRegistry;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        Player p = (Player) sender;
        if (args.length == 1) {
            ArrayList<Object> q = new ArrayList<>();
            q.add(p.getUniqueId().toString());
            try {
                if (0 < (Integer) (new ArrayList<>(Database.getInstance().execute("SELECT EXISTS (SELECT * FROM UserAuthentication WHERE uuid = ?)", q).get(0).values()).get(0))) {
                    p.sendMessage(transText("&f \uE1C4 &f디스코드 인증이 완료된 계정입니다."));
                    this.unauthorizedPlayerRegistry.unregister(p);
                    return true;
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

            q.clear();
            q.add(args[0].trim());
            try {
                if (1 > (Integer) new ArrayList<>(Database.getInstance().execute("SELECT EXISTS (SELECT * FROM AuthCodes WHERE code = ? AND at > DATE_SUB(NOW(), INTERVAL 60 SECOND))", q).get(0).values()).get(0)) {
                    p.sendMessage(transText("&f \uE1C4 &f인증 코드가 올바르지 않습니다."));
                    return false;
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            ArrayList<Map<String, Object>> dbRes1 = null;
            try {
                dbRes1 = Database.getInstance().execute("SELECT * FROM AuthCodes WHERE code = ?", q);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            try {
                Database.getInstance().execute("DELETE FROM AuthCodes WHERE code = ?", q);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

            q.clear();
            q.add(p.getUniqueId().toString());
            q.add((String) dbRes1.get(0).get("discord"));
            q.add(p.getName());
            q.add(p.getAddress().getAddress().getHostAddress());
            q.add((String) dbRes1.get(0).get("code"));

            try {
                Database.getInstance().execute("INSERT INTO UserAuthentication (uuid, discord, username, ip, code) VALUES(?, ?, ?, ?, ?)", q);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

            ArrayList<Map<String, Object>> finalDbRes = dbRes1;
            Bukkit.getScheduler().runTaskAsynchronously(PICOSERVER.getInstance(), new Runnable() {
                @Override
                public void run() {
                    updateNickname("1019910169792106547", String.valueOf(finalDbRes.get(0).get("discord")), p.getName());
                }
            });

            p.sendMessage(transText("&f \uE1C4 &f인증이 완료 되었습니다. 서버 플레이 전 반드시 법전을 확인해 주세요."));
            this.unauthorizedPlayerRegistry.unregister(p);
        }
        return false;
    }
}
