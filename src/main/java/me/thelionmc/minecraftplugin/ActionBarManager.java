package me.thelionmc.minecraftplugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import me.thelionmc.minecraftplugin.Abilities.Ability;
import me.thelionmc.minecraftplugin.Groups.AbilityGroup;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

public class ActionBarManager implements Listener {
    GlintSMP main;
    ClassManager classManager;
    Plugin plugin;
    Map<UUID, Integer> selectedAbility = new HashMap<>();

    public ActionBarManager(GlintSMP main, final ClassManager classManager, Plugin plugin) {
        this.main = main;
        this.classManager = classManager;
        this.plugin = plugin;

        for(Player player : Bukkit.getOnlinePlayers()) {
            selectedAbility.put(player.getUniqueId(), 1);
        }

        new BukkitRunnable() {
            @Override
            public void run () {
                for(Player player : Bukkit.getOnlinePlayers()) {
                    AbilityGroup group = classManager.getPlayerGroup(player.getUniqueId());

                    if(selectedAbility.containsKey(player.getUniqueId())) {
                        Ability ability = group.getAbilities().get(selectedAbility.get(player.getUniqueId()) - 1);
                        String initialString = ChatColor.AQUA + group.displayName() + " " + (group.getAbilities().indexOf(ability) + 1) + ChatColor.WHITE + " | " + ChatColor.AQUA + ability.displayName() + ChatColor.WHITE + " | ";
                        StringBuilder builder = new StringBuilder(initialString);

                        if(ability.isEnabled()) {
                            if(ability.onCooldown(player)) {
                                long cool = ability.cooldownRemaining(player);
                                long minutes = (cool / 1000) / 60;
                                long seconds = (cool / 1000) % 60;
                                builder.append(ChatColor.YELLOW + "" + minutes + "m " + seconds + "s");
                            } else {
                                builder.append(ChatColor.GREEN + "Ready!");
                            }
                        }

                        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(String.valueOf(builder)));
                    } else {
                        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(""));
                    }
                }
            }
        }.runTaskTimer(plugin, 0, 1);
    }

    public void nextSelectedAbility(Player player) {
        AbilityGroup group = classManager.getPlayerGroup(player.getUniqueId());
        if(selectedAbility.containsKey(player.getUniqueId())) {
            int selected = selectedAbility.get(player.getUniqueId());

            if (selected + 1 > group.getAbilities().size()) {
                selectedAbility.put(player.getUniqueId(), 1);
                return;
            }

            selectedAbility.put(player.getUniqueId(), selected + 1);
        } else {
            selectedAbility.put(player.getUniqueId(), 1);
        }
    }

    public void useAbility(Player player) {
        AbilityGroup group = classManager.getPlayerGroup(player.getUniqueId());
        int selected = selectedAbility.get(player.getUniqueId());

        group.getAbilities().get(selected - 1).useAbility(player);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        selectedAbility.put(e.getPlayer().getUniqueId(), 1);
    }

}
