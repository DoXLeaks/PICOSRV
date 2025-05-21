package kr.rth.picoserver.etc;

import com.google.gson.Gson;
import kr.rth.picoserver.Database;
import net.ess3.api.events.AfkStatusChangeEvent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class afk implements Listener, CommandExecutor {
    @EventHandler
    public void onafk(AfkStatusChangeEvent e) {
        if(!e.getController().getBase().getWorld().getName().equals("spawn")){
            return;
        }

        if(!e.getValue()) {
            return;
        }
            ArrayList<Map<String, Object>> dbres1 = null;
            try {
                dbres1 = Database.getInstance().execute("SELECT * FROM keyv WHERE name = 'afkpos'", null);
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
            if(dbres1.size() < 1){
//                Bukkit.mess
                return;
            }
            Gson gson = new Gson();
            Map<String, Object> a1 = new HashMap<>();
            a1 = gson.fromJson((String) dbres1.get(0).get("data"), a1.getClass());

            e.getController().getBase().teleport(
            Location.deserialize(a1)

            );


    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if(args.length == 1 && args[0].equals("setpos")) {
            Player p = (Player) sender;
            Gson gson = new Gson();
            ArrayList<Object> q=  new ArrayList<>();
            q.add(
                    gson.toJson(
                    p.getLocation().serialize()

                    )
            );
            q.add(q.get(0));
            try {
                Database.getInstance().execute("INSERT INTO keyv (name,data) VALUES('afkpos',?) ON DUPLICATE KEY UPDATE data = ?",q );
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            sender.sendMessage("done.");
        }
        return false;
    }
}
