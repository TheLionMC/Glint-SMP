package me.thelionmc.minecraftplugin.Abilities.Mischief;

import me.thelionmc.minecraftplugin.Abilities.Ability;
import me.thelionmc.minecraftplugin.Abilities.Cooldown;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class MischiefAbility2 extends Ability {
    Map<UUID, Long> cools = new HashMap<>();

    public MischiefAbility2() {
        super();
        this.cooldownSeconds = 180; // Set custom cooldown for Mischief Ability 2
    }

    public boolean execute(Player player) {
        Location location = player.getLocation();
        for (int i = 0; i < 50; i++) {
            Bukkit.getScheduler().runTaskLater(
                    Objects.requireNonNull(Bukkit.getPluginManager().getPlugin("GlintSMP")),
                    () -> player.getWorld().spawnParticle(Particle.LARGE_SMOKE, player.getLocation(), 100, 3, 3, 3, 0.2),
                    i * 2L
            );
        }

        int radius = 5;
        player.getWorld().getNearbyEntities(location, radius, radius, radius).forEach(entity -> {
            if (entity instanceof Player && entity != player) {
                ((Player) entity).addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 10*20, 1));
            }
        });

        return true;
    }

    public String abilityName() {
        return "Smoke Bomb";
    }
}