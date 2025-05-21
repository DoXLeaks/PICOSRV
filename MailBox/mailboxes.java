package kr.rth.picoserver.MailBox;

import com.google.gson.Gson;
import kr.rth.picoserver.Database;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import static java.lang.Integer.parseInt;
import static kr.rth.picoserver.util.itemStackSerializer.itemStackDeSerializer;

public class mailboxes {
    static mailboxes instance;
    static HashMap<String, InventoryHolder> datas = new HashMap<>();
    private mailboxes() {

    }

    public Inventory getMailbox(String owner) {
        if(datas.containsKey(owner)){
            return datas.get(owner).getInventory();
        }
        boxInv invH = new boxInv(owner);
        Inventory inv = invH.getInventory();
        ArrayList<Object> q= new ArrayList<>();
        q.add(owner);
        try {
            if( 1 > (Integer) new ArrayList<>(Database.getInstance().execute("SELECT EXISTS(SELECT * FROM mailBox WHERE uuid = ?)", q).get(0).values()).get(0)) {
                Database.getInstance().execute("INSERT INTO mailBox VALUES(?,'{}')", q);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        Object dbRes1 = null;
        try {
            dbRes1 = Database.getInstance().execute("SELECT * FROM mailBox WHERE uuid = ?", q).get(0).get("invContent");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        Gson gson = new Gson();
        HashMap<String, String> a = new HashMap<>();
        a = gson.fromJson((String) dbRes1, a.getClass());
        for( String i : a.keySet() ) {
            inv.setItem(parseInt(i), itemStackDeSerializer(a.get(i)));
        }
        datas.put(owner, invH);
        return inv;

    }

    public static mailboxes getInstance() {
        if( instance == null){
            instance = new mailboxes();
        }
        return instance;
    }
}
