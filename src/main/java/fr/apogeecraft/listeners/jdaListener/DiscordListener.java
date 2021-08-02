package fr.apogeecraft.listeners.jdaListener;

import fr.apogeecraft.Core;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.ErrorResponse;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;


public class DiscordListener extends ListenerAdapter {
    private final Core plugin;

    public DiscordListener(Core plugin){
        this.plugin = plugin;

    }



    @Override
    public void onReady(ReadyEvent e){
        System.out.println("Le bot est connecté");
        e.getJDA().getPresence().setPresence(Activity.playing("apogeecraft.fr"),true);


        super.onReady(e);
    }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent e){
        if(e.getAuthor().isBot())return;
        if(e.getGuild() == null) return;
        super.onMessageReceived(e);
    }
}
