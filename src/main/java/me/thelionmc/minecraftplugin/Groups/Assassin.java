package me.thelionmc.minecraftplugin.Groups;

import me.thelionmc.minecraftplugin.Abilities.Assassin.*;
import me.thelionmc.minecraftplugin.GlintSMP;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;

public class Assassin extends AbilityGroup {
    Plugin plugin;
    GlintSMP mainClass;
    public Assassin(Plugin plugin, GlintSMP main) {
        this.abilities = new ArrayList<>();
        this.plugin = plugin;
        this.mainClass = main;
        defineAbilities();
    }
    protected void defineAbilities() {
        addAbility(new AssassinAbility1());
        addAbility(new AssassinAbility2(plugin, mainClass));
        addAbility(new AssassinAbility3());
    }

    public String displayName() {return "Assassin";}
}
