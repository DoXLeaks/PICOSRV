package kr.rth.picoserver.chestPurchase;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.block.data.type.Chest;
import org.bukkit.block.data.type.WallSign;
import org.bukkit.block.sign.Side;
import org.bukkit.block.sign.SignSide;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public class EventListener implements Listener {

    ChestPurchaseConfig chestPurchaseConfig;

    public EventListener(ChestPurchaseConfig chestPurchaseConfig) {
        this.chestPurchaseConfig = chestPurchaseConfig;
    }


    private void setLocketteSign(Location location, BlockFace face, Player player) {
        Block sign = location.getBlock();
        sign.setType(Material.BAMBOO_WALL_SIGN);

        WallSign signData = (WallSign) sign.getBlockData();
        signData.setFacing(face);
        sign.setBlockData(signData);

        Sign signState = (Sign) sign.getState();
        signState.setWaxed(true);
        SignSide side = signState.getSide(Side.FRONT);
        side.setLine(0, "§0[잠금]");
        side.setLine(1, player.getName());
        signState.update();
    }

    @EventHandler
    public void onRightClick(PlayerInteractEvent event) {
        Block block = event.getClickedBlock();
        if (block == null) return;
        if (event.getHand() != EquipmentSlot.HAND) return;
        if (chestPurchaseConfig.getTriggerBlock() != block.getType()) return;
        BlockFace face = event.getBlockFace();
        if (!isValidFace(face)) return;

        Player player = event.getPlayer();
        World world = block.getWorld();

        if (!chestPurchaseConfig.getPurchasableWorlds().contains(world.getName())) return;
        ItemStack item = player.getInventory().getItemInMainHand();
        if (chestPurchaseConfig.getPurchasingItem() == null) return;
        if (!chestPurchaseConfig.getPurchasingItem().isSimilar(item)) return;

        //player.sendTitle(ChatColor.translateAlternateColorCodes('&', "§x§d§f§f§b§5§8§oH§x§e§3§f§c§7§d§oO§x§e§8§f§c§a§2§oU§x§e§c§f§d§c§6§oS§x§f§0§f§d§e§b§oE"), ChatColor.translateAlternateColorCodes('&', "&f집 구매가 완료 되었습니다 &7( /lock )"), 10, 200, 10);

        Location chestLoc = block.getLocation().clone();
        Location chest2Loc = block.getLocation().clone();
        Location signLoc = block.getLocation().clone().add(face.getDirection());

        if (face == BlockFace.SOUTH) {
            chest2Loc.setX(chest2Loc.getX() + 1);
        }
        if (face == BlockFace.NORTH) {
            chest2Loc.setX(chest2Loc.getX() - 1);
        }
        if (face == BlockFace.WEST) {
            chest2Loc.setZ(chest2Loc.getZ() + 1);
        }
        if (face == BlockFace.EAST) {
            chest2Loc.setZ(chest2Loc.getZ() - 1);
        }

        Block chest = chestLoc.getBlock();
        Block chest2 = chest2Loc.getBlock();

        if (chest2.getType() != chestPurchaseConfig.getSideBlock()) return;
        event.setCancelled(true);

        item.setAmount(item.getAmount() - 1);

        chest.setType(Material.CHEST);
        chest2.setType(Material.CHEST);

        Chest chestData = (Chest) chest.getBlockData();
        Chest chest2Data = (Chest) chest2.getBlockData();

        chestData.setFacing(face);
        chestData.setType(Chest.Type.RIGHT);
        chest2Data.setFacing(face);
        chest2Data.setType(Chest.Type.LEFT);

        chest.setBlockData(chestData, true);
        chest2.setBlockData(chest2Data, true);

        setLocketteSign(signLoc, face, player);
    }

    public boolean isValidFace(BlockFace face) {
        switch (face) {
            case NORTH:
            case SOUTH:
            case EAST:
            case WEST:
                return true;
            default:
                return false;
        }
    }
}
