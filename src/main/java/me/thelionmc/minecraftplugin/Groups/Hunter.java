package me.thelionmc.minecraftplugin.Groups;

import me.thelionmc.minecraftplugin.Abilities.Hunter.*;

public class Hunter extends AbilityGroup {
    @Override
    protected void defineAbilities() {
        addAbility(new HunterAbility1());
        addAbility(new HunterAbility2());
        addAbility(new HunterAbility3());
    }
}
