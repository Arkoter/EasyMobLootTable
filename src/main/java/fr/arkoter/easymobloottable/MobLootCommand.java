package fr.arkoter.easymobloottable;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;

public class MobLootCommand implements CommandExecutor {
    
    private final EasyMobLootTable plugin;
    
    public MobLootCommand(EasyMobLootTable plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("easymobloottable.admin")) {
            sender.sendMessage("§cYou don't have permission to use this command!");
            return true;
        }
        
        if (args.length == 0) {
            sendUsage(sender);
            return true;
        }
        
        String action = args[0].toLowerCase();
        
        switch (action) {
            case "remove":
                return handleRemove(sender, args);
            default:
                return handleAdd(sender, args);
        }
    }
    
    private boolean handleAdd(CommandSender sender, String[] args) {
        if (args.length != 4) {
            sender.sendMessage("§cUsage: /mobloot <mob> <item> <min_quantity> <max_quantity>");
            return true;
        }
        
        String mobName = args[0].toUpperCase();
        String itemName = args[1].toUpperCase();
        String minStr = args[2];
        String maxStr = args[3];

        EntityType mobType;
        try {
            mobType = EntityType.valueOf(mobName);
        } catch (IllegalArgumentException e) {
            sender.sendMessage("§cInvalid mob type: " + args[0]);
            return true;
        }

        Material item;
        try {
            item = Material.valueOf(itemName);
        } catch (IllegalArgumentException e) {
            sender.sendMessage("§cInvalid item: " + args[1]);
            return true;
        }

        int minQuantity, maxQuantity;
        try {
            minQuantity = Integer.parseInt(minStr);
            maxQuantity = Integer.parseInt(maxStr);
        } catch (NumberFormatException e) {
            sender.sendMessage("§cQuantities must be valid numbers!");
            return true;
        }
        
        if (minQuantity < 1 || maxQuantity < 1 || minQuantity > maxQuantity) {
            sender.sendMessage("§cInvalid quantities! Min and max must be positive, and min <= max");
            return true;
        }

        String configPath = "mobs." + mobType.name() + ".items." + item.name();
        plugin.getConfig().set(configPath + ".min", minQuantity);
        plugin.getConfig().set(configPath + ".max", maxQuantity);
        plugin.saveConfig();
        
        sender.sendMessage("§aCustom loot added: " + mobType.name() + " will drop " + minQuantity + "-" + maxQuantity + " " + item.name());
        return true;
    }
    
    private boolean handleRemove(CommandSender sender, String[] args) {
        if (args.length != 2) {
            sender.sendMessage("§cUsage: /mobloot remove <mob>");
            return true;
        }
        
        String mobName = args[1].toUpperCase();

        EntityType mobType;
        try {
            mobType = EntityType.valueOf(mobName);
        } catch (IllegalArgumentException e) {
            sender.sendMessage("§cInvalid mob type: " + args[1]);
            return true;
        }

        plugin.getConfig().set("mobs." + mobType.name(), null);
        plugin.saveConfig();
        
        sender.sendMessage("§aCustom loot removed for: " + mobType.name());
        return true;
    }
    
    private void sendUsage(CommandSender sender) {
        sender.sendMessage("§6EasyMobLootTable Commands:");
        sender.sendMessage("§e/mobloot <mob> <item> <min> <max> §f- Add custom loot");
        sender.sendMessage("§e/mobloot remove <mob> §f- Remove all custom loot");
    }
}
