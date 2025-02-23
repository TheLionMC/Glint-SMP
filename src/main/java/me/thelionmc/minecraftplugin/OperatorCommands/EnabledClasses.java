package me.thelionmc.minecraftplugin.OperatorCommands;

import me.thelionmc.minecraftplugin.ClassManager;
import me.thelionmc.minecraftplugin.Groups.AbilityGroup;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class EnabledClasses implements CommandExecutor {
    ClassManager cm;
    public EnabledClasses (ClassManager cm) {
        this.cm = cm;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        StringBuilder builder = new StringBuilder("Enabled Classes: ");
        for(AbilityGroup group : cm.enabledClasses) {
            builder.append(group.displayName() + ", ");
        }
        commandSender.sendMessage(builder + "");

        return true;
    }
}
