package me.thelionmc.minecraftplugin;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.*;
import com.mojang.authlib.GameProfile;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ClientInformation;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.ChatVisiblity;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_21_R1.CraftServer;
import org.bukkit.craftbukkit.v1_21_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_21_R1.entity.CraftPlayer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.*;

import static net.minecraft.core.registries.BuiltInRegistries.ENTITY_TYPE;

public class FakePlayer {
    ProtocolManager manager;

    Location spawn;
    UUID uuid;
    int entityID;
    Player attacker;
    Player target;

    public FakePlayer(Player attacker, Player target) {
        this.uuid = UUID.randomUUID();
        this.entityID = new Random().nextInt(10000, 20000);
        this.attacker = attacker;
        this.target = target;
        this.spawn = attacker.getLocation();

        this.manager = ProtocolLibrary.getProtocolManager();

        for(Player player : Bukkit.getOnlinePlayers()) {
            manager.sendServerPacket(player, playerInfoPacket());
            manager.sendServerPacket(player, addPlayerPacket());
        }
    }

    private PacketContainer playerInfoPacket() {
        PacketContainer p = manager.createPacket(PacketType.Play.Server.PLAYER_INFO);

        p.getPlayerInfoActions().write(0, EnumSet.of(EnumWrappers.PlayerInfoAction.ADD_PLAYER));

        List<PlayerInfoData> playerInfoDataList = new ArrayList<>();

        WrappedGameProfile profile = new WrappedGameProfile(uuid, attacker.getName());
        WrappedGameProfile attackerProfile = WrappedGameProfile.fromPlayer(attacker);

        profile.getProperties().putAll(attackerProfile.getProperties());

        PlayerInfoData data = new PlayerInfoData(
                profile,
                1,
                EnumWrappers.NativeGameMode.SURVIVAL,
                WrappedChatComponent.fromText(attacker.getName())
        );
        playerInfoDataList.add(data);
        p.getPlayerInfoDataLists().write(1, playerInfoDataList);

        return p;
    }


    private PacketContainer addPlayerPacket() {
        PacketContainer p = new PacketContainer(PacketType.Play.Server.SPAWN_ENTITY);

        p.getIntegers().write(0, entityID); //id
        p.getUUIDs().write(0, uuid); //uuid
        p.getEntityTypeModifier().write(0, EntityType.PLAYER); //player entity type
        p.getDoubles() //coords
                .write(0, spawn.getX())
                .write(1, spawn.getY())
                .write(2, spawn.getZ());
        p.getBytes() //pitch/yaw
                .write(0, (byte) (spawn.getPitch() * 256.0F / 360.0F))
                .write(1, (byte) (spawn.getYaw() * 256.0F / 360.0F));

        return p;
    }
}
