package kr.rth.picoserver.etc;

import com.google.gson.Gson;
import kr.rth.picoserver.Database;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.Plugin;

import java.sql.SQLException;
import java.util.*;

import static kr.rth.picoserver.util.getItemStack.getItemStack;
import static kr.rth.picoserver.util.random.generateRandomNumber;
import static kr.rth.picoserver.util.transText.transText;

public class fishingMacroBlock implements Listener {
    static fishingMacroBlock instance;
    public HashMap<Material, String> captchaItem = new HashMap<>();
    public HashMap<Player, Integer> captchaStorage = new HashMap<>();
    public HashMap<Player, UUID> captchaHash = new HashMap<>();
    public void teleportToSpawn(Player p ) {
        Map<String, Object> locData= new HashMap<>();
        Gson gson = new Gson();
        try {
            locData = gson.fromJson(
                    (String) new ArrayList<>(Database.getInstance().execute("SELECT * FROM keyv WHERE name = 'spawnLoc'", null).get(0).values()).get(0),
                    locData.getClass()
            );
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
        p.teleport(
                Location.deserialize(locData)
        );
    }

    @EventHandler
    public void onInvClose(InventoryCloseEvent e) {
        if(!ChatColor.stripColor(e.getView().getTitle()).trim().endsWith("를 클릭하여주세요")) return;
        Player player = (Player) e.getPlayer();
        if(!captchaStorage.containsKey(player)){
            return;
        }
        captchaStorage.remove(player);
        player.playSound(player, Sound.BLOCK_LANTERN_BREAK, 1f, 1f);
        teleportToSpawn(player);
    }

    @EventHandler
    public void onInvClick(InventoryClickEvent e) {
        if(!ChatColor.stripColor(e.getView().getTitle()).trim().endsWith("를 클릭하여주세요")) return;
        if(e.getResult().equals(Event.Result.DENY)){
            e.setCancelled(true);
            return;
        }

        Player p = (Player) e.getWhoClicked();

        if(!captchaStorage.containsKey(p)){
            e.setCancelled(true);
            e.getView().close();
        }
        if(e.isRightClick()) {
            e.setCancelled(true);
            return;
        }

        e.setCancelled(true);
        int clickedCaptchaSlot = -1;

        if(e.getRawSlot() == 11) clickedCaptchaSlot = 0;
        else if(e.getRawSlot() == 13) clickedCaptchaSlot = 1;
        else if(e.getRawSlot() == 15) clickedCaptchaSlot = 2;

        if(clickedCaptchaSlot == -1 || captchaStorage.get(p) != clickedCaptchaSlot) {
            captchaStorage.remove(p);
            teleportToSpawn(p);
            p.playSound(p, Sound.BLOCK_LANTERN_BREAK, 1f, 1f);
            return;
        }
        captchaStorage.remove(p);
        e.getView().close();
        p.playSound(p, Sound.ENTITY_PLAYER_LEVELUP,1f, 2f);
    }

    public Material getRandomCaptchaItem() {
        // keySet()을 사용하여 모든 키(Material)를 가져옵니다.
        List<Material> keys = new ArrayList<>(captchaItem.keySet());

        // Random 객체를 생성하여 랜덤으로 아이템을 선택합니다.
        Random random = new Random();
        int randomIndex = random.nextInt(keys.size());

        // 랜덤으로 선택된 인덱스의 아이템 반환
        return keys.get(randomIndex);
    }

    public Inventory createCaptchaInv(Player p, Plugin plugin) {
        ArrayList<Material> items = new ArrayList<>();
        while (true) {
            Material mat = getRandomCaptchaItem();
            if(items.contains(mat)) {
                continue;
            }
            else{
                items.add(mat);
            }
            if(items.size() == 3) {
                break;
            }
        }

        int selectedIdx = generateRandomNumber( 0, 2 );

        Inventory createdInv = Bukkit.createInventory(null, 27, transText("[i]를 클릭하여주세요".replace("[i]", captchaItem.get(items.get(selectedIdx)))));
        createdInv.setItem(11, getItemStack(items.get(0), "", ""));
        createdInv.setItem(13, getItemStack(items.get(1), "", ""));
        createdInv.setItem(15, getItemStack(items.get(2), "", ""));

        captchaStorage.put(p, selectedIdx);
        UUID hash = UUID.randomUUID();
        captchaHash.put(p, hash);

        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            if (captchaHash.get(p).equals(hash) && captchaStorage.containsKey(p)) {
                teleportToSpawn(p);
                captchaStorage.remove(p);
                p.playSound(p, Sound.BLOCK_LANTERN_BREAK, 1f, 1f);
            }
        }, 20L * 10);

        return createdInv;
    }
    @EventHandler
    public void onFishing(PlayerFishEvent e) {
        if(!e.getPlayer().getWorld().getName().equals("spawn")){
            return;
        }
        if(e.getState().equals(PlayerFishEvent.State.CAUGHT_ENTITY)){
            if(e.getCaught() instanceof Player ){
                e.setCancelled(true);
            }
        }
    }

    private fishingMacroBlock() {
        captchaItem.put(Material.TNT_MINECART, "TNT가 실린 마인카트");
        captchaItem.put(Material.HOPPER_MINECART, "호퍼가 실린 마인카트");
        captchaItem.put(Material.FURNACE_MINECART, "화로가 실린 마인카트");
        captchaItem.put(Material.CHEST_MINECART, "상자가 실린 마인카트");
    }

    public static fishingMacroBlock getInstance() {
        if(instance == null) {
            instance = new fishingMacroBlock();
        }
        return instance;

    }
}
