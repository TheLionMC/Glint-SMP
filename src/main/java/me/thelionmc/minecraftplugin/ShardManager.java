package me.thelionmc.minecraftplugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.BanList.Type;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;


import static me.thelionmc.minecraftplugin.GlintSMP.shardCounts;

public class ShardManager implements Listener, CommandExecutor, TabCompleter {
    final private FileConfiguration shardData;
    final private Plugin plugin;
    private GlintSMP mainClass;
    private Map<UUID, Long> interactCooldown = new HashMap<>();

    public ShardManager(Plugin plugin, GlintSMP mainClass) {
        this.plugin = plugin;
        this.mainClass = mainClass;
        this.shardData = YamlConfiguration.loadConfiguration(new File(mainClass.getDataFolder(), "shardData.yml"));
    }

    public void saveShardData() {
        try {
            this.shardData.save(new File(this.mainClass.getDataFolder(), "shardData.yml"));
        } catch (IOException var2) {
            var2.printStackTrace();
        }
    }

    public void setShards(UUID playerID, int shardCount) {
        this.shardData.set(playerID.toString(), shardCount);
        this.saveShardData();
    }
    private void setPlayerShards(UUID playerId, int shardCount) {
        shardCounts.put(playerId, shardCount);
    }

    public int getShards(UUID playerID) {
        return this.shardData.getInt(playerID.toString());
    }

