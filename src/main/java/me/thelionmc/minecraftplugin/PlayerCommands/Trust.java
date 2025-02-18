package me.thelionmc.minecraftplugin.PlayerCommands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import me.thelionmc.minecraftplugin.TrustManager;

import java.util.UUID;

public class Trust implements CommandExecutor {
    TrustManager trustManager;
    public Trust(TrustManager trustManager) {
        this.trustManager = trustManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(args.length > 1) {
            sender.sendMessage(ChatColor.RED + "Invalid Arguments; 1 argument required!");
            return true;
        }
        if(Bukkit.getOfflinePlayer(args[0]) == null) {
            sender.sendMessage(ChatColor.RED + "Invalid Arguments; Player doesn't exist!");
            return true;
        }
        if(!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can use this command!");
            return true;
        }

        UUID playerToBeTrusted = Bukkit.getOfflinePlayer(args[0]).getUniqueId();
        UUID player = ((Player) sender).getUniqueId();
        boolean playerIsTrusted = trustManager.isPlayerTrustedByPlayer(playerToBeTrusted, player);

        if(label.equalsIgnoreCase("trust") && sender instanceof Player) {
            if(playerIsTrusted) {
                sender.sendMessage(ChatColor.RED + "This player is already trusted!");
                return true;
            }

            trustManager.trustPlayer(player, playerToBeTrusted);
            sender.sendMessage(ChatColor.GREEN + "Success!");
        }

        else if(label.equalsIgnoreCase("distrust") && sender instanceof Player) {
            if(!playerIsTrusted) {
                sender.sendMessage(ChatColor.RED + "This player is already distrusted!");
                return true;
            }

            trustManager.distrustPlayer(player, playerToBeTrusted);
            sender.sendMessage(ChatColor.GREEN + "Success!");
        }
        return true;
    }
}
