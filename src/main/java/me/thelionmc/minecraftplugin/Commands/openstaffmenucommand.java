package me.thelionmc.minecraftplugin.Commands;

import com.sk89q.worldedit.bukkit.adapter.BukkitImplAdapter;
import me.thelionmc.minecraftplugin.GlintSMP;
import me.thelionmc.minecraftplugin.ShardManager;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

public class openstaffmenucommand implements CommandExecutor, Listener {
    private GlintSMP mainClass;
    private ShardManager shardManager;
    private UUID selectedPlayerUUID;
    private static final Map<UUID, UUID> playerSelections = new HashMap<>();
    private static final Set<UUID> allPlayers = new HashSet<>();

    public openstaffmenucommand(GlintSMP mainClass, ShardManager shardManager) {
        this.mainClass = mainClass;
        this.shardManager = shardManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("openstaffmenu")) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                if (player.isOp()) {
                    Inventory staffMenu = openstaffmenu(player, 1);
                    player.openInventory(staffMenu);
                    player.sendMessage(ChatColor.BLUE + "[Glint SMP] " + ChatColor.GREEN + "Server control opened.");
                    return true;
                } else {
                    sender.sendMessage(ChatColor.BLUE + "[Glint SMP] " +ChatColor.RED + "You do not have permission to use this command.");
                    return false;
                }
            } else {
                sender.sendMessage(ChatColor.BLUE + "[Glint SMP] " +ChatColor.RED + "This command can only be used by players.");
                return false;
            }
        }
        return false;
    }
    private static ItemStack createCustomItem(Material material, String name, ArrayList<String> lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }


    public Inventory openstaffmenu(Player player,int i) {
        UUID selectedPlayerUUID = playerSelections.get(player.getUniqueId());

        Inventory staffMenu = Bukkit.createInventory(null, 54, "Staff Menu");

        ItemStack item = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(" ");
        item.setItemMeta(meta);
        if (i > 5 || i < 1) {
            i = 1;
        }

        for (int j = 0; j < 18; j++) {
            if (j % 2 != 0) {
                staffMenu.setItem(j, item);
            }
        }

        ItemStack playerControls = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta playerControlsMeta = (SkullMeta) playerControls.getItemMeta();
        playerControlsMeta.setDisplayName(ChatColor.GREEN + "Player Controls");
        if (selectedPlayerUUID != null) {
            OfflinePlayer selectedPlayer = Bukkit.getOfflinePlayer(selectedPlayerUUID);
            playerControlsMeta.setOwningPlayer(selectedPlayer);
            playerControlsMeta.setLore(List.of(ChatColor.YELLOW + "Selected Player: " + selectedPlayer.getName()));
        }
        playerControls.setItemMeta(playerControlsMeta);
        if (i == 1) {
            playerControls.addUnsafeEnchantment(Enchantment.PROTECTION_FALL, 1);
            List<String> lore = new ArrayList<>();
            lore.add(ChatColor.YELLOW + "Selected Tab");
            playerControlsMeta.setLore(lore);
            ArrayList<String> lore1 = new ArrayList<>();
            lore1.add(ChatColor.GREEN + "Shows The Selected Players Inventory");
            lore1.add(ChatColor.ITALIC + "Coded by Council");
            ArrayList<String> lore2 = new ArrayList<>();
            lore2.add(ChatColor.DARK_PURPLE + "Gives Selected Player a selected Potion Effect");
            lore2.add("");
            ArrayList<String> lore3 = new ArrayList<>();
            lore3.add(ChatColor.RED + "Shows you the Enderchest of the selected player");
            lore3.add("");
            ArrayList<String> lore4 = new ArrayList<>();
            lore4.add(ChatColor.RED + "Removes a Shard from the selected Player");
            lore4.add(ChatColor.GRAY + "Will control the Ability as well");
            ArrayList<String> lore5 = new ArrayList<>();
            lore5.add(ChatColor.GREEN + "Selected Player");
            lore5.add(ChatColor.RED + "Health: " + Bukkit.getPlayer(selectedPlayerUUID).getHealth());
            Location playerLocation = Bukkit.getPlayer(selectedPlayerUUID).getLocation();
            lore5.add(ChatColor.GREEN + "Location: " + playerLocation.getWorld().getEnvironment() + " " + playerLocation.getBlockX() + " " + playerLocation.getBlockY() + " " + playerLocation.getBlockZ());
            lore5.add("Shards: " + shardManager.getShards(selectedPlayerUUID));
            ArrayList<String> lore6 = new ArrayList<>();
            lore6.add(ChatColor.GREEN + "Adds a Shard to the Selected Player");
            lore6.add(ChatColor.GRAY + "Will control the ability as well");
            ArrayList<String> lore7 = new ArrayList<>();
            lore7.add(ChatColor.WHITE + "Makes the Selected Player Completely Invisible to other Players");
            lore7.add("");
            ArrayList<String> lore8 = new ArrayList<>();
            lore8.add(ChatColor.YELLOW + "Right Click to teleport to Players Location");
            lore8.add(ChatColor.YELLOW + "Left Click to teleport Player to you");
            ArrayList<String> lore9 = new ArrayList<>();
            lore9.add(ChatColor.RED + "Instantly Kills the selected Player");
            lore9.add(ChatColor.GRAY + "Will not influence the selected Players Shard Count");
            ArrayList<String> lore10 = new ArrayList<>();
            lore10.add(ChatColor.YELLOW + "Right Click to op the Player");
            lore10.add(ChatColor.YELLOW + "Left Click to put the player into Godmode");
            ArrayList<String> lore11 = new ArrayList<>();
            lore11.add(ChatColor.AQUA + "Will Revive/ Eliminate the Player");
            lore11.add("");
            staffMenu.setItem(18, createCustomItem(Material.CHEST, ChatColor.GREEN + "" + ChatColor.BOLD + "See Inventory", lore1));
            staffMenu.setItem(22, createCustomItem(Material.POTION, ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + "Edit Potion Effect", lore2));
            staffMenu.setItem(26, createCustomItem(Material.ENDER_CHEST, ChatColor.RED + "" + ChatColor.BOLD + "See EnderChest", lore3));
            staffMenu.setItem(30, createCustomItem(Material.RED_CONCRETE, ChatColor.RED + "" + ChatColor.BOLD + "Remove Shard", lore4));
            staffMenu.setItem(31, createCustomItem(Material.PLAYER_HEAD, ChatColor.GREEN + "" + ChatColor.BOLD + "Select Player", lore5));
            staffMenu.setItem(32, createCustomItem(Material.LIME_CONCRETE, ChatColor.GREEN + "" + ChatColor.BOLD + "Add Shard", lore6));
            staffMenu.setItem(36, createCustomItem(Material.GLASS, ChatColor.YELLOW + "" + ChatColor.BOLD + "Vanish", lore7));
            staffMenu.setItem(38, createCustomItem(Material.GRASS_BLOCK, ChatColor.GREEN + "" + ChatColor.BOLD + "Location", lore8));
            staffMenu.setItem(42, createCustomItem(Material.DIAMOND_SWORD, ChatColor.RED + "" + ChatColor.BOLD + "Kill", lore9));
            staffMenu.setItem(44, createCustomItem(Material.COMMAND_BLOCK, ChatColor.WHITE + "" + ChatColor.BOLD + "Operator", lore10));
            staffMenu.setItem(49, createCustomItem(Material.BEACON, ChatColor.RED + "" + ChatColor.BOLD + "Eliminate/ Revive", lore11));
        } else if (i == 2) {
            staffMenu.setItem(0, playerControls);
            staffMenu.setItem(2, createEnchantedItem(Material.COMMAND_BLOCK, ChatColor.GREEN + "Server Controls", i == 2));
            staffMenu.setItem(4, createEnchantedItem(Material.POTION, ChatColor.GREEN + "Potion Controls", i == 3));
            staffMenu.setItem(6, createEnchantedItem(Material.IRON_SWORD, ChatColor.GREEN + "Event Controls", i == 4));
            staffMenu.setItem(8, createEnchantedItem(Material.NETHER_STAR, ChatColor.GREEN + "Item Controls", i == 5));
        } else if (i == 3) {
        } else if (i == 4) {
        } else if (i == 5) {
        }
        return staffMenu;
        }
    private ItemStack createEnchantedItem(Material material, String name, boolean isSelected) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        if (isSelected) {
            item.addUnsafeEnchantment(Enchantment.PROTECTION_FALL, 1);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }
        item.setItemMeta(meta);
        return item;
    }
    private Inventory openPlayerSelectionMenu() {
        Inventory playerSelectionMenu = Bukkit.createInventory(null, 54, "Select a Player");

        int slot = 0;
        for (UUID uuid : allPlayers) {
            if (slot >= 54) break;
            OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);
            ItemStack playerHead = new ItemStack(Material.PLAYER_HEAD);
            SkullMeta meta = (SkullMeta) playerHead.getItemMeta();
            meta.setOwningPlayer(player);
            meta.setDisplayName(ChatColor.GREEN + player.getName());
            playerHead.setItemMeta(meta);
            playerSelectionMenu.setItem(slot++, playerHead);
        }

        return playerSelectionMenu;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        allPlayers.add(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getView().getTitle().equals("Staff Menu")) {
            event.setCancelled(true); // Prevent normal inventory behavior

            if (event.getCurrentItem() != null) {
                Material material = event.getCurrentItem().getType();
                Player player = (Player) event.getWhoClicked();

                switch (material) {
                    case PLAYER_HEAD -> {
                        // Open player selection menu
                        player.openInventory(openPlayerSelectionMenu());
                    }
                    case CHEST -> {
                        // Open the selected player's inventory
                        if (selectedPlayerUUID != null) {
                            OfflinePlayer selectedPlayer = Bukkit.getOfflinePlayer(selectedPlayerUUID);
                            if (selectedPlayer.isOnline()) {
                                player.openInventory(selectedPlayer.getPlayer().getInventory());
                            } else {
                                player.sendMessage(ChatColor.BLUE + "[Glint SMP] " +ChatColor.RED + "The selected player is not online.");
                            }
                        } else {
                            player.sendMessage(ChatColor.BLUE + "[Glint SMP] " +ChatColor.RED + "No player selected.");
                        }
                    }
                    case POTION -> {
                        // Apply a potion effect to the selected player
                        if (selectedPlayerUUID != null) {
                            OfflinePlayer selectedPlayer = Bukkit.getOfflinePlayer(selectedPlayerUUID);
                            if (selectedPlayer.isOnline()) {
                                Player targetPlayer = selectedPlayer.getPlayer();
                                targetPlayer.addPotionEffect(
                                        new PotionEffect(PotionEffectType.INVISIBILITY, 600, 1) // Example: Invisibility for 30 seconds
                                );
                                player.sendMessage(ChatColor.BLUE + "[Glint SMP] " +ChatColor.GREEN + "Invisibility applied to " + targetPlayer.getName() + ".");
                            } else {
                                player.sendMessage(ChatColor.BLUE + "[Glint SMP] " +ChatColor.RED + "The selected player is not online.");
                            }
                        } else {
                            player.sendMessage(ChatColor.BLUE + "[Glint SMP] " +ChatColor.RED + "No player selected.");
                        }
                    }
                    case ENDER_CHEST -> {
                        // Open the selected player's Ender Chest
                        if (selectedPlayerUUID != null) {
                            OfflinePlayer selectedPlayer = Bukkit.getOfflinePlayer(selectedPlayerUUID);
                            if (selectedPlayer.isOnline()) {
                                player.openInventory(selectedPlayer.getPlayer().getEnderChest());
                            } else {
                                player.sendMessage(ChatColor.BLUE + "[Glint SMP] " +ChatColor.RED + "The selected player is not online.");
                            }
                        } else {
                            player.sendMessage(ChatColor.BLUE + "[Glint SMP] " +ChatColor.RED + "No player selected.");
                        }
                    }
                    case RED_CONCRETE -> {
                        // Remove a shard from the selected player
                        if (selectedPlayerUUID != null) {
                            OfflinePlayer selectedPlayer = Bukkit.getOfflinePlayer(selectedPlayerUUID);
                            if (selectedPlayer.isOnline()) {
                                shardManager.setShards(selectedPlayer.getUniqueId(), shardManager.getShards(selectedPlayer.getUniqueId()) - 1);
                                player.sendMessage(ChatColor.BLUE + "[Glint SMP] " +ChatColor.RED + "Shard removed from " + selectedPlayer.getName() + ".");
                            } else {
                                player.sendMessage(ChatColor.BLUE + "[Glint SMP] " +ChatColor.RED + "The selected player is not online.");
                            }
                        } else {
                            player.sendMessage(ChatColor.BLUE + "[Glint SMP] " +ChatColor.RED + "No player selected.");
                        }
                    }
                    case LIME_CONCRETE -> {
                        // Add a shard to the selected player
                        if (selectedPlayerUUID != null) {
                            OfflinePlayer selectedPlayer = Bukkit.getOfflinePlayer(selectedPlayerUUID);
                            if (selectedPlayer.isOnline()) {
                                shardManager.setShards(selectedPlayer.getUniqueId(), shardManager.getShards(selectedPlayer.getUniqueId()) + 1);
                                player.sendMessage(ChatColor.BLUE + "[Glint SMP] " +ChatColor.GREEN + "Shard added to " + selectedPlayer.getName() + ".");
                            } else {
                                player.sendMessage(ChatColor.BLUE + "[Glint SMP] " +ChatColor.RED + "The selected player is not online.");
                            }
                        } else {
                            player.sendMessage(ChatColor.BLUE + "[Glint SMP] " +ChatColor.RED + "No player selected.");
                        }
                    }
                    case GLASS -> {
                        if (selectedPlayerUUID != null) {
                            OfflinePlayer selectedPlayer = Bukkit.getOfflinePlayer(selectedPlayerUUID);
                            if (selectedPlayer.isOnline()) {
                                Player targetPlayer = selectedPlayer.getPlayer();
                                for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                                    if(targetPlayer.canSee(onlinePlayer)) {
                                        onlinePlayer.hidePlayer(mainClass, targetPlayer);
                                        String player1 = onlinePlayer.toString();
                                        player.sendMessage(ChatColor.BLUE + "[Glint SMP] " +ChatColor.GREEN + targetPlayer.getName() + " is now invisible to others.");
                                        for (Player sendmessagething : Bukkit.getOnlinePlayers()) {
                                            sendmessagething.sendMessage(ChatColor.YELLOW + player1 + " has left the game");
                                        }
                                    } else {
                                        onlinePlayer.showPlayer(mainClass, targetPlayer);
                                        String player1 = onlinePlayer.toString();
                                        player.sendMessage(ChatColor.BLUE + "[Glint SMP] " +ChatColor.GREEN + targetPlayer.getName() + " is now visible to others.");
                                        for (Player sendmessagething : Bukkit.getOnlinePlayers()) {
                                            sendmessagething.sendMessage(ChatColor.YELLOW + player1 + " has left the game");
                                        }
                                    }
                                }
                                player.sendMessage(ChatColor.BLUE + "[Glint SMP] " +ChatColor.YELLOW + targetPlayer.getName() + " is now invisible to others.");
                            } else {
                                player.sendMessage(ChatColor.BLUE + "[Glint SMP] " +ChatColor.RED + "The selected player is not online.");
                            }
                        } else {
                            player.sendMessage(ChatColor.BLUE + "[Glint SMP] " +ChatColor.RED + "No player selected.");
                        }
                    }
                    case GRASS_BLOCK -> {
                        // Teleport logic
                        if (selectedPlayerUUID != null) {
                            OfflinePlayer selectedPlayer = Bukkit.getOfflinePlayer(selectedPlayerUUID);
                            if (selectedPlayer.isOnline()) {
                                Player targetPlayer = selectedPlayer.getPlayer();
                                // Right-click to teleport to the player
                                player.teleport(targetPlayer.getLocation());
                                player.sendMessage(ChatColor.BLUE + "[Glint SMP] " +ChatColor.GREEN + "You have been teleported to " + targetPlayer.getName() + ".");
                            } else {
                                player.sendMessage(ChatColor.BLUE + "[Glint SMP] " +ChatColor.RED + "The selected player is not online.");
                            }
                        } else {
                            player.sendMessage(ChatColor.BLUE + "[Glint SMP] " +ChatColor.RED + "No player selected.");
                        }
                    }
                    case DIAMOND_SWORD -> {
                        // Kill the selected player
                        if (selectedPlayerUUID != null) {
                            OfflinePlayer selectedPlayer = Bukkit.getOfflinePlayer(selectedPlayerUUID);
                            if (selectedPlayer.isOnline()) {
                                Player targetPlayer = selectedPlayer.getPlayer();
                                targetPlayer.setHealth(0);
                                player.sendMessage(ChatColor.BLUE + "[Glint SMP] " +ChatColor.RED + targetPlayer.getName() + " has been killed.");
                            } else {
                                player.sendMessage(ChatColor.BLUE + "[Glint SMP] " +ChatColor.RED + "The selected player is not online.");
                            }
                        } else {
                            player.sendMessage(ChatColor.BLUE + "[Glint SMP] " +ChatColor.RED + "No player selected.");
                        }
                    }
                    case COMMAND_BLOCK -> {
                        if (selectedPlayerUUID != null) {
                            OfflinePlayer selectedPlayer = Bukkit.getOfflinePlayer(selectedPlayerUUID);
                            if (selectedPlayer.isOnline()) {
                                Player targetPlayer = selectedPlayer.getPlayer();
                                if (!targetPlayer.isOp()) {
                                    targetPlayer.setOp(true);
                                    player.sendMessage(ChatColor.BLUE + "[Glint SMP] " +ChatColor.YELLOW + targetPlayer.getName() + " is now an operator.");
                                } else {
                                    targetPlayer.setInvulnerable(!targetPlayer.isInvulnerable());
                                    player.sendMessage(ChatColor.BLUE + "[Glint SMP] " +
                                            ChatColor.YELLOW + targetPlayer.getName() + " is now " + (targetPlayer.isInvulnerable() ? "in God Mode." : "no longer in God Mode.")
                                    );
                                }
                            } else {
                                player.sendMessage(ChatColor.BLUE + "[Glint SMP] " +ChatColor.RED + "The selected player is not online.");
                            }
                        } else {
                            player.sendMessage(ChatColor.BLUE + "[Glint SMP] " +ChatColor.RED + "No player selected.");
                        }
                    }
                    case BEACON -> {
                        if (selectedPlayerUUID != null) {
                            OfflinePlayer selectedPlayer = Bukkit.getOfflinePlayer(selectedPlayerUUID);
                            if (selectedPlayer.isBanned()) {
                                player.sendMessage(ChatColor.BLUE + "[Glint SMP] " +ChatColor.GREEN + "Player " + selectedPlayer.getName() + " revived.");
                                Bukkit.getBanList(org.bukkit.BanList.Type.NAME).pardon(selectedPlayer.getName());
                            } else {
                                player.sendMessage(ChatColor.BLUE + "[Glint SMP] " +ChatColor.RED + "Player " + selectedPlayer.getName() + " eliminated.");
                                String player1 = selectedPlayer.getName();
                                Bukkit.getBanList(org.bukkit.BanList.Type.NAME).addBan(player1, ChatColor.RED + "You have been eliminated!", null, "Banned from the Glint SMP");
                            }

                        } else {
                            player.sendMessage(ChatColor.BLUE + "[Glint SMP] " +ChatColor.RED + "No player selected.");
                        }
                    }
                }
            }
        } else if (event.getView().getTitle().equals("Select a Player")) {
            event.setCancelled(true);
            if (event.getCurrentItem() != null && event.getCurrentItem().getType() == Material.PLAYER_HEAD) {
                SkullMeta meta = (SkullMeta) event.getCurrentItem().getItemMeta();
                OfflinePlayer selectedPlayer = meta.getOwningPlayer();
                if (selectedPlayer != null) {
                    selectedPlayerUUID = selectedPlayer.getUniqueId();
                    event.getWhoClicked().closeInventory();
                    event.getWhoClicked().openInventory(openstaffmenu((Player) event.getWhoClicked(), 1));
                    event.getWhoClicked().sendMessage(ChatColor.GREEN + "You have selected " + selectedPlayer.getName() + ".");
                }
            }
        }
    }
}
