package fr.arkoter.easymobloottable;

import org.bukkit.plugin.java.JavaPlugin;

public class EasyMobLootTable extends JavaPlugin {
    
    @Override
    public void onEnable() {
        getLogger().info("EasyMobLootTable plugin has been enabled!");

        this.getCommand("mobloot").setExecutor(new MobLootCommand(this));

        getServer().getPluginManager().registerEvents(new MobDeathListener(this), this);

        saveDefaultConfig();
    }
    
    @Override
    public void onDisable() {
        getLogger().info("EasyMobLootTable plugin has been disabled!");
    }
}