package fr.apogeecraft.listeners.auth;

import fr.apogeecraft.Core;
import fr.apogeecraft.database.SqlConnection;
import fr.apogeecraft.manager.authentification.AuthManager;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.cacheddata.CachedDataManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.OptionalInt;
public class ProtectionListener implements Listener {
    private Core plugin;
    private SqlConnection sql;
    private AuthManager authManager;
    private LuckPerms luckPermsProvider;

    public ProtectionListener(Core plugin){
        this.plugin = plugin;
        this.authManager = this.plugin.getAuthManager();
        this.sql = this.plugin.getSqlConnection();
        this.luckPermsProvider = this.plugin.getLuckPermsProvider();
    }

    @EventHandler
    private void onPlayerBreakBlock(BlockBreakEvent e){
        if(!e.getPlayer().hasPermission("interact.use") && authManager.isLoggedIn(e.getPlayer()))
            e.setCancelled(true);
    }
    @EventHandler
    private void onPlayerPlaceBlock(BlockPlaceEvent e){
        if(!e.getPlayer().hasPermission("interact.use") && authManager.isLoggedIn(e.getPlayer()))
            e.setCancelled(true);
    }
    @EventHandler
    private void onPlayerInteract(PlayerInteractEvent e){
        if(!e.getPlayer().hasPermission("interact.use") && authManager.isLoggedIn(e.getPlayer()))
            e.setCancelled(true);
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    private void onPlayerSendCommand(PlayerCommandPreprocessEvent e){
        if(!authManager.isLoggedIn(e.getPlayer())){
            String command = e.getMessage().split(" ")[0];
            if(!command.equals("/login") && !command.equals("/register")){
                if(sql.hasAccount(e.getPlayer()))e.getPlayer().sendMessage(chatColor("&cVous devez vous login avec /login <password>"));
                else e.getPlayer().sendMessage(chatColor("&cVous devez utiliser /register <password> <password>"));
                e.setCancelled(true);
            }
        }
    }
    private String chatColor(String text){
        return plugin.chatColor(text);
    }


    @EventHandler
    private void onPlayerChatting(AsyncPlayerChatEvent e){
        if(!authManager.isLoggedIn(e.getPlayer())){
            e.setCancelled(true);
            return;
        }
        Player p = e.getPlayer();


        CachedDataManager cache = luckPermsProvider.getUserManager().getUser(p.getName()).getCachedData();
        OptionalInt groupWeight = luckPermsProvider.getGroupManager().getGroup(cache.getMetaData().getPrimaryGroup()).getWeight();
        String prefix = cache.getMetaData().getPrefix();

        if(groupWeight.isPresent()){
            int newGroupWeight = groupWeight.getAsInt();
            if(newGroupWeight>=500){
                e.setFormat(chatColor(prefix+p.getName()+" &r: "+e.getMessage()));
                return;
            }
        }
        e.setFormat(chatColor(prefix+p.getName()+" &r: ")+e.getMessage());
    }
}
