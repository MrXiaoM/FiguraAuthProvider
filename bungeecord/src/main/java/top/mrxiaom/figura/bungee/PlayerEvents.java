package top.mrxiaom.figura.bungee;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ServerConnectedEvent;
import net.md_5.bungee.api.event.ServerDisconnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

public class PlayerEvents implements Listener {
    PluginMain plugin;
    public PlayerEvents(PluginMain plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onDisconnect(ServerDisconnectEvent event) {
        plugin.sendCurrentPlayerList();
    }

    @EventHandler
    public void onConnected(ServerConnectedEvent event) {
        plugin.sendCurrentPlayerList();
    }
}
