package top.mrxiaom.figura.bungee;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.scheduler.ScheduledTask;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class PluginMain extends Plugin {
    private String apiAddress;
    private ScheduledTask task;
    private boolean logSubmitMsg;
    @Override
    public void onEnable() {
        getProxy().getPluginManager().registerListener(this, new PlayerEvents(this));
        getProxy().getPluginManager().registerCommand(this, new ReloadCommand(this));
        reloadConfig();
    }

    @Override
    public void onDisable() {
        if (task != null) {
            task.cancel();
            task = null;
        }
    }

    public String getUrl(String path) {
        return apiAddress + path;
    }

    public void sendCurrentPlayerListAsync() {
        getProxy().getScheduler().runAsync(this, this::sendCurrentPlayerList);
    }

    public void sendCurrentPlayerList() {
        Set<String> players = new HashSet<>();
        for (ProxiedPlayer player : getProxy().getPlayers()) {
            if (player.isConnected()) {
                players.add(player.getName() + ":" + player.getUniqueId().toString());
            }
        }
        String message = String.join(",", players);
        String url = getUrl("/pushPlayerList");
        try {
            if (logSubmitMsg) getLogger().info("正在推送玩家列表到 " + url + ": " + message);
            HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setUseCaches(false);
            conn.connect();
            try (OutputStream output = conn.getOutputStream()) {
                try (PrintWriter pw = new PrintWriter(output)) {
                    pw.write(message);
                    pw.flush();
                }
            }
            int status = conn.getResponseCode();
            if (status != 200) {
                getLogger().warning("推送失败 " + status + ": " + conn.getResponseMessage());
            }
        } catch (IOException e) {
            warn(e);
        }
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
            this.logSubmitMsg = config.getBoolean("log-submit-message", false);
            this.apiAddress = apiUrl.endsWith("/") ? apiUrl.substring(0, apiUrl.length() - 1) : apiUrl;
            if (task != null) {
                task.cancel();
                task = null;
            }
            int timeSeconds = config.getInt("schedule-send-player-list-time-seconds", -1);
            if (timeSeconds > 0) {
                task = getProxy().getScheduler().schedule(this, this::sendCurrentPlayerList, timeSeconds, timeSeconds, TimeUnit.SECONDS);
            }
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
