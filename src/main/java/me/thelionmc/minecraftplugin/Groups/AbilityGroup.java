package me.thelionmc.minecraftplugin.Groups;

import me.thelionmc.minecraftplugin.Abilities.Ability;
import me.thelionmc.minecraftplugin.Abilities.AllClasses.AllClassesAbility1;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public abstract class AbilityGroup {
    private final List<Ability> abilities = new ArrayList<>();

    public AbilityGroup() {
        abilities.add(new AllClassesAbility1()); //ability that everyone has
        defineAbilities();
    }

    protected abstract void defineAbilities();

    public void useAbility(int x, Player player) {
        if(x < 0 || x >= abilities.size()) {
            player.sendMessage("Invalid ability number!"); //this shouldn't show
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
