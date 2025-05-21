package kr.rth.picoserver.etc;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static kr.rth.picoserver.util.transText.transText;

public class forcetp implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if(!commandSender.hasPermission("guide.forcetp")) return false;
        if(strings.length == 1) {
            Player p = Bukkit.getPlayer(strings[0]);
            if(p == null) {
                return false;
            }
            p.teleport(
            ((Player ) commandSender)

            );
            p.getWorld().playSound(p.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1f, 1f);
            ((Player ) commandSender).playSound(((Player ) commandSender), Sound.ENTITY_ENDERMAN_TELEPORT, 1f, 1f);
            Bukkit.broadcastMessage(transText("&f \uE146 &f관리자 권한으로 &x&F&0&C&D&C&D[p]&f 님께서 &x&C&7&9&E&9&E[p1]&f 님을 소환 시켰습니다")
                    .replace("[p]", commandSender.getName())
                    .replace("[p1]", p.getName())
            );
        }

        return false;
    }
}
