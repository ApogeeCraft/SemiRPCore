package fr.apogeecraft.listeners.auth;

import fr.apogeecraft.Core;
import fr.apogeecraft.database.SqlConnection;
import fr.apogeecraft.manager.authentification.AuthManager;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerResourcePackStatusEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class AuthentificationOnJoin implements Listener {
    private Core plugin;
    private SqlConnection sql;
    private AuthManager authManager;

    public AuthentificationOnJoin(Core plugin){
        this.plugin = plugin;
        this.sql = this.plugin.getSqlConnection();
        this.authManager = this.plugin.getAuthManager();
    }

    @EventHandler
    private void onTexturePackChange(PlayerResourcePackStatusEvent e){
        System.out.println(e.getStatus());
        if(e.getStatus().equals(PlayerResourcePackStatusEvent.Status.ACCEPTED))
            return;
        if(e.getStatus().equals(PlayerResourcePackStatusEvent.Status.SUCCESSFULLY_LOADED))
            return;

        if(e.getStatus().equals(PlayerResourcePackStatusEvent.Status.DECLINED)){
            e.getPlayer().kickPlayer(plugin.chatColor("Vous devez avoir le texture Packs."));
            return;
        }
        if(e.getStatus().equals(PlayerResourcePackStatusEvent.Status.FAILED_DOWNLOAD)){
            e.getPlayer().kickPlayer(plugin.chatColor("Vous n'avez pas réussi a téléchargé le texture pack"));
        }
    }

    @EventHandler
    public void onJoinEvent(PlayerJoinEvent e){
        sql.reActive();
        e.getPlayer().setResourcePack("https://apogeecraft.fr/storage/img/apogeecraftV0.0.4.zip");
        e.setJoinMessage("");
        Player p = e.getPlayer();
        authManager.addPlayerToOldLocation(p);
        p.teleport(authManager.getSpawnLocation());

        if(!sql.hasAccount(p)) p.sendMessage(chatColor("&eTu dois t'enregistrer avec &f&l/register <password> <password>"));
        else p.sendMessage(chatColor("&eTu dois te connecter avec &f&l/login <password>"));
        p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 1000000,1));
    }

    @EventHandler
    public void onMoveEvent(PlayerMoveEvent e){
        if(!authManager.isLoggedIn(e.getPlayer())){
            e.setCancelled(true);
            Player p = e.getPlayer();
            Block b = p.getWorld().getHighestBlockAt(p.getLocation());
            p.teleport(b.getLocation());
        }
    }


    private String chatColor(String text){ return plugin.chatColor(text); }
}
