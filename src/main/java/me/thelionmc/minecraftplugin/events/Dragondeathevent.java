package me.thelionmc.minecraftplugin.events;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

public class Dragondeathevent implements Listener {
    private final Plugin plugin;

    public Dragondeathevent(Plugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onDragonDeath(EntityDeathEvent event) {
        if (event.getEntity() instanceof EnderDragon) {
            World endWorld = event.getEntity().getWorld();
            Location portalLocation = new Location(endWorld, 0, 64, 0);
            for (Player player : Bukkit.getOnlinePlayers()) {
                player.sendTitle(ChatColor.RED + "END EXIT PORTAL LOCKED",
                        "For " + ChatColor.YELLOW + "5 minutes" + ChatColor.WHITE + "...",
                        10, 70, 20);
            }
            lockPortal(portalLocation);
            new BukkitRunnable() {
                @Override
                public void run() {
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        player.sendTitle(ChatColor.RED + "END EXIT PORTAL UNLOCKED",
                                ChatColor.YELLOW + "RUN!",
                                10, 70, 20);
                    }
                    unlockPortal(portalLocation);
                }
            }.runTaskLater(plugin, 6000);
        }
    }

    private void lockPortal(Location center) {
        int[][] offsets = {
                {1, 0, 1}, {2, 0, -1}, {1, 0, -2}, {-1, 0, -1},
                {-1, 0, 1}, {-2, 0, 1}, {-1, 0, 2}, {1, 0, 2}
        };

        for (int[] offset : offsets) {
            Block block = center.clone().add(offset[0], offset[1], offset[2]).getBlock();
            block.setType(Material.RED_STAINED_GLASS);
        }
    }

    private void unlockPortal(Location center) {
        int[][] offsets = {
                {1, 0, 1}, {2, 0, -1}, {1, 0, -2}, {-1, 0, -1},
                {-1, 0, 1}, {-2, 0, 1}, {-1, 0, 2}, {1, 0, 2}
        };

        for (int[] offset : offsets) {
            Block block = center.clone().add(offset[0], offset[1], offset[2]).getBlock();
            block.setType(Material.END_PORTAL);
        }
    }
}
