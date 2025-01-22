package me.thelionmc.minecraftplugin.Groups;

import me.thelionmc.minecraftplugin.Abilities.Miner.*;

public class Miner extends AbilityGroup {
    @Override
    protected void defineAbilities() {
        addAbility(new MinerAbility1());
        addAbility(new MinerAbility2());
        addAbility(new MinerAbility3());
    }
}
