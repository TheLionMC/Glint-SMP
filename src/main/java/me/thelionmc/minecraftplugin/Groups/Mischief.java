package me.thelionmc.minecraftplugin.Groups;

import me.thelionmc.minecraftplugin.Abilities.Mischief.*;

public class Mischief extends AbilityGroup {
    @Override
    protected void defineAbilities() {
        addAbility(new MischiefAbility1());
        addAbility(new MischiefAbility2());
        addAbility(new MischiefAbility3());
    }
}
