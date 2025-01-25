package top.mrxiaom.figura.velocity;

import com.google.inject.Inject;
import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.scheduler.ScheduledTask;
import org.slf4j.Logger;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Plugin(
        id = "figuraauthprovider",
        name = "FiguraAuthProvider",
        version = BuildConstants.VERSION,
        authors = {"MrXiaoM"}
)
public class PluginMain {
    @Inject
    private ProxyServer server;
    @Inject
    private Logger logger;
    @Inject @DataDirectory
    private Path dataFolder;
    String apiAddress;
    ScheduledTask task;
    boolean logSubmitMsg;

    public File getDataFolder() {
        return dataFolder.toFile();
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        server.getEventManager().register(this, new PlayerEvents(this));
        CommandManager commandManager = server.getCommandManager();
        new ReloadCommand(this, commandManager);
    }

    @Subscribe
    public void onProxyShutdown(ProxyShutdownEvent event) {
        if (task != null) {
            task.cancel();
            task = null;
        }
    }

    public String getUrl(String path) {
        return apiAddress + path;
    }

    public void sendCurrentPlayerListAsync() {
        server.getScheduler().buildTask(this, this::sendCurrentPlayerList).schedule();
    }

    public void sendCurrentPlayerList() {
        Set<String> players = new HashSet<>();
        for (Player player : server.getAllPlayers()) {
            if (player.isActive()) {

                players.add(player.getUsername() + ":" + player.getUniqueId().toString());
            }
        }
        String message = String.join(",", players);
        String url = getUrl("/pushPlayerList");
        try {
            if (logSubmitMsg) logger.info("正在推送玩家列表到 {}: {}", url, message);
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
                logger.warn("推送失败 {}: {}", status, conn.getResponseMessage());
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
                InputStream input = getClass().getClassLoader().getResourceAsStream("config-velocity.yml");
                if (input != null) try (InputStream in = input) {
                    Files.copy(in, configFile.toPath());
                }
            }
            CommentedConfigurationNode config = load(configFile);
            String apiUrl = config.node("api-url").getString("http://127.0.0.1:5009");
            this.logSubmitMsg = config.node("log-submit-message").getBoolean(false);
            this.apiAddress = apiUrl.endsWith("/") ? apiUrl.substring(0, apiUrl.length() - 1) : apiUrl;
            if (task != null) {
                task.cancel();
                task = null;
            }
            int timeSeconds = config.node("schedule-send-player-list-time-seconds").getInt(-1);
            if (timeSeconds > 0) {
                task = server.getScheduler()
                        .buildTask(this, this::sendCurrentPlayerList)
                        .delay(timeSeconds, TimeUnit.SECONDS)
                        .repeat(timeSeconds, TimeUnit.SECONDS)
                        .schedule();
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
        logger.warn(sw.toString());
    }

    public static CommentedConfigurationNode load(File file) {
        try {
            return YamlConfigurationLoader.builder()
                    .source(() -> new BufferedReader(new FileReader(file, StandardCharsets.UTF_8)))
                    .build().load();
        } catch (IOException e) {
            return CommentedConfigurationNode.root();
        }
    }
}
