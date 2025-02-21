package me.thelionmc.minecraftplugin;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.*;

public class FakePlayer {
    private final UUID uuid;
    private final String name;
    private final int entityId;
    private final WrappedGameProfile profile;
    private final ProtocolManager protocolManager;
    private final Location location;

    public FakePlayer(Player originalPlayer, Location location) {
        this.uuid = originalPlayer.getUniqueId();
        this.name = originalPlayer.getName();
        this.entityId = (int) (Math.random() * Integer.MAX_VALUE);
        this.profile = new WrappedGameProfile(uuid, name);
        this.protocolManager = ProtocolLibrary.getProtocolManager();
        this.location = location;

        spawn(originalPlayer);
    }

    private void spawn(Player originalPlayer) {
        Location loc = originalPlayer.getLocation();

        PacketContainer fake = new PacketContainer(PacketType.Play.Server.SPAWN_ENTITY);
        fake.getModifier().writeDefaults();
        var mod = fake.getModifier();

        mod.write(0, entityId); //id for teh entity
        mod.write(1, uuid); //uuid for the entity
        mod.write(2, 2); //player id
        mod.write(3, loc.getX());
        mod.write(4, loc.getY());
        mod.write(5, loc.getZ());
        mod.write(6, loc.getPitch());
        mod.write(7, loc.getYaw());

        sendPacketToAll(fake);
    }


    public void destroy() {
        PacketContainer destroyPacket = protocolManager.createPacket(PacketType.Play.Server.ENTITY_DESTROY);
        destroyPacket.getIntegerArrays().write(0, new int[]{entityId});
        sendPacketToAll(destroyPacket);
    }

    private void sendPacketToAll(PacketContainer packet) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            protocolManager.sendServerPacket(player, packet);
        }
    }

    private void msg(String string) {
        for(Player player : Bukkit.getOnlinePlayers()) {
            player.sendMessage(string);
        }
    }

    public Location getLocation() {
        return location;
    }
}
