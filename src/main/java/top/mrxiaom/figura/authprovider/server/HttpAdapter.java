package top.mrxiaom.figura.authprovider.server;

import com.sun.net.httpserver.HttpServer;
import org.bukkit.entity.Player;
import top.mrxiaom.figura.authprovider.PluginMain;
import top.mrxiaom.figura.authprovider.auth.IAuthProvider;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import static top.mrxiaom.figura.authprovider.PluginMain.warn;

public class HttpAdapter {
    HttpServer server;
    Logger logger = Logger.getLogger("FiguraHttp");
    public HttpAdapter(PluginMain plugin, String host, int port) throws IOException {
        InetSocketAddress address = new InetSocketAddress(host, port);
        server = HttpServer.create(address, 4096);
        server.createContext("/hasJoined", exchange -> {
            try {
                Map<String, String> query = parseQuery(exchange.getRequestURI().getRawQuery());
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
                    responseCode = 403;
                    responseMsg = "{\"msg\":\"玩家 " + userName + " 不在线\"}";
                }

                byte[] response = responseMsg.getBytes();
                exchange.getResponseHeaders().add("Content-Type", "application/json; charset=utf-8");
                exchange.sendResponseHeaders(responseCode, response.length);
                OutputStream os = exchange.getResponseBody();
                os.write(response);
                os.close();
            } catch (Throwable t) {
                warn(logger, t);
            }
        });
        server.start();
        plugin.getLogger().info("验证服务已在 " + host + ":" + port + " 启动");
    }

    private Map<String, String> parseQuery(String query) {
        Map<String, String> map = new HashMap<>();
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
