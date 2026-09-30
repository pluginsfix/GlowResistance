package pluginsfix.glowresistance;

import org.bukkit.command.PluginCommand;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;
import pluginsfix.glowresistance.command.GlowResistanceCommand;
import pluginsfix.glowresistance.config.PluginConfig;
import pluginsfix.glowresistance.listener.WindChargeKnockbackListener;
import pluginsfix.glowresistance.text.Messages;

public final class GlowResistance extends JavaPlugin {
    private PluginConfig config;
    private Messages messages;
    private WindChargeKnockbackListener listener;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        this.config = PluginConfig.fromYaml(getConfig());
        this.messages = new Messages(this);

        this.listener = new WindChargeKnockbackListener(this.config);
        getServer().getPluginManager().registerEvents(this.listener, this);

        PluginCommand command = getCommand("glowresistance");
        if (command != null) {
            GlowResistanceCommand executor = new GlowResistanceCommand(this, this.messages);
            command.setExecutor(executor);
            command.setTabCompleter(executor);
        }
    }

    @Override
    public void onDisable() {
        HandlerList.unregisterAll(this);
    }

    public void reloadPlugin() {
        reloadConfig();
        this.config = PluginConfig.fromYaml(getConfig());
        this.messages.reload();

        if (this.listener != null) {
            HandlerList.unregisterAll(this.listener);
        }
        this.listener = new WindChargeKnockbackListener(this.config);
        getServer().getPluginManager().registerEvents(this.listener, this);
    }

    public PluginConfig getPluginConfig() {
        return this.config;
    }

    public Messages getMessages() {
        return this.messages;
    }
}
