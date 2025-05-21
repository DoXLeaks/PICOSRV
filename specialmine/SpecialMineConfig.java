package kr.rth.picoserver.specialmine;

import kr.rth.picoserver.util.SimpleConfig;
import org.bukkit.Location;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;

public class SpecialMineConfig extends SimpleConfig {

    public SpecialMineConfig(JavaPlugin plugin, String fileName) {
        super(plugin, fileName);
    }
    public Location getMineLocation() { return config.getLocation("mine-location"); }

    public void setMineLocation(Location location) {
        config.set("mine-location", location.clone());
        save();
    }

    public void reload() throws IOException, InvalidConfigurationException {
        config.load(configFile);
    }
}
