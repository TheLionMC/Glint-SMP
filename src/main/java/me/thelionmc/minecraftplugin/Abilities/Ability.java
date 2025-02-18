package me.thelionmc.minecraftplugin.Abilities;

import org.bukkit.entity.Player;

import java.util.UUID;

public interface Ability {
    public void useAbility(Player player);
    public void execute(Player player);
    public String displayName();
    public boolean onCooldown(Player player);
    public long cooldownRemaining(Player player);
    public int getDefaultCooldownSeconds();
    public void setCooldownSeconds(UUID uuid, Long cool);
}
