package me.thelionmc.minecraftplugin.Groups;

import me.thelionmc.minecraftplugin.Abilities.Wizard.*;

public class Wizard extends AbilityGroup {
    @Override
    protected void defineAbilities() {
        addAbility(new WizardAbility1());
        addAbility(new WizardAbility2());
        addAbility(new WizardAbility3());
    }
}
