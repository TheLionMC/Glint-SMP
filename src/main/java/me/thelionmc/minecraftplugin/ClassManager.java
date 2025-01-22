package me.thelionmc.minecraftplugin;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import me.thelionmc.minecraftplugin.Groups.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;

public class ClassManager implements Listener {
    private Plugin plugin;
    private GlintSMP mainClass;
    private FileConfiguration classData;
    Map<String, AbilityGroup> classMap = new HashMap();

    public ClassManager(Plugin plugin, GlintSMP mainClass) {
        this.plugin = plugin;
        this.mainClass = mainClass;
        this.classData = YamlConfiguration.loadConfiguration(new File(mainClass.getDataFolder(), "classData.yml"));

        classMap.put("Assassin", new Assassin());
        classMap.put("Explorer", new Explorer());
        classMap.put("Farmer", new Farmer());
        classMap.put("Hunter", new Hunter());
        classMap.put("Medic", new Medic());
        classMap.put("Miner", new Miner());
    }

    public void saveClassData() {
        try {
            this.classData.save(new File(this.mainClass.getDataFolder(), "classData.yml"));
        } catch (IOException var2) {
            var2.printStackTrace();
        }

    }

    public void setClass(UUID playerID, String className) {
        this.classData.set(playerID.toString(), className);
        this.saveClassData();
    }

    public String getPlayerClass(UUID playerID) {
        return this.classData.getString(playerID.toString());
    }

    public String randomClass() {
        Random random = new Random();
        int randomInt = random.nextInt(6);
        return switch (randomInt) {
            case 0 -> "Assassin";
            case 1 -> "Explorer";
            case 2 -> "Farmer";
            case 3 -> "Hunter";
            case 4 -> "Medic";
            case 5 -> "Miner";
            default -> null;
        };
    }

    @EventHandler
    void onPlayerJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        UUID playerID = player.getUniqueId();
        if (!this.classData.contains(playerID.toString())) {
            this.setClass(playerID, this.randomClass());
        }

    }
}
