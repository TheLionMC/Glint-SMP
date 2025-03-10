package me.thelionmc.minecraftplugin.OperatorCommands;

import me.thelionmc.minecraftplugin.ClassManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.List;

public class EnableClass implements CommandExecutor, TabCompleter {
    ClassManager cm;
    public EnableClass(ClassManager cm) {
        this.cm = cm;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if(!cm.classMap.containsKey(strings[0])) {
            commandSender.sendMessage(ChatColor.BLUE + "[GlintSMP] " + ChatColor.RED + "This class doesn't exist!");
            return true;
        }
        cm.enablePlayerGroup(cm.classMap.get(strings[0]));
        commandSender.sendMessage(ChatColor.BLUE + "[GlintSMP] " + ChatColor.RED + "Success!");
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] args) {
        return new ArrayList<>(cm.classMap.keySet());
    }
}
