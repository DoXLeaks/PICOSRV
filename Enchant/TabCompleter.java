package kr.rth.picoserver.Enchant;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentWrapper;

import java.util.ArrayList;
import java.util.List;

public class TabCompleter  implements org.bukkit.command.TabCompleter {
    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] args) {

        if( !(command.getName().equalsIgnoreCase("enchantment")) ) return null;
        ArrayList<String> res = new ArrayList<>();
//        EnchantmentWrapper
        if( args.length == 1 ) {
            for ( Enchantment i :  Enchantment.values()) {
                if( args[0].trim().equalsIgnoreCase("")){

                res.add(i.getName());
                } else{
                    if(i.getName().startsWith(args[0].trim().toUpperCase())) {
                        res.add(i.getName());
                    }

                }
            }
        }
        return res;
    }
}
