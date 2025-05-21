package kr.rth.picoserver.etc;

import com.plotsquared.core.PlotAPI;
import com.plotsquared.core.player.PlotPlayer;
import de.oliver.fancyholograms.api.FancyHologramsPlugin;
import de.oliver.fancyholograms.api.Hologram;
import de.oliver.fancyholograms.api.HologramData;
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
import java.util.*;

import static java.lang.Integer.parseInt;
import static kr.rth.picoserver.util.getItemStack.getItemStack;
import static kr.rth.picoserver.util.itemStackSerializer.itemStackDeSerializer;
import static kr.rth.picoserver.util.itemStackSerializer.itemStackSerializer;
import static kr.rth.picoserver.util.numberWithComma.numberWithComma;
import static kr.rth.picoserver.util.transText.transText;

public class itemmelt implements CommandExecutor, Listener {
    static itemmelt instance;

    private final PlotAPI api = new PlotAPI();

    public HashMap<ItemStack, Integer> dataMap = new HashMap();

    public void reload() {
        dataMap.clear();
        try {
            for( var i : Database.getInstance().execute("SELECT * FROM itemmelt", null) ) {
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
                    if( 1 > (Integer) new ArrayList<>(Database.getInstance().execute("SELECT EXISTS(SELECT * FROM itemmelt WHERE itemstack = ?)", q).get(0).values()).get(0)) {
                        q.add(parseInt(args[1]));
                        Database.getInstance().execute("INSERT INTO itemmelt VALUES(?, ?)", q);
                    }else {
                        q.add(0, parseInt(args[1]));
                        Database.getInstance().execute("UPDATE itemmelt SET price = ? WHERE itemstack = ?", q);
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
                    Database.getInstance().execute("UPDATE FROM itemmelt WHERE itemstack = ?", q);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
            reload();
            p.sendMessage("Done");
            return false;
        }

        UUID uuid = p.getUniqueId();
        PlotPlayer<?> plotPlayer = api.wrapPlayer(uuid);
        if (plotPlayer == null || plotPlayer.getPlots().isEmpty()) {
            p.sendMessage(transText("&f \uE138 &f당신은 보유 중인 농장이 없어 명성을 쌓을 수 없습니다"));
            return true;
        }

        Inventory inv = Bukkit.createInventory(null, 54, transText("&o           아이템 분해"));
        ItemStack glassPane = getItemStack(Material.GRAY_STAINED_GLASS_PANE, transText("&f"), transText("&f"));
        for (int i = 0; i < 9; i++) {
            inv.setItem(i, glassPane);
            inv.setItem(53 - i, glassPane);
        }

        p.openInventory(inv);
        p.playSound(p, Sound.BLOCK_ENCHANTMENT_TABLE_USE, 2f, 2f);

        return false;
    }
    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if(!ChatColor.stripColor(e.getView().getTitle()).trim().equals("아이템 분해")) return;
        if(e.getClickedInventory() == null) return;
        if(e.getClickedInventory().getType() == InventoryType.PLAYER) {
            ItemStack clickedItem = e.getCurrentItem();
            if (clickedItem != null) {
                ItemStack clonedItem = clickedItem.asOne();
                if (!dataMap.containsKey(clonedItem)) {
                    e.setCancelled(true);
                }
            }
        }
        if((e.getRawSlot() > -1 && e.getRawSlot() < 9) || (e.getRawSlot() > 44 && e.getRawSlot() < 54)) e.setCancelled(true);
    }

    @EventHandler
    public void onClose(InventoryCloseEvent e ) {
        if(!ChatColor.stripColor(e.getView().getTitle()).trim().equals("아이템 분해")) return;
        Inventory closedInv = e.getInventory();
        int pricing = 0;

        for( int i = 9; i < 45; i ++){
            ItemStack item = closedInv.getItem(i);
            if(item == null) continue;
            ItemStack tempItem1 = item.asOne();
            if(!dataMap.containsKey(tempItem1)) continue;
            pricing += dataMap.get(tempItem1) * item.getAmount();
        }

        if (pricing != 0) {
            Player player = (Player) e.getPlayer();

            ArrayList<Object> q = new ArrayList<>();
            q.add(player.getUniqueId().toString());
            q.add(pricing);
            q.add(pricing);
            try {
                Database.getInstance().execute("INSERT INTO plot_fame (uuid, rate) VALUES(?,?) ON DUPLICATE KEY UPDATE rate = rate + ?", q);
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
            e.getPlayer().sendMessage(transText("\n§x§8§f§f§f§b§6 ● §f아이템 분해가 완료 되었습니다 §x§8§f§f§f§b§6>§x§8§f§f§f§b§6>§x§8§f§f§f§b§6> §f§o+[p]\n&7"
                    .replace("[p]", numberWithComma(pricing))));
            player.playSound(player, Sound.BLOCK_LAVA_EXTINGUISH, 1f , 1f);
        }
    }

    private itemmelt() {
        reload();
        Bukkit.getServer().getScheduler().runTaskTimerAsynchronously(PICOSERVER.getInstance(), () -> {
            ArrayList<Map<String, Object>> fameRates;
            try {
                fameRates = Database.getInstance().execute("SELECT uuid, SUM(rate) as total_rate FROM plot_fame GROUP BY uuid ORDER BY total_rate DESC LIMIT 20;", null);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

            ArrayList<String> q = new ArrayList<>();
            q.add("");
            q.add("");
            q.add("");
            Integer idx = 1;
            for (Map<String, Object> entry : fameRates) {
                if(idx == 11) {
                    break;
                }
                String colorCode = "<#9ADC6B>";
                if(idx == 1) {
                    colorCode = "<#DC3223>";
                }
                if(idx == 2) {
                    colorCode = "<#FF940A>";
                }
                if(idx == 3) {
                    colorCode = "<#DCC221>";
                }

                q.add("<reset>{cc}<i>#<b>{n}<reset><white><i><b> {n2} <gray>● <#d2ee45><i><b>{a} 점<reset>"
                        .replace("{cc}", colorCode)
                        .replace("{n}", idx.toString())
                        .replace("{n2}", Bukkit.getOfflinePlayer(UUID.fromString((String) entry.get("uuid"))).getName() )
                        .replace("{a}", numberWithComma( Integer.parseInt(String.valueOf( entry.get("total_rate"))) ) )
                );
                idx += 1;
            }

            q.add("");
            Optional<Hologram> holo = FancyHologramsPlugin.get().getHologramManager().getHologram("tja1");
            if (holo.isEmpty()) return;
            Hologram hologram = holo.get();
            HologramData holoData = hologram.getData();
            holoData.setText(q);
            hologram.refreshHologram(Bukkit.getOnlinePlayers());

        }, 0, 20 * 60 * 30);
    }

    public static itemmelt getInstance() {
        if( instance == null) {
            instance = new itemmelt();
        }
        return instance;
    }
}
