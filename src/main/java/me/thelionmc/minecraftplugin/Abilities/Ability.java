package me.thelionmc.minecraftplugin.Abilities;

import org.bukkit.entity.Player;

public interface Ability {
    public void useAbility(Player player);
    public void execute(Player player);
    public String displayName();
    public boolean onCooldown(Player player);
    public long cooldownRemaining(Player player);
}
