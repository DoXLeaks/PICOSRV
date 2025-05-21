package kr.rth.picoserver.autoHomeBuy;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class HomePurchaseTabCompleter implements TabCompleter {


    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!sender.isOp()) return new ArrayList<>();
        if (args.length == 1) {
            ArrayList<String> arr = new ArrayList<>();
            arr.add("reload");
            arr.add("clearcache");
            arr.add("setPurchasingItem");
            arr.add("givePurchasingItem");
            return arr;
        }
        return new ArrayList<>();
    }
}
