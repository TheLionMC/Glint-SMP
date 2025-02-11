package me.thelionmc.minecraftplugin.Groups;

import me.thelionmc.minecraftplugin.Abilities.Medic.MedicAbility1;
import me.thelionmc.minecraftplugin.Abilities.Medic.MedicAbility2;
import me.thelionmc.minecraftplugin.Abilities.Medic.MedicAbility3;
import me.thelionmc.minecraftplugin.GlintSMP;
import org.bukkit.plugin.Plugin;

public class Medic extends AbilityGroup {
    private final GlintSMP mainclass;
    private final Plugin plugin;

    public Medic(Plugin plugin, GlintSMP mainClass) {
        this.mainclass = mainClass;
        this.plugin = plugin;
    }

    @Override
    protected void defineAbilities() {
        addAbility(new MedicAbility1());
        addAbility(new MedicAbility2(plugin, mainclass));
        addAbility(new MedicAbility3());
    }
}
