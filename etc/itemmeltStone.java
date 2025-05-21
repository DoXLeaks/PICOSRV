package kr.rth.picoserver.etc;

import kr.rth.picoserver.Database;
import kr.rth.picoserver.PICOSERVER;
import kr.rth.picoserver.itemUpgrade.seemlessStorage;
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
import static kr.rth.picoserver.util.itemStackSerializer.itemStackDeSerializer;
import static kr.rth.picoserver.util.itemStackSerializer.itemStackSerializer;
import static kr.rth.picoserver.util.numberWithComma.numberWithComma;
import static kr.rth.picoserver.util.transText.transText;

public class itemmeltStone implements CommandExecutor, Listener {
    static itemmeltStone instance;
    static ArrayList<Player> Introing = new ArrayList<>();

    public HashMap<ItemStack, Integer> dataMap = new HashMap();

    public void reload() {
        dataMap.clear();
        try {
            for( var i : Database.getInstance().execute("SELECT * FROM itemmeltstone", null) ) {
                ItemStack itemstack = itemStackDeSerializer((String) i.get("itemstack"));
                dataMap.put(itemstack, (Integer) i.get("price"));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        Player p = (Player) sender;
        if(sender.isOp() && args.length > 1) {
            if(args[0].equals("설정") && args.length == 2){
                ItemStack stack = p.getItemInHand();
                stack.setAmount(1);
                ArrayList<Object> q = new ArrayList<>();
                q.add(itemStackSerializer(stack));

                try {
                    if( 1 > (Integer) new ArrayList<>(Database.getInstance().execute("SELECT EXISTS(SELECT * FROM itemmeltstone WHERE itemstack = ?)", q).get(0).values()).get(0)) {
                        q.add(parseInt(args[1]));
                        Database.getInstance().execute("INSERT INTO itemmeltstone VALUES(?, ?)", q);
                    }else {
                        q.add(0, parseInt(args[1]));
                        Database.getInstance().execute("UPDATE itemmeltstone SET price = ? WHERE itemstack = ?", q);
                    }
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
            if(args[0].equals("삭제")) {
                ItemStack stack = p.getItemInHand();
                stack.setAmount(1);
                ArrayList<Object> q = new ArrayList<>();
                q.add(itemStackSerializer(stack));

                try {
                    Database.getInstance().execute("UPDATE FROM itemmeltstone WHERE itemstack = ?", q);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
            reload();
            p.sendMessage("Done");
            return false;
        }

        Inventory inv = Bukkit.createInventory( null, InventoryType.DROPPER, transText("&o:qnsgo:"));
        ItemStack glassPane = getItemStack(Material.GRAY_STAINED_GLASS_PANE, transText("&f"), transText("&f"));
        var slotio = new int[]{0, 1, 3, 2, 4, 6, 5,  7, 8};
        Introing.add(p);
        for( int i  = 0; i < 9; i ++) {
            int finalI = i;
            Bukkit.getScheduler().scheduleSyncDelayedTask(PICOSERVER.getInstance(), () -> {
                p.playSound(p, Sound.UI_BUTTON_CLICK, 0.5f, 2f);
                inv.setItem(slotio[finalI], glassPane);
            }, (long) i);
            Bukkit.getScheduler().scheduleSyncDelayedTask(PICOSERVER.getInstance(), () -> {
                p.playSound(p, Sound.UI_BUTTON_CLICK, 0.5f, 2f);
                inv.setItem(slotio[finalI], null);
                if(finalI == 8) {
                    Introing.remove(p);
                }
            },  10L+ (long) i);
        }


        p.openInventory(inv);
        p.playSound(p, Sound.BLOCK_ENCHANTMENT_TABLE_USE, 2f, 0.5f);



        return false;
    }
    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if(!ChatColor.stripColor(e.getView().getTitle()).trim().equals(":qnsgo:")) {
            return;
        }
        if(e.getClickedInventory() == null) {
            return;
        }
        if(e.getClickedInventory().equals(e.getWhoClicked().getInventory())){
            return;
        }
        if(Introing.contains( (Player) e.getWhoClicked())){
            e.setCancelled(true);
        }



    }
    @EventHandler
    public void onClose(InventoryCloseEvent e ) {
        if(!ChatColor.stripColor(e.getView().getTitle()).trim().equals(":qnsgo:")) {
            return;
        }
        Inventory closedInv = e.getInventory();

        Integer pricing = 0;

        for( int i = 0; i < 9; i ++){
            ItemStack item = closedInv.getItem(i);
            if(item == null) continue;
            ItemStack tempItem1 = item.clone();
            tempItem1.setAmount(1);
            if(!dataMap.containsKey(tempItem1)) continue;
            pricing += dataMap.get(tempItem1) * item.getAmount();
        }
        if(pricing!= 0){

        ItemStack stack1 = seemlessStorage.getInstance().normalCoin.clone();
        stack1.setAmount(pricing);
        Player p = (Player) e.getPlayer();
        p.getInventory().addItem(stack1);
        p.playSound(p, Sound.BLOCK_LAVA_EXTINGUISH, 1f , 1f);

                        e.getPlayer().sendMessage(transText("&f \uE153 &f아이템 분해로 &x&B&2&A&4&C&8특별한 강화석 [p]개&f 를 지급 받았습니다"
                    .replace("[p]", numberWithComma(pricing))));
        }
//
//
//       try {
//            Database.getInstance().execute("INSERT INTO islandworth (id, worth) VALUES(?,?) ON DUPLICATE KEY UPDATE worth = worth + ?", q);
//        } catch (SQLException ex) {
//            throw new RuntimeException(ex);
//        }
//
//        if( pricing != 0) {
//


//        }

    }

    private itemmeltStone() {
        reload();
    }

    public static itemmeltStone getInstance() {
        if( instance == null) {
            instance = new itemmeltStone();
        }
        return instance;
    }
}
