package me.thelionmc.minecraftplugin.Events;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemStack;

public class ForbiddenCraftingEvent implements Listener {
    @EventHandler
    public void onPrepareItemCraft(PrepareItemCraftEvent event) {
        if (event.getRecipe() == null) return;

        ItemStack result = event.getRecipe().getResult();
        if (result == null) return;

        Material resultType = result.getType();
        if (resultType == Material.NETHERITE_SWORD ||
                resultType == Material.NETHERITE_HELMET ||
                resultType == Material.NETHERITE_CHESTPLATE ||
                resultType == Material.NETHERITE_LEGGINGS ||
                resultType == Material.NETHERITE_BOOTS) {
            event.getInventory().setResult(null);
        }
    }
}
