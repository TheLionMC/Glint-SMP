package me.thelionmc.minecraftplugin;

import me.thelionmc.minecraftplugin.Abilities.AllClasses.AllClassesAbility1;
import me.thelionmc.minecraftplugin.Abilities.Angel.AngelAbility1;
import me.thelionmc.minecraftplugin.Abilities.Angel.AngelAbility2;
import me.thelionmc.minecraftplugin.Abilities.Angel.AngelAbility3;
import me.thelionmc.minecraftplugin.Abilities.Aqua.AquaAbility1;
import me.thelionmc.minecraftplugin.Abilities.Aqua.AquaAbility2;
import me.thelionmc.minecraftplugin.Abilities.Aqua.AquaAbility3;
import me.thelionmc.minecraftplugin.Abilities.Assassin.AssassinAbility1;
import me.thelionmc.minecraftplugin.Abilities.Assassin.AssassinAbility2;
import me.thelionmc.minecraftplugin.Abilities.Assassin.AssassinAbility3;
import me.thelionmc.minecraftplugin.Abilities.Escapist.EscapistAbility1;
import me.thelionmc.minecraftplugin.Abilities.Escapist.EscapistAbility2;
import me.thelionmc.minecraftplugin.Abilities.Escapist.EscapistAbility3;
import me.thelionmc.minecraftplugin.Abilities.Farmer.FarmerAbility1;
import me.thelionmc.minecraftplugin.Abilities.Farmer.FarmerAbility2;
import me.thelionmc.minecraftplugin.Abilities.Farmer.FarmerAbility3;
import me.thelionmc.minecraftplugin.Abilities.Medic.MedicAbility1;
import me.thelionmc.minecraftplugin.Abilities.Medic.MedicAbility2;
import me.thelionmc.minecraftplugin.Abilities.Medic.MedicAbility3;
import me.thelionmc.minecraftplugin.Abilities.Mischief.MischiefAbility1;
import me.thelionmc.minecraftplugin.Abilities.Mischief.MischiefAbility2;
import me.thelionmc.minecraftplugin.Abilities.Mischief.MischiefAbility3;
import me.thelionmc.minecraftplugin.Abilities.Ninja.NinjaAbility1;
import me.thelionmc.minecraftplugin.Abilities.Ninja.NinjaAbility2;
import me.thelionmc.minecraftplugin.Abilities.Ninja.NinjaAbility3;
import me.thelionmc.minecraftplugin.Abilities.Warrior.WarriorAbility1;
import me.thelionmc.minecraftplugin.Abilities.Warrior.WarriorAbility2;
import me.thelionmc.minecraftplugin.Abilities.Warrior.WarriorAbility3;
import me.thelionmc.minecraftplugin.OperatorCommands.*;
import me.thelionmc.minecraftplugin.Tools.CoreProtectTool;
import me.thelionmc.minecraftplugin.events.Dragondeathevent;
import me.thelionmc.minecraftplugin.events.ReviveEvent;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.*;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.event.*;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.*;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.*;
import org.bukkit.plugin.java.*;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import java.io.File;
import java.util.*;
import me.thelionmc.minecraftplugin.PlayerCommands.*;
import me.thelionmc.minecraftplugin.customItems.CleansingBow;
import me.thelionmc.minecraftplugin.OperatorCommands.StaffMenu;
import me.thelionmc.minecraftplugin.customItems.*;

public class GlintSMP extends JavaPlugin implements Listener {
    private FileConfiguration staffOptions;
    public static Map<UUID, Integer> shardCounts = new HashMap<>();
    private FileConfiguration playerData;
    private ShardManager shardLogic;
    private CleansingBow cleansingBow;
    private BrisknessAxe brisknessAxe;
    private FreezingTrident freezingTrident;
    private StaffMenu openstaffMenu;
    private isLegendary isLegendaryClass;
    private KitRules kitRules;
    private ReviveEvent reviveEvent;
    private SetPhase setPhase;
    private SummonBoss summonBoss;
    private BanList<?> bannedPlayers;
    private Invsee invseecommand;
    public static ItemStack InvisShards;
    private Dragondeathevent dragondeathevent;
    private EchestSee echestseecommand;
    private ActionBarManager abilityManager;
    private ClassManager classManager;
    private ActionBarManager actionBarManager;
    private AbilityKeybinds keys;

