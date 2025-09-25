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

public class MobSelectionGui {
    
    private final GuiManager guiManager;
    private final EasyMobLootTable plugin;
    
    // All hostile, passive and neutral mobs
    private static final List<EntityType> ALL_MOBS = Arrays.asList(
        // Hostile mobs
        EntityType.ZOMBIE,
        EntityType.SKELETON,
        EntityType.CREEPER,
        EntityType.SPIDER,
        EntityType.ENDERMAN,
        EntityType.WITCH,
        EntityType.ZOMBIE_VILLAGER,
        EntityType.HUSK,
        EntityType.STRAY,
        EntityType.WITHER_SKELETON,
        EntityType.BLAZE,
        EntityType.GHAST,
        EntityType.MAGMA_CUBE,
        EntityType.SLIME,
        EntityType.SILVERFISH,
        EntityType.CAVE_SPIDER,
        EntityType.DROWNED,
        EntityType.PHANTOM,
        EntityType.PILLAGER,
        EntityType.VINDICATOR,
        EntityType.EVOKER,
        EntityType.RAVAGER,
        EntityType.VEX,
        EntityType.GUARDIAN,
        EntityType.ELDER_GUARDIAN,
        EntityType.SHULKER,
        EntityType.ENDERMITE,
        EntityType.WITHER,
        EntityType.ENDER_DRAGON,
        EntityType.ZOMBIFIED_PIGLIN,
        EntityType.PIGLIN,
        EntityType.PIGLIN_BRUTE,
        EntityType.HOGLIN,
        EntityType.ZOGLIN,
        EntityType.WARDEN,
        
        // Passive mobs
        EntityType.COW,
        EntityType.PIG,
        EntityType.SHEEP,
        EntityType.CHICKEN,
        EntityType.HORSE,
        EntityType.DONKEY,
        EntityType.MULE,
        EntityType.LLAMA,
        EntityType.VILLAGER,
        EntityType.SQUID,
        EntityType.BAT,
        EntityType.OCELOT,
        EntityType.WOLF,
        EntityType.MOOSHROOM,
        EntityType.RABBIT,
        EntityType.POLAR_BEAR,
        EntityType.PARROT,
        EntityType.TURTLE,
        EntityType.COD,
        EntityType.SALMON,
        EntityType.PUFFERFISH,
        EntityType.TROPICAL_FISH,
        EntityType.DOLPHIN,
        EntityType.PANDA,
        EntityType.CAT,
        EntityType.FOX,
        EntityType.BEE,
        EntityType.STRIDER,
        EntityType.AXOLOTL,
        EntityType.GOAT,
        EntityType.GLOW_SQUID,
        EntityType.ALLAY,
        EntityType.FROG,
        EntityType.TADPOLE,
        EntityType.CAMEL,
        EntityType.SNIFFER,
        
        // Neutral mobs
        EntityType.IRON_GOLEM,
        EntityType.SNOW_GOLEM
    );
    
    public MobSelectionGui(GuiManager guiManager, EasyMobLootTable plugin) {
        this.guiManager = guiManager;
        this.plugin = plugin;
    }
    
    public void open(Player player) {
        Inventory gui = Bukkit.createInventory(null, 54, "§6§lChoisir un mob");
        
        int slot = 0;
        for (EntityType mobType : ALL_MOBS) {
            if (slot >= 45) break; // Leave space for navigation buttons
            
            Material iconMaterial = getMobIcon(mobType);
            ItemStack mobItem = new ItemStack(iconMaterial);
            ItemMeta meta = mobItem.getItemMeta();
            
            String mobName = getMobDisplayName(mobType);
            meta.setDisplayName("§e§l" + mobName);
            meta.setLore(Arrays.asList(
                "§7Type: §f" + mobType.name(),
                "§7Cliquez pour créer une loot table",
                "§7pour ce mob"
            ));
            
            mobItem.setItemMeta(meta);
            gui.setItem(slot, mobItem);
            slot++;
        }
        
        // Back button
        ItemStack back = new ItemStack(Material.ARROW);
        ItemMeta backMeta = back.getItemMeta();
        backMeta.setDisplayName("§c§lRetour au menu principal");
        back.setItemMeta(backMeta);
        gui.setItem(49, back);
        
        // Close button
        ItemStack close = new ItemStack(Material.BARRIER);
        ItemMeta closeMeta = close.getItemMeta();
        closeMeta.setDisplayName("§c§lFermer");
        close.setItemMeta(closeMeta);
        gui.setItem(53, close);
        
        player.openInventory(gui);
        guiManager.setGuiType(player, GuiManager.GuiType.MOB_SELECTION);
    }
    
