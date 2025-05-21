package kr.rth.picoserver.minefarmFunctions;

import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class customPlayerInteractionEvent extends PlayerInteractEvent {

    public customPlayerInteractionEvent(Player who, Action action, ItemStack item, Block clickedBlock, BlockFace clickedFace) {
        super(who, action, item, clickedBlock, clickedFace);

    }

    @Override
    public  boolean isCancelled() {
        return super.isCancelled();

    }

    @Override
    public String toString() {
        return "customPlayerInteractionEvent";
    }
}
