package me.thelionmc.minecraftplugin.Abilities.Mischief;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.wrappers.*;
import com.mojang.authlib.GameProfile;
import me.thelionmc.minecraftplugin.Abilities.Ability;
import me.thelionmc.minecraftplugin.Abilities.Cooldown;
import me.thelionmc.minecraftplugin.FakePlayer;
import me.thelionmc.minecraftplugin.GlintSMP;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import com.mojang.authlib.properties.Property;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class MischiefAbility3 extends Cooldown implements Ability, Listener {
    private final Map<UUID, UUID> lastHitPlayer = new HashMap<>();

    private static final AtomicInteger entityIdCounter = new AtomicInteger(-1000);
    private final ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();

    Plugin plugin;

    public MischiefAbility3(GlintSMP main, Plugin plugin) {
        super();
        this.cooldownSeconds = 10; // Set custom cooldown for Mischief Ability 3
        this.plugin = plugin;

        main.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public void execute(Player player) {
        UUID lastTargetUUID = lastHitPlayer.get(player.getUniqueId());
        if (lastTargetUUID == null) {
            player.sendMessage("§cYou haven't hit anyone recently!");
            return;
        }
        Player target = Bukkit.getPlayer(lastTargetUUID);
        if (target == null || !target.isOnline()) {
            player.sendMessage("§cYour last hit target is no longer online.");
            return;
        }
        Location spawnLoc = target.getLocation();

        int dummyCount = 3;

        for (int i = 0; i < dummyCount; i++) {

            FakePlayer dummy = new FakePlayer(player, player.getLocation());

            new BukkitRunnable() {
                @Override
                public void run() {
                    if (!target.isOnline()) {
                        dummy.destroy();
                        cancel();
                        return;
                    }
                    Location targetLoc = target.getLocation();
                    Location currentLoc = dummy.getLocation();
                    double distance = currentLoc.distance(targetLoc);
                    if (distance < 2) {
                        target.damage(0.5, player);
                        dummy.destroy();
                        cancel();
                        return;
                    }
                    Vector direction = targetLoc.toVector().subtract(currentLoc.toVector()).normalize();
                    Location newLoc = currentLoc.clone().add(direction.multiply(0.5));
                    //dummy.teleport(newLoc);
                }
            }.runTaskTimer(plugin, 0L, 1L);
        }

        player.sendMessage(ChatColor.BLUE + "[GlintSMP] " + ChatColor.GREEN + "You have summoned 3 dummies to attack " + target.getName() + "!");
    }

    @Override
    public String abilityName() {
        return "Distraction";
    }

    @EventHandler
    public void onPlayerHit(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player && event.getEntity() instanceof Player) {
            Player attacker = (Player) event.getDamager();
            Player victim = (Player) event.getEntity();
            lastHitPlayer.put(attacker.getUniqueId(), victim.getUniqueId());
        }
    }
}