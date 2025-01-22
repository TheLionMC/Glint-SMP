package me.thelionmc.minecraftplugin.Commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class invisshardscommand implements CommandExecutor {

    public Map<UUID, Integer> shardCounts = new HashMap<>();

    private int getPlayerShards(UUID playerId) {

        return shardCounts.getOrDefault(playerId, 10);
    }
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("invisshards") && sender instanceof Player) {
            Player player = (Player) sender;
            player.sendMessage(ChatColor.GREEN + "You have " + getPlayerShards(player.getUniqueId()) + " Invis Shards. " +
                    ChatColor.YELLOW + "To gain shards, you have to kill players. " + ChatColor.GREEN + "If you want to craft them, check /invisrecipe.");
            return true;
        }
        return false;
    }
}
