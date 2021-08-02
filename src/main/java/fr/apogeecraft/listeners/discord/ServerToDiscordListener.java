package fr.apogeecraft.listeners.discord;

import fr.apogeecraft.Core;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Objects;

public class ServerToDiscordListener implements Listener {
    private Core plugin;
    public ServerToDiscordListener(Core plugin){
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e){
        Objects.requireNonNull(plugin.getJda().getTextChannelById("856148124547547146")).sendMessage("```+ "+e.getPlayer().getName()+"```").queue();
    }
    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent e){
        int i =Bukkit.getServer().getOnlinePlayers().size()-1;
        Objects.requireNonNull(plugin.getJda().getTextChannelById("856148124547547146")).sendMessage("```- "+e.getPlayer().getName()+"```").queue();
    }
}
