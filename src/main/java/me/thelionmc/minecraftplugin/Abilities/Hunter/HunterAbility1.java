package me.thelionmc.minecraftplugin.Abilities.Hunter;

import me.thelionmc.minecraftplugin.Abilities.Ability;
import me.thelionmc.minecraftplugin.Abilities.Cooldown;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class HunterAbility1 extends Cooldown implements Ability {
    Map<UUID, Long> cools = new HashMap<>();

    public HunterAbility1() {
        super();
        this.cooldownSeconds = 10; // Set custom cooldown for Assassin Ability 1
    }

    public void execute(Player player) {

    }

    public String displayName() {return "Ability 1 Hunter Class";}
}
