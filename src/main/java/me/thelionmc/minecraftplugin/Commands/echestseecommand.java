package me.thelionmc.minecraftplugin.Commands;

import me.thelionmc.minecraftplugin.GlintSMP;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class echestseecommand implements CommandExecutor, Listener {
    private final Map<UUID, UUID> viewingEnderChest = new HashMap<>();
    private final GlintSMP plugin;
    public echestseecommand(GlintSMP plugin) {
        this.plugin = plugin;
    }
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("echest")) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                if (args.length == 1) {
                    if (player.isOp()) {
                        Player target = Bukkit.getPlayer(args[0]);
                        if (target != null) {
                            Inventory targetEnderChest = target.getEnderChest();
                            player.openInventory(targetEnderChest);
                            viewingEnderChest.put(player.getUniqueId(), target.getUniqueId());
                            player.sendMessage(ChatColor.BLUE + "[GlintSMP] " + ChatColor.GREEN + "You have opened " + target.getName() + "'s Ender Chest.");
                        } else {
                            player.sendMessage(ChatColor.BLUE + "[GlintSMP] " + ChatColor.RED + "Player not found.");
                        }
                    } else {
                        player.sendMessage(ChatColor.BLUE + "[GlintSMP] " +ChatColor.RED + "You don't have permission to access other players' Ender Chests.");
                    }
                    return true;
                }
                player.sendMessage(ChatColor.BLUE + "[GlintSMP] " +ChatColor.RED + "Usage: /echest [Player]");
            } else {
                sender.sendMessage(ChatColor.BLUE + "[GlintSMP] " +ChatColor.RED + "Only players can use this command.");
            }
        }
        return false;
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();
        UUID playerUUID = player.getUniqueId();
        if (viewingEnderChest.containsKey(playerUUID)) {
            UUID targetUUID = viewingEnderChest.get(playerUUID);
            Player targetPlayer = Bukkit.getPlayer(targetUUID);

            if (targetPlayer != null) {
                Inventory targetEnderChest = targetPlayer.getEnderChest();
                Inventory viewerInventory = event.getInventory();
                targetEnderChest.setContents(viewerInventory.getContents());
            }
            viewingEnderChest.remove(playerUUID);
        }
    }
}

