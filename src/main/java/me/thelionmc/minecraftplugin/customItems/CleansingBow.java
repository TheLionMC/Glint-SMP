package me.thelionmc.minecraftplugin.customItems;

import me.thelionmc.minecraftplugin.GlintSMP;
import org.bukkit.*;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Arrow;
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

public class CleansingBow implements Listener {
    private GlintSMP mainClass;
    private Plugin plugin;

    public Map<UUID, Long> cooldowns = new HashMap<>();
    private Map<UUID, BossBar> playerBossBars = new HashMap<>();
    public int cooldownSeconds = 150;
    int activeSeconds = 40;

    public boolean isActive(UUID uuid) {
        if (cooldowns.containsKey(uuid)) {
            long lastUse = cooldowns.get(uuid);
            int timeSinceLastUseSeconds = (int) (System.currentTimeMillis() - lastUse) / 1000;
            return timeSinceLastUseSeconds < activeSeconds;
        }
        return false;
    }

    public double percentActive(UUID uuid) {
        if (isActive(uuid)) {
            long lastUse = cooldowns.get(uuid);
            int timeSinceLastUseSeconds = (int) (System.currentTimeMillis() - lastUse) / 1000;
            return Math.min(1.0, (double) timeSinceLastUseSeconds / activeSeconds);
        }
        return 0;
    }

    public CleansingBow(GlintSMP glintSMP) {
        this.mainClass = glintSMP;
        this.plugin = glintSMP.getServer().getPluginManager().getPlugin("GlintSMP");
        init(glintSMP);


        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    UUID uuid = player.getUniqueId();
                    if (isActive(uuid)) {
                        BossBar bossBar = playerBossBars.computeIfAbsent(uuid, k -> {
                            BossBar bar = getServer().createBossBar("Cleansing Bow Time Remaining", BarColor.PINK, BarStyle.SOLID);
                            bar.addPlayer(player);
                            return bar;
                        });
                        bossBar.setProgress(1 - percentActive(uuid));
                    } else {
                        BossBar bossBar = playerBossBars.remove(uuid);
                        if (bossBar != null) {
                            bossBar.removeAll();
                        }
                    }
                }
            }
        }.runTaskTimer(plugin, 0, 20);
    }

    public ItemStack TheCleansingBow() {
        ItemStack item = new ItemStack(Material.BOW);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§4The Cleansing Bow");
        meta.addEnchant(Enchantment.MENDING, 1, true);
        meta.addEnchant(Enchantment.DURABILITY, 3, true);
        meta.setUnbreakable(true);
        meta.addEnchant(Enchantment.ARROW_DAMAGE, 6, true);
        meta.addEnchant(Enchantment.ARROW_FIRE, 1, true);
        meta.addEnchant(Enchantment.ARROW_KNOCKBACK, 1, true);
        item.setItemMeta(meta);
        return item;
    }

    public ItemStack CleansingShard() {
        ItemStack item1 = new ItemStack(Material.AMETHYST_SHARD);
        ItemMeta meta = item1.getItemMeta();
        meta.setDisplayName(ChatColor.BOLD + "" + ChatColor.AQUA + "Cleansing Shard");
        meta.setUnbreakable(true);
        item1.setItemMeta(meta);
        return item1;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        ItemStack item = e.getItem();
        UUID playerId = player.getUniqueId();

        if (item != null && item.isSimilar(TheCleansingBow())) {
            if (e.getAction().name().contains("LEFT_CLICK") && player.isSneaking()) {
                if (System.currentTimeMillis() - cooldowns.getOrDefault(playerId, 0L) > cooldownSeconds * 1000) {
                    spawnParticlesTowardsPlayer(player);
                } else {
                    player.sendMessage(ChatColor.RED + "Cleansing Bow is on Cooldown!");
                }
            } else if (e.getAction().name().contains("LEFT_CLICK")) {
                if (isActive(playerId)) {
                    if (!player.hasCooldown(Material.BOW)) {
                        Location location = player.getEyeLocation();
                        Arrow arrow = player.getWorld().spawnArrow(location, location.getDirection(), 1.5f, 0.0f);
                        arrow.addCustomEffect(new PotionEffect(PotionEffectType.HARM, 1, 2), true);

                        cooldowns.put(playerId, cooldowns.getOrDefault(playerId, System.currentTimeMillis()) - 2000);
                        player.setCooldown(Material.BOW, 10);
                    }
                }
            }
        }
    }
    public void init(GlintSMP glintSMP) {
        ShapedRecipe shapedRecipe = new ShapedRecipe(new NamespacedKey(plugin, "CleansingBowRecipe"), TheCleansingBow());
        shapedRecipe.shape(" BI", "B S", " BI");
        shapedRecipe.setIngredient('I', new RecipeChoice.ExactChoice(mainClass.getInvisShardItem()));
        shapedRecipe.setIngredient('S', new RecipeChoice.ExactChoice(CleansingShard()));
        shapedRecipe.setIngredient('B', Material.BLAZE_ROD);
        getServer().addRecipe(shapedRecipe);
    }

    public void spawnParticlesTowardsPlayer(Player player) {
        new BukkitRunnable() {
            int ticks = 0;
            final int maxTicks = 35;
            final double spawnDistance = 10.0;

            @Override
            public void run() {
                if (ticks >= maxTicks) {
                    cancel();
                    player.getWorld().playSound(player.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 2, 1);
                    cooldowns.put(player.getUniqueId(), System.currentTimeMillis());
                    player.sendMessage(ChatColor.AQUA + "Activated the Cleansing Bow!");
                    return;
                }
                for (int i = 0; i < 5; i++) {
                    Location startLocation = player.getLocation().clone().add(
                            (Math.random() - 0.5) * spawnDistance,
                            (Math.random() - 0.5) * spawnDistance,
                            (Math.random() - 0.5) * spawnDistance
                    );
                    createParticleLine(startLocation, player.getLocation(), (double) ticks / maxTicks);
                }
                ticks++;
            }
        }.runTaskTimer(plugin, 0L, 1L);
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_WARDEN_SONIC_CHARGE, 1, 1);
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
            particleLocation.getWorld().spawnParticle(Particle.REDSTONE, particleLocation, 1, new Particle.DustOptions(org.bukkit.Color.AQUA, 1));
        }
    }
}
