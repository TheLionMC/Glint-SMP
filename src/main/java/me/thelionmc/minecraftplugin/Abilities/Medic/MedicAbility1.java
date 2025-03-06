package me.thelionmc.minecraftplugin.Abilities.Medic;

import me.thelionmc.minecraftplugin.Abilities.Ability;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.HashMap;
import java.util.UUID;

public class MedicAbility1 extends Ability {
    private Map<UUID, Long> cools = new HashMap<>();
    private Scoreboard scoreboard;
    private ScoreboardManager manager;
    private Objective healthObjective;
    private Set<Player> allowedViewers;
    private Plugin plugin;

    public MedicAbility1(Plugin plugin) {
        super();
        this.plugin = plugin;
        this.cooldownSeconds = 10; //cd
    }

    public boolean execute(Player player) {
        cools.put(player.getUniqueId(), System.currentTimeMillis());
        allowedViewers = new HashSet<>();
        manager = Bukkit.getScoreboardManager();
        scoreboard = manager.getNewScoreboard();
        healthObjective = scoreboard.registerNewObjective("showHealth", "dummy", ChatColor.RED + "❤");

        healthObjective.setDisplaySlot(DisplaySlot.BELOW_NAME);

        allowedViewers.add(player);
        player.setScoreboard(scoreboard);

        new BukkitRunnable() {
            @Override
            public void run() {
                allowedViewers.remove(player);
                player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
            }
        }.runTaskLater(plugin, 20 * 30);
        new BukkitRunnable() {
            @Override
            public void run() {
                updateHealthTags(player);
            }
        }.runTaskTimer(plugin, 0, 20);
        return true;
    }

    private void updateHealthTags(Player viewer) {
        if (allowedViewers.contains(viewer)) {
            for (Player target : Bukkit.getOnlinePlayers()) {
                healthObjective.getScore(target.getName()).setScore((int) Math.ceil(target.getHealth()));
            }
            viewer.setScoreboard(scoreboard);
        }
    }

    public String abilityName() {
        return "Health Inspection";
    }
}
