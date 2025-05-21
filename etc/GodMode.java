package kr.rth.picoserver.etc;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import static kr.rth.picoserver.util.transText.transText;

public class GodMode implements CommandExecutor {

    public static boolean isGodMode = false;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (!sender.isOp()) return false;
        isGodMode = !isGodMode;
        if (isGodMode) {
            Bukkit.broadcastMessage(transText("\n&f \uE146 &f관리자에 의해 무적이 &6활성화&f 되었습니다\n&f"));
        }else {
            Bukkit.broadcastMessage(transText("\n&f \uE146 &f관리자에 의해 무적이 &e비활성화&f 되었습니다\n&f"));
        }
        return true;
    }
}
