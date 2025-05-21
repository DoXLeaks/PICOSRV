package kr.rth.picoserver;
//
//import com.nametagedit.plugin.NametagEdit;
//import com.nametagedit.plugin.api.NametagAPI;
//import com.nametagedit.plugin.api.events.NametagEvent;

import com.destroystokyo.paper.event.player.PlayerJumpEvent;
import com.plotsquared.bukkit.util.BukkitUtil;
import com.plotsquared.core.location.Location;
import com.plotsquared.core.plot.Plot;
import com.plotsquared.core.plot.PlotArea;
import kr.rth.picoserver.Combat.EventListener;
import kr.rth.picoserver.Money.Money;
import kr.rth.picoserver.etc.GodMode;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import static kr.rth.picoserver.Team.teams.updateNametag;
import static kr.rth.picoserver.util.numberWithComma.numberWithComma;
import static kr.rth.picoserver.util.random.generateRandomNumber;

public class Cancels implements Listener {
    public HashMap<Material ,Integer> cancelingItems = new HashMap<>();
    public Cancels () {
        cancelingItems.put(Material.STONE, 64);
        cancelingItems.put(Material.COPPER_ORE, 20);
        cancelingItems.put(Material.IRON_ORE, 10);
        cancelingItems.put(Material.GOLD_ORE, 5 );
        cancelingItems.put(Material.DIAMOND_ORE, 1);
    }

    public Material selectRandomItem() {
        int totalProbability = cancelingItems.values().stream().mapToInt(Integer::intValue).sum();
        Random rand = new Random();
        int randomNumber = rand.nextInt(totalProbability);
        while (true){
            for (Material item : cancelingItems.keySet()) {
                int probability = cancelingItems.get(item);
                if (randomNumber < probability) {
                    return item;
                }
                randomNumber -= probability;
            }
        }

        // This should not happen if your probabilities are correctly normalized.
    }

    private ItemStack findExistingItem(Player player, ItemStack targetItem) {
        for (ItemStack item : player.getInventory().getContents()) {
            if (item != null && item.isSimilar(targetItem) && item.getAmount() < item.getMaxStackSize()) {
                return item;
            }
        }
        return null;
    }

    public HashMap<Player, Long> jumpcool = new HashMap<>();

    @EventHandler
    public void onConsume(PlayerItemConsumeEvent event) {
        ItemStack item = event.getItem();
        Material type = item.getType();
        if (type == Material.POTION || type == Material.MILK_BUCKET) event.setCancelled(true);
    }
    @EventHandler
    public void onJump(BlockBreakEvent e ){
        if(e.getBlock() == null) {
            return;
        }
        if(e.getBlock().getType().equals(Material.SNOW_BLOCK)) {
            e.setDropItems(false);
            return;

        }
        if(e.getBlock().getType().equals(Material.SNOW)) {
            e.setDropItems(false);

        }
    }
    @EventHandler
    public void onMove2(PlayerMoveEvent e ){
        if(!e.getPlayer().getWorld().getName().equals("pvp") && !e.getPlayer().getWorld().getName().equals("dungeon")){
            return;
        }
        if( Math.floor(e.getFrom().getX()) == Math.floor(e.getTo().getX()) && (Math.floor(e.getTo().getY()) == Math.floor(e.getFrom().getY())  || Math.floor(e.getFrom().getY()) < Math.floor(e.getTo().getY())) && Math.floor(e.getFrom().getZ()) == Math.floor(e.getTo().getZ())) return;


        if(e.getTo().getWorld().getBlockAt(e.getTo().getBlockX(),e.getTo().getBlockY() -1, e.getTo().getBlockZ()).getType().equals(Material.SHROOMLIGHT)) {
            Player player = e.getPlayer();
            if (EventListener.getInstance().pvpActiveMap.containsKey(player.getUniqueId())) {
                if (e.hasChangedBlock()) {
                    player.sendTitle("\uE07D", "§f전투 중에는 도약 블럭을 사용 할 수 없습니다", 0, 20, 20);
                }
                return;
            }
            Vector direction = player.getLocation().getDirection();

            Vector movement = new Vector(5, 0, 5);

            movement = direction.multiply(movement);
            movement.setY(0.7);

                // 플레이어를 이동시키기
//            player.playSound(player, Sound.BLOCK_PISTON_CONTRACT, 1f, 0.8f);
            player.setVelocity(movement);
            player.playSound(player, Sound.ENTITY_FIREWORK_ROCKET_BLAST_FAR, 1f, 0.8f);
            player.getWorld().spawnParticle(Particle.EXPLOSION_LARGE, player.getLocation(), 0);
        }
    }

