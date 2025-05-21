package kr.rth.picoserver.etc;

import com.plotsquared.core.PlotAPI;
import com.plotsquared.core.player.PlotPlayer;
import kr.rth.picoserver.Database;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;

import static kr.rth.picoserver.util.numberWithComma.numberWithComma;
import static kr.rth.picoserver.util.transText.transText;

public class FameCommandListener implements CommandExecutor {

    private final PlotAPI api = new PlotAPI();

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        Player p = (Player) commandSender;
        UUID uuid = p.getUniqueId();
        PlotPlayer<?> plotPlayer = api.wrapPlayer(uuid);
        if (plotPlayer == null || plotPlayer.getPlots().isEmpty()) {
            p.sendMessage(transText("&f \uE138 &f당신은 보유 중인 농장이 없습니다"));
            return true;
        }
        ArrayList<Object> q = new ArrayList<>();
        q.add(uuid.toString());
        ArrayList<Map<String, Object>> dbRes = null;
        try {
            dbRes = Database.getInstance().execute("SELECT * FROM plot_fame WHERE uuid = ?", q);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        int amount = 0;
        if(!dbRes.isEmpty()) {
            amount = (int) dbRes.get(0).get("rate");
        }
        p.sendMessage(transText("&f \uE138 &f농장 명성은 [x]점 입니다.".replace("[x]", numberWithComma(amount))));

        return false;
    }
}
