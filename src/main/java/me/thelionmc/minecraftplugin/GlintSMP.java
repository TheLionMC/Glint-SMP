package me.thelionmc.minecraftplugin;

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
    private SummonBoss summonBoss;
    private BanList<?> bannedPlayers;
    private Invsee invseecommand;
    public static ItemStack shards;
    private Dragondeathevent dragondeathevent;
    private EchestSee echestseecommand;
    private ActionBarManager abilityManager;
    private ClassManager classManager;
    private ActionBarManager actionBarManager;
    private AbilityKeybinds keys;
    private ClassCommands classCommands;

    public void onEnable() {
        shard = new Shard();
        shard.initialize(this);

        if (shardLogic == null) {
            shardLogic = new ShardManager(this, this);
        }

        reviveEvent = new ReviveEvent(this, this);
        summonBoss = new SummonBoss(this, this);
        kitRules = new KitRules(this, this);
        invseecommand = new Invsee(this, this);
        echestseecommand = new EchestSee(this);
        dragondeathevent = new Dragondeathevent(this);
        shardLogic = new ShardManager(this, this);
        openstaffMenu = new StaffMenu(this, shardLogic);
        classManager = new ClassManager(this, this);
        abilityManager = new ActionBarManager(this, classManager, this);
        keys = new AbilityKeybinds(abilityManager);
        classCommands = new ClassCommands(classManager);
        resetcooldown = new SetCooldown(classManager);
        trustManager = new TrustManager(this);

        getServer().getPluginManager().registerEvents(this, this);
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

        getCommand("openstaffmenu").setExecutor(openstaffMenu);
        getCommand("giveportalobsidian").setExecutor(new GivePortalObsidian());
        getCommand("shardcount").setExecutor(shardLogic);
        getCommand("withdraw").setExecutor(shardLogic);
        getCommand("setpvp").setExecutor(new PVP());
        getCommand("start").setExecutor(new Start());
        getCommand("switchability").setExecutor(keys);
        getCommand("useability").setExecutor(keys);
        getCommand("summonboss").setExecutor(summonBoss);
        getCommand("whitelistall").setExecutor(new WhitelistAll());
        getCommand("invsee").setExecutor(invseecommand);
        getCommand("setshards").setExecutor(shardLogic);
        getCommand("echest").setExecutor(echestseecommand);
        getCommand("gettool").setExecutor(new Tools());
        getCommand("setclass").setExecutor(classCommands);

        getCommand("gettool").setTabCompleter(new Tools());
        getCommand("summonboss").setTabCompleter(summonBoss);
        getCommand("setclass").setTabCompleter(classCommands);
        getCommand("withdraw").setTabCompleter(new ShardManager(this, this));


        getLogger().info("GlintSMP is ready to go!!!");
    }

    @EventHandler
    public void onEntityCombust(EntityCombustEvent e) {
        if (e.getEntity() instanceof Item) {
            ItemStack item = ((Item) e.getEntity()).getItemStack();
            if (item.equals(shards)) {
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
                damager.playSound(damager.getLocation(), Sound.ENTITY_ITEM_BREAK, 5.0F, 1.0F);
            }
        }
    }

    @Override
    public void onDisable() {
        getConfig().set("Shard", shards);
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

    public void initShardItem() {
        ItemStack item = new ItemStack(Material.NETHER_STAR, 1);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.RED + "Shard");
        meta.addEnchant(Enchantment.DURABILITY, 1, false);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        item.setItemMeta(meta);
        shards = item;

        ShapedRecipe shapedRecipe = new ShapedRecipe(new NamespacedKey(this, "Shard"), item);
        shapedRecipe.shape("DND", "NSN", "DND");
        shapedRecipe.setIngredient('D', new RecipeChoice.MaterialChoice(Material.DIAMOND_BLOCK));
        shapedRecipe.setIngredient('S', new RecipeChoice.MaterialChoice(Material.NETHER_STAR));
        shapedRecipe.setIngredient('N', new RecipeChoice.MaterialChoice(Material.NETHERITE_INGOT));
        Bukkit.getServer().addRecipe(shapedRecipe);
    }

    public static ItemStack getShardItem() {
        return shards;
    }
}