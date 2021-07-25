package fr.apogeecraft.commands.admins;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Day implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!(sender instanceof Player)) return true;
        Player p = (Player) sender;
        p.getWorld().setTime(0);
        p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aVous avez changé l'heure au jour !"));
        return true;
    }
}
