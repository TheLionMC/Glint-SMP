package me.thelionmc.minecraftplugin;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityResurrectEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import java.time.format.DateTimeFormatter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

public class KitRules implements Listener {
    private Plugin plugin;
    private int combatTimeSeconds = 180;
    private int pearlCap = 0;
    private int gapCap = 128;
    private int xpCap = 128;
    private int cobCap = 64;
    private int notchCap = 0;
    private int totemCap = 0;
    private int chorusCap = 32;
    private int cartCap = 0;
    private GlintSMP main;
    private Map<UUID, Long> playerStartFightingTime = new HashMap<>();
    private Map<UUID, Integer> pearlsRemaining = new HashMap<>();
    private Map<UUID, Integer> gapsRemaining = new HashMap<>();
    private Map<UUID, Integer> xpRemaining = new HashMap<>();
    private Map<UUID, Integer> cobwebsRemaining = new HashMap<>();
    private Map<UUID, Integer> notchesRemaining = new HashMap<>();
    private Map<UUID, Integer> totemsRemaining = new HashMap<>();
    private Map<UUID, Integer> chorusesRemaining = new HashMap<>();
    private Map<UUID, Integer> cartsRemaining = new HashMap<>();
    private File combatLogFile;

    public long combatTimerMillis(UUID uuid) { //returns the amount of millis between the current time and the last time the player was hit
        return System.currentTimeMillis() - playerStartFightingTime.get(uuid);
    }
    public int combatTimerSeconds(UUID uuid) {
        return (int) (System.currentTimeMillis() - playerStartFightingTime.get(uuid)) / 1000;
    }
    public long combatTimeRemainingMillis(UUID uuid) {
        long time = combatTimeSeconds * 1000 - (System.currentTimeMillis() - playerStartFightingTime.get(uuid));
        if(time >= 0) {
            return time;
        } else {
            return 0;
        }
    }
    public int combatTimeRemainingSeconds(UUID uuid) {
        Long startTime = playerStartFightingTime.get(uuid);
        if (startTime == null) {
            return 0;
        }
        int time = (int) (combatTimeSeconds - (System.currentTimeMillis() - startTime) / 1000);
        return Math.max(time, 0);
    }


    public boolean inCombat(UUID uuid) {
        if(combatTimeRemainingSeconds(uuid) <= 1) {
            return false;
        } else {
            return true;
        }
    }


    public KitRules(Plugin plugin1, GlintSMP main) {
        this.plugin = plugin1;
        this.main = main;

        combatLogFile = new File(plugin.getDataFolder(), "combat-log.txt");

        if (!combatLogFile.exists()) {
            try {
                combatLogFile.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().log(Level.SEVERE, "Could not create combat log file", e);
            }
        }

        BukkitRunnable task = new BukkitRunnable() {
            int ticks = 0;
            @Override
            public void run() {
                for(Player player : Bukkit.getOnlinePlayers()) {
                    if(inCombat(player.getUniqueId())) {
                        player.sendMessage(combatTimeRemainingSeconds(player.getUniqueId()) + " seconds remain in your fight. " + ChatColor.RED + "DON'T LOG OUT!");
                    }
                }
            }
        };
        task.runTaskTimer(plugin, 0, 300);
        BukkitRunnable task1 = new BukkitRunnable() {
            @Override
            public void run() {
                for(Player player : Bukkit.getOnlinePlayers()) {
                    if (combatTimeRemainingSeconds(player.getUniqueId()) == 1) {
                        player.sendMessage(ChatColor.BLUE + "[GlintSMP] " + ChatColor.GREEN + "Your combat timer is over, it is now safe to log out.");
                    }
                }
            }
        };
        task1.runTaskTimer(plugin, 0, 20);
    }

    @EventHandler
    public void onEntityDamageByEntity(final EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player) || !(event.getDamager() instanceof Player))
            return;
        final Player player = (Player) event.getEntity();
        final Player damager = (Player) event.getDamager();
        if (damager.getEquipment().getItemInMainHand().getType().toString().toLowerCase().contains("axe") && player.isBlocking() && (int) event.getFinalDamage() == 0) {
            try {
                //Everything is OK!
            } catch (final Exception e) {
                damager.playSound(damager.getLocation(), Sound.ITEM_SHIELD_BREAK, 5, 5);
                player.playSound(player.getLocation(), Sound.ITEM_SHIELD_BREAK,5,5);
            }
        }
    }
    @EventHandler
    public void onPlayerDamage(EntityDamageByEntityEvent e) {
        if (e.getEntity() instanceof Player && e.getDamager() instanceof Player) {
            Player playerHit = (Player) e.getEntity();
            Player playerHitting = (Player) e.getDamager();
            if (!inCombat(playerHit.getUniqueId())) {
                playerHit.sendMessage(ChatColor.BLUE + "[GlintSMP] " + ChatColor.RED + "You've started a fight against " + ChatColor.YELLOW + playerHitting + ChatColor.RED + "!");
                pearlsRemaining.put(playerHit.getUniqueId(), pearlCap);
                gapsRemaining.put(playerHit.getUniqueId(), gapCap);
                xpRemaining.put(playerHit.getUniqueId(), xpCap);
                cobwebsRemaining.put(playerHit.getUniqueId(), cobCap);
                notchesRemaining.put(playerHit.getUniqueId(), notchCap);
                totemsRemaining.put(playerHit.getUniqueId(), totemCap);
                chorusesRemaining.put(playerHit.getUniqueId(), chorusCap);
                cartsRemaining.put(playerHit.getUniqueId(), cartCap);
            }
            if (!inCombat(playerHitting.getUniqueId())) {
                playerHitting.sendMessage(ChatColor.BLUE + "[GlintSMP] " + ChatColor.RED + "You've started a fight against " + ChatColor.YELLOW + playerHit + ChatColor.RED + "!");
                pearlsRemaining.put(playerHitting.getUniqueId(), pearlCap);
                gapsRemaining.put(playerHitting.getUniqueId(), gapCap);
                xpRemaining.put(playerHitting.getUniqueId(), xpCap);
                cobwebsRemaining.put(playerHitting.getUniqueId(), cobCap);
                notchesRemaining.put(playerHitting.getUniqueId(), notchCap);
                totemsRemaining.put(playerHitting.getUniqueId(), totemCap);
                chorusesRemaining.put(playerHitting.getUniqueId(), chorusCap);
                cartsRemaining.put(playerHitting.getUniqueId(), cartCap);
            }
            if (playerHitting.getInventory().getChestplate() != null && playerHitting.getInventory().getChestplate().getType() == Material.ELYTRA ||
                    playerHit.getInventory().getChestplate() != null && playerHit.getInventory().getChestplate().getType() == Material.ELYTRA) {
                return;
            }

            playerStartFightingTime.put(playerHit.getUniqueId(), System.currentTimeMillis());
            playerStartFightingTime.put(playerHitting.getUniqueId(), System.currentTimeMillis());
            swapElytraWithChestplate(playerHit);
            swapElytraWithChestplate(playerHitting);

        }
    }
    private void logCombatLogout(String quitterName, String opponentName) {
        try (FileWriter writer = new FileWriter(combatLogFile, true)) {
            long currentTimeMillis = System.currentTimeMillis();
            LocalDateTime dateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(currentTimeMillis), ZoneId.systemDefault());
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String formattedDateTime = dateTime.format(formatter);
            String playercoords = Bukkit.getPlayer(quitterName).getLocation().getX() + " " + Bukkit.getPlayer(quitterName).getLocation().getY() + " " + Bukkit.getPlayer(quitterName).getLocation().getZ();
                    writer.write(ChatColor.RED + quitterName + ChatColor.WHITE + " combat-logged while fighting " + opponentName + "at the Coordinates: " + playercoords +  " at server time " + formattedDateTime + ".\n");
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Could not log combat logout", e);
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player quitter = event.getPlayer();

        if (inCombat(quitter.getUniqueId())) {
            String opponentName = "an unknown opponent";
            for (UUID uuid : playerStartFightingTime.keySet()) {
                if (!uuid.equals(quitter.getUniqueId())) {
                    Player opponent = Bukkit.getPlayer(uuid);
                    if (opponent != null) {
                        opponentName = opponent.getName();
                        break;
                    }
                }
            }
            logCombatLogout(quitter.getName(), opponentName);
            playerStartFightingTime.remove(quitter.getUniqueId());
        }
    }
    private void swapElytraWithChestplate(Player player) {
        ItemStack chestplate = player.getInventory().getChestplate();
        ItemStack elytra = null;
        int elytraSlot = -1;

        for (int i = 0; i < player.getInventory().getSize(); i++) {
            ItemStack item = player.getInventory().getItem(i);
            if (item != null && item.getType() == Material.ELYTRA) {
                elytra = item;
                elytraSlot = i;
                break;
            }
        }

        if (elytra != null) {
            player.getInventory().setItem(elytraSlot, chestplate);
            player.getInventory().setChestplate(elytra);
        }
    }

    @EventHandler
    public void onThrowPearlEvent(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        if (event.getItem() == null) {
            return;
        }
        if (!pearlsRemaining.containsKey(player.getUniqueId())) {
            pearlsRemaining.put(player.getUniqueId(), pearlCap);
        }
        if (playerStartFightingTime.containsKey(player.getUniqueId())) {
            if (event.getItem().getType().equals(Material.ENDER_PEARL) && event.getAction().name().contains("RIGHT_CLICK")) {
                if (!player.hasCooldown(Material.ENDER_PEARL)) {
                    if (inCombat(player.getUniqueId())) {
                        if (pearlsRemaining.get(player.getUniqueId()) > 0) {
                            pearlsRemaining.put(player.getUniqueId(), pearlsRemaining.get(player.getUniqueId()) - 1);
                        } else {
                            player.playSound(player.getLocation(), Sound.ITEM_SHIELD_BREAK, 1, 1);
                            player.sendMessage(ChatColor.BLUE + "[GlintSMP] " + ChatColor.RED + "You've run out of pearls for this fight!");
                            event.setCancelled(true);
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onPlayerEatChorus(PlayerItemConsumeEvent event) {
        if(!chorusesRemaining.containsKey(event.getPlayer().getUniqueId())) {
            chorusesRemaining.put(event.getPlayer().getUniqueId(), chorusCap);
        }
        if (playerStartFightingTime.containsKey(event.getPlayer().getUniqueId())) {
            if (event.getItem().getType().equals(Material.CHORUS_FRUIT)) {
                if (inCombat(event.getPlayer().getUniqueId())) {
                    if (chorusesRemaining.get(event.getPlayer().getUniqueId()) > 0) {
                        chorusesRemaining.put(event.getPlayer().getUniqueId(), chorusesRemaining.get(event.getPlayer().getUniqueId()) - 1);
                    } else {
                        event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.ITEM_SHIELD_BREAK, 1, 1);
                        event.getPlayer().sendMessage(ChatColor.BLUE + "[GlintSMP] " + ChatColor.RED + "You've run out of Chorus Fruit this fight!");
                        event.setCancelled(true);
                    }
                }
            }
        }
    }
    @EventHandler
    public void onPlayerEatGap(PlayerItemConsumeEvent event) {
        if(!gapsRemaining.containsKey(event.getPlayer().getUniqueId())) {
            gapsRemaining.put(event.getPlayer().getUniqueId(), gapCap);
        }
        if (playerStartFightingTime.containsKey(event.getPlayer().getUniqueId())) {
            if (event.getItem().getType().equals(Material.GOLDEN_APPLE)) {
                if (inCombat(event.getPlayer().getUniqueId())) {
                    if (gapsRemaining.get(event.getPlayer().getUniqueId()) > 0) {
                        gapsRemaining.put(event.getPlayer().getUniqueId(), gapsRemaining.get(event.getPlayer().getUniqueId()) - 1);
                    } else {
                        event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.ITEM_SHIELD_BREAK, 1, 1);
                        event.getPlayer().sendMessage(ChatColor.BLUE + "[GlintSMP] " + ChatColor.RED + "You've run out of gaps for this fight! (you're cooked lmao)");
                        event.setCancelled(true);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onPlayerUseXP(PlayerInteractEvent event) {
        if(!xpRemaining.containsKey(event.getPlayer().getUniqueId())) {
            xpRemaining.put(event.getPlayer().getUniqueId(), xpCap);
        }
        if (playerStartFightingTime.containsKey(event.getPlayer().getUniqueId())) {
            if (event.getAction().name().contains("RIGHT")) {
                ItemStack item = event.getItem();
                if (item != null && item.getType() == Material.EXPERIENCE_BOTTLE) {
                    if (!((System.currentTimeMillis() - playerStartFightingTime.get(event.getPlayer().getUniqueId())) >= combatTimeSeconds * 1000)) {
                        if (xpRemaining.get(event.getPlayer().getUniqueId()) > 0) {
                            xpRemaining.put(event.getPlayer().getUniqueId(), xpRemaining.get(event.getPlayer().getUniqueId()) - 1);
                        } else {
                            event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.ITEM_SHIELD_BREAK, 1, 1);
                            event.getPlayer().sendMessage(ChatColor.BLUE + "[GlintSMP] " + ChatColor.RED + "You've run out of XP for this fight!");
                            event.setCancelled(true);
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onPlayerPlaceCob(BlockPlaceEvent event) {
        if(!cobwebsRemaining.containsKey(event.getPlayer().getUniqueId())) {
            cobwebsRemaining.put(event.getPlayer().getUniqueId(), cobCap);
        }
        if (playerStartFightingTime.containsKey(event.getPlayer().getUniqueId())) {
            if (event.getBlockPlaced().getType() == Material.COBWEB) {
                if (!((System.currentTimeMillis() - playerStartFightingTime.get(event.getPlayer().getUniqueId())) >= combatTimeSeconds * 1000)) {
                    if (cobwebsRemaining.get(event.getPlayer().getUniqueId()) > 0) {
                        cobwebsRemaining.put(event.getPlayer().getUniqueId(), cobwebsRemaining.get(event.getPlayer().getUniqueId()) - 1);
                    } else {
                        event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.ITEM_SHIELD_BREAK, 1, 1);
                        event.getPlayer().sendMessage(ChatColor.BLUE + "[GlintSMP] " + ChatColor.RED + "You've run out of Cobwebs for this fight!");
                        event.setCancelled(true);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onPlayerEatNotch(PlayerItemConsumeEvent event) {
        if(!notchesRemaining.containsKey(event.getPlayer().getUniqueId())) {
            notchesRemaining.put(event.getPlayer().getUniqueId(), notchCap);
        }
        if (playerStartFightingTime.containsKey(event.getPlayer().getUniqueId())) {
            if (event.getItem().getType().equals(Material.ENCHANTED_GOLDEN_APPLE)) {
                if (!((System.currentTimeMillis() - playerStartFightingTime.get(event.getPlayer().getUniqueId())) >= combatTimeSeconds * 1000)) {
                    if (notchesRemaining.get(event.getPlayer().getUniqueId()) > 0) {
                        notchesRemaining.put(event.getPlayer().getUniqueId(), notchesRemaining.get(event.getPlayer().getUniqueId()) - 1);
                    } else {
                        event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.ITEM_SHIELD_BREAK, 1, 1);
                        event.getPlayer().sendMessage(ChatColor.BLUE + "[GlintSMP] " + ChatColor.RED + "You aren't permitted to eat Notches during a fight!");
                        event.setCancelled(true);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onPlayerPop(EntityResurrectEvent event) {
            if (event.getEntity() instanceof Player) {
                Player player = (Player) event.getEntity();
                if(!totemsRemaining.containsKey(player.getUniqueId())) {
                    totemsRemaining.put(player.getUniqueId(), totemCap);
                }
                if (playerStartFightingTime.containsKey(player.getUniqueId())) {
                if (player.getInventory().getItemInMainHand().getType() == Material.TOTEM_OF_UNDYING ||
                        player.getInventory().getItemInOffHand().getType() == Material.TOTEM_OF_UNDYING) {
                    if (!((System.currentTimeMillis() - playerStartFightingTime.get(player.getUniqueId())) >= combatTimeSeconds * 1000)) {
                        if (totemsRemaining.get(player.getUniqueId()) > 0) {
                            totemsRemaining.put(player.getUniqueId(), totemsRemaining.get(player.getUniqueId()) - 1);
                        } else {
                            player.playSound(player.getLocation(), Sound.ITEM_SHIELD_BREAK, 1, 1);
                            player.sendMessage(ChatColor.BLUE + "[GlintSMP] " + ChatColor.RED + "You've run out of totems for this fight!");
                            event.setCancelled(true);
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onPlayerPlaceCart(PlayerInteractEvent event) {
        if(!cartsRemaining.containsKey(event.getPlayer().getUniqueId())) {
            cartsRemaining.put(event.getPlayer().getUniqueId(), cartCap);
        }
        if (playerStartFightingTime.containsKey(event.getPlayer().getUniqueId())) {
            if (event.getAction().name().contains("RIGHT")) {
                ItemStack item = event.getItem();
                if (item != null && item.getType() == Material.TNT_MINECART) {
                    if (!((System.currentTimeMillis() - playerStartFightingTime.get(event.getPlayer().getUniqueId())) >= combatTimeSeconds * 1000)) {
                        if (cartsRemaining.get(event.getPlayer().getUniqueId()) > 0) {
                            cartsRemaining.put(event.getPlayer().getUniqueId(), cartsRemaining.get(event.getPlayer().getUniqueId()) - 1);
                        } else {
                            event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.ITEM_SHIELD_BREAK, 1, 1);
                            event.getPlayer().sendMessage(ChatColor.BLUE + "[GlintSMP] " + ChatColor.RED + "You've run out of carts for this fight!");
                            event.setCancelled(true);
                        }
                    }
                }
            }
        }
    }
}
