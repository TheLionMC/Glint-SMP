package me.thelionmc.minecraftplugin.OperatorCommands;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.item.ItemStack;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_21_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_21_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import java.util.ArrayList;
import java.util.List;

public class PrivateTestCommand implements CommandExecutor {
    private BossBar bossBar;
    private final List<ArmorStand> bodyParts = new ArrayList<>();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can execute this command.");
            return true;
        }
        Player player = (Player) sender;
        Location loc = player.getLocation();
        ServerLevel nmsWorld = ((CraftWorld) loc.getWorld()).getHandle();

        // Create boss bar
        bossBar = player.getServer().createBossBar("§c🔥 Fire Demon 🔥", BarColor.RED, BarStyle.SOLID);
        bossBar.setProgress(1.0);
        bossBar.addPlayer(player);

        // Spawn Fire Demon boss with detailed structure
        spawnBoss(nmsWorld, loc);

        player.sendMessage("§4🔥 Fire Demon has been summoned! 🔥");
        return true;
    }

    private void spawnBoss(ServerLevel nmsWorld, Location center) {
        // Head - Magma & Crying Obsidian Eyes
        createBlockEntity(nmsWorld, center, 0, 4, 0, Material.MAGMA_BLOCK);
        createBlockEntity(nmsWorld, center, 1, 4, 0, Material.CRYING_OBSIDIAN);
        createBlockEntity(nmsWorld, center, -1, 4, 0, Material.CRYING_OBSIDIAN);

        // Horns - Blackstone
        createBlockEntity(nmsWorld, center, 1, 5, 0, Material.BLACKSTONE);
        createBlockEntity(nmsWorld, center, -1, 5, 0, Material.BLACKSTONE);

        // Torso - Magma & Obsidian
        for (int i = -1; i <= 1; i++) {
            for (int j = 2; j <= 3; j++) {
                createBlockEntity(nmsWorld, center, i, j, 0, Material.MAGMA_BLOCK);
            }
        }

        // Arms - Blackstone & Magma
        for (int i = -2; i <= 2; i += 4) {
            createBlockEntity(nmsWorld, center, i, 3, 0, Material.BLACKSTONE);
            createBlockEntity(nmsWorld, center, i, 2, 0, Material.MAGMA_BLOCK);
        }

        // Legs - Basalt & Obsidian
        for (int i = -1; i <= 1; i++) {
            createBlockEntity(nmsWorld, center, i, 1, 0, Material.BASALT);
            createBlockEntity(nmsWorld, center, i, 0, 0, Material.OBSIDIAN);
        }
    }

    private void createBlockEntity(ServerLevel nmsWorld, Location center, double offsetX, double offsetY, double offsetZ, Material blockMaterial) {
        ArmorStand part = new ArmorStand(EntityType.ARMOR_STAND, nmsWorld);
        part.setPos(center.getX() + offsetX, center.getY() + offsetY, center.getZ() + offsetZ);
        part.setInvisible(true);
        part.setNoGravity(true);
        part.setMarker(true);

        ItemStack nmsItem = CraftItemStack.asNMSCopy(new org.bukkit.inventory.ItemStack(blockMaterial));
        part.setItemSlot(net.minecraft.world.entity.EquipmentSlot.HEAD, nmsItem);

        nmsWorld.addFreshEntity(part);
        bodyParts.add(part);
    }
}
