package fr.apogeecraft.commands.users;

import fr.apogeecraft.Core;
import fr.apogeecraft.database.SqlConnection;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Money implements CommandExecutor {

    private final Core plugin;
    private final SqlConnection sql;

    public Money(Core plugin){
        this.plugin = plugin;
        this.sql = plugin.getSqlConnection();
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if(!(commandSender instanceof Player)) return true;
        Player p = (Player) commandSender;
        if(!sql.hasAccount(p)){
            System.out.println("error");
            return true;
        }
        if(args.length>=2) {
            p.sendMessage(chatColor("&cVous devez utiliser &f&l/money"));
            return true;
        }
        if(args.length == 0){
            p.sendMessage(chatColor("&eVous avez &f&l"+sql.getMoneyPlayer(p.getName())+ plugin.getCurrency()+ "&r&e."));
        }

        if(args.length == 1 && p.hasPermission("money.other.use")){
            p.sendMessage(chatColor("&eLe joueur &f&l"+ args[0] +" &r&ea &f&l"+sql.getMoneyPlayer(args[0])+ plugin.getCurrency()+ "&r&e."));
        }


        return true;
    }

    private String chatColor(String text){
        return plugin.chatColor(text);
    }
}
