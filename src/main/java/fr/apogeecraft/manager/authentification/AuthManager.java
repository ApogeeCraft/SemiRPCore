package fr.apogeecraft.manager.authentification;

import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class AuthManager {
    List<String> loggedIn = new ArrayList<>();

    public boolean isLoggedIn(Player p){
        return loggedIn.contains(p.getName());
    }

    public void logPlayer(Player p){
        loggedIn.add(p.getName());
    }

    public void logoutPlayer(Player p){
        if(isLoggedIn(p)) loggedIn.remove(p.getName());
    }

}
