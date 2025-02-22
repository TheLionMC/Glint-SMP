package me.thelionmc.minecraftplugin.Groups;

import me.thelionmc.minecraftplugin.Abilities.Mischief.*;
import me.thelionmc.minecraftplugin.GlintSMP;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;

public class Mischief extends AbilityGroup {
    Plugin plugin;
    GlintSMP main;
    public Mischief(Plugin plugin, GlintSMP main) {
        this.plugin = plugin;
        this.main = main;

        this.abilities = new ArrayList<>();
        defineAbilities();
    }

    @Override
    protected void defineAbilities() {
        addAbility(new MischiefAbility1());
        addAbility(new MischiefAbility2());
        addAbility(new MischiefAbility3(main, plugin));
    }

    public String displayName() {return "Mischief";}
}
