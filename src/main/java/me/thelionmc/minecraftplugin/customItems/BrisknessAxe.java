package me.thelionmc.minecraftplugin.customItems;
import me.thelionmc.minecraftplugin.GlintSMP;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.bukkit.Bukkit.*;

public class BrisknessAxe implements Listener {
    private GlintSMP mainClass;
    private Plugin plugin;
    public Map<UUID, Long> cooldowns = new HashMap<>();
    private Map<UUID, BossBar> playerBossBars = new HashMap<>();
    public int cooldownSeconds = 150;
    private int activeSeconds = 20;

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

    public BrisknessAxe(GlintSMP glintSMP) {
        this.mainClass = glintSMP;
        this.plugin = glintSMP.getServer().getPluginManager().getPlugin("GlintSMP");
        init();

        BukkitRunnable task = new BukkitRunnable() {
            @Override
            public void run() {
                for(Player player : Bukkit.getOnlinePlayers()) {
                    if (cooldowns.containsKey(player.getUniqueId())) {
                        int secondsRemaining = 20 - (int) ((System.currentTimeMillis() - cooldowns.get(player.getUniqueId())) / 1000);
                        if (secondsRemaining > 0) {
                            if (cooldowns.containsKey(player.getUniqueId())) {
                                BossBar bossBar = playerBossBars.getOrDefault(player.getUniqueId(), null);
                                if (bossBar == null) {
                                    bossBar = getServer().createBossBar("Briskness Axe Time Remaining", BarColor.PURPLE, BarStyle.SOLID);
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

    public ItemStack BrisknessShard() {
        ItemStack item = new ItemStack(Material.AMETHYST_SHARD, 1);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.BOLD + "" + ChatColor.AQUA + "Briskness Shard");
        meta.setUnbreakable(true);
        item.setItemMeta(meta);
        return item;
    }

    private void init() {
        ShapedRecipe shapedRecipe = new ShapedRecipe(new NamespacedKey(plugin, "BrisknessAxeRecipe"), TheBrisknessAxe());
        shapedRecipe.shape("SN ", "NT ", " T ");
        shapedRecipe.setIngredient('S', new RecipeChoice.ExactChoice(BrisknessShard()));
        shapedRecipe.setIngredient('T', Material.STICK);
        shapedRecipe.setIngredient('N', Material.NETHERITE_BLOCK);

        getServer().addRecipe(shapedRecipe);
    }
    public ItemStack TheBrisknessAxe() {
        ItemStack item = new ItemStack(Material.NETHERITE_AXE, 1);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("The Briskness Axe");
        meta.addEnchant(Enchantment.MENDING, 1, true);
        meta.addEnchant(Enchantment.DURABILITY, 3, true);
        meta.setUnbreakable(true);
        meta.addEnchant(Enchantment.DAMAGE_ALL, 5, true);
        meta.addEnchant(Enchantment.DIG_SPEED, 5, true);
        meta.addEnchant(Enchantment.LOOT_BONUS_BLOCKS, 1, true);
        item.setItemMeta(meta);
        return item;
    }
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        ItemStack item = e.getItem();
        Player player = e.getPlayer();

        if (item != null && item.isSimilar(TheBrisknessAxe())) {
            if (e.getAction().name().contains("LEFT_CLICK") && player.isSneaking()) {
                if (System.currentTimeMillis() - cooldowns.getOrDefault(player.getUniqueId(), 0L) > cooldownSeconds * 1000) {
                    cooldowns.put(player.getUniqueId(), System.currentTimeMillis());
                    player.sendMessage(ChatColor.AQUA + "Activated the Briskness Axe!");
                    spawnParticlesTowardsPlayer(player);
                    for(Player otherPlayer : Bukkit.getOnlinePlayers()) {
                        if(otherPlayer != player) {
                            if(otherPlayer.getLocation().distance(player.getLocation()) <= 15) {
                                otherPlayer.addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION, 35, 1, true, true));
                            }
                        }
                    }
                    new BukkitRunnable() {
                        int ticks = 0;
                        @Override
                        public void run() {
                            if(ticks >= 35) {
                                player.getWorld().playSound(player.getLocation(), Sound.ENTITY_PLAYER_SPLASH_HIGH_SPEED, 1, 1);
                                player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, activeSeconds * 20, 2, true, true));
                                AttributeInstance attributeInstance = player.getAttribute(Attribute.GENERIC_ATTACK_SPEED);
                                if(attributeInstance != null) {
                                    attributeInstance.setBaseValue(8);
                                }
                                for(Player otherPlayer : Bukkit.getOnlinePlayers()) {
                                    if(otherPlayer != player) {
                                        if(otherPlayer.getLocation().distance(player.getLocation()) <= 15) {
                                            otherPlayer.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, activeSeconds * 20, 2, true, true));
                                        }
                                    }
                                }
                                new BukkitRunnable() {
                                    @Override
                                    public void run() {
                                        AttributeInstance attributeInstance = player.getAttribute(Attribute.GENERIC_ATTACK_SPEED);
                                        if(attributeInstance != null ) {
                                            attributeInstance.setBaseValue(4);
                                        }
                                    }
                                }.runTaskLater(plugin, activeSeconds * 20);
                                this.cancel();
                                return;
                            }

                            for(Player otherPlayer : Bukkit.getOnlinePlayers()) {
                                if(otherPlayer != player) {
                                    if(otherPlayer.getLocation().distance(player.getLocation()) <= 15) {
                                        createParticleLine(player.getLocation(), otherPlayer.getLocation(), ticks / 60);

                                    }
                                }
                            }

                            ticks++;
                        }
                    }.runTaskTimer(plugin, 0, 1);

                } else {
                    player.sendMessage(ChatColor.RED + "Briskness Axe is on Cooldown!");
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

                if(ticks > maxTicks) {
                    return;
                }
                Location playerLocation = player.getLocation();
                for(Player otherPlayer : Bukkit.getOnlinePlayers()) {
                    if (otherPlayer != player) {
                        if (otherPlayer.getLocation().distance(player.getLocation()) <= 15) {
                            createParticleLine(otherPlayer.getLocation(), playerLocation, (double) ticks / maxTicks);
                        }
                    }
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
            particleLocation.getWorld().spawnParticle(Particle.REDSTONE, particleLocation, 1, new Particle.DustOptions(org.bukkit.Color.YELLOW, 1));
        }
    }
}
