package kr.rth.picoserver.etc;

import kr.rth.picoserver.Database;
import kr.rth.picoserver.PICOSERVER;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import static java.lang.Integer.parseInt;
import static kr.rth.picoserver.util.getItemStack.getItemStack;
import static kr.rth.picoserver.util.random.generateRandomNumber;
import static kr.rth.picoserver.util.transText.transText;

public class moleCatch implements CommandExecutor, Listener {
    static moleCatch instance;
    static ArrayList<Player> invFreeze = new ArrayList<>();
    static ArrayList<Player> isPlaying = new ArrayList<>();
    static HashMap<Player, Integer> playingData = new HashMap<>();
    static HashMap<Player, ItemStack> chipMap = new HashMap<>();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        Player p = (Player) sender;

        if(p.getItemInHand() ==null) {
            p.sendMessage(transText("§f \uE13A &f피코칩을 손에 들고 다시 시도 해주세요"));
            return false;
        }
        if(p.getItemInHand().getType().equals(Material.AIR)) {
            p.sendMessage(transText("§f \uE13A &f피코칩을 손에 들고 다시 시도 해주세요"));
            return false;

        }
        if(p.getItemInHand().getItemMeta().getDisplayName().trim().equals("")) {
            p.sendMessage(transText("§f \uE13A &f피코칩을 손에 들고 다시 시도 해주세요"));
            return false;

        }

        if(!ChatColor.stripColor(p.getItemInHand().getItemMeta().getDisplayName()).equals("\uE142 피코칩")) {
            p.sendMessage(transText("§f \uE13A &f피코칩을 손에 들고 다시 시도 해주세요"));
            return false;
        }
        ItemStack chip = p.getPlayer().getItemInHand().clone();
        p.getItemInHand().setAmount(p.getItemInHand().getAmount() -1);
        ArrayList<Object> q1=  new ArrayList<>();
        q1.add(p.getUniqueId().toString());
        try {
            Database.getInstance().execute("INSERT INTO molecatchstat (`by`, amount) VALUES(?, -1)", q1);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }


        Inventory inv = Bukkit.createInventory( null, InventoryType.DROPPER, transText("&f두더지 잡기"));
        ItemStack glassPane = getItemStack(Material.BROWN_STAINED_GLASS_PANE, transText("&f"), transText("&f"));
        var slotio = new int[]{0, 1, 3, 2, 4, 6, 5,  7, 8};
        invFreeze.add(p);
        isPlaying.add(p);
        playingData.put(p, generateRandomNumber(0, 8));
        for( int i  = 0; i < 9; i ++) {
            int finalI = i;
            Bukkit.getScheduler().scheduleSyncDelayedTask(PICOSERVER.getInstance(), () -> {
//                p.playSound(p, Sound.UI_BUTTON_CLICK, 0.5f, 2f);
                p.playSound(p, Sound.BLOCK_ROOTED_DIRT_BREAK, 1f, 01f);
                inv.setItem(slotio[finalI], glassPane);

                if (finalI == 8) {
                    invFreeze.remove(p);

                }

            }, (long) i);
        }


        p.openInventory(inv);
        p.playSound(p, Sound.BLOCK_ROOTED_DIRT_BREAK, 1f, 0.5f);

        chip.setAmount(1);
        chipMap.put(p, chip);

        return false;
    }
    public  void resetGame(Player p ) {
        invFreeze.remove(p);
        isPlaying.remove(p);
        playingData.remove(p);
    }
    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if(!ChatColor.stripColor(e.getView().getTitle()).trim().equals("두더지 잡기")) {
            return;
        }
        if(e.getClickedInventory() == null) {
            return;
        }
        if(e.getClickedInventory().equals(e.getWhoClicked().getInventory())){
            return;
        }
        Player p = (Player) e.getWhoClicked();
        if( invFreeze.contains(p) ) {
            e.setCancelled(true);
            return;
        }

        if(! isPlaying.contains(p) ) {
            return;
        }
        Inventory inv = e.getInventory();


        invFreeze.add(p);
        isPlaying.remove(p);

        e.setCancelled(true);

        if(e.getSlot() == playingData.get(p)) {
            ItemStack stack = chipMap.get(p).clone();
            stack.setAmount(5);
            p.getInventory().addItem(stack);
            p.playSound(p, Sound.BLOCK_NOTE_BLOCK_FLUTE, 2f, 1.5f);

            Bukkit.broadcastMessage("§1");
            Bukkit.broadcastMessage(transText(
                    "§f \uE146 §f두더지 도박으로 §x§C§5§9§5§9§5[p] &f님께서 §x§C§5§9§5§9§5[n]배&f 당첨 되었습니다"
                            .replace("[n]", Integer.toString( 5 ))
                            .replace("[p]", p.getName())
            ));
            Bukkit.broadcastMessage("§1");

            ArrayList<Object> q1=  new ArrayList<>();
            q1.add(p.getUniqueId().toString());
            try {
                Database.getInstance().execute("INSERT INTO molecatchstat (`by`, amount) VALUES(?, 5)", q1);
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }


        } else {
            p.playSound(p, Sound.BLOCK_NOTE_BLOCK_FLUTE, 2f, 0.25f);
        }
        for( int i  = 0; i < 9; i ++) {
            int finalI = i;
            Bukkit.getScheduler().scheduleSyncDelayedTask(PICOSERVER.getInstance(), () -> {

                if( finalI == playingData.get(p) ){
                    p.playSound(p, Sound.BLOCK_ROOTED_DIRT_BREAK, 1f, 01f);
                    inv.setItem(finalI, getItemStack(Material.LEATHER, "두더지", ""));


                }else{
                    p.playSound(p, Sound.BLOCK_ROOTED_DIRT_BREAK, 1f, 01f);
                    if( finalI == e.getSlot() ) {
                        inv.setItem(finalI, getItemStack(Material.GREEN_STAINED_GLASS_PANE, "클릭한 곳", ""));
                    }else {
                    inv.setItem(finalI, null);

                    }
                }

            }, (long) i);
        }


    }
    @EventHandler
    public void onClose(InventoryCloseEvent e ) {
        if(!ChatColor.stripColor(e.getView().getTitle()).trim().equals("두더지 잡기")) {
            return;
        }
        Player p = (Player) e.getPlayer();

        if(isPlaying.contains(p) ){
            ItemStack stack = chipMap.get(p).clone();
            p.getInventory().addItem(stack);
        }
        resetGame(p);

    }

    private moleCatch() {

    }

    public static moleCatch getInstance() {
        if( instance == null) {
            instance = new moleCatch();
        }
        return instance;
    }


}
