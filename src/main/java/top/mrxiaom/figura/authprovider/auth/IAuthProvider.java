package top.mrxiaom.figura.authprovider.auth;

import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

public interface IAuthProvider extends Listener {
    boolean hasLogon(Player player);
}
