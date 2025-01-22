package me.thelionmc.minecraftplugin.Groups;

import me.thelionmc.minecraftplugin.Abilities.Assassin.*;

public class Assassin extends AbilityGroup {
    protected void defineAbilities() {
        addAbility(new AssassinAbility1());
        addAbility(new AssassinAbility2());
        addAbility(new AssassinAbility3());
    }
}
