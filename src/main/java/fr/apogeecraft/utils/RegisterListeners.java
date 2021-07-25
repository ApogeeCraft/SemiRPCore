package fr.apogeecraft.utils;

import fr.apogeecraft.Core;
import fr.apogeecraft.listeners.auth.AuthentificationOnJoin;
import fr.apogeecraft.listeners.auth.AuthentificationOnLeave;
import fr.apogeecraft.listeners.auth.ProtectionListener;
import fr.apogeecraft.listeners.claim.ClaimProtection;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;

public class RegisterListeners {
    private Core plugin;

    public RegisterListeners(Core plugin){
        this.plugin = plugin;
        registerListeners();
    }

    private void registerListeners(){
        registerListener(new AuthentificationOnJoin(plugin));
        registerListener(new AuthentificationOnLeave(plugin));
        registerListener(new ProtectionListener(plugin));
        registerListener(new ClaimProtection(plugin));
    }

    private void registerListener(Listener listener){
        Bukkit.getPluginManager().registerEvents(listener, plugin);
    }
}
