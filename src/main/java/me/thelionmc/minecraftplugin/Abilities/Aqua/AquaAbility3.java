package me.thelionmc.minecraftplugin.Abilities.Aqua;

import me.thelionmc.minecraftplugin.Abilities.Ability;
import me.thelionmc.minecraftplugin.Abilities.Cooldown;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AquaAbility3 extends Ability {
    Map<UUID, Long> cools = new HashMap<>();

    public AquaAbility3() {
        super();
        this.cooldownSeconds = 10; // Set custom cooldown for Assassin Ability 1
    }

    public boolean execute(Player player) {
        return true;
    }
    public String abilityName() {return "Ability 3 Aqua Class";}
}
