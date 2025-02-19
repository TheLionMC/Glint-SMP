package me.thelionmc.minecraftplugin.Groups;

import me.thelionmc.minecraftplugin.Abilities.Farmer.*;

public class Farmer extends AbilityGroup {
    public Farmer() {
        defineAbilities();
    }
    @Override
    protected void defineAbilities() {
        addAbility(new FarmerAbility1());
        addAbility(new FarmerAbility2());
        addAbility(new FarmerAbility3());
    }

    public String displayName() {return "Farmer";}
}
