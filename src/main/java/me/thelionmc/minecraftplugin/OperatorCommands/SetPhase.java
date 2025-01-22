package me.thelionmc.minecraftplugin.OperatorCommands;

import me.thelionmc.minecraftplugin.GlintSMP;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashSet;
import java.util.Set;

public class SetPhase implements CommandExecutor {
    private GlintSMP main;
    private final Set<Player> sneakingPlayers = new HashSet<>();
    private BukkitRunnable particleTask;

    public SetPhase(GlintSMP main) {
        this.main = main;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("setphase")) {
            if(args.length != 2) {
                sender.sendMessage("Two args!");
                return true;
            }

            int phaseNumber;
            boolean value;
            try {
                phaseNumber = Integer.parseInt(args[0]);
            } catch (Exception e) {
                sender.sendMessage("Invalid Phase!");
                return true;
            }

            if(phaseNumber > 3) {
                sender.sendMessage("Phase " + phaseNumber + " doesn't exist!");
                return true;
            }

            if(!args[1].equalsIgnoreCase("true") && !args[1].equalsIgnoreCase("false")) {
                sender.sendMessage("Argument two must be true or false.");
                return true;
            }

            if(args[1].equalsIgnoreCase("true")) {
                if(phaseNumber == 1) {
                    //main.setProgressionOptions("Phases.Phase1", true);
                    sender.sendMessage("Done!");
                } else if(phaseNumber == 2) {
                    //main.setProgressionOptions("Phases.Phase2", true);
                    sender.sendMessage("Done!");
                } if(phaseNumber == 3) {
                    sender.sendMessage("Currently Unavailable!");
                }
            } else {
                if(phaseNumber == 1) {
                    //main.setProgressionOptions("Phases.Phase1", false);
                    sender.sendMessage("Done!");
                } else if(phaseNumber == 2) {
                    //main.setProgressionOptions("Phases.Phase2", false);
                    sender.sendMessage("Done!");
                } else if(phaseNumber == 3) {
                    sender.sendMessage("Currently Unavailable!");
                }
            }
            return true;
        } else if (cmd.getName().equalsIgnoreCase("getphase")) {
            if(args.length != 1) {
                sender.sendMessage("One arg!");
                return true;
            }

            int phaseNumber;
            try {
                phaseNumber = Integer.parseInt(args[0]);
            } catch (Exception e) {
                sender.sendMessage("Invalid Phase!");
                return true;
            }

            if(phaseNumber > 3) {
                sender.sendMessage("Phase " + phaseNumber + " doesn't exist!");
                return true;
            }

            if(phaseNumber == 1) {
                //if(main.checkProgression("Phases.Phase1")) {
                    sender.sendMessage("true");
                } else {
                    sender.sendMessage("false");
                }
            //} else if(phaseNumber == 2) {
                //if(main.checkProgression("Phases.Phase2")) {
                    sender.sendMessage("true");
                } else {
                    sender.sendMessage("false");
                }
            //} else if(phaseNumber == 3) {
                sender.sendMessage("Currently Unavailable!");
           // }
            return true;
        //}
        //return true;
    }
}
