package top.mrxiaom.figura.authprovider;

import org.bukkit.command.*;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class Commands implements CommandExecutor, TabCompleter {
    PluginMain plugin;
    private Commands(PluginMain plugin) {
        this.plugin = plugin;
    }

    protected static void register(PluginMain plugin) {
        PluginCommand command = plugin.getCommand("figuraauthprovider");
        if (command != null) {
            Commands instance = new Commands(plugin);
            command.setExecutor(instance);
            command.setTabCompleter(instance);
        }
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (args.length == 1 && "reload".equalsIgnoreCase(args[0]) && sender.isOp()) {
            plugin.reloadConfig();
            sender.sendMessage("&a配置文件已重载");
            return true;
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        return new ArrayList<>();
    }
}
