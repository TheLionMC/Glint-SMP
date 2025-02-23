package me.thelionmc.minecraftplugin.Abilities.Angel;

import me.thelionmc.minecraftplugin.Abilities.Ability;
import me.thelionmc.minecraftplugin.Abilities.Cooldown;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AngelAbility3 extends Ability {
    Map<UUID, Long> cools = new HashMap<>();

    public AngelAbility3() {
        super();
        this.cooldownSeconds = 600;
    }

    public boolean execute(Player player) {
        return true;
    }

    public String abilityName() {return "Ability 3 Angel Class";}
}
