package fr.apogeecraft.commands.admins;

import fr.apogeecraft.Core;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.text.NumberFormat;
import java.text.ParsePosition;

public class Speed implements CommandExecutor {
    private Core plugin;

    public Speed(Core plugin){
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if(!(commandSender instanceof Player))return true;

        Player p =(Player) commandSender;
        if(args.length == 0 || args.length > 2) {
            sendUsage(p);
            return true;
        }

        if(args.length == 1){
            if(!isNumeric(args[0])){
                sendUsage(p);
                return true;
            }
            p.setFlySpeed(Float.parseFloat(args[0]));
            p.setWalkSpeed(Float.parseFloat(args[0]));
            return true;
        }
        return true;
    }
    public static boolean isNumeric(String str) {
        ParsePosition pos = new ParsePosition(0);
        NumberFormat.getInstance().parse(str, pos);
        return str.length() == pos.getIndex();
    }

    private void sendUsage(Player p){
        p.sendMessage(plugin.chatColor("&cutilise /speed <number>[0-10]"));
    }
}
