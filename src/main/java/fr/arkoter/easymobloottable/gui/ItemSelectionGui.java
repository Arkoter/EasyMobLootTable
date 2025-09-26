package fr.arkoter.easymobloottable.gui;

import fr.arkoter.easymobloottable.EasyMobLootTable;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.List;

public class ItemSelectionGui {
    
    private final GuiManager guiManager;
    private final EasyMobLootTable plugin;
    
    // Common items that players might want to add as loot
    private static final List<Material> COMMON_ITEMS = Arrays.asList(
        // Ores and Ingots
        Material.IRON_INGOT,
        Material.GOLD_INGOT,
        Material.DIAMOND,
        Material.EMERALD,
        Material.NETHERITE_INGOT,
        Material.COPPER_INGOT,
        Material.COAL,
        Material.REDSTONE,
        Material.LAPIS_LAZULI,
        
        // Food
        Material.BREAD,
        Material.COOKED_BEEF,
        Material.COOKED_PORKCHOP,
        Material.COOKED_CHICKEN,
        Material.GOLDEN_APPLE,
        Material.ENCHANTED_GOLDEN_APPLE,
        
        // Tools and Weapons
        Material.IRON_SWORD,
        Material.IRON_AXE,
        Material.IRON_PICKAXE,
        Material.BOW,
        Material.CROSSBOW,
        
        // Armor
        Material.IRON_HELMET,
        Material.IRON_CHESTPLATE,
        Material.IRON_LEGGINGS,
        Material.IRON_BOOTS,
        Material.DIAMOND_HELMET,
        Material.DIAMOND_CHESTPLATE,
        Material.DIAMOND_LEGGINGS,
        Material.DIAMOND_BOOTS,
        
        // Potions and Brewing
        Material.POTION,
        Material.EXPERIENCE_BOTTLE,
        Material.BLAZE_POWDER,
        Material.GHAST_TEAR,
        
        // Rare Items
        Material.ENDER_PEARL,
        Material.NETHER_STAR,
        Material.TOTEM_OF_UNDYING,
        Material.ELYTRA,
        
        // Building Blocks
        Material.COBBLESTONE,
        Material.STONE,
        Material.DIRT,
        Material.OAK_LOG,
        Material.SAND,
        Material.GRAVEL
    );
    
    public ItemSelectionGui(GuiManager guiManager, EasyMobLootTable plugin) {
        this.guiManager = guiManager;
        this.plugin = plugin;
    }
    
    public void open(Player player, EntityType mobType) {
        Inventory gui = Bukkit.createInventory(null, 54, "§6§lChoose an Item to Add");
        
        int slot = 0;
        for (Material material : COMMON_ITEMS) {
            if (slot >= 45) break; // Leave space for navigation buttons
            
            ItemStack item = new ItemStack(material);
            ItemMeta meta = item.getItemMeta();
            
            String displayName = getItemDisplayName(material);
            meta.setDisplayName("§e§l" + displayName);
            meta.setLore(Arrays.asList(
                "§7Type: §f" + material.name(),
                "§7Click to add this item",
                "§7to the loot table"
            ));
            item.setItemMeta(meta);
            gui.setItem(slot, item);
            slot++;
        }
        
        // Back button
        ItemStack back = new ItemStack(Material.ARROW);
        ItemMeta backMeta = back.getItemMeta();
        backMeta.setDisplayName("§c§lBack to Editor");
        back.setItemMeta(backMeta);
        gui.setItem(49, back);
        
        // Close button
        ItemStack close = new ItemStack(Material.BARRIER);
        ItemMeta closeMeta = close.getItemMeta();
        closeMeta.setDisplayName("§c§lClose");
        close.setItemMeta(closeMeta);
        gui.setItem(53, close);
        
        player.openInventory(gui);
        guiManager.setGuiType(player, GuiManager.GuiType.ITEM_SELECTION);
        guiManager.setGuiData(player, mobType);
    }
    
