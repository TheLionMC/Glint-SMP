package me.thelionmc.minecraftplugin.customItems;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

public class Shard extends ItemStack {
    public Shard() {
        super(Material.NETHER_STAR, 1);
        ItemMeta meta = getItemMeta();
        meta.setDisplayName(ChatColor.RED + "Shard");
        meta.addEnchant(Enchantment.UNBREAKING, 1, false);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        setItemMeta(meta);
    }

    public void initialize(Plugin plugin) {
        ShapedRecipe shapedRecipe = new ShapedRecipe(new NamespacedKey(plugin, "Shard"), this);
        shapedRecipe.shape("DND", "NSN", "DND");
        shapedRecipe.setIngredient('D', new RecipeChoice.MaterialChoice(Material.DIAMOND_BLOCK));
        shapedRecipe.setIngredient('S', new RecipeChoice.MaterialChoice(Material.NETHER_STAR));
        shapedRecipe.setIngredient('N', new RecipeChoice.MaterialChoice(Material.NETHERITE_INGOT));
        Bukkit.getServer().addRecipe(shapedRecipe);
    }
}
