package me.thelionmc.minecraftplugin.Groups;

import me.thelionmc.minecraftplugin.Abilities.Wizard.*;

import java.util.ArrayList;

public class Wizard extends AbilityGroup {
    public Wizard() {
        this.abilities = new ArrayList<>();
        defineAbilities();
    }
    @Override
    protected void defineAbilities() {
        addAbility(new WizardAbility1());
        addAbility(new WizardAbility2());
        addAbility(new WizardAbility3());
    }

    public String displayName() {return "Wizard";}
}
