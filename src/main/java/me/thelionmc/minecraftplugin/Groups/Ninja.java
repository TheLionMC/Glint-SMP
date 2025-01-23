package me.thelionmc.minecraftplugin.Groups;

import me.thelionmc.minecraftplugin.Abilities.Ninja.*;

public class Ninja extends AbilityGroup {
    @Override
    protected void defineAbilities() {
        addAbility(new NinjaAbility1());
        addAbility(new NinjaAbility2());
        addAbility(new NinjaAbility3());
    }
}
