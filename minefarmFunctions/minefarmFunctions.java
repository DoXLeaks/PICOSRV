package kr.rth.picoserver.minefarmFunctions;


import com.bgsoftware.superiorskyblock.api.SuperiorSkyblockAPI;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


public class minefarmFunctions implements Listener {

    public void superhoe(PlayerInteractEvent e) {

        if(!e.getPlayer().getWorld().getName().equals("plotworld")){
            return;
        }

        if(!SuperiorSkyblockAPI.getGrid().getIslandAt(e.getPlayer().getLocation()).isMember(SuperiorSkyblockAPI.getPlayer(e.getPlayer()))){
            return;
        }


//
        Block clickedBlock = e.getClickedBlock();

        World world = clickedBlock.getWorld();
        int centerX = clickedBlock.getX();
        int centerY = clickedBlock.getY();
        int centerZ = clickedBlock.getZ();
        Integer a=  0;
        for (int xOffset = -1; xOffset <= 1; xOffset++) {
            for (int zOffset = -1; zOffset <= 1; zOffset++) {
                Block blockToTill = world.getBlockAt(centerX + xOffset, centerY, centerZ + zOffset);

                // 만약 흙 블록이라면 경작된 토지로 변경
                if (blockToTill.getType().equals(Material.GRASS_BLOCK) || blockToTill.getType().equals(Material.DIRT)) {
                    blockToTill.setType(Material.FARMLAND);
                    a += 1;
                }
            }
        }
        if(a > 0) {
        e.getPlayer().getWorld().playSound(e.getPlayer().getLocation(), Sound.ITEM_HOE_TILL,1f, 1f);

        }

    }
    public void supergrass(PlayerInteractEvent e) {

        if(!e.getPlayer().getWorld().getName().equals("plotworld")){
            return;
        }

        if(!SuperiorSkyblockAPI.getGrid().getIslandAt(e.getPlayer().getLocation()).isMember(SuperiorSkyblockAPI.getPlayer(e.getPlayer()))){
            return;
        }


//
        Block clickedBlock = e.getClickedBlock();

        World world = clickedBlock.getWorld();
        int centerX = clickedBlock.getX();
        int centerY = clickedBlock.getY();
        int centerZ = clickedBlock.getZ();
        Integer a=  0;
        for (int xOffset = -1; xOffset <= 1; xOffset++) {
            for (int zOffset = -1; zOffset <= 1; zOffset++) {
                Block blockToTill = world.getBlockAt(centerX + xOffset, centerY, centerZ + zOffset);

                // 만약 흙 블록이라면 경작된 토지로 변경
                    blockToTill.setType(Material.GRASS_BLOCK);
                    a += 1;
            }
        }
        if(a > 0) {
            e.getPlayer().getWorld().playSound(e.getPlayer().getLocation(), Sound.BLOCK_GRASS_PLACE,1f, 1f);

        }

    }


    @EventHandler
    public void onSneak(PlayerToggleSneakEvent e) {
        if(!e.isSneaking()){
            return;
        }
        Player p = e.getPlayer();
        Location loc = p.getLocation();
        loc.setY(loc.getY()  - 1);
//        Location
        if(p.getWorld().getBlockAt(loc).getType().equals(Material.LAPIS_BLOCK)) {
            for( int i = Double.valueOf(Math.floor(loc.getY())).intValue()   - 1 ; i > -65; i-- ){
                loc.setY(i);
                if(!p.getWorld().getBlockAt(loc).getType().equals(Material.AIR))  {
                    loc.setY( i + 1);
                    p.teleport(loc);
                    p.playSound(p, Sound.ENTITY_EXPERIENCE_ORB_PICKUP,1f ,1f);
                    break;
                }
            }

        }
    }

