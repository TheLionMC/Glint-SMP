package me.thelionmc.minecraftplugin.Commands;

import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class revivecommand implements CommandExecutor {
    private BanList bannedPlayers;

    public revivecommand() {
        this.bannedPlayers = Bukkit.getBanList(BanList.Type.NAME);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("revive") && sender instanceof Player) {
            if (sender.isOp()) {
                if (args.length == 1) {
                    String playerName = args[0];
                    if (bannedPlayers.isBanned(playerName)) {
                        bannedPlayers.pardon(playerName);
                        sender.sendMessage(playerName + " has been unbanned.");
                    } else {
                        sender.sendMessage(playerName + " is not banned.");
                    }
                } else {
                    sender.sendMessage(ChatColor.RED + "Usage: /revive <playername>");
                }
                return true;
            } else {
                sender.sendMessage(ChatColor.RED + "You need to be an operator to use this command.");
            }
            return false;
        }
        return false;
    }
}