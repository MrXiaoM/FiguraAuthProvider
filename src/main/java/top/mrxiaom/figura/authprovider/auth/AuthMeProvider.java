package top.mrxiaom.figura.authprovider.auth;

import fr.xephi.authme.api.v3.AuthMeApi;
import fr.xephi.authme.events.LoginEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import top.mrxiaom.figura.authprovider.PluginMain;

public class AuthMeProvider implements IAuthProvider {
    PluginMain plugin;
    public AuthMeProvider(PluginMain plugin) {
        this.plugin = plugin;
        plugin.getLogger().info("使用 AuthMe 提供验证服务");
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }
    @Override
    public boolean hasLogon(Player player) {
        return AuthMeApi.getInstance().isAuthenticated(player);
    }

    @EventHandler
    public void on(LoginEvent e) {
        Player player = e.getPlayer();
        plugin.customPayload(player, "figura:reconnect");
    }
}