    @EventHandler
    public void onBreak(BlockBreakEvent e) {
        if(e.getPlayer().getInventory().getItemInMainHand().getType().equals(Material.AIR)) {
            return;
        }
        if(e.getPlayer().getInventory().getItemInMainHand().getItemMeta() == null) {
            return;
        }
        if(e.getPlayer().getInventory().getItemInMainHand().getItemMeta().getDisplayName().trim().equals("")) {
            return;
        }
//        Bukkit.getLogger().info("1");
        var especialList = new ArrayList<>(List.of(new String[]{"[太陽熱] 태양열 곡괭이 ✱", "[勞動者] 노동자 곡괭이", "[鎔鑛爐] &f용광로 곡괭이 \uD83D\uDD25", }));

        if(!especialList.contains(ChatColor.stripColor( e.getPlayer().getInventory().getItemInMainHand().getItemMeta().getDisplayName().trim() ).trim())){
            return;
        }
        switch (ChatColor.stripColor( e.getPlayer().getInventory().getItemInMainHand().getItemMeta().getDisplayName().trim() ).trim()  ) {

            case "[鎔鑛爐] &f용광로 곡괭이 \uD83D\uDD25":
                if(e.getBlock().getType().equals(Material.DEEPSLATE) || e.getBlock().getType().equals(Material.STONE) || e.getBlock().getType().equals(Material.COAL_ORE) ) {
                    e.setDropItems(false);
                    return;
                }

            case "[太陽熱] 태양열 곡괭이 ✱":
            case "[勞動者] 노동자 곡괭이":
                if(e.getBlock().getType().equals(Material.IRON_ORE) || e.getBlock().getType().equals(Material.DEEPSLATE_IRON_ORE)) {
                    e.setDropItems(false);
                    Collection<ItemStack> dropItem = e.getBlock().getDrops(e.getPlayer().getItemInHand());


                    for( ItemStack i : dropItem) {
                        if(i.getType().equals(Material.RAW_IRON)){
                            Integer amount = i.getAmount();
                            ItemStack i2 = new ItemStack(Material.IRON_INGOT);
                            i2.setAmount(amount);

                            e.getBlock().getWorld().dropItem(e.getBlock().getLocation(), i2);
                            continue;
                        }

                        e.getBlock().getWorld().dropItem(e.getBlock().getLocation(), i);
                    }


                }
                if(e.getBlock().getType().equals(Material.GOLD_ORE) || e.getBlock().getType().equals(Material.DEEPSLATE_GOLD_ORE)) {
                    e.setDropItems(false);
                    Collection<ItemStack> dropItem = e.getBlock().getDrops(e.getPlayer().getItemInHand());
                    for( ItemStack i : dropItem) {
                        if(i.getType().equals(Material.RAW_GOLD)){
                            Integer amount = i.getAmount();
                            i = new ItemStack(Material.GOLD_INGOT);
                            i.setAmount(amount);

                            e.getBlock().getWorld().dropItem(e.getBlock().getLocation(), i);
                            continue;
                        }
                        e.getBlock().getWorld().dropItem(e.getBlock().getLocation(), i);
                    }
                }

        }




    }

    @EventHandler
    public void onSneak(PlayerMoveEvent e) {
        if( ! (e.getTo().getY() > e.getFrom().getY())) {
            return;
        }
        Player p = e.getPlayer();
        Location loc = p.getLocation();
        loc.setY(loc.getY()  - 1);
//        Location
        if(p.getWorld().getBlockAt(loc).getType().equals(Material.LAPIS_BLOCK)) {
            for( int i = Double.valueOf(Math.floor(loc.getY())).intValue()  + 1; i < 256; i++ ){
                loc.setY(i);
                if(!p.getWorld().getBlockAt(loc).getType().equals(Material.AIR))  {
                    loc.setY( i + 1);
                    p.teleport(loc);
                    p.playSound(p, Sound.ENTITY_EXPERIENCE_ORB_PICKUP,1f ,1f);
                    break;
                }
            }

        }
    }



    @EventHandler(priority = EventPriority.HIGHEST)
    public void onRightClick(PlayerInteractEvent e) {



        if(e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            if(e.getItem() == null ) return;
            if(!e.getItem().getType().equals(Material.AIR) && e.getItem().getItemMeta() != null) {

                if(e.getPlayer().getItemInHand().getItemMeta() != null) {
                    switch (ChatColor.stripColor(
                        e.getPlayer().getItemInHand().getItemMeta().getDisplayName().trim())) {
                        case "[神祕] 신비한 이끼 ⧫":
                            e.setCancelled(true);
                            supergrass(e);
                            break;
                        case "[CASH] 잃어버린 자물쇠 ⧫":
                            superhoe(e);
                            break;
                        case "[CASH] 푸른 얼음 ⧫":
                            if (!SuperiorSkyblockAPI.getGrid()
                                .getIslandAt(e.getPlayer().getLocation())
                                .isMember(SuperiorSkyblockAPI.getPlayer(e.getPlayer()))) {
                                e.setCancelled(true);
                                return;
                            }
//                            if(e.getItem().getAmount() == e.getPlayer().getItemInHand().getAmount()){
//                                return;
//                            }
                            e.setCancelled(true);
                            Location blockLocation = e.getClickedBlock()
                                .getRelative(e.getBlockFace()).getLocation();
                            e.getPlayer().getWorld().getBlockAt(blockLocation)
                                .setType(Material.WATER);
//                            ItemStack stack = e.getItem();
//                            stack.setAmount(1);
//                            e.getPlayer().getInventory().addItem(stack);
//                            e.getPlayer().getWorld().playSound(e.getPlayer().getLocation(), Sound.BLOCK_WATER_AMBIENT, 1f, 1f);
                            break;
                    }
                }
            }
        }
    }

}