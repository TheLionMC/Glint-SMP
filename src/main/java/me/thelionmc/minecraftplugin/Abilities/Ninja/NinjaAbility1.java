package me.thelionmc.minecraftplugin.Abilities.Ninja;

import me.thelionmc.minecraftplugin.Abilities.Ability;
import me.thelionmc.minecraftplugin.Abilities.Cooldown;
import me.thelionmc.minecraftplugin.ShardManager;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class NinjaAbility1 extends Ability {
    Map<UUID, Long> cools = new HashMap<>();
    ShardManager shardManager;
    Plugin plugin;

    public NinjaAbility1(ShardManager shardManager, Plugin plugin) {
        super();
        this.cooldownSeconds = 120; // Set custom cooldown for Ninja Ability 1
        this.shardManager = shardManager;
        this.plugin = plugin;
    }

    public boolean execute(Player player) {
        AttributeInstance fallDamage = player.getAttribute(Attribute.GENERIC_FALL_DAMAGE_MULTIPLIER);
        assert fallDamage != null;
        fallDamage.setBaseValue(0);
        Material block = player.getLocation().subtract(0, 1, 0).getBlock().getType();

        int shardCount = shardManager.getShards(player.getUniqueId());
        double forwardPower = 1.5 + (0.5 * shardCount);
        double yv = 1.5 + (0.3 * shardCount);

        Vector direction = player.getLocation().getDirection().normalize();
        direction.multiply(forwardPower);
        direction.setY(yv);
        player.setVelocity(direction);

        new BukkitRunnable() {
            @Override
            public void run() {
                if (player.isOnline() || !block.equals(Material.AIR)) {
                    fallDamage.setBaseValue(1);
                }
            }
        }.runTaskLater(plugin, 5);

        return true;
    }

    public String abilityName() {
        return "Dash";
    }
}
