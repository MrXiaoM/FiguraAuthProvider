package top.mrxiaom.figura.authprovider;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;
import top.mrxiaom.figura.authprovider.auth.AuthMeProvider;
import top.mrxiaom.figura.authprovider.auth.IAuthProvider;
import top.mrxiaom.figura.authprovider.server.HttpAdapter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.util.logging.Logger;

public class PluginMain extends JavaPlugin {
    HttpAdapter adapter = null;
    IAuthProvider authProvider = null;
    @Override
    public void onEnable() {
        if (hasPlugin("AuthMe")) {
            authProvider = new AuthMeProvider(this);
        }
        if (authProvider == null) {
            getLogger().warning("没有发现任何验证提供器，所有在线玩家都可通过验证");
        }
        Commands.register(this);
        reloadConfig();

    }

    @Override
    public void reloadConfig() {
        this.saveDefaultConfig();
        super.reloadConfig();
        FileConfiguration config = getConfig();
        String host = config.getString("host", "0.0.0.0");
        int port = config.getInt("port");
        try {
            if (adapter != null) {
                adapter.close();
            }
            adapter = new HttpAdapter(this, host, port);
        } catch (IOException e) {
            warn(getLogger(), e);
        }
    }

    private boolean hasPlugin(String plugin) {
        return Bukkit.getPluginManager().isPluginEnabled(plugin);
    }

    @Override
    public void onDisable() {
        if (adapter != null) {
            adapter.close();
        }
        if (authProvider != null) {
            HandlerList.unregisterAll(authProvider);
        }
    }

    public IAuthProvider getAuthProvider() {
        return authProvider;
    }

    public void customPayload(Player player, String id) {
        // TODO: 发送 CustomPayLoad 包
        if (!player.getListeningPluginChannels().contains(id)) {
            Class<? extends Player> clazz = player.getClass();
            try {
                Method method = clazz.getDeclaredMethod("addChannel", String.class);
                method.invoke(player, id);
            } catch (ReflectiveOperationException e) {
                warn(getLogger(), e);
                return;
            }
        }
        player.sendPluginMessage(this, id, new ByteArrayOutputStream().toByteArray());
    }

    public static void warn(Logger logger, Throwable t) {
        StringWriter sw = new StringWriter();
        try (PrintWriter pw = new PrintWriter(sw)) {
            t.printStackTrace(pw);
        }
        logger.warning(sw.toString());
    }
    public static Player getOnlinePlayer(String name) {
        if (name == null || name.isEmpty()) return null;
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.getName().equals(name)) return player;
        }
        return null;
    }
}
