package top.mrxiaom.figura.authprovider.auth;

import fr.xephi.authme.api.v3.AuthMeApi;
import org.bukkit.entity.Player;
import top.mrxiaom.figura.authprovider.PluginMain;

public class AuthMeProvider implements IAuthProvider {
    public AuthMeProvider(PluginMain plugin) {
        plugin.getLogger().info("使用 AuthMe 提供验证服务");
    }
    @Override
    public boolean hasLogon(Player player) {
        return AuthMeApi.getInstance().isAuthenticated(player);
    }
}
