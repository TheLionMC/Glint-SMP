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
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MedicAbility2 extends Cooldown implements Ability, Listener {
    private final Plugin plugin;
    private GlintSMP mainClass;
    private FileConfiguration shardData;
    private Map<UUID, Long> cools = new HashMap<>();

    public MedicAbility2(Plugin plugin, GlintSMP mainClass) {
        super();

        this.plugin = plugin;
        this.mainClass = mainClass;

        System.out.println(plugin == null ? "a null" : "a non-null");
        this.shardData = plugin.getConfig();
        this.cooldownSeconds = 180;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    public int getShards(UUID playerID) {
        return this.shardData.getInt(playerID.toString(), 0);
    }

    public void execute(Player player) {

        player.sendMessage(ChatColor.BLUE + "[GlintSMP] " + ChatColor.GREEN + "You have started meditating. Do not move or take damage!");

        int shards = getShards(player.getUniqueId());
        int meditationTime = Math.min(30, Math.max(10, shards));
        double shardMultiplier = 0.5;

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
                if (player.isDead() || player.hasMetadata("meditation_interrupted")) {
                    bossBar.removePlayer(player);
                    player.sendMessage(ChatColor.BLUE + "[GlintSMP] " + ChatColor.RED + "Meditation Ended!");
                    player.removeMetadata("meditation_interrupted", plugin);
                    cancel();
                    return;
                }

                bossBar.setTitle(ChatColor.GREEN + "Meditation Time: " + timeLeft + "s");
                bossBar.setProgress((double) timeLeft / meditationTime);

                double healAmount = Math.min(shards * shardMultiplier, player.getMaxHealth() - player.getHealth());
                player.setHealth(player.getHealth() + healAmount);
                player.getWorld().spawnParticle(Particle.HEART, player.getLocation(), 5);

                double absorptionToAdd = Math.min(15 - player.getAbsorptionAmount(), shards * shardMultiplier);
                player.setAbsorptionAmount(player.getAbsorptionAmount() + absorptionToAdd);

                timeLeft--;
                if (timeLeft <= 0) {
                    bossBar.removePlayer(player);
                    player.sendMessage(ChatColor.BLUE + "[GlintSMP] " + ChatColor.GREEN + "Meditation Completed Successfully!");
                    cancel();
                }
            }
        }.runTaskTimer(plugin, 0, 20);
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if (!event.getFrom().getBlock().equals(event.getTo().getBlock())) {
            event.getPlayer().setMetadata("meditation_interrupted", new org.bukkit.metadata.FixedMetadataValue(plugin, true));
        }
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player player) {
            player.setMetadata("meditation_interrupted", new org.bukkit.metadata.FixedMetadataValue(plugin, true));
        }
    }

    public String displayName() {
        return "Ability 2 Medic Class";
    }
}