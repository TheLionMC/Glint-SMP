package me.thelionmc.minecraftplugin;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.PlayerInfoData;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.comphenix.protocol.wrappers.WrappedGameProfile;
import com.mojang.authlib.GameProfile;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import net.minecraft.network.Connection;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.protocol.EnumProtocolDirection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoRemovePacket;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket;
import net.minecraft.server.level.*;
import net.minecraft.server.network.CommonListenerCookie;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.level.GameType;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_21_R1.CraftServer;
import org.bukkit.craftbukkit.v1_21_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_21_R1.entity.CraftPlayer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.UUID;

public class DistractionPlayer extends ServerPlayer {
    Player original;
    Location spawnLocation;
    ProtocolManager manager;
    GameProfile profile;
    Plugin plugin;

    public DistractionPlayer(Player original, UUID fakeUUID, GameProfile fakeProfile, Plugin plugin) {
        super(
                ((CraftServer) Bukkit.getServer()).getServer(),
                ((CraftWorld) original.getWorld()).getHandle(),
                fakeProfile,
                ClientInformation.createDefault()
        );

        this.profile = fakeProfile;
        this.original = original;
        this.spawnLocation = original.getLocation();
        this.manager = ProtocolLibrary.getProtocolManager();
        this.plugin = plugin;

        setPos(spawnLocation.getX(), spawnLocation.getY(), spawnLocation.getZ());

        for(Player player : Bukkit.getOnlinePlayers()) {
            manager.sendServerPacket(player, playerInfoPacket());
            manager.sendServerPacket(player, addPlayerPacket());
        }

        this.setNoGravity(false);
        this.setGameMode(GameType.SURVIVAL);
        this.getAbilities().flying = false;

        connection = new ServerGamePacketListenerImpl(
                ((CraftServer) Bukkit.getServer()).getServer(),
                new DummyConnection(),
                this,  // 'this' is your fake ServerPlayer
                new CommonListenerCookie(profile, 0, ClientInformation.createDefault(), false)
        );



        ((CraftWorld) original.getWorld()).getHandle().addFreshEntity(this);

        setDeltaMovement(0, 2, 0);
    }

    private PacketContainer playerInfoPacket() {
        PacketContainer p = manager.createPacket(PacketType.Play.Server.PLAYER_INFO);

        p.getPlayerInfoActions().write(0, EnumSet.of(EnumWrappers.PlayerInfoAction.ADD_PLAYER));

        List<PlayerInfoData> playerInfoDataList = new ArrayList<>();

        WrappedGameProfile wrappedProfile = WrappedGameProfile.fromHandle(profile);

        PlayerInfoData data = new PlayerInfoData(
                wrappedProfile,
                1,
                EnumWrappers.NativeGameMode.SURVIVAL,
                WrappedChatComponent.fromText(original.getName())
        );
        playerInfoDataList.add(data);
        p.getPlayerInfoDataLists().write(1, playerInfoDataList);

        return p;
    }


    private PacketContainer addPlayerPacket() {
        PacketContainer p = new PacketContainer(PacketType.Play.Server.SPAWN_ENTITY);

        p.getIntegers().write(0, this.getBukkitEntity().getEntityId()); //id
        p.getUUIDs().write(0, uuid); //uuid
        p.getEntityTypeModifier().write(0, EntityType.PLAYER); //player entity type
        p.getDoubles() //coords
                .write(0, original.getLocation().getX())
                .write(1, original.getLocation().getY())
                .write(2, original.getLocation().getZ());
        p.getBytes() //pitch/yaw
                .write(0, (byte) (original.getLocation().getPitch() * 256.0F / 360.0F))
                .write(1, (byte) (original.getLocation().getYaw() * 256.0F / 360.0F));

        return p;
    }
}

class DummyConnection extends Connection {
    public DummyConnection() {
        super(PacketFlow.SERVERBOUND);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        // No operation for dummy connection
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        // No operation for dummy connection
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        // Suppress exceptions for dummy connection
    }
}
