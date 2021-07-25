package fr.apogeecraft.manager.claim;

import org.bukkit.Chunk;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlayerManager {

    private HashMap<String, Chunk> isClaiming = new HashMap<>();
    public void addPlayerToIsClaiming(String name, Chunk chunk){ if(!isClaiming.containsKey(name)) isClaiming.put(name, chunk); }
    public void removePlayerToIsClaiming(Player p){
        isClaiming.remove(p.getName());
    }
    public boolean isClaiming(Player p){ return isClaiming.containsKey(p.getName()); }

    private HashMap<String, Chunk> isRemoving = new HashMap<>();
    public void addPlayerToIsRemoving(String name, Chunk chunk){ if(!isRemoving.containsKey(name)) isRemoving.put(name, chunk); }
    public void removePlayerToIsRemoving(Player p){
        isRemoving.remove(p.getName());
    }
    public boolean isRemoving(String name){ return isRemoving.containsKey(name); }


//
    private HashMap<String, String> playerToKick = new HashMap<>();
    public void addPlayerToKick(String name, String target){ if(!playerToKick.containsKey(name)) playerToKick.put(name, target); }
    public void removePlayerToKick(Player p){
        playerToKick.remove(p.getName());
    }
    public String getPlayerToKick(Player p){ return playerToKick.get(p.getName()); }

    private List<String> isKickingFromClaim = new ArrayList<>();
    public void addPlayerToIsKickingFromClaim(String name){ if(!isKickingFromClaim.contains(name)) isKickingFromClaim.add(name); }
    public void removePlayerToIsKickingFromClaim(String name){
        isKickingFromClaim.remove(name);
    }
    public boolean isKickingFromClaim(String name){ return isKickingFromClaim.contains(name); }

//



    private HashMap<String, String> playerToAdd = new HashMap<>();
    public void addPlayerToAdd(String name, String target){ if(!playerToAdd.containsKey(name)) playerToAdd.put(name, target); }
    public void removePlayerToAdd(String name){
        for(Map.Entry<String, String> entry: playerToAdd.entrySet()) {
            if(entry.getValue().equals(name)) playerToAdd.remove(entry.getKey());
        }

    }

    public String getPlayerToAdd(Player p){ return playerToAdd.get(p.getName()); }

    public boolean canJoinClaim(String p, String target){
        for(Map.Entry<String, String> entry: playerToAdd.entrySet()) {
            if(entry.getValue().equals(p) && entry.getKey().equals(target)) return true;
        }
        return false;
    }
    public boolean hasInvited(String target){
        for(Map.Entry<String, String> entry: playerToAdd.entrySet()) {
            if(entry.getValue().equals(target)) return true;
        }
        return false;
    }
    public String getOwnerTeamInvite(String target){
        for(Map.Entry<String, String> entry: playerToAdd.entrySet()) {
            if(entry.getValue().equals(target)) return entry.getKey();
        }
        return "";
    }

    private HashMap<String, String> playerClaimToAdd = new HashMap<>();
    public void addPlayerClaimToAdd(String name, String claim){ playerClaimToAdd.put(name, claim); }
    public void removePlayerClaimToAdd(String name ){ playerClaimToAdd.remove(name); }
    public String getPlayerClaimToAdd(String target){ return playerClaimToAdd.get(target); }

    private List<String> isAddingToClaim = new ArrayList<>();
    public void addPlayerToIsAddingToClaim(String name){ if(!isAddingToClaim.contains(name)) isAddingToClaim.add(name); }
    public void removePlayerToIsAddingToClaim(String name){
        isAddingToClaim.remove(name);
    }
    public boolean isAddingToClaim(String name){ return isAddingToClaim.contains(name); }

}
