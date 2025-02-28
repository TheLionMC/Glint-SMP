package me.thelionmc.minecraftplugin.OperatorCommands;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import me.thelionmc.minecraftplugin.Tools.data.ProtectedArea;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.session.ClipboardHolder;
import com.sk89q.worldedit.world.World;
import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

public class Tools implements CommandExecutor, TabCompleter, Listener {

    private final List<String> tools = Arrays.asList("coreprotect", "oneshot", "banhammer", "schematic");
    private final HashMap<UUID, ProtectedArea> pendingSaves = new HashMap<>();
    private final File savedAreasFile = new File("plugins/GlintSMP/protected_areas.yml");
    private final YamlConfiguration savedAreasConfig = YamlConfiguration.loadConfiguration(savedAreasFile);
    private final File schematicFolder = new File("plugins/GlintSMP/schematics");

    public Tools() {
        if (!schematicFolder.exists()) {
            schematicFolder.mkdirs();
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.BLUE + "[Glint SMP] " + ChatColor.RED + "Only players can use this command!");
            return true;
        }

        Player player = (Player) sender;

        if (args.length < 1) {
            player.sendMessage(ChatColor.BLUE + "[Glint SMP] " + ChatColor.RED + "Usage: /gettool [tool_name|coreprotect save|coreprotect abandon|coreprotect delete]");
            return true;
        }

        String subCommand = args[0].toLowerCase();

        if (subCommand.equals("coreprotect")) {
            if (args.length == 1) {
                giveCoreProtectTool(player);
                return true;
            }

            String action = args[1].toLowerCase();
            switch (action) {
                case "save":
                    if (args.length < 3) {
                        player.sendMessage(ChatColor.BLUE + "[Glint SMP] " + ChatColor.GREEN + ChatColor.RED + "Usage: /gettool coreprotect save [name]");
                        return true;
                    }
                    saveProtectedArea(player, args[2]);
                    break;

                case "abandon":
                    if (args.length < 3) {
                        player.sendMessage(ChatColor.BLUE + "[Glint SMP] " + ChatColor.GREEN + ChatColor.RED + "Usage: /gettool coreprotect abandon [name]");
                        return true;
                    }
                    abandonProtectedArea(player, args[2]);
                    break;

                case "delete":
                    if (args.length < 3) {
                        player.sendMessage(ChatColor.BLUE + "[Glint SMP] " + ChatColor.GREEN + ChatColor.RED + "Usage: /gettool coreprotect delete [name]");
                        return true;
                    }
                    deleteProtectedArea(player, args[2]);
                    break;

                default:
                    player.sendMessage(ChatColor.BLUE + "[Glint SMP] " + ChatColor.GREEN + ChatColor.RED + "Invalid coreprotect action. Use save, abandon, or delete.");
                    break;
            }
            return true;
        }

        if (subCommand.equals("oneshot")) {
            giveOneShotSword(player);
            return true;
        }

        if (subCommand.equals("banhammer")) {
            giveBanHammer(player);
            return true;
        }
        switch (subCommand) {
            case "schematic":
                if (args.length < 2) {
                    player.sendMessage(ChatColor.BLUE + "[Glint SMP] " + ChatColor.RED + "Usage: /gettool schematic [schematic_name]");
                    return true;
                }
                pasteSchematic(player, args[1]);
                return true;
        }

        player.sendMessage(ChatColor.BLUE + "[Glint SMP] " + ChatColor.GREEN + ChatColor.RED + "Unknown tool: " + subCommand);
        return true;
    }
    private void pasteSchematic(Player player, String schematicName) {
        File schematicFile = new File(schematicFolder, schematicName + ".schem");

        if (!schematicFile.exists()) {
            player.sendMessage(ChatColor.BLUE + "[Glint SMP] " + ChatColor.RED + "Schematic file '" + schematicName + "' not found in the schematics folder.");
            return;
        }

        try (FileInputStream fis = new FileInputStream(schematicFile)) {
            ClipboardFormat format = ClipboardFormats.findByFile(schematicFile);

            if (format == null) {
                player.sendMessage(ChatColor.BLUE + "[Glint SMP] " + ChatColor.RED + "Invalid schematic file format.");
                return;
            }

            Clipboard clipboard = format.getReader(fis).read();

            World adaptedWorld = BukkitAdapter.adapt(player.getWorld());

            BlockVector3 position = BlockVector3.at(player.getLocation().getBlockX(), player.getLocation().getBlockY(), player.getLocation().getBlockZ());

            try (EditSession editSession = WorldEdit.getInstance().newEditSession(adaptedWorld)) {
                Operation operation = new ClipboardHolder(clipboard)
                        .createPaste(editSession)
                        .to(position)
                        .ignoreAirBlocks(false)
                        .build();

                Operations.complete(operation);
                editSession.flushSession();

                player.sendMessage(ChatColor.BLUE + "[Glint SMP] " + ChatColor.GREEN + "Schematic '" + schematicName + "' pasted successfully!");
            }
        } catch (IOException | WorldEditException e) {
            player.sendMessage(ChatColor.BLUE + "[Glint SMP] " + ChatColor.RED + "An error occurred while pasting the schematic.");
            e.printStackTrace();
        }
    }
    private void giveCoreProtectTool(Player player) {
        ItemStack shovel = new ItemStack(Material.STONE_SHOVEL);
        ItemMeta meta = shovel.getItemMeta();
        meta.setDisplayName(ChatColor.GREEN + "CoreProtect Tool");
        shovel.setItemMeta(meta);

        player.getInventory().addItem(shovel);
        player.sendMessage(ChatColor.BLUE + "[Glint SMP] " + ChatColor.GREEN + ChatColor.GREEN + "Successfully gave you the CoreProtect Tool!");
    }
    private void giveBanHammer(Player player) {
        ItemStack hammer = new ItemStack(Material.NETHERITE_AXE);
        ItemMeta meta = hammer.getItemMeta();
        meta.setDisplayName(ChatColor.RED + "" + ChatColor.UNDERLINE + "BanHammer");
        hammer.setItemMeta(meta);

        player.getInventory().addItem(hammer);
        player.sendMessage(ChatColor.BLUE + "[Glint SMP] " + ChatColor.GREEN + ChatColor.GREEN + "Successfully gave you the Banhammer Tool!");
    }

    private void saveProtectedArea(Player player, String name) {
        UUID playerId = player.getUniqueId();

        if (!pendingSaves.containsKey(playerId)) {
            player.sendMessage(ChatColor.BLUE + "[Glint SMP] " + ChatColor.GREEN + ChatColor.RED + "You do not have any pending area to save.");
            return;
        }

        ProtectedArea area = pendingSaves.get(playerId);
        savedAreasConfig.set(name, area.toMap());

        try {
            savedAreasConfig.save(savedAreasFile);
            player.sendMessage(ChatColor.BLUE + "[Glint SMP] " + ChatColor.GREEN + ChatColor.YELLOW + "Protected area saved as '" + name + "'!");
        } catch (IOException e) {
            player.sendMessage(ChatColor.BLUE + "[Glint SMP] " + ChatColor.GREEN + ChatColor.RED + "Failed to save the protected area.");
            e.printStackTrace();
        }

        pendingSaves.remove(playerId);
    }

    private void abandonProtectedArea(Player player, String name) {
        if (!savedAreasConfig.contains(name)) {
            player.sendMessage(ChatColor.BLUE + "[Glint SMP] " + ChatColor.GREEN + ChatColor.RED + "No saved area found with the name '" + name + "'.");
            return;
        }

        savedAreasConfig.set(name, null);

        try {
            savedAreasConfig.save(savedAreasFile);
            player.sendMessage(ChatColor.BLUE + "[Glint SMP] " + ChatColor.GREEN + ChatColor.YELLOW + "Protected area '" + name + "' has been abandoned.");
        } catch (IOException e) {
            player.sendMessage(ChatColor.BLUE + "[Glint SMP] " + ChatColor.GREEN + ChatColor.RED + "Failed to abandon the protected area.");
            e.printStackTrace();
        }
    }

    private void deleteProtectedArea(Player player, String name) {
        if (!savedAreasConfig.contains(name)) {
            player.sendMessage(ChatColor.BLUE + "[Glint SMP] " + ChatColor.GREEN + ChatColor.RED + "No saved area found with the name '" + name + "'.");
            return;
        }

        savedAreasConfig.set(name, null);

        try {
            savedAreasConfig.save(savedAreasFile);
            player.sendMessage(ChatColor.BLUE + "[Glint SMP] " + ChatColor.GREEN + ChatColor.YELLOW + "Protected area '" + name + "' has been deleted.");
        } catch (IOException e) {
            player.sendMessage(ChatColor.BLUE + "[Glint SMP] " + ChatColor.GREEN + ChatColor.RED + "Failed to delete the protected area.");
            e.printStackTrace();
        }
    }

    @EventHandler
    private void onPlayerhit(EntityDamageByEntityEvent e) {
        Player banvictim = (Player) e.getEntity();
        Player banner = (Player) e.getDamager();
        ItemStack item = banner.getInventory().getItemInMainHand();
        BanList banList = Bukkit.getBanList(BanList.Type.NAME);
        String reason = ChatColor.RED + "You have been struck by the ban hammer!";

        if(item.getType().equals(Material.NETHERITE_AXE) || Objects.requireNonNull(item.getItemMeta()).getDisplayName().equalsIgnoreCase(ChatColor.RED + "Ban Hammer")) {
            banvictim.kickPlayer(ChatColor.RED + "You have been struck by the ban hammer!");
            banList.addBan(banvictim.getName(), reason, (Date)null, banvictim.getName());
        } else if (item.getType().equals(Material.WOODEN_SWORD) || item.getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.RED + "One Shot Sword")) {
            banvictim.setHealth(0);
        }
    }
    private void giveOneShotSword(Player player) {
        ItemStack sword = new ItemStack(Material.WOODEN_SWORD);
        ItemMeta meta = sword.getItemMeta();
        meta.setUnbreakable(true);
        meta.setDisplayName(ChatColor.RED + "One Shot Sword");
        sword.setItemMeta(meta);

        player.getInventory().addItem(sword);
        player.sendMessage(ChatColor.BLUE + "[Glint SMP] " + ChatColor.GREEN + "Successfully gave you the One Shot Item!");
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String alias, String[] args) {
        if (args.length == 1) {
            String input = args[0].toLowerCase();
            List<String> suggestions = new ArrayList<>();
            for (String tool : tools) {
                if (tool.startsWith(input)) {
                    suggestions.add(tool);
                }
            }
            return suggestions;

        } else if (args.length == 2 && args[0].equalsIgnoreCase("coreprotect")) {
            return Arrays.asList("save", "abandon", "delete");

        } else if (args.length == 3 && args[0].equalsIgnoreCase("coreprotect") &&
                (args[1].equalsIgnoreCase("abandon") || args[1].equalsIgnoreCase("delete"))) {
            return new ArrayList<>(savedAreasConfig.getKeys(false));

        } else if (args.length == 2 && args[0].equalsIgnoreCase("schematic")) {
            List<String> schematicNames = new ArrayList<>();
            if (schematicFolder.exists()) {
                for (File file : Objects.requireNonNull(schematicFolder.listFiles())) {
                    if (file.getName().endsWith(".schem")) {
                        schematicNames.add(file.getName().replace(".schem", ""));
                    }
                }
            }
            return schematicNames;
    }
        return null;
    }
}
