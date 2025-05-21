package kr.rth.picoserver.chestPurchase;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class ChestPurchaseTabCompleter implements TabCompleter {


    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!sender.isOp()) return new ArrayList<>();
        if (args.length == 1) {
            ArrayList<String> arr = new ArrayList<>();
            arr.add("리로드");
            arr.add("설정");
            arr.add("지급");
            return arr;
        }
        return new ArrayList<>();
    }
}
