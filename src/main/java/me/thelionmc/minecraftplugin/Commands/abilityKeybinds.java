package me.thelionmc.minecraftplugin.Commands;

import me.thelionmc.minecraftplugin.ActionBarManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class abilityKeybinds implements CommandExecutor {
    ActionBarManager abilityManager;

    public abilityKeybinds(ActionBarManager abilityManager) {
        this.abilityManager = abilityManager;
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(label.equalsIgnoreCase("useability") && sender instanceof Player) {
            abilityManager.useAbility((Player) sender);
        } else if(label.equalsIgnoreCase("switchability") && sender instanceof Player) {
            abilityManager.nextSelectedAbility((Player) sender);
        }
        return true;
    }
}