    @EventHandler
    void onPlayerJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        UUID playerID = player.getUniqueId();
        if (!this.shardData.contains(playerID.toString())) {
            this.setShards(playerID, 0);
        }
    }

    @EventHandler
    void onPlayerDeath(PlayerDeathEvent e) {
        Player player = e.getEntity();
        UUID playerID = player.getUniqueId();
        if (this.getShards(playerID) - 1 > 0) {
            this.setShards(playerID, this.getShards(playerID) - 1);
        } else {
            this.setShards(playerID, 4);
            BanList banList = Bukkit.getBanList(Type.NAME);
            String reason = "You ran out of shards!";
            banList.addBan(player.getName(), ChatColor.RED + "You have run out of Shards!", (Date)null, player.getName());
            if (player != null) {
                player.kickPlayer(reason);
            }
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        this.interactCooldown.put(player.getUniqueId(), System.currentTimeMillis());
        ItemStack handItem = player.getInventory().getItemInMainHand();
        if (handItem != null && handItem.isSimilar(GlintSMP.shard) && !player.isSneaking()) {
            if (!player.hasCooldown(Material.NETHER_STAR)) {
                if (this.getShards(player.getUniqueId()) >= 20) {
                    player.sendMessage(ChatColor.RED + "You cannot have more than 20 Shards.");
                    player.getWorld().playSound(player.getLocation(), Sound.ITEM_SHIELD_BREAK, 1.0F, 1.0F);
                    player.setCooldown(Material.NETHER_STAR, 5);
                } else {
                    this.setShards(player.getUniqueId(), this.getShards(player.getUniqueId()) + 1);
                    player.getWorld().playSound(player.getLocation(), Sound.BLOCK_RESPAWN_ANCHOR_CHARGE, 1.0F, 1.0F);
                    ChatColor var10001 = ChatColor.BLUE;
                    player.sendMessage(var10001 + "[GlintSMP] " + ChatColor.GREEN + "You now have " + this.getShards(player.getUniqueId()) + " shards.");
                    player.setCooldown(Material.NETHER_STAR, 5);
                    int newAmount = handItem.getAmount() - 1;
                    if (newAmount <= 0) {
                        player.getInventory().setItemInMainHand((ItemStack)null);

                    } else {
                        handItem.setAmount(newAmount);
                        player.getInventory().setItemInMainHand(handItem);
                    }
                }
            } else {
                event.setCancelled(true);
            }
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("withdraw") && sender instanceof Player) {
            Player player = (Player) sender;
            if (args.length == 1) {
                try {
                    int amount = Integer.parseInt(args[0]);
                    if (amount <= 0) {
                        player.sendMessage(ChatColor.BLUE + "[GlintSMP] " + ChatColor.RED + "You can't withdraw a negative amount of Invis Shards.");
                    } else if (getShards(player.getUniqueId()) >= amount) {
                        mainClass.shard.setAmount(amount);
                        HashMap<Integer, ItemStack> remainingItems = player.getInventory().addItem(mainClass.shard);
                        if (!remainingItems.isEmpty()) {
                            for (ItemStack item : remainingItems.values()) {
                                player.getWorld().dropItemNaturally(player.getLocation(), item);
                            }
                            player.sendMessage(ChatColor.BLUE + "[GlintSMP] " + ChatColor.YELLOW + "Your inventory was full. Excess shards were dropped at your feet.");
                        }
                        setShards(player.getUniqueId(), getShards(player.getUniqueId()) - amount);

                        player.sendMessage(ChatColor.BLUE + "[GlintSMP] " + ChatColor.GREEN + "You withdrew " + amount + " Invis Shards.");
                    } else {
                        player.sendMessage(ChatColor.BLUE + "[GlintSMP] " + ChatColor.RED + "You don't have enough Invis Shards.");
                    }
                } catch (NumberFormatException e) {
                    player.sendMessage(ChatColor.BLUE + "[GlintSMP] " + ChatColor.RED + "Invalid amount of shards.");
                }
            } else {
                player.sendMessage(ChatColor.BLUE + "[GlintSMP] " + ChatColor.RED + "Usage: /withdraw <amount>");
            }
            return true;
        } else if (cmd.getName().equalsIgnoreCase("setShards") && sender instanceof Player && sender.isOp()) {
                if (args.length == 2) {
                    String playerName = args[0];
                    int amount = Integer.parseInt(args[1]);
                    Player targetPlayer = Bukkit.getPlayer(playerName);

                    if (targetPlayer != null && targetPlayer.isOnline()) {
                        UUID targetPlayerId = targetPlayer.getUniqueId();
                        if (amount >= 0) {
                            setPlayerShards(targetPlayerId, amount);
                            sender.sendMessage(ChatColor.BLUE + "[GlintSMP] " +ChatColor.GREEN + "Shards set to " + amount + " for player " + playerName);
                            return true;
                        } else {
                            sender.sendMessage(ChatColor.BLUE + "[GlintSMP] " +ChatColor.RED + "Please provide a non-negative amount of shards.");
                        }
                    } else {
                        sender.sendMessage(ChatColor.BLUE + "[GlintSMP] " +ChatColor.RED + "Player " + playerName + " is not online.");
                    }
                } else {
                    sender.sendMessage(ChatColor.BLUE + "[GlintSMP] " +ChatColor.RED + "Usage: /setShards <playerName> <amount>");

                }
            } else if (cmd.getName().equalsIgnoreCase("shardcount") && sender instanceof Player) {
            if (args.length == 0) {
                UUID playeruuid = ((Player) sender).getUniqueId();
                sender.sendMessage(ChatColor.BLUE + "[GlintSMP] " + ChatColor.GREEN + "You have " + getShards(playeruuid) + " Shards" );
                return true;
            }
        }

            return false;
        }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String alias, String[] args) {
        if (cmd.getName().equalsIgnoreCase("withdraw") && args.length == 1) {
            List<String> suggestions = new ArrayList<>();
            for (int i = 1; i <= 10; i++) {
                suggestions.add(String.valueOf(i));
            }
            return suggestions;
        } else if (cmd.getName().equalsIgnoreCase("setShards") && args.length == 2 ) {
            List<String> suggestions = new ArrayList<>();
            for (int i = 1; i <= 10; i++) {
                suggestions.add(String.valueOf(i));
            }
            return suggestions;
        }
        return null;
    }

}
