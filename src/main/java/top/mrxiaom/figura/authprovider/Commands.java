package top.mrxiaom.figura.authprovider;

import org.bukkit.command.*;
import org.jetbrains.annotations.NotNull;

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
        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        return List.of();
    }
}
