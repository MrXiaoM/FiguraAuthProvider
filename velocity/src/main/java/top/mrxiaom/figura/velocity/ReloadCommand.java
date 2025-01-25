package top.mrxiaom.figura.velocity;

import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public class ReloadCommand implements SimpleCommand {
    PluginMain plugin;
    public ReloadCommand(PluginMain plugin, CommandManager manager) {
        this.plugin = plugin;
        manager.register(manager.metaBuilder("velocityfiguraauthprovider")
                .aliases("velocityfap", "vfap")
                .plugin(this)
                .build(), this);
    }

    @Override
    public void execute(final Invocation invocation) {
        CommandSource sender = invocation.source();
        String[] args = invocation.arguments();
        if (sender.hasPermission("figura.auth.admin")) {
            if (args.length == 1 && args[0].equals("reload")) {
                plugin.reloadConfig();
                sender.sendMessage(Component.text("配置文件已重载").color(NamedTextColor.GREEN));
            }
        }
    }

    @Override
    public boolean hasPermission(final Invocation invocation) {
        return invocation.source().hasPermission("figura.auth.admin");
    }
}