    public void handleClick(Player player, ItemStack clickedItem) {
        if (clickedItem.getType() == Material.ARROW) {
            // Back to main menu
            guiManager.openMainMenu(player);
            return;
        }
        
        if (clickedItem.getType() == Material.BARRIER) {
            // Close GUI
            player.closeInventory();
            return;
        }
        
        // Check if clicked item represents a mob
        ItemMeta meta = clickedItem.getItemMeta();
        if (meta != null && meta.hasLore()) {
            List<String> lore = meta.getLore();
            if (lore.size() >= 1) {
                String typeLine = lore.get(0);
                if (typeLine.startsWith("§7Type: §f")) {
                    String mobTypeName = typeLine.substring(10); // Remove "§7Type: §f"
                    try {
                        EntityType mobType = EntityType.valueOf(mobTypeName);
                        // Store selected mob and open loot table editor
                        guiManager.setGuiData(player, mobType);
                        LootTableEditorGui editorGui = new LootTableEditorGui(guiManager, plugin);
                        editorGui.open(player, mobType);
                    } catch (IllegalArgumentException e) {
                        player.sendMessage("§cErreur: Type de mob invalide!");
                    }
                }
            }
        }
    }
    
    private Material getMobIcon(EntityType mobType) {
        switch (mobType) {
            // Hostile mobs
            case ZOMBIE:
                return Material.ZOMBIE_SPAWN_EGG;
            case SKELETON:
                return Material.SKELETON_SPAWN_EGG;
            case CREEPER:
                return Material.CREEPER_SPAWN_EGG;
            case SPIDER:
                return Material.SPIDER_SPAWN_EGG;
            case ENDERMAN:
                return Material.ENDERMAN_SPAWN_EGG;
            case WITCH:
                return Material.WITCH_SPAWN_EGG;
            case ZOMBIE_VILLAGER:
                return Material.ZOMBIE_VILLAGER_SPAWN_EGG;
            case HUSK:
                return Material.HUSK_SPAWN_EGG;
            case STRAY:
                return Material.STRAY_SPAWN_EGG;
            case WITHER_SKELETON:
                return Material.WITHER_SKELETON_SPAWN_EGG;
            case BLAZE:
                return Material.BLAZE_SPAWN_EGG;
            case GHAST:
                return Material.GHAST_SPAWN_EGG;
            case MAGMA_CUBE:
                return Material.MAGMA_CUBE_SPAWN_EGG;
            case SLIME:
                return Material.SLIME_SPAWN_EGG;
            case SILVERFISH:
                return Material.SILVERFISH_SPAWN_EGG;
            case CAVE_SPIDER:
                return Material.CAVE_SPIDER_SPAWN_EGG;
            case DROWNED:
                return Material.DROWNED_SPAWN_EGG;
            case PHANTOM:
                return Material.PHANTOM_SPAWN_EGG;
            case PILLAGER:
                return Material.PILLAGER_SPAWN_EGG;
            case VINDICATOR:
                return Material.VINDICATOR_SPAWN_EGG;
            case EVOKER:
                return Material.EVOKER_SPAWN_EGG;
            case RAVAGER:
                return Material.RAVAGER_SPAWN_EGG;
            case VEX:
                return Material.VEX_SPAWN_EGG;
            case GUARDIAN:
                return Material.GUARDIAN_SPAWN_EGG;
            case ELDER_GUARDIAN:
                return Material.ELDER_GUARDIAN_SPAWN_EGG;
            case SHULKER:
                return Material.SHULKER_SPAWN_EGG;
            case ENDERMITE:
                return Material.ENDERMITE_SPAWN_EGG;
            case WITHER:
                return Material.WITHER_SPAWN_EGG;
            case ENDER_DRAGON:
                return Material.ENDER_DRAGON_SPAWN_EGG;
            case ZOMBIFIED_PIGLIN:
                return Material.ZOMBIFIED_PIGLIN_SPAWN_EGG;
            case PIGLIN:
                return Material.PIGLIN_SPAWN_EGG;
            case PIGLIN_BRUTE:
                return Material.PIGLIN_BRUTE_SPAWN_EGG;
            case HOGLIN:
                return Material.HOGLIN_SPAWN_EGG;
            case ZOGLIN:
                return Material.ZOGLIN_SPAWN_EGG;
            case WARDEN:
                return Material.WARDEN_SPAWN_EGG;
            
            // Passive mobs
            case COW:
                return Material.COW_SPAWN_EGG;
            case PIG:
                return Material.PIG_SPAWN_EGG;
            case SHEEP:
                return Material.SHEEP_SPAWN_EGG;
            case CHICKEN:
                return Material.CHICKEN_SPAWN_EGG;
            case HORSE:
                return Material.HORSE_SPAWN_EGG;
            case DONKEY:
                return Material.DONKEY_SPAWN_EGG;
            case MULE:
                return Material.MULE_SPAWN_EGG;
            case LLAMA:
                return Material.LLAMA_SPAWN_EGG;
            case VILLAGER:
                return Material.VILLAGER_SPAWN_EGG;
            case SQUID:
                return Material.SQUID_SPAWN_EGG;
            case BAT:
                return Material.BAT_SPAWN_EGG;
            case OCELOT:
                return Material.OCELOT_SPAWN_EGG;
            case WOLF:
                return Material.WOLF_SPAWN_EGG;
            case MOOSHROOM:
                return Material.MOOSHROOM_SPAWN_EGG;
            case RABBIT:
                return Material.RABBIT_SPAWN_EGG;
            case POLAR_BEAR:
                return Material.POLAR_BEAR_SPAWN_EGG;
            case PARROT:
                return Material.PARROT_SPAWN_EGG;
            case TURTLE:
                return Material.TURTLE_SPAWN_EGG;
            case COD:
                return Material.COD_SPAWN_EGG;
            case SALMON:
                return Material.SALMON_SPAWN_EGG;
            case PUFFERFISH:
                return Material.PUFFERFISH_SPAWN_EGG;
            case TROPICAL_FISH:
                return Material.TROPICAL_FISH_SPAWN_EGG;
            case DOLPHIN:
                return Material.DOLPHIN_SPAWN_EGG;
            case PANDA:
                return Material.PANDA_SPAWN_EGG;
            case CAT:
                return Material.CAT_SPAWN_EGG;
            case FOX:
                return Material.FOX_SPAWN_EGG;
            case BEE:
                return Material.BEE_SPAWN_EGG;
            case STRIDER:
                return Material.STRIDER_SPAWN_EGG;
            case AXOLOTL:
                return Material.AXOLOTL_SPAWN_EGG;
            case GOAT:
                return Material.GOAT_SPAWN_EGG;
            case GLOW_SQUID:
                return Material.GLOW_SQUID_SPAWN_EGG;
            case ALLAY:
                return Material.ALLAY_SPAWN_EGG;
            case FROG:
                return Material.FROG_SPAWN_EGG;
            case TADPOLE:
                return Material.TADPOLE_SPAWN_EGG;
            case CAMEL:
                return Material.CAMEL_SPAWN_EGG;
            case SNIFFER:
                return Material.SNIFFER_SPAWN_EGG;
            
            // Neutral mobs
            case IRON_GOLEM:
                return Material.IRON_GOLEM_SPAWN_EGG;
            case SNOW_GOLEM:
                return Material.SNOW_GOLEM_SPAWN_EGG;
            
            default:
                return Material.EGG;
        }
    }
    
