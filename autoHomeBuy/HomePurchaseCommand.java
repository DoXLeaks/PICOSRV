package kr.rth.picoserver.autoHomeBuy;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class HomePurchaseCommand implements CommandExecutor {

    HomeDataContainer homeDataContainer;

    public HomePurchaseCommand(HomeDataContainer homeDataContainer) {
        this.homeDataContainer = homeDataContainer;
    }


    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!sender.isOp()) return false;
        if (args.length == 1) {
            if (args[0].equals("reload")) {
                homeDataContainer.reload();
                sender.sendMessage("[§6PicoHouse§r] 리로드했습니다.");
                return true;
            }
            if (args[0].equals("clearcache")) {
                homeDataContainer.clearCache();
                sender.sendMessage("[§6PicoHouse§r] 구매 기록 캐시를 제거하였습니다.");
                return true;
            }
            if (args[0].equals("setPurchasingItem")) {
                if (!(sender instanceof Player)) {
                    sender.sendMessage("[§6PicoHouse§r] 플레이어만 해당 명령어를 사용할 수 있습니다.");
                    return true;
                }
                else {
                    ItemStack itemStack = ((Player) sender).getInventory().getItemInMainHand().clone();
                    if (itemStack.getType() == Material.AIR) {
                        sender.sendMessage("[§6PicoHouse§r] 손에 아이템을 들어주세요.");
                        return true;
                    } else {
                        itemStack.setAmount(1);
                        homeDataContainer.setPurchasingItem(itemStack);
                        sender.sendMessage("[§6PicoHouse§r] 설정되었습니다.");
                        return true;
                    }
                }
            }
            if (args[0].equals("givePurchasingItem")) {
                if (!(sender instanceof Player)) {
                    sender.sendMessage("[§6PicoHouse§r] 플레이어만 해당 명령어를 사용할 수 있습니다.");
                    return true;
                }
                ItemStack item = homeDataContainer.getPurchasingItem();
                if (item == null) {
                    sender.sendMessage("[§6PicoHouse§r] 아이템이 설정되어 있지 않습니다.");
                    return true;
                } else {
                    ((Player) sender).getInventory().addItem(item);
                    sender.sendMessage("[§6PicoHouse§r] 지급되었습니다.");
                    return true;
                }
            }
        }
        sender.sendMessage("[§6PicoHouse§r] /picohouse reload - config를 리로드합니다.");
        sender.sendMessage("[§6PicoHouse§r] /picohouse clearcache - 캐싱된 homebuylog를 제거합니다, SQL단에서 유저의 homebuylog를 수정했을 때 사용하세요.");
        sender.sendMessage("[§6PicoHouse§r] /picohouse setPurchasingItem - 손에 든 아이템을 구매 아이템으로 지정합니다.");
        sender.sendMessage("[§6PicoHouse§r] /picohouse givePurchasingItem - 구매 아이템을 자기자신에게 지급합니다.");
        return true;
    }
}
