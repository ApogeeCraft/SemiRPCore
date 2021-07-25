package fr.apogeecraft.commands.authentification;

import fr.apogeecraft.Core;
import fr.apogeecraft.database.SqlConnection;
import fr.apogeecraft.manager.authentification.AuthManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

public class login implements CommandExecutor {
    private Core plugin;
    private SqlConnection sql;
    private AuthManager authManager;

    public login(Core plugin){
        this.plugin = plugin;
        this.sql = this.plugin.getSqlConnection();
        this.authManager = this.plugin.getAuthManager();
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if (!(commandSender instanceof Player)) return true;
        Player p = (Player) commandSender;

        if (authManager.isLoggedIn(p)) {
            p.sendMessage(plugin.chatColor("&cVous êtes déjà &lconnecté."));
            return true;
        }
        if(!sql.isConnected()) sql.connection();

        if (!(sql.hasAccount(p))) {
            p.sendMessage(plugin.chatColor("&cVous devez vous register avec /register <password> <password>"));
            return true;
        }

        if (args.length != 1) {
            p.sendMessage(plugin.chatColor("&cVous devez vous login avec /login <password>"));
            return true;
        }

        if (sql.checkPasswordUsers(p, args[0])) {
            authManager.logPlayer(p);
            p.sendMessage(plugin.chatColor("&aVous êtes désormais connecté!"));
            p.removePotionEffect(PotionEffectType.BLINDNESS);
            return true;
        }else{
            p.sendMessage(plugin.chatColor("&cMauvais mot de passe."));
        }
        return true;
    }
}
