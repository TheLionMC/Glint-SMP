package me.thelionmc.minecraftplugin.Commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

public class customitemscommand implements CommandExecutor, Listener {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("givecustom")) {
            if (args.length < 2) {
                sender.sendMessage(ChatColor.RED + "Usage: /givecustom <player> <customitem>");
                return true;
            }

            Player target = Bukkit.getPlayer(args[0]);
            if (target == null) {
                sender.sendMessage(ChatColor.RED + "Player not found.");
                return true;
            }

            String customItemName = args[1].toLowerCase();

            //ItemStack customItem = CustomItemFactory.createCustomItem(customItemName);
            //if (customItem == null) {
                sender.sendMessage(ChatColor.RED + "Invalid custom item name.");
                return true;
            }

            //target.getInventory().addItem(customItem);
            //sender.sendMessage(ChatColor.GREEN + "Gave " + target.getName() + " a custom item: " + customItemName);
            return true;
        //}

       // return false;
    }
}