    @EventHandler
    public void onMove(PlayerJumpEvent e ){
        Player player = e.getPlayer();
        String worldName = player.getWorld().getName();
        if(worldName.equals("pvp") || worldName.equals("parkour") || worldName.equals("newlobby")) return;
        if(!player.hasPermission("superjump.use")) return;
        if(player.isSneaking()) {
            jumpcool.putIfAbsent(player, 0L);
            if(jumpcool.get(player) > System.currentTimeMillis()) {
                return;
            }

            Vector direction = player.getLocation().getDirection();

            Vector movement = new Vector(2, 0, 2);

            movement = direction.multiply(movement);
            movement.setY(0.5);

            // 플레이어를 이동시키기
//            player.playSound(player, Sound.BLOCK_PISTON_CONTRACT, 1f, 0.8f);
            player.setVelocity(movement);
            jumpcool.put(player, System.currentTimeMillis() + 1000);
            player.playSound(player, "pico_sounds:pico.jump", 0.6f, 1f);
            player.getWorld().spawnParticle(Particle.EXPLOSION_LARGE, player.getLocation(), 0);


//            for( int i = 0 ; i < 30; i += 1) {
//                Bukkit.getScheduler().scheduleAsyncDelayedTask(PICOSERVER.getInstance(), () -> {
//                    player.getWorld().spawnParticle(Particle.ST);
//                }, i);
//            }


        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onCombat(EntityDamageByEntityEvent e ) {
        if (!(e.getEntity() instanceof Player)) return;
        if (!(e.getDamager() instanceof Player)) return;
        if (GodMode.isGodMode) {
            e.setCancelled(true);
            return;
        }
        String worldName = e.getDamager().getWorld().getName();
        if (!worldName.contains("pvp") && !worldName.contains("newlobby")) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onDamage(EntityDamageEvent e ){
        if (!(e.getEntity() instanceof Player)) {
            return ;
        }
        if(e.getCause().equals(EntityDamageEvent.DamageCause.FALL)) {
            e.setCancelled(true);
        }
    }
//    @EventHandler
//    public void onfood(FoodLevelChangeEvent e ){
//        if (!(e.getEntity() instanceof Player)) {
//            return ;
//        }
//        e.setFoodLevel(20);
//    }
    @EventHandler
    public void onBowDamage(PlayerItemDamageEvent e){
        ItemStack item = e.getItem();
        if (item.getType() == Material.BOW) {
            e.setCancelled(true);
            if (item.getItemMeta() instanceof Damageable itemMeta) {
                itemMeta.setDamage(0);
                item.setItemMeta(itemMeta);
            }
        }
    }

    @EventHandler
    public void onExplode(EntityExplodeEvent e){
        e.setCancelled(true);
    }
    @EventHandler
    public void onBlockExplode(BlockExplodeEvent e){
        e.setCancelled(true);
    }
//    @EventHandler
//    public void onProjectileHit(ProjectileHitEvent e) {
//        e.getEntity().remove();
//    }
//    @EventHandler
//    public void onProjectileSummon(ProjectileLaunchEvent e) {
//        e.getEntity().setVelocity(e.getEntity().getVelocity().multiply(5D));
//    }

    @EventHandler
    public void onPlayerInteractAtGodMode(PlayerInteractEvent e) {
        if (!GodMode.isGodMode) return;
        Action action = e.getAction();
        if (action.isLeftClick() || action.isRightClick()) {
            ItemStack item = e.getItem();
            if (item == null) return;
            Material itemType = item.getType();
            if (itemType == Material.SNOWBALL || itemType == Material.FISHING_ROD || itemType == Material.BOW || itemType == Material.CROSSBOW || itemType == Material.TRIDENT) {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void opndeBreath(EntityAirChangeEvent e) {
        if(!(e.getEntity() instanceof Player p)) return;
        e.setCancelled(true);
    }
    @EventHandler
    public void onWorldChange(PlayerChangedWorldEvent e) {
        if(!(e.getPlayer().isOp())) {
            Bukkit.getScheduler().runTaskLater(PICOSERVER.getInstance(), () -> {
                e.getPlayer().setGameMode(GameMode.ADVENTURE);
            }, 5L);
        }
    }
    public int countItemsInInventory(Player player, Material material) {
        int count = 0;
        for (ItemStack item : player.getInventory().getContents()) {
            if (item != null && item.getType() == material) {
                count += item.getAmount();
            }
        }
        return count;
    }
//    @EventHandler
//    public void onWorldChange1(PlayerChangedWorldEvent e ){
//        if(e.getPlayer().getWorld().getName().equals("backup_pvp")) {
//            for( Player i : Bukkit.getWorld("backup_pvp").getPlayers()) {
//                e.getPlayer().hideEntity(PICOSERVER.getInstance(), i);
//            }
//        } else {
//            for( Player i : Bukkit.getWorld("backup_pvp").getPlayers()) {
//                e.getPlayer().showEntity(PICOSERVER.getInstance(), i);
//            }
//        }
//    }
    @EventHandler
    public void onPlace(BlockPlaceEvent e ) {
        if(e.getPlayer().getWorld().getName().equals("plotworld")) {
            return;
        }
        if(!e.getPlayer().isOp()) {
            e.setCancelled(true);
        }
    }
    @EventHandler(ignoreCancelled = true)
    public void onDeath(PlayerDeathEvent e) {
        Player player = e.getEntity(); // 죽은 플레이어 가져오기
        String message = "YOU DEAD";

        // 타이틀을 보여주는 Scheduler 실행
        for(int i = 0; i < message.length(); i +=1){


            int finalI = i;
            Bukkit.getScheduler().scheduleSyncDelayedTask(PICOSERVER.getInstance(),() -> {
                    String title = message.substring(0, finalI + 1);
                    player.sendTitle( "§x§a§b§2§0§2§0§l§o"+title,"", 0, 20, 10); // 타이틀 보내기
                    player.playSound(player, Sound.BLOCK_WOODEN_BUTTON_CLICK_ON,  1f, 1f);
                    if( finalI ==( message.length() -1)) {
                        player.playSound(player, Sound.BLOCK_GLASS_BREAK,  2f, 1.2f);
                    }

            }, 5L + 3L + (3L * i)); // 0틱부터 시작하여 0.5초(10 틱)마다 실행
        }
        for(int i = 0; i < message.length() + 1; i +=1){


            int finalI = i;
            Bukkit.getScheduler().scheduleSyncDelayedTask(PICOSERVER.getInstance(),() -> {
                String title = message.substring(0, message.length() -  finalI);
                player.sendTitle( "§x§a§b§2§0§2§0§l§o"+title,"", 0, 20, 10); // 타이틀 보내기
                player.playSound(player, Sound.BLOCK_WOODEN_BUTTON_CLICK_ON,  1f, 1f);
                if( finalI ==( message.length() -1)) {
                    player.playSound(player, Sound.BLOCK_GLASS_BREAK,  2f, 1.2f);
                }

            }, 40L + 5L + 3L + (2L * i)); // 0틱부터 시작하여 0.5초(10 틱)마다 실행
        }
    }
    @EventHandler
    public void onMoveMap(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if(!event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) return;
        if(!event.getPlayer().getWorld().getName().equals("plotworld")) return;
        Block clickedBlock = event.getClickedBlock();
        if(clickedBlock == null || !clickedBlock.getType().equals(Material.ENCHANTING_TABLE)) return;

        event.setCancelled(true);

        Location location = BukkitUtil.adapt(clickedBlock.getLocation());
        PlotArea plotArea = location.getPlotArea();
        if (plotArea == null) return;
        Plot plot = plotArea.getPlot(location);
        if (plot == null) return;
        if (plot.isAdded(player.getUniqueId())) {
            boolean isOp = player.isOp();
            try{
                player.setOp(true);
                PICOSERVER.dispatchCommand(player, "picoserver:상점 건축상점");
            } finally {
                player.setOp(isOp);
            }
        }
    }
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (!event.getPlayer().isOp() && event.getClickedBlock() != null && event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if(event.getPlayer().getWorld().getName().equals("plotworld")) {
                return;
            }
            Block clickedBlock = event.getClickedBlock();
            if (event.getPlayer().getWorld().getName().equals("spawn") && (clickedBlock.getType().name().endsWith("DOOR"))) {
                event.setCancelled(true);
                return;
            }
            if (clickedBlock.getType().name().endsWith("_TRAPDOOR") ||
                    clickedBlock.getType().name().endsWith("_FENCE_GATE") ||
                    clickedBlock.getType() == Material.SPRUCE_DOOR ||
                    clickedBlock.getType() == Material.FURNACE ||
                    clickedBlock.getType() == Material.ANVIL ||
                    clickedBlock.getType() == Material.CRAFTING_TABLE ||
                    clickedBlock.getType() == Material.SMOKER ||
                    clickedBlock.getType() == Material.BARREL ||
                    clickedBlock.getType() == Material.LOOM ||
                    clickedBlock.getType() == Material.SMITHING_TABLE ||
                    clickedBlock.getType() == Material.BREWING_STAND ||
//                    clickedBlock.getType() == Material.LEVER ||
                    clickedBlock.getType() == Material.STONECUTTER ||
                    clickedBlock.getType() == Material.BIRCH_WALL_SIGN||
                    clickedBlock.getType() == Material.CRIMSON_WALL_HANGING_SIGN ||
                    clickedBlock.getType() == Material.GRINDSTONE ||
                    clickedBlock.getType() == Material.CHISELED_BOOKSHELF ||
                    clickedBlock.getType() == Material.BELL
            ) {
                // 특정 블록을 우클릭한 경우 블록 변경을 취소
                event.setCancelled(true);
                // 추가로 원하는 작업을 수행할 수 있습니다.
            }
            if(event.getPlayer().getWorld().getName().equals("pvp")) {
                if ((
                        clickedBlock.getType() == Material.BEACON
                )){
                    event.setCancelled(true);
                }
            }
        }
    }
    @EventHandler
    public void pI(PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if(event.getItem() != null) {
                if(event.getItem().getItemMeta() != null ) {
                    if(event.getItem().getItemMeta().hasDisplayName()) {
                        if(ChatColor.stripColor(event.getItem().getItemMeta().getDisplayName()).trim().startsWith("[ 골드 주머니 ]")) {
                            event.setCancelled(true);
                            event.getItem().setAmount( event.getItem().getAmount() - 1);
                            event.getPlayer().playSound(event.getPlayer(), Sound.BLOCK_SLIME_BLOCK_BREAK, 1, 1f);
                            Integer amount = generateRandomNumber(50, 700) * 100;
                            event.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', " &f ➡ &7[a] &6G&f".replace("[a]", numberWithComma(amount))));
                            ArrayList<Object> q= new ArrayList<>();
                            q.add(amount);
                            q.add(event.getPlayer().getUniqueId().toString());
//                                Database.getInstance().execute("UPDATE money SET balance = balance + ? WHERE uuid = ?", q);
                            Money.add(event.getPlayer(), amount);

                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onNametagEvent(PlayerLoginEvent event) {
        updateNametag(event.getPlayer());
    }
//    @EventHandler
//    public void onNametagEvent(NametagEvent event) {
//        if (event.getChangeReason() == NametagEvent.ChangeReason.PLUGIN) {
//            if (event.getChangeType() == NametagEvent.ChangeType.PREFIX) {
//                Player player = Bukkit.getPlayerExact(event.getPlayer());
////                player.sendMessage();
////                NametagEdit.getApi().setNametag(player, );
//                player.sendMessage("The value was: " + event.getValue());
//            }
//        }
//    }

//    @EventHandler(priority = EventPriority.LOWEST)
//    public void onInvaildCommand(PlayerCommandPreprocessEvent event) {
//        if (!event.isCancelled()) {
//            Player player = event.getPlayer();
//            String command = event.getMessage().split(" ")[0];
//            HelpTopic topic = Bukkit.getServer().getHelpMap().getHelpTopic(command);
//            if (topic == null) {
//                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6 ● &f해당 명령어는 없거나 사용이 제한되어 있습니다"));
//                event.setCancelled(true);
//            }
//        }
//    }

}
