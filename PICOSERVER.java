package kr.rth.picoserver;

import com.earth2me.essentials.Essentials;
import kr.rth.picoserver.Display.commandListener;
import kr.rth.picoserver.afk.AFKListener;
import kr.rth.picoserver.auth.UnauthorizedPlayerRegistry;
import kr.rth.picoserver.specialmine.SMineTicketListener;
import kr.rth.picoserver.specialmine.SpecialMineConfig;
import kr.rth.picoserver.util.PicoExpansion;
import kr.rth.picoserver.Display.tabcompleter;
import kr.rth.picoserver.Enchant.CommandExcutor;
import kr.rth.picoserver.Enchant.TabCompleter;
import kr.rth.picoserver.Heal.EventListener;
import kr.rth.picoserver.Money.CommandListener;
import kr.rth.picoserver.Status.Eventlistener;
import kr.rth.picoserver.Status.commandExcutor;
import kr.rth.picoserver.Status.tabCompleter;
import kr.rth.picoserver.autoHomeBuy.HomeDataContainer;
import kr.rth.picoserver.autoHomeBuy.HomePurchaseCommand;
import kr.rth.picoserver.autoHomeBuy.HomePurchaseConfig;
import kr.rth.picoserver.autoHomeBuy.HomePurchaseTabCompleter;
import kr.rth.picoserver.chat.ChatCommand;
import kr.rth.picoserver.chat.ChatConfig;
import kr.rth.picoserver.chat.chatFreeze;
import kr.rth.picoserver.chat.chatting;
import kr.rth.picoserver.chestPurchase.ChestPurchaseCommand;
import kr.rth.picoserver.chestPurchase.ChestPurchaseConfig;
import kr.rth.picoserver.chestPurchase.ChestPurchaseTabCompleter;
import kr.rth.picoserver.etc.*;
import kr.rth.picoserver.itemUpgrade.EventLIstener;
import kr.rth.picoserver.minefarmFunctions.minefarmFunctions;
import kr.rth.picoserver.virtualStorage.backpack;
import kr.rth.picoserver.virtualStorage.backpacks;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import static kr.rth.picoserver.etc.woolplate.autohealing;

