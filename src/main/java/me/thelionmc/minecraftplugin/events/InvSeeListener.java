package me.thelionmc.minecraftplugin.events;

import me.thelionmc.minecraftplugin.Commands.invseecommand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class InvSeeListener implements Listener {

    private final invseecommand invseeCommandInstance;
    public InvSeeListener(invseecommand invseeCommandInstance) {
        this.invseeCommandInstance = invseeCommandInstance;
    }
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getView().getTitle().startsWith("Viewing")) {
            Player viewer = (Player) event.getWhoClicked();
            Player target = invseeCommandInstance.getTargetPlayer(viewer.getUniqueId());
            if (target != null) {
                Inventory targetInventory = target.getInventory();
                int slot = event.getRawSlot();
                if (slot < event.getInventory().getSize()) {
                    ItemStack clickedItem = event.getCurrentItem();
                    ItemStack cursorItem = event.getCursor();
                    if (slot < 36) {
                        targetInventory.setItem(slot, cursorItem);
                    } else if (slot == 36) {
                        ItemStack[] armorContents = target.getInventory().getArmorContents();
                        armorContents[3] = cursorItem;
                        target.getInventory().setArmorContents(armorContents);
                    } else if (slot == 37) {
                        ItemStack[] armorContents = target.getInventory().getArmorContents();
                        armorContents[2] = cursorItem;
                        target.getInventory().setArmorContents(armorContents);
                    } else if (slot == 38) {
                        ItemStack[] armorContents = target.getInventory().getArmorContents();
                        armorContents[1] = cursorItem;
                        target.getInventory().setArmorContents(armorContents);
                    } else if (slot == 39) {
                        ItemStack[] armorContents = target.getInventory().getArmorContents();
                        armorContents[0] = cursorItem;
                        target.getInventory().setArmorContents(armorContents);
                    } else if (slot == 40) {
                        target.getInventory().setItemInOffHand(cursorItem);
                    }

                    target.updateInventory();
                    if (event.isShiftClick()) {
                        event.setCancelled(true);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (event.getView().getTitle().startsWith("Viewing")) {
            Player viewer = (Player) event.getPlayer();
            invseeCommandInstance.removeViewer(viewer);
        }
    }
}
