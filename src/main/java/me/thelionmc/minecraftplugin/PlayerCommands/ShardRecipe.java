package me.thelionmc.minecraftplugin.PlayerCommands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ShardRecipe implements CommandExecutor {


    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)){
            sender.sendMessage(ChatColor.RED + "Only Players can use this command");
        }
        Player player = (Player) sender;
        if(cmd.getName().equalsIgnoreCase("shardrecipe")) {
            return true;
        }
        return false;
    }
}
