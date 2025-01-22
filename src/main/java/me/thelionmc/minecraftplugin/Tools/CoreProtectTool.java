package me.thelionmc.minecraftplugin.Tools;

import me.thelionmc.minecraftplugin.Tools.data.ProtectedArea;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class CoreProtectTool implements Listener {

    private final HashMap<UUID, Block> firstCorner = new HashMap<>();
    private final HashMap<UUID, Block> secondCorner = new HashMap<>();
    private final List<ProtectedArea> protectedAreas = new ArrayList<>();
    private final HashMap<UUID, Boolean> showGlowstone = new HashMap<>();
    private final JavaPlugin plugin;
    private final File savedAreasFile;
    private final FileConfiguration savedAreasConfig;

    public CoreProtectTool(JavaPlugin plugin) {
        this.plugin = plugin;
        this.savedAreasFile = new File(plugin.getDataFolder(), "protected_areas.yml");
        this.savedAreasConfig = YamlConfiguration.loadConfiguration(savedAreasFile);
        loadProtectedAreas();
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();

        if (item.getType() != Material.STONE_SHOVEL ||
                !item.getItemMeta().getDisplayName().equals(ChatColor.GREEN + "CoreProtect Tool")) {
            return;
        }

        Block block = event.getClickedBlock();
        if (block == null) return;

        UUID playerId = player.getUniqueId();

        if (event.getAction().toString().contains("LEFT_CLICK")) {
            firstCorner.put(playerId, block);
            player.sendMessage(ChatColor.BLUE + "[Glint SMP] " + ChatColor.GREEN + ChatColor.YELLOW + "Position 1 Set");
            event.setCancelled(true);
        } else if (event.getAction().toString().contains("RIGHT_CLICK")) {
            secondCorner.put(playerId, block);

            Block firstBlock = firstCorner.get(playerId);
            if (firstBlock != null) {
                player.sendMessage(ChatColor.BLUE + "[Glint SMP] " + ChatColor.GREEN +
                        "Second corner set. Type " + ChatColor.YELLOW +
                        "/tool coreprotect save [Name] " +
                        ChatColor.GREEN + "to save or " +
                        ChatColor.YELLOW + "/tool coreprotect abandon" +
                        ChatColor.GREEN + " to cancel.");
            } else {
                player.sendMessage(ChatColor.BLUE + "[Glint SMP] " + ChatColor.RED + "First corner must be set first!");
            }
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();

        if (isInProtectedArea(player, block)) {
            if (!player.isOp()) {
                if (block.getType() == Material.COBWEB || block.getType() == Material.WATER_BUCKET || block.getType() == Material.LAVA_BUCKET) {
                    return;
                }
                player.sendMessage(ChatColor.BLUE + "[Glint SMP] " + ChatColor.RED + "You cannot place blocks in this area!");
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();

        if (isInProtectedArea(player, block)) {
            if (!player.isOp()) {
                if (block.getType() == Material.COBWEB) {
                    return;
                }
                player.sendMessage(ChatColor.BLUE + "[Glint SMP] " + ChatColor.RED + "You cannot break blocks in this area!");
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        UUID playerId = player.getUniqueId();
        ItemStack item = player.getInventory().getItemInMainHand();

        boolean show = showGlowstone.getOrDefault(playerId, false);
        showGlowstone.put(playerId, !show);

        if (item.getType() != Material.STONE_SHOVEL ||
                !item.getItemMeta().getDisplayName().equals(ChatColor.GREEN + "CoreProtect Tool")) {
            return;
        }

        if (!show) {
            for (ProtectedArea area : protectedAreas) {
                area.toggleVisibility(player, true);
            }
            player.sendMessage(ChatColor.BLUE + "[Glint SMP] " + ChatColor.YELLOW + "All protected areas are now visible!");
        } else {
            for (ProtectedArea area : protectedAreas) {
                area.toggleVisibility(player, false);
            }
            player.sendMessage(ChatColor.BLUE + "[Glint SMP] " + ChatColor.YELLOW + "Protected areas are now hidden!");
        }

        event.setCancelled(true);
    }

    private boolean isInProtectedArea(Player player, Block block) {
        for (ProtectedArea area : protectedAreas) {
            if (area.isInside(block)) {
                return true;
            }
        }
        return false;
    }

    public void saveProtectedArea(Player player, String name) {
        Block firstBlock = firstCorner.get(player.getUniqueId());
        Block secondBlock = secondCorner.get(player.getUniqueId());

        if (firstBlock == null || secondBlock == null) {
            player.sendMessage(ChatColor.BLUE + "[Glint SMP] " + ChatColor.RED + "Both corners must be set before saving!");
            return;
        }

        ProtectedArea area = new ProtectedArea(
                firstBlock.getLocation().getBlockX(),
                firstBlock.getLocation().getBlockY(),
                firstBlock.getLocation().getBlockZ(),
                secondBlock.getLocation().getBlockX(),
                secondBlock.getLocation().getBlockY(),
                secondBlock.getLocation().getBlockZ()
        );

        protectedAreas.add(area);

        // Save to file
        savedAreasConfig.set(name, area.toMap());
        try {
            savedAreasConfig.save(savedAreasFile);
        } catch (IOException e) {
            player.sendMessage(ChatColor.BLUE + "[Glint SMP] " + ChatColor.RED + "Error saving area to file!");
            e.printStackTrace();
            return;
        }

        firstCorner.remove(player.getUniqueId());
        secondCorner.remove(player.getUniqueId());

        player.sendMessage(ChatColor.BLUE + "[Glint SMP] " + ChatColor.GREEN + "Area saved as: " + name);
    }

    public void abandonProtectedArea(Player player, String name) {
        if (!savedAreasConfig.contains(name)) {
            player.sendMessage(ChatColor.BLUE + "[Glint SMP] " + ChatColor.RED + "No saved area with that name exists!");
            return;
        }

        savedAreasConfig.set(name, null);
        try {
            savedAreasConfig.save(savedAreasFile);
        } catch (IOException e) {
            player.sendMessage(ChatColor.BLUE + "[Glint SMP] " + ChatColor.RED + "Error abandoning area!");
            e.printStackTrace();
            return;
        }

        player.sendMessage(ChatColor.BLUE + "[Glint SMP] " + ChatColor.GREEN + "Abandoned area: " + name);
    }

    private void loadProtectedAreas() {
        for (String key : savedAreasConfig.getKeys(false)) {
            Map<String, Object> map = savedAreasConfig.getConfigurationSection(key).getValues(false);
            protectedAreas.add(ProtectedArea.fromMap(map));
        }
    }
}
