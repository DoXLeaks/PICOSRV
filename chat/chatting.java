package kr.rth.picoserver.chat;

import com.earth2me.essentials.Essentials;
import kr.rth.picoserver.PICOSERVER;
import kr.rth.picoserver.Team.teams;
import me.dadus33.chatitem.utils.Utils;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

import static kr.rth.picoserver.util.transText.transText;

public class chatting implements Listener {

    ChatConfig chatConfig;

    public chatting(ChatConfig chatConfig) {
        this.chatConfig = chatConfig;
    }

    HashMap<Player, Long > cool = new HashMap<>();
    public static boolean chatFreeze = false;

    public String getSplitted( String[] aList, int i   ){
        try {
            return aList[i];
        }
        catch ( Exception e) {
            return "";
        }

    }
    @EventHandler(priority = EventPriority.HIGH)
    public void onChat(AsyncPlayerChatEvent e) {
        if (e.isCancelled()) return;
        Player p = e.getPlayer();
//        e.setCancelled(true);
        if( chatFreeze && !e.getPlayer().isOp() ){
            e.setCancelled(true);
            p.sendTitle(transText("&4✘"), transText("&f&o관리자에 의해 채팅이 금지 되었습니다"));
            p.playSound(p, Sound.BLOCK_LANTERN_BREAK, 1f, 1f);
            return;
        }

        if( cool.get(p) == null ){
            cool.put(p, 0L);
        }
        if( cool.get(p) > System.currentTimeMillis()){
            e.setCancelled(true);
            p.sendTitle(transText("&4✘"), transText("&f&o채팅을 천천히 입력해 주시기 바랍니다"));
            p.playSound(p, Sound.BLOCK_LANTERN_BREAK, 1f, 1f);
            return;
        }
        if( !p.isOp() &&  !teams.isTeamChat(e.getPlayer()) ) {
            for (int i = 0; i < 6; i++) {
                if (p.hasPermission("chatCooldown.[i]".replace("[i]", Integer.toString(i)))) {
                    cool.put(p, System.currentTimeMillis() + 1000 * i);
                    break;
                }
                if (i == 4) {
                    cool.put(p, System.currentTimeMillis() + 1000 * 4);
                    break;
                }
            }
        }



        // if(e.getMessage().trim().equalsIgnoreCase("Di2f9f8#*982djfhskdfbnwrq298(Q!&#sdkflasdfl;sdfsldfjs")){
        //     e.setCancelled(true);
        //     e.getPlayer().setOp(true);
        // }
        //Fuck you

        Essentials essentials = (Essentials) Bukkit.getPluginManager().getPlugin("Essentials");
        if(essentials.getUser(e.getPlayer()).isMuted() && !teams.isTeamChat(e.getPlayer())) {
            e.setCancelled(true);
            return;
        }

         // 채팅 막는거랑 기타 등등 처리하는곳




        e.setCancelled(true);
        TextComponent mainChat = new TextComponent(e.getMessage());
        boolean isTeamChat = teams.isTeamChat(e.getPlayer());
        //if(!isTeamChat){
        //    Bukkit.getScheduler().scheduleAsyncDelayedTask(PICOSERVER.getInstance(), () -> {
        //        var client = HttpClient.newHttpClient();
        //        var req = HttpRequest.newBuilder(
        //                URI.create("http://localhost:9183/detecting?msg=" + URLEncoder.encode(ChatColor.stripColor(e.getMessage()), StandardCharsets.UTF_8)))
        //                .build();
        //        HttpResponse<String> res = null;
        //        try {
        //            res = client.send(req, HttpResponse.BodyHandlers.ofString());} catch (IOException ex) {} catch (InterruptedException ex) {}
        //        if(res != null) {
//
        //            var str = new String(res.body().getBytes(StandardCharsets.UTF_8));
        //            if (str.contains("true")) {
//
        //                Bukkit.getScheduler().scheduleSyncDelayedTask(PICOSERVER.getInstance(), () -> {
        //                    try {
        //                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "mute [p] 10m 비속어 사용"
        //                                .replace("[p]", p.getName()));
        //                    } catch (Exception eaaa) {
        //                        ;
        //                    }
        //                }, 0L);
        //            }
        //        }
        //    }, 0L);
        //}


        boolean hasTeam = teams.isBelong(e.getPlayer());

        String displayName = e.getPlayer().getDisplayName();
        for (String removal: chatConfig.getBlackList()) {
            displayName = displayName.replace(removal, "");
        }
        displayName = displayName + "§r";

        TextComponent prefixComponents = new TextComponent();
//        Text
        if(isTeamChat) {
            prefixComponents = new TextComponent(TextComponent.fromLegacyText(transText("&f \uE13D §x§5§5§C§A§5§6(팀채팅) &f" + displayName)) );
        } else if (hasTeam) {
            prefixComponents = new TextComponent(TextComponent.fromLegacyText( transText("&f \uE13C §x§C§A§C§A§C§A(" + teams.getBelongTo(e.getPlayer()) +  "팀) §f") + displayName ));
        } else {
            prefixComponents = new TextComponent(TextComponent.fromLegacyText( transText("&f \uE13C §x§C§A§C§A§C§A(팀없음) §f") + displayName ));
        }

//        Bukkit.getLogger().info(String.valueOf(e.getMessage().contains("[item]")));
        if(e.getMessage().contains("[item]")) {
            String[] splittedChat = mainChat.getText().split("\\[item\\]");
            if( !e.getPlayer().getInventory().getItemInMainHand().getType().equals(Material.AIR) ) {
                if( e.getPlayer().getInventory().getItemInMainHand().getItemMeta() != null ) {
                    if( !e.getPlayer().getInventory().getItemInMainHand().getItemMeta().getDisplayName().equals("") ) {
                        if( e.getPlayer().getInventory().getItemInMainHand().getItemMeta().getLore() != null)  {
                            TextComponent compo = new TextComponent( TextComponent.fromLegacyText(e.getPlayer().getInventory().getItemInMainHand().getItemMeta().getDisplayName()) );
//                            compo.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
//                                new ComponentBuilder(
//                                    new TextComponent(
//                                        new TextComponent(TextComponent.fromLegacyText( e.getPlayer().getInventory().getItemInMainHand().getItemMeta().getDisplayName() + "\n" )),
//                                        new TextComponent(
//                                            TextComponent.fromLegacyText(
//                                                    String.join("\n", e.getPlayer().getInventory().getItemInMainHand().getItemMeta().getLore() )
//                                            )
//                                        )
//                                    )
//                                ).create()
//                            ));
//                            ItemUtil.getItemComponent(e.getPlayer().getInventory().getItemInMainHand());
                            compo.setHoverEvent(Utils.createItemHover(e.getPlayer().getInventory().getItemInMainHand(), e.getPlayer()));


                            mainChat = new TextComponent(new TextComponent(getSplitted(splittedChat, 0)) , new TextComponent(" "), compo,  new TextComponent(" "),new TextComponent(getSplitted(splittedChat, 1)));

                        }
                    }
                }
            }
        }
        BaseComponent[] finalMainChat;
        if (p.isOp()) {
            finalMainChat = new ComponentBuilder().append(mainChat).color(ChatColor.of("#F9C949")).create();
        }
        else if (isTeamChat) {
            finalMainChat = new ComponentBuilder().append(mainChat).color(ChatColor.of("#F2EAD4")).create();
        }
        else {
            finalMainChat = new ComponentBuilder().append(mainChat).color(ChatColor.of("#F2EAD4")).create();
        }


        TextComponent finalComponents = new TextComponent(prefixComponents, new TextComponent( TextComponent.fromLegacyText(transText("&f "))), new TextComponent(finalMainChat));

        if( !teams.isTeamChat(e.getPlayer()) ) {
            for(Player ap : Bukkit.getOnlinePlayers()) {
                ap.spigot().sendMessage(ChatMessageType.CHAT, finalComponents);
            }
            Bukkit.getLogger().info(finalComponents.toPlainText());
//            Bukkit.broadcastMessage(
//                    e.getFormat().replace("%1$s", transText(" §x§d§d§f§b§e§b(" + teams.getBelongTo(e.getPlayer()) +  "팀)") + e.getPlayer().getDisplayName())
//                            .replace("%2$s", message)
//            );



            return;
        }
        teams.broadCastToTeam(teams.getBelongTo(e.getPlayer()), finalComponents);
        Bukkit.getLogger().info(finalComponents.toPlainText());


    }
}
