package kr.rth.picoserver.auth;

import org.bukkit.entity.Player;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class UnauthorizedPlayerRegistry {
    private final Set<UUID> unauthorizedPlayer = ConcurrentHashMap.newKeySet();

    public void register(Player player) {
        unauthorizedPlayer.add(player.getUniqueId());
    }

    public boolean unregister(Player player) {
        return unauthorizedPlayer.remove(player.getUniqueId());
    }

    public boolean contains(Player player) {
        return unauthorizedPlayer.contains(player.getUniqueId());
    }
}
