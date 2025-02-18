package me.thelionmc.minecraftplugin;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.*;

public class TrustManager {
    private FileConfiguration trustData;
    private GlintSMP main;
    Map<UUID, List<UUID>> trusted;

    public TrustManager(GlintSMP main) {
        trusted = new HashMap<>();
        this.main = main;
        this.trustData = YamlConfiguration.loadConfiguration(new File(main.getDataFolder(), "shardData.yml"));

        for (String key : trustData.getConfigurationSection("").getKeys(false)) {
            List<UUID> trustedPlayers = new ArrayList<>();
            for (String trustedPlayer : trustData.getStringList(key)) {
                trustedPlayers.add(Bukkit.getOfflinePlayer(trustedPlayer).getUniqueId());
            }
            trusted.put(Bukkit.getOfflinePlayer(key).getUniqueId(), trustedPlayers);
        }
    }

    public List<UUID> getTrustedPlayers(UUID uuid) {
        return trusted.get(uuid);
    }

    public void trustPlayer(UUID playerTrusting, UUID playerToTrust) {
        List<UUID> list = new ArrayList<>();
        if(!trusted.containsKey(playerTrusting)) {
            list.add(playerToTrust);
        } else {
            list = trusted.get(playerTrusting);
            list.add(playerToTrust);
        }
        trusted.put(playerTrusting, list);
    }

    public void distrustPlayer(UUID playerDistrusting, UUID playerToDistrust) {
        if(trusted.containsKey(playerDistrusting)) {
            List<UUID> list = trusted.get(playerDistrusting);
        }
    }

    public boolean isPlayerTrustedByPlayer(UUID trustedPlayer, UUID player) {
        if(!trusted.containsKey(player)) {
            return false;
        }
        return trusted.get(player).contains(trustedPlayer);
    }

    public void saveTrustData() {
        for(UUID key : trusted.keySet()) {
            trustData.set(key.toString(), trusted.get(key));
        }
    }
}
