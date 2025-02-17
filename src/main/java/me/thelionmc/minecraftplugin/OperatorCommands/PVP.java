package me.thelionmc.minecraftplugin.OperatorCommands;

import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PVP implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("setpvp") && sender instanceof Player && sender.isOp()) {
            Player player = (Player) sender;
            World playerWorld = player.getWorld();
            if (args.length > 0) {
                if (args[0].equalsIgnoreCase("true")) {
                    playerWorld.setPVP(true);
                    for (Player p : Bukkit.getOnlinePlayers()) {
                      //  p.sendTitle(ChatColor.RED + "PVP ENABLED", ChatColor.WHITE + "Be careful out there...");
                        //p.getWorld().playSound(p.getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 1, 1);
                        //p.sendMessage("----------------------------------------------------");
                        //p.sendMessage("");
                        //p.sendMessage(ChatColor.BLUE + "       [Glint SMP] " + ChatColor.GREEN + "Server Enabled PvP");
                        //p.sendMessage("");
                        //p.sendMessage("----------------------------------------------------");

                        p.sendTitle(ChatColor.RED + "PvP Enabled", ChatColor.YELLOW + "You may now kill other players!");
                    }
                } else if (args[0].equalsIgnoreCase("false")) {
                    playerWorld.setPVP(false);
                    for (Player p : Bukkit.getOnlinePlayers()) {
                      //  p.sendTitle(ChatColor.GREEN + "PVP DISABLED", ChatColor.WHITE + "Relax... while you can...");
                       // p.getWorld().playSound(p.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
                        //p.sendMessage("----------------------------------------------------");
                        //p.sendMessage("");
                        //p.sendMessage(ChatColor.BLUE + "       [Glint SMP] " + ChatColor.GREEN +  "Server Disabled PvP");
                        //p.sendMessage("");
                        //p.sendMessage("----------------------------------------------------");
                        p.sendTitle(ChatColor.GREEN + "PvP Disabled", ChatColor.YELLOW + "You are save, for now...");
                        return true;
                    }
                } else {
                    player.sendMessage(ChatColor.BLUE + "[GlintSMP] " + ChatColor.YELLOW + "Usage: /setpvp [true/false]");
                }
            } else {
                player.sendMessage(ChatColor.BLUE + "[GlintSMP] " + ChatColor.YELLOW + "Usage: /setpvp [true/false]");
            }
        }
        return false;
    }
}
