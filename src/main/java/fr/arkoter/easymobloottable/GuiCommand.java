package fr.arkoter.easymobloottable;

import fr.arkoter.easymobloottable.gui.GuiManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GuiCommand implements CommandExecutor {
    
    private final GuiManager guiManager;
    
    public GuiCommand(GuiManager guiManager) {
        this.guiManager = guiManager;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cCette commande ne peut être exécutée que par un joueur!");
            return true;
        }
        
        Player player = (Player) sender;
        
        if (!player.hasPermission("easymobloottable.admin")) {
            player.sendMessage("§cVous n'avez pas la permission d'utiliser cette commande!");
            return true;
        }
        
        // Open the main GUI menu
        guiManager.openMainMenu(player);
        return true;
    }
}