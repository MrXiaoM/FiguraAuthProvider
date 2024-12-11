package top.mrxiaom.figura.authprovider;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import top.mrxiaom.figura.authprovider.auth.AuthMeProvider;
import top.mrxiaom.figura.authprovider.auth.IAuthProvider;
import top.mrxiaom.figura.authprovider.server.HttpAdapter;

import java.io.IOException;

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
        try {
            adapter = new HttpAdapter(this, "0.0.0.0", 5009);
        } catch (IOException e) {
            e.printStackTrace();
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
    }

    public IAuthProvider getAuthProvider() {
        return authProvider;
    }

    public static Player getOnlinePlayer(String name) {
        if (name == null || name.isEmpty()) return null;
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.getName().equals(name)) return player;
        }
        return null;
    }
}
