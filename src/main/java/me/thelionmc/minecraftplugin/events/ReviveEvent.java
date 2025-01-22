package me.thelionmc.minecraftplugin.events;

import me.thelionmc.minecraftplugin.GlintSMP;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static me.thelionmc.minecraftplugin.GlintSMP.InvisShards;

public class ReviveEvent implements Listener {

    private GlintSMP main;
    private Plugin plugin;
    public static ItemStack ReviveBeacon;
    private Map<Player, Block> placedBeacons = new HashMap<>();

    public ReviveEvent(Plugin plugin, GlintSMP main) {
        this.plugin = plugin;
        this.main = main;
        initReviveBeacon();
    }

    private void openBanList(Player player) {
        Set<OfflinePlayer> bannedPlayers = Bukkit.getBannedPlayers();
        int size = (((bannedPlayers.size()) / 9) + 2) * 9;
        Inventory banInventory = Bukkit.createInventory(null, size, "Banned Players");
        int count = 0;

        for (OfflinePlayer banned : bannedPlayers) {
            ItemStack skull = new ItemStack(Material.PLAYER_HEAD, 1);
            SkullMeta meta = (SkullMeta) skull.getItemMeta();
            meta.setOwningPlayer(banned);
            meta.setDisplayName(banned.getName());
            skull.setItemMeta(meta);
            banInventory.setItem(count, skull);
            count++;
        }
        ItemStack cancel = new ItemStack(Material.BARRIER, 1);
        ItemMeta cancelMeta = cancel.getItemMeta();
        cancelMeta.setDisplayName(ChatColor.RED + "Cancel");
        cancel.setItemMeta(cancelMeta);
        banInventory.setItem(banInventory.getSize() - 2, cancel);

        player.openInventory(banInventory);
    }

    public ItemStack getReviveBeacon() {
        ItemStack beacon = new ItemStack(Material.BEACON);
        ItemMeta meta = beacon.getItemMeta();
        meta.addEnchant(Enchantment.MENDING, 1, true);
        meta.setDisplayName(ChatColor.RED + "Revive Beacon");
        meta.setUnbreakable(true);
        beacon.setItemMeta(meta);
        ReviveBeacon = beacon;
        return beacon;
    }

    public void initReviveBeacon() {
        ShapedRecipe shapedRecipe = new ShapedRecipe(new NamespacedKey(plugin, "ReviveBeacon"), getReviveBeacon());
        shapedRecipe.shape("TIT", "INI", "TIT");
        shapedRecipe.setIngredient('N', Material.NETHERITE_BLOCK);
        shapedRecipe.setIngredient('T', Material.TOTEM_OF_UNDYING);
        shapedRecipe.setIngredient('I', new RecipeChoice.ExactChoice(InvisShards));
        Bukkit.getServer().addRecipe(shapedRecipe);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getView().getTitle().equals("Banned Players")) {
            event.setCancelled(true);

            if (event.getCurrentItem() != null && event.getCurrentItem().getType() == Material.PLAYER_HEAD) {
                SkullMeta meta = (SkullMeta) event.getCurrentItem().getItemMeta();
                OfflinePlayer target = meta.getOwningPlayer();

                if (target != null && target.isBanned()) {
                    Player player = (Player) event.getWhoClicked();
                    Block beaconBlock = placedBeacons.get(player);
                    player.closeInventory();
                    if (beaconBlock != null) {
                        startRevivalRitual(player, target, beaconBlock);
                    }
                }
            } else if (event.getCurrentItem() != null && event.getCurrentItem().getType() == Material.BARRIER) {
                Player player = (Player) event.getWhoClicked();
                player.closeInventory();
            }
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (event.getView().getTitle().equals("Banned Players")) {
            Player player = (Player) event.getPlayer();
            Block beaconBlock = placedBeacons.get(player);

            if (beaconBlock != null) {
                boolean playerPardoned = true;
                for (ItemStack item : event.getInventory().getContents()) {
                    if (item != null && item.getType() == Material.PLAYER_HEAD) {
                        playerPardoned = false;
                        break;
                    }
                }
                for (ItemStack item : event.getInventory().getContents()) {
                    if (item != null && item.getType() == Material.BARRIER) {
                        beaconBlock.setType(Material.AIR);
                        placedBeacons.remove(player);
                        player.getInventory().addItem(getReviveBeacon());
                        player.sendMessage(ChatColor.BLUE + "[Glint SMP] " + ChatColor.YELLOW + "Your Revive Beacon has been returned to your inventory.");
                    }
                }
                if (!playerPardoned) {
                    beaconBlock.setType(Material.AIR);
                    placedBeacons.remove(player);
                    player.sendMessage(ChatColor.BLUE + "[Glint SMP] " + ChatColor.GREEN + "The Revive Beacon has been consumed.");
                }
            }
        }
    }


