package me.thelionmc.minecraftplugin.OperatorCommands;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class Start implements CommandExecutor {
    private Plugin plugin;

    public Start(){
       this.plugin = plugin;
    }
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        Player player = (Player) sender;
        if (cmd.getName().equalsIgnoreCase("start")&& sender instanceof Player) {
            if(player.isOp()) {
                player.performCommand("worldborder set 3000 500");
                player.performCommand("gamemode survival @a[name=!GlintSMPAdmin]");
                player.performCommand("title @a title \"\\u00A7ePhase 1\"");
                player.performCommand("title @a subtitle \"\\u00A7fThe Beginning of \\u00A7cEverything... \"");
                for(Player player1 : Bukkit.getOnlinePlayers() ){
                    player1.getWorld().playSound(player1.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 1, 1);
                }
            }

        }
        return false;
    }
}
