package kr.rth.picoserver.chat;

import kr.rth.picoserver.chat.chatting;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import static kr.rth.picoserver.util.transText.transText;

public class chatFreeze implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command,  String s, String[] args) {
        if (chatting.chatFreeze) {
//            sender.sendMessage("얼리기가 비활성화 됨.");
            chatting.chatFreeze = false;
            Bukkit.broadcastMessage(transText("\n&f \uE146 &f관리자에 의해 전체 채팅이 &6활성화&f 되었습니다\n&f"));
            return false;
        }else {
//            sender.sendMessage("얼리기가 활성화 됨.");
            Bukkit.broadcastMessage(transText("\n&f \uE146 &f관리자에 의해 전체 채팅이 &e비활성화&f 되었습니다\n&f"));
            chatting.chatFreeze = true;
            return false;
        }
    }
}
