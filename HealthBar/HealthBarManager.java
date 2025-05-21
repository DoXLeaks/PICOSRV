package kr.rth.picoserver.HealthBar;

import kr.rth.picoserver.Team.teams;
import kr.rth.picoserver.util.transText;
import me.neznamy.tab.api.TabAPI;
import me.neznamy.tab.api.TabPlayer;
import me.neznamy.tab.api.nametag.NameTagManager;
//import me.neznamy.tab.api.nametag.UnlimitedNameTagManager;
import me.neznamy.tab.api.nametag.UnlimitedNameTagManager;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import static kr.rth.picoserver.util.transText.transText;

public class HealthBarManager {
//    public static void update(Player p) {
//        if (p.hasMetadata("NPC")) {
//            return; //NPC인지 확인합니다.
//        }
////        var teamName = teams.getBelongTo(p) ;
////        if (teamName.equals("")) {
////            teamName = "팀 없음";
////        }else {
////            teamName += "팀";
////        }
//        TabPlayer tabPlayer = TabAPI.getInstance().getPlayer(p.getUniqueId());
//        if (tabPlayer == null) return;
//        UnlimitedNameTagManager unlimitedNameTagManager = (UnlimitedNameTagManager) TabAPI.getInstance().getNameTagManager();
//        unlimitedNameTagManager.setLine(
//                tabPlayer, "another", p.getName()
//        );
////        LuckPermsApi api = LuckPerms.getApi();
//        LuckPerms api = LuckPermsProvider.get();
//
//        User user = api.getUserManager().getUser(p.getUniqueId());
//
//
//        unlimitedNameTagManager.setLine(
//                tabPlayer, "nametag", ChatColor.stripColor(api.getPlayerAdapter(Player.class).getMetaData(p).getPrefix()).strip().replace(" ", "")
//
//        );
//    }
    public static void update(Player p) {
        String teamName;
        if (p.hasMetadata("NPC")) {
            return;
        }
        String teamName2 = teams.getBelongTo(p);
        if (teamName2.equals("")) {
            teamName = "팀 없음";
        } else {
            teamName = teamName2 + "팀";
        }
        TabPlayer tabPlayer = TabAPI.getInstance().getPlayer(p.getUniqueId());
        if (tabPlayer == null) {
            return;
        }
        UnlimitedNameTagManager unlimitedNameTagManager = (UnlimitedNameTagManager) TabAPI.getInstance().getNameTagManager();
        unlimitedNameTagManager.setLine(tabPlayer, "another", transText.transText("§x§d§d§f§b§e§b[team] &7•&r [n]§x§f§b§4§4§6§b &f\uE172".replace("[team]", teamName).replace("[n]", String.valueOf(Double.valueOf(Math.floor(p.getHealth())).intValue()))));
    }

}
