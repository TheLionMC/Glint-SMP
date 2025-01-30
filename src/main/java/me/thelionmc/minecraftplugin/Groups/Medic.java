package me.thelionmc.minecraftplugin.Groups;

import me.thelionmc.minecraftplugin.Abilities.Medic.*;
import me.thelionmc.minecraftplugin.GlintSMP;

public class Medic extends AbilityGroup {
    @Override
    protected void defineAbilities() {
        addAbility(new MedicAbility1());
        addAbility(new MedicAbility2(new GlintSMP(), new GlintSMP()));
        addAbility(new MedicAbility3());
    }
}
