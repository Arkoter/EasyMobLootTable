package fr.arkoter.easymobloottable.gui;

import fr.arkoter.easymobloottable.EasyMobLootTable;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class LootTableEditorGui {
    
    private final GuiManager guiManager;
    private final EasyMobLootTable plugin;
    
    public LootTableEditorGui(GuiManager guiManager, EasyMobLootTable plugin) {
        this.guiManager = guiManager;
        this.plugin = plugin;
    }
    
    public void open(Player player, EntityType mobType) {
        Inventory gui = Bukkit.createInventory(null, 54, "§6§lLoot Table - " + getMobDisplayName(mobType));
        
        // Load existing loot table items
        Map<Material, ItemQuantity> currentLoots = loadCurrentLootTable(mobType);
        
        int slot = 0;
        for (Map.Entry<Material, ItemQuantity> entry : currentLoots.entrySet()) {
            if (slot >= 45) break; // Leave space for buttons
            
            Material material = entry.getKey();
            ItemQuantity quantity = entry.getValue();
            
            ItemStack item = new ItemStack(material);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName("§e§l" + material.name());
            meta.setLore(Arrays.asList(
                "§7Minimum quantity: §f" + quantity.min,
                "§7Maximum quantity: §f" + quantity.max,
                "§c§lRight click to remove",
                "§a§lLeft click to modify"
            ));
            item.setItemMeta(meta);
            gui.setItem(slot, item);
            slot++;
        }
        
        // Add new item button
        ItemStack addItem = new ItemStack(Material.EMERALD);
        ItemMeta addMeta = addItem.getItemMeta();
        addMeta.setDisplayName("§a§lAdd Item in Hand");
        addMeta.setLore(Arrays.asList(
            "§7Click to add the item you",
            "§7are holding to the loot table"
        ));
        addItem.setItemMeta(addMeta);
        gui.setItem(45, addItem);
        
        // Save button
        ItemStack save = new ItemStack(Material.WRITABLE_BOOK);
        ItemMeta saveMeta = save.getItemMeta();
        saveMeta.setDisplayName("§a§lSave");
        saveMeta.setLore(Arrays.asList(
            "§7Click to save",
            "§7the modifications"
        ));
        save.setItemMeta(saveMeta);
        gui.setItem(48, save);
        
        // Back button
        ItemStack back = new ItemStack(Material.ARROW);
        ItemMeta backMeta = back.getItemMeta();
        backMeta.setDisplayName("§c§lBack to Selection");
        back.setItemMeta(backMeta);
        gui.setItem(49, back);
        
        // Reset button (restore original loot)
        ItemStack reset = new ItemStack(Material.REDSTONE);
        ItemMeta resetMeta = reset.getItemMeta();
        resetMeta.setDisplayName("§c§lReset");
        resetMeta.setLore(Arrays.asList(
            "§7Remove all modifications",
            "§7and restore original loot table"
        ));
        reset.setItemMeta(resetMeta);
        gui.setItem(50, reset);
        
        // Close button
        ItemStack close = new ItemStack(Material.BARRIER);
        ItemMeta closeMeta = close.getItemMeta();
        closeMeta.setDisplayName("§c§lClose");
        close.setItemMeta(closeMeta);
        gui.setItem(53, close);
        
        player.openInventory(gui);
        guiManager.setGuiType(player, GuiManager.GuiType.LOOT_TABLE_EDITOR);
        guiManager.setGuiData(player, mobType);
    }
    
    public void handleClick(Player player, ItemStack clickedItem, boolean rightClick) {
        EntityType mobType = (EntityType) guiManager.getGuiData(player);
        
        switch (clickedItem.getType()) {
            case EMERALD:
                // Add item from player's hand
                ItemStack handItem = player.getInventory().getItemInMainHand();
                if (handItem == null || handItem.getType() == Material.AIR) {
                    player.sendMessage("§cYou must hold an item in your hand to add it!");
                    return;
                }
                
                Material material = handItem.getType();
                
                // Check if item already exists in loot table
                String configPath = "mobs." + mobType.name() + ".items." + material.name();
                if (plugin.getConfig().contains(configPath)) {
                    player.sendMessage("§cThis item is already in the loot table!");
                    return;
                }
                
                // Add item with default quantities (1-1) - player can modify later
                plugin.getConfig().set(configPath + ".min", 1);
                plugin.getConfig().set(configPath + ".max", 1);
                plugin.saveConfig();
                
                player.sendMessage("§aItem added: " + material.name() + " (1-1). Click on the item to modify quantities.");
                
                // Refresh the GUI
                open(player, mobType);
                break;
            case WRITABLE_BOOK:
                // Save (already auto-saved when items are modified)
                player.sendMessage("§aLoot table saved!");
                break;
            case ARROW:
                // Back to mob selection
                MobSelectionGui mobGui = new MobSelectionGui(guiManager, plugin);
                mobGui.open(player);
                break;
            case REDSTONE:
                // Reset loot table
                resetLootTable(player, mobType);
                break;
            case BARRIER:
                // Close
                player.closeInventory();
                break;
            default:
                // Handle item modification/deletion
                if (rightClick) {
                    // Delete item from loot table
                    removeItemFromLootTable(player, mobType, clickedItem.getType());
                } else {
                    // Modify quantities (open quantity selector)
                    openQuantitySelector(player, mobType, clickedItem.getType());
                }
                break;
        }
    }
    
    private void openItemSelection(Player player, EntityType mobType) {
        ItemSelectionGui itemGui = new ItemSelectionGui(guiManager, plugin);
        itemGui.open(player, mobType);
    }
    
    private void openQuantitySelector(Player player, EntityType mobType, Material material) {
        // Open GUI-based quantity selector - start with minimum quantity
        openMinQuantitySelector(player, mobType, material);
    }
    
    private void openMinQuantitySelector(Player player, EntityType mobType, Material material) {
        openMinQuantitySelector(player, mobType, material, -1, -1);
    }
    
    private void openMinQuantitySelector(Player player, EntityType mobType, Material material, int currentMin, int currentMax) {
        // Get current quantities if not provided
        if (currentMin == -1 || currentMax == -1) {
            String configPath = "mobs." + mobType.name() + ".items." + material.name();
            currentMin = plugin.getConfig().getInt(configPath + ".min", 1);
            currentMax = plugin.getConfig().getInt(configPath + ".max", 1);
        }
        
        Inventory gui = Bukkit.createInventory(null, 27, "§6§lMinimum Quantity - " + material.name());
        
        // Decrease buttons (left side)
        ItemStack decrease10 = new ItemStack(Material.RED_CONCRETE);
        ItemMeta decrease10Meta = decrease10.getItemMeta();
        decrease10Meta.setDisplayName("§c§l-10");
        decrease10.setItemMeta(decrease10Meta);
        gui.setItem(10, decrease10);
        
        ItemStack decrease1 = new ItemStack(Material.ORANGE_CONCRETE);
        ItemMeta decrease1Meta = decrease1.getItemMeta();
        decrease1Meta.setDisplayName("§c§l-1");
        decrease1.setItemMeta(decrease1Meta);
        gui.setItem(11, decrease1);
        
        // Item display (center)
        ItemStack displayItem = new ItemStack(material);
        ItemMeta displayMeta = displayItem.getItemMeta();
        displayMeta.setDisplayName("§e§l" + material.name());
        displayMeta.setLore(Arrays.asList(
            "§7Minimum quantity: §f" + currentMin,
            "§7Current maximum quantity: §f" + currentMax
        ));
        displayItem.setItemMeta(displayMeta);
        gui.setItem(13, displayItem);
        
        // Increase buttons (right side)
        ItemStack increase1 = new ItemStack(Material.LIME_CONCRETE);
        ItemMeta increase1Meta = increase1.getItemMeta();
        increase1Meta.setDisplayName("§a§l+1");
        increase1.setItemMeta(increase1Meta);
        gui.setItem(15, increase1);
        
        ItemStack increase10 = new ItemStack(Material.GREEN_CONCRETE);
        ItemMeta increase10Meta = increase10.getItemMeta();
        increase10Meta.setDisplayName("§a§l+10");
        increase10.setItemMeta(increase10Meta);
        gui.setItem(16, increase10);
        
        // Confirm button
        ItemStack confirm = new ItemStack(Material.EMERALD);
        ItemMeta confirmMeta = confirm.getItemMeta();
        confirmMeta.setDisplayName("§a§lConfirm Minimum");
        confirmMeta.setLore(Arrays.asList(
            "§7Confirm and proceed to",
            "§7maximum quantity selection"
        ));
        confirm.setItemMeta(confirmMeta);
        gui.setItem(22, confirm);
        
        // Cancel button
        ItemStack cancel = new ItemStack(Material.BARRIER);
        ItemMeta cancelMeta = cancel.getItemMeta();
        cancelMeta.setDisplayName("§c§lCancel");
        cancel.setItemMeta(cancelMeta);
        gui.setItem(26, cancel);
        
        player.openInventory(gui);
        guiManager.setGuiType(player, GuiManager.GuiType.QUANTITY_SELECTOR_MIN);
        
        // Store data for quantity selection
        Map<String, Object> data = new HashMap<>();
        data.put("mobType", mobType);
        data.put("material", material);
        data.put("currentMin", currentMin);
        data.put("currentMax", currentMax);
        guiManager.setGuiData(player, data);
    }
    
    private void openMaxQuantitySelector(Player player, EntityType mobType, Material material, int minQuantity) {
        openMaxQuantitySelector(player, mobType, material, minQuantity, -1);
    }
    
    private void openMaxQuantitySelector(Player player, EntityType mobType, Material material, int minQuantity, int currentMax) {
        // Get current max quantity if not provided
        if (currentMax == -1) {
            String configPath = "mobs." + mobType.name() + ".items." + material.name();
            currentMax = plugin.getConfig().getInt(configPath + ".max", minQuantity);
        }
        
        // Ensure max is at least equal to min
        if (currentMax < minQuantity) {
            currentMax = minQuantity;
        }
        
        Inventory gui = Bukkit.createInventory(null, 27, "§6§lMaximum Quantity - " + material.name());
        
        // Decrease buttons (left side)
        ItemStack decrease10 = new ItemStack(Material.RED_CONCRETE);
        ItemMeta decrease10Meta = decrease10.getItemMeta();
        decrease10Meta.setDisplayName("§c§l-10");
        decrease10.setItemMeta(decrease10Meta);
        gui.setItem(10, decrease10);
        
        ItemStack decrease1 = new ItemStack(Material.ORANGE_CONCRETE);
        ItemMeta decrease1Meta = decrease1.getItemMeta();
        decrease1Meta.setDisplayName("§c§l-1");
        decrease1.setItemMeta(decrease1Meta);
        gui.setItem(11, decrease1);
        
        // Item display (center)
        ItemStack displayItem = new ItemStack(material);
        ItemMeta displayMeta = displayItem.getItemMeta();
        displayMeta.setDisplayName("§e§l" + material.name());
        displayMeta.setLore(Arrays.asList(
            "§7Minimum quantity: §f" + minQuantity,
            "§7Maximum quantity: §f" + currentMax
        ));
        displayItem.setItemMeta(displayMeta);
        gui.setItem(13, displayItem);
        
        // Increase buttons (right side)
        ItemStack increase1 = new ItemStack(Material.LIME_CONCRETE);
        ItemMeta increase1Meta = increase1.getItemMeta();
        increase1Meta.setDisplayName("§a§l+1");
        increase1.setItemMeta(increase1Meta);
        gui.setItem(15, increase1);
        
        ItemStack increase10 = new ItemStack(Material.GREEN_CONCRETE);
        ItemMeta increase10Meta = increase10.getItemMeta();
        increase10Meta.setDisplayName("§a§l+10");
        increase10.setItemMeta(increase10Meta);
        gui.setItem(16, increase10);
        
        // Confirm button
        ItemStack confirm = new ItemStack(Material.EMERALD);
        ItemMeta confirmMeta = confirm.getItemMeta();
        confirmMeta.setDisplayName("§a§lConfirm Maximum");
        confirmMeta.setLore(Arrays.asList(
            "§7Save modifications",
            "§7and return to menu"
        ));
        confirm.setItemMeta(confirmMeta);
        gui.setItem(22, confirm);
        
        // Cancel button
        ItemStack cancel = new ItemStack(Material.BARRIER);
        ItemMeta cancelMeta = cancel.getItemMeta();
        cancelMeta.setDisplayName("§c§lCancel");
        cancel.setItemMeta(cancelMeta);
        gui.setItem(26, cancel);
        
        player.openInventory(gui);
        guiManager.setGuiType(player, GuiManager.GuiType.QUANTITY_SELECTOR_MAX);
        
        // Store data for quantity selection
        Map<String, Object> data = new HashMap<>();
        data.put("mobType", mobType);
        data.put("material", material);
        data.put("minQuantity", minQuantity);
        data.put("currentMax", currentMax);
        guiManager.setGuiData(player, data);
    }
    
    public void handleQuantityMinClick(Player player, ItemStack clickedItem) {
        @SuppressWarnings("unchecked")
        Map<String, Object> data = (Map<String, Object>) guiManager.getGuiData(player);
        EntityType mobType = (EntityType) data.get("mobType");
        Material material = (Material) data.get("material");
        int currentMin = (Integer) data.get("currentMin");
        int currentMax = (Integer) data.get("currentMax");
        
        switch (clickedItem.getType()) {
            case RED_CONCRETE:
                // Decrease by 10
                currentMin = Math.max(1, currentMin - 10);
                data.put("currentMin", currentMin);
                openMinQuantitySelector(player, mobType, material, currentMin, currentMax);
                break;
            case ORANGE_CONCRETE:
                // Decrease by 1
                currentMin = Math.max(1, currentMin - 1);
                data.put("currentMin", currentMin);
                openMinQuantitySelector(player, mobType, material, currentMin, currentMax);
                break;
            case LIME_CONCRETE:
                // Increase by 1
                currentMin = currentMin + 1;
                data.put("currentMin", currentMin);
                openMinQuantitySelector(player, mobType, material, currentMin, currentMax);
                break;
            case GREEN_CONCRETE:
                // Increase by 10
                currentMin = currentMin + 10;
                data.put("currentMin", currentMin);
                openMinQuantitySelector(player, mobType, material, currentMin, currentMax);
                break;
            case EMERALD:
                // Confirm minimum and move to maximum selection
                openMaxQuantitySelector(player, mobType, material, currentMin);
                break;
            case BARRIER:
                // Cancel - go back to loot table editor
                open(player, mobType);
                break;
        }
    }
    
    public void handleQuantityMaxClick(Player player, ItemStack clickedItem) {
        @SuppressWarnings("unchecked")
        Map<String, Object> data = (Map<String, Object>) guiManager.getGuiData(player);
        EntityType mobType = (EntityType) data.get("mobType");
        Material material = (Material) data.get("material");
        int minQuantity = (Integer) data.get("minQuantity");
        int currentMax = (Integer) data.get("currentMax");
        
        switch (clickedItem.getType()) {
            case RED_CONCRETE:
                // Decrease by 10
                currentMax = Math.max(minQuantity, currentMax - 10);
                data.put("currentMax", currentMax);
                openMaxQuantitySelector(player, mobType, material, minQuantity, currentMax);
                break;
            case ORANGE_CONCRETE:
                // Decrease by 1
                currentMax = Math.max(minQuantity, currentMax - 1);
                data.put("currentMax", currentMax);
                openMaxQuantitySelector(player, mobType, material, minQuantity, currentMax);
                break;
            case LIME_CONCRETE:
                // Increase by 1
                currentMax = currentMax + 1;
                data.put("currentMax", currentMax);
                openMaxQuantitySelector(player, mobType, material, minQuantity, currentMax);
                break;
            case GREEN_CONCRETE:
                // Increase by 10
                currentMax = currentMax + 10;
                data.put("currentMax", currentMax);
                openMaxQuantitySelector(player, mobType, material, minQuantity, currentMax);
                break;
            case EMERALD:
                // Confirm maximum and save to config
                String configPath = "mobs." + mobType.name() + ".items." + material.name();
                plugin.getConfig().set(configPath + ".min", minQuantity);
                plugin.getConfig().set(configPath + ".max", currentMax);
                plugin.saveConfig();
                
                player.sendMessage("§aQuantities updated: " + material.name() + " (" + minQuantity + "-" + currentMax + ")");
                
                // Go back to loot table editor
                open(player, mobType);
                break;
            case BARRIER:
                // Cancel - go back to loot table editor
                open(player, mobType);
                break;
        }
    }
    
    private void removeItemFromLootTable(Player player, EntityType mobType, Material material) {
        String configPath = "mobs." + mobType.name() + ".items." + material.name();
        plugin.getConfig().set(configPath, null);
        
        // Clean up empty sections
        ConfigurationSection mobSection = plugin.getConfig().getConfigurationSection("mobs." + mobType.name());
        if (mobSection != null) {
            ConfigurationSection itemsSection = mobSection.getConfigurationSection("items");
            if (itemsSection == null || itemsSection.getKeys(false).isEmpty()) {
                plugin.getConfig().set("mobs." + mobType.name(), null);
            }
        }
        
        plugin.saveConfig();
        player.sendMessage("§cItem removed: " + material.name());
        
        // Refresh the GUI
        open(player, mobType);
    }
    
    private void resetLootTable(Player player, EntityType mobType) {
        plugin.getConfig().set("mobs." + mobType.name(), null);
        plugin.saveConfig();
        player.sendMessage("§aLoot table reset for: " + getMobDisplayName(mobType));
        
        // Go back to mob selection
        MobSelectionGui mobGui = new MobSelectionGui(guiManager, plugin);
        mobGui.open(player);
    }
    
    private Map<Material, ItemQuantity> loadCurrentLootTable(EntityType mobType) {
        Map<Material, ItemQuantity> loots = new HashMap<>();
        
        String mobConfigPath = "mobs." + mobType.name();
        ConfigurationSection mobSection = plugin.getConfig().getConfigurationSection(mobConfigPath);
        
        if (mobSection != null) {
            ConfigurationSection itemsSection = mobSection.getConfigurationSection("items");
            if (itemsSection != null) {
                Set<String> itemNames = itemsSection.getKeys(false);
                
                for (String itemName : itemNames) {
                    try {
                        Material material = Material.valueOf(itemName);
                        ConfigurationSection itemSection = itemsSection.getConfigurationSection(itemName);
                        
                        if (itemSection != null) {
                            int min = itemSection.getInt("min", 1);
                            int max = itemSection.getInt("max", 1);
                            loots.put(material, new ItemQuantity(min, max));
                        }
                    } catch (IllegalArgumentException e) {
                        plugin.getLogger().warning("Invalid material in config: " + itemName);
                    }
                }
            }
        }
        
        return loots;
    }
    
    private String getMobDisplayName(EntityType mobType) {
        // Use the same method from MobSelectionGui
        switch (mobType) {
            case ZOMBIE:
                return "Zombie";
            case SKELETON:
                return "Skeleton";
            case CREEPER:
                return "Creeper";
            case SPIDER:
                return "Spider";
            case ENDERMAN:
                return "Enderman";
            case WITCH:
                return "Witch";
            case ZOMBIE_VILLAGER:
                return "Zombie Villager";
            case HUSK:
                return "Husk";
            case STRAY:
                return "Stray";
            case WITHER_SKELETON:
                return "Wither Skeleton";
            case BLAZE:
                return "Blaze";
            case GHAST:
                return "Ghast";
            case MAGMA_CUBE:
                return "Magma Cube";
            case SLIME:
                return "Slime";
            case SILVERFISH:
                return "Silverfish";
            case CAVE_SPIDER:
                return "Cave Spider";
            case DROWNED:
                return "Drowned";
            case PHANTOM:
                return "Phantom";
            case PILLAGER:
                return "Pillager";
            case VINDICATOR:
                return "Vindicator";
            case EVOKER:
                return "Evoker";
            case RAVAGER:
                return "Ravager";
            case VEX:
                return "Vex";
            case GUARDIAN:
                return "Guardian";
            case ELDER_GUARDIAN:
                return "Elder Guardian";
            case SHULKER:
                return "Shulker";
            case ENDERMITE:
                return "Endermite";
            default:
                return mobType.name();
        }
    }
    
    private static class ItemQuantity {
        public final int min;
        public final int max;
        
        public ItemQuantity(int min, int max) {
            this.min = min;
            this.max = max;
        }
    }
}