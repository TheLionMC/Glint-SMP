package me.thelionmc.minecraftplugin.Commands;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class preparecommand implements CommandExecutor {
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        Player player = (Player) sender;
        if (cmd.getName().equalsIgnoreCase("whitelistall") && sender instanceof Player) {
            if(player.isOp()) {
                player.performCommand("whitelist add TheLionMCYT");
                player.performCommand("whitelist add JelloMIN");
                player.performCommand("whitelist add RocraftYT");
                player.performCommand("whitelist add Fancyfied");
                player.performCommand("whitelist add Fonzathy");
                player.performCommand("whitelist add Bravure");
                player.performCommand("whitelist add _leat");
                player.performCommand("whitelist add ItzKorexx");
                player.performCommand("whitelist add Trasaahh");
                player.performCommand("whitelist add Mrsmoresking");
                player.performCommand("whitelist add Tide4K");
                player.performCommand("whitelist add Lucakoo");
                player.performCommand("whitelist add Pwngu");
                player.performCommand("whitelist add AdoreHybrid");
                player.performCommand("whitelist add _cvpher");
                player.performCommand("whitelist add Kwilver");
                player.performCommand("whitelist add __rancor");
                player.performCommand("whitelist add MasterCaty");
                player.performCommand("whitelist add 3ls_lincoln");
                player.performCommand("whitelist add Fyrenity");
                player.performCommand("whitelist add Dude2ds");
                player.performCommand("whitelist add Beazley_");
                player.performCommand("whitelist add RavenTCWinters");
                player.performCommand("whitelist add vyc_");
                player.performCommand("whitelist add Mikymoosy");
                player.performCommand("whitelist add Flo4ter");
                player.performCommand("whitelist add lenaB2808");
                player.performCommand("whitelist add V_Tag");
                player.performCommand("whitelist add Kio_na");
                player.performCommand("whitelist add Pwngu");
                player.performCommand("whitelist add 1polly");
                player.performCommand("whitelist add nepituni");
                player.performCommand("whitelist add ItzKorexx");
                player.performCommand("whitelist add Odyssey1337x");
                player.performCommand("whitelist add IQRedPanda");
                player.performCommand("whitelist add dogpkf");
                for(Player player1 : Bukkit.getOnlinePlayers() ){
                    player1.getWorld().playSound(player1.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 1, 1);
                }
            }

        }
        return false;
    }
}
