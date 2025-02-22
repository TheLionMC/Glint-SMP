package me.thelionmc.minecraftplugin.OperatorCommands;

import me.thelionmc.minecraftplugin.ClassManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class ClassCommands implements CommandExecutor, TabCompleter {
    private ClassManager classManager;

    public ClassCommands (ClassManager classManager) {
        this.classManager = classManager;
    }

    private String glint = ChatColor.BLUE + "[GlintSMP] " + ChatColor.WHITE;
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("setclass") && sender instanceof Player) {
            if(args.length != 2) {
                sender.sendMessage(glint + ChatColor.RED + "Invalid Argument Length (2 args required)");
                return true;
            }
            if(!classManager.classMap.containsKey(args[1])) {
                sender.sendMessage(glint + ChatColor.RED + "Invalid Class Name!");
                return true;
            }

            OfflinePlayer target = Bukkit.getPlayer(args[0]);

            if(target == null) {
                sender.sendMessage(glint + ChatColor.RED + "Player doesn't exist!");
                return true;
            }

            classManager.setPlayerGroup(target.getUniqueId(), classManager.classMap.get(args[1]));
            sender.sendMessage(glint + ChatColor.GREEN + "Success! " + args[0] + "'s class is now \"" + classManager.getPlayerGroup(target.getUniqueId()).displayName() + "\"!");
        }

        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] args) {
        if (command.getName().equalsIgnoreCase("setclass") && args.length == 2) {
            ArrayList<String> list = new ArrayList<>();
            list.addAll(classManager.classMap.keySet());
            return list;
        }
        return null;
    }
}
