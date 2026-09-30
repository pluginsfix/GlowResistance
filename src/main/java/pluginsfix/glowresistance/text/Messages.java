package pluginsfix.glowresistance.text;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class Messages {
    private static final LegacyComponentSerializer SERIALIZER = LegacyComponentSerializer.builder()
        .character('&')
        .hexColors()
        .build();

    private final Plugin plugin;
    private final File messagesFile;
    private final Map<String, List<String>> messages = new HashMap<>();

    public Messages(Plugin plugin) {
        this.plugin = plugin;
        this.messagesFile = new File(plugin.getDataFolder(), "messages.yml");
        reload();
    }

    public void reload() {
        if (!messagesFile.exists()) {
            plugin.saveResource("messages.yml", false);
        }

        messages.clear();
        YamlConfiguration config = YamlConfiguration.loadConfiguration(messagesFile);

        for (String key : config.getKeys(false)) {
            if (config.isList(key)) {
                messages.put(key, config.getStringList(key));
            } else if (config.isString(key)) {
                String single = config.getString(key);
                if (single != null) {
                    messages.put(key, List.of(single));
                }
            }
        }
    }

    public void send(CommandSender sender, String key) {
        List<String> lines = messages.getOrDefault(key, Collections.emptyList());
        if (lines.isEmpty()) {
            return;
        }

        for (String line : lines) {
            dispatchLine(sender, line);
        }
    }

    private void dispatchLine(CommandSender sender, String rawLine) {
        if (rawLine.startsWith("[sound] ")) {
            if (sender instanceof Player player) {
                String soundName = rawLine.substring(8).trim();
                playSound(player, soundName);
            }
            return;
        }

        if (rawLine.startsWith("[actionbar] ")) {
            if (sender instanceof Player player) {
                String actionText = rawLine.substring(12);
                Component comp = parseComponent(actionText);
                player.sendActionBar(comp);
            }
            return;
        }

        String messageText = rawLine;
        if (rawLine.startsWith("[message] ")) {
            messageText = rawLine.substring(10);
        }

        Component comp = parseComponent(messageText);
        sender.sendMessage(comp);
    }

    private void playSound(Player player, String soundName) {
        try {
            Sound sound = Sound.valueOf(soundName.toUpperCase());
            player.playSound(player.getLocation(), sound, 1.0f, 1.0f);
        } catch (IllegalArgumentException ignored) {
        }
    }

    private Component parseComponent(String text) {
        String normalized = text.replace('§', '&');
        return SERIALIZER.deserialize(normalized).decoration(TextDecoration.ITALIC, false);
    }
}
