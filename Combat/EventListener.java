package kr.rth.picoserver.Combat;

import kr.rth.picoserver.PICOSERVER;
import kr.rth.picoserver.Team.teams;
import kr.rth.picoserver.util.RegionChecker;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.projectiles.ProjectileSource;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import static kr.rth.picoserver.util.transText.transText;

public class EventListener implements Listener {
    static EventListener instance;
    public Map<UUID, Long> pvpActiveMap = new ConcurrentHashMap<>();
    HashMap<UUID, PermissionAttachment> perms = new HashMap<>();

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onDamage(EntityDamageByEntityEvent e) {
        Player attacker;
        Player victim;
        if (e.getEntity() instanceof Player) {
            victim = (Player) e.getEntity();
        }
        else return;
        if (e.getDamager() instanceof Player) {
            attacker = (Player) e.getDamager();
        } else {
            if (e.getDamager() instanceof Projectile) {
                ProjectileSource shooter = ((Projectile) e.getDamager()).getShooter();
                if (shooter instanceof Player) {
                    attacker = (Player) shooter;
                } else {
                    return;
                }
            } else {
                return;
            }
        }

        try {
            if (RegionChecker.isPvpDisabledInPlayerRegion(attacker)) return;
            if (RegionChecker.isPvpDisabledInPlayerRegion(victim)) return;
        } catch (Exception ignored) {
        }

        if (!((victim.getWorld().getName().equals("pvp") || victim.getWorld().getName().equals("newlobby")) && (attacker.getWorld().getName().equals("pvp") || attacker.getWorld().getName().equals("newlobby")))) {
            return;
        }
        if (victim.getName().startsWith("CIT-")) return;
        if (!teams.isAttackable(attacker, victim)) return;
        disableElevator(attacker);
        disableElevator(victim);
        if (!victim.isOp()) {
            pvpActiveMap.put(victim.getUniqueId(), System.currentTimeMillis() + (10 * 1000));
        }
        if (!attacker.isOp()) {
            pvpActiveMap.put(attacker.getUniqueId(), System.currentTimeMillis() + (10 * 1000));
        }
    }

    private void disableElevator(Player player) {
        if (!pvpActiveMap.containsKey(player.getUniqueId()) && !player.isOp()) {
            player.sendMessage(transText("\n§f \uE13A 전투가 시작되어 명령어 사용이 제한 됩니다\n&f"));
            PermissionAttachment attachment = perms.get(player.getUniqueId());
            if (attachment != null) attachment.setPermission("elevator.use", false);
            else {
                PICOSERVER.getInstance().getLogger().warning("Cannot find perm attachment from player " + player.getName() + "! reallocating...");
                allocatePerm(player).setPermission("elevator.use", false);
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerDeath(PlayerDeathEvent e) {
        pvpActiveMap.put(e.getEntity().getUniqueId(), 0L);
    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent e) {
        if (e.getMessage().startsWith("PICOSERVER:heal") ||
                e.getMessage().startsWith("/힐") ||
                e.getMessage().startsWith("/glf") ||
                e.getMessage().startsWith("/ㅗㄷ미") ||
                e.getMessage().startsWith("/heal") ||
                e.getMessage().startsWith("/m") ||
                e.getMessage().startsWith("/w") ||
                e.getMessage().startsWith("/귓") ||
                e.getMessage().startsWith("/팀 채팅") ||
                e.getMessage().startsWith("/정보")

        ) {
            return;
        }
        if (pvpActiveMap.containsKey(e.getPlayer().getUniqueId())) {
            e.getPlayer().playSound(e.getPlayer(), Sound.BLOCK_LANTERN_BREAK, 0.5f, 1f);
            e.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR,
                    TextComponent.fromLegacyText("&f 전투 중 명령어 사용이 금지 됩니다 ")
            );
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerWorldChange(PlayerChangedWorldEvent event) {
        Player player = event.getPlayer();
        if (player.getWorld().getName().equals("spawn")) {
            PermissionAttachment attachment = perms.get(player.getUniqueId());
            if (attachment != null) {
                attachment.setPermission("elevator.use", true);
            } else {
                PICOSERVER.getInstance().getLogger().warning("Cannot find perm attachment from player " + player.getName() + "! reallocating...");
                allocatePerm(player).setPermission("elevator.use", true);
            }
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        Player player = e.getPlayer();
        if (pvpActiveMap.containsKey(player.getUniqueId())) {
            player.setMetadata("bypass-totem", new FixedMetadataValue(PICOSERVER.getInstance(), true));
            player.setHealth(0);
        }
        perms.remove(player.getUniqueId());
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        allocatePerm(player);
    }

    private PermissionAttachment allocatePerm(Entity entity) {
        PermissionAttachment attachment = entity.addAttachment(PICOSERVER.getInstance());
        perms.put(entity.getUniqueId(), attachment);
        return attachment;
    }

    private EventListener() {
        Bukkit.getServer().getScheduler().runTaskTimerAsynchronously(PICOSERVER.getInstance(), () -> {
            //pvpActiveMap 은 전투 활성화 후 11초 후에 시간을 담은 변수
            for (UUID uuid : pvpActiveMap.keySet()) {
                NamespacedKey key = new NamespacedKey(PICOSERVER.getInstance(), "pvp-" + uuid);
                if (System.currentTimeMillis() > pvpActiveMap.get(uuid)) {
                    BossBar bb = Bukkit.getBossBar(key);
                    if (bb != null) {
                        bb.removeAll();
                        Bukkit.removeBossBar(key);
                    }

                    PermissionAttachment attachment = perms.get(uuid);
                    if (attachment != null) {
                        attachment.setPermission("elevator.use", true);
                    }
                    else {
                        Player player = Bukkit.getPlayer(uuid);
                        if (player != null) {
                            PICOSERVER.getInstance().getLogger().warning("Cannot find perm attachment from player " + player.getName() + "! reallocating...");
                            allocatePerm(player).setPermission("elevator.use", true);
                        }
                    }
                    pvpActiveMap.remove(uuid);
                    continue;
                }
                BossBar bossbar = Bukkit.getBossBar(key);
                if (bossbar == null) {
                    bossbar = Bukkit.createBossBar(key,
                            "전투모드: [s]초".replace("[s]", String.valueOf(Long.valueOf((pvpActiveMap.get(uuid) - System.currentTimeMillis()) / 1000).intValue())),
                            BarColor.WHITE,
                            BarStyle.SOLID
                    );
                    double progress = 1.0;
                    bossbar.setProgress(progress);
                    Player player = Bukkit.getPlayer(uuid);
                    if (player != null) {
                        bossbar.addPlayer(player);
                    }
                }

                long timeRemaining = pvpActiveMap.get(uuid) - System.currentTimeMillis();

                double progress = (pvpActiveMap.get(uuid).doubleValue() - System.currentTimeMillis()) / 10000;

                bossbar.setProgress(Math.min(progress, 1.0f));
                double secondsRemainingInt = (timeRemaining / 1000.0);
                bossbar.setTitle("전투가 시작 되었습니다 §7( %.1f초 뒤 해제 )".formatted(secondsRemainingInt));
            }
        }, 0, 0L);
    }

    @SuppressWarnings("null")
    public static EventListener getInstance() {
        if (instance == null) {
            instance = new EventListener();
        }
        return instance;
    }
}
