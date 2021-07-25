package fr.apogeecraft.commands.others;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class skull implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if (!(commandSender instanceof Player)) return true;
        Player p = (Player) commandSender;

        if(args.length > 1){
            return true;
        }

        p.performCommand("give @p minecraft:player_head{SkullOwner:\""+args[0]+"\"} 1");

        return true;
    }
}
