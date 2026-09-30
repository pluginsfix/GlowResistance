package pluginsfix.glowresistance.config;

import org.bukkit.configuration.file.FileConfiguration;

public record PluginConfig(
    boolean ignoreBlastProtection,
    boolean ignoreExplosionKnockbackResistance,
    double knockbackMultiplier,
    double verticalMultiplier,
    double horizontalMultiplier,
    boolean playerWindChargeEnabled,
    boolean breezeWindChargeEnabled,
    boolean maceWindBurstEnabled
) {
    public static PluginConfig fromYaml(FileConfiguration config) {
        boolean ignoreBlast = config.getBoolean("settings.ignore-blast-protection", true);
        boolean ignoreAttr = config.getBoolean("settings.ignore-explosion-knockback-resistance", true);
        double kbMul = Math.max(0.0, config.getDouble("settings.knockback-multiplier", 1.0));
        double vertMul = Math.max(0.0, config.getDouble("settings.vertical-multiplier", 1.0));
        double horizMul = Math.max(0.0, config.getDouble("settings.horizontal-multiplier", 1.0));
        boolean playerWind = config.getBoolean("settings.player-wind-charge-enabled", true);
        boolean breezeWind = config.getBoolean("settings.breeze-wind-charge-enabled", true);
        boolean maceWind = config.getBoolean("settings.mace-wind-burst-enabled", true);

        return new PluginConfig(
            ignoreBlast,
            ignoreAttr,
            kbMul,
            vertMul,
            horizMul,
            playerWind,
            breezeWind,
            maceWind
        );
    }
}
