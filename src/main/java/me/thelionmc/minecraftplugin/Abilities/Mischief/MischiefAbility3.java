package me.thelionmc.minecraftplugin.Abilities.Mischief;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import me.thelionmc.minecraftplugin.Abilities.Ability;
import me.thelionmc.minecraftplugin.FakePlayer;
import me.thelionmc.minecraftplugin.GlintSMP;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.plugin.Plugin;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class MischiefAbility3 extends Ability implements Listener {
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
    public boolean execute(Player player) {
        UUID lastTargetUUID = lastHitPlayer.get(player.getUniqueId());
        if (lastTargetUUID == null) {
            player.sendMessage("§cYou haven't hit anyone recently!");
            return false;
        }
        Player target = Bukkit.getPlayer(lastTargetUUID);
        if (target == null || !target.isOnline()) {
            player.sendMessage("§cYour last hit target is no longer online.");
            return false;
        }
        Location spawnLoc = target.getLocation();
        int botCount = 1;

        for (int i = 0; i < botCount; i++) {

            FakePlayer dummy = new FakePlayer(player, Bukkit.getPlayer(lastTargetUUID));
/*
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
                    dummy.teleport(newLoc);
                }
            }.runTaskTimer(plugin, 0L, 1L);


         */
            player.sendMessage("§aYou have summoned 3 dummies to attack " + target.getName() + "!");
        }
        return true;
    }

    @EventHandler
    public void onPlayerHit(EntityDamageByEntityEvent e) {
        if(e.getDamager() instanceof Player damaging) {
            if(e.getEntity() instanceof Player damaged) {
                lastHitPlayer.put(damaging.getUniqueId(), damaged.getUniqueId());
            }
        }
    }

    @Override
    public String abilityName() {
        return "Distraction";
    }
}
