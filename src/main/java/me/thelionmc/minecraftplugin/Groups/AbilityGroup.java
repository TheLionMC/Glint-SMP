package me.thelionmc.minecraftplugin.Groups;

import me.thelionmc.minecraftplugin.Abilities.Ability;
import me.thelionmc.minecraftplugin.Abilities.AllClasses.AllClassesAbility1;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public abstract class AbilityGroup {
    private final List<Ability> abilities = new ArrayList<>();

    public AbilityGroup() {
        //abilities.add(new AllClassesAbility1()); // ➕ Add here any ability that every player will have
        defineAbilities();
    }

    protected abstract void defineAbilities();

    public void useAbility(int x, Player player) {
        if(x < 0 || x >= abilities.size()) {
            player.sendMessage(ChatColor.BLUE + "[GlintSMP] "+ ChatColor.RED + "Invalid ability number! Report to an admin+ IMMEDIATELY!"); // ❌ If this shows something is wrong
            return;
        }
        abilities.get(x).useAbility(player);
    }

    public List<Ability> getAbilities() {
        return abilities;
    }

    protected void addAbility(Ability ability) {
        abilities.add(ability);
    }
}
