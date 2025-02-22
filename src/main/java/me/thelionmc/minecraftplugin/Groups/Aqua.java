package me.thelionmc.minecraftplugin.Groups;

import me.thelionmc.minecraftplugin.Abilities.Angel.AngelAbility1;
import me.thelionmc.minecraftplugin.Abilities.Angel.AngelAbility2;
import me.thelionmc.minecraftplugin.Abilities.Angel.AngelAbility3;
import me.thelionmc.minecraftplugin.Abilities.Aqua.AquaAbility1;
import me.thelionmc.minecraftplugin.Abilities.Aqua.AquaAbility2;
import me.thelionmc.minecraftplugin.Abilities.Aqua.AquaAbility3;

import java.util.ArrayList;

public class Aqua extends AbilityGroup {
    public Aqua() {
        this.abilities = new ArrayList<>();
        defineAbilities();
    }
    @Override
    protected void defineAbilities() {
        addAbility(new AquaAbility1());
        addAbility(new AquaAbility2());
        addAbility(new AquaAbility3());
    }

    public String displayName() {return "Aqua";}
}
