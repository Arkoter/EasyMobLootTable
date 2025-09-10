# EasyMobLootTable

A Minecraft plugin that allows server administrators to easily customize mob loot tables through simple in-game commands.

## 🚀 Features

- **Simple Commands**: Manage mob loot tables without editing configuration files
- **Custom Loot**: Define what items mobs drop and in what quantities
- **Easy Management**: Add or remove custom loot tables with simple commands
- **Permission-based**: Secure command access with permission nodes
- **Persistent Storage**: Configuration automatically saved and loaded

## 📋 Requirements

- **Minecraft Version**: 1.20.x | 1.21.x
- **Server Software**: Spigot/Paper
- **Java Version**: Java 8 or higher

## 📦 Installation

1. Download the latest `EasyMobLootTable-1.0.0.jar` from the releases
2. Place the JAR file in your server's `plugins` folder
3. Restart your server
4. The plugin will create a default configuration file automatically

## 🎮 Commands

### Add Custom Loot
```
/mobloot <mob> <item> <min_quantity> <max_quantity>
```
**Example**: `/mobloot zombie diamond 1 3`
- Makes zombies drop between 1 and 3 diamonds

### Remove Custom Loot
```
/mobloot remove <mob>
```
**Example**: `/mobloot remove zombie`
- Removes all custom loot for zombies (reverts to default Minecraft loot)

### Help
```
/mobloot
```
- Shows all available commands and usage

## 🔐 Permissions

| Permission | Description | Default |
|------------|-------------|---------|
| `easymobloottable.admin` | Allows access to all EasyMobLootTable commands | OP |

## 📖 Usage Examples

### Basic Examples
```bash
# Make skeletons drop gold ingots (2-5 per kill)
/mobloot skeleton gold_ingot 2 5

# Make creepers drop TNT (1 per kill)
/mobloot creeper tnt 1 1

# Make zombies drop emeralds (1-2 per kill)
/mobloot zombie emerald 1 2

# Remove custom loot from skeletons
/mobloot remove skeleton
```

### Advanced Configuration
You can also manually edit the `config.yml` file for more complex setups:

```yaml
mobs:
  ZOMBIE:
    items:
      DIAMOND:
        min: 1
        max: 2
      EMERALD:
        min: 1
        max: 1
  SKELETON:
    items:
      GOLD_INGOT:
        min: 2
        max: 5
```

## ⚙️ Configuration

The plugin creates a `config.yml` file in the `plugins/EasyMobLootTable/` directory. This file stores all custom loot tables and can be edited manually if needed.

**Note**: When you configure custom loot for a mob, it completely replaces the default Minecraft loot table for that mob. Use `/mobloot remove <mob>` to restore default behavior.

## 🎯 Use Cases

- **RPG Servers**: Create unique loot experiences
- **Modified Survival**: Add rare materials to common mobs  
- **Special Events**: Temporary loot modifications for events
- **Economy Servers**: Control resource availability
- **Custom Gameplay**: Create unique server experiences

## 🛠️ Building from Source

1. Clone this repository
2. Ensure you have Maven installed
3. Run `mvn clean package`
4. The compiled JAR will be in the `target/` directory

## 📝 Valid Mob Types

Use Minecraft's entity names (case-insensitive):
- `ZOMBIE`, `SKELETON`, `CREEPER`, `SPIDER`, `ENDERMAN`
- `WITCH`, `PILLAGER`, `VINDICATOR`, `EVOKER`
- `BLAZE`, `GHAST`, `MAGMA_CUBE`, `SLIME`
- And many more...

## 📝 Valid Item Types

Use Minecraft's material names (case-insensitive):
- `DIAMOND`, `EMERALD`, `GOLD_INGOT`, `IRON_INGOT`
- `TNT`, `REDSTONE`, `COAL`, `WHEAT`
- `ENCHANTED_BOOK`, `GOLDEN_APPLE`
- And all other Minecraft items...

## 👥 Author

**Arkoter** - Plugin Developer

## 📄 License

This project is open source. Feel free to modify and distribute according to your needs.

## 🐛 Bug Reports & Feature Requests

If you encounter any issues or have suggestions for new features, please create an issue on this GitHub repository.

## 🔄 Version History

- **v1.0.0** - Initial release
  - Basic mob loot customization
  - Add and remove commands
  - Configuration file support