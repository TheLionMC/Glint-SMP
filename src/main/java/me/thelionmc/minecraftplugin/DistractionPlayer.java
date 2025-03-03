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
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.*;
import net.minecraft.server.network.CommonListenerCookie;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.level.GameType;
import net.minecraft.world.phys.Vec3;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.craftbukkit.v1_21_R1.CraftServer;
import org.bukkit.craftbukkit.v1_21_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_21_R1.entity.CraftPlayer;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.checkerframework.framework.qual.Covariant;

import java.util.*;

public class DistractionPlayer extends ServerPlayer {
    Player original;
    ServerLevel level;
    Location spawnLocation;
    ProtocolManager manager;
    GameProfile profile;
    Plugin plugin;

    public DistractionPlayer(Player original, Player target, Location location, GameProfile fakeProfile, Plugin plugin) {
        super(
                ((CraftServer) Bukkit.getServer()).getServer(),
                ((CraftWorld) original.getWorld()).getHandle(),
                fakeProfile,
                ClientInformation.createDefault()
        );

        this.level = ((CraftWorld) original.getWorld()).getHandle();
        this.profile = fakeProfile;
        this.original = original;
        this.spawnLocation = location;
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

        connection = new TutNetHandler(server, new TutNetworkManager(PacketFlow.CLIENTBOUND), this, profile);
        getBukkitEntity().setNoDamageTicks(0);

        level.addFreshEntity(this);

        new BukkitRunnable() {
            @Override
            public void run () {
                if(getBukkitEntity().getLocation().distance(target.getLocation()) > 50) {
                    deletePlayer();
                    cancel();
                }

                Location targetLocation = target.getLocation().clone().add(0, target.getEyeHeight(), 0);
                Vector direction = targetLocation.toVector().subtract(getBukkitEntity().getLocation().toVector());
                float yaw = (float) Math.toDegrees(Math.atan2(direction.getZ(), direction.getX())) - 90;
                float pitch = (float) -Math.toDegrees(Math.atan2(direction.getY(), Math.sqrt(direction.getX() * direction.getX() + direction.getZ() * direction.getZ())));

                setRot(yaw, pitch);
                setYHeadRot(yaw);
                setSprinting(true);
                if(getDeltaMovement().x + getDeltaMovement().y <= 0) {
                    setSprinting(false);
                }
            }
        }.runTaskTimer(plugin, 0, 1);
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

    @Override
    public void tick(){
        super.tick();
        doTick();
    }

    @Override
    public boolean hurt(DamageSource damageSource, float f) {
        if (this.isBlocking()) {
            //block the attack idk how lmao
            this.playSound(SoundEvents.SHIELD_BLOCK, 0.8F, 0.8F + this.level.random.nextFloat() * 0.4F);
        }
        boolean damaged = super.hurt(damageSource, f);
        if (damaged) {
            if (this.hurtMarked) {
                this.hurtMarked = false;
                Bukkit.getScheduler().runTask(plugin, () -> this.hurtMarked = true);
            }
        }
        return damaged;
    }

    public void deletePlayer() {
        double range = 0.5d;
        Random r = new Random();
        Location l = getBukkitEntity().getLocation();
        for(int i = 0; i < 10; i++) {
            getBukkitEntity().getWorld().spawnParticle(Particle.ASH, l.getX() + r.nextDouble(range), l.getY() + + r.nextDouble(range), l.getZ() + + r.nextDouble(range), 1);
        }
        getBukkitEntity().kickPlayer("Deleted!");
        for(Player p : Bukkit.getOnlinePlayers()) {
            p.sendMessage("KILLED BOT!");
        }
    }

}

class TutNetHandler extends ServerGamePacketListenerImpl {
    public TutNetHandler(MinecraftServer minecraftserver, Connection networkmanager, ServerPlayer entityplayer, GameProfile profile) {
        super(minecraftserver, networkmanager, entityplayer, new CommonListenerCookie(profile, 0, ClientInformation.createDefault(), false));
    }
    @Override
    public void send(Packet<?> packet) {
    }
}
class TutNetworkManager extends Connection {
    public TutNetworkManager(PacketFlow enumprotocoldirection) {
        super(enumprotocoldirection);
    }
}
