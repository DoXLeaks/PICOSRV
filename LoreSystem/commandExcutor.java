package kr.rth.picoserver.LoreSystem;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;

public class commandExcutor implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        Player p = (Player) sender;

        if( p.getInventory().getItemInMainHand().getType() == Material.AIR ){
            p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', "&c[SYSTEM] &f손에 아이템을 들고 명령어를 실행해주세요.") ));
            p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 5f, 5f);
            return false;
        }

        return false;
    }
}
