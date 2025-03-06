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
    private final List<String> argsworlds = Arrays.asList("Overall", "Normal", "Nether", "End");


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
            String action = args[0].toLowerCase();
            if (!args1option.contains(action)) {
                sender.sendMessage(ChatColor.BLUE + "[GlintSMP] " + ChatColor.RED + "Not a valid /set argument!");
                return true;
            }
            switch (action) {
                case "ability":
                    if (args.length < 3) {
                        sender.sendMessage("setability isnt really set up yet you gott wait young chat");
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
                    if (!(sender instanceof Player)) {
                        sender.sendMessage(ChatColor.RED + "Only players can use this command!");
                        return true;
                    }
                    // Now expect at least 3 arguments: pvp <world/overall> [enabled/disabled]
                    if (args.length < 3) {
                        sender.sendMessage(ChatColor.BLUE + "[GlintSMP] " + ChatColor.YELLOW + "Usage: /set pvp <Overworld/Nether/End/Overall> [enabled/disabled]");
                        return true;
                    }

                    Player player = (Player) sender;
                    String targetArg = args[1].toLowerCase();
                    String pvpSetting = args[2].toLowerCase();

                    boolean enable;
                    if (pvpSetting.equals("enabled")) {
                        enable = true;
                    } else if (pvpSetting.equals("disabled")) {
                        enable = false;
                    } else {
                        sender.sendMessage(ChatColor.BLUE + "[GlintSMP] " + ChatColor.YELLOW + "Usage: /set pvp <Overworld/Nether/End/Overall> [enabled/disabled]");
                        return true;
                    }

                    if (targetArg.equals("Overall")) {
                        for (World world : Bukkit.getWorlds()) {
                            world.setPVP(enable);
                        }
                        for (Player p : Bukkit.getOnlinePlayers()) {
                            if (enable) {
                                p.sendTitle(ChatColor.RED + "PvP Enabled", ChatColor.YELLOW + "You may now kill other players!");
                            } else {
                                p.sendTitle(ChatColor.GREEN + "PvP Disabled", ChatColor.YELLOW + "You are safe, for now...");
                            }
                        }
                        sender.sendMessage(ChatColor.BLUE + "[GlintSMP] " + ChatColor.GREEN + "PvP has been " + (enable ? "enabled" : "disabled") + " for all worlds.");
                    } else if (targetArg.equals("normal") || targetArg.equals("nether") || targetArg.equals("end")) {
                        String properName = "";
                        switch (targetArg) {
                            case "normal":
                                properName = "NORMAL";
                                break;
                            case "nether":
                                properName = "NETHER";
                                break;
                            case "end":
                                properName = "END";
                                break;
                        }
                        World targetWorld = Bukkit.getWorld(properName);
                        if (targetWorld == null) {
                            sender.sendMessage(ChatColor.BLUE + "[GlintSMP] " + ChatColor.RED + "World '" + properName + "' not found!");
                            return true;
                        }
                        targetWorld.setPVP(enable);
                        for (Player p : Bukkit.getOnlinePlayers()) {
                            if (enable) {
                                p.sendTitle(ChatColor.RED + "PvP Enabled", ChatColor.YELLOW + "You may now kill other players!");
                            } else {
                                p.sendTitle(ChatColor.GREEN + "PvP Disabled", ChatColor.YELLOW + "You are safe, for now...");
                            }
                        }
                        sender.sendMessage(ChatColor.BLUE + "[GlintSMP] " + ChatColor.GREEN + "PvP has been " + (enable ? "enabled" : "disabled") + " for world " + properName + ".");
                    } else {
                        sender.sendMessage(ChatColor.BLUE + "[GlintSMP] " + ChatColor.YELLOW + "Usage: /set pvp <Overworld/Nether/End/Overall> [enabled/disabled]");
                        return true;
                    }
                    return true;


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
        if (args.length == 1) {
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
            String firstArg = args[0].toLowerCase();
            String input = args[1].toLowerCase();
            List<String> options = new ArrayList<>();

            switch (firstArg) {
                case "ability":
                    for (String argsclass : argsclasses) {
                        if (argsclass.toLowerCase().startsWith(input)) {
                            options.add(argsclass);
                        }
                    }
                case "classstatus":
                case "class":
                    for (String argclass : argsclasses) {
                        if (argclass.toLowerCase().startsWith(input)) {
                            options.add(argclass);
                        }
                    }
                    break;

                case "cooldown":
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        if (player.getName().toLowerCase().startsWith(input)) {
                            options.add(player.getName());
                        }
                    }
                    break;

                case "pvp":
                    for (String worlds : argsworlds) {
                        if (worlds.startsWith(input)) {
                            options.add(worlds);
                        }
                    }
                    break;
            }
            return options;
        }

        if (args.length == 3) {
            String firstArg = args[0].toLowerCase();
            String input = args[2].toLowerCase();
            List<String> options = new ArrayList<>();

            if (firstArg.equals("ability")) {
                for (String number : argsabilitynumbers) {
                    if (number.startsWith(input)) {
                        options.add(number);
                    }
                }
            } else if (firstArg.equals("pvp")) {
                for (String pvpoptions : argspvpoption) {
                    if (pvpoptions.startsWith(input)) {
                        options.add(pvpoptions);
                    }
                }
            }
            return options;
        }

        return List.of();
    }
}