    public void onEnable() {
        saveDefaultConfig();

        playerData = YamlConfiguration.loadConfiguration(new File(getDataFolder(), "players.yml"));

        this.bannedPlayers = Bukkit.getBanList(BanList.Type.NAME);
        initInvisShardItem();

        if (shardLogic == null) {
            shardLogic = new ShardManager(this, this);
        }

        loadClass(() -> cleansingBow = new CleansingBow(this), "CleansingBow");
        loadClass(() -> brisknessAxe = new BrisknessAxe(this), "BrisknessAxe");
        loadClass(() -> freezingTrident = new FreezingTrident(this), "FreezingTrident");
        loadClass(() -> isLegendaryClass = new isLegendary(cleansingBow, brisknessAxe, freezingTrident), "isLegendary");
        loadClass(() -> setPhase = new SetPhase(this), "SetPhase");
        loadClass(() -> reviveEvent = new ReviveEvent(this, this), "ReviveEvent");
        loadClass(() -> summonBoss = new SummonBoss(brisknessAxe, this, this), "SummonBoss");
        loadClass(() -> kitRules = new KitRules(this, this), "KitRules");
        loadClass(() -> invseecommand = new Invsee(this, this), "Invsee");
        loadClass(() -> echestseecommand = new EchestSee(this), "EchestSee");
        loadClass(() -> dragondeathevent = new Dragondeathevent(this), "Dragondeathevent");
        loadClass(() -> shardLogic = new ShardManager(this, this), "ShardManager");
        loadClass(() -> openstaffMenu = new StaffMenu(this, shardLogic), "StaffMenu");
        loadClass(() -> classManager = new ClassManager(this, this), "ClassManager");
        loadClass(() -> abilityManager = new ActionBarManager(this, classManager, this), "ActionBarManager");
        loadClass(() -> keys = new AbilityKeybinds(abilityManager), "AbilityKeybinds");

        loadClass(() -> new AllClassesAbility1(), "AllClassesAbility1");

        loadClass(() -> new AngelAbility1(), "AngelAbility1");
        loadClass(() -> new AngelAbility2(), "AngelAbility2");
        loadClass(() -> new AngelAbility3(), "AngelAbility3");

        loadClass(() -> new AquaAbility1(), "AquaAbility1");
        loadClass(() -> new AquaAbility2(), "AquaAbility2");
        loadClass(() -> new AquaAbility3(), "AquaAbility3");

        loadClass(() -> new AssassinAbility1(), "AssassinAbility1");
        loadClass(() -> new AssassinAbility2(), "AssassinAbility2");
        loadClass(() -> new AssassinAbility3(), "AssassinAbility3");

        loadClass(() -> new EscapistAbility1(), "EscapistAbility1");
        loadClass(() -> new EscapistAbility2(), "EscapistAbility2");
        loadClass(() -> new EscapistAbility3(), "EscapistAbility3");

        loadClass(() -> new FarmerAbility1(), "FarmerAbility1");
        loadClass(() -> new FarmerAbility2(), "FarmerAbility2");
        loadClass(() -> new FarmerAbility3(), "FarmerAbility3");

        loadClass(() -> new MedicAbility1(), "MedicAbility1");
        loadClass(() -> new MedicAbility2(this), "MedicAbility2");
        loadClass(() -> new MedicAbility3(), "MedicAbility3");

        loadClass(() -> new MischiefAbility1(), "MischiefAbility1");
        loadClass(() -> new MischiefAbility2(), "MischiefAbility2");
        loadClass(() -> new MischiefAbility3(), "MischiefAbility3");

        loadClass(() -> new NinjaAbility1(), "NinjaAbility1");
        loadClass(() -> new NinjaAbility2(), "NinjaAbility2");
        loadClass(() -> new NinjaAbility3(), "NinjaAbility3");

        loadClass(() -> new WarriorAbility1(), "WarriorAbility1");
        loadClass(() -> new WarriorAbility2(), "WarriorAbility2");
        loadClass(() -> new WarriorAbility3(), "WarriorAbility3");

        setPhase = new SetPhase(this);
        reviveEvent = new ReviveEvent(this, this);
        summonBoss = new SummonBoss(brisknessAxe, this, this);
        kitRules = new KitRules(this, this);
        invseecommand = new Invsee(this, this);
        echestseecommand = new EchestSee(this);
        dragondeathevent = new Dragondeathevent(this);
        shardLogic = new ShardManager(this, this);
        openstaffMenu = new StaffMenu(this, shardLogic);
        ClassManager classManager = new ClassManager(this, this);
        abilityManager = new ActionBarManager(this, classManager, this);
        keys = new AbilityKeybinds(abilityManager);

        getServer().getPluginManager().registerEvents(this, this);
        getServer().getPluginManager().registerEvents(cleansingBow, this);
        getServer().getPluginManager().registerEvents(brisknessAxe, this);
        getServer().getPluginManager().registerEvents(freezingTrident, this);
        getServer().getPluginManager().registerEvents(openstaffMenu, this);
        getServer().getPluginManager().registerEvents(kitRules, this);
        getServer().getPluginManager().registerEvents(summonBoss, this);
        getServer().getPluginManager().registerEvents(invseecommand, this);
        getServer().getPluginManager().registerEvents(reviveEvent, this);
        getServer().getPluginManager().registerEvents(dragondeathevent, this);
        getServer().getPluginManager().registerEvents(shardLogic, this);
        getServer().getPluginManager().registerEvents(classManager, this);
        getServer().getPluginManager().registerEvents(new CoreProtectTool(this), this);
        getServer().getPluginManager().registerEvents(new Tools(), this);



        getCommand("giveinvisiblearmor").setExecutor(this);
        getCommand("openstaffmenu").setExecutor(openstaffMenu);
        getCommand("giveportalobsidian").setExecutor(new GivePortalObsidian());
        getCommand("shardcount").setExecutor(shardLogic);
        getCommand("withdraw").setExecutor(shardLogic);
        getCommand("setpvp").setExecutor(new PVP());
        getCommand("start").setExecutor(new Start());
        getCommand("switchability").setExecutor(keys);
        getCommand("useability").setExecutor(keys);
        getCommand("setphase").setExecutor(setPhase);
        getCommand("getphase").setExecutor(setPhase);
        getCommand("summonboss").setExecutor(summonBoss);
        getCommand("whitelistall").setExecutor(new WhitelistAll());
        getCommand("invsee").setExecutor(invseecommand);
        getCommand("setshards").setExecutor(shardLogic);
        getCommand("echest").setExecutor(echestseecommand);
        getCommand("gettool").setExecutor(new Tools());



        getCommand("gettool").setTabCompleter(new Tools());
        getCommand("summonboss").setTabCompleter(summonBoss);
        getCommand("withdraw").setTabCompleter(new ShardManager(this, this));


        getLogger().info("GlintSMP is ready to go!!!");
    }