    private String getMobDisplayName(EntityType mobType) {
        switch (mobType) {
            case ZOMBIE:
                return "Zombie";
            case SKELETON:
                return "Squelette";
            case CREEPER:
                return "Creeper";
            case SPIDER:
                return "Araignée";
            case ENDERMAN:
                return "Enderman";
            case WITCH:
                return "Sorcière";
            case ZOMBIE_VILLAGER:
                return "Zombie Villageois";
            case HUSK:
                return "Zombie Momifié";
            case STRAY:
                return "Squelette Polaire";
            case WITHER_SKELETON:
                return "Squelette Wither";
            case BLAZE:
                return "Blaze";
            case GHAST:
                return "Ghast";
            case MAGMA_CUBE:
                return "Cube de Magma";
            case SLIME:
                return "Slime";
            case SILVERFISH:
                return "Poisson d'Argent";
            case CAVE_SPIDER:
                return "Araignée des Cavernes";
            case DROWNED:
                return "Noyé";
            case PHANTOM:
                return "Fantôme";
            case PILLAGER:
                return "Pillard";
            case VINDICATOR:
                return "Vindicateur";
            case EVOKER:
                return "Évocateur";
            case RAVAGER:
                return "Ravageur";
            case VEX:
                return "Vex";
            case GUARDIAN:
                return "Gardien";
            case ELDER_GUARDIAN:
                return "Gardien Ancien";
            case SHULKER:
                return "Shulker";
            case ENDERMITE:
                return "Endermite";
            default:
                return mobType.name();
        }
    }
}