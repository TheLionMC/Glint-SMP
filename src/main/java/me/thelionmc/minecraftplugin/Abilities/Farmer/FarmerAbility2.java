package me.thelionmc.minecraftplugin.Abilities.Farmer;

import me.thelionmc.minecraftplugin.Abilities.Ability;
import me.thelionmc.minecraftplugin.Abilities.Cooldown;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class FarmerAbility2 extends Ability {
    Map<UUID, Long> cools = new HashMap<>();

    public FarmerAbility2() {
        super();
        this.cooldownSeconds = 5; // Set custom cooldown for Assassin Ability 1
    }

    public boolean execute(Player player) {
        return true;
    }

    public String abilityName() {return "Ability 2 Farmer Class";}
}
