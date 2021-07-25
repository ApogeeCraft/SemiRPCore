package fr.apogeecraft.commands.protection.claim;

import fr.apogeecraft.Core;
import fr.apogeecraft.database.SqlConnection;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ClaimProtect implements CommandExecutor {
    private final Core plugin;
    private final SqlConnection sql;

    public ClaimProtect(Core plugin){
        this.plugin = plugin;
        this.sql = plugin.getSqlConnection();
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if(!(commandSender instanceof Player)){
            Bukkit.getConsoleSender().sendMessage(plugin.chatColor("&cVous devez être un joueur"));
            return true;
        }
        Player p = (Player) commandSender;
        if(args.length == 0){
            plugin.getInventoryManager().openClaimInventory(p);
            return true;
        }
        if(args.length == 1){
            System.out.println(plugin.getPlayerManager().hasInvited(p.getName()));

            if(args[0].equals("accept")){
                if(plugin.getPlayerManager().hasInvited(p.getName())){
                    sql.addMemberToClaim(p.getName(), plugin.getPlayerManager().getPlayerClaimToAdd(p.getName()));
                    plugin.getPlayerManager().removePlayerClaimToAdd(p.getName());
                    p.sendMessage(plugin.chatColor("&aVous avez rejoint ce claim !"));
                    plugin.getPlayerManager().removePlayerToAdd(p.getName());

                }else{
                    p.sendMessage(plugin.chatColor("&cVous n'avez aucune invitation"));
                }
                return true;
            }
            if(args[0].equals("refuse")){
                if(plugin.getPlayerManager().hasInvited(p.getName())){
                    plugin.getPlayerManager().removePlayerClaimToAdd(p.getName());
                    p.sendMessage(plugin.chatColor("&cVous avez refuse l'invitation !"));
                    plugin.getPlayerManager().removePlayerToAdd(p.getName());
                }else{
                    p.sendMessage(plugin.chatColor("&cVous n'avez aucune invitation"));
                }
                return true;
            }
        }
    /*  if(args.length == 1){
            if(args[0].equals("remove")){
                if(sql.getClaimName(p.getLocation().getChunk()).isEmpty()){
                    p.sendMessage(plugin.chatColor("&cCette zone n'appartiens à personne!"));
                    return true;
                }

                else if(sql.playerIsOwnerOfClaim(p,p.getLocation().getChunk())){
                    sql.removeClaim(p.getLocation().getChunk());
                    p.sendMessage(plugin.chatColor("&aVous venez de supprimé ce claim"));
                    return true;
                }else{
                    p.sendMessage(plugin.chatColor("&cVous n'etes pas le propriétaire du claim"));
                    return true;
                }

            }
            if(sql.getClaimName(p.getLocation().getChunk()).isEmpty()){
                p.sendMessage(plugin.chatColor("&aVous venez de claim ici "));
                sql.addClaim(p,args[0],p.getLocation().getChunk());
                return true;
            }
            else{
                p.sendMessage(plugin.chatColor("&cVous ne pouvez pas claim ce chunk il est deja pris"));
                return true;
            }
        }*/
        return true;
    }
}
