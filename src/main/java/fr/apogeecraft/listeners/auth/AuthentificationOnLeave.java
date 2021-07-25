package fr.apogeecraft.listeners.auth;

import fr.apogeecraft.Core;
import fr.apogeecraft.manager.authentification.AuthManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;

public class AuthentificationOnLeave implements Listener {
    private Core plugin;
    private AuthManager authManager;

    public AuthentificationOnLeave(Core plugin){
        this.plugin = plugin;
        this.authManager = this.plugin.getAuthManager();
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent e){
        e.setQuitMessage("");
        authManager.logoutPlayer(e.getPlayer());
    }



}
