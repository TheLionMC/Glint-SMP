package me.thelionmc.minecraftplugin.Commands;

import me.thelionmc.minecraftplugin.GlintSMP;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.UUID;

public class invseecommand implements CommandExecutor, Listener {
    public final HashMap<UUID, Player> inventoryViewers = new HashMap<>();
    private final HashMap<UUID, Integer> updateTasks = new HashMap<>();
    private GlintSMP mainClass;
    private Plugin plugin;

    public invseecommand(Plugin plugin, GlintSMP mainClass) {
        this.plugin = plugin;
        this.mainClass = mainClass;
    }

    public void addViewer(Player viewer, Player target) {
        inventoryViewers.put(viewer.getUniqueId(), target);
    }

    public Player getTargetPlayer(UUID viewerUUID) {
        return inventoryViewers.get(viewerUUID);
    }

    public void removeViewer(Player viewer) {
        inventoryViewers.remove(viewer.getUniqueId());
        if (updateTasks.containsKey(viewer.getUniqueId())) {
            Bukkit.getScheduler().cancelTask(updateTasks.get(viewer.getUniqueId()));
            updateTasks.remove(viewer.getUniqueId());
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("invsee")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage("Only players can use this command.");
                return true;
            }

            Player player = (Player) sender;

            if (args.length != 1) {
                player.sendMessage("Usage: /invsee <player>");
                return true;
            }

            Player target = Bukkit.getPlayer(args[0]);
            if (target == null || !target.isOnline()) {
                player.sendMessage("The player " + args[0] + " is not online.");
                return true;
            }

            Inventory inv = Bukkit.createInventory(player, 45, "Viewing " + target.getName() + "'s Inventory");
            updateInventoryContents(player, target, inv);

            addViewer(player, target);
            player.openInventory(inv);

            player.sendMessage("Opening " + target.getName() + "'s inventory, armor, offhand, health, and hunger.");

            int taskID = new BukkitRunnable() {
                @Override
                public void run() {
                    if (player.getOpenInventory() != null && player.getOpenInventory().getTitle().contains("Viewing")) {
                        updateInventoryContents(player, target, player.getOpenInventory().getTopInventory());
                    } else {
                        removeViewer(player);
                        cancel();
                    }
                }
            }.runTaskTimer(plugin, 0L, 20L).getTaskId();

            updateTasks.put(player.getUniqueId(), taskID);

            return true;
        }
        return false;
    }

    private void updateInventoryContents(Player viewer, Player target, Inventory inv) {
        for (int i = 0; i < 36; i++) {
            inv.setItem(i, target.getInventory().getItem(i));
        }

        ItemStack[] armor = target.getInventory().getArmorContents();
        inv.setItem(36, armor[3]);
        inv.setItem(37, armor[2]);
        inv.setItem(38, armor[1]);
        inv.setItem(39, armor[0]);
        inv.setItem(40, target.getInventory().getItemInOffHand());

        ItemStack healthItem = new ItemStack(Material.REDSTONE);
        ItemMeta healthMeta = healthItem.getItemMeta();
        healthMeta.setDisplayName(ChatColor.GREEN + "Health: " + target.getHealth() + " / 20");
        healthItem.setItemMeta(healthMeta);
        inv.setItem(41, healthItem);

        ItemStack hungerItem = new ItemStack(Material.COOKED_BEEF);
        ItemMeta hungerMeta = hungerItem.getItemMeta();
        hungerMeta.setDisplayName(ChatColor.YELLOW + "Hunger: " + target.getFoodLevel() + " / 20");
        hungerItem.setItemMeta(hungerMeta);
        inv.setItem(42, hungerItem);

        ItemStack enderChestItem = new ItemStack(Material.ENDER_CHEST);
        ItemMeta enderChestMeta = enderChestItem.getItemMeta();
        enderChestMeta.setDisplayName(ChatColor.LIGHT_PURPLE + "View Ender Chest");
        enderChestItem.setItemMeta(enderChestMeta);
        inv.setItem(43, enderChestItem);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getView().getTitle().contains("Viewing")) {
            Player viewer = (Player) event.getWhoClicked();
            Inventory clickedInventory = event.getClickedInventory();
            if (clickedInventory == null || !clickedInventory.equals(event.getInventory())) {
                return;
            }

            int slot = event.getSlot();

            if (slot == 41 || slot == 42 || slot == 43) {
                event.setCancelled(true);
                if (slot == 43) {
                    UUID viewerUUID = viewer.getUniqueId();
                    Player target = getTargetPlayer(viewerUUID);
                    if (target != null && target.isOnline()) {
                        viewer.openInventory(target.getEnderChest());
                        viewer.sendMessage(ChatColor.GREEN + "You are now viewing " + target.getName() + "'s Ender Chest.");
                    } else {
                        viewer.sendMessage(ChatColor.RED + "Target player is no longer online.");
                    }
                }
                return;
            }

            UUID viewerUUID = viewer.getUniqueId();
            Player target = getTargetPlayer(viewerUUID);

            if (target != null && target.isOnline()) {
                if (slot >= 0 && slot < 36) {
                    target.getInventory().setItem(slot, event.getCurrentItem());
                } else if (slot == 36 || slot == 37 || slot == 38 || slot == 39) {
                    ItemStack[] armor = target.getInventory().getArmorContents();
                    armor[39 - slot] = event.getCurrentItem();
                    target.getInventory().setArmorContents(armor);
                } else if (slot == 40) {
                    target.getInventory().setItemInOffHand(event.getCurrentItem());
                }

                target.updateInventory();
            }
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        Player viewer = (Player) event.getPlayer();
        if (inventoryViewers.containsKey(viewer.getUniqueId())) {
            removeViewer(viewer);
        }
    }
}
