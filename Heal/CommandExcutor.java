package kr.rth.picoserver.Heal;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class CommandExcutor implements CommandExecutor {
    public static HashMap<UUID, Long> cooldown = new HashMap<>();

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {

        Player p  = (Player) commandSender;
        UUID uuid = p.getUniqueId();
        cooldown.computeIfAbsent(uuid, k -> (long) 0);
        if(cooldown.get(uuid) > System.currentTimeMillis()) {
            if(!(Long.valueOf((cooldown.get(uuid)  - System.currentTimeMillis())/ 1000).intValue() > 58)){

                p.sendTitle(ChatColor.translateAlternateColorCodes('&', "§x§d§e§8§e§c§3§l§oH§x§d§c§a§7§c§a§l§oE§x§d§b§c§0§d§2§o§lA§x§d§9§d§9§d§9§o§lL") , ChatColor.translateAlternateColorCodes('&', net.md_5.bungee.api.ChatColor.translateAlternateColorCodes('&', "&f힐 쿨타임 &e[s] &f초".replace( "[s]", Integer.toString(Long.valueOf((cooldown.get(uuid)  - System.currentTimeMillis())/ 1000).intValue()) ) )),0,20,20);
            }
            return false;
        }
        cooldown.put(uuid, System.currentTimeMillis() + (60 * 1000));
        p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(""));
        p.setHealth(p.getMaxHealth());

        p.sendTitle(ChatColor.translateAlternateColorCodes('&', "§x§d§e§8§e§c§3§l§oH§x§d§c§a§7§c§a§l§oE§x§d§b§c§0§d§2§o§lA§x§d§9§d§9§d§9§o§lL") , ChatColor.translateAlternateColorCodes('&', "&f체력이 모두 회복 되었습니다"),0,20,20);
        p.playSound(p.getLocation(), Sound.BLOCK_AMETHYST_BLOCK_BREAK, 5f, 1f);
        return false;
    }
}
