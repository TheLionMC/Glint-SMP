package me.thelionmc.minecraftplugin.OperatorCommands;

import me.thelionmc.minecraftplugin.Abilities.Ability;
import me.thelionmc.minecraftplugin.ClassManager;
import me.thelionmc.minecraftplugin.GlintSMP;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class SetCommand implements CommandExecutor, TabCompleter {

    ClassManager classManager;
    Plugin plugin;
    GlintSMP main;
    private final List<String> args1option = Arrays.asList("ability", "classstatus", "class", "cooldown", "pvp");
    private final List<String> argspvpoption = Arrays.asList("enabled", "disabled");
    private final List<String> argsabilitynumbers = Arrays.asList("1", "2", "3");
    private final List <String> argsclasses = Arrays.asList("Assassin", "Mischief", "Farmer", "Warrior","Medic","Ninja","Angel","Aqua","Wizard");


    public SetCommand(GlintSMP main, Plugin plugin, ClassManager classManager) {
        this.main = main;
        this.plugin = plugin;
        this.classManager = classManager;
    }


    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("set")) {
            if (args.length <= 1) {
                sender.sendMessage(ChatColor.BLUE + "[GlintSMP] " + ChatColor.RED + "Too little Args!");
                return true;
            }
            String action = args[1].toLowerCase();
            if (!args1option.contains(action)) {
                sender.sendMessage(ChatColor.BLUE + "[GlintSMP] " + ChatColor.RED + "Not a valid /set argument!");
                return true;
            }
            switch (action) {
                case "ability":
                    if (args.length < 3) {
                        //sender.sendMessage(ChatColor.BLUE + "[Glint SMP] " + ChatColor.GREEN + ChatColor.RED + "Usage: /gettool coreprotect save [name]");
                        return true;
                    }
                    break;

                case "classstatus":
                    if (args.length < 3) {
                        return true;
                    }
                    break;

                case "class":
                    if (args.length < 3) {
                        //player.sendMessage(ChatColor.BLUE + "[Glint SMP] " + ChatColor.GREEN + ChatColor.RED + "Usage: /gettool coreprotect delete [name]");
                        return true;
                    }
                    break;
                case "cooldown":
                    if (!(args.length >= 3)) {
                        sender.sendMessage(ChatColor.BLUE + "[GlintSMP] " + ChatColor.RED + "3 Arguments Required!");
                        return true;
                    }

                    OfflinePlayer target = Bukkit.getPlayer(args[0]);

                    if (target == null) {
                        sender.sendMessage(ChatColor.BLUE + "[GlintSMP] " + ChatColor.RED + "Targeted player doesn't exist!");
                        return true;
                    }

                    Player player1 = target.getPlayer();


                    int index;
                    try {
                        index = Integer.parseInt(args[3]);
                    } catch (NumberFormatException e) {
                        sender.sendMessage(ChatColor.BLUE + "[GlintSMP] " + ChatColor.RED + "Invalid ability selection: Invalid Integer!");
                        return true;
                    }

                    List<Ability> list = classManager.getPlayerGroup(player1.getUniqueId()).getAbilities();

                    if (index > list.size()) {
                        sender.sendMessage(ChatColor.BLUE + "[GlintSMP] " + ChatColor.RED + "Invalid ability Selection: Not in bounds!");
                        return true;
                    }

                    Ability selectedAbility = classManager.getPlayerGroup(player1.getUniqueId()).getAbilities().get(index - 1);

                    if (!args[2].matches("\\d{2}:\\d{2}")) {
                        sender.sendMessage(ChatColor.BLUE + "[GlintSMP] " + ChatColor.RED + "Invalid time: Invalid format (mm:ss)");
                    }

                    int min;
                    int sec;

                    try {
                        min = Integer.parseInt(args[2].split(":")[0]);
                        sec = Integer.parseInt(args[2].split(":")[1]);
                    } catch (NumberFormatException e) {
                        sender.sendMessage(ChatColor.BLUE + "[GlintSMP] " + ChatColor.RED + "Invalid time: Non integer!");
                        return true;
                    }

                    sec += min * 60;
                    int milsec = sec * 1000;

                    selectedAbility.setCooldownSeconds(player1.getUniqueId(), System.currentTimeMillis() - (selectedAbility.getDefaultCooldownSeconds() * 1000 - milsec));
                    sender.sendMessage(ChatColor.BLUE + "[GlintSMP] " + ChatColor.GREEN + "Cooldown has been successfully set.");
                    return true;
                case "pvp":
                    Player player = (Player) sender;
                    World playerWorld = player.getWorld();
                    if (args.length == 2) {
                        if (args[2].equalsIgnoreCase("true")) {
                            playerWorld.setPVP(true);
                            for (Player p : Bukkit.getOnlinePlayers()) {
                                p.sendTitle(ChatColor.RED + "PvP Enabled", ChatColor.YELLOW + "You may now kill other players!");
                            }
                        } else if (args[2].equalsIgnoreCase("false")) {
                            playerWorld.setPVP(false);
                            for (Player p : Bukkit.getOnlinePlayers()) {
                                p.sendTitle(ChatColor.GREEN + "PvP Disabled", ChatColor.YELLOW + "You are save, for now...");
                                return true;
                            }
                        } else {
                            player.sendMessage(ChatColor.BLUE + "[GlintSMP] " + ChatColor.YELLOW + "Usage: /setpvp [true/false]");
                        }
                    } else {
                        player.sendMessage(ChatColor.BLUE + "[GlintSMP] " + ChatColor.YELLOW + "This Set-Term requires 3 Args");
                    }

                    break;

                default:
                    sender.sendMessage(ChatColor.BLUE + "[Glint SMP] " + ChatColor.GREEN + ChatColor.RED + "Invalid coreprotect action. Use save, abandon, or delete.");
                    break;
            }
            return false;
        }
        return false;
    }
    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String alias, String[] args) {
        if(args.length == 1) {
            String input = args[0].toLowerCase();
            List<String> options = new ArrayList<>();
            for (String arg1 : args1option) {
                if (arg1.startsWith(input)) {
                    options.add(arg1);
                }
            }
            return options;
        }
        if (args.length == 2) {
            String firstarg = args[1].toLowerCase();
            String input = args[1].toLowerCase();
            List<String> options = new ArrayList<>();
            if (firstarg.equalsIgnoreCase("ability")) {
                for (String argclass : argsclasses) {
                    if (argclass.startsWith(input)){
                        options.add(argclass);
                    }
                }
            } else if (firstarg.equalsIgnoreCase("classstatus")) {
                for (String argclass : argsclasses) {
                    if (argclass.startsWith(input)){
                        options.add(argclass);
                    }
                }
            } else if (firstarg.equalsIgnoreCase("class")) {
                for (String argclass : argsclasses) {
                    if (argclass.startsWith(input)){
                        options.add(argclass);
                    }
                }
            } else if (firstarg.equalsIgnoreCase("cooldown")) {
            } else if (firstarg.equalsIgnoreCase("pvp")) {
                for (String argpvp : argspvpoption) {
                    if (argpvp.startsWith(input)) {
                        options.add(argpvp);
                    }
                }
            return options;
            }
        }
        if (args.length == 3) {
            String firstarg = args[0].toLowerCase();
            String input = args[1].toLowerCase();
            List<String> options = new ArrayList<>();
            if (firstarg.equalsIgnoreCase("ability")) {

            }
        }
        return List.of();
    }
}
