package me.thelionmc.minecraftplugin.Groups;

import me.thelionmc.minecraftplugin.Abilities.Medic.MedicAbility1;
import me.thelionmc.minecraftplugin.Abilities.Medic.MedicAbility2;
import me.thelionmc.minecraftplugin.Abilities.Medic.MedicAbility3;
import me.thelionmc.minecraftplugin.GlintSMP;
import org.bukkit.plugin.Plugin;

public class Medic extends AbilityGroup {
    private final GlintSMP mainClass;
    private final Plugin plugin;

    public Medic(Plugin plugin, GlintSMP mainClass) {
        this.mainClass = mainClass;
        this.plugin = plugin;

        defineAbilities();
    }

    @Override
    protected void defineAbilities() {
        addAbility(new MedicAbility1());
        if(plugin == null) throw new IllegalArgumentException();
        addAbility(new MedicAbility2(plugin, mainClass));
        addAbility(new MedicAbility3());
    }

    public String displayName() {return "Medic";}
}
