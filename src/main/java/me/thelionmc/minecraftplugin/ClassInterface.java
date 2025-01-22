package me.thelionmc.minecraftplugin;

import java.util.UUID;
import org.bukkit.entity.Player;

public interface ClassInterface {
    boolean isOnCooldownAbility1(UUID var1);

    Long cooldownRemainingAbility1(UUID var1);

    void useAbility1(Player var1);

    boolean isOnCooldownAbility2(UUID var1);

    Long cooldownRemainingAbility2(UUID var1);

    void useAbility2(Player var1);

    boolean[] cools(UUID var1);

    Long[] coolsRemaining(UUID var1);
}
    