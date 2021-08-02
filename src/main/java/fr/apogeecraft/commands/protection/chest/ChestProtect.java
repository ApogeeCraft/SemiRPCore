package fr.apogeecraft.commands.protection.chest;

import fr.apogeecraft.Core;
import fr.apogeecraft.database.SqlConnection;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.DoubleChest;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.DoubleChestInventory;

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

        if(!(b.getType().equals(Material.CHEST))) {
            p.sendMessage(plugin.chatColor("&cVous devez regarder un coffre a moins de 5 blocs\npour pouvoir accéder a cette commande !"));
            return true;
        }

        if(args.length == 0){
            if(sql.playerIsOwnerOfChest(p, b.getLocation())) {
                p.sendMessage(
                        plugin.chatColor(
                                "&aVous êtes le propriétaire \n" +
                                        "&aVous pouvez ajouter un membre en faisant &f&l/chest add <player>\n"
                        )
                );
                sql.getMembersList(p,p.getTargetBlock(null,5).getLocation());
                return true;
            }
            else if(!sql.chestIsPrivate(b.getLocation())){
                p.sendMessage(plugin.chatColor("&aVous pouvez privatiser ce coffre en faisant /chest private"));
                return true;
            }
            else{
                p.sendMessage(plugin.chatColor("&cCe coffre appartient à "+sql.getOwnerOfChest(b.getLocation())));
                return true;
            }
        }


        if(args.length == 1){
            if(args[0].equalsIgnoreCase("private")){
                if(sql.playerIsOwnerOfChest(p,b.getLocation())){
                    p.sendMessage(plugin.chatColor("&cVous êtes deja le propriétaire"));
                    return true;
                }
                if(sql.chestIsPrivate(b.getLocation())){
                    p.sendMessage(plugin.chatColor("&cCe coffre appartient à "+sql.getOwnerOfChest(b.getLocation())));
                    return true;
                }
                else{
                    Chest chest = (Chest) b.getState();
                    if(chest.getInventory() instanceof DoubleChestInventory){
                        checkAndAddPrivateChest(p, chest);
                    }else{
                        sql.createChest(p,b.getLocation());
                        p.sendMessage(plugin.chatColor("&aVous avez privatisé ce coffre !"));
                    }

                    return true;
                }

            }
            if(args[0].equalsIgnoreCase("unprivate")){
                if(sql.playerIsOwnerOfChest(p,b.getLocation())){
                    Chest chest = (Chest) b.getState();
                    if(chest.getInventory() instanceof DoubleChestInventory){
                        checkAndUnprivateChest(p, chest);
                    }else{
                        sql.removeChest(b.getLocation());
                        p.sendMessage(plugin.chatColor("&aVous avez unprivate ce coffre !"));
                    }

                    return true;

                }
                else if(sql.chestIsPrivate(b.getLocation())){
                    p.sendMessage(plugin.chatColor("&cCe coffre appartient à "+sql.getOwnerOfChest(b.getLocation())));
                    return true;
                }
                else{
                    p.sendMessage(plugin.chatColor("&cCe coffre appartient à personne"));
                    return true;
                }
            }
        }


        if(args.length == 2){
            if(sql.playerIsOwnerOfChest(p, b.getLocation())) {
                if (args[0].equals("add")) {
                    if(sql.playerIsMemberOfChest(args[1], b.getLocation())){
                        p.sendMessage(plugin.chatColor("&cCe joueur appartient deja aux membres de ce coffre"));
                        return true;
                    }
                    if (Bukkit.getOfflinePlayer(args[1]).hasPlayedBefore()) {
                        Chest chest = (Chest) b.getState();
                        if(chest.getInventory() instanceof DoubleChestInventory){
                            checkAndAddMemberToPrivateChest(p,args[1], chest);
                        }else{
                            sql.addMemberToChest(p, args[1], p.getTargetBlock(null, 5).getLocation());
                            p.sendMessage(plugin.chatColor("&aVous avez ajouté "+args[1]+" à ce coffre !"));
                        }
                        return true;

                    } else{
                        p.sendMessage(plugin.chatColor("&cLe joueur &f&l" + args[1] + " &cn'a jamais jouer sur le serveur."));
                        return true;
                    }
                }
                if (args[0].equals("remove")) {
                    if(sql.playerIsMemberOfChest(args[1], b.getLocation())){
                        Chest chest = (Chest) b.getState();
                        if(chest.getInventory() instanceof DoubleChestInventory){
                            checkAndRemoveMemberToPrivateChest(p,args[1], chest);
                        }else{
                            sql.removeMemberOfChest(p, args[1], p.getTargetBlock(null, 5).getLocation());
                            p.sendMessage(plugin.chatColor("&aCette personne n'as maintenant plus accès a votre coffre."));
                        }
                        return true;
                    }else {
                        p.sendMessage(plugin.chatColor("&cLe joueur &f&l" + args[1] + " &cn'est pas dans la liste des membres pouvant acceder au coffre."));
                    }
                }
            }
        }

        return true;
    }


    private void checkAndAddPrivateChest(Player p, Chest chest){
        if(chest.getInventory() instanceof DoubleChestInventory){
            DoubleChest doubleChest = ((DoubleChestInventory) chest.getInventory()).getHolder();
            Chest leftChest = (Chest) doubleChest.getLeftSide();
            Chest rightChest = (Chest) doubleChest.getRightSide();
            sql.createChest(p,leftChest.getLocation());
            sql.createChest(p,rightChest.getLocation());
            p.sendMessage(plugin.chatColor("&aVous avez privatisé votre double coffres"));
        }
    }

    private void checkAndUnprivateChest(Player p, Chest chest){
        if(chest.getInventory() instanceof DoubleChestInventory){
            DoubleChest doubleChest = ((DoubleChestInventory) chest.getInventory()).getHolder();
            Chest leftChest = (Chest) doubleChest.getLeftSide();
            Chest rightChest = (Chest) doubleChest.getRightSide();
            sql.removeChest(leftChest.getLocation());
            sql.removeChest(rightChest.getLocation());
            p.sendMessage(plugin.chatColor("&aVous avez unprivate votre double coffres"));
        }
    }

    private void checkAndAddMemberToPrivateChest(Player p,String target, Chest chest){
        if(chest.getInventory() instanceof DoubleChestInventory){
            DoubleChest doubleChest = ((DoubleChestInventory) chest.getInventory()).getHolder();
            Chest leftChest = (Chest) doubleChest.getLeftSide();
            Chest rightChest = (Chest) doubleChest.getRightSide();

            sql.addMemberToChest(p,target,leftChest.getLocation());
            sql.addMemberToChest(p,target,rightChest.getLocation());
            p.sendMessage(plugin.chatColor("&aVous avez ajouté "+target+" a votre double coffres!"));
        }
    }
    private void checkAndRemoveMemberToPrivateChest(Player p,String target, Chest chest){
        if(chest.getInventory() instanceof DoubleChestInventory){
            DoubleChest doubleChest = ((DoubleChestInventory) chest.getInventory()).getHolder();
            Chest leftChest = (Chest) doubleChest.getLeftSide();
            Chest rightChest = (Chest) doubleChest.getRightSide();

            sql.removeMemberOfChest(p,target,leftChest.getLocation());
            sql.removeMemberOfChest(p,target,rightChest.getLocation());
            p.sendMessage(plugin.chatColor("&aCette personne n'as maintenant plus accès a votre double coffre."));
        }
    }

}
