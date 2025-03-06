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
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.*;
import net.minecraft.server.network.CommonListenerCookie;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.level.GameType;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.craftbukkit.v1_21_R1.CraftServer;
import org.bukkit.craftbukkit.v1_21_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_21_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_21_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

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

        this.setItemSlot(net.minecraft.world.entity.EquipmentSlot.HEAD, ((CraftPlayer) original).getHandle().getItemBySlot(net.minecraft.world.entity.EquipmentSlot.HEAD).copy());
        this.setItemSlot(net.minecraft.world.entity.EquipmentSlot.CHEST, ((CraftPlayer) original).getHandle().getItemBySlot(net.minecraft.world.entity.EquipmentSlot.CHEST).copy());
        this.setItemSlot(net.minecraft.world.entity.EquipmentSlot.LEGS, ((CraftPlayer) original).getHandle().getItemBySlot(net.minecraft.world.entity.EquipmentSlot.LEGS).copy());
        this.setItemSlot(net.minecraft.world.entity.EquipmentSlot.FEET, ((CraftPlayer) original).getHandle().getItemBySlot(net.minecraft.world.entity.EquipmentSlot.FEET).copy());

        ItemStack strongestWeapon = getStrongestWeapon(original);
        if (strongestWeapon != null) {
            this.setItemSlot(net.minecraft.world.entity.EquipmentSlot.MAINHAND, CraftItemStack.asNMSCopy(strongestWeapon));
        }

        ItemStack offhandItem = original.getInventory().getItemInOffHand();
        if (offhandItem != null) {
            this.setItemSlot(net.minecraft.world.entity.EquipmentSlot.OFFHAND, CraftItemStack.asNMSCopy(offhandItem));
        }

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

    private ItemStack getStrongestWeapon(Player player) {
        ItemStack strongest = null;
        double highestDamage = 0;
        for (ItemStack item : player.getInventory().getContents()) {
            if (item != null && isWeapon(item)) {
                double damage = getWeaponDamage(item);
                if (damage > highestDamage) {
                    highestDamage = damage;
                    strongest = item;
                }
            }
        }
        return strongest;
    }

    private boolean isWeapon(ItemStack item) {
        String name = item.getType().toString();
        return name.contains("SWORD") || name.contains("AXE");
    }

    private double getWeaponDamage(ItemStack item) {
        switch(item.getType()) {
            case WOODEN_SWORD:
                return 4;
            case STONE_SWORD:
                return 5;
            case IRON_SWORD:
                return 6;
            case GOLDEN_SWORD:
                return 4;
            case DIAMOND_SWORD:
                return 7;
            case NETHERITE_SWORD:
                return 8;
            case WOODEN_AXE:
                return 3;
            case STONE_AXE:
                return 4;
            case IRON_AXE:
                return 5;
            case GOLDEN_AXE:
                return 3;
            case DIAMOND_AXE:
                return 6;
            case NETHERITE_AXE:
                return 6;
            default:
                return 0;
        }
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

        p.getIntegers().write(0, this.getBukkitEntity().getEntityId());
        p.getUUIDs().write(0, uuid);
        p.getEntityTypeModifier().write(0, org.bukkit.entity.EntityType.PLAYER);
        p.getDoubles() // coords
                .write(0, original.getLocation().getX())
                .write(1, original.getLocation().getY())
                .write(2, original.getLocation().getZ());
        p.getBytes() // pitch/yaw
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
        for (int i = 0; i < 10; i++) {
            getBukkitEntity().getWorld().spawnParticle(Particle.ASH, l.getX() + r.nextDouble(range), l.getY() + r.nextDouble(range), l.getZ() + r.nextDouble(range), 1);
        }
        getBukkitEntity().kickPlayer("Deleted!");
        for (Player p : Bukkit.getOnlinePlayers()) {
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