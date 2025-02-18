package me.thelionmc.minecraftplugin;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import me.thelionmc.minecraftplugin.Groups.*;
import org.bukkit.Bukkit;
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
    public Map<String, AbilityGroup> classMap = new HashMap();

    public ClassManager(Plugin plugin, GlintSMP mainClass) {
        System.out.println("Initializing ClassManager...");

        this.mainClass = mainClass;
        this.plugin = plugin;

        if (!mainClass.getDataFolder().exists()) {
            mainClass.getDataFolder().mkdirs();
        }

        // Ensure the classData.yml file exists
        File classDataFile = new File(mainClass.getDataFolder(), "classData.yml");
        if (!classDataFile.exists()) {
            try {
                classDataFile.createNewFile(); // Create the file if it doesn't exist
            } catch (IOException e) {
                e.printStackTrace();
                mainClass.getLogger().severe("Could not create classData.yml file!");
            }
        }

        // Load the classData.yml configuration
        this.classData = YamlConfiguration.loadConfiguration(classDataFile);

        populateClassMap();

        // Initialize the class map
    }

    private void populateClassMap() {
        classMap.put("Assassin", new Assassin());
        classMap.put("Mischief", new Mischief());
        classMap.put("Farmer", new Farmer());
        classMap.put("Warrior", new Warrior());
        classMap.put("Medic", new Medic(plugin, mainClass));
        classMap.put("Miner", new Ninja());
        classMap.put("Angel", new Angel());
        classMap.put("Aqua", new Aqua());
        classMap.put("Wizard", new Wizard());
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

    public AbilityGroup getPlayerGroup(UUID playerID) { return classMap.get(getPlayerClass(playerID)); }

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
