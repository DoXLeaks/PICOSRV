package kr.rth.picoserver.chestPurchase;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class ChestPurchaseCommand implements CommandExecutor {

    ChestPurchaseConfig chestPurchaseConfig;

    public ChestPurchaseCommand(ChestPurchaseConfig chestPurchaseConfig) {
        this.chestPurchaseConfig = chestPurchaseConfig;
    }


    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!sender.isOp()) return false;
        if (args.length == 1) {
            if (args[0].equals("리로드")) {
                try {
                    chestPurchaseConfig.reload();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                sender.sendMessage("[§6ChestPurchase§r] 리로드했습니다.");
                return true;
            }
            if (args[0].equals("설정")) {
                if (!(sender instanceof Player)) {
                    sender.sendMessage("[§6ChestPurchase§r] 플레이어만 해당 명령어를 사용할 수 있습니다.");
                    return true;
                }
                else {
                    ItemStack itemStack = ((Player) sender).getInventory().getItemInMainHand().clone();
                    if (itemStack.getType() == Material.AIR) {
                        sender.sendMessage("[§6ChestPurchase§r] 손에 아이템을 들어주세요.");
                        return true;
                    } else {
                        itemStack.setAmount(1);
                        chestPurchaseConfig.setPurchasingItem(itemStack);
                        sender.sendMessage("[§6ChestPurchase§r] 설정되었습니다.");
                        return true;
                    }
                }
            }
            if (args[0].equals("지급")) {
                if (!(sender instanceof Player)) {
                    sender.sendMessage("[§6ChestPurchase§r] 플레이어만 해당 명령어를 사용할 수 있습니다.");
                    return true;
                }
                ItemStack item = chestPurchaseConfig.getPurchasingItem();
                if (item == null) {
                    sender.sendMessage("[§6ChestPurchase§r] 아이템이 설정되어 있지 않습니다.");
                    return true;
                } else {
                    ((Player) sender).getInventory().addItem(item);
                    sender.sendMessage("[§6ChestPurchase§r] 지급되었습니다.");
                    return true;
                }
            }
        }
        sender.sendMessage("[§6ChestPurchase§r] /상자구매 리로드 - config를 리로드합니다.");
        sender.sendMessage("[§6ChestPurchase§r] /상자구매 설정 - 손에 든 아이템을 구매 아이템으로 지정합니다.");
        sender.sendMessage("[§6ChestPurchase§r] /상자구매 지급 - 구매 아이템을 자기자신에게 지급합니다.");
        return true;
    }
}
