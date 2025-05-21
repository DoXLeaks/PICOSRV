package kr.rth.picoserver.afk;

import kr.rth.picoserver.PICOSERVER;
import kr.rth.picoserver.afkpoint.AFKPoint;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class AFKListener implements Listener {
    public static final String AFK_WORLD = "AFK";
    private static final int DEBOUNCE_TIME = 30;
    private static final int REWARD_POINT_AMOUNT = 1;
    private int tooltipIndex = 1;
    public Map<UUID, Long> debounce = new ConcurrentHashMap<>();

    public AFKListener() {
        Bukkit.getServer().getScheduler().runTaskTimerAsynchronously(PICOSERVER.getInstance(), () -> {
            World world = Bukkit.getWorld(AFK_WORLD);
            if (world == null) return;
            long currentTime = System.currentTimeMillis();
            List<UUID> rewardList = world.getPlayers().stream().filter(player -> {
                Long debounced = debounce.get(player.getUniqueId());
                return (debounced == null || debounced < currentTime);
            }).map(Player::getUniqueId).toList();
            if (!rewardList.isEmpty()) AFKPoint.add(rewardList, REWARD_POINT_AMOUNT);
        }, 20L * 60L, 20L * 60L);

        Bukkit.getServer().getScheduler().runTaskTimerAsynchronously(PICOSERVER.getInstance(), () -> {
            World world = Bukkit.getWorld(AFK_WORLD);
            if (world == null) return;
            String message = getTooltipMessage();
            tooltipIndex++;
            world.getPlayers().forEach(player -> {
                if (!message.isEmpty()) player.sendActionBar(message);
            });
        }, 0L, 30L);

        Bukkit.getServer().getScheduler().runTaskTimer(PICOSERVER.getInstance(),() -> {
            Server server = Bukkit.getServer();
            server.getOnlinePlayers().forEach(player -> server.getOnlinePlayers().forEach(otherPlayer -> {
                if (player.getWorld().getName().equals(AFK_WORLD) && !player.isOp()) {
                    player.hidePlayer(PICOSERVER.getInstance(), otherPlayer);
                } else {
                    player.showPlayer(PICOSERVER.getInstance(), otherPlayer);
                }
            }));
        }, 0L, 20L);
    }

    @EventHandler
    public void onPlayerWorldChange(PlayerChangedWorldEvent event) {
        Player player = event.getPlayer();
        World world = player.getWorld();
        if (world.getName().equals(AFK_WORLD)) {
            debounce.put(player.getUniqueId(), System.currentTimeMillis() + 1000 * DEBOUNCE_TIME);
            String message = getTooltipMessage();
            if (!message.isEmpty()) player.sendActionBar(message);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        World world = player.getWorld();
        if (player.isOp()) return;
        if (world.getName().equals(AFK_WORLD)) event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        World world = player.getWorld();
        if (world.getName().equals(AFK_WORLD)) event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onEntityDamageEntity(EntityDamageByEntityEvent event) {
        Entity entity = event.getDamager();
        World world = entity.getWorld();
        if (entity.isOp()) return;
        if (world.getName().equals(AFK_WORLD)) event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        World world = player.getWorld();
        if (player.isOp()) return;
        if (world.getName().equals(AFK_WORLD)) {
            event.setCancelled(true);
            boolean isOp = player.isOp();
            try{
                player.setOp(true);
                player.performCommand("picoserver:메뉴 잠수");
            } finally {
                player.setOp(isOp);
            }
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        World world = player.getWorld();
        if (world.getName().equals(AFK_WORLD)) {
            debounce.put(player.getUniqueId(), System.currentTimeMillis() + 1000 * DEBOUNCE_TIME);
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        debounce.remove(event.getPlayer().getUniqueId());
    }

    private String getTooltipMessage() {
        if (tooltipIndex == 13) tooltipIndex = 1;
        return switch (tooltipIndex) {
            case 1:
            case 2:
            case 3:
                yield " 당신은 잠수 상태입니다 잠수를 종료하시려면 움직이세요   ";
            case 4:
            case 5:
            case 6:
                yield " 잠수 포인트는 분당 1 포인트씩 적립됩니다  ";
            case 7:
            case 8:
            case 9:
                yield " 상점에서 잠수 포인트로 아이템을 구매 할 수 있습니다   ";
            case 10:
            case 11:
            case 12:
                yield " 명령어 /잠수포인트 입력시 보유중인 포인트 확인이 가능합니다  ";
            default:
                tooltipIndex = 1;
                yield "";
        };
    }
}
