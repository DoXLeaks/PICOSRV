package kr.rth.picoserver.etc;

import kr.rth.picoserver.PICOSERVER;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.Bed;

import static kr.rth.picoserver.HealthBar.HealthBarManager.update;

public class woolplate {
    public static void autohealing(){
        PICOSERVER.getInstance().getServer().getScheduler().scheduleAsyncRepeatingTask(PICOSERVER.getInstance(), () -> {
            for (var i : PICOSERVER.getInstance().getServer().getOnlinePlayers()) {
                if (!i.getWorld().getName().equals("pvp"))
                    continue;
                Location loc = i.getLocation();
                Block block = loc.getWorld().getBlockAt(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
//                Bukkit.getLogger().info(String.valueOf(block.getType()));
                if (block.getType().equals(Material.RED_CARPET)) {
                    if (i.getHealth() < i.getMaxHealth()) {
                        i.getWorld().spawnParticle(Particle.HEART, loc, 3, 1, 1, 1, 0.1);
                        i.playSound(i, Sound.BLOCK_LAVA_AMBIENT, 1, 2);
                    }
                    try {
                        i.setHealth(i.getHealth() + 2);
                        update(i);
                    } catch (Exception e) {
                    }
                }
            }
        }, 0L, 20L);
    }
}
