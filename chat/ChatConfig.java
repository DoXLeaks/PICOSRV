package kr.rth.picoserver.chat;

import kr.rth.picoserver.util.SimpleConfig;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.util.List;

public class ChatConfig extends SimpleConfig {

    public ChatConfig(JavaPlugin plugin, String fileName) {
        super(plugin, fileName);
    }

    public List<String> getBlackList() {
        return config.getStringList("blacklist");
    }

    public void reload() throws IOException, InvalidConfigurationException {
        config.load(configFile);
    }
}
