package top.mrxiaom.figura.bungee.auth;

import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.List;

public interface IAuthProvider {
    void removePlayerNotLogin(List<ProxiedPlayer> players);
}
