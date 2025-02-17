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
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.Location;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MedicAbility2 extends Cooldown implements Ability, Listener {
    private final Plugin plugin;
    private final GlintSMP mainClass;
    private final FileConfiguration shardData;
    private final Map<UUID, Long> cools = new HashMap<>();
    private final Map<UUID, Location> meditationStartLocations = new HashMap<>();
    private final Set<UUID> interrupted = new HashSet<>();
    private final Set<UUID> isMeditating = new HashSet<>();

    public MedicAbility2(Plugin plugin, GlintSMP mainClass) {
        super();
        this.plugin = plugin;
        this.mainClass = mainClass;
        this.shardData = plugin.getConfig();

        this.cooldownSeconds = 5;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    public int getShards(UUID playerID) {
        return this.shardData.getInt(playerID.toString(), 0);
    }

    public void execute(Player player) {
        player.sendMessage(ChatColor.BLUE + "[GlintSMP] " + ChatColor.GREEN + "You have started meditating. Do not move more than half a block or take damage!");

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

        meditationStartLocations.put(player.getUniqueId(), player.getLocation());

        isMeditating.add(player.getUniqueId());
        new BukkitRunnable() {
            int timeLeft = meditationTime;
            double angle = 0;

            @Override
            public void run() {
                if (player.isDead() || interrupted.contains(player.getUniqueId())) {
                    bossBar.removePlayer(player);
                    player.sendMessage(ChatColor.BLUE + "[GlintSMP] " + ChatColor.RED + "Meditation Ended!");
                    meditationStartLocations.remove(player.getUniqueId());
                    isMeditating.remove(player.getUniqueId());
                    cancel();
                    return;
                }

                bossBar.setTitle(ChatColor.GREEN + "Meditation Time: " + timeLeft + "s");
                bossBar.setProgress((double) timeLeft / meditationTime);

                if (player.getHealth() == player.getMaxHealth()) {
                    if (player.getAbsorptionAmount() < 15) {
                        player.setAbsorptionAmount(player.getAbsorptionAmount() + 2);
                    }
                } else {
                    player.setHealth(player.getHealth() + 2);
                }

                for (int i = 0; i < 8; i++) {
                    double x = Math.cos(angle + (i * Math.PI / 4)) * 1;
                    double z = Math.sin(angle + (i * Math.PI / 4)) * 1;
                    Location particleLoc = player.getLocation().clone().add(x, 1, z);
                    player.getWorld().spawnParticle(Particle.VILLAGER_HAPPY, particleLoc, 1, 0, 0, 0, 0);
                }
                angle += Math.PI / 16;

                timeLeft--;
                if (timeLeft <= 0) {
                    bossBar.removePlayer(player);
                    player.sendMessage(ChatColor.BLUE + "[GlintSMP] " + ChatColor.GREEN + "Meditation Completed Successfully!");
                    meditationStartLocations.remove(player.getUniqueId());
                    isMeditating.remove(player.getUniqueId());
                    cancel();
                }
            }
        }.runTaskTimer(plugin, 0, 20);
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if(isMeditating.contains(player.getUniqueId())) {
            Location startLocation = meditationStartLocations.get(player.getUniqueId());
            if (startLocation != null && Objects.requireNonNull(event.getTo()).distance(startLocation) > 1) {
                interrupted.add(player.getUniqueId());
            }
        }
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player player) {
            interrupted.add(player.getUniqueId());
        }
    }

    public String displayName() {
        return "Meditation";
    }
}
