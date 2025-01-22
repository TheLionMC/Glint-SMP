package me.thelionmc.minecraftplugin.Groups;

import me.thelionmc.minecraftplugin.Abilities.Ability;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public abstract class AbilityGroup {
    private final List<Ability> abilities = new ArrayList<>();

    public AbilityGroup() {
        defineAbilities();
    }

    protected abstract void defineAbilities();

    public void useAbility(int x, Player player) {
        if(x < 0 || x >= abilities.size()) {
            player.sendMessage("Invalid ability number!"); //this shouldnt show
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
