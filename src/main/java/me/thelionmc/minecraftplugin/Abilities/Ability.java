package me.thelionmc.minecraftplugin.Abilities;

import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public abstract class Ability extends Cooldown {
    private boolean isEnabled = false;

    public Ability() {
        super();
    }

    public abstract boolean execute(Player player);

    public abstract String abilityName();

    public String displayName() {
        if(isEnabled) {
            return abilityName();
        }
        return "???";
    }

    public void useAbility(Player player) {
        if(!onCooldown(player) && isEnabled) {
            if (execute(player)) {
                player.getWorld().playSound(player.getLocation(), Sound.ENTITY_EVOKER_CAST_SPELL, 1, 1);
                cools.put(player.getUniqueId(), System.currentTimeMillis());
                return;
            }
        }

        player.getWorld().playSound(player.getLocation(), Sound.ITEM_SHIELD_BREAK, 1, 1);
        if(!isEnabled) {
            player.sendMessage(ChatColor.AQUA + displayName() + ChatColor.YELLOW + " is not yet enabled!");
        } else {
            player.sendMessage(ChatColor.AQUA + displayName() + ChatColor.YELLOW + " is on cooldown!");
        }

    }

    public void setEnabled(boolean b) {
        isEnabled = b;
    } public boolean isEnabled() {
        return isEnabled;
    }

}
