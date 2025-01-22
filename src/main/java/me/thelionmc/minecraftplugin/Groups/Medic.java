package me.thelionmc.minecraftplugin.Groups;

import me.thelionmc.minecraftplugin.Abilities.Medic.*;

public class Medic extends AbilityGroup {
    @Override
    protected void defineAbilities() {
        addAbility(new MedicAbility1());
        addAbility(new MedicAbility2());
        addAbility(new MedicAbility3());
    }
}
