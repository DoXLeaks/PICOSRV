package kr.rth.picoserver.battle;

import kr.rth.picoserver.PICOSERVER;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

import static kr.rth.picoserver.util.transText.transText;

public class commandListener implements CommandExecutor {
    public Map<Player, Player> battleRequests = new HashMap<>();
    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if ( args[0].equalsIgnoreCase("수락")) {
            Player player = (Player) sender;
            player.playSound(player, Sound.BLOCK_NOTE_BLOCK_BELL,  1f, 1.3f);

            if (!battleRequests.containsKey(player)) {
                player.sendMessage(transText("&6 ● &f티피 요청이 존재하지 않습니다"));
                return true;
            }

            Player target = battleRequests.get(player);
            player.sendMessage(transText("&6 ● &f" + target.getName() +  " 님의 티피 요청을 &a수락 &f했습니다"));
            battleRequests.remove(player);
            // 이동 처리 코드 추가




            return true;
        }
        if (args[0].equalsIgnoreCase("거절")) {

            Player player = (Player) sender;
            player.playSound(player, Sound.BLOCK_NOTE_BLOCK_BELL,  1f, 1.3f);
            if (!battleRequests.containsKey(player)) {
                player.sendMessage(transText("&6 ● &f대전 신청이 존재하지 않습니다"));
                return true;
            }

            Player target = battleRequests.get(player);
            player.sendMessage( transText("&6 ● &f" + target.getName() + " 님의 대전 신청을 &c거절 &f했습니다"));

            target.sendMessage( transText("&6 ● &f" + player.getName() + " 님께서 대전 신청을 &c거절 &f했습니다"));
            battleRequests.remove(player);
            return true;
        }

        Player senderPlayer = (Player) sender;
        Player targetPlayer = Bukkit.getPlayer(args[0]);

        if (targetPlayer == null || !targetPlayer.isOnline()) {
            senderPlayer.sendMessage(transText("&6 ● &f그 플레이어를 찾을 수 없습니다"));
            return true;
        }
        if (senderPlayer.getUniqueId().toString().equals(targetPlayer.getUniqueId().toString())){
            senderPlayer.sendMessage(transText("&6 ● &f본인에게 대전 신청을 보낼 수 없습니다"));
            return false;
        }
        if( battleRequests.containsKey(targetPlayer)){
            senderPlayer.sendMessage(transText("&6 ● &f해당 플레이어는 이미 대전 신청을 처리중입니다"));
            return true;
        }
//        if( ! ((senderPlayer.getWorld().getName().equals("spawn") || senderPlayer.getWorld().getName().equals("plotworld"))  && ( targetPlayer.getWorld().getName().equals("spawn") || targetPlayer.getWorld().getName().equals("SuperiorWorld")))   ) {
//            senderPlayer.sendMessage(transText("&6 ● &f대전요청을 대상 또는 본인이 스폰이나 마인팜에 있지 않습니다"));
//            return true;
//        }

        battleRequests.put(targetPlayer, senderPlayer);

        senderPlayer.sendMessage(transText("&6 ● &f" + targetPlayer.getName() + " 님에게 &6대전&f 신청을 보냈습니다"));
        TextComponent textcomp1 = new TextComponent(transText(" &a수락 &f"));
        textcomp1.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/대전 수락"));
        TextComponent textcomp2 = new TextComponent(transText(" &c거절 &f"));
        textcomp2.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/대전 거절"));

        targetPlayer.spigot().sendMessage(
                new TextComponent(
                        new TextComponent(transText("\n&6 ● &f" + senderPlayer.getName()  + " 님께서 &6대전&f 를 신청했습니다 &7[")),
                        textcomp1,
                        new TextComponent("|") ,
                        textcomp2,
                        new TextComponent(transText("&7]\n&f")))
        );

        Bukkit.getScheduler().scheduleSyncDelayedTask(Bukkit.getPluginManager().getPlugin("PICOSERVER"), new Runnable() {
            @Override
            public void run() {
                targetPlayer.playSound(targetPlayer.getLocation(), Sound.BLOCK_NOTE_BLOCK_XYLOPHONE, 5f, 0.89f);
            }
        }, 0L); //20 Tick (1 Second) delay before run() is called
        Bukkit.getScheduler().scheduleSyncDelayedTask(Bukkit.getPluginManager().getPlugin("PICOSERVER"), new Runnable() {
            @Override
            public void run() {targetPlayer.playSound(targetPlayer  .getLocation(), Sound.BLOCK_NOTE_BLOCK_XYLOPHONE, 5f, 1.33f);
            }
        }, 2L); //20 Tick (1 Second) delay before run() is called

        // 요청 만료 시간 설정 (10초 후 삭제)
        Bukkit.getScheduler().runTaskLater(PICOSERVER.getInstance(), () -> {
            if (battleRequests.containsKey(targetPlayer) && battleRequests.get(targetPlayer).equals(senderPlayer)) {
                battleRequests.remove(targetPlayer);
                senderPlayer.sendMessage(transText("&6 ● &f대전 신청이 &7만료&f 되었습니다"));
                targetPlayer.sendMessage(transText("&6 ● &f" + senderPlayer.getName() + " 님의 대전 신청이 &7만료&f 되었습니다"));
            }
        }, 10 * 20); // 20틱 = 1초, 10초 후에 실행


        return false;
    }


}