    @EventHandler
    public void onRightClickEvent(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        ItemStack item = player.getItemInHand();
        if (item.isSimilar(ReviveBeacon)) {
            openBanList(player);
            Block placedBlock = event.getBlockPlaced();
            placedBeacons.put(player, placedBlock);
        }
    }

    private void startRevivalRitual(Player player, OfflinePlayer target, Block beaconBlock) {
        new BukkitRunnable() {
            int ticks = 0;
            Location beaconLoc = beaconBlock.getLocation().add(0.5, 0, 0.5); // Center of the block
            double radius = 5.0; // Radius of the circle
            double starRadius = 5.0; // Star fits inside the circle
            double angle = 0; // Rotation angle for both the circle and the star

            @Override
            public void run() {
                if (ticks >= 100) {
                    // Explosion effect
                    beaconBlock.getWorld().spawnParticle(Particle.EXPLOSION_LARGE, beaconLoc, 1);
                    beaconBlock.getWorld().playSound(beaconLoc, Sound.ENTITY_GENERIC_EXPLODE, 1, 1);

                    // Pardon the player
                    Bukkit.getBanList(BanList.Type.NAME).pardon(target.getName());
                    beaconBlock.setType(Material.AIR);
                    placedBeacons.remove(player);

                    for (Player p : Bukkit.getOnlinePlayers()) {
                        p.playSound(p.getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 1, 1);
                        p.sendMessage(ChatColor.BLUE + "[Glint SMP] " + ChatColor.GREEN + target.getName() + " has been revived!");
                    }

                    cancel();
                    return;
                }

                // Drawing the rotating outer circle
                for (int i = 0; i < 360; i += 5) { // Increment angle for smooth circle
                    double radians = Math.toRadians(i + angle);
                    double x = radius * Math.cos(radians);
                    double z = radius * Math.sin(radians);

                    Location particleLoc = beaconLoc.clone().add(x, 0, z);
                    beaconBlock.getWorld().spawnParticle(
                            Particle.REDSTONE,
                            particleLoc,
                            0,
                            new Particle.DustOptions(Color.RED, 1) // Redstone particle effect
                    );
                }

                // Drawing the rotating star
                for (int i = 0; i < 5; i++) {
                    double startAngle = angle + i * 2 * Math.PI / 5; // Position each point of the star
                    double endAngle = angle + ((i + 2) % 5) * 2 * Math.PI / 5; // Skip one point to create the star shape

                    for (double t = 0; t <= 1; t += 0.05) { // Draw lines between star points
                        double x = starRadius * Math.cos(startAngle + (endAngle - startAngle) * t);
                        double z = starRadius * Math.sin(startAngle + (endAngle - startAngle) * t);

                        Location particleLoc = beaconLoc.clone().add(x, 0, z);
                        beaconBlock.getWorld().spawnParticle(
                                Particle.REDSTONE,
                                particleLoc,
                                0,
                                new Particle.DustOptions(Color.RED, 1)
                        );
                    }
                }

                angle += Math.PI / 60; // Gradually rotate the circle and star together
                ticks += 3;
            }
        }.runTaskTimer(plugin, 0, 3);
    }





    private void simulateBeaconBeam(Location location) {
        World world = location.getWorld();
        if (world == null) return;

        Location start = location.clone().add(0.5, 1.0, 0.5);
        double maxHeight = world.getMaxHeight();

        Particle.DustTransition dustTransition = new Particle.DustTransition(
                Color.AQUA,
                Color.WHITE,
                1.5f
        );

        for (double y = start.getY(); y < maxHeight; y += 0.5) {
            Location beamLoc = start.clone();
            beamLoc.setY(y);
            world.spawnParticle(Particle.DUST_COLOR_TRANSITION, beamLoc, 5, 0.1, 0.1, 0.1, dustTransition);
        }
    }


}
