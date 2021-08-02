package fr.apogeecraft.manager.authentification;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AuthManager {
    public AuthManager(){
        for(Player p : Bukkit.getOnlinePlayers()){
            oldLocation.put(p,p.getLocation());
            p.teleport(getSpawnLocation());
        }

    }

    private final Location spawn = new Location(Bukkit.getWorld("semiRPWorldSpawn"), -248.5, 77, -1172.5, 0, 0);
    private final Location spawn2 = new Location(Bukkit.getWorld("semiRPWorldSpawn"), -248.5, 77, -1172.5, 180, 0);

    private final List<String> loggedIn = new ArrayList<>();

    private final HashMap<Player, Location> oldLocation = new HashMap<>();

    public boolean isLoggedIn(Player p){
        return loggedIn.contains(p.getName());
    }

    public void logPlayer(Player p){
        loggedIn.add(p.getName());
    }

    public void logoutPlayer(Player p){
        if(isLoggedIn(p)) loggedIn.remove(p.getName());
    }

    public void addPlayerToOldLocation(Player p){
        oldLocation.put(p,p.getLocation());
    }

    public Location getPlayerOldLocation(Player p){
        Location l = oldLocation.get(p);
        oldLocation.remove(l);
        return l;
    }

    public Location getSpawnLocation(){
        int i = (int) (Math.random() * 20 - 0 + 1);;
        if(i>=10)return spawn;
        return spawn2;
    }

}
