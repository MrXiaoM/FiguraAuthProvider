package top.mrxiaom.figura.bungee;

import net.md_5.bungee.api.event.*;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class PlayerEvents implements Listener {
    PluginMain plugin;
    public PlayerEvents(PluginMain plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onDisconnect(ServerDisconnectEvent event) {
        plugin.sendCurrentPlayerListAsync();
    }

    @EventHandler
    public void onSwitchServer(ServerSwitchEvent event) {
        plugin.sendCurrentPlayerListAsync();
    }

    @EventHandler
    public void onPlayerDisconnect(PlayerDisconnectEvent event) {
        plugin.sendCurrentPlayerListAsync();
    }

    @EventHandler
    public void onConnected(ServerConnectedEvent event) {
        plugin.sendCurrentPlayerListAsync();
    }

    @EventHandler
    public void onPostLogin(PostLoginEvent event) {
        plugin.sendCurrentPlayerListAsync();
    }
}
