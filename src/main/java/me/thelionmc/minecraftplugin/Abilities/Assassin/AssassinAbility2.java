package me.thelionmc.minecraftplugin.Abilities.Assassin;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import com.comphenix.protocol.wrappers.WrappedWatchableObject;
import me.thelionmc.minecraftplugin.Abilities.Ability;
import me.thelionmc.minecraftplugin.Abilities.Cooldown;
import me.thelionmc.minecraftplugin.GlintSMP;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class AssassinAbility2 extends Cooldown implements Ability {
    Map<UUID, Long> cools = new HashMap<>();
    private final Plugin plugin;
    private final GlintSMP mainClass;
    private final FileConfiguration shardData;
    private final ProtocolManager protocolManager;

    public AssassinAbility2(Plugin plugin, GlintSMP main) {
        super();
        this.plugin = plugin;
        this.mainClass = main;
        this.shardData = plugin.getConfig();
        this.protocolManager = ProtocolLibrary.getProtocolManager(); // Initialize ProtocolLib manager
        this.cooldownSeconds = 10; // Set custom cooldown for Assassin Ability 2
    }

    public int getShards(UUID playerID) {
        return this.shardData.getInt(playerID.toString(), 0);
    }

    public void execute(Player player) {
        int shards = getShards(player.getUniqueId());
        for (Player target : Bukkit.getOnlinePlayers()) {
            sendGlowPacket(player, target, true);
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player target : Bukkit.getOnlinePlayers()) {
                    sendGlowPacket(player, target, false);
                }
            }
        }.runTaskLater(plugin, (long) (3 * 0.25 + shards));
    }

    public String abilityName() {
        return "Blood Smell";
    }

    private void sendGlowPacket(Player receiver, Player target, boolean glow) {
        try {
            PacketContainer packet = protocolManager.createPacket(
                    com.comphenix.protocol.PacketType.Play.Server.ENTITY_METADATA
            );
            packet.getIntegers().write(0, target.getEntityId());

            // Create the metadata list manually.
            List<WrappedWatchableObject> metaList = new ArrayList<>();
            WrappedDataWatcher.WrappedDataWatcherObject flagsObject =
                    new WrappedDataWatcher.WrappedDataWatcherObject(0, WrappedDataWatcher.Registry.get(Byte.class));
            byte flags = (byte) (glow ? 0x40 : 0);
            metaList.add(new WrappedWatchableObject(flagsObject, flags));

            packet.getWatchableCollectionModifier().write(0, metaList);
            protocolManager.sendServerPacket(receiver, packet);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
