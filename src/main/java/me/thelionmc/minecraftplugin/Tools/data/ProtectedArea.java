package me.thelionmc.minecraftplugin.Tools.data;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.entity.Shulker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProtectedArea {
    private final int x1, y1, z1, x2, y2, z2;
    private final Map<Location, Material> originalBlocks = new HashMap<>();
    private final List<Shulker> shulkers = new ArrayList<>();

    public ProtectedArea(int x1, int y1, int z1, int x2, int y2, int z2) {
        this.x1 = Math.min(x1, x2);
        this.y1 = Math.min(y1, y2);
        this.z1 = Math.min(z1, z2);
        this.x2 = Math.max(x1, x2);
        this.y2 = Math.max(y1, y2);
        this.z2 = Math.max(z1, z2);
    }

    public boolean isInside(Block block) {
        int x = block.getX();
        int y = block.getY();
        int z = block.getZ();
        return x >= x1 && x <= x2 && y >= y1 && y <= y2 && z >= z1 && z <= z2;
    }

    public void toggleVisibility(Player player, boolean show) {
        World world = player.getWorld();

        if (show) {
            Location[] corners = new Location[]{
                    new Location(world, x1, y1, z1),
                    new Location(world, x2, y2, z2)
            };

            for (Location corner : corners) {
                if (!originalBlocks.containsKey(corner)) {
                    Block block = world.getBlockAt(corner);
                    originalBlocks.put(corner, block.getType());
                    if (block.getType().equals(Material.AIR)) {
                        block.setType(Material.GLOWSTONE);
                    }
                }
                spawnGlowingShulker(corner);
            }
        } else {
            for (Location location : originalBlocks.keySet()) {
                Block block = world.getBlockAt(location);
                Material originalMaterial = originalBlocks.get(location);
                if(block.getType().equals(Material.AIR)) {
                    block.setType(Material.AIR);
                }
            }
            originalBlocks.clear();
            removeShulkers();
        }
    }
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("x1", x1);
        map.put("y1", y1);
        map.put("z1", z1);
        map.put("x2", x2);
        map.put("y2", y2);
        map.put("z2", z2);
        return map;
    }

    public static ProtectedArea fromMap(Map<String, Object> map) {
        int x1 = (int) map.get("x1");
        int y1 = (int) map.get("y1");
        int z1 = (int) map.get("z1");
        int x2 = (int) map.get("x2");
        int y2 = (int) map.get("y2");
        int z2 = (int) map.get("z2");
        return new ProtectedArea(x1, y1, z1, x2, y2, z2);
    }
    private void spawnGlowingShulker(Location location) {
        World world = location.getWorld();
        Location shulkerLocation = location.clone().add(0, 0, 0);

        Shulker shulker = world.spawn(shulkerLocation, Shulker.class, entity -> {
            entity.setInvisible(true);
            entity.setGlowing(true);
            entity.setAI(false);
            entity.setSilent(true);
            entity.setInvulnerable(true);
            entity.setLootTable(null);
        });

        shulkers.add(shulker);
    }

    private void removeShulkers() {
        for (Shulker shulker : shulkers) {
            if (!shulker.isDead()) {
                shulker.remove();
            }
        }
        shulkers.clear();
    }
}
