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

    public void sendCurrentPlayerList() {
        Set<String> players = new HashSet<>();
        for (ProxiedPlayer player : ProxyServer.getInstance().getPlayers()) {
            if (player.isConnected()) {
                players.add(player.getName() + ":" + player.getUniqueId().toString());
            }
        }
        String message = String.join(",", players);
        String url = plugin.getUrl("/pushPlayerList");
        try {
            HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setUseCaches(false);
            conn.connect();
            try (OutputStream output = conn.getOutputStream()) {
                try (PrintWriter pw = new PrintWriter(output)) {
                    pw.write(message);
                    pw.flush();
                }
            }
        } catch (IOException e) {
            plugin.warn(e);
        }
    }

    @EventHandler
    public void onDisconnect(ServerDisconnectEvent event) {
        sendCurrentPlayerList();
    }

    @EventHandler
    public void onConnected(ServerConnectedEvent event) {
        sendCurrentPlayerList();
    }
}
