package me.thelionmc.minecraftplugin.OperatorCommands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.ChatColor;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SetShards implements CommandExecutor {

    private final Map<UUID, Integer> shardCounts = new HashMap<>();

    private void setPlayerShards(UUID playerId, int shardCount) {
        shardCounts.put(playerId, shardCount);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("setShards") && sender instanceof Player && sender.isOp()) {
            if (args.length == 2) {
                String playerName = args[0];
                int amount = Integer.parseInt(args[1]);
                Player targetPlayer = Bukkit.getPlayer(playerName);

                if (targetPlayer != null && targetPlayer.isOnline()) {
                    UUID targetPlayerId = targetPlayer.getUniqueId();
                    if (amount >= 0) {
                        setPlayerShards(targetPlayerId, amount);
                        sender.sendMessage(ChatColor.GREEN + "Shards set to " + amount + " for player " + playerName);
                        return true;
                    } else {
                        sender.sendMessage(ChatColor.RED + "Please provide a non-negative amount of shards.");
                    }
                } else {
                    sender.sendMessage(ChatColor.RED + "Player " + playerName + " is not online.");
                }
            } else {
            sender.sendMessage(ChatColor.RED + "Usage: /setShards <playerName> <amount>");
            }
        }

        return false;
    }
}
