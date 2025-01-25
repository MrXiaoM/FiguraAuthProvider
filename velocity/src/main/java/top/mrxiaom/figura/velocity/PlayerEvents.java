package top.mrxiaom.figura.velocity;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.player.ServerConnectedEvent;

public class PlayerEvents {
    PluginMain plugin;
    public PlayerEvents(PluginMain plugin) {
        this.plugin = plugin;
    }

    @Subscribe
    public void onSwitchServer(ServerConnectedEvent event) {
        plugin.sendCurrentPlayerListAsync();
    }

    @Subscribe
    public void onPlayerDisconnect(DisconnectEvent event) {
        plugin.sendCurrentPlayerListAsync();
    }
}
