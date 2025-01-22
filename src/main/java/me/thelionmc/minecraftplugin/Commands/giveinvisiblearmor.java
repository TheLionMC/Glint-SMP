package me.thelionmc.minecraftplugin.Commands;


import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class giveinvisiblearmor implements CommandExecutor {
    public ItemStack getInvisibleHelmet() {
        ItemStack helmet = new ItemStack(Material.NETHERITE_HELMET);
        ItemMeta meta = helmet.getItemMeta();
        meta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 5, true);
        meta.addEnchant(Enchantment.DURABILITY, 5, true);
        meta.addEnchant(Enchantment.MENDING, 1, true);
        meta.setDisplayName(ChatColor.RED + "Tier 3 Helmet");
        meta.setUnbreakable(true);
        helmet.setItemMeta(meta);
        return helmet;
    }

    public ItemStack getInvisibleChestplate() {
        ItemStack chestplate = new ItemStack(Material.NETHERITE_CHESTPLATE);
        ItemMeta meta = chestplate.getItemMeta();
        meta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 5, true);
        meta.addEnchant(Enchantment.DURABILITY, 5, true);
        meta.addEnchant(Enchantment.MENDING, 1, true);
        meta.setDisplayName(ChatColor.RED + "Tier 3 Chestplate");
        meta.setUnbreakable(true);
        chestplate.setItemMeta(meta);
        return chestplate;
    }

    public ItemStack getInvisibleLeggings() {
        ItemStack leggings = new ItemStack(Material.NETHERITE_LEGGINGS);
        ItemMeta meta = leggings.getItemMeta();
        meta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 5, true);
        meta.addEnchant(Enchantment.DURABILITY, 5, true);
        meta.addEnchant(Enchantment.MENDING, 1, true);
        meta.setDisplayName(ChatColor.RED + "Tier 3 Leggins");
        meta.setUnbreakable(true);
        leggings.setItemMeta(meta);
        return leggings;
    }

    public ItemStack getInvisibleBoots() {
        ItemStack boots = new ItemStack(Material.NETHERITE_BOOTS);
        ItemMeta meta = boots.getItemMeta();
        meta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 5, true);
        meta.addEnchant(Enchantment.DURABILITY, 5, true);
        meta.addEnchant(Enchantment.MENDING, 1, true);
        meta.setDisplayName(ChatColor.RED + "Tier 3 Boots");
        meta.setUnbreakable(true);
        boots.setItemMeta(meta);
        return boots;
    }

    private ItemStack[] getInvisibleArmorSet() {
        return new ItemStack[]{getInvisibleHelmet(), getInvisibleChestplate(), getInvisibleLeggings(), getInvisibleBoots()};
    }


    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("giveinvisiblearmor") && sender instanceof Player) {
            Player player = (Player) sender;

            if (player.isOp()) {
                player.getInventory().addItem(getInvisibleArmorSet());
                return true;
            } else {
                player.sendMessage(ChatColor.RED + "You need to be an operator to use this command.");
            }
            return false;
        }
        return true;
    }

}