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
        System.out.println("Initializing ClassManager...");

        // Assign plugin to the provided parameter if it's not null, otherwise fallback to mainClass
        this.plugin = plugin != null ? plugin : mainClass;

        if (this.plugin == null) {
            System.out.println("ERROR: Plugin is null! Please check initialization order.");
            return; // Early return if the plugin is still null
        }

        this.mainClass = mainClass;

        // Ensure the plugin's data folder exists
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

        // Initialize the class map
        classMap.put("Assassin", new Assassin());
        classMap.put("Explorer", new Mischief());
        classMap.put("Farmer", new Farmer());
        classMap.put("Hunter", new Warrior());
        classMap.put("Medic", new Medic(mainClass));
        classMap.put("Miner", new Ninja());
        classMap.put("Angel", new Angel());
        classMap.put("Aqua", new Aqua());
        classMap.put("Wizard", new Wizard());

        System.out.println("ClassManager initialized with plugin: " + (plugin != null ? "Initialized" : "Null"));
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
