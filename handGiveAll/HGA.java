package kr.rth.picoserver.handGiveAll;

import kr.rth.picoserver.MailBox.boxInv;
import kr.rth.picoserver.MailBox.mailboxes;
import kr.rth.picoserver.PICOSERVER;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import static java.lang.Integer.parseInt;

public class HGA implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        Player p = (Player) sender;

        if( p.isOp()  && args.length == 1) {
            if( parseInt(args[0]) > 9) {
                sender.sendMessage("정신차려 이양반아 갯수 확인점;;; 서버 말아먹게? ");
                return false;
            }

            Bukkit.getScheduler().scheduleAsyncDelayedTask(PICOSERVER.getInstance(), () -> {
                for( Player i : Bukkit.getOnlinePlayers()) {
                        ItemStack stack = p.getInventory().getItemInMainHand().clone();
                        stack.setAmount(parseInt(args[0]));
                        Inventory inv1 = mailboxes.getInstance().getMailbox(i.getUniqueId().toString());
                        boxInv aasasd = (boxInv) inv1.getHolder();
                        aasasd.getInventory().addItem(stack);
                        aasasd.save();

                        i.sendTitle(ChatColor.translateAlternateColorCodes('&', "§x§f§b§e§a§4§e§l§oP§x§f§1§e§e§5§f§o§li§x§e§6§f§2§7§0§l§oc§x§d§c§f§7§8§1§l§oo §x§d§1§f§b§9§2§l§oO§x§c§6§f§7§a§3§l§on§x§b§b§e§b§b§4§l§ol§x§b§0§d§f§c§5§l§oi§x§a§4§d§3§d§7§l§on§x§9§9§c§7§e§8§l§oe"), ChatColor.translateAlternateColorCodes('&', "&f우편함에 새로운 아이템이 도착 했습니다&f"));

                    Bukkit.getScheduler().scheduleSyncDelayedTask(Bukkit.getPluginManager().getPlugin("PICOSERVER"),
                            () -> i.playSound(i.getLocation(), Sound.BLOCK_NOTE_BLOCK_XYLOPHONE, 5f, 0.89f), 0L); //20 Tick (1 Second) delay before run() is called
                        Bukkit.getScheduler().scheduleSyncDelayedTask(Bukkit.getPluginManager().getPlugin("PICOSERVER"),
                            () -> i.playSound(i.getLocation(), Sound.BLOCK_NOTE_BLOCK_XYLOPHONE, 5f, 1.33f), 2L); //20 Tick (1 Second) delay before run() is called
                }
                p.sendMessage("지급완료.");
            }, 0L);
        }
        return false;
    }
}
