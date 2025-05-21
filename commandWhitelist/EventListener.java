package kr.rth.picoserver.commandWhitelist;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.BukkitConverters;
import kr.rth.picoserver.Database;
import kr.rth.picoserver.PICOSERVER;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChatTabCompleteEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerCommandSendEvent;
import org.bukkit.event.server.TabCompleteEvent;
import org.bukkit.help.HelpTopic;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

public class EventListener implements Listener {


    @EventHandler(priority =  EventPriority.HIGHEST)
    public void onTav(TabCompleteEvent e) throws SQLException {
        if(e.getSender().isOp()) return;
        ArrayList<Object> q = new ArrayList<>();
        q.add(
                Arrays.asList(e.getBuffer().replace("/", "").split(" ")).get(0)
        );
        if( 1 > (Integer) new ArrayList<>(Database.getInstance().execute("SELECT EXISTS(SELECT * FROM commandWhitelist WHERE name = ? )", q).get(0).values()).get(0)) {
            e.setCancelled(true);
        }
        HelpTopic topic = Bukkit.getServer().getHelpMap().getHelpTopic((String) q.get(0));

    }

    @EventHandler(priority =  EventPriority.HIGHEST)
    public void onSent(PlayerCommandSendEvent e) {
        if (e.getPlayer().isOp()) return;

        ArrayList<String> command = new ArrayList<>(e.getCommands());
        ArrayList<String> acceptedCommands = new ArrayList<>();
        ArrayList<Map<String, Object>> dbRes1 = null;

        try {
            dbRes1 = Database.getInstance().execute("SELECT * FROM commandWhitelist", null);
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
        for(var i : dbRes1 ) {
            acceptedCommands.add((String) i.get("name"));
        }

        for(String i: command ){
            if(!acceptedCommands.contains(i)) {
                e.getCommands().remove(i);
            }
        }


    }

    @EventHandler(priority =  EventPriority.HIGHEST)
    public void onCommandPreProcess(PlayerCommandPreprocessEvent e) throws SQLException {
        if(e.getPlayer().isOp()) return;
        ArrayList<Object> q = new ArrayList<>();
        q.add(
                Arrays.asList(e.getMessage().replace("/", "").split(" ")).get(0)
        );
        if( 1 > (Integer) new ArrayList<>(Database.getInstance().execute("SELECT EXISTS(SELECT * FROM commandWhitelist WHERE name = ? )", q).get(0).values()).get(0)) {
            e.setCancelled(true);
            e.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', "&6 ● &f해당 명령어는 없거나 사용이 제한되어 있습니다."));
        }



    }

//    public EventListener() {
//        ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(
//                PICOSERVER.getInstance(),
//                ListenerPriority.NORMAL,
//                PacketType.Play.Server.COMMANDS
//        ) {
//            @Override
//            public void onPacketSending(PacketEvent event) {
//                ArrayList<Object> a = new ArrayList<>();
//                Bukkit.getLogger().info(String.valueOf(event.getPacket().get()));
//                event.setCancelled(true);
////                super.onPacketSending(event);
//            }
//        });
//    }
}
