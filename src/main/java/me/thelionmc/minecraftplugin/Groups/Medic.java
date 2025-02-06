package me.thelionmc.minecraftplugin.Groups;

import me.thelionmc.minecraftplugin.Abilities.Medic.MedicAbility1;
import me.thelionmc.minecraftplugin.Abilities.Medic.MedicAbility2;
import me.thelionmc.minecraftplugin.Abilities.Medic.MedicAbility3;
import me.thelionmc.minecraftplugin.GlintSMP;

public class Medic extends AbilityGroup {
    private final GlintSMP mainclass;

    public Medic(GlintSMP mainClass) {
        this.mainclass = mainClass;
    }

    @Override
    protected void defineAbilities() {
        addAbility(new MedicAbility1());
        addAbility(new MedicAbility2(mainclass));
        addAbility(new MedicAbility3());
    }
}
