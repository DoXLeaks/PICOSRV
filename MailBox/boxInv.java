package kr.rth.picoserver.MailBox;

import com.google.gson.Gson;
import kr.rth.picoserver.Database;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import static kr.rth.picoserver.util.itemStackSerializer.itemStackSerializer;
import static kr.rth.picoserver.util.transText.transText;

public class boxInv implements InventoryHolder {
    public String owner;
    public Inventory inv;
    @Override
    public Inventory getInventory() {
        return inv;
    }
    public void save(){
        HashMap<Integer, String > a = new HashMap<>();
        for(int i = 0;i <  inv.getSize(); i ++) {
            ItemStack stack = inv.getItem(i);
            if(stack != null){
                a.put(i, itemStackSerializer(stack));
            }
        }
        Gson gson = new Gson();
        ArrayList<Object> q=  new ArrayList<>();
        q.add(gson.toJson(a));
        q.add(owner);
        try {
            Database.getInstance().execute("UPDATE mailBox SET invContent = ? WHERE uuid = ?", q);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }
    public boxInv(String owner) {
        this.owner = owner;
        inv = Bukkit.createInventory(this, 54, transText(":offset_-48::apdlf:"));
    }
}
