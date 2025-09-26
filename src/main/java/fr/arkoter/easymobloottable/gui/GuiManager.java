package fr.arkoter.easymobloottable.gui;

import fr.arkoter.easymobloottable.EasyMobLootTable;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class GuiManager implements Listener {
    
    private final EasyMobLootTable plugin;
    private final Map<UUID, GuiType> openGuis;
    private final Map<UUID, Object> guiData; // Store additional data for each GUI
    
    public GuiManager(EasyMobLootTable plugin) {
        this.plugin = plugin;
        this.openGuis = new HashMap<>();
        this.guiData = new HashMap<>();
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }
    
    public enum GuiType {
        MAIN_MENU,
        MOB_SELECTION,
        LOOT_TABLE_VIEWER,
        LOOT_TABLE_EDITOR,
        ITEM_SELECTION,
        QUANTITY_SELECTOR_MIN,
        QUANTITY_SELECTOR_MAX
    }
    
    public void openMainMenu(Player player) {
        Inventory gui = Bukkit.createInventory(null, 27, "§6§lEasyMobLootTable - Main Menu");
        
        // Create new loot table option
        ItemStack createNew = new ItemStack(Material.EMERALD);
        ItemMeta createMeta = createNew.getItemMeta();
        createMeta.setDisplayName("§a§lCreate New Loot Table");
        createMeta.setLore(Arrays.asList(
            "§7Click to choose a mob and",
            "§7create a new loot table"
        ));
        createNew.setItemMeta(createMeta);
        gui.setItem(11, createNew);
        
        // View existing loot tables option
        ItemStack viewExisting = new ItemStack(Material.BOOK);
        ItemMeta viewMeta = viewExisting.getItemMeta();
        viewMeta.setDisplayName("§b§lView Modified Loot Tables");
        viewMeta.setLore(Arrays.asList(
            "§7Click to view, modify or",
            "§7delete existing loot tables"
        ));
        viewExisting.setItemMeta(viewMeta);
        gui.setItem(15, viewExisting);
        
        // Close button
        ItemStack close = new ItemStack(Material.BARRIER);
        ItemMeta closeMeta = close.getItemMeta();
        closeMeta.setDisplayName("§c§lClose");
        close.setItemMeta(closeMeta);
        gui.setItem(22, close);
        
        player.openInventory(gui);
        openGuis.put(player.getUniqueId(), GuiType.MAIN_MENU);
    }
    
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;
        
        Player player = (Player) event.getWhoClicked();
        UUID playerId = player.getUniqueId();
        
        if (!openGuis.containsKey(playerId)) return;
        
        event.setCancelled(true);
        
        if (event.getCurrentItem() == null || event.getCurrentItem().getType() == Material.AIR) {
            return;
        }
        
        GuiType guiType = openGuis.get(playerId);
        boolean rightClick = event.isRightClick();
        
        switch (guiType) {
            case MAIN_MENU:
                handleMainMenuClick(player, event.getCurrentItem());
                break;
            case MOB_SELECTION:
                handleMobSelectionClick(player, event.getCurrentItem());
                break;
            case LOOT_TABLE_VIEWER:
                handleLootTableViewerClick(player, event.getCurrentItem());
                break;
            case LOOT_TABLE_EDITOR:
                handleLootTableEditorClick(player, event.getCurrentItem(), rightClick);
                break;
            case ITEM_SELECTION:
                handleItemSelectionClick(player, event.getCurrentItem());
                break;
            case QUANTITY_SELECTOR_MIN:
                handleQuantityMinSelectorClick(player, event.getCurrentItem());
                break;
            case QUANTITY_SELECTOR_MAX:
                handleQuantityMaxSelectorClick(player, event.getCurrentItem());
                break;
        }
    }
    
    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (event.getPlayer() instanceof Player) {
            Player player = (Player) event.getPlayer();
            openGuis.remove(player.getUniqueId());
            guiData.remove(player.getUniqueId());
        }
    }
    
    private void handleMainMenuClick(Player player, ItemStack item) {
        switch (item.getType()) {
            case EMERALD:
                openMobSelectionGui(player);
                break;
            case BOOK:
                openLootTableViewer(player);
                break;
            case BARRIER:
                player.closeInventory();
                break;
        }
    }
    
    private void handleMobSelectionClick(Player player, ItemStack item) {
        MobSelectionGui mobGui = new MobSelectionGui(this, plugin);
        mobGui.handleClick(player, item);
    }
    
    private void handleLootTableViewerClick(Player player, ItemStack item) {
        LootTableViewerGui viewerGui = new LootTableViewerGui(this, plugin);
        viewerGui.handleClick(player, item);
    }
    
    private void handleLootTableEditorClick(Player player, ItemStack item, boolean rightClick) {
        LootTableEditorGui editorGui = new LootTableEditorGui(this, plugin);
        editorGui.handleClick(player, item, rightClick);
    }
    
    private void handleItemSelectionClick(Player player, ItemStack item) {
        ItemSelectionGui itemGui = new ItemSelectionGui(this, plugin);
        itemGui.handleClick(player, item);
    }
    
    private void handleQuantityMinSelectorClick(Player player, ItemStack item) {
        LootTableEditorGui editorGui = new LootTableEditorGui(this, plugin);
        editorGui.handleQuantityMinClick(player, item);
    }
    
    private void handleQuantityMaxSelectorClick(Player player, ItemStack item) {
        LootTableEditorGui editorGui = new LootTableEditorGui(this, plugin);
        editorGui.handleQuantityMaxClick(player, item);
    }
    
    private void openMobSelectionGui(Player player) {
        MobSelectionGui mobGui = new MobSelectionGui(this, plugin);
        mobGui.open(player);
    }
    
    private void openLootTableViewer(Player player) {
        LootTableViewerGui viewerGui = new LootTableViewerGui(this, plugin);
        viewerGui.open(player);
    }
    
    public void setGuiType(Player player, GuiType type) {
        openGuis.put(player.getUniqueId(), type);
    }
    
    public void setGuiData(Player player, Object data) {
        guiData.put(player.getUniqueId(), data);
    }
    
    public Object getGuiData(Player player) {
        return guiData.get(player.getUniqueId());
    }
    
    public EasyMobLootTable getPlugin() {
        return plugin;
    }
}