package me.thelionmc.minecraftplugin;

import me.thelionmc.minecraftplugin.Groups.AbilityGroup;
import me.thelionmc.minecraftplugin.OperatorCommands.*;
import me.thelionmc.minecraftplugin.PlayerCommands.AbilityKeybinds;
import me.thelionmc.minecraftplugin.PlayerCommands.Trust;
import me.thelionmc.minecraftplugin.Tools.CoreProtectTool;
import me.thelionmc.minecraftplugin.customItems.Shard;
import me.thelionmc.minecraftplugin.Events.Dragondeathevent;
import me.thelionmc.minecraftplugin.Events.ReviveEvent;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.checkerframework.checker.nullness.qual.NonNull;


import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class GlintSMP extends JavaPlugin implements Listener {
    public static Map<UUID, Integer> shardCounts = new HashMap<>();
    ShardManager shardManager;
    StaffMenu staffMenu;
    KitRules kitRules;
    ReviveEvent reviveEvent;
    SummonBoss summonBoss;
    Invsee invseecommand;
    Dragondeathevent dragondeathevent;
    EchestSee echestseecommand;
    ActionBarManager abilityManager;
    ClassManager classManager;
    ClassCommands classCommands;
    AbilityKeybinds keys;
    TrustManager trustManager;
    Trust trust;
    DisableClass disableClass;
    EnableClass enableClass;
    EnableAbility enableAbility;
    DisableAbility disableAbility;

    SetCooldown resetcooldown;

    public static Shard shard;

    public void onEnable() {
        shard = new Shard();
        shard.initialize(this);

        if (shardManager == null) {
            shardManager = new ShardManager(this, this);
        }

        reviveEvent = new ReviveEvent(this, this);
        summonBoss = new SummonBoss(this, this);
        kitRules = new KitRules(this, this);
        invseecommand = new Invsee(this, this);
        echestseecommand = new EchestSee(this);
        dragondeathevent = new Dragondeathevent(this);
        shardManager = new ShardManager(this, this);
        staffMenu = new StaffMenu(this, shardManager);
        classManager = new ClassManager(this, this);
        abilityManager = new ActionBarManager(this, classManager, this);
        keys = new AbilityKeybinds(abilityManager);
        classCommands = new ClassCommands(classManager);
        resetcooldown = new SetCooldown(classManager);
        trustManager = new TrustManager(this);
        trust = new Trust(trustManager);
        disableClass = new DisableClass(classManager);
        enableClass = new EnableClass(classManager);
        enableAbility = new EnableAbility(classManager);
        disableAbility = new DisableAbility(classManager);

        getServer().getPluginManager().registerEvents(this, this);
        getServer().getPluginManager().registerEvents(staffMenu, this);
        getServer().getPluginManager().registerEvents(kitRules, this);
        getServer().getPluginManager().registerEvents(summonBoss, this);
        getServer().getPluginManager().registerEvents(invseecommand, this);
        getServer().getPluginManager().registerEvents(reviveEvent, this);
        getServer().getPluginManager().registerEvents(dragondeathevent, this);
        getServer().getPluginManager().registerEvents(shardManager, this);
        getServer().getPluginManager().registerEvents(classManager, this);
        getServer().getPluginManager().registerEvents(abilityManager, this);
        getServer().getPluginManager().registerEvents(new CoreProtectTool(this), this);
        getServer().getPluginManager().registerEvents(new Tools(), this);

        getCommand("openstaffmenu").setExecutor(staffMenu);
        getCommand("giveportalobsidian").setExecutor(new GivePortalObsidian());
        getCommand("shardcount").setExecutor(shardManager);
        getCommand("withdraw").setExecutor(shardManager);
        getCommand("setpvp").setExecutor(new PVP());
        getCommand("start").setExecutor(new Start());
        getCommand("switchability").setExecutor(keys);
        getCommand("useability").setExecutor(keys);
        getCommand("summonboss").setExecutor(summonBoss);
        getCommand("whitelistall").setExecutor(new WhitelistAll());
        getCommand("invsee").setExecutor(invseecommand);
        getCommand("setshards").setExecutor(shardManager);
        getCommand("echest").setExecutor(echestseecommand);
        getCommand("gettool").setExecutor(new Tools());
        getCommand("setclass").setExecutor(classCommands);
        getCommand("setcooldown").setExecutor(new SetCooldown(classManager));
        getCommand("trust").setExecutor(trust);
        getCommand("distrust").setExecutor(trust);
        getCommand("enableclass").setExecutor(enableClass);
        getCommand("disableclass").setExecutor(disableClass);
        getCommand("enabledClasses").setExecutor(new EnabledClasses(classManager));
        getCommand("enableability").setExecutor(enableAbility);
        getCommand("disableability").setExecutor(disableAbility);

        getCommand("gettool").setTabCompleter(new Tools());
        getCommand("summonboss").setTabCompleter(summonBoss);
        getCommand("setclass").setTabCompleter(classCommands);
        getCommand("withdraw").setTabCompleter(new ShardManager(this, this));
        getCommand("enableclass").setTabCompleter(enableClass);
        getCommand("disableclass").setTabCompleter(disableClass);

        //In case of a /reload
        for(Player player : Bukkit.getOnlinePlayers()) {
            if(!classManager.playerHasGroup(player.getUniqueId())) {
                classManager.setPlayerGroup(player.getUniqueId(), classManager.randomEnabledGroup());
            }
        }

        getLogger().info("GlintSMP is ready to go!!!");
    }

    public void resetAttributes(@NonNull Player player) {
        AttributeInstance movementSpeed = player.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED);
        AttributeInstance gravity = player.getAttribute(Attribute.GENERIC_GRAVITY);
        AttributeInstance fallDamage = player.getAttribute(Attribute.GENERIC_FALL_DAMAGE_MULTIPLIER);

        movementSpeed.setBaseValue(0.10000000149011612);
        gravity.setBaseValue(0.08);
        fallDamage.setBaseValue(1);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        resetAttributes(player);
    }
    @EventHandler
    public void onEntityCombust(EntityCombustEvent e) {
        if (e.getEntity() instanceof Item) {
            ItemStack item = ((Item) e.getEntity()).getItemStack();
            if (item.equals(shard)) {
                e.setCancelled(true);
            }
        }
    }
    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player && event.getDamager() instanceof Player) {
            Player player = (Player)event.getEntity();
            Player damager = (Player)event.getDamager();
            if(damager.getEquipment().getItemInMainHand() != null) {
                if (damager.getEquipment().getItemInMainHand().getItemMeta().getItemName().toLowerCase().contains("axe") && player.isBlocking() && (int)event.getFinalDamage() == 0) {
                    damager.playSound(damager.getLocation(), Sound.ENTITY_ITEM_BREAK, 5.0F, 1.0F);
                }
            }

        }
    }

    public void scalePlayer(Player player, double scale) {
        if(Objects.requireNonNull(player.getAttribute(Attribute.GENERIC_SCALE)).getValue() == scale) {
            return;
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                AttributeInstance attribute = player.getAttribute(Attribute.GENERIC_SCALE);
                double val = attribute.getBaseValue();
                if(val > scale) {
                    attribute.setBaseValue(val + 0.01);
                }
                if(val < scale) {
                    attribute.setBaseValue(val - 0.01);
                }

                if(val == scale) {
                    cancel();
                }
            }
        }.runTaskTimer(this, 0, 1);
    }

    @Override
    public void onDisable() {
        getConfig().set("Shard", shard);
        saveConfig();
        getLogger().info("GlintSMP shut down.");
        shardManager.saveShardData();
        trustManager.saveTrustData();
        classManager.saveGroupData();
        classManager.saveEnabledClassData();
    }
}