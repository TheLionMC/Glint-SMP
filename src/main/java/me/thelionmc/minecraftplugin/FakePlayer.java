package me.thelionmc.minecraftplugin;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.*;
import com.google.common.collect.Iterables;
import com.google.common.collect.Iterators;
import com.mojang.authlib.GameProfile;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_21_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import net.minecraft.world.entity.EntityType;
import com.mojang.authlib.properties.Property;

import java.util.*;

public class FakePlayer {
    private final UUID uuid;
    private final String name;
    private final int entityId;
    private final WrappedGameProfile profile;
    private final ProtocolManager protocolManager;
    private final Location loc;
    private final CraftPlayer player;

    public FakePlayer(Player originalPlayer) {
        this.uuid = originalPlayer.getUniqueId();
        this.name = originalPlayer.getName();
        this.entityId = (int) (Math.random() * Integer.MAX_VALUE);
        this.profile = new WrappedGameProfile(uuid, name);
        this.protocolManager = ProtocolLibrary.getProtocolManager();
        this.loc = originalPlayer.getLocation();
        this.player = (CraftPlayer) originalPlayer;

        spawn();
    }

    private void spawn() {
        //ADDING THE PLAYER
        PacketContainer addPlayer = new PacketContainer(PacketType.Play.Server.PLAYER_INFO);
        addPlayer.getPlayerInfoActions().write(0, EnumSet.of(EnumWrappers.PlayerInfoAction.ADD_PLAYER));

        WrappedGameProfile profile = WrappedGameProfile.fromPlayer(player);
        //profile.getProperties().put("textures", WrappedSignedProperty.fromHandle(new Property("textures", getSkin().value(), getSkin().signature())));

        PlayerInfoData playerInfoData = new PlayerInfoData(
                profile,
                player.getPing(),
                EnumWrappers.NativeGameMode.SURVIVAL,
                WrappedChatComponent.fromText(name)
        );

        addPlayer.getPlayerInfoDataLists().write(1, Collections.singletonList(playerInfoData));

        //SPAWNING THE ENTITY
        PacketContainer fake = new PacketContainer(PacketType.Play.Server.SPAWN_ENTITY);
        fake.getModifier().writeDefaults();

        fake.getModifier().write(0, entityId); //id for teh entity
        fake.getModifier().write(1, uuid); //uuid for the entity
        fake.getModifier().write(2, EntityType.PLAYER);
        fake.getModifier().write(3, loc.getX()); //(int) Math.round(loc.getX()))
        fake.getModifier().write(4, loc.getY());
        fake.getModifier().write(5, loc.getZ());
        fake.getModifier().write(6, Math.round(loc.getPitch()));//(loc.getPitch() * 256 / 360)
        fake.getModifier().write(7, Math.round(loc.getYaw())); //loc.getYaw();


        player.teleport(new Location(player.getWorld(), loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch()));

        sendPacketToAll(fake);
        sendPacketToAll(addPlayer);
    }


    public void destroy() {
        PacketContainer destroyPacket = protocolManager.createPacket(PacketType.Play.Server.ENTITY_DESTROY);
        destroyPacket.getIntegerArrays().write(0, new int[]{entityId});
        sendPacketToAll(destroyPacket);
    }

    private Property getSkin() {
        GameProfile profile = player.getHandle().getGameProfile();
        Property textures = Iterables.getFirst(profile.getProperties().get("textures"), null);
        if (textures == null) {
            player.sendMessage("ERROR! Please report to an admin ASAP!");
            return null;
        }
        return textures;
    }


    private void sendPacketToAll(PacketContainer packet) {
        try {
            for (Player player : Bukkit.getOnlinePlayers()) {
                protocolManager.sendServerPacket(player, packet);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void msg(String string) {
        for(Player player : Bukkit.getOnlinePlayers()) {
            player.sendMessage(string);
        }
    }

    public Location getLocation() {
        return loc;
    }
}
