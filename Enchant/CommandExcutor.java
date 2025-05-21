package kr.rth.picoserver.Enchant;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class CommandExcutor implements CommandExecutor {
    public static boolean isInteger(String strValue) {
        try {
            Integer.parseInt(strValue);
            return true;
        } catch (NumberFormatException ex) {
            return false;
        }
    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if(!command.getName().equalsIgnoreCase("enchantment")) return false;
        if(!(sender instanceof Player p)) {
            sender.sendMessage("플레이어만 사용할 수 있습니다.");
            return false;
        }
        if( args.length != 2 ){
            sender.sendMessage("잘못된 사용입니다. \n 사용법: /enchantment [인첸트명] [값]");
            return false;
        }
        if( p.getInventory().getItemInMainHand().getType() == Material.AIR ) {
            sender.sendMessage("오른손에 아이템을 들고 실행해주세요.");
            return false;
        }
        if(!isInteger(args[1])) {
            sender.sendMessage("인첸트 값은 정수로 입력하여주세요.");
            return false;
        }

        Enchantment enchantment = null;
        for ( Enchantment i :  Enchantment.values()) {
            if(args[0].trim().equalsIgnoreCase(i.getName())) {
                enchantment = i;
            }
        }

        if( enchantment == null) {
            sender.sendMessage("잘못된 인첸트명입니다.");
            return false;
        }

        ItemStack stack = p.getInventory().getItemInMainHand();


        stack.addUnsafeEnchantment(enchantment, Integer.parseInt(args[1]));

        p.getInventory().setItemInMainHand(stack);
        p.playSound(p.getLocation(), Sound.BLOCK_AMETHYST_BLOCK_HIT, 7.5f, 1f);

        return false;
    }
}