public final class PICOSERVER extends JavaPlugin implements Listener {
    public static PICOSERVER picoserver;
    public static Economy econ = null;
    public static Essentials essentials = null;
    @Override
    public void onEnable() {
        picoserver = this;
        autohealing();
//        Bukkit.getScheduler().scheduleAsyncDelayedTask(PICOSERVER.getInstance(), () -> {
//            var client = HttpClient.newHttpClient();
//            var req = HttpRequest.newBuilder(
//                            URI.create("https://pluginlicensing.rth.kr/authplugin/PICOSERVER_pack_1?port={0}&motd={1}"
//                                    .replace("{0}", URLEncoder.encode(ChatColor.stripColor(String.valueOf(this.getServer().getPort())), StandardCharsets.UTF_8))
//                                    .replace("{1}",  URLEncoder.encode( ChatColor.stripColor(this.getServer().getMotd()) ,StandardCharsets.UTF_8 ))
//                            )
//                    )
//                    .build();
//            HttpResponse<String> res = null;
//            try {
//                res = client.send(req, HttpResponse.BodyHandlers.ofString());
//            } catch (IOException ex) {
//            } catch (InterruptedException ex) {
//            }
//            if(res != null) {
//                var str = new String(res.body().getBytes(StandardCharsets.UTF_8));
//                if(!str.contains("true")){
//                    this.getServer().getPluginManager().disablePlugin(this);
//                    return;
//                }
//            }
//        }, 0L);


        UnauthorizedPlayerRegistry unauthorizedPlayerRegistry = new UnauthorizedPlayerRegistry();
        HomePurchaseConfig homePurchaseConfig = new HomePurchaseConfig(this, "homepurchase_config.yml");
        ChestPurchaseConfig chestPurchaseConfig = new ChestPurchaseConfig(this, "chestpurchase_config.yml");
        ChatConfig chatConfig = new ChatConfig(this, "chat_config.yml");
        SpecialMineConfig specialMineConfig = new SpecialMineConfig(this, "specialmine_config.yml");
        HomeDataContainer homeDataContainer = new HomeDataContainer(homePurchaseConfig);
        new PicoExpansion().register();

        Bukkit.getPluginManager().registerEvents(kr.rth.picoserver.Combat.EventListener.getInstance(), this);
        // Plugin startup logic
        Bukkit.getPluginManager().registerEvents(new Cancels(), this);

        Bukkit.getPluginManager().registerEvents(new EventListener(), this);
        Bukkit.getPluginManager().registerEvents(new EventListener(), this);
        Bukkit.getPluginManager().registerEvents(new FirstSetup(), this);
        Bukkit.getPluginManager().registerEvents(new kr.rth.picoserver.LoreSystem.EventListener(), this);
        Bukkit.getPluginManager().registerEvents(new Eventlistener(), this);
        Bukkit.getPluginManager().registerEvents(new kr.rth.picoserver.Display.EventListener(), this);

        Bukkit.getPluginManager().registerEvents(new kr.rth.picoserver.autoHomeBuy.EventListener(homeDataContainer), this);
        Bukkit.getPluginManager().registerEvents(new kr.rth.picoserver.chestPurchase.EventListener(chestPurchaseConfig), this);
        Bukkit.getPluginManager().registerEvents(new kr.rth.picoserver.virtualStorage.EventListener(), this);
        Bukkit.getPluginManager().registerEvents(new kr.rth.picoserver.battle.EventListener(), this);
        Bukkit.getPluginManager().registerEvents(new kr.rth.picoserver.totalDonateDisplay.EventListener(), this);
//        Bukkit.getPluginManager().registerEvents(new kr.rth.picoserver.commandWhitelist.EventListener(), this);
        Bukkit.getPluginManager().registerEvents(new kr.rth.picoserver.randomItem.EventListener(), this);
        Bukkit.getPluginManager().registerEvents(new kr.rth.picoserver.randomBox.EventListener(), this);
        Bukkit.getPluginManager().registerEvents(new kr.rth.picoserver.runeStone.EventListener(), this);
        Bukkit.getPluginManager().registerEvents(new kr.rth.picoserver.Status.Eventlistener(), this);
        Bukkit.getPluginManager().registerEvents(new kr.rth.picoserver.Store.EventListener(), this);
        Bukkit.getPluginManager().registerEvents(new kr.rth.picoserver.oclockGiving.EventListener(), this);
        Bukkit.getPluginManager().registerEvents(new kr.rth.picoserver.playingReward.EventListener(), this);
        Bukkit.getPluginManager().registerEvents(new kr.rth.picoserver.menu.EventListener(), this);
        Bukkit.getPluginManager().registerEvents(new kr.rth.picoserver.Team.EventListener(), this);
        Bukkit.getPluginManager().registerEvents(new kr.rth.picoserver.auth.EventListener(unauthorizedPlayerRegistry), this);
        Bukkit.getPluginManager().registerEvents(new kr.rth.picoserver.MailBox.EventListener(), this);
        Bukkit.getPluginManager().registerEvents(new kr.rth.picoserver.mineList.EventListener(), this);
        Bukkit.getPluginManager().registerEvents(new kr.rth.picoserver.Package.EventListener(), this);
        Bukkit.getPluginManager().registerEvents(new kr.rth.picoserver.defualtItem.EventListener(), this);
        Bukkit.getPluginManager().registerEvents(new kr.rth.picoserver.preReversedReward.EventListener(), this);
        Bukkit.getPluginManager().registerEvents(new chatting(chatConfig), this);
        Bukkit.getPluginManager().registerEvents(new kr.rth.picoserver.posionSet.EventListener(), this);
        Bukkit.getPluginManager().registerEvents(new kr.rth.picoserver.userParticle.EventListener() , this);
        Bukkit.getPluginManager().registerEvents(new rush() , this);
        Bukkit.getPluginManager().registerEvents(new afk() , this);
        Bukkit.getPluginManager().registerEvents(slotmachine.getInstance() , this);
        Bukkit.getPluginManager().registerEvents(new EventLIstener(), this);
        //Bukkit.getPluginManager().registerEvents(Fly.getInstance() , this); 마인팜 플라이 기능
        //Bukkit.getPluginManager().registerEvents(tprequest.getInstance() , this); 태양열 곡괭이 등 마인팜 기능
        Bukkit.getPluginManager().registerEvents(new minefarmFunctions(), this);
        Bukkit.getPluginManager().registerEvents(fishingMacroBlock.getInstance() , this);
        Bukkit.getPluginManager().registerEvents(itemmelt.getInstance() , this);
        Bukkit.getPluginManager().registerEvents(itemmeltStone.getInstance() , this);
        Bukkit.getPluginManager().registerEvents(moleCatch.getInstance() , this);
        Bukkit.getPluginManager().registerEvents(new kr.rth.picoserver.HealthBar.EventListener() , this);
        Bukkit.getPluginManager().registerEvents(kr.rth.picoserver.etc.daegari.getInstance() , this);
        Bukkit.getPluginManager().registerEvents(new kr.rth.picoserver.etc.potionStack() , this);
        Bukkit.getPluginManager().registerEvents(new SMineTicketListener(), this);
        Bukkit.getPluginManager().registerEvents(new AFKListener(), this);
        this.getCommand("enchantment").setTabCompleter(new TabCompleter());
        this.getCommand("정보").setTabCompleter(new tabCompleter());
        this.getCommand("뽑기").setTabCompleter(new kr.rth.picoserver.randomItem.tabcompleter());
        this.getCommand("뽑기").setExecutor(new kr.rth.picoserver.randomItem.CommandListener() );
        this.getCommand("랜덤박스").setTabCompleter(new kr.rth.picoserver.randomBox.TabCompleter());
        this.getCommand("랜덤박스").setExecutor(new kr.rth.picoserver.randomBox.CommandExcutor());
        this.getCommand("사전예약보상").setExecutor(new kr.rth.picoserver.preReversedReward.commandListener());
        this.getCommand("enchantment").setExecutor(new CommandExcutor());
        this.getCommand("heal").setExecutor(new kr.rth.picoserver.Heal.CommandExcutor() );
        this.getCommand("정보").setExecutor(new commandExcutor());
        this.getCommand("전시").setExecutor(new commandListener());
        this.getCommand("돈").setExecutor(new CommandListener());
        this.getCommand("캐시").setExecutor(new kr.rth.picoserver.Cash.CommandListener());
        this.getCommand("크래딧").setExecutor(new kr.rth.picoserver.credit.CommandListener());
        this.getCommand("마일리지").setExecutor(new kr.rth.picoserver.mileage.CommandListener());
        this.getCommand("잠수포인트").setExecutor(new kr.rth.picoserver.afkpoint.CommandListener());
        this.getCommand("접속시간").setExecutor(new kr.rth.picoserver.playTime.CommandExcutor());
        this.getCommand("창고").setExecutor(new kr.rth.picoserver.virtualStorage.commandExcutor());
        this.getCommand("전시").setTabCompleter(new tabcompleter());
//        this.getCommand("대전").setExecutor(new kr.rth.picoserver.battle.commandListener());
        this.getCommand("후원누적보상").setExecutor(new kr.rth.picoserver.totalDonateDisplay.CommandListener());
        this.getCommand("후원누적보상").setTabCompleter(new kr.rth.picoserver.totalDonateDisplay.TabCompleter());
//        this.getCommand("명령어").setExecutor(new kr.rth.picoserver.commandWhitelist.CommandExcutor() );
//        this.getCommand("명령어").setTabCompleter(new kr.rth.picoserver.commandWhitelist.TabCompleter());
        this.getCommand("룬스톤").setExecutor(new kr.rth.picoserver.runeStone.CommandListener() );
//this.getCommand("룬스톤").setExecutor(new kr.rth.picoserver.runeStone.CommandListener() );
        this.getCommand("상점").setExecutor(new kr.rth.picoserver.Store.CommandListener() );
        this.getCommand("상점").setTabCompleter(new kr.rth.picoserver.Store.TabCompleter() );
        this.getCommand("정각보상설정").setExecutor(new kr.rth.picoserver.oclockGiving.commandListener() );
        this.getCommand("접속보상설정").setExecutor(new kr.rth.picoserver.playingReward.commandListener() );
        this.getCommand("메뉴").setExecutor(new kr.rth.picoserver.menu.commandListener() );
        this.getCommand("마리보상설정").setExecutor(new kr.rth.picoserver.mineList.command() );
        this.getCommand("메뉴").setTabCompleter(new kr.rth.picoserver.menu.TabCompleter());
        this.getCommand("특별광산").setExecutor(new kr.rth.picoserver.specialmine.commandListener(specialMineConfig) );
        this.getCommand("특별광산").setTabCompleter(new kr.rth.picoserver.specialmine.tabCompleter() );
        this.getCommand("인증").setExecutor(new kr.rth.picoserver.auth.commandListener(unauthorizedPlayerRegistry) );
        this.getCommand("hga").setExecutor(new kr.rth.picoserver.handGiveAll.HGA() );
        this.getCommand("팀").setExecutor(new kr.rth.picoserver.Team.CommandListener() );
        this.getCommand("팀").setTabCompleter(new kr.rth.picoserver.Team.TabCompleter());
        this.getCommand("스텟").setExecutor(new kr.rth.picoserver.Stat.commandListener() );
        this.getCommand("우편함").setExecutor(new kr.rth.picoserver.MailBox.CommandListener() );
        this.getCommand("패키지").setExecutor(new kr.rth.picoserver.Package.CommandListener() );
        this.getCommand("기본템").setExecutor(new kr.rth.picoserver.defualtItem.commandListener() );
        this.getCommand("조합셋").setExecutor(new kr.rth.picoserver.posionSet.ComandListener() );
        this.getCommand("파티클").setExecutor(new kr.rth.picoserver.userParticle.CommandListener() );
        this.getCommand("파티클").setTabCompleter(new kr.rth.picoserver.userParticle.tabCompleter());
        this.getCommand("조합셋").setTabCompleter(new kr.rth.picoserver.posionSet.tabCompleter());
        this.getCommand("최대접속인원").setExecutor(new kr.rth.picoserver.etc.maxPlayers());
        this.getCommand("장사글").setExecutor(new jangsa() );
        this.getCommand("rush").setExecutor(new rush() );
        this.getCommand("slotmachine").setExecutor(slotmachine.getInstance());
        this.getCommand("afkteleport").setExecutor(new afk());
        this.getCommand("강화").setExecutor(new kr.rth.picoserver.itemUpgrade.commandListener());
        this.getCommand("강화").setTabCompleter(new  kr.rth.picoserver.itemUpgrade.tabCompleter());
        //this.getCommand("플라이").setExecutor(        Fly.getInstance());
        this.getCommand("강제티피").setExecutor(       new forcetp());
        this.getCommand("티피").setExecutor(TPARequest.getInstance());
        this.getCommand("채팅금지").setExecutor(      new chatFreeze());
        this.getCommand("분해").setExecutor(    itemmelt.getInstance());
        this.getCommand("연금술").setExecutor(    itemmeltStone.getInstance());
        this.getCommand("두더지").setExecutor(    moleCatch.getInstance());
        this.getCommand("명성").setExecutor(    new FameCommandListener());
        this.getCommand("picohouse").setExecutor(new HomePurchaseCommand(homeDataContainer));
        this.getCommand("picohouse").setTabCompleter(new HomePurchaseTabCompleter());
        this.getCommand("상자구매").setExecutor(new ChestPurchaseCommand(chestPurchaseConfig));
        this.getCommand("상자구매").setTabCompleter(new ChestPurchaseTabCompleter());
        this.getCommand("채팅").setExecutor(new ChatCommand(chatConfig));
        this.getCommand("무적").setExecutor(new GodMode());
        this.getCommand("entitynametag").setExecutor(new entityNametag());
        kr.rth.picoserver.Display.looper.getInstance().reload();
        kr.rth.picoserver.posionSet.Looper.getInstance().reload();
        kr.rth.picoserver.userParticle.Looper.getInstance().reload();
        kr.rth.picoserver.playTime.looper.getInstance();
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        econ = rsp.getProvider();
        essentials = (Essentials) Bukkit.getPluginManager().getPlugin("Essentials");
//        for (Player p2 : Bukkit.getOnlinePlayers()) {
//            p2.setMaximumNoDamageTicks(0);
//        }
//        .register();


//        PlaceholderAPI.registerPlaceholder(this, "player_level", event -> {
//            // 플레이어를 가져옴
//            Player player = event.getPlayer();
//            if (player != null) {
//                // 플레이어의 레벨을 반환
//                return String.valueOf(player.getLevel());
//            }
//            return null;
//        });
//        new HealthBarManager(this);
        //        this.getLogger().setLevel(Level.);
//
//        Bukkit.getLogger().addHandler();


//        Logger.getGlobal().setFilter(new consoleFilter());
//        Bukkit.getLogger().setFilter(new consoleFilter());


    }
    public static void dispatchCommand(Player sender, String msg) {
        PlayerCommandPreprocessEvent ea = new PlayerCommandPreprocessEvent(sender, msg );
        Bukkit.getPluginManager().callEvent(ea);
        if(!ea.isCancelled()) {
//                        p.performCommand("정보 "+ ChatColor.stripColor(e.getView().getTitle()).replace("→ ", "").trim());
            Bukkit.dispatchCommand(sender,msg );
        }
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        try{
            backpacks.getInstance();
            for( Inventory i : backpacks.backpackContents.values()  ) {
                    backpack bp = (backpack) i.getHolder();
                    bp.saveInventory();
            }
        }
        catch(Exception e) {
        }
        if(!Database.getInstance().datasource.isClosed()){
            Database.getInstance().datasource.close();
        }

    }
    public static Economy getEcon() {
        return econ;
    }
    public static PICOSERVER getInstance() {
        return picoserver;
    }
}
