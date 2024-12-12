package top.mrxiaom.figura.authprovider.server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import org.bukkit.entity.Player;
import top.mrxiaom.figura.authprovider.PluginMain;
import top.mrxiaom.figura.authprovider.auth.IAuthProvider;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.logging.Logger;

import static top.mrxiaom.figura.authprovider.PluginMain.warn;

public class HttpAdapter {
    HttpServer server;
    Logger logger = Logger.getLogger("FiguraHttp");
    Map<String, String> bungeePlayers = new HashMap<>();
    public HttpAdapter(PluginMain plugin, String host, int port) throws IOException {
        InetSocketAddress address = new InetSocketAddress(host, port);
        server = HttpServer.create(address, 4096);
        createContext("/hasJoined", exchange -> {
            Map<String, String> query = parseQuery(exchange);
            int responseCode;
            String responseMsg;

            // String serverId = query.get("serverId");
            String userName = query.get("username");
            Player player = PluginMain.getOnlinePlayer(userName);
            if (player != null) {
                IAuthProvider provider = plugin.getAuthProvider();
                if (provider != null && !provider.hasLogon(player)) {
                    responseCode = 403;
                    responseMsg = "{\"msg\":\"玩家 " + userName + " 还没有登录\"}";
                } else {
                    responseCode = 200;
                    responseMsg = "{\"id\":\"" + player.getUniqueId() + "\"}";
                }
            } else {
                String uuid = bungeePlayers.get(userName);
                if (uuid != null) {
                    responseCode = 200;
                    responseMsg = "{\"id\":\"" + uuid + "\"}";
                } else {
                    responseCode = 403;
                    responseMsg = "{\"msg\":\"玩家 " + userName + " 不在线\"}";
                }
            }

            byte[] response = responseMsg.getBytes();
            exchange.getResponseHeaders().add("Content-Type", "application/json; charset=utf-8");
            exchange.sendResponseHeaders(responseCode, response.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response);
            }
        });
        createContext("/pushPlayerList", exchange -> {
            if (!exchange.getRequestMethod().equals("POST")) {
                byte[] response = "{\"msg\":\"require post\"}".getBytes();
                exchange.getResponseHeaders().add("Content-Type", "application/json; charset=utf-8");
                exchange.sendResponseHeaders(400, response.length);
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(response);
                }
                return;
            }
            try (InputStream input = exchange.getRequestBody();
                 InputStreamReader reader = new InputStreamReader(input, StandardCharsets.UTF_8)) {
                StringBuilder sb = new StringBuilder();
                char[] buffer = new char[1024];
                int len;
                while ((len = reader.read(buffer)) != -1) {
                    sb.append(buffer, 0, len);
                }
                String string = sb.toString();
                bungeePlayers.clear();
                String[] split = string.split(",");
                for (String s : split) {
                    String[] strings = s.split(":", 2);
                    if (strings.length != 2) continue;
                    bungeePlayers.put(split[0], split[1]);
                }
            }
            byte[] response = "{\"msg\":\"ok\"}".getBytes();
            exchange.getResponseHeaders().add("Content-Type", "application/json; charset=utf-8");
            exchange.sendResponseHeaders(200, response.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response);
            }
        });
        server.start();
        plugin.getLogger().info("验证服务已在 " + host + ":" + port + " 启动");
    }
    public interface Handler {
        void run(HttpExchange exchange) throws Throwable;
    }
    private void createContext(String path, Handler handler) {
        server.createContext(path, exchange -> {
            try {
                handler.run(exchange);
            } catch (Throwable t) {
                warn(logger, t);
            }
        });
    }

    private Map<String, String> parseQuery(HttpExchange exchange) {
        Map<String, String> map = new HashMap<>();
        String query = exchange.getRequestURI().getRawQuery();
        if (query != null) {
            String[] split = query.split("&");
            for (String str : split) {
                String[] split1 = str.split("=", 2);
                if (split1.length == 1) {
                    map.put(split1[0], "");
                } else {
                    map.put(split1[0], URLDecoder.decode(split1[1], StandardCharsets.UTF_8));
                }
            }
        }
        return map;
    }

    public void close() {
        if (server != null) {
            server.stop(0);
        }
    }
}