    public void handleClick(Player player, ItemStack clickedItem) {
        EntityType mobType = (EntityType) guiManager.getGuiData(player);
        
        if (clickedItem.getType() == Material.ARROW) {
            // Back to loot table editor
            LootTableEditorGui editorGui = new LootTableEditorGui(guiManager, plugin);
            editorGui.open(player, mobType);
            return;
        }
        
        if (clickedItem.getType() == Material.BARRIER) {
            // Close GUI
            player.closeInventory();
            return;
        }
        
        // Check if clicked item represents a material
        ItemMeta meta = clickedItem.getItemMeta();
        if (meta != null && meta.hasLore()) {
            List<String> lore = meta.getLore();
            if (lore.size() >= 1) {
                String typeLine = lore.get(0);
                if (typeLine.startsWith("§7Type: §f")) {
                    String materialName = typeLine.substring(10); // Remove "§7Type: §f"
                    try {
                        Material material = Material.valueOf(materialName);
                        // Add item with default quantities and prompt for customization
                        addItemToLootTable(player, mobType, material);
                    } catch (IllegalArgumentException e) {
                        player.sendMessage("§cError: Invalid item type!");
                    }
                }
            }
        }
    }
    
    private void addItemToLootTable(Player player, EntityType mobType, Material material) {
        // Add with default quantities (1-1)
        String configPath = "mobs." + mobType.name() + ".items." + material.name();
        plugin.getConfig().set(configPath + ".min", 1);
        plugin.getConfig().set(configPath + ".max", 1);
        plugin.saveConfig();
        
        player.sendMessage("§aItem added: " + material.name() + " (quantity: 1-1)");
        player.sendMessage("§7Use the editor to modify quantities");
        
        // Return to loot table editor
        LootTableEditorGui editorGui = new LootTableEditorGui(guiManager, plugin);
        editorGui.open(player, mobType);
    }
    
    private String getItemDisplayName(Material material) {
        switch (material) {
            case IRON_INGOT:
                return "Iron Ingot";
            case GOLD_INGOT:
                return "Gold Ingot";
            case DIAMOND:
                return "Diamond";
            case EMERALD:
                return "Emerald";
            case NETHERITE_INGOT:
                return "Netherite Ingot";
            case COPPER_INGOT:
                return "Copper Ingot";
            case COAL:
                return "Coal";
            case REDSTONE:
                return "Redstone";
            case LAPIS_LAZULI:
                return "Lapis Lazuli";
            case BREAD:
                return "Bread";
            case COOKED_BEEF:
                return "Cooked Beef";
            case COOKED_PORKCHOP:
                return "Cooked Porkchop";
            case COOKED_CHICKEN:
                return "Cooked Chicken";
            case GOLDEN_APPLE:
                return "Golden Apple";
            case ENCHANTED_GOLDEN_APPLE:
                return "Enchanted Golden Apple";
            case IRON_SWORD:
                return "Iron Sword";
            case IRON_AXE:
                return "Iron Axe";
            case IRON_PICKAXE:
                return "Iron Pickaxe";
            case BOW:
                return "Bow";
            case CROSSBOW:
                return "Crossbow";
            case IRON_HELMET:
                return "Iron Helmet";
            case IRON_CHESTPLATE:
                return "Iron Chestplate";
            case IRON_LEGGINGS:
                return "Iron Leggings";
            case IRON_BOOTS:
                return "Iron Boots";
            case DIAMOND_HELMET:
                return "Diamond Helmet";
            case DIAMOND_CHESTPLATE:
                return "Diamond Chestplate";
            case DIAMOND_LEGGINGS:
                return "Diamond Leggings";
            case DIAMOND_BOOTS:
                return "Diamond Boots";
            case POTION:
                return "Potion";
            case EXPERIENCE_BOTTLE:
                return "Bottle o' Enchanting";
            case BLAZE_POWDER:
                return "Blaze Powder";
            case GHAST_TEAR:
                return "Ghast Tear";
            case ENDER_PEARL:
                return "Ender Pearl";
            case NETHER_STAR:
                return "Nether Star";
            case TOTEM_OF_UNDYING:
                return "Totem of Undying";
            case ELYTRA:
                return "Elytra";
            case COBBLESTONE:
                return "Cobblestone";
            case STONE:
                return "Stone";
            case DIRT:
                return "Dirt";
            case OAK_LOG:
                return "Oak Log";
            case SAND:
                return "Sand";
            case GRAVEL:
                return "Gravel";
            default:
                return material.name();
        }
    }
}