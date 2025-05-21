package kr.rth.picoserver.etc;

import kr.rth.picoserver.PICOSERVER;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.*;

import static kr.rth.picoserver.util.transText.transText;


public class TPARequest implements CommandExecutor {
    public Map<Player, Player> teleportRequests = new HashMap<>();
    public ArrayList<Player> teleportWaiting = new ArrayList<>();
    static TPARequest instance;
    private final List<String> tpAbleWorlds = Arrays.asList("spawn", "plotworld");

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {

        if (!(sender instanceof Player player)) {
            sender.sendMessage("플레이어만 이 명령어를 사용할 수 있습니다.");
            return true;
        }
        if (args.length < 1) {
            player.sendMessage(transText("&f  &f이동을 희망하는 플레이어의 닉네임을 적어주세요"));
            return true;
        }

        if ( args[0].equalsIgnoreCase("수락")) {
            player.playSound(player, Sound.BLOCK_NOTE_BLOCK_BELL,  1f, 1.3f);

            if (!teleportRequests.containsKey(player)) {
                player.sendMessage(transText("&f  &f티피 요청이 존재하지 않습니다"));
                return true;
            }

            Player target = teleportRequests.get(player);
            player.sendMessage(transText("&f  &f" + target.getName() +  " 님의 티피 요청을 &a수락 &f했습니다"));
            teleportRequests.remove(player);

            teleportWaiting.add(target);
            new BukkitRunnable() {
                int count = 0;
                @Override
                public void run() {
                    if(count == 3){
                        target.sendTitle(transText("&3&l&o✔"), transText("&f성공적으로 이동 되었습니다"));
                        target.teleport(player);
                        target.playSound(target, Sound.ENTITY_ENDERMAN_TELEPORT, 1f, 1f);
                        player.playSound(player, Sound.ENTITY_ENDERMAN_TELEPORT, 1f, 1f);
                        this.cancel();
                    }
                    target.sendTitle(transText("&3&l&o{n}".replace("{n}", Integer.toString(3 - count))), transText("&f티피 요청이 수락 되어 잠시 후 이동됩니다"), 0, 30, 0);
                    count += 1;
                    target.playSound(target, Sound.ENTITY_ITEM_PICKUP, 1f, 2f);
                }
            }.runTaskTimer(PICOSERVER.getInstance(), 0L ,20L);


            return true;
        }
        if (args[0].equalsIgnoreCase("거절")) {
            player.playSound(player, Sound.BLOCK_NOTE_BLOCK_BELL,  1f, 1.3f);
            if (!teleportRequests.containsKey(player)) {
                player.sendMessage(transText("&f  &f티피 요청이 존재하지 않습니다"));
                return true;
            }

            Player target = teleportRequests.get(player);
            player.sendMessage( transText("&f  &f" + target.getName() + " 님의 티피 요청을 &c거절 &f했습니다"));

            target.sendMessage( transText("&f  &f" + player.getName() + " 님께서 티피 요청을 &c거절 &f했습니다"));
            teleportRequests.remove(player);
            return true;
        }

        Player targetPlayer = Bukkit.getPlayer(args[0]);

        if (targetPlayer == null || !targetPlayer.isOnline()) {
            player.sendMessage(transText("&f  &f해당 플레이어를 찾을 수 없습니다"));
            return true;
        }
        if (player.getUniqueId().toString().equals(targetPlayer.getUniqueId().toString())){
            player.sendMessage(transText("&f  &f본인에게 티피요청을 보낼 수 없습니다"));
            return false;
        }
        if( teleportRequests.containsKey(targetPlayer)){
            player.sendMessage(transText("&f  &f해당 플레이어는 이미 티피 요청을 처리중입니다"));
            return true;
        }
        
        String senderWorldName = player.getWorld().getName();
        String targetWorldName = targetPlayer.getWorld().getName();
        
        if (!tpAbleWorlds.contains(senderWorldName) || !tpAbleWorlds.contains(targetWorldName)) {
            player.sendMessage(transText("&f  &f티피를 요청할 대상 또는 본인이 스폰이나 농장에 있지 않습니다"));
            return true;
        }

        teleportRequests.put(targetPlayer, player);

        player.sendMessage(transText("&f  &f" + targetPlayer.getName() + " 님에게 &6티피&f 요청을 보냈습니다"));
        TextComponent textcomp1 = new TextComponent(transText(" &a수락 &f"));
        textcomp1.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/티피 수락"));
        TextComponent textcomp2 = new TextComponent(transText(" &c거절 &f"));
        textcomp2.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/티피 거절"));

        targetPlayer.spigot().sendMessage(
                new TextComponent(
                        new TextComponent(transText("\n&f  &f" + player.getName()  + " 님께서 &6티피&f 를 요청했습니다 &7[")),
                        textcomp1,
                        new TextComponent("|") ,
                        textcomp2,
                        new TextComponent(transText("&7]\n&f")))
        );

        targetPlayer.playSound(targetPlayer.getLocation(), Sound.BLOCK_NOTE_BLOCK_XYLOPHONE, 5f, 0.89f);

        Bukkit.getScheduler().runTaskLater(PICOSERVER.getInstance(), () ->
                        targetPlayer.playSound(targetPlayer.getLocation(), Sound.BLOCK_NOTE_BLOCK_XYLOPHONE, 5f, 1.33f), 2L); //20 Tick (1 Second) delay before run() is called

        // 요청 만료 시간 설정 (10초 후 삭제)
        Bukkit.getScheduler().runTaskLater(PICOSERVER.getInstance(), () -> {
            if (teleportRequests.containsKey(targetPlayer) && teleportRequests.get(targetPlayer).equals(player)) {
                teleportRequests.remove(targetPlayer);
                player.sendMessage(transText("&f  &f티피 요청이 &7만료&f 되었습니다"));
                targetPlayer.sendMessage(transText("&f  &f" + player.getName() + " 님의 티피 요청이 &7만료&f 되었습니다"));
            }
        }, 10 * 20);

        return true;
    }
    private TPARequest() {}

    public static TPARequest getInstance() {
        if(instance == null){
            instance  = new TPARequest();
        }
        return instance;
    }
}
