package me.thelionmc.minecraftplugin.customItems;

import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public class isLegendary {
    public ArrayList<ItemStack> legendaryItems = new ArrayList<>();
    CleansingBow cleansingBow;
    BrisknessAxe brisknessAxe;
    FreezingTrident freezingTrident;

    public isLegendary(CleansingBow a, BrisknessAxe b, FreezingTrident c) {
        this.cleansingBow = a;
        this.brisknessAxe = b;
        this.freezingTrident = c;
        legendaryItems.add(cleansingBow.TheCleansingBow());
        legendaryItems.add(brisknessAxe.TheBrisknessAxe());
        legendaryItems.add(freezingTrident.TheFreezingTrident());
    }

    public boolean isLegendaryItem(ItemStack item) {
        if(legendaryItems.isEmpty()) {
            return false;
        }
        if(item != null) {
            for (ItemStack legendaryItem : legendaryItems) {
                if (item.isSimilar(legendaryItem)) {
                    return true;
                } else {
                    return false;
                }
            }
        }
        return false;
    }
}
