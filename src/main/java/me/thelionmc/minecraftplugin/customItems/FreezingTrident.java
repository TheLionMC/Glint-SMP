package me.thelionmc.minecraftplugin.customItems;
import me.thelionmc.minecraftplugin.GlintSMP;
import org.bukkit.*;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.bukkit.Bukkit.*;

public class FreezingTrident implements Listener {
    private GlintSMP mainClass;
    private Plugin plugin;

    public Map<UUID, Long> cooldowns = new HashMap<>();
    private Map<UUID, BossBar> playerBossBars = new HashMap<>();

    public int cooldownSeconds = 150;
    int activeSeconds = 40;

    public boolean isActive(UUID uuid) {
        if(cooldowns.containsKey(uuid)) {
            long lastUse = cooldowns.get(uuid);
            int timeSinceLastUseSeconds = (int) (System.currentTimeMillis() - lastUse) / 1000;

            if(timeSinceLastUseSeconds < activeSeconds) {
                return true;
            }
        }
        return false;
    }

    public double percentActive(UUID uuid) {
        if(isActive(uuid)) {
            long lastUse = cooldowns.get(uuid);
            int timeSinceLastUseSeconds = (int) (System.currentTimeMillis() - lastUse) / 1000;
            return Math.min(1.0, (double) timeSinceLastUseSeconds / activeSeconds);
        } else {
            return 0;
        }
    }

    public FreezingTrident(GlintSMP glintSMP) {
        this.mainClass = glintSMP;
        this.plugin = glintSMP.getServer().getPluginManager().getPlugin("GlintSMP");
        init();

        BukkitRunnable task = new BukkitRunnable() {
            @Override
            public void run() {
                for(Player player : Bukkit.getOnlinePlayers()) {
                    if (cooldowns.containsKey(player.getUniqueId())) {
                        if (isActive(player.getUniqueId())) {
                            if (cooldowns.containsKey(player.getUniqueId())) {
                                BossBar bossBar = playerBossBars.getOrDefault(player.getUniqueId(), null);
                                if (bossBar == null) {
                                    bossBar = getServer().createBossBar("Freezing Trident Time Remaining", BarColor.PINK, BarStyle.SOLID);
                                    bossBar.addPlayer(player);
                                    playerBossBars.put(player.getUniqueId(), bossBar);
                                }
                                bossBar.setProgress(1 - percentActive(player.getUniqueId()));
                            }
                        } else {
                            BossBar bossBar = playerBossBars.remove(player.getUniqueId());
                            if (bossBar != null) {
                                bossBar.removeAll();
                            }
                        }
                    }
                }
            }
        };
        task.runTaskTimer(plugin, 0, 1);
    }



