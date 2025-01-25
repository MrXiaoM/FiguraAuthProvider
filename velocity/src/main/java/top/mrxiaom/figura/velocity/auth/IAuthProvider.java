package top.mrxiaom.figura.velocity.auth;

import com.velocitypowered.api.proxy.Player;

import java.util.List;

public interface IAuthProvider {
    void removePlayerNotLogin(List<Player> players);
}
