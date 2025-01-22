package me.thelionmc.minecraftplugin.Groups;

import me.thelionmc.minecraftplugin.Abilities.Explorer.*;

public class Explorer extends AbilityGroup {
    @Override
    protected void defineAbilities() {
        addAbility(new ExplorerAbility1());
        addAbility(new ExplorerAbility2());
        addAbility(new ExplorerAbility3());
    }
}
