package pluginsfix.glowresistance.listener;

import io.papermc.paper.event.entity.EntityKnockbackEvent;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.AbstractWindCharge;
import org.bukkit.entity.BreezeWindCharge;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.WindCharge;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import pluginsfix.glowresistance.config.PluginConfig;
import pluginsfix.glowresistance.service.KnockbackCalculator;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class WindChargeKnockbackListener implements Listener {
    private static final Attribute EXPLOSION_RESISTANCE_ATTR = resolveExplosionResistanceAttribute();

    private final PluginConfig config;
    private final Map<UUID, Long> recentBoostedEntities = new ConcurrentHashMap<>();

    public WindChargeKnockbackListener(PluginConfig config) {
        this.config = config;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onProjectileHit(ProjectileHitEvent event) {
        if (!(event.getEntity() instanceof AbstractWindCharge windCharge)) {
            return;
        }

        handleWindChargeExplosion(windCharge);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onEntityExplode(EntityExplodeEvent event) {
        if (!(event.getEntity() instanceof AbstractWindCharge windCharge)) {
            return;
        }

        handleWindChargeExplosion(windCharge);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onEntityKnockback(EntityKnockbackEvent event) {
        if (event.getCause() != EntityKnockbackEvent.Cause.EXPLOSION) {
            return;
        }

        if (!(event.getEntity() instanceof LivingEntity living)) {
            return;
        }

        long now = System.currentTimeMillis();
        Long lastBoost = recentBoostedEntities.get(living.getUniqueId());
        if (lastBoost != null && (now - lastBoost) < 250) {
            return;
        }

        double resistance = calculateResistance(living);
        if (resistance <= 0.0) {
            return;
        }

        Vector currentKnockback = event.getKnockback();
        if (currentKnockback.lengthSquared() > 0.0001 && resistance < 0.999) {
            double multiplier = 1.0 / (1.0 - resistance);
            event.setKnockback(currentKnockback.multiply(multiplier));
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        if (!config.maceWindBurstEnabled()) {
            return;
        }

        if (!(event.getEntity() instanceof LivingEntity victim)) {
            return;
        }

        Entity damager = event.getDamager();
        if (!(damager instanceof Player attacker)) {
            return;
        }

        ItemStack mainHand = attacker.getInventory().getItemInMainHand();
        if (!mainHand.containsEnchantment(Enchantment.WIND_BURST)) {
            return;
        }

        int burstLevel = mainHand.getEnchantmentLevel(Enchantment.WIND_BURST);
        double resistance = calculateResistance(attacker);
        if (resistance > 0.0) {
            double upwardForce = (0.7 + (burstLevel * 0.35)) * config.verticalMultiplier();
            Vector current = attacker.getVelocity();
            attacker.setVelocity(new Vector(current.getX(), Math.max(current.getY(), upwardForce), current.getZ()));
            attacker.setFallDistance(0.0f);
            recentBoostedEntities.put(attacker.getUniqueId(), System.currentTimeMillis());
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        recentBoostedEntities.remove(event.getPlayer().getUniqueId());
    }

    private void handleWindChargeExplosion(AbstractWindCharge windCharge) {
        boolean isPlayerCharge = windCharge instanceof WindCharge;
        boolean isBreezeCharge = windCharge instanceof BreezeWindCharge;

        if (isPlayerCharge && !config.playerWindChargeEnabled()) {
            return;
        }
        if (isBreezeCharge && !config.breezeWindChargeEnabled()) {
            return;
        }

        double radius = isBreezeCharge ? 3.0 : 1.2;
        double baseScale = isBreezeCharge ? 0.6 : 1.22;

        Location center = windCharge.getLocation();
        if (center.getWorld() == null) {
            return;
        }

        Collection<Entity> nearby = center.getWorld().getNearbyEntities(center, radius * 2.5, radius * 2.5, radius * 2.5);
        long now = System.currentTimeMillis();

        for (Entity entity : nearby) {
            if (!(entity instanceof LivingEntity living)) {
                continue;
            }

            double resistance = calculateResistance(living);
            if (resistance <= 0.0) {
                continue;
            }

            Location entityLoc = living.getLocation();
            KnockbackCalculator.Vector3d calcVec = KnockbackCalculator.calculateWindChargeVector(
                center.getX(), center.getY(), center.getZ(),
                entityLoc.getX(), entityLoc.getY() + (living.getHeight() * 0.5), entityLoc.getZ(),
                radius,
                baseScale,
                config.knockbackMultiplier(),
                config.verticalMultiplier(),
                config.horizontalMultiplier()
            );

            if (calcVec.lengthSquared() <= 0.0001) {
                continue;
            }

            if (resistance >= 0.999) {
                Vector added = new Vector(calcVec.x(), calcVec.y(), calcVec.z());
                Vector current = living.getVelocity();
                living.setVelocity(current.add(added));

                if (living instanceof Player player) {
                    player.setFallDistance(0.0f);
                }
                recentBoostedEntities.put(living.getUniqueId(), now);
            }
        }
    }

    private double calculateResistance(LivingEntity entity) {
        double total = 0.0;

        if (config.ignoreExplosionKnockbackResistance() && EXPLOSION_RESISTANCE_ATTR != null) {
            AttributeInstance attr = entity.getAttribute(EXPLOSION_RESISTANCE_ATTR);
            if (attr != null && attr.getValue() > 0.0) {
                total += attr.getValue();
            }
        }

        if (config.ignoreBlastProtection()) {
            EntityEquipment equipment = entity.getEquipment();
            if (equipment != null) {
                for (ItemStack piece : equipment.getArmorContents()) {
                    if (piece != null && !piece.getType().isAir()) {
                        int level = piece.getEnchantmentLevel(Enchantment.BLAST_PROTECTION);
                        if (level > 0) {
                            total += level * 0.15;
                        }
                    }
                }
            }
        }

        return Math.min(1.0, total);
    }

    private static Attribute resolveExplosionResistanceAttribute() {
        for (String name : new String[]{"GENERIC_EXPLOSION_KNOCKBACK_RESISTANCE", "EXPLOSION_KNOCKBACK_RESISTANCE"}) {
            try {
                return Attribute.valueOf(name);
            } catch (IllegalArgumentException ignored) {
            }
        }
        return null;
    }
}
