package me.thelionmc.minecraftplugin.Abilities.Farmer;

import me.thelionmc.minecraftplugin.Abilities.Ability;
import me.thelionmc.minecraftplugin.Abilities.Cooldown;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class FarmerAbility3 extends Cooldown implements Ability {
    Map<UUID, Long> cools = new HashMap<>();

    public FarmerAbility3() {
        super();
        this.cooldownSeconds = 272;
    }

    public void execute(Player player) {

    }

    public String abilityName() {return "Ability 3 Farmer Class";}
}
