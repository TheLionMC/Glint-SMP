package me.thelionmc.minecraftplugin.Groups;

import me.thelionmc.minecraftplugin.Abilities.Angel.*;

import java.util.ArrayList;
import java.util.Map;

public class Angel extends AbilityGroup {
    public Angel() {
        this.abilities = new ArrayList<>();
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
