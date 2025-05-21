package kr.rth.picoserver.itemUpgrade;

import kr.rth.picoserver.Database;
import org.bukkit.inventory.ItemStack;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static kr.rth.picoserver.util.itemStackSerializer.itemStackDeSerializer;

public class seemlessStorage {
    static  seemlessStorage instance;
    HashMap<ItemStack, HashMap<String, Object>> storage = new HashMap<>();
    public  ItemStack normalCoin;
    public  ItemStack specialCoin;

    private  seemlessStorage() {
        reload();

    }
    public void reload() {
        ArrayList<Map<String, Object>> dbres = null;
        try {
            dbres = Database.getInstance().execute("SELECT * FROM itemUpgrade", null);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        for(var i : dbres){
            if(i.get("targetItem") != null){
                storage.put(itemStackDeSerializer((String) i.get("targetItem")), (HashMap<String, Object>) i);
            }
        }
        try {
            normalCoin = itemStackDeSerializer((String) Database.getInstance().execute("SELECT * FROM keyv WHERE name = 'normalUpgradeCoin'", null).get(0).get("data"));
        } catch (SQLException e) {
//            throw new RuntimeException(e);
        }
        try {
            specialCoin = itemStackDeSerializer((String) Database.getInstance().execute("SELECT * FROM keyv WHERE name = 'specialUpgradeCoin'", null).get(0).get("data"));
        } catch (SQLException e) {
//            throw new RuntimeException(e);
        }
    }
    public HashMap<String, Object> get(ItemStack stack) {
        return storage.get(stack);

    }
    public static seemlessStorage getInstance() {
        if(instance == null){
            instance = new seemlessStorage();
        }
        return instance;
    }
 }
