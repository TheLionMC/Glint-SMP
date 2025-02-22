package me.thelionmc.minecraftplugin.Groups;

import me.thelionmc.minecraftplugin.Abilities.Ability;
import me.thelionmc.minecraftplugin.Abilities.AllClasses.AllClassesAbility1;
import org.jetbrains.annotations.NotNull;
import org.bukkit.ChatColor;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.Player;
import org.checkerframework.checker.builder.qual.NotCalledMethods;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class AbilityGroup implements ConfigurationSerializable {
    List<Ability> abilities = new ArrayList<>();

    public AbilityGroup() {
        //abilities.add(new AllClassesAbility1()); // ➕ Add here any ability that every player will have
    }

    protected abstract void defineAbilities();

    public void useAbility(int x, Player player) {
        if(x < 0 || x >= abilities.size()) {
            player.sendMessage(ChatColor.BLUE + "[GlintSMP] "+ ChatColor.RED + "Invalid ability number! Report to an admin+ IMMEDIATELY!"); // ❌ If this shows something is wrong
            return;
        }
        abilities.get(x).useAbility(player);
    }

    @Override
    public @NotNull Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();
        map.put("abilities", abilities);
        return map;
    }

    public List<Ability> getAbilities() {
        return abilities;
    }

    protected void addAbility(Ability ability) {
        abilities.add(ability);
    }

    public abstract String displayName();
}
