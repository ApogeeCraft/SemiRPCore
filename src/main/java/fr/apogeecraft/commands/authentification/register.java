package fr.apogeecraft.commands.authentification;

import fr.apogeecraft.Core;
import fr.apogeecraft.database.SqlConnection;
import fr.apogeecraft.manager.authentification.AuthManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

import java.util.Base64;

public class register implements CommandExecutor {
    private Core plugin;
    private SqlConnection sql;
    private AuthManager authManager;

    public register(Core plugin){
        this.plugin = plugin;
        this.sql = this.plugin.getSqlConnection();
        this.authManager = this.plugin.getAuthManager();
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if(!(commandSender instanceof Player)) return true;
        Player p = (Player) commandSender;

        if(authManager.isLoggedIn(p)) {
            p.sendMessage(plugin.chatColor("&cVous êtes déjà &lconnecté."));
            return true;
        }
        if(!sql.isConnected()) sql.connection();

        if(sql.hasAccount(p)) {
            p.sendMessage(plugin.chatColor("&cVous avez déjà un &lcompte !"));
            return true;
        }

        if(args.length != 2){
            p.sendMessage(plugin.chatColor("&cVous devez utiliser /register <password> <password>"));
            return true;
        }

        if(!(args[0].length() > 5 && args[0].length() < 15)){
            p.sendMessage(plugin.chatColor("&cVotre mot de passe &f&ldoit contenir au minimum 5 caractères"));
            p.sendMessage(plugin.chatColor("&cet &f&lau maximum 15 caratères&r&c."));
            return true;
        }

        if(!(args[0].equals(args[1]))){
            p.sendMessage(plugin.chatColor("&cVos deux mot de passes &f&lne correspondent pas&r&c."));
            return true;
        }


        String password = Base64.getEncoder().encodeToString(args[0].getBytes());
        sql.createUsers(p, password);
        p.sendMessage(plugin.chatColor("&aVous êtes désormais &lconnecté&r&a!"));
        p.removePotionEffect(PotionEffectType.BLINDNESS);
        authManager.logPlayer(p);
        return true;
    }
}