    @EventHandler
    public void onEntityCombust(EntityCombustEvent e) {
        if (e.getEntity() instanceof Item) {
            ItemStack item = ((Item) e.getEntity()).getItemStack();
            if (item.equals(InvisShards)) {
                e.setCancelled(true);
            }
        }
    }
    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player && event.getDamager() instanceof Player) {
            Player player = (Player)event.getEntity();
            Player damager = (Player)event.getDamager();
            if (damager.getEquipment().getItemInMainHand().getType().toString().toLowerCase().contains("axe") && player.isBlocking() && (int)event.getFinalDamage() == 0) {
                damager.playSound(damager.getLocation(), Sound.ITEM_SHIELD_BREAK, 5.0F, 5.0F);
            }
        }
    }
    private void loadClass(Runnable init, String className) {
        try {
            init.run();
        } catch (Exception e) {
            StackTraceElement[] stackTrace = e.getStackTrace();
            String errorMessage = "Failed to load " + className + ": " + e.getMessage();

            if (stackTrace.length > 0) {
                errorMessage += " at line " + stackTrace[0].getLineNumber();
            }

            getLogger().severe(errorMessage);
            e.printStackTrace();

            for (Player player : Bukkit.getOnlinePlayers()) {
                if (player.isOp()) {
                    player.sendMessage(ChatColor.BLUE + "[GlintSMP] " + ChatColor.RED + errorMessage);
                }
            }
        }
    }

    private boolean isAxe(Material material) {
        switch (material) {
            case WOODEN_AXE:
            case STONE_AXE:
            case IRON_AXE:
            case GOLDEN_AXE:
            case DIAMOND_AXE:
            case NETHERITE_AXE:
                return true;
            default:
                return false;
        }
    }

    @Override
    public void onDisable() {
        getConfig().set("Shard", InvisShards);
        saveConfig();
        getLogger().info("GlintSMP shut down.");
        shardLogic.saveShardData();
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        AttributeInstance attributeInstance = player.getAttribute(Attribute.GENERIC_ATTACK_SPEED);
        if(attributeInstance != null) {
            attributeInstance.setBaseValue(4);
        }
    }

    public void initInvisShardItem() {
        ItemStack item = new ItemStack(Material.NETHER_STAR, 1);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.RED + "Shard");
        meta.addEnchant(Enchantment.DURABILITY, 1, false);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        item.setItemMeta(meta);
        InvisShards = item;

        ShapedRecipe shapedRecipe = new ShapedRecipe(new NamespacedKey(this, "Shard"), item);
        shapedRecipe.shape("DND", "NSN", "DND");
        shapedRecipe.setIngredient('D', new RecipeChoice.MaterialChoice(Material.DIAMOND_BLOCK));
        shapedRecipe.setIngredient('S', new RecipeChoice.MaterialChoice(Material.NETHER_STAR));
        shapedRecipe.setIngredient('N', new RecipeChoice.MaterialChoice(Material.NETHERITE_INGOT));
        Bukkit.getServer().addRecipe(shapedRecipe);
    }

    public static ItemStack getInvisShardItem() {
        return InvisShards;
    }
}