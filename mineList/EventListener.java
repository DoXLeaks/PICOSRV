package kr.rth.picoserver.mineList;

import com.google.gson.Gson;
import com.vexsoftware.votifier.model.VotifierEvent;
import kr.rth.picoserver.Cash.Cash;
import kr.rth.picoserver.Database;
import kr.rth.picoserver.MailBox.boxInv;
import kr.rth.picoserver.MailBox.mailboxes;
import kr.rth.picoserver.Money.Money;
import kr.rth.picoserver.PICOSERVER;
import kr.rth.picoserver.credit.Credit;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import static kr.rth.picoserver.util.itemStackSerializer.itemStackDeSerializer;
import static kr.rth.picoserver.util.itemStackSerializer.itemStackSerializer;
import static kr.rth.picoserver.util.transText.transText;

public class EventListener implements Listener {
    @EventHandler
    public void onVote(VotifierEvent e) {
        OfflinePlayer p = Bukkit.getOfflinePlayerIfCached(e.getVote().getUsername());
        if(p == null) {
            return;
        }
        Inventory inv = mailboxes.getInstance().getMailbox(p.getUniqueId().toString());
        boxInv boxinv = (boxInv) inv.getHolder();

        HashMap<String, String> invContent=new HashMap<>();
        Gson gson = new Gson();
        try {
            invContent = gson.fromJson(
                    (String) Database.getInstance().execute("SELECT * FROM keyv WHERE name = 'minelistInv'", null).get(0).get("data"),
                    invContent.getClass()
            );
        } catch (SQLException ae) {
            throw new RuntimeException(ae);
        }
        for( String i : invContent.keySet() ) {
            inv.addItem(itemStackDeSerializer(invContent.get(i)));
        }
        boxinv.save();
        Money.add(p, 300000);

        UUID uuid = p.getUniqueId();

        //오프라인/온라인 둘 다 사용
        dispatchConsoleCommand("skript:picovotesk [p]", e.getVote().getUsername());
        if (p.isOnline()) {
            //온라인만 사용
            //dispatchConsoleCommand("skript:온라인테스트 [p]", e.getVote().getUsername());
            Cash.add(p, 500);
            Credit.add(p, 500);
        } else {
            //오프라인만 사용
            //dispatchConsoleCommand("skript:오프라인테스트 [p]", e.getVote().getUsername());
            try {
                ArrayList<Object> q = new ArrayList<>();
                q.add(uuid.toString());
                if( !(((Integer) new ArrayList<>(Database.getInstance().execute("SELECT EXISTS(SELECT * FROM cash WHERE uuid=?)", q).get(0).values()).get(0)) > 0)) {
                    q.clear();
                    q.add(uuid.toString());
                    q.add(500);
                    Database.getInstance().execute("INSERT INTO cash VALUES(?,?)", q);
                } else {
                    Cash.add(p, 500);
                }
                q.clear();
                q.add(uuid.toString());
                if( !(((Integer) new ArrayList<>(Database.getInstance().execute("SELECT EXISTS(SELECT * FROM credit WHERE uuid=?)", q).get(0).values()).get(0)) > 0)) {
                    q.clear();
                    q.add(uuid.toString());
                    q.add(500);
                    Database.getInstance().execute("INSERT INTO credit VALUES(?,?)", q);
                } else {
                    Credit.add(p, 500);
                }

            } catch (SQLException sqlException) {
                PICOSERVER.getInstance().getLogger().warning("추천 보상 지급 중 문제가 발생했습니다, name : [name] / UUID : [uuid]"
                        .replace("[name]", e.getVote().getUsername())
                        .replace("[uuid]", p.getUniqueId().toString()));
                throw new RuntimeException(sqlException);
            }
        }

        if(p.isOnline()){
            p.getPlayer().sendTitle(ChatColor.translateAlternateColorCodes('&', "§x§9§e§f§2§0§2§o§lM§x§a§4§f§4§1§f§o§lI§x§a§b§f§5§3§c§o§lN§x§b§1§f§7§5§9§o§lE§x§b§7§f§8§7§7§o§lL§x§b§d§f§a§9§4§o§lI§x§c§4§f§b§b§1§o§lS§x§c§a§f§d§c§e§o§lT"), ChatColor.translateAlternateColorCodes('&', "&f마인리스트 보상이 우편함으로 지급 되었습니다"), 20, 100, 20);
            p.getPlayer().playSound(p.getPlayer(), Sound.UI_TOAST_CHALLENGE_COMPLETE,  1, 1f);
            TextComponent compo = new TextComponent(TextComponent.fromLegacyText(transText("§x§D§0§E§4§C§9 <§n추천하기 클릭§x§D§0§E§4§C§9>\n&f")));
            compo.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://minelist.kr/servers/picosv.kr"));

            Bukkit.spigot().broadcast(
                    new TextComponent(new TextComponent(TextComponent.fromLegacyText(transText("&f&f\n&f \uE209 &f마인리스트 §x§D§0§E§4§C§9[p]&f 님의 추천이 등록 되었습니다".replace("[p]", p.getName())))), compo)
            );
        }
    }

    @EventHandler
    public void onClose(InventoryCloseEvent e) {
        if(!e.getView().getTitle().startsWith("마리보상설정")){
            return;
        }
        HashMap<Integer, String> ctc = new HashMap<>();
        Gson gson = new Gson();
        Inventory closedInv = e.getInventory();
        for( int i = 0; i < e.getInventory().getSize(); i ++) {
            ItemStack stack = closedInv.getItem(i);
            if(stack != null ) {
                ctc.put(i, itemStackSerializer(closedInv.getItem(i)));
            }
        }
        ArrayList<Object> q = new ArrayList<>();
        q.add(gson.toJson(ctc));

        try {
            Database.getInstance().execute("UPDATE keyv SET data = ? WHERE name = 'minelistInv'", q);
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }

    }

    private void dispatchConsoleCommand(String command, String playerName) {
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.replace("[p]", playerName));
    }
}
