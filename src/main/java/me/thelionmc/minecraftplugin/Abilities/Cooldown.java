package me.thelionmc.minecraftplugin.Abilities;

import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public abstract class Cooldown {
    Map<UUID, Long> cools = new HashMap<>();
    protected int cooldownSeconds = 10; //default value, we change this in the ability classes

    public boolean onCooldown(Player player) {
        UUID id = player.getUniqueId();
        if(cools.containsKey(id)) {
            Long cooldownMillis = (long) (cooldownSeconds * 1000);
            return (cooldownMillis - (System.currentTimeMillis() - cools.get(id))) > 0;
        } else {
            return false;
        }
    }

    public long cooldownRemaining(Player player) {
        UUID id = player.getUniqueId();
        if(onCooldown(player) && cools.containsKey(id)) {
            Long cooldownMillis = (long) (cooldownSeconds * 1000);
            return cooldownMillis - (System.currentTimeMillis() - cools.get(id));
        } else {
            return 0L;
        }
    }

    public void setCooldownSeconds(UUID uuid, Long time) {
        cools.put(uuid, time);
    }

    public int getDefaultCooldownSeconds() { return cooldownSeconds; }

    public abstract void execute(Player player);

    public abstract String abilityName();

    public void useAbility(Player player) {
        if(!onCooldown(player)) {
            player.getWorld().playSound(player.getLocation(), Sound.ENTITY_EVOKER_CAST_SPELL, 1, 1);
            cools.put(player.getUniqueId(), System.currentTimeMillis());
            execute(player);
        } else {
            player.getWorld().playSound(player.getLocation(), Sound.ITEM_SHIELD_BREAK, 1, 1);
            player.sendMessage(ChatColor.AQUA + abilityName() + ChatColor.YELLOW + " is on cooldown!");
        }
    }
}
