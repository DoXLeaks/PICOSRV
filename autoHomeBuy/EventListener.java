package kr.rth.picoserver.autoHomeBuy;

import kr.rth.picoserver.Database;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.block.data.Bisected;
import org.bukkit.block.data.type.Door;
import org.bukkit.block.data.type.WallSign;
import org.bukkit.block.sign.Side;
import org.bukkit.block.sign.SignSide;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.sql.SQLException;
import java.util.ArrayList;

public class EventListener implements Listener {

    HomeDataContainer homeDataContainer;

    public EventListener(HomeDataContainer homeDataContainer) {
        this.homeDataContainer = homeDataContainer;
    }

    private void setDoor(Location location, Material eDoorType, BlockFace eFace, Door.Hinge hinge)
    {
        Block bottom = location.getBlock();
        Block top = bottom.getRelative(BlockFace.UP);

        bottom.setType(eDoorType, false);
        top.setType(eDoorType, false);

        Door d1 = (Door) bottom.getBlockData();
        Door d2 = (Door) top.getBlockData();

        d1.setHalf(Bisected.Half.BOTTOM);
        d2.setHalf(Bisected.Half.TOP);

        d1.setHinge(hinge);
        d2.setHinge(hinge);

        d1.setFacing(eFace);
        d2.setFacing(eFace);

        top.setBlockData(d2);
        bottom.setBlockData(d1);
        //순서 바꾸면 작동이 안됩니다
    }

    private void setLocketteSign(Location location, BlockFace face, boolean isMain, Player player) {
        Block sign = location.getBlock();
        sign.setType(Material.BAMBOO_WALL_SIGN);

        WallSign signData = (WallSign) sign.getBlockData();
        signData.setFacing(face);
        sign.setBlockData(signData);

        Sign signState = (Sign) sign.getState();
        signState.setWaxed(true);
        SignSide side = signState.getSide(Side.FRONT);
        if (isMain) {
            side.setLine(0, "§0[잠금]");
            side.setLine(1, player.getName());
        } else {
            side.setLine(0, "§0[추가잠금]");
        }
        signState.update();
    }

    @EventHandler
    public void onRightClick(PlayerInteractEvent event) {
        Block block = event.getClickedBlock();
        if (block == null) return;
        if (!homeDataContainer.getTriggerBlocks().contains(block.getType())) return;
        BlockFace face = event.getBlockFace();
        if (!isValidFace(face)) return;

        Player player = event.getPlayer();
        World world = block.getWorld();

        if (!homeDataContainer.getWorlds().contains(world.getName())) return;
        ItemStack item = player.getInventory().getItemInMainHand();
        if (homeDataContainer.getPurchasingItem() == null) return;
        if (!homeDataContainer.getPurchasingItem().isSimilar(item)) return;
        if (homeDataContainer.isCached(player)) {
            player.sendTitle(ChatColor.translateAlternateColorCodes('&', "§x§d§f§f§b§5§8§oH§x§e§3§f§c§7§d§oO§x§e§8§f§c§a§2§oU§x§e§c§f§d§c§6§oS§x§f§0§f§d§e§b§oE"), ChatColor.translateAlternateColorCodes('&', "&f&o당신은 이미 집을 소유하고 있습니다&f"), 10, 70, 20);
            player.playSound(player, Sound.BLOCK_LANTERN_BREAK, 1f, 1f );
            return;
        }
        ArrayList<Object> arguments = new ArrayList<>();
        arguments.add(player.getUniqueId().toString());

        try {
            int bought = (Integer) new ArrayList<>(Database.getInstance().execute("SELECT EXISTS (SELECT * FROM homeBuyLog WHERE uuid = ?)", arguments).get(0).values()).get(0);
            if (0 < bought) {
                player.sendTitle(ChatColor.translateAlternateColorCodes('&', "§x§d§f§f§b§5§8§oH§x§e§3§f§c§7§d§oO§x§e§8§f§c§a§2§oU§x§e§c§f§d§c§6§oS§x§f§0§f§d§e§b§oE"), ChatColor.translateAlternateColorCodes('&', "&f&o당신은 이미 집을 소유하고 있습니다&f"), 10, 70, 20);
                player.playSound(player, Sound.BLOCK_LANTERN_BREAK, 1f, 1f );
                homeDataContainer.cache(player);
                return;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        event.setCancelled(true);
        item.setAmount(item.getAmount() - 1);
        homeDataContainer.cache(player);
        player.sendTitle(ChatColor.translateAlternateColorCodes('&', "§x§d§f§f§b§5§8§oH§x§e§3§f§c§7§d§oO§x§e§8§f§c§a§2§oU§x§e§c§f§d§c§6§oS§x§f§0§f§d§e§b§oE"), ChatColor.translateAlternateColorCodes('&', "&f집 구매가 완료 되었습니다 &7( /lock )"), 10, 200, 10);

        try {
            Database.getInstance().execute("INSERT INTO homeBuyLog VALUES(?)", arguments);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        Location doorLoc = block.getLocation().clone().subtract(0.0, 1.0, 0.0);
        Location door2Loc = doorLoc.clone();
        Location signLoc = block.getLocation().clone().add(face.getDirection()).add(0.0, 1.0, 0.0);
        Location sign2Loc = signLoc.clone();

        if (face == BlockFace.SOUTH) {
            door2Loc.setX(door2Loc.getX() + 1);
            sign2Loc.setX(sign2Loc.getX() + 1);
        }
        if (face == BlockFace.NORTH) {
            door2Loc.setX(door2Loc.getX() - 1);
            sign2Loc.setX(sign2Loc.getX() - 1);
        }
        if (face == BlockFace.WEST) {
            door2Loc.setZ(door2Loc.getZ() + 1);
            sign2Loc.setZ(sign2Loc.getZ() + 1);
        }
        if (face == BlockFace.EAST) {
            door2Loc.setZ(door2Loc.getZ() - 1);
            sign2Loc.setZ(sign2Loc.getZ() - 1);
        }

        setDoor(doorLoc, Material.IRON_DOOR, face.getOppositeFace(), Door.Hinge.LEFT);
        setDoor(door2Loc, Material.IRON_DOOR, face.getOppositeFace(), Door.Hinge.RIGHT);

        setLocketteSign(signLoc, face, true, player);
        setLocketteSign(sign2Loc, face, false, player);

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
