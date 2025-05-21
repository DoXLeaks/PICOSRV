package kr.rth.picoserver.posionSet;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import kr.rth.picoserver.Database;
import kr.rth.picoserver.PICOSERVER;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import static kr.rth.picoserver.util.itemStackSerializer.itemStackDeSerializer;

public class Looper {

    static  Looper instance;
    static HashMap<String, ArrayList<Object>> sets = new HashMap();
    static HashMap<Player, Long> cooldown = new HashMap<>();
    public boolean isWearingItem(Player player, ItemStack material) {
        // 플레이어의 헤드 슬롯 아이템 확인
        ItemStack helmet = player.getInventory().getHelmet();
        if (helmet != null && helmet.equals(material) ) {
            return true;
        }

        // 플레이어의 체스트 플레이트 슬롯 아이템 확인
        ItemStack chestplate = player.getInventory().getChestplate();
        if (chestplate != null && chestplate.equals(material) ) {
            return true;
        }

        // 플레이어의 갑옷 다리 슬롯 아이템 확인
        ItemStack leggings = player.getInventory().getLeggings();
        if (leggings != null && leggings.equals(material)) {
            return true;
        }

        // 플레이어의 갑옷 부츠 슬롯 아이템 확인
        ItemStack boots = player.getInventory().getBoots();
        return boots != null && boots.equals(material);
    }
    private  Looper() {
        Bukkit.getScheduler().scheduleAsyncRepeatingTask(PICOSERVER.getInstance(), () ->{
            for(Player i : Bukkit.getOnlinePlayers()){
                for(var  ii: sets.values() ) {
                    ArrayList<ItemStack> items = (ArrayList<ItemStack>) ii.get(0);
                    ArrayList<LinkedTreeMap<String, String>> effects = (ArrayList<LinkedTreeMap<String, String>>) ii.get(1);
                    ArrayList<String> worlds = (ArrayList<String>) ii.get(2);
                    ArrayList<String> inactiveWorlds = (ArrayList<String>) ii.get(3);
                    World playerWorld = i.getWorld();
                    if (!inactiveWorlds.isEmpty()) {
                        if (!inactiveWorlds.contains(playerWorld.getName())) {
                            int b=  0;
                            for(ItemStack iii : items ) {
                                if(!isWearingItem(i, iii) ) {
                                    b = 1;
                                }
                            }
                            if(b == 1) continue; //이해하기싫다 - MellDa

                            for (var iii : effects) {
                                Bukkit.getScheduler().scheduleSyncDelayedTask(PICOSERVER.getInstance(), () -> {
                                    if(cooldown.get(i) != null) {
                                        if(cooldown.get(i) > System.currentTimeMillis()){
                                            return;
                                        }else{
                                            cooldown.remove(i);
                                        }

                                    }
                                    i.addPotionEffect(
                                            new PotionEffect(
                                                    PotionEffectType.getByName(String.valueOf(iii.get("POSION"))),
                                                    (Integer.parseInt(iii.get("DURATION")) ),
                                                    (Integer.parseInt(iii.get("AMP")) -1)
                                            )
                                    )
                                    ;

                                });
                            }
//                    isWearingItem
                        }
                    } else {
                        if (worlds.contains(playerWorld.getName())) {
                            int b=  0;
                            for(ItemStack iii : items ) {
                                if(!isWearingItem(i, iii) ) {
                                    b = 1;
                                }
                            }
                            if(b == 1) continue; //이해하기싫다 - MellDa

                            for (var iii : effects) {
                                Bukkit.getScheduler().scheduleSyncDelayedTask(PICOSERVER.getInstance(), () -> {
                                    if(cooldown.get(i) != null) {
                                        if(cooldown.get(i) > System.currentTimeMillis()){
                                            return;
                                        }else{
                                            cooldown.remove(i);
                                        }

                                    }
                                    i.addPotionEffect(
                                            new PotionEffect(
                                                    PotionEffectType.getByName(String.valueOf(iii.get("POSION"))),
                                                    (Integer.parseInt(iii.get("DURATION")) ),
                                                    (Integer.parseInt(iii.get("AMP")) -1)
                                            )
                                    )
                                    ;

                                });
                            }
//                    isWearingItem
                        }
                    }
                }
            }
        }, 0L, 20L);

    }
    public void reload() {
        sets.clear();
        Gson gson = new Gson();
        HashMap<String, String> invContent = new HashMap<>();
        ArrayList<HashMap<String, String>> effects = new ArrayList<>();
        try {
            for( var i : Database.getInstance().execute("SELECT * FROM posionset", null)) {
                if(i.get("sets") == null || i.get("posionEffects") == null || i.get("active_worlds") == null) {
                    continue;
                }
                invContent = gson.fromJson((String) i.get("sets"), invContent.getClass());
                ArrayList<Object> dt = new ArrayList<>();
                ArrayList<ItemStack> items = new ArrayList<>();
                ArrayList<String> worlds = new ArrayList<>();
                ArrayList<String> inactiveWorlds = new ArrayList<>();
                for( String ii : invContent.values() ) {
                    items.add(
                        itemStackDeSerializer(ii)
                    );
                }
                effects = gson.fromJson((String) i.get("posionEffects"), effects.getClass());
                worlds = gson.fromJson((String) i.get("active_worlds"), worlds.getClass());
                if (i.get("inactive_worlds") != null) {
                    inactiveWorlds = gson.fromJson((String) i.get("inactive_worlds"), inactiveWorlds.getClass());
                }

                if(items.isEmpty() || effects.isEmpty()) {
                    continue;
                }
                dt.add(items);
                dt.add(effects);
                dt.add(worlds);
                dt.add(inactiveWorlds);
                sets.put((String) i.get("id"), dt);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    public static Looper getInstance() {
        if(instance == null) {
            instance = new Looper();
        }
        return instance;
    }
}
