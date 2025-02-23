package me.thelionmc.minecraftplugin;

import java.io.File;
import java.io.IOException;
import java.util.*;

import me.thelionmc.minecraftplugin.Abilities.Ability;
import me.thelionmc.minecraftplugin.Groups.*;
import org.bukkit.Bukkit;
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
        this.enabledClassesData = YamlConfiguration.loadConfiguration(enabledClassesFile);

        populateClassMap();
        populateEnabledClassesList();
        populatePlayerGroupsMap();
        enableAbilities();
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

        for(String string : classDataSection.getKeys(false)) {
            Bukkit.getLogger().info(string);
            playerGroups.put(UUID.fromString(string), classMap.get(groupData.getString(string)));
        }
    } public void saveGroupData() {
        for(UUID uuid : playerGroups.keySet()) {
            groupData.set(uuid.toString(), playerGroups.get(uuid).displayName());
        }

        try {
            groupData.save(groupDataFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void populateEnabledClassesList() {
        List<String> list = enabledClassesData.getStringList("enabledClasses");
        if(list.isEmpty()) {
            enabledClasses.add(randomGroup());
        }

        for(String s : list) {
            enabledClasses.add(classMap.get(s));
        }
    } public void saveEnabledClassData() {
        ArrayList<String> enabled = new ArrayList<>();
        for(AbilityGroup a : enabledClasses) {
            enabled.add(a.displayName());
        }

        enabledClassesData.set("enabledClasses", enabled);

        for(AbilityGroup group : classMap.values()) {
            ArrayList<Integer> enabledAbilities = new ArrayList<>();
            for(Ability ability : group.getAbilities()) {
                if(ability.isEnabled()) {
                    enabledAbilities.add(group.getAbilities().indexOf(ability));
                }
            }
            enabledClassesData.set(group.displayName(), enabledAbilities);
        }

         try {
            enabledClassesData.save(enabledClassesFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private void enableAbilities() {
        for(String string : enabledClassesData.getKeys(false)) {
            if(classMap.containsKey(string)) {
                AbilityGroup group = classMap.get(string);
                List<Integer> enabledAbilities = enabledClassesData.getIntegerList(string);
                for (Integer enabledAbility : enabledAbilities) {
                    group.getAbility(enabledAbility).setEnabled(true);
                }
            }
        }
    }

    public void enablePlayerGroup(AbilityGroup group) {
        if(enabledClasses.contains(group)) {
            return;
        }
        enabledClasses.add(group);
    } public boolean disablePlayerGroup(AbilityGroup group) {
        if(!enabledClasses.contains(group) || enabledClasses.size() == 1) {
            return false;
        }

        enabledClasses.remove(group);

        for(UUID uuid : playerGroups.keySet()) {
            if(playerGroups.get(uuid) == group) {
                playerGroups.put(uuid, randomEnabledGroup());
            }
        }
        return true;
    }

    public boolean isEnabled(AbilityGroup group) {
        return enabledClasses.contains(group);
    }

    public boolean setPlayerGroup(UUID playerID, AbilityGroup group) { //returns false if its disabled
        if(isEnabled(group)) {
            playerGroups.put(playerID, group);
            return true;
        }
        return false;
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
