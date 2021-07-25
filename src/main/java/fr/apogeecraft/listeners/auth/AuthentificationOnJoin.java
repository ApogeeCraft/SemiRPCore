package fr.apogeecraft.listeners.auth;

import fr.apogeecraft.Core;
import fr.apogeecraft.database.SqlConnection;
import fr.apogeecraft.manager.authentification.AuthManager;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerResourcePackStatusEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class AuthentificationOnJoin implements Listener {
    private Core plugin;
    private SqlConnection sql;
    private AuthManager authManager;

    public AuthentificationOnJoin(Core plugin){
        this.plugin = plugin;
        this.sql = this.plugin.getSqlConnection();
        this.authManager = this.plugin.getAuthManager();
    }

    @EventHandler
    private void onTexturePackChange(PlayerResourcePackStatusEvent e){
        System.out.println(e.getStatus());
        if(e.getStatus().equals(PlayerResourcePackStatusEvent.Status.ACCEPTED))
            return;
        if(e.getStatus().equals(PlayerResourcePackStatusEvent.Status.SUCCESSFULLY_LOADED))
            return;

        if(e.getStatus().equals(PlayerResourcePackStatusEvent.Status.DECLINED)){
            e.getPlayer().kickPlayer(plugin.chatColor("Vous devez avoir le texture Packs."));
            return;
        }
        if(e.getStatus().equals(PlayerResourcePackStatusEvent.Status.FAILED_DOWNLOAD)){
            e.getPlayer().kickPlayer(plugin.chatColor("Vous n'avez pas réussi a téléchargé le texture pack"));
        }
    }

    @EventHandler
    public void onJoinEvent(PlayerJoinEvent e){
        sql.reActive();
        e.getPlayer().setResourcePack("https://apogeecraft.fr/storage/img/ApogeeCraftV7.zip");
        e.setJoinMessage("");
        Player p = e.getPlayer();
        Location l = p.getWorld().getHighestBlockAt(p.getLocation()).getLocation();
        l.setY(l.getY()+1);
        p.teleport(l);
        if(!sql.hasAccount(p)) p.sendMessage(chatColor("&eTu dois t'enregistrer avec &f&l/register <password> <password>"));
        else p.sendMessage(chatColor("&eTu dois te connecter avec &f&l/login <password>"));
        p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 2000,1));
    }

    @EventHandler
    public void onMoveEvent(PlayerMoveEvent e){
        if(!authManager.isLoggedIn(e.getPlayer())){
            e.setCancelled(true);
            Player p = e.getPlayer();
            Block b = p.getWorld().getHighestBlockAt(p.getLocation());
            p.teleport(b.getLocation());
        }
    }

    @EventHandler
    public void onPlaceChest(BlockPlaceEvent e){
        if(!(e.getBlockPlaced().getType().equals(Material.CHEST))) return;
        Block b = e.getBlockPlaced();
        int X = b.getX();
        int Y = b.getY();
        int Z = b.getZ();

        Location l = new Location(b.getWorld(), X+1, Y, Z);

        if(e.getPlayer().getWorld().getBlockAt(l).getType().equals(Material.CHEST)){
            if(sql.playerIsOwnerOfChest(e.getPlayer(),l)||sql.playerIsMemberOfChest(e.getPlayer().getName(),l));
            else{
                e.getPlayer().sendMessage(chatColor("#960000V#9b0000o#a10000u#a60000s #ac0000n#b10000'#b70000ê#bc0000t#c20000e#c70000s #cd0000p#d20000a#d80000s #dd0000a#e30000u#e80000t#ee0000o#f30000r#f90000i#ff0000s#f80000é #f20000à #eb0000p#e50000o#de0000s#d80000e#d10000r #cb0000u#c40000n #be0000c#b70000o#b10000f#aa0000f#a40000r#9d0000e #970000i#900000c#8a0000i #840000!"));
                e.setCancelled(true);
                return;
            }
        }
        Location l2 = new Location(b.getWorld(), X-1, Y, Z);

        if(e.getPlayer().getWorld().getBlockAt(l2).getType().equals(Material.CHEST)) {
            if(sql.playerIsOwnerOfChest(e.getPlayer(),l2)||sql.playerIsMemberOfChest(e.getPlayer().getName(),l2));
            else {
                e.getPlayer().sendMessage(chatColor("#960000V#9b0000o#a10000u#a60000s #ac0000n#b10000'#b70000ê#bc0000t#c20000e#c70000s #cd0000p#d20000a#d80000s #dd0000a#e30000u#e80000t#ee0000o#f30000r#f90000i#ff0000s#f80000é #f20000à #eb0000p#e50000o#de0000s#d80000e#d10000r #cb0000u#c40000n #be0000c#b70000o#b10000f#aa0000f#a40000r#9d0000e #970000i#900000c#8a0000i #840000!"));
                e.setCancelled(true);
                return;
            }
        }
        Location l3 = new Location(b.getWorld(), X, Y, Z+1);

        if(e.getPlayer().getWorld().getBlockAt(l3).getType().equals(Material.CHEST)){
            if(sql.playerIsOwnerOfChest(e.getPlayer(),l3)||sql.playerIsMemberOfChest(e.getPlayer().getName(),l3));
            else {
                e.getPlayer().sendMessage(chatColor("#960000V#9b0000o#a10000u#a60000s #ac0000n#b10000'#b70000ê#bc0000t#c20000e#c70000s #cd0000p#d20000a#d80000s #dd0000a#e30000u#e80000t#ee0000o#f30000r#f90000i#ff0000s#f80000é #f20000à #eb0000p#e50000o#de0000s#d80000e#d10000r #cb0000u#c40000n #be0000c#b70000o#b10000f#aa0000f#a40000r#9d0000e #970000i#900000c#8a0000i #840000!"));
                e.setCancelled(true);
                return;
            }
        }

        Location l4 = new Location(b.getWorld(), X, Y, Z-1);

        if(e.getPlayer().getWorld().getBlockAt(l4).getType().equals(Material.CHEST)) {
            if(sql.playerIsOwnerOfChest(e.getPlayer(),l4)||sql.playerIsMemberOfChest(e.getPlayer().getName(),l4));
            else {
                e.getPlayer().sendMessage(chatColor("#960000V#9b0000o#a10000u#a60000s #ac0000n#b10000'#b70000ê#bc0000t#c20000e#c70000s #cd0000p#d20000a#d80000s #dd0000a#e30000u#e80000t#ee0000o#f30000r#f90000i#ff0000s#f80000é #f20000à #eb0000p#e50000o#de0000s#d80000e#d10000r #cb0000u#c40000n #be0000c#b70000o#b10000f#aa0000f#a40000r#9d0000e #970000i#900000c#8a0000i #840000!"));
                e.setCancelled(true);
                return;
            }
        }

        sql.createChest(e.getPlayer(), e.getBlockPlaced().getLocation());
        e.getPlayer().sendMessage(plugin.chatColor("&aVous êtes desormais Propriétaire d'un coffre"));
    }

    @EventHandler
    public void onBreakChest(BlockBreakEvent e){
        if(!(e.getBlock().getType().equals(Material.CHEST))) return;

        if(!(sql.playerIsOwnerOfChest(e.getPlayer(),e.getBlock().getLocation()))){
            e.setCancelled(true);
            return;
        }
        sql.removeChest(e.getBlock().getLocation());
    }

    @EventHandler
    public void onClickChest(PlayerInteractEvent e){
        if(e.getClickedBlock()== null) return;
        if(!(e.getClickedBlock().getType().equals(Material.CHEST))) return;
        if( sql.playerIsMemberOfChest(e.getPlayer().getName(), e.getClickedBlock().getLocation()) ||sql.playerIsOwnerOfChest(e.getPlayer(), e.getClickedBlock().getLocation()))return;
        e.getPlayer().sendMessage(chatColor("#960000V#9b0000o#a10000u#a70000s #ad0000n#b30000'#b90000ê#be0000t#c40000e#ca0000s #d00000p#d60000a#dc0000s #e10000a#e70000u#ed0000t#f30000o#f90000r#ff0000i#f80000s#f10000é #ea0000à #e30000o#dc0000u#d60000v#cf0000r#c80000i#c10000r #ba0000c#b30000e #ad0000c#a60000o#9f0000f#980000f#910000r#8a0000e #840000!"));
        e.setCancelled(true);
    }
    private String chatColor(String text){ return plugin.chatColor(text); }
}
