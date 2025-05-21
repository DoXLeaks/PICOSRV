package kr.rth.picoserver.etc;

import kr.rth.picoserver.Database;
import kr.rth.picoserver.PICOSERVER;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.GlowItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.sql.SQLException;
import java.util.*;

import static kr.rth.picoserver.util.transText.transText;

public class slotmachine implements Listener, CommandExecutor {
    static slotmachine slotmachineInstance;
    static Player lockedPlayer = null;
    HashMap<Player, Integer> selectmap = new HashMap<>();
    @EventHandler
    public void onRightClick_ForSet(PlayerInteractAtEntityEvent e) {
        if(!e.getPlayer().isOp()) return;
        if(!selectmap.containsKey(e.getPlayer())) return;
        e.setCancelled(true);
        if(!e.getRightClicked().getType().equals(EntityType.GLOW_ITEM_FRAME )) {

            e.getPlayer().sendMessage("아이템프레임이 아닌 다른엔티티를 클릭하셔서 취소되었습니다.");
            selectmap.remove(e.getPlayer());
            return;
        }
        Player p = e.getPlayer();
        ArrayList<Object> q = new ArrayList<>();
        q.add("slotmachineItemFrame"+ selectmap.get(p));
        q.add(e.getRightClicked().getUniqueId().toString());
        q.add(q.get(1));
        try {
            Database.getInstance().execute("INSERT INTO keyv (name, data) VALUES (?,?) ON DUPLICATE KEY UPDATE data =  ?",q );
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
        selectmap.remove(e.getPlayer());
        p.sendMessage("done.");


    }
    public ItemStack getRandomBlock() {
        // 가능한 블록 목록을 배열로 정의합니다.
        Material[] blockOptions = {
                Material.STONE, // 돌블럭
                Material.IRON_INGOT, // 철블럭
                Material.GOLD_INGOT, // 다이아몬드블럭
                Material.DIAMOND, // 다이아몬드블럭
                Material.EMERALD, // 에메랄드블럭
                Material.NETHER_STAR // 네더의 별
        };

        // 무작위 숫자 생성을 위한 Random 객체를 생성합니다.
        Random random = new Random();

        // 배열에서 무작위 블록을 선택합니다.
        Material randomBlock = blockOptions[random.nextInt(blockOptions.length)];

        return new ItemStack(randomBlock);
    }
    @EventHandler
    public void onRightClick_Gamble(PlayerInteractEvent e) {
        if(!e.getAction().equals(Action.RIGHT_CLICK_BLOCK))return;
        if(!(e.getClickedBlock().getType().equals(Material.DRAGON_HEAD) || e.getClickedBlock().getType().equals(Material.DRAGON_WALL_HEAD))) return;
        Player p = e.getPlayer();

        if(p.getItemInHand() ==null) return;
        if(p.getItemInHand().getType().equals(Material.AIR)) return;
        if(p.getItemInHand().getItemMeta().getDisplayName().trim().equals("")) return;

        if(!ChatColor.stripColor(p.getItemInHand().getItemMeta().getDisplayName()).equals("\uE142 피코칩")) return;

        ArrayList<Map<String, Object>> dbres1 = null;
        try {
            dbres1 = Database.getInstance().execute("SELECT * FROM keyv  WHERE name = 'slotmachineLock' AND data = '1' ", null);
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
        if(dbres1.size() > 0) {
            if(lockedPlayer != p && lockedPlayer != null) {
                p.sendMessage(transText("§f \uE13A §f이미 &e[p]§f 님께서 §e슬롯머신 §f을 사용 중 입니다".replace("[p]", lockedPlayer.getName())));
            }
            return;
        }
        try {
            Database.getInstance().execute("INSERT INTO keyv (name, data) VALUES('slotmachineLock','1') ON DUPLICATE KEY UPDATE data = '1' ", null);
            lockedPlayer = p;
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
        e.setCancelled(true);
        ItemStack chip = e.getPlayer().getItemInHand().clone();
        chip.setAmount(1);
        e.getPlayer().getItemInHand().setAmount(e.getPlayer().getItemInHand().getAmount() -1);
        ArrayList<Object> q1=  new ArrayList<>();
        q1.add(p.getUniqueId().toString());
        try {
            Database.getInstance().execute("INSERT INTO slotmachinestat (`by`, amount) VALUES(?, -1)", q1);
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }

        ArrayList<Map<String, Object>> dbres2 = null;
        try {
            dbres2 = Database.getInstance().execute("SELECT * FROM keyv WHERE name = 'slotmachineItemFrame1' OR name = 'slotmachineItemFrame2' OR name = 'slotmachineItemFrame3'", null);
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
        if(dbres2.size() <3) {
            return;
        }
//        Bukkit.getLogger().info(String.valueOf(dbres2));
        HashMap<String, String> itemFrameMaps= new HashMap<>();
        for(var i : dbres2) {
            itemFrameMaps.put((String) i.get("name"), (String) i.get("data"));
        }
        GlowItemFrame itemFrame1 = (GlowItemFrame) Bukkit.getEntity(UUID.fromString(itemFrameMaps.get("slotmachineItemFrame1")));
        GlowItemFrame itemFrame2 = (GlowItemFrame) Bukkit.getEntity(UUID.fromString(itemFrameMaps.get("slotmachineItemFrame2")));
        GlowItemFrame itemFrame3 = (GlowItemFrame) Bukkit.getEntity(UUID.fromString(itemFrameMaps.get("slotmachineItemFrame3")));

        for (int i  = 0; i <12; i += 1){

            if(i == 11) {
                Bukkit.getScheduler().scheduleAsyncDelayedTask(PICOSERVER.getInstance(), () -> {
                    try {
                        var item1 = getRandomBlock();
                        var item2 = getRandomBlock();
                        var item3 = getRandomBlock();
                        itemFrame1.setItem(item1);
                        itemFrame2.setItem(item2);
                        itemFrame3.setItem(item3);

                        if(item1.getType().equals(item2.getType())  && item2.getType().equals(item3.getType())) {
                            p.getWorld().playSound(itemFrame2.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 1f, 1f);
                            p.getWorld().spawnParticle(Particle.TOTEM, itemFrame2.getLocation(), 500, 2.5, 3, 2.5, 0);
                            HashMap<Material, Integer> hashmap1 = new HashMap<>();
                            hashmap1.put(Material.STONE, 1);
                            hashmap1.put(Material.IRON_INGOT, 5);
                            hashmap1.put(Material.GOLD_INGOT, 10);
                            hashmap1.put(Material.DIAMOND, 15);
                            hashmap1.put(Material.EMERALD, 25);
                            hashmap1.put(Material.NETHER_STAR, 100);
                            Bukkit.broadcastMessage("§f");
                            Bukkit.broadcastMessage(transText("§f \uE146 §f슬롯머신 사용으로 §x§C§5§9§5§9§5[p] §f님께서 §x§C§5§9§5§9§5[n]배&f 에 당첨 되었습니다".replace("[n]", Integer.toString(hashmap1.get(item2.getType())))
                                    .replace("[p]", p.getName())));
                            Bukkit.broadcastMessage("§1");
                            chip.setAmount(hashmap1.get(item2.getType()));
                            p.getInventory().addItem(chip);



                            ArrayList<Object> q2=  new ArrayList<>();
                            q2.add(p.getUniqueId().toString());
                            q2.add(hashmap1.get(item2.getType()));
                            try {
                                Database.getInstance().execute("INSERT INTO slotmachinestat (`by`, amount) VALUES(?, ?)", q2);
                            } catch (SQLException ex) {
                                throw new RuntimeException(ex);
                            }

                        }else{
                            p.getWorld().playSound(itemFrame2.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 1f, 1f);
                            p.getWorld().spawnParticle(Particle.EXPLOSION_LARGE, itemFrame1.getLocation(), 0);
                            p.getWorld().spawnParticle(Particle.EXPLOSION_LARGE, itemFrame2.getLocation(), 0);
                            p.getWorld().spawnParticle(Particle.EXPLOSION_LARGE, itemFrame3.getLocation(), 0);

                        }

                        Database.getInstance().execute("INSERT INTO keyv (name, data) VALUES('slotmachineLock','0') ON DUPLICATE KEY UPDATE data = '0' ", null);
                        lockedPlayer = null;


                    } catch (SQLException ex) {
                        throw new RuntimeException(ex);
                    }
                }, 2 * i);
            }else {
                Bukkit.getScheduler().scheduleAsyncDelayedTask(PICOSERVER.getInstance(), () -> {
                    p.getWorld().playSound(itemFrame2.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 2f, 1.25f);
                    itemFrame1.setItem(getRandomBlock());
                    itemFrame2.setItem(getRandomBlock());
                    itemFrame3.setItem(getRandomBlock());

                }, 2 * i);
            }

        }

    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if(!sender.isOp()) {
            return false;
        }
        if(args[0].equals("set") && args.length == 2) {
            ArrayList<Object> q=  new ArrayList<>();
            Player p = (Player) sender;

            selectmap.put(p, Integer.valueOf(args[1]));
            p.sendMessage("지정하고자 하는 아이템 프레임을 우클릭해주세요");
            return false;
        }



        return false;
    }
    private slotmachine() {
        try {
            Database.getInstance().execute("INSERT INTO keyv (name, data) VALUES('slotmachineLock','0') ON DUPLICATE KEY UPDATE data = '0' ", null);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public static slotmachine getInstance() {
        if(slotmachineInstance == null) {
            slotmachineInstance = new slotmachine();
        }


        return slotmachineInstance;

    }

}
