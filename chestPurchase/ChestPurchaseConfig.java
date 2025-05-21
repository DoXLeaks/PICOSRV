package kr.rth.picoserver.chestPurchase;

import kr.rth.picoserver.util.SimpleConfig;
import org.bukkit.Material;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.util.List;

public class ChestPurchaseConfig extends SimpleConfig {

    public ChestPurchaseConfig(JavaPlugin plugin, String fileName) {
        super(plugin, fileName);
    }

    public List<String> getPurchasableWorlds() {
        return config.getStringList("purchasable-worlds");
    }

    public ItemStack getPurchasingItem() {
        return config.getItemStack("purchasing-item");
    }

    public Material getTriggerBlock() {
        return Material.valueOf(config.getString("trigger-block"));
    }

    public Material getSideBlock() {
        return Material.valueOf(config.getString("side-block"));
    }

    public void setPurchasingItem(ItemStack itemStack) {
        config.set("purchasing-item", itemStack);
        save();
    }

    public void reload() throws IOException, InvalidConfigurationException {
        config.load(configFile);
    }
}
