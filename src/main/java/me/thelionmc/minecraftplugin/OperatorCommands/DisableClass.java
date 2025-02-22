package me.thelionmc.minecraftplugin.OperatorCommands;

import me.thelionmc.minecraftplugin.ClassManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.List;

public class DisableClass implements CommandExecutor, TabCompleter {
    ClassManager cm;
    public DisableClass(ClassManager cm) {
        this.cm = cm;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if(!cm.classMap.containsKey(strings[0])) {
            commandSender.sendMessage("This class doesn't exist!");
            return true;
        }
        cm.disablePlayerGroup(cm.classMap.get(strings[0]));
        commandSender.sendMessage("Success!");
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] args) {
        return new ArrayList<>(cm.classMap.keySet());
    }
}
