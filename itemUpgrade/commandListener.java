package kr.rth.picoserver.itemUpgrade;

import kr.rth.picoserver.Database;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.sql.SQLException;
import java.util.ArrayList;

import static kr.rth.picoserver.util.getItemStack.getItemStack;
import static kr.rth.picoserver.util.getItemStack.getItemStackWithCustomModelData;
import static kr.rth.picoserver.util.itemStackSerializer.itemStackSerializer;
import static kr.rth.picoserver.util.transText.transText;

public class commandListener implements CommandExecutor {



    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {

        Player p = (Player) sender;
        if(args.length == 0) {
            Inventory inv = Bukkit.createInventory(p, InventoryType.BREWING, transText(":offset_5::rkdghk:"));

            ItemStack book = new ItemStack(Material.KNOWLEDGE_BOOK);

            ItemStack anvil = new ItemStack(Material.ANVIL);
    //        ItemStack  = new ItemStack(Material.ANVIL);

            // 0 -> 아래 왼쪽
            // 1 -> 아래 중간
            // 2 -> 아래 오른
            // 3 -> 윗쪽 중간
            // 4 -> 윗쪽 왼쪽
            inv.setItem(4, getItemStack(Material.KNOWLEDGE_BOOK, " &x&F&C&E&7&A&D강화 도움말", "&f\n" +
                    "&f 서버에 강화석 종류는 두가지며 아래와 같습니다 &f\n" +
                    "&f\n" +
                    "&f &x&F&C&E&7&A&D&l&o1. &f특별한 강화석 &7(파괴가 방지되지 않음)\n" +
                    "&f &x&F&C&E&7&A&D&l&o2. &f영혼이 담긴 강화석 &7(파괴가 방지됨)\n" +
                    "&f\n" +
                    "&f 아이템을 중첩해서 올리면 강화가 불가능합니다 &7(1개씩 가능) &f\n" +
                    "&f 강화에 필요한 강화석과 비용은 아이템 마다 상이합니다 &f\n" +
                    "&f\n" ));

            inv.setItem(1, getItemStackWithCustomModelData(Material.PAPER, "&f \uE11B &7강화 시작하기",
                    "&f\n&f &7[ &f왼쪽 강화 할 아이템 &7| &f오른쪽 강화석 &7] &f을 넣어주세요 \n&f 강화에 대한 정보는 아이템을 넣으면 아래에 표기 됩니다 &f\n&f",
                    10048
            ));
            p.openInventory(inv);
            p.playSound(p, Sound.BLOCK_ANVIL_PLACE, 1f, 1f);
        }
        if(args.length == 1) {
            if(args[0].equalsIgnoreCase("reload")) {
                seemlessStorage.getInstance().reload();
                p.sendMessage("reload Done.");
            }
            if(args[0].equalsIgnoreCase("normalcoin")) {
                ArrayList<Object  > q = new ArrayList<>();
                q.add(itemStackSerializer(p.getItemInHand()));
                q.add(itemStackSerializer(p.getItemInHand()));
                try {
                    Database.getInstance().execute("INSERT INTO keyv (name, data) VALUES('normalUpgradeCoin', ?) ON DUPLICATE KEY UPDATE data = ?", q);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                p.sendMessage("done.");
                seemlessStorage.getInstance().reload();

            }
            if(args[0].equalsIgnoreCase("specialcoin")) {
                ArrayList<Object  > q = new ArrayList<>();
                q.add(itemStackSerializer(p.getItemInHand()));
                q.add(itemStackSerializer(p.getItemInHand()));
                try {
                    Database.getInstance().execute("INSERT INTO keyv (name, data) VALUES('specialUpgradeCoin', ?) ON DUPLICATE KEY UPDATE data = ?", q);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                p.sendMessage("done.");
                seemlessStorage.getInstance().reload();
            }
         }
        if(args.length == 2) {
            if(args[0].equals("create")) {
                ArrayList<Object> q=  new ArrayList<>();
                q.add(args[1]);
                try {
                    if( (Integer) new ArrayList<>(Database.getInstance().execute("SELECT EXISTS(SELECT * FROM itemUpgrade WHERE 'id' = ?)", q).get(0).values()).get(0)> 0) {
                        p.sendMessage("the id is already taken");
                        return false;
                    }
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                try {
                    Database.getInstance().execute("INSERT INTO itemUpgrade (id) VALUES(?)", q);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                p.sendMessage("Successfully created {0}".replace("{0}", args[1]));
//                reload()
                seemlessStorage.getInstance().reload();
//                Database.getInstance().execute("SELECT * FROM itemupgrade WHERE id = ?", )
            }
        }
        if(args.length == 3 && args[0].equalsIgnoreCase("modify")) {
            ArrayList<Object> q =  new ArrayList<>();
            q.add(args[1]);
            try {
                if( (Integer) new ArrayList<>(Database.getInstance().execute("SELECT EXISTS(SELECT * FROM itemUpgrade WHERE 'id' = ?)", q).get(0).values()).get(0)> 0) {
                    p.sendMessage("the id is already taken");
                    return false;
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            q.clear();
            if(args[2].equals("winitem")) {
                ItemStack item = p.getItemInHand().clone();
                item.setAmount(1);
                q.add(itemStackSerializer(item));
                q.add(args[1]);
                try {
                    Database.getInstance().execute("UPDATE itemUpgrade SET winItem = ? WHERE id = ?",q );
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                p.sendMessage("Successfully update win item to {0}".replace("{0}", item.getItemMeta().getDisplayName()));
                seemlessStorage.getInstance().reload();

            }
            if(args[2].equals("targetitem")) {
                ItemStack item = p.getItemInHand().clone();
                item.setAmount(1);
                q.add(itemStackSerializer(item));
                q.add(args[1]);
                try {
                    Database.getInstance().execute("UPDATE itemUpgrade SET targetItem = ? WHERE id = ?",q );
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                p.sendMessage("Successfully update target item to {0}".replace("{0}", item.getItemMeta().getDisplayName()));
                seemlessStorage.getInstance().reload();
            }



        }
        if(args.length == 4 && args[0].equalsIgnoreCase("modify")) {
            ArrayList<Object> q =  new ArrayList<>();
            q.add(args[1]);
            try {
                if( (Integer) new ArrayList<>(Database.getInstance().execute("SELECT EXISTS(SELECT * FROM itemUpgrade WHERE 'id' = ?)", q).get(0).values()).get(0)> 0) {
                    p.sendMessage("the id is already taken");
                    return false;
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            q.clear();

            if(args[2].equals("losepercent")) {
                q.add(args[3]);
                q.add(args[1]);
                try {
                    Database.getInstance().execute("UPDATE itemUpgrade SET losePerc = ? WHERE id = ?",q );
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                p.sendMessage("Successfully update losePercent to {0}".replace("{0}", args[3]));
                seemlessStorage.getInstance().reload();
            }
            if(args[2].equals("winpercent")) {
                q.add(args[3]);
                q.add(args[1]);
                try {
                    Database.getInstance().execute("UPDATE itemUpgrade SET winPerc = ? WHERE id = ?",q );
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                p.sendMessage("Successfully update wi2nPercent to {0}".replace("{0}", args[3]));
                seemlessStorage.getInstance().reload();
            }
            if(args[2].equals("breakpercent")) {
                q.add(args[3]);
                q.add(args[1]);
                try {
                    Database.getInstance().execute("UPDATE itemUpgrade SET delPerc = ? WHERE id = ?",q );
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                p.sendMessage("Successfully update breakPercent to {0}".replace("{0}", args[3]));
                seemlessStorage.getInstance().reload();
            }

            if(args[2].equals("requiredcoin")) {
                q.add(args[3]);
                q.add(args[1]);
                try {
                    Database.getInstance().execute("UPDATE itemUpgrade SET coin = ? WHERE id = ?",q );
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                p.sendMessage("Successfully update requiredcoin to {0}".replace("{0}", args[3]));
                seemlessStorage.getInstance().reload();
            }

            if(args[2].equals("price")) {
                q.add(args[3]);
                q.add(args[1]);
                try {
                    Database.getInstance().execute("UPDATE itemUpgrade SET price = ? WHERE id = ?",q );
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                p.sendMessage("Successfully update price to {0}".replace("{0}", args[3]));
                seemlessStorage.getInstance().reload();
            }
        }



        return false;
    }
}