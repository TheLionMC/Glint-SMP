package me.thelionmc.minecraftplugin.Abilities.Mischief;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.wrappers.*;
import me.thelionmc.minecraftplugin.Abilities.Ability;
import me.thelionmc.minecraftplugin.Abilities.Cooldown;
import me.thelionmc.minecraftplugin.GlintSMP;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

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

        for (int i = 0; i < 3; i++) {

            FakePlayer dummy = new FakePlayer(player.getName(), spawnLoc, player, player.getPing());
            dummy.spawn();

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
        }

        player.sendMessage("§aYou have summoned 3 dummies to attack " + target.getName() + "!");
    }

    @Override
    public String abilityName() {
        return "Distraction";
    }


    public class FakePlayer {
        private final int entityId;
        private final UUID uuid;
        private final String name;
        private Location location;
        private final WrappedGameProfile gameProfile;
        private int ping;

        public FakePlayer(String name, Location location, Player player, int ping) {
            this.name = name;
            this.location = location;
            this.entityId = entityIdCounter.getAndDecrement();
            this.uuid = UUID.randomUUID();
            this.gameProfile = new WrappedGameProfile(uuid, name);
            this.ping = ping;

            gameProfile.getProperties().put("textures", (WrappedSignedProperty) getSkinProperty(player));
        }

        public Location getLocation() {
            return location;
        }

        public void spawn() {
            // 1. Send PLAYER_INFO ADD_PLAYER packet
            PacketContainer playerInfoAdd = protocolManager.createPacket(PacketType.Play.Server.PLAYER_INFO);
            playerInfoAdd.getPlayerInfoAction().write(0, EnumWrappers.PlayerInfoAction.ADD_PLAYER);
            List<PlayerInfoData> infoData = new ArrayList<>();
            infoData.add(new PlayerInfoData(gameProfile, ping, EnumWrappers.NativeGameMode.SURVIVAL,
                    WrappedChatComponent.fromText(name)));
            playerInfoAdd.getPlayerInfoDataLists().write(0, infoData);
            protocolManager.broadcastServerPacket(playerInfoAdd);

            // 2. Send NAMED_ENTITY_SPAWN packet
            PacketContainer spawnPacket = protocolManager.createPacket(PacketType.Play.Server.SPAWN_ENTITY);
            spawnPacket.getIntegers().write(0, entityId);
            spawnPacket.getUUIDs().write(0, uuid);
            spawnPacket.getDoubles().write(0, location.getX());
            spawnPacket.getDoubles().write(1, location.getY());
            spawnPacket.getDoubles().write(2, location.getZ());
            spawnPacket.getBytes().write(0, (byte) (location.getPitch() * 256 / 360));
            spawnPacket.getBytes().write(1, (byte) (location.getYaw() * 256 / 360));
            protocolManager.broadcastServerPacket(spawnPacket);

            // 3. Send ENTITY_METADATA packet to set health (30.0 = 15 hearts) and other properties
            PacketContainer metaPacket = protocolManager.createPacket(PacketType.Play.Server.ENTITY_METADATA);
            metaPacket.getIntegers().write(0, entityId); // used to be getModifier() instead of getIntegers()
            WrappedDataWatcher watcher = new WrappedDataWatcher();
            watcher.setObject(6, 20.0f);
            metaPacket.getWatchableCollectionModifier().write(0, watcher.getWatchableObjects());
            protocolManager.broadcastServerPacket(metaPacket);
        }


        public void teleport(Location newLocation) {
            this.location = newLocation;
            PacketContainer teleportPacket = protocolManager.createPacket(PacketType.Play.Server.ENTITY_TELEPORT);
            teleportPacket.getIntegers().write(0, entityId);
            teleportPacket.getDoubles().write(0, newLocation.getX());
            teleportPacket.getDoubles().write(1, newLocation.getY());
            teleportPacket.getDoubles().write(2, newLocation.getZ());
            teleportPacket.getBytes().write(0, (byte) (newLocation.getPitch() * 256 / 360));
            teleportPacket.getBytes().write(1, (byte) (newLocation.getYaw() * 256 / 360));
            protocolManager.broadcastServerPacket(teleportPacket);
        }

        public void destroy() {
            PacketContainer playerInfoRemove = protocolManager.createPacket(PacketType.Play.Server.PLAYER_INFO);
            playerInfoRemove.getPlayerInfoAction().write(0, EnumWrappers.PlayerInfoAction.REMOVE_PLAYER);
            List<PlayerInfoData> infoData = new ArrayList<>();
            infoData.add(new PlayerInfoData(gameProfile, 0, EnumWrappers.NativeGameMode.SURVIVAL,
                    WrappedChatComponent.fromText(name)));
            playerInfoRemove.getPlayerInfoDataLists().write(1, infoData);
            protocolManager.broadcastServerPacket(playerInfoRemove);

            PacketContainer destroyPacket = protocolManager.createPacket(PacketType.Play.Server.ENTITY_DESTROY);
            destroyPacket.getIntegerArrays().write(0, new int[]{entityId});
            protocolManager.broadcastServerPacket(destroyPacket);
        }
    }

    public com.comphenix.protocol.wrappers.WrappedSignedProperty getSkinProperty(Player player) {
        GameProfile profile = ((CraftPlayer) player).getHandle().getProfile();  // Getting the profile
        for (WrappedSignProperty property : WrappedGameProfile.fromGameProfile(profile).getProperties().values()) {
            if (property.getName().equals("textures")) {
                return property;  // Return the texture property (skin data)
            }
        }
        return null;
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
