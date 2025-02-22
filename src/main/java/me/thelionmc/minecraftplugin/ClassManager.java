package me.thelionmc.minecraftplugin;

import java.io.File;
import java.io.IOException;
import java.util.*;

import me.thelionmc.minecraftplugin.Groups.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;

public class ClassManager implements Listener {
    private final Plugin plugin;
    private final GlintSMP mainClass;

    private final Map<UUID, AbilityGroup> playerGroups = new HashMap<>();
    private final FileConfiguration groupData;
    private final File groupDataFile;

    public final ArrayList<AbilityGroup> enabledClasses = new ArrayList<>();
    private final FileConfiguration enabledClassesData;
    private final File enabledClassesFile;

    public Map<String, AbilityGroup> classMap = new HashMap<>();

    ShardManager shardManager;

    public ClassManager(Plugin plugin, GlintSMP mainClass) {
        this.mainClass = mainClass;
        this.plugin = plugin;
        shardManager = new ShardManager(plugin, mainClass);

        groupDataFile = new File(mainClass.getDataFolder(), "classData.yml");
        enabledClassesFile = new File(mainClass.getDataFolder(), "enabledClasses.yml");

        this.groupData = YamlConfiguration.loadConfiguration(groupDataFile);
        groupData.set("classData", playerGroups);
        this.enabledClassesData = YamlConfiguration.loadConfiguration(enabledClassesFile);

        populateClassMap();
        populateEnabledClassesList();
        populatePlayerGroupsMap();
    }

    private void populateClassMap() {
        classMap.put("Assassin", new Assassin(plugin, mainClass));
        classMap.put("Mischief", new Mischief(plugin, mainClass));
        classMap.put("Farmer", new Farmer());
        classMap.put("Warrior", new Warrior());
        classMap.put("Medic", new Medic(plugin, mainClass));
        classMap.put("Ninja", new Ninja(plugin, shardManager));
        classMap.put("Angel", new Angel());
        classMap.put("Aqua", new Aqua());
        classMap.put("Wizard", new Wizard());
    }

    private void populatePlayerGroupsMap() {
        ConfigurationSection classDataSection = groupData.getConfigurationSection("classData");
        if (classDataSection == null) {
            return;
        }

        Map<String, Object> rawMap = classDataSection.getValues(false);

        for (Map.Entry<String, Object> entry : rawMap.entrySet()) {
            if (entry.getValue() instanceof AbilityGroup) {
                if (enabledClasses.contains(entry.getValue())) {
                    playerGroups.put(UUID.fromString(entry.getKey()), (AbilityGroup) entry.getValue());
                    return;
                }
                playerGroups.put(UUID.fromString(entry.getKey()), randomEnabledGroup());
            }
        }
    } public void saveClassData() {
        groupData.set("classData", playerGroups);
        try {
            groupData.save(groupDataFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void populateEnabledClassesList() {
        List<?> rawList = enabledClassesData.getList("groups");
        if (rawList != null) {
            for (Object obj : rawList) {
                if (obj instanceof AbilityGroup) {
                    enabledClasses.add((AbilityGroup) obj);
                }
            }
        }
    } public void saveEnabledClassData() {
        enabledClassesData.set("enabledClasses", enabledClasses);
        try {
            enabledClassesData.save(enabledClassesFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public void enablePlayerGroup(AbilityGroup group) {
        if(enabledClasses.contains(group)) {
            return;
        }
        enabledClasses.add(group);
    } public void disablePlayerGroup(AbilityGroup group) {
        if(!enabledClasses.contains(group)) {
            return;
        }
        for(UUID uuid : playerGroups.keySet()) {
            if(playerGroups.get(uuid) == group) {
                playerGroups.put(uuid, randomEnabledGroup());
            }
        }
        enabledClasses.remove(group);
    }

    public void setPlayerGroup(UUID playerID, AbilityGroup group) {
        playerGroups.put(playerID, group);
    } public AbilityGroup getPlayerGroup(UUID playerID) {
        return playerGroups.get(playerID);
    } public boolean playerHasGroup(UUID playerId) {
        return playerGroups.containsKey(playerId);
    }

    private AbilityGroup randomGroup() {
        Collection<AbilityGroup> classSet = classMap.values();
        int index = new Random().nextInt(classSet.size());
        return (AbilityGroup) classSet.toArray()[index];
    }

    public AbilityGroup randomEnabledGroup() {
        if(enabledClasses.isEmpty()) {
            enabledClasses.add(randomGroup());
        }
        Random random = new Random();
        int randomInt = random.nextInt(enabledClasses.size());
        return enabledClasses.get(randomInt);
    }

    @EventHandler
    void onPlayerJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        UUID playerID = player.getUniqueId();
        if (!this.groupData.contains(playerID.toString())) {
            this.setPlayerGroup(playerID, randomEnabledGroup());
        }
    }
}