    public ItemStack TheFreezingTrident() {
        ItemStack item = new ItemStack(Material.TRIDENT, 1);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.AQUA + "The Freezing Trident");
        meta.addEnchant(Enchantment.MENDING, 1, true);
        meta.addEnchant(Enchantment.UNBREAKING, 3, true);
        meta.addEnchant(Enchantment.IMPALING, 5, true);
        meta.addEnchant(Enchantment.LOYALTY, 3, true);
        meta.setUnbreakable(true);
        item.setItemMeta(meta);
        return item;
    }
    public ItemStack FreezingShard() {
        ItemStack item = new ItemStack(Material.AMETHYST_SHARD, 1);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.BOLD + "" + ChatColor.AQUA + "Freezing Shard");
        meta.setUnbreakable(true);
        item.setItemMeta(meta);
        return item;
    }

    private void init() {
        ShapedRecipe shapedRecipe = new ShapedRecipe(new NamespacedKey(plugin, "FreezingTridentRecipe"), TheFreezingTrident());
        shapedRecipe.shape(" S ", "ITI", " I ");
        shapedRecipe.setIngredient('T', Material.TRIDENT);
        shapedRecipe.setIngredient('S', new RecipeChoice.ExactChoice(FreezingShard()));
        shapedRecipe.setIngredient('I', new RecipeChoice.ExactChoice(mainClass.shard));
        getServer().addRecipe(shapedRecipe);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        ItemStack item = e.getItem();
        Player player = e.getPlayer();

        if (item != null && item.isSimilar(TheFreezingTrident())) {
            if (e.getAction().name().contains("LEFT_CLICK") && player.isSneaking()) {
                if (System.currentTimeMillis() - cooldowns.getOrDefault(player.getUniqueId(), 0L) > cooldownSeconds * 1000) {
                    spawnParticlesTowardsPlayer(player);
                } else {
                    player.sendMessage(ChatColor.RED + "Freezing Trident is on Cooldown!");
                }
            }
        }
    }

    private void trapPlayer(Player player) {
        Location loc = player.getLocation().subtract(2, 1, 2);
        for (int x = 0; x < 5; x++) {
            for (int y = 0; y < 5; y++) {
                for (int z = 0; z < 5; z++) {
                    if (x == 0 || x == 4 || y == 0 || y == 4 || z == 0 || z == 4) {
                        Location blockLoc = loc.clone().add(x, y, z);
                        if (blockLoc.getBlock().getType() == Material.AIR) {
                            blockLoc.getBlock().setType(Material.FROSTED_ICE);
                        } else if (blockLoc.getBlock().getType()== Material.GRASS_BLOCK) {
                            blockLoc.getBlock().setType(Material.FROSTED_ICE);
                        } else if (blockLoc.getBlock().getType() == Material.SNOW) {
                            blockLoc.getBlock().setType(Material.FROSTED_ICE);
                        }
                    }
                }
            }
        }

        new BukkitRunnable() {
            int age = 1;

            @Override
            public void run() {
                if (age > 3) {
                    for (int x = 0; x < 5; x++) {
                        for (int y = 0; y < 5; y++) {
                            for (int z = 0; z < 5; z++) {
                                Location blockLoc = loc.clone().add(x, y, z);
                                if (blockLoc.getBlock().getType() == Material.FROSTED_ICE) {
                                    blockLoc.getBlock().setType(Material.AIR);
                                    player.getWorld().playSound(player.getLocation(), Sound.BLOCK_ANVIL_FALL, 1,1);
                                } else if (blockLoc.getBlock().getType() == Material.WATER) {
                                    blockLoc.getBlock().setType(Material.AIR);
                                    player.getWorld().playSound(player.getLocation(), Sound.BLOCK_ANVIL_FALL, 1,1);
                                }
                            }
                        }
                        cancel();
                    }
                } else {
                    age++;
                    player.getWorld().playSound(player.getLocation(), Sound.BLOCK_ANVIL_BREAK, 1,1);
                }
            }
        }.runTaskTimer(plugin, 20L * 2, 20L * 2);
    }

    @EventHandler
    public void onProjectileHit(ProjectileHitEvent event) {
        if (event.getEntity() instanceof Trident) {
            Trident trident = (Trident) event.getEntity();
            Entity hitEntity = event.getHitEntity();

            if (hitEntity instanceof Player) {
                Player hitPlayer = (Player) hitEntity;

                    Player shooter = (Player) trident.getShooter();
                    if (isActive(shooter.getUniqueId())) {
                        trapPlayer(hitPlayer);
                        shooter.setCooldown(Material.TRIDENT,100);
                    }
                }
            }
        }

    public void spawnParticlesTowardsPlayer(Player player) {
        new BukkitRunnable() {
            int ticks = 0;
            final int maxTicks = 35;
            final double speed = 0.5;
            final double spawnDistance = 10.0;
            Location location = player.getLocation();

            @Override
            public void run() {
                if (ticks >= maxTicks) {
                    this.cancel();
                    player.getWorld().playSound(player.getLocation(), Sound.ENTITY_GENERIC_EXPLODE,2,1);
                    cooldowns.put(player.getUniqueId(), System.currentTimeMillis());
                    player.sendMessage(ChatColor.AQUA + "Activated the Freezing Trident!");
                    return;
                }
                Location playerLocation = player.getLocation();
                for (int i = 0; i < 5; i++) {
                    Location startLocation = playerLocation.clone().add(
                            (Math.random() - 0.5) * spawnDistance,
                            (Math.random() - 0.5) * spawnDistance,
                            (Math.random() - 0.5) * spawnDistance
                    );
                    createParticleLine(startLocation, playerLocation, (double) ticks / maxTicks);
                }
                ticks++;
            }
        }.runTaskTimer(plugin, 0L, 1L);
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_WARDEN_SONIC_CHARGE, 1,1);
    }
    private void createParticleLine(Location start, Location end, double progress) {
        double distance = start.distance(end);
        int points = (int) (distance * 10);
        for (int i = 0; i <= points; i++) {
            double ratio = (double) i / points;
            if (ratio > progress) break;
            double x = start.getX() + (end.getX() - start.getX()) * ratio;
            double y = start.getY() + (end.getY() - start.getY()) * ratio;
            double z = start.getZ() + (end.getZ() - start.getZ()) * ratio;
            Location particleLocation = new Location(start.getWorld(), x, y, z);
            particleLocation.getWorld().spawnParticle(Particle.DUST, particleLocation, 1, new Particle.DustOptions(org.bukkit.Color.AQUA, 1));
        }
    }
}
