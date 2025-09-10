package fr.arkoter.easymobloottable;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class MobDeathListener implements Listener {
    
    private final EasyMobLootTable plugin;
    private final Random random;
    
    public MobDeathListener(EasyMobLootTable plugin) {
        this.plugin = plugin;
        this.random = new Random();
    }
    
    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        Entity entity = event.getEntity();
        EntityType entityType = entity.getType();

        String mobConfigPath = "mobs." + entityType.name();
        ConfigurationSection mobSection = plugin.getConfig().getConfigurationSection(mobConfigPath);
        
        if (mobSection == null) {
            return;
        }

        event.getDrops().clear();

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

                        int quantity = min + random.nextInt(max - min + 1);
                        
                        if (quantity > 0) {
                            ItemStack itemStack = new ItemStack(material, quantity);
                            event.getDrops().add(itemStack);
                        }
                    }
                } catch (IllegalArgumentException e) {
                    plugin.getLogger().warning("Invalid material in config: " + itemName);
                }
            }
        }
    }
}
