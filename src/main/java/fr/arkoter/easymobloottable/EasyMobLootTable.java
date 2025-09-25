package fr.arkoter.easymobloottable;

import fr.arkoter.easymobloottable.gui.GuiManager;
import org.bukkit.plugin.java.JavaPlugin;

public class EasyMobLootTable extends JavaPlugin {
    
    private GuiManager guiManager;
    
    @Override
    public void onEnable() {
        getLogger().info("EasyMobLootTable plugin has been enabled!");

        // Initialize GUI Manager
        this.guiManager = new GuiManager(this);

        // Register commands
        this.getCommand("mobloot").setExecutor(new MobLootCommand(this));
        this.getCommand("moblootgui").setExecutor(new GuiCommand(guiManager));

        // Register events
        getServer().getPluginManager().registerEvents(new MobDeathListener(this), this);

        saveDefaultConfig();
    }
    
    @Override
    public void onDisable() {
        getLogger().info("EasyMobLootTable plugin has been disabled!");
    }
    
    public GuiManager getGuiManager() {
        return guiManager;
    }
}