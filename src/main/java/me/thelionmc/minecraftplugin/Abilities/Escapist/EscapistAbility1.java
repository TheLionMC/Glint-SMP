package me.thelionmc.minecraftplugin.Abilities.Escapist;

import me.thelionmc.minecraftplugin.Abilities.Ability;
import me.thelionmc.minecraftplugin.Abilities.Cooldown;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class EscapistAbility1 extends Ability {
    Map<UUID, Long> cools = new HashMap<>();

    public EscapistAbility1() {
        super();
        this.cooldownSeconds = 10; // Set custom cooldown for Assassin Ability 1
    }

    public boolean execute(Player player) {
        return true;
    }

    public String abilityName() {return "Ability 1 Escapist Class";}
}
