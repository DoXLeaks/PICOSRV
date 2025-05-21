package kr.rth.picoserver;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import kr.rth.picoserver.Money.CommandListener;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class entityNametag implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        Player p = (Player) sender;
        var protocolManager = ProtocolLibrary.getProtocolManager();
        var ArmorStandSummon = new PacketContainer(PacketType.Play.Server.SPAWN_ENTITY);
        ArmorStandSummon.getIntegers()
                .write(0, 2);
//                .write(6, 78);
        ArmorStandSummon.getEntityTypeModifier().write(0, EntityType.ARMOR_STAND);
        ArmorStandSummon.getModifier().write(0, 2);
        ArmorStandSummon.getUUIDs().write(0, UUID.randomUUID());
        ArmorStandSummon.getDoubles().write(0, p.getLocation().getX())
                .write(1, p.getLocation().getY())
                .write(2, p.getLocation().getZ());
//        ArmorStandSummon.getDataWatcherModifier().write()
        protocolManager.sendServerPacket(p, ArmorStandSummon);
        return false;
    }
}
