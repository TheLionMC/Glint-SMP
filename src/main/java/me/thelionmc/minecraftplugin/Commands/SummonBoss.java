package me.thelionmc.minecraftplugin.Commands;

import me.thelionmc.minecraftplugin.GlintSMP;
import me.thelionmc.minecraftplugin.customItems.BrisknessAxe;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
import org.bukkit.event.entity.EntityTargetEvent.TargetReason;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.*;

public class SummonBoss implements CommandExecutor, Listener, TabCompleter {
    BrisknessAxe brisknessAxe;
    private GlintSMP main;
    private Plugin plugin;
    private final List<String> bosses = Arrays.asList("CleansingWarden", "StaffWither", "DesertDweller");

    public SummonBoss(BrisknessAxe a, GlintSMP main, Plugin plugin) {
        brisknessAxe = a;
        this.plugin = plugin;
        this.main = main;
    }

    @EventHandler
    public void onWardenDeath(EntityDeathEvent event) {
        if (event.getEntity() instanceof EnderDragon) {
            event.getDrops().clear();
            ItemStack specialItem = new ItemStack(Material.CLOCK);
            event.getDrops().add(specialItem);
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("summonboss")) {
            Player player;
            if (!(sender instanceof Player)) {
                sender.sendMessage("You must be a player to execute this command!");
                return true;
            } else {
                player = (Player) sender;
            }

            if (args.length != 1) {
                sender.sendMessage(ChatColor.BLUE + "[Glint SMP] " + ChatColor.RED +"One args!");
                return true;
            }

            if (args[0].equalsIgnoreCase("CleansingWarden")) {
                player.sendMessage(ChatColor.BLUE + "[Glint SMP] " + ChatColor.YELLOW + "Summoning Cleansing Warden...");
                player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 100, 1));
                spawnCustomWarden(player.getLocation());
                //final int[] interval = {20};
                //new BukkitRunnable() {
                    //int ticks = 0;

                    //@Override
                    //public void run() {
                        //if (ticks >= 100) { // Stop after 5 seconds (100 ticks)
                            // Cancel the task and spawn the Warden
                            //player.sendMessage(ChatColor.BLUE + "[Glint SMP] " + ChatColor.GREEN + "Successfully Spawned Cleansing Warden!");
                            //spawnCustomWarden(player.getLocation());
                            //this.cancel();
                            //return;
                        //}

                        // Play the heartbeat sound at the player's location
                        //player.playSound(player.getLocation(), Sound.ENTITY_WARDEN_HEARTBEAT, 1.0f, 1.0f);

                        // Increment ticks by the current interval
                        //ticks += interval[0];

                        // Make the heartbeat interval faster over time
                        //interval[0] = Math.max(5, interval[0] - 1); // Decrease interval to minimum of 5 ticks (0.25 seconds)

                        // Reschedule this task with the updated interval
                        //this.runTaskLater(plugin, 10);
                    //}
                //}.runTaskLater(plugin, 10); // Initial scheduling with the starting interval
            } else if (args[0].equalsIgnoreCase("StaffWither")) {
                player.sendMessage(ChatColor.BLUE + "[Glint SMP]" + ChatColor.GREEN + " Spawned Staff Wither!");
                spawnCustomWither(player.getLocation());
            } else if(args[0].equalsIgnoreCase("DesertDweller")){
                player.sendMessage(ChatColor.BLUE + "[Glint SMP]" + ChatColor.GREEN + " Spawned DesertDweller");
                spawnCustomHusk(player.getLocation());
            } else {
                player.sendMessage(ChatColor.BLUE + "[Glint SMP] " + ChatColor.RED + " Not a boss!");
            }
        }
        return true;
    }



    public void spawnCustomWither(Location location) {
        World world = location.getWorld();
        if (world == null) return;

        // Spawn the Wither
        Wither wither = (Wither) world.spawnEntity(location, EntityType.WITHER);
        wither.setCustomName("§cStaff Wither");
        wither.setMaxHealth(1024.0);
        wither.setHealth(1024.0);
        BossBar bossBar = Bukkit.createBossBar("§cWither", BarColor.PURPLE, BarStyle.SOLID);
        bossBar.setProgress(1.0);
        new BukkitRunnable() {
            @Override
            public void run() {
                if (wither.isDead()) {
                    this.cancel();
                    return;
                }
                Random random = new Random();
                if (random.nextInt(10) < 3) {
                    specialAttack(wither);
                }
            }
        }.runTaskTimer(plugin, 0, 20);
    }
    @EventHandler
    public void onWitherDeath(EntityDeathEvent event) {
        if (event.getEntity() instanceof Wither) {
            Wither wither = (Wither) event.getEntity();

            if (wither.getCustomName() != null && wither.getCustomName().equals("§cStaff Wither")) {
                event.getDrops().clear();
                event.getDrops().add(new ItemStack(Material.GOLDEN_APPLE, 128));
                event.getDrops().add(new ItemStack(Material.DIAMOND_BLOCK, 16));
                event.getDrops().add(new ItemStack(Material.NETHERITE_INGOT, 2));
                event.getDrops().add(new ItemStack(Material.EMERALD_BLOCK, 32));
                event.getDrops().add(new ItemStack(Material.GOLD_BLOCK, 64));
            }
        }
    }
    private void specialAttack(Wither wither) {
        World world = wither.getWorld();
        Location witherLocation = wither.getLocation();

        Player nearestPlayer = null;
        double nearestDistance = 30;

        for (Player player : world.getPlayers()) {
            double distance = player.getLocation().distance(witherLocation);
            if (distance < nearestDistance) {
                nearestDistance = distance;
                nearestPlayer = player;
            }
        }

        if (nearestPlayer != null) {
            Random random = new Random();
            if (random.nextBoolean()) {
                witherSkullBarrage(wither, nearestPlayer);
            } else {
                darknessExplosion(wither);
            }
        }
    }
    private void witherSkullBarrage(Wither wither, Player target) {
        Location witherLocation = wither.getLocation();
        World world = witherLocation.getWorld();
        if (world == null) return;
        new BukkitRunnable() {
            int skullCount = 10;

            @Override
            public void run() {
                if (skullCount <= 0 || wither.isDead()) {
                    this.cancel();
                    return;
                }
                Vector direction = target.getLocation().toVector().subtract(witherLocation.toVector()).normalize();
                wither.launchProjectile(org.bukkit.entity.WitherSkull.class, direction.multiply(1.5));
                skullCount--;
            }
        }.runTaskTimer(plugin, 0, 10);
    }
    public void spawnCustomHusk(Location location) {
        World world = location.getWorld();
        if (world == null) return;

        Husk husk = (Husk) world.spawnEntity(location, EntityType.HUSK);
        husk.setCustomName("§6Desert Colossus");
        husk.setMaxHealth(1024.0);
        husk.setHealth(1024.0);
        husk.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(40.0);
        husk.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(0.4);

        new BukkitRunnable() {
            @Override
            public void run() {
                if (husk.isDead()) {
                    this.cancel();
                    return;
                }
                Random random = new Random();
                if (random.nextInt(10) < 3) {
                    switchTarget(husk);
                }
                if (random.nextInt(10) < 2) {
                    quicksandTrap(husk);
                } else if (random.nextInt(10) < 4) {
                    sandstormBarrage(husk);
                }
            }
        }.runTaskTimer(plugin, 0, 100);
    }
    private void quicksandTrap(Husk husk) {
        Location huskLocation = husk.getLocation();
        World world = husk.getWorld();
        int radius = 5;
        for (Player player : world.getPlayers()) {
            double distance = player.getLocation().distance(huskLocation);
            if (distance <= radius) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 100, 10));
                new BukkitRunnable() {
                    int ticks = 0;
                    @Override
                    public void run() {
                        if (ticks >= 60 || player.isDead() || husk.isDead()) {
                            this.cancel();
                            return;
                        }
                        player.damage(4.0, husk);
                        ticks += 20;
                        world.spawnParticle(Particle.BLOCK_CRACK, huskLocation, 100, 5, 0.5, 5, 0.1, Material.SAND.createBlockData());
                        world.playSound(huskLocation, Sound.BLOCK_SAND_BREAK, 1.0f, 0.5f);
                    }
                }.runTaskTimer(plugin, 0, 20);
            }
        }
    }
    private void sandstormBarrage(Husk husk) {
        Location huskLocation = husk.getLocation();
        World world = husk.getWorld();
        int radius = 10;
        List<Player> nearbyPlayers = new ArrayList<>();
        for (Player player : world.getPlayers()) {
            double distance = player.getLocation().distance(huskLocation);
            if (distance <= radius) {
                nearbyPlayers.add(player);
            }
        }
        if (nearbyPlayers.isEmpty()) return;

        new BukkitRunnable() {
            int projectiles = 20;
            @Override
            public void run() {
                if (projectiles <= 0 || husk.isDead()) {
                    this.cancel();
                    return;
                }
                Player target = nearbyPlayers.get(new Random().nextInt(nearbyPlayers.size()));
                Vector direction = target.getLocation().toVector().subtract(huskLocation.toVector()).normalize();
                Snowball sandProjectile = husk.launchProjectile(Snowball.class);
                sandProjectile.setVelocity(direction.multiply(1.5));
                sandProjectile.setCustomName("sand_projectile");  // Naming the projectile
                world.spawnParticle(Particle.BLOCK_CRACK, huskLocation.add(0, 3, 0), 20, 1, 1, 1, 0.2, Material.SAND.createBlockData());
                world.playSound(huskLocation, Sound.ENTITY_PHANTOM_FLAP, 1.0f, 0.8f);
                projectiles--;
            }
        }.runTaskTimer(plugin, 0, 5);

        for (Player player : nearbyPlayers) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 100, 1));
        }
    }
    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Snowball) {
            Snowball projectile = (Snowball) event.getDamager();
            if ("sand_projectile".equals(projectile.getCustomName())) {
                if (event.getEntity() instanceof Player) {
                    Player player = (Player) event.getEntity();
                    event.setDamage(5.0);
                }
            }
        }
    }


    private void darknessExplosion(Wither wither) {
        Location location = wither.getLocation();
        World world = wither.getWorld();
        if (world == null) return;
        world.spawnParticle(Particle.SMOKE_LARGE, location, 50, 3, 3, 3, 0.1);
        world.playSound(location, Sound.ENTITY_WITHER_BREAK_BLOCK, 2.0f, 0.5f);
        for (Player player : world.getPlayers()) {
            if (player.getLocation().distance(location) <= 10) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 100, 1));
                player.damage(5.0);
            }
        }
    }
    @EventHandler
    public void onWitherHitPlayer(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Wither wither && wither.getCustomName() != null && wither.getCustomName().equals("§cStaff Wither") && event.getEntity() instanceof Player player) {
            Random random = new Random();
            if (random.nextInt(10) == 1) {
                applyDownwardSlam(player);
            } else if (random.nextInt(10) == 2) {
                witherSkullBarrage(wither, player);
            } else if (random.nextInt(10) == 3) {
                darknessExplosion(wither);
            }
        }
    }
    private void applyDownwardSlam(Player player) {
        Location startLocation = player.getLocation();
        World world = startLocation.getWorld();
        if (world == null) return;

        new BukkitRunnable() {
            int depth = 0;

            @Override
            public void run() {
                if (depth >= 10) {
                    player.damage(15.0);
                    world.playSound(player.getLocation(), Sound.ENTITY_IRON_GOLEM_HURT, 1.0f, 1.0f);
                    this.cancel();
                    return;
                }
                Location targetLocation = player.getLocation().clone().add(0, -1, 0);
                if (targetLocation.getBlock().getType() != Material.AIR) {
                    targetLocation.getBlock().setType(Material.AIR);
                    world.spawnParticle(Particle.BLOCK_CRACK, targetLocation, 10, 0.2, 0.2, 0.2, Material.STONE.createBlockData());
                }
                player.setVelocity(new Vector(0, -1, 0));

                depth++;
            }
        }.runTaskTimer(plugin, 0, 2);
    }

    public void spawnCustomWarden(Location location) {
        World world = location.getWorld();
        if (world == null) return;

        Warden warden = (Warden) world.spawnEntity(location, EntityType.WARDEN);
        warden.setCustomName("§cCleansing Warden");
        warden.setMaxHealth(2000);
        warden.setHealth(2000);
        BossBar bossBar = Bukkit.createBossBar("§cCleansing Warden", BarColor.RED, BarStyle.SEGMENTED_10);
        bossBar.setProgress(1.0);
        bossBar.setVisible(true);
        for (Player player : world.getPlayers()) {
            bossBar.addPlayer(player);
        }
        new BukkitRunnable() {
            @Override
            public void run() {
                if (warden.isDead()) {
                    bossBar.removeAll();
                    this.cancel();
                    return;
                }
                double healthProgress = warden.getHealth() / warden.getMaxHealth();
                bossBar.setProgress(healthProgress);
                Random random = new Random();
                if (random.nextInt(10) < 3) {
                    switchTarget(warden);
                }
                handleCobwebs(warden);
                if (random.nextInt(10) < 1) {
                    specialAttack(warden);
                }
            }
        }.runTaskTimer(plugin, 0, 20);
    }
    private void switchTarget(LivingEntity entity) {
        if (!(entity instanceof Mob)) return;
        Mob mob = (Mob) entity;
        World world = mob.getWorld();
        List<Player> nearbyPlayers = new ArrayList<>();
        for (Player player : world.getPlayers()) {
            double distance = player.getLocation().distance(mob.getLocation());
            if (distance <= 30) {
                nearbyPlayers.add(player);
            }
        }
        if (nearbyPlayers.size() <= 1) return;
        Player currentTarget = (Player) mob.getTarget();
        Player newTarget;
        do {
            newTarget = nearbyPlayers.get(new Random().nextInt(nearbyPlayers.size()));
        } while (newTarget.equals(currentTarget));
        mob.setTarget(newTarget);
        mob.getWorld().playSound(mob.getLocation(), Sound.ENTITY_ENDERMAN_STARE, 1.0f, 1.0f);
    }

    private void setCustomTarget(Warden warden, Player target) {
        warden.setMetadata("customTarget", new FixedMetadataValue(plugin, target.getUniqueId().toString()));
    }
    private Player getCustomTarget(Warden warden) {
        if (warden.hasMetadata("customTarget")) {
            String playerUUID = warden.getMetadata("customTarget").get(0).asString();
            return Bukkit.getPlayer(UUID.fromString(playerUUID));
        }
        return null;
    }


    private void specialAttack(Warden warden) {
        World world = warden.getWorld();
        Location wardenLocation = warden.getLocation();
        Player target = getCustomTarget(warden);
        if (target != null) {
            Random random = new Random();
            if (random.nextBoolean()) {
                Location targetLocation = target.getLocation();
                Vector direction = targetLocation.toVector().subtract(wardenLocation.toVector()).normalize();
                Fireball fireball = warden.launchProjectile(Fireball.class);
                fireball.setCustomName("warden_fireball");
                double speed = 1.5;
                fireball.setVelocity(direction.multiply(speed));
                fireball.setYield(0);
                fireball.setIsIncendiary(false);
                world.spawnParticle(Particle.FLAME, wardenLocation, 50, 0.5, 0.5, 0.5, 0.05);
            } else {
                Location playerLocation = target.getLocation();
                world.strikeLightningEffect(playerLocation);
                target.damage(50.0, warden);
                world.spawnParticle(Particle.ELECTRIC_SPARK, playerLocation, 100, 1, 1, 1, 0.1);
            }
        }
    }
    @EventHandler
    public void onProjectileHit(ProjectileHitEvent event) {
        if (event.getEntity() instanceof Fireball) {
            Fireball fireball = (Fireball) event.getEntity();

            if (fireball.getCustomName() != null && fireball.getCustomName().equals("warden_fireball")) {
                if (event.getHitEntity() instanceof Player) {
                    Player player = (Player) event.getHitEntity();

                    player.damage(60.0, (Entity) fireball.getShooter());
                }
            }
        }
    }

    @EventHandler
    public void onWardenHitPlayer(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Warden warden && warden.getCustomName() != null && warden.getCustomName().equals("§cCleansing Warden") && event.getEntity() instanceof Player player) {
            Random random = new Random();
            if (random.nextInt(5) == 1) {
                player.setVelocity(new Vector(0, 5, 0));
                player.getWorld().playSound(player.getLocation(), Sound.ENTITY_IRON_GOLEM_HURT, 1.0f, 1.0f);
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        player.setVelocity(new Vector(0,-10,0));
                    }
                }.runTaskLater(plugin, 30);
            }
        }
    }

    private void handleCobwebs(Warden warden) {
        Location wardenLocation = warden.getLocation();
        World world = wardenLocation.getWorld();
        if (world == null) return;
        for (int x = -1; x <= 1; x++) {
            for (int y = -1; y <= 1; y++) {
                for (int z = -1; z <= 1; z++) {
                    Location checkLocation = wardenLocation.clone().add(x, y, z);
                    Block block = checkLocation.getBlock();
                    if (block.getType() == Material.COBWEB) {
                        block.breakNaturally();
                    }
                }
            }
        }
    }
    private void handleCobwebs2(Husk husk) {
        Location huskLocation = husk.getLocation();
        World world = huskLocation.getWorld();
        if (world == null) return;
        for (int x = -1; x <= 1; x++) {
            for (int y = -1; y <= 1; y++) {
                for (int z = -1; z <= 1; z++) {
                    Location checkLocation = huskLocation.clone().add(x, y, z);
                    Block block = checkLocation.getBlock();
                    if (block.getType() == Material.COBWEB) {
                        block.breakNaturally();
                    }
                }
            }
        }
    }
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            String input = args[0].toLowerCase();
            List<String> suggestions = new ArrayList<>();
            for (String boss : bosses) {
                if (boss.startsWith(input)) {
                    suggestions.add(boss);
                }
            }
            return suggestions;
        }
        return null;
    }
}
