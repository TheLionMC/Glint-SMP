package me.thelionmc.minecraftplugin.Groups;

import me.thelionmc.minecraftplugin.Abilities.Warrior.*;

public class Warrior extends AbilityGroup {
    public Warrior() {
        defineAbilities();
    }
    @Override
    protected void defineAbilities() {
        addAbility(new WarriorAbility1());
        addAbility(new WarriorAbility2());
        addAbility(new WarriorAbility3());
    }

    public String displayName() {return "Warrior";}
}
