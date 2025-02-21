package me.thelionmc.minecraftplugin.OperatorCommands;

import me.thelionmc.minecraftplugin.Abilities.Ability;
import me.thelionmc.minecraftplugin.ClassManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class SetCooldown implements CommandExecutor {
    ClassManager classManager;
    public SetCooldown(ClassManager classManager) {
        this.classManager = classManager;
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("setcooldown")) {
            if(!(args.length >= 3)) {
                sender.sendMessage(ChatColor.BLUE + "[GlintSMP] " + ChatColor.RED + "3 Arguments Required!");
                return true;
            }

            OfflinePlayer target = Bukkit.getPlayer(args[0]);

            if(target == null) {
                sender.sendMessage(ChatColor.BLUE + "[GlintSMP] " + ChatColor.RED + "Targeted player doesn't exist!");
                return true;
            }

            Player player = target.getPlayer();


            int index;
            try {
                index = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                sender.sendMessage(ChatColor.BLUE + "[GlintSMP] " + ChatColor.RED +"Invalid ability selection: Invalid Integer!");
                return true;
            }

            List<Ability> list = classManager.getPlayerGroup(player.getUniqueId()).getAbilities();

            if(index > list.size()) {
                sender.sendMessage(ChatColor.BLUE + "[GlintSMP] " + ChatColor.RED +"Invalid ability Selection: Not in bounds!");
                return true;
            }

            Ability selectedAbility = classManager.getPlayerGroup(player.getUniqueId()).getAbilities().get(index - 1);

            if(!args[2].matches("\\d{2}:\\d{2}")) {
                sender.sendMessage(ChatColor.BLUE + "[GlintSMP] " + ChatColor.RED +"Invalid time: Invalid format (mm:ss)");
            }

            int min;
            int sec;

            try {
                min = Integer.parseInt(args[2].split(":")[0]);
                sec = Integer.parseInt(args[2].split(":")[1]);
            } catch (NumberFormatException e) {
                sender.sendMessage(ChatColor.BLUE + "[GlintSMP] " + ChatColor.RED +"Invalid time: Non integer!");
                return true;
            }

            sec += min * 60;
            int milsec = sec * 1000;

            selectedAbility.setCooldownSeconds(player.getUniqueId(), System.currentTimeMillis() - (selectedAbility.getDefaultCooldownSeconds() * 1000 - milsec));
            player.sendMessage(ChatColor.BLUE + "[GlintSMP] " + ChatColor.GREEN + "Cooldown has been successfully set.");
            return true;
        }
        return false;
    }
}
