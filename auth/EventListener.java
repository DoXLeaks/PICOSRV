package kr.rth.picoserver.auth;

import kr.rth.picoserver.Database;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import java.sql.SQLException;
import java.util.ArrayList;

import static kr.rth.picoserver.util.transText.transText;

public class EventListener implements Listener {

    UnauthorizedPlayerRegistry unauthorizedPlayerRegistry;

    public EventListener(UnauthorizedPlayerRegistry unauthorizedPlayerRegistry) {
        this.unauthorizedPlayerRegistry = unauthorizedPlayerRegistry;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        ArrayList<Object> q = new ArrayList<>();
        q.add(e.getPlayer().getUniqueId().toString());

        try {
            if (1 > (Integer) new ArrayList<>(Database.getInstance().execute("SELECT EXISTS (SELECT * FROM UserAuthentication WHERE uuid = ?)", q).get(0).values()).get(0)) {
                this.unauthorizedPlayerRegistry.register(e.getPlayer());
            } else {
                this.unauthorizedPlayerRegistry.unregister(e.getPlayer());
            }
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent e) {
        if (e.getMessage().startsWith("/인증")) {
            return;
        }
        if (!this.unauthorizedPlayerRegistry.contains(e.getPlayer())) {
            return;
        }
        e.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onChat(AsyncPlayerChatEvent e) {
        if (!this.unauthorizedPlayerRegistry.contains(e.getPlayer())) {
            return;
        }
        e.setCancelled(true);
    }

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        if (!this.unauthorizedPlayerRegistry.contains(e.getPlayer())) {
            return;
        }
        e.setCancelled(true);

        // Sending message with link to Discord for verification
        TextComponent component = new TextComponent(transText("&e<&n인증&e>"));
        component.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://discord.gg/picosv"));
        e.getPlayer().spigot().sendMessage(ChatMessageType.CHAT, new TextComponent(new TextComponent(transText("&f \uE1C4 &f서버 플레이 전 디스코드 인증을 진행해 주세요. ")), component));
    }
}