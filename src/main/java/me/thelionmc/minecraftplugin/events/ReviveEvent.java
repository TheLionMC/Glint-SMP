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
        init();
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
        banInventory.setItem(banInventory.getSize() - 1, cancel);

        player.openInventory(banInventory);
    }

    public ItemStack getReviveBeacon() {
        ItemStack beacon1 = new ItemStack(Material.BEACON);
        ItemMeta meta = beacon1.getItemMeta();
        meta.addEnchant(Enchantment.MENDING, 1, true);
        meta.setDisplayName(ChatColor.RED + "Revive Beacon");
        meta.setUnbreakable(true);
        beacon1.setItemMeta(meta);
        ReviveBeacon = beacon1;
        return beacon1;
    }

    public void init() {
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
            Location beaconLoc = beaconBlock.getLocation().add(0.5, 0, 0.5);
            double radius = 5.0;
            double[][] starPoints = {
                    {0, -5},
                    {4.76, -1.55},
                    {2.94, 4.05},
                    {-2.94, 4.05},
                    {-4.76, -1.55}
            };

            int[][] starConnections = {
                    {0, 2}, {2, 4}, {4, 1}, {1, 3}, {3, 0}
            };

            @Override
            public void run() {
                if (ticks >= 100) {
                    beaconBlock.getWorld().spawnParticle(Particle.EXPLOSION_LARGE, beaconLoc, 1);
                    beaconBlock.getWorld().playSound(beaconLoc, Sound.ENTITY_GENERIC_EXPLODE, 1, 1);

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

                // Drawing the outer circle
                for (int i = 0; i < 360; i += 5) {
                    double radians = Math.toRadians(i + ticks);
                    double x = radius * Math.cos(radians);
                    double z = radius * Math.sin(radians);

                    Location particleLoc = beaconLoc.clone().add(x, 0, z);
                    beaconBlock.getWorld().spawnParticle(
                            Particle.REDSTONE,
                            particleLoc,
                            0,
                            new Particle.DustOptions(Color.RED, 1)
                    );
                }

                for (int[] connection : starConnections) {
                    double[] startPoint = starPoints[connection[0]];
                    double[] endPoint = starPoints[connection[1]];

                    for (double t = 0; t <= 1; t += 0.05) {
                        double x = startPoint[0] + (endPoint[0] - startPoint[0]) * t;
                        double z = startPoint[1] + (endPoint[1] - startPoint[1]) * t;

                        Location particleLoc = beaconLoc.clone().add(x, 0, z);
                        beaconBlock.getWorld().spawnParticle(
                                Particle.REDSTONE,
                                particleLoc,
                                0,
                                new Particle.DustOptions(Color.RED, 1)
                        );
                    }
                }

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

        for (double y = start.getY(); y < maxHeight; y += 1) {
            y = 365;
            Location beamLoc = start.clone();
            beamLoc.setY(y);
            world.spawnParticle(Particle.DUST_COLOR_TRANSITION, beamLoc, 5, 0.1, 0.1, 0.1, dustTransition);
        }
    }


}
