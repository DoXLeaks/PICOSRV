package kr.rth.picoserver.autoHomeBuy;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class HomeDataContainer {
    private final HomePurchaseConfig config;

    private List<String> worlds;

    private ItemStack purchasingItem;

    private List<Material> triggerBlocks;

    private final ArrayList<UUID> cachedBought = new ArrayList<>();

    public HomeDataContainer(HomePurchaseConfig config) {
        this.config = config;
        initialize();
    }

    public List<String> getWorlds() {
        return worlds;
    }

    public ItemStack getPurchasingItem() {
        return purchasingItem;
    }

    public List<Material> getTriggerBlocks() {
        return triggerBlocks;
    }

    public void setPurchasingItem(ItemStack itemStack) {
        purchasingItem = itemStack;
        config.setPurchasingItem(itemStack);
    }

    public boolean isCached(Player player) {
        return cachedBought.contains(player.getUniqueId());
    }

    public void cache(Player player) {
        cachedBought.add(player.getUniqueId());
    }

    public void clearCache() {
        cachedBought.clear();
    }

    private void initialize() {
        worlds = config.getPurchasableWorlds();
        purchasingItem = config.getPurchasingItem();
        triggerBlocks = config.getTriggerBlocks();
    }

    public void reload() {
        try {
            config.reload();
            initialize();
        } catch (Exception ignored) {
        }
    }
}
