package me.thelionmc.minecraftplugin.Groups;

import me.thelionmc.minecraftplugin.Abilities.Angel.*;

public class Angel extends AbilityGroup {
    public Angel() {
        defineAbilities();
    }
    @Override
    protected void defineAbilities() {
        addAbility(new AngelAbility1());
        addAbility(new AngelAbility2());
        addAbility(new AngelAbility3());
    }

    public String displayName() {return "Angel";}
}
