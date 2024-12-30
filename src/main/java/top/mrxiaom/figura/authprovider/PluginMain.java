package top.mrxiaom.figura.authprovider;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import top.mrxiaom.figura.authprovider.auth.AuthMeProvider;
import top.mrxiaom.figura.authprovider.auth.IAuthProvider;
import top.mrxiaom.figura.authprovider.perm.IPermissionProvider;
import top.mrxiaom.figura.authprovider.perm.NoProvider;
import top.mrxiaom.figura.authprovider.perm.VaultProvider;
import top.mrxiaom.figura.authprovider.server.HttpAdapter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;
import java.util.logging.Logger;

public class PluginMain extends JavaPlugin implements Listener {
    HttpAdapter adapter = null;
    IAuthProvider authProvider = null;
    IPermissionProvider permProvider = null;
    private static Map<String, OfflinePlayer> playersByName = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
    @Override
    public void onEnable() {
        Bukkit.getMessenger().registerOutgoingPluginChannel(this, "figura:reconnect");
        if (hasPlugin("AuthMe")) {
            authProvider = new AuthMeProvider(this);
        }
        if (authProvider == null) {
            getLogger().warning("没有发现任何验证提供器，所有在线玩家都可通过验证");
        }
        if (hasPlugin("Vault")) {
            permProvider = VaultProvider.create();
            if (permProvider == null) {
                permProvider = NoProvider.INSTANCE;
                getLogger().warning("已安装 Vault 前置，但无法找到权限服务，无法与权限插件挂钩");
            }
        } else {
            permProvider = NoProvider.INSTANCE;
            getLogger().warning("未安装 Vault 前置，无法与权限插件挂钩");
        }
        Commands.register(this);
        reloadConfig();
        Bukkit.getPluginManager().registerEvents(this, this);
        Bukkit.getScheduler().runTaskAsynchronously(this, () -> {
            for (OfflinePlayer player : Bukkit.getOfflinePlayers()) {
                if (player != null) {
                    String name = player.getName();
                    if (name != null) {
                        playersByName.put(name, player);
                    }
                }
            }
        });
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        playersByName.put(player.getName(), player);
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

    @Nullable
    public IAuthProvider getAuthProvider() {
        return authProvider;
    }

    @NotNull
    public IPermissionProvider getPermProvider() {
        return permProvider;
    }

    public void requestReconnect(Player player) {
        customPayload(player, "figura:reconnect", new ByteArrayOutputStream().toByteArray());
    }

    public void customPayload(Player player, String id, byte[] bytes) {
        // 发送 CustomPayLoad 包
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
        player.sendPluginMessage(this, id, bytes);
    }

    public static void warn(Logger logger, Throwable t) {
        StringWriter sw = new StringWriter();
        try (PrintWriter pw = new PrintWriter(sw)) {
            t.printStackTrace(pw);
        }
        logger.warning(sw.toString());
    }
    @Nullable
    public static Player getOnlinePlayer(String name) {
        if (name == null || name.isEmpty()) return null;
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.getName().equals(name)) return player;
        }
        return null;
    }

    @Nullable
    public static OfflinePlayer getOfflinePlayer(String name) {
        return playersByName.get(name);
    }
}
