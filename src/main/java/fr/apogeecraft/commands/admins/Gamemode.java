package fr.apogeecraft.commands.admins;

import fr.apogeecraft.Core;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public class Gamemode implements CommandExecutor {

    private final Core plugin;

    public Gamemode(Core Plugin){ this.plugin = Plugin; }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!(sender instanceof Player)) {
            return true;
        }
        Player p = (Player) sender;

        if(args.length == 0){
            p.sendMessage(ChatColor.RED+"Vous avez oubli\u00e9 votre gamemode");
            return true;
        }
        if(args.length == 2 ){
            Player target = Bukkit.getPlayer(args[1]);

            if(target != null) changeGamemode(target, args);
            else p.sendMessage(ChatColor.RED+"Le joueur n'est pas reconnu");
            return true;
        }
        if(args.length==1) changeGamemode(p, args);
        return true;
    }


    private void changeGamemode(Player p , String[] args){
        String gamemode;
        if(args[0].equalsIgnoreCase("1") || args[0].equalsIgnoreCase("c"))
            if (p.getGameMode() != GameMode.CREATIVE){
                p.setGameMode(GameMode.CREATIVE);
                p.sendMessage(ChatColor.GOLD+"Votre mode de jeu \u00e0 \u00e9t\u00e9 modifi\u00e9 !");

                gamemode = "GAMEMODE CREATIF";

            }

            else p.sendMessage(ChatColor.RED+"Vous \u00eates d\u00e9j\u00e0 en gamemode"+ ChatColor.WHITE+" cr\u00e9atif");

        else if(args[0].equalsIgnoreCase("0") || args[0].equalsIgnoreCase("s"))
            if (p.getGameMode() != GameMode.SURVIVAL){
                p.setGameMode(GameMode.SURVIVAL);
                p.sendMessage(ChatColor.GOLD+"Votre mode de jeu \u00e0 \u00e9t\u00e9 modifi\u00e9 !");

                gamemode = "GAMEMODE SURVIVAL";
            }
            else p.sendMessage(ChatColor.RED+"Vous \u00eates d\u00e9j\u00e0 en gamemode"+ ChatColor.WHITE+" survie");

        else if(args[0].equalsIgnoreCase("3") || args[0].equalsIgnoreCase("spec")) {
            if (p.getGameMode() != GameMode.SPECTATOR) {
                p.setGameMode(GameMode.SPECTATOR);
                p.sendMessage(ChatColor.GOLD + "Votre mode de jeu \u00e0 \u00e9t\u00e9 modifi\u00e9 !");

                gamemode = "GAMEMODE SPECTATEUR";
            } else
                p.sendMessage(ChatColor.RED + "Vous \u00eates d\u00e9j\u00e0 en gamemode" + ChatColor.WHITE + " spectateur");
        }
        else p.sendMessage(ChatColor.RED+"tu utilise un truc de merde");
    }
}