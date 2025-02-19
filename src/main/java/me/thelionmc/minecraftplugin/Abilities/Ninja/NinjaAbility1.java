package me.thelionmc.minecraftplugin.Abilities.Ninja;

import me.thelionmc.minecraftplugin.Abilities.Ability;
import me.thelionmc.minecraftplugin.Abilities.Cooldown;
import me.thelionmc.minecraftplugin.ShardManager;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class NinjaAbility1 extends Cooldown implements Ability {
    Map<UUID, Long> cools = new HashMap<>();
    ShardManager shardManager;
    Plugin plugin;

    public NinjaAbility1(ShardManager shardManager, Plugin plugin) {
        super();
        this.cooldownSeconds = 120; // Set custom cooldown for Assassin Ability 1

        this.shardManager = shardManager;
        this.plugin = plugin;
    }

    public void execute(Player player) {
        AttributeInstance movementSpeed = player.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED);
        AttributeInstance gravity = player.getAttribute(Attribute.GENERIC_GRAVITY);
        AttributeInstance fallDamage = player.getAttribute(Attribute.GENERIC_FALL_DAMAGE_MULTIPLIER);

        new BukkitRunnable() {
            int ticks = 0;
            @Override
            public void run() {
                assert movementSpeed != null;
                movementSpeed.setBaseValue(1.5);
                assert gravity != null;
                gravity.setBaseValue(0.08);
                assert fallDamage != null;
                fallDamage.setBaseValue(0);

                Vector vector = new Vector();
                vector.setX(player.getVelocity().getX());
                vector.setY(0.42);
                vector.setZ(player.getVelocity().getZ());

                if(player.getWorld().getBlockAt(new Location(player.getWorld(), player.getLocation().getBlockX(), player.getLocation().getBlockY() - 1, player.getLocation().getBlockZ())).getType() != Material.AIR) {
                    player.setVelocity(vector);
                }

                ticks++;

                if(ticks > shardManager.getShards(player.getUniqueId()) * 20) {
                    movementSpeed.setBaseValue(0.7);
                    gravity.setBaseValue(0.08);
                    fallDamage.setBaseValue(1);
                    cancel();
                }
            }
        }.runTaskTimer(plugin, 0, 1);
    }

    public String abilityName() {return "Moon Hop";}
}
