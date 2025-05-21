package kr.rth.picoserver.autoHomeBuy;

import kr.rth.picoserver.util.SimpleConfig;
import org.bukkit.Material;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class HomePurchaseConfig extends SimpleConfig {

    public HomePurchaseConfig(JavaPlugin plugin, String fileName) {
        super(plugin, fileName);
    }

    public List<String> getPurchasableWorlds() {
        return config.getStringList("purchasable-worlds");
    }
    public ItemStack getPurchasingItem() {
        return config.getItemStack("purchasing-item");
    }

    public List<Material> getTriggerBlocks() {
        if (config.getStringList("trigger-blocks").isEmpty()) return new ArrayList<>();
        return config.getStringList("trigger-blocks").stream().map(Material::valueOf).collect(Collectors.toList());
    }

    public void setPurchasingItem(ItemStack itemStack) {
        config.set("purchasing-item", itemStack);
        save();
    }

    public void reload() throws IOException, InvalidConfigurationException {
        config.load(configFile);
    }
}
