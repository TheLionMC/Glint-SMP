package me.thelionmc.minecraftplugin.Abilities.Medic;

import me.thelionmc.minecraftplugin.Abilities.Ability;
import me.thelionmc.minecraftplugin.Abilities.Cooldown;
import me.thelionmc.minecraftplugin.GlintSMP;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.boss.BarColor;
import org.bukkit.ChatColor;
import org.bukkit.Particle;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MedicAbility2 extends Cooldown implements Ability {
    Map<UUID, Long> cools = new HashMap<>();
    private Plugin plugin;
    private GlintSMP main;

    public MedicAbility2(Plugin plugin1, GlintSMP main) {
        super();
        this.plugin = plugin1;
        this.main = main;
        this.cooldownSeconds = 180;

    }

    private FileConfiguration shardData;
    public int getShards(UUID playerID) {
        return this.shardData.getInt(playerID.toString());
    }


    public void execute(Player player) {
        player.sendMessage(ChatColor.BLUE + "[GlintSMP] " + ChatColor.GREEN + "You have started meditating. Do not move or take damage!");

        int shards = getShards(player.getUniqueId());
        int meditationTime = Math.min(30, Math.max(10, shards));

        double shardMultiplier = 0.5;
        double maxHealth = player.getMaxHealth();
        double currentHealth = player.getHealth();

        BossBar bossBar = player.getServer().createBossBar(
                ChatColor.GREEN + "Meditation Time: " + meditationTime + "s",
                BarColor.GREEN,
                BarStyle.SEGMENTED_20
        );
        bossBar.setProgress(1.0);
        bossBar.addPlayer(player);

        new BukkitRunnable() {
            int timeLeft = meditationTime;

            @Override
            public void run() {
                bossBar.setTitle(ChatColor.GREEN + "Meditation Time: " + timeLeft + "s");
                bossBar.setProgress((double) timeLeft / meditationTime);

                if (currentHealth < maxHealth) {
                    double healAmount = Math.min(shards * shardMultiplier, maxHealth - currentHealth);
                    player.setHealth(currentHealth + healAmount);
                    player.getWorld().spawnParticle(Particle.HEART, player.getLocation(), 1);
                }

                if (player.getAbsorptionAmount() < 15) {
                    double absorptionToAdd = Math.min(15 - player.getAbsorptionAmount(), shards * shardMultiplier);
                    player.setAbsorptionAmount(player.getAbsorptionAmount() + absorptionToAdd);
                    player.getWorld().spawnParticle(Particle.HEART, player.getLocation(), 1);
                }

                timeLeft--;
                if (timeLeft <= 0 || player.isDead() || player.hasMetadata("meditation_interrupted")) {
                    bossBar.removePlayer(player);
                    player.sendMessage(ChatColor.BLUE + "[GlintSMP] " + ChatColor.RED + "Meditation Ended!");
                    cancel();
                }
            }
        }.runTaskTimer(plugin, 0, 20);
    }

    public String displayName() {
        return "Ability 2 Medic Class";
    }
}
