package kr.rth.picoserver.itemUpgrade;

import kr.rth.picoserver.Money.Money;
import kr.rth.picoserver.PICOSERVER;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Random;

import static kr.rth.picoserver.util.getItemStack.getItemStack;
import static kr.rth.picoserver.util.getItemStack.getItemStackWithCustomModelData;
import static kr.rth.picoserver.util.itemStackSerializer.itemStackDeSerializer;
import static kr.rth.picoserver.util.transText.transText;
import static kr.rth.picoserver.util.numberWithComma.numberWithComma;

public class EventLIstener implements Listener {
    public  EventLIstener() {
        seemlessStorage.getInstance();

    }
    public HashMap<Player, HashMap<String, Object>> lockMap = new HashMap<>();

    public String getRandomString(HashMap<String, Integer> randomMap) {
        int totalWeight = randomMap.values().stream().mapToInt(Integer::intValue).sum();
        int randomNumber = (new Random()).nextInt(totalWeight) + 1; // 1부터 totalWeight 사이의 랜덤 숫자 생성

        for (var entry : randomMap.entrySet()) {
            randomNumber -= entry.getValue();
            if (randomNumber <= 0) {
                return entry.getKey();
            }
        }

        // 만약 루프가 끝날 때까지 반환되지 않았다면 오류가 있을 수 있음
        throw new IllegalStateException("랜덤 문자열을 선택하는 도중 오류가 발생했습니다.");
    }
    @EventHandler
    public void onInvClose(InventoryCloseEvent e) {
        if(e.getInventory() == null) return;

        if(!e.getInventory().getType().equals(InventoryType.BREWING)) return;

        if(!ChatColor.stripColor(e.getView().getTitle()).trim().equalsIgnoreCase(":offset_5::rkdghk:")){
            return;
        }

        Inventory closedInv = e.getInventory();
        Player p = (Player) e.getPlayer();

        if(closedInv.getItem(0)!= null){
            p.getInventory().addItem(closedInv.getItem(0));
        }

        if(closedInv.getItem(2)!= null){
            p.getInventory().addItem(closedInv.getItem(2));
        }

        if(closedInv.getItem(3)!= null){
            p.getInventory().addItem(closedInv.getItem(3));
        }
        if( lockMap.containsKey(p) ){
            var i  = lockMap.get(p);
            if((boolean) i.get("isSpecialCoin")) {
                ItemStack coin = seemlessStorage.getInstance().specialCoin.clone();
                coin.setAmount((Integer) i.get("usedCoin"));
                p.getInventory().addItem(coin);
            }else{
                ItemStack coin = seemlessStorage.getInstance().normalCoin.clone();
                coin.setAmount((Integer) i.get("usedCoin"));
                p.getInventory().addItem(coin);
            }
            Money.add(p, (Integer) i.get("usedMoney"));
//            p.getInventory().addItem((ItemStack) i.get("targetItem"));

            lockMap.remove(p);
        }


    }
    @EventHandler(priority = EventPriority.LOWEST)
    public void onInvClick(InventoryClickEvent e) {

        if(e.getClickedInventory() == null) return;


        if(!ChatColor.stripColor(e.getView().getTitle()).trim().equalsIgnoreCase(":offset_5::rkdghk:")){
            return;
        }
        if( e.getSlot() == 3 ) {
            e.setCancelled(true);
            if(e.getClickedInventory().getItem(3) == null ) {
                return;
            }else {
                e.setCancelled(false);
            }
            return;
        }

        if(!e.getClickedInventory().getType().equals(InventoryType.BREWING)) return;

        if(e.getResult().equals(Event.Result.DENY) ) {
            return;
        }
        Inventory clickedInv = e.getClickedInventory();
        e.setCancelled(true);
        if(!(e.getClick().equals(ClickType.LEFT)|| e.getClick().equals(ClickType.RIGHT) || e.getClick().equals(ClickType.NUMBER_KEY))) {
            return;
        }
        Player p = (Player) e.getWhoClicked();
        if(lockMap.containsKey(p)) {
            return;
        }
//        p.sendMessage(String.valueOf( e.getSlot()));



        if(clickedInv.getItem(3) != null) {
            p.playSound(p, Sound.BLOCK_LANTERN_BREAK, 1, 1);
            return;
        }
        if(e.getSlot() == 0) {
            if(!(clickedInv.getItem(0) == null)){
                e.setCancelled(false);
                clickedInv.setItem(1, getItemStackWithCustomModelData(Material.PAPER, "&f \uE11B &7강화 시작하기",
                        "&f\n&f &7[ &f왼쪽 강화 할 아이템 &7| &f오른쪽 강화석 &7] &f을 넣어주세요 \n" +
                             "&f 강화에 대한 정보는 아이템을 넣으면 아래에 표기 됩니다 &f\n&f",
                        10048
                ));
                return;
            }
//            e.get
            if(seemlessStorage.getInstance().get(e.getCursor()) == null) {
                return;
            }
            clickedInv.setItem(0, e.getCursor());
            e.setCursor(null);
            p.playSound(p, Sound.BLOCK_END_PORTAL_FRAME_FILL, 1f, 2f);
            if(clickedInv.getItem(0) != null){
                var i = seemlessStorage.getInstance().get(clickedInv.getItem(0));
                if(i == null) {
                    return;
                }

                clickedInv.setItem(1, getItemStackWithCustomModelData(Material.PAPER, "&f \uE11B &7강화 시작하기",
                        ("&f\n" +
                        "&f &7[ &f왼쪽 강화 할 아이템 &7| &f오른쪽 강화석 &7] &f을 넣어주세요 \n" +
                        "&f 강화에 대한 정보는 아이템을 넣으면 아래에 표기 됩니다 &f\n" +
                        "&f\n" +
                        "&7 噾 &f성공시 제작 가능 &x&F&A&E&D&C&B&l>&x&F&B&D&F&9&4&l>&x&F&C&D&0&5&C&l>&r [itemN]\n" +
                        "&7 噾 &f강화 가격 &x&F&A&E&D&C&B&l>&x&F&B&D&F&9&4&l>&x&F&C&D&0&5&C&l>&r &x&F&F&F&4&5&E[price]\n" +
                        "&f\n" +
                        "&e                 \uE143 &f성공 확률 &x&E&A&D&D&B&C&l&o{0}%&r \n&f  \uE159 &f \uE13A &f실패 확률 &x&D&5&A&0&A&0&l&o{1}%&r\n" +
                        "&f                 \uE15A &f파괴 확률 &x&C&D&C&D&C&D&l&o{2}%&r \n&f                 \uE153 &f필요한 강화석 &x&C&8&C&0&D&4&l&o{3}개 &f\n" +
                        "&f")
                        .replace("{0}", Integer.toString( ( (Double) i.get("winPerc")   ).intValue()) )
                        .replace("{1}", Integer.toString( ( (Double) i.get("losePerc")  ).intValue()) )
                        .replace("{2}", Integer.toString( ( (Double) i.get("delPerc") ).intValue()) )
                        .replace("{3}", Integer.toString( ( (Integer) i.get("coin") )) )
                        .replace("[price]", numberWithComma((Integer) i.get("price")))
                        .replace("[itemN]", itemStackDeSerializer( (String) i.get("winItem") ).getItemMeta().getDisplayName() ),
                        10048
                ));

            }
        }


        if(e.getSlot() == 2) {
            if( clickedInv.getItem(2) == null && !( seemlessStorage.getInstance().normalCoin.isSimilar(e.getCursor()) || seemlessStorage.getInstance().specialCoin.isSimilar(e.getCursor()))  ){
                return;
            }
            if(!(clickedInv.getItem(2) == null)){
                e.setCancelled(false);
                return;
            }
            clickedInv.setItem(2, e.getCursor());
            e.setCursor(null);
            p.playSound(p, Sound.BLOCK_END_PORTAL_FRAME_FILL, 1f, 2f);
        }
        if( e.getSlot() == 1 ) {
            if(clickedInv.getItem(0) == null) {
                p.playSound(p, Sound.BLOCK_LANTERN_BREAK, 1, 1);
                return;
            }
            if(clickedInv.getItem(2) == null) {
                p.playSound(p, Sound.BLOCK_LANTERN_BREAK, 1, 1);
                return;
            }
            var i = seemlessStorage.getInstance().get(clickedInv.getItem(0));

            if( (Integer) i.get("coin") > clickedInv.getItem(2).getAmount() ) {
                p.playSound(p, Sound.BLOCK_LANTERN_BREAK, 1, 1);
                return;
            }

            if( (Integer) i.get("price") > Money.get(p)) {
                p.playSound(p, Sound.BLOCK_LANTERN_BREAK, 1, 1);
                return;
            }

            boolean isSpecialCoin = seemlessStorage.getInstance().specialCoin.isSimilar(clickedInv.getItem(2));

            clickedInv.getItem(2).setAmount(clickedInv.getItem(2).getAmount() - (Integer) i.get("coin") );
            Money.sub(p, (Integer) i.get("price"));
            HashMap<String, Object> lockValue = new HashMap<>();
            lockValue.put("usedCoin", i.get("coin"));
            lockValue.put("isSpecialCoin", isSpecialCoin);
            lockValue.put("targetItem", clickedInv.getItem(0));
            lockValue.put("usedMoney", i.get("price"));
            lockMap.put(p, lockValue);



            HashMap<String, Integer> randomMap = new HashMap<>();
            randomMap.put("win",( (Double) i.get("winPerc")   ).intValue() );
            randomMap.put("break", ( (Double) i.get("delPerc")  ).intValue());
            randomMap.put("lose",  ((Double) i.get("losePerc")  ).intValue() );
            String result = getRandomString(randomMap);
            p.playSound(p, Sound.BLOCK_ANVIL_USE, 1f, 1.5f);
            Bukkit.getScheduler().scheduleAsyncDelayedTask(PICOSERVER.getInstance(), () ->{
                if(!lockMap.containsKey(p)) {
                    return ;
                }

                if( result.equalsIgnoreCase("win")) {
                    clickedInv.setItem(0, null);
                    clickedInv.setItem(3, itemStackDeSerializer((String) i.get("winItem")));
                    clickedInv.setItem(1, getItemStackWithCustomModelData(Material.PAPER, "&f \uE11B &7강화 시작하기",
                            "&f\n&f &7[ &f왼쪽 강화 할 아이템 &7| &f오른쪽 강화석 &7] &f을 넣어주세요 \n&f 강화에 대한 정보는 아이템을 넣으면 아래에 표기 됩니다 &f\n&f",
                            10048)
                    );
                    p.playSound(p, Sound.ENTITY_PLAYER_LEVELUP, 1f, 1.75f);
                    Bukkit.broadcastMessage(transText("&f \uE139 &f강화를 통해 &x&F&A&E&D&C&B[p]&f 님께서 &r[i] &f을(를) 획득했습니다 ".replace("[p]", p.getName()).replace("[i]", itemStackDeSerializer((String) i.get("winItem")).getItemMeta().getDisplayName())));
                }

                if( result.equalsIgnoreCase("break")) {
                    if( !isSpecialCoin ){
                        clickedInv.setItem(0, null);
                        clickedInv.setItem(1, getItemStackWithCustomModelData(Material.PAPER, "&f \uE11B &7강화 시작하기",
                                "&f\n&f &7[ &f왼쪽 강화 할 아이템 &7| &f오른쪽 강화석 &7] &f을 넣어주세요 \n&f 강화에 대한 정보는 아이템을 넣으면 아래에 표기 됩니다 &f\n&f",
                                10048
                        ));
                    }
                    p.playSound(p, Sound.ENTITY_GENERIC_EXPLODE, 1f, 1f);
                }
                if( result.equalsIgnoreCase("lose")) {
//                    clickedInv.setItem(2, itemStackDeSerializer((String) i.get("targetItem")));
                    p.playSound(p, Sound.ENTITY_ITEM_BREAK, 1f, 1f);
                }
                lockMap.remove(p);
            }, 11L);






        }





    }
}
