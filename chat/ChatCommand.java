package kr.rth.picoserver.chat;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class ChatCommand implements CommandExecutor {

    ChatConfig chatConfig;

    public ChatCommand(ChatConfig chatConfig) {
        this.chatConfig = chatConfig;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!sender.isOp()) return false;
        if (args.length == 1) {
            if (args[0].equals("리로드")) {
                try {
                    chatConfig.reload();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                sender.sendMessage("[§6PicoChat§r] 리로드했습니다.");
                return true;
            }
        }
        sender.sendMessage("[§6PicoChat§r] /채팅 리로드 - config를 리로드합니다.");
        return true;
    }
}
