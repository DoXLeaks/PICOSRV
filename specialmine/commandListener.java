package kr.rth.picoserver.specialmine;

import com.google.gson.Gson;
import kr.rth.picoserver.Database;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static kr.rth.picoserver.util.transText.transText;

public class commandListener implements CommandExecutor {

    SpecialMineConfig specialMineConfig;

    public commandListener(SpecialMineConfig specialMineConfig) {
        this.specialMineConfig = specialMineConfig;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if(args.length == 0) {
            ArrayList<Object> q=  new ArrayList<>();
            Player p = (Player) sender;
            q.add(p.getUniqueId().toString());

            ArrayList<Map<String, Object>> dbRes1 = null;
            try {
                dbRes1 = Database.getInstance().execute("SELECT * FROM special_mine WHERE uuid = ?", q);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            if(dbRes1.isEmpty()) {
                p.closeInventory();
                p.sendTitle("§x§F§2§C§6§D§E§l§oV§x§F§4§D§0§E§4§l§oI§x§F§6§D§9§E§9§l§oP §x§F§9§E§3§E§F§l§oM§x§F§B§E§C§F§4§l§oI§x§F§D§F§6§F§A§l§oN§x§F§F§F§F§F§F§l§oE", transText("특별 광산 권한을 가지고 있지 않습니다"));
                p.playSound(p, Sound.BLOCK_LANTERN_BREAK, 1 ,1f);
                return false;
            }

            Location location = specialMineConfig.getMineLocation();
            if (location != null) {
                p.teleport(location);
            }

            p.closeInventory();
            p.sendTitle("§x§F§2§C§6§D§E§l§oV§x§F§4§D§0§E§4§l§oI§x§F§6§D§9§E§9§l§oP §x§F§9§E§3§E§F§l§oM§x§F§B§E§C§F§4§l§oI§x§F§D§F§6§F§A§l§oN§x§F§F§F§F§F§F§l§oE", transText("특별 광산으로 이동 되었습니다"));
            p.playSound(p, Sound.ENTITY_SHULKER_TELEPORT, 1 ,1f);
        }

        if( !sender.isOp() ) return false;
        if(args.length == 1) {
            if (args[0].equals("위치설정")) {
                if (sender instanceof Player) {
                    specialMineConfig.setMineLocation(((Player) sender).getLocation());
                    sender.sendMessage("현재 위치가 특별 광산의 좌표로 설정되었습니다.");
                } else {
                    sender.sendMessage("해당 명령어는 플레이어만 실행 가능합니다.");
                }
                return true;
            }
        }

        if(args.length == 2) {
            OfflinePlayer op = Bukkit.getOfflinePlayer(args[1]);
            if (args[0].equals("삭제")) {
                ArrayList<Object> q = new ArrayList<>();
                q.add(op.getUniqueId().toString());
                try {
                    Database.getInstance().execute("DELETE FROM special_mine WHERE uuid = ?", q);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                sender.sendMessage("Done.");
            }
            if (args[0].equals("추가")) {
                ArrayList<Object> q = new ArrayList<>();
                q.add(op.getUniqueId().toString());
                try {
                    if (1 > (Integer) new ArrayList<>(Database.getInstance().execute("SELECT EXISTS (  SELECT * FROM special_mine WHERE uuid = ?) ", q).get(0).values()).get(0)) {
                        Database.getInstance().execute("INSERT INTO special_mine VALUES(?)", q);
                        sender.sendMessage("Done.");
                    } else {
                        sender.sendMessage("이미 등록된 유저입니다.");
                    }
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return false;
    }
}
