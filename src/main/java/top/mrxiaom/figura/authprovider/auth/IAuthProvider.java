package top.mrxiaom.figura.authprovider.auth;

import org.bukkit.entity.Player;

public interface IAuthProvider {
    boolean hasLogon(Player player);
}
