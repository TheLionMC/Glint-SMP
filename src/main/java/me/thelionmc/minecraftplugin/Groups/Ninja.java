package me.thelionmc.minecraftplugin.Groups;

import me.thelionmc.minecraftplugin.Abilities.Ninja.*;
import me.thelionmc.minecraftplugin.ShardManager;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;

public class Ninja extends AbilityGroup {
    ShardManager shardManager;
    Plugin plugin;
    public Ninja(Plugin plugin, ShardManager shardManager) {
        this.plugin = plugin;
        this.shardManager = shardManager;

        this.abilities = new ArrayList<>();
        defineAbilities();
    }
    @Override
    protected void defineAbilities() {
        addAbility(new NinjaAbility1(shardManager, plugin));
        addAbility(new NinjaAbility2());
        addAbility(new NinjaAbility3());
    }

    public String displayName() {return "Ninja";}
}
