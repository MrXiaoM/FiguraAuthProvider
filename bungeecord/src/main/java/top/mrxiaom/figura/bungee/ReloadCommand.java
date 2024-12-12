package top.mrxiaom.figura.bungee;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;

public class ReloadCommand extends Command {
    PluginMain plugin;
    public ReloadCommand(PluginMain plugin) {
        super("bungeefiguraauthprovider", "figura.auth.admin", "bungeefap", "bfap");
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (sender.hasPermission("figura.auth.admin")) {
            if (args.length == 1 && args[0].equals("reload")) {
                plugin.reloadConfig();
                sender.sendMessage(TextComponent.fromLegacy("§a配置文件已重载"));
            }
        }
    }
}
