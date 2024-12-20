package top.mrxiaom.figura.bungee;

import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Files;

public class PluginMain extends Plugin {
    private String apiAddress;
    @Override
    public void onEnable() {
        getProxy().getPluginManager().registerListener(this, new PlayerEvents(this));
        getProxy().getPluginManager().registerCommand(this, new ReloadCommand(this));
        reloadConfig();
    }

    public String getUrl(String path) {
        return apiAddress + path;
    }

    public void reloadConfig() {
        File configFile = new File(getDataFolder(), "config.yml");
        if (!getDataFolder().exists())
            getDataFolder().mkdir();

        try {
            if (!configFile.exists()) {
                try (InputStream in = getResourceAsStream("config-bungee.yml")) {
                    Files.copy(in, configFile.toPath());
                }
            }
            Configuration config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(configFile);
            String apiUrl = config.getString("api-url", "http://127.0.0.1:5009");
            this.apiAddress = apiUrl.endsWith("/") ? apiUrl.substring(0, apiUrl.length() - 1) : apiUrl;
        } catch (Throwable t) {
            warn(t);
        }
    }

    public void warn(Throwable t) {
        StringWriter sw = new StringWriter();
        try (PrintWriter pw = new PrintWriter(sw)) {
            t.printStackTrace(pw);
        }
        getLogger().warning(sw.toString());
    }
}
