package kr.rth.picoserver.MailBox;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandListener implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        Player p = (Player) sender;
        if(args.length == 0) {
            p.openInventory(
                mailboxes.getInstance().getMailbox(p.getUniqueId().toString())
            );
            p.playSound(p, Sound.ENTITY_BEE_POLLINATE, 2, 1f);
            p.playSound(p, Sound.BLOCK_HONEY_BLOCK_STEP, 2, 1f);
        }
        if(args.length == 1 && p.isOp()) {
            Player p2 = Bukkit.getPlayer(args[0]);
            p.openInventory(
                    mailboxes.getInstance().getMailbox(p2.getUniqueId().toString())
            );
        }
        return false;
    }
    


}
