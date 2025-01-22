package me.thelionmc.minecraftplugin.Commands;


import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class givePortalObsidian implements CommandExecutor {
    private final String portalObsidianName = ChatColor.YELLOW + "Portal Obsidian";
    public ItemStack portalObsidian() {
        ItemStack item = new ItemStack(Material.CRYING_OBSIDIAN);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(portalObsidianName);
        item.setItemMeta(meta);
        return item;
    }
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("giveportalobsidian") && sender instanceof Player) {
            Player player = (Player) sender;

            if (player.isOp()) {
                player.getInventory().addItem(portalObsidian());
                return true;
            } else {
                player.sendMessage(ChatColor.RED + "You need to be an operator to use this command.");
            }
            return false;
        }
        return true;
    }

}