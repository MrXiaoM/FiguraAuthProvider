package top.mrxiaom.figura.authprovider.perm;

import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.RegisteredServiceProvider;
import top.mrxiaom.figura.authprovider.PluginMain;

public class VaultProvider implements IPermissionProvider {
    private final Permission permission;
    public VaultProvider(Permission permission) {
        this.permission = permission;
    }

    public Boolean playerHas(String playerName, String permission) {
        OfflinePlayer player = PluginMain.getOfflinePlayer(playerName);
        return player == null ? null : this.permission.playerHas(null, player, permission);
    }

    public static VaultProvider create() {
        RegisteredServiceProvider<Permission> provider = Bukkit.getServicesManager().getRegistration(Permission.class);
        if (provider == null) return null;
        return new VaultProvider(provider.getProvider());
    }
}
