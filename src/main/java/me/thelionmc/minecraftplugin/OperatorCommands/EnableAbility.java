package me.thelionmc.minecraftplugin.OperatorCommands;

import me.thelionmc.minecraftplugin.Abilities.Ability;
import me.thelionmc.minecraftplugin.ClassManager;
import me.thelionmc.minecraftplugin.Groups.AbilityGroup;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class EnableAbility implements CommandExecutor {
    ClassManager cm;

    public EnableAbility(ClassManager cm) {
        this.cm = cm;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if(args.length != 2) {
            sender.sendMessage(ChatColor.BLUE + "[GlintSMP] " + ChatColor.RED + "2 Arguments Required!");
            return true;
        }

        if(!cm.classMap.containsKey(args[0])) {
            sender.sendMessage(ChatColor.BLUE + "[GlintSMP] " +ChatColor.RED + "Invalid class name!");
            return true;
        }

        int index;
        try {
            index = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            sender.sendMessage(ChatColor.BLUE + "[GlintSMP] " +ChatColor.RED + "Invalid ability index; Index must be an integer!");
            return true;
        }

        AbilityGroup group = cm.classMap.get(args[0]);

        if(index > group.getAbilities().size()) {
            sender.sendMessage(ChatColor.BLUE + "[GlintSMP] " +ChatColor.RED + "Invalid ability index; Index is too large! (Index STARTS at 1!)");
            return true;
        }

        Ability ability = group.getAbility(index - 1);

        if(ability.isEnabled()) {
            sender.sendMessage(ChatColor.BLUE + "[GlintSMP] " +ChatColor.RED + "Ability is already enabled!");
            return true;
        }

        ability.setEnabled(true);
        sender.sendMessage(ChatColor.BLUE + "[GlintSMP] " +ChatColor.GREEN + "Success!");
        sender.sendMessage(ChatColor.BLUE + "[GlintSMP] " +ChatColor.AQUA + "Enabled Abilities of the " + group.displayName() + " class:");
        for(Ability a : group.getAbilities()) {
            if(a.isEnabled()) {
                sender.sendMessage(ChatColor.BLUE + "[GlintSMP] " + ChatColor.BLUE + a.displayName());
            }
        }
        return true;
    }
}
