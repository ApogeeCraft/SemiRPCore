package fr.apogeecraft.commands.protection.chest;

import fr.apogeecraft.Core;
import fr.apogeecraft.database.SqlConnection;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ChestProtect implements CommandExecutor {
    private final Core plugin;
    private final SqlConnection sql;

    public ChestProtect(Core plugin){
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
        Block b = p.getTargetBlock(null,5);


        if(args.length == 0){
            if(!(b.getType().equals(Material.CHEST))) {
                p.sendMessage(plugin.chatColor("&cVous devez regarder un coffre a moins de 5 blocs\npour pouvoir accéder a cette commande !"));
                return true;
            }
            if(sql.playerIsOwnerOfChest(p, b.getLocation())) {
                p.sendMessage(
                        plugin.chatColor(
                                "&aVous êtes le propriétaire \n" +
                                        "&aVous pouvez ajouter un membre en faisant &f&l/chest add <player>\n"
                        )
                );
                sql.getMembersList(p,p.getTargetBlock(null,5).getLocation());
            }
            else p.sendMessage(plugin.chatColor("#960000V#9e0000o#a60000u#af0000s #b70000n#c00000'#c80000ê#d00000t#d90000e#e10000s #ea0000p#f20000a#fa0000s p#f00000r#e60000o#dc0000p#d20000r#c80000i#bf0000é#b50000t#ab0000a#a10000i#970000r#8d0000e #840000!"));
        }
        if(args.length == 2){
            if(sql.playerIsOwnerOfChest(p, b.getLocation())) {
                if (args[0].equals("add")) {
                    if (Bukkit.getOfflinePlayer(args[1]).hasPlayedBefore()) {
                        sql.addMemberToChest(p, args[1], p.getTargetBlock(null, 5).getLocation());
                    } else
                        p.sendMessage(plugin.chatColor("&cLe joueur &f&l" + args[1] + " &cn'a jamais jouer sur le serveur."));
                }
                if (args[0].equals("remove")) {
                    if (Bukkit.getOfflinePlayer(args[1]).hasPlayedBefore()) {
                        sql.removeMemberOfChest(p,args[1], p.getTargetBlock(null, 5).getLocation());
                    } else
                        p.sendMessage(plugin.chatColor("&cLe joueur &f&l" + args[1] + " &cn'a jamais jouer sur le serveur."));
                }
            }
        }

        if(args.length == 2){

        }
        return true;
    }
}
