package kr.rth.picoserver.etc;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import static java.lang.Integer.parseInt;

public class maxPlayers implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if(!sender.isOp())return false;
        if(args.length ==1) {
            Bukkit.getServer().setMaxPlayers(parseInt(args[0]));
            sender.sendMessage("done.");
        }
        return false;
    }
}
