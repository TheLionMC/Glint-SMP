package me.thelionmc.minecraftplugin;

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
import me.thelionmc.minecraftplugin.Commands.*;
import me.thelionmc.minecraftplugin.customItems.CleansingBow;
import me.thelionmc.minecraftplugin.Commands.openstaffmenucommand;
import me.thelionmc.minecraftplugin.customItems.*;

public class GlintSMP extends JavaPlugin implements Listener {
    private FileConfiguration staffOptions;
    public static Map<UUID, Integer> shardCounts = new HashMap<>();
    private FileConfiguration playerData;
    private ShardManager shardLogic;
    private CleansingBow cleansingBow;
    private BrisknessAxe brisknessAxe;
    private FreezingTrident freezingTrident;
    private openstaffmenucommand openstaffMenu;
    private isLegendary isLegendaryClass;
    private KitRules kitRules;
    private ReviveEvent reviveEvent;
    private SetPhase setPhase;
    private SummonBoss summonBoss;
    private BanList<?> bannedPlayers;
    private invseecommand invseecommand;
    public static ItemStack InvisShards;
    private Dragondeathevent dragondeathevent;
    private echestseecommand echestseecommand;
    private ActionBarManager abilityManager;
    private ClassManager classManager;
    private ActionBarManager actionBarManager;
    private abilityKeybinds keys;

    public void onEnable() {
        saveDefaultConfig();

        playerData = YamlConfiguration.loadConfiguration(new File(getDataFolder(), "players.yml"));

        this.bannedPlayers = Bukkit.getBanList(BanList.Type.NAME);
        initInvisShardItem();

        cleansingBow = new CleansingBow(this);
        brisknessAxe = new BrisknessAxe(this);
        freezingTrident = new FreezingTrident(this);
        isLegendaryClass = new isLegendary(cleansingBow, brisknessAxe, freezingTrident);
        setPhase = new SetPhase(this);
        reviveEvent = new ReviveEvent(this, this);
        summonBoss = new SummonBoss(brisknessAxe, this, this);
        kitRules = new KitRules(this, this);
        invseecommand = new invseecommand(this, this);
        echestseecommand = new echestseecommand(this);
        dragondeathevent = new Dragondeathevent(this);
        shardLogic = new ShardManager(this, this);
        openstaffMenu = new openstaffmenucommand(this, shardLogic);
       
        classManager = new ClassManager(this,
                this);
        abilityManager = new ActionBarManager(this, classManager, this);
        keys = new abilityKeybinds(abilityManager);

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
        getServer().getPluginManager().registerEvents(new toolcommands(), this);


        getCommand("giveinvisiblearmor").setExecutor(this);
        getCommand("openstaffmenu").setExecutor(openstaffMenu);
        getCommand("giveportalobsidian").setExecutor(new givePortalObsidian());
        getCommand("revive").setExecutor(new revivecommand());
        getCommand("shardcount").setExecutor(shardLogic);
        getCommand("withdraw").setExecutor(shardLogic);
        getCommand("setpvp").setExecutor(new pvpcommands());
        getCommand("start").setExecutor(new startcommand());
        getCommand("switchability").setExecutor(keys);
        getCommand("useability").setExecutor(keys);
        getCommand("setphase").setExecutor(setPhase);
        getCommand("getphase").setExecutor(setPhase);
        getCommand("summonboss").setExecutor(summonBoss);
        getCommand("whitelistall").setExecutor(new preparecommand());
        getCommand("invsee").setExecutor(invseecommand);
        getCommand("setshards").setExecutor(shardLogic);
        getCommand("echest").setExecutor(echestseecommand);
        getCommand("gettool").setExecutor(new toolcommands());



        getCommand("gettool").setTabCompleter(new toolcommands());
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
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();

            if (player.isBlocking() && player.getInventory().getItemInOffHand().getType() == Material.SHIELD) {
                ItemStack damagerItem = null;

                if (event.getDamager() instanceof Player) {
                    damagerItem = ((Player) event.getDamager()).getInventory().getItemInMainHand();
                } else if (event.getDamager() instanceof org.bukkit.entity.Entity) {
                    return;
                }

                if (isAxe(damagerItem.getType())) {
                    player.playSound(player.getLocation(), Sound.ENTITY_ITEM_BREAK, 1.0f, 1.0f);
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