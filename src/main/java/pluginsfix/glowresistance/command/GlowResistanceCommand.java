package pluginsfix.glowresistance.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import pluginsfix.glowresistance.GlowResistance;
import pluginsfix.glowresistance.text.Messages;

import java.util.Collections;
import java.util.List;

public final class GlowResistanceCommand implements CommandExecutor, TabCompleter {
    private final GlowResistance plugin;
    private final Messages messages;

    public GlowResistanceCommand(GlowResistance plugin, Messages messages) {
        this.plugin = plugin;
        this.messages = messages;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission("glowresistance.use")) {
            messages.send(sender, "no-permission");
            return true;
        }

        if (args.length > 0 && args[0].equalsIgnoreCase("reload")) {
            if (!sender.hasPermission("glowresistance.admin.reload")) {
                messages.send(sender, "no-permission");
                return true;
            }

            plugin.reloadPlugin();
            messages.send(sender, "reloaded");
            return true;
        }

        messages.send(sender, "help");
        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 1 && sender.hasPermission("glowresistance.admin.reload")) {
            if ("reload".startsWith(args[0].toLowerCase())) {
                return List.of("reload");
            }
        }
        return Collections.emptyList();
    }
}
