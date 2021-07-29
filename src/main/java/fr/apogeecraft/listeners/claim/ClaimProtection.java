package fr.apogeecraft.listeners.claim;

import fr.apogeecraft.Core;
import fr.apogeecraft.database.SqlConnection;
import fr.apogeecraft.manager.authentification.AuthManager;
import net.md_5.bungee.api.chat.*;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.world.ChunkPopulateEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.List;
import java.util.Objects;

public class ClaimProtection implements Listener {
    private Core plugin;
    private SqlConnection sql;
    private AuthManager authManager;

    public ClaimProtection(Core plugin){
        this.plugin = plugin;
        this.sql = this.plugin.getSqlConnection();
        this.authManager = this.plugin.getAuthManager();
    }

    @EventHandler
    private void onBreakBlock(BlockBreakEvent e){
        if(sql.getClaimName(e.getBlock().getChunk()).isEmpty()) return;
        if(!sql.playerIsOwnerOfClaim(e.getPlayer(),e.getBlock().getChunk())){
            e.getPlayer().sendMessage(plugin.chatColor("&cVous n'êtes pas propriétaire de cette zone!"));
            e.setCancelled(true);
        }
    }
    @EventHandler
    private void onPlaceBlock(BlockPlaceEvent e){
        if(sql.getClaimName(e.getBlock().getChunk()).isEmpty()) return;
        if(!sql.playerIsOwnerOfClaim(e.getPlayer(),e.getBlock().getChunk())){
            e.getPlayer().sendMessage(plugin.chatColor("&cVous n'êtes pas propriétaire de cette zone!"));
            e.setCancelled(true);
        }
    }
    @EventHandler
    private void onPlayerEnterInChunk(PlayerMoveEvent e){
        if(sql.getClaimName(e.getTo().getChunk()).equals(sql.getClaimName(e.getFrom().getChunk())))
            return;
        if(sql.getClaimName(e.getTo().getChunk()).isEmpty()){
            e.getPlayer().sendTitle(plugin.chatColor("&eVous êtes désormais dans une zone libre."), e.getTo().getChunk().getX()+"-"+e.getTo().getChunk().getZ());

            return;
        }
        e.getPlayer().sendTitle(plugin.chatColor("&eVous entrez dans le claim : "),plugin.chatColor("&f&l"+sql.getClaimName(e.getTo().getChunk())+" "+e.getTo().getChunk().getX()+"-"+e.getTo().getChunk().getZ()));}


    @EventHandler
    private void onConfirmUnclaimingInventory(InventoryClickEvent e){
        if(!e.getView().getTitle().equals(plugin.chatColor("&lConfirmation"))) return;
        if(e.getCurrentItem() == null) return;

        if(plugin.getPlayerManager().isRemoving(e.getWhoClicked().getName())){
            if(!sql.playerIsOwnerOfClaim((Player) e.getWhoClicked(), e.getWhoClicked().getLocation().getChunk())){
                plugin.getPlayerManager().removePlayerToIsRemoving((Player)e.getWhoClicked());
                e.getWhoClicked().sendMessage(plugin.chatColor("&cVous n'êtes pas l'owner du claim."));
                e.setCancelled(true);
                e.getWhoClicked().closeInventory();
                return;
            }
            if(e.getCurrentItem().getItemMeta().getCustomModelData() == 400){
                //annuler
                plugin.getPlayerManager().removePlayerToIsRemoving((Player)e.getWhoClicked());
                e.getWhoClicked().sendMessage(plugin.chatColor("&cVous avez annulé votre action."));
                e.setCancelled(true);
                e.getWhoClicked().closeInventory();
                return;
            }
            if(e.getCurrentItem().getItemMeta().getCustomModelData() == 401){
                //confirmer
                plugin.getPlayerManager().removePlayerToIsRemoving((Player)e.getWhoClicked());
                sql.removeClaim(e.getWhoClicked().getLocation().getChunk());
                e.getWhoClicked().sendMessage(plugin.chatColor("&aVous avez bien unclaim votre zone!"));
                e.setCancelled(true);
                e.getWhoClicked().closeInventory();
                return;
            }
            e.setCancelled(true);
        }

    }

    @EventHandler
    private void onConfirmKickingInventory(InventoryClickEvent e){
        if(!e.getView().getTitle().equals(plugin.chatColor("&lConfirmation"))) return;
        if(e.getCurrentItem() == null) return;

        if(plugin.getPlayerManager().isKickingFromClaim(e.getWhoClicked().getName())){
            if(!sql.playerIsOwnerOfClaim((Player) e.getWhoClicked(), e.getWhoClicked().getLocation().getChunk())){
                e.getWhoClicked().sendMessage(plugin.chatColor("&cVous n'êtes pas l'owner du claim."));
                e.setCancelled(true);
                e.getWhoClicked().closeInventory();
                return;
            }
            if(e.getCurrentItem().getItemMeta().getCustomModelData() == 400){
                //annuler
                plugin.getPlayerManager().removePlayerToKick((Player)e.getWhoClicked());
                plugin.getPlayerManager().removePlayerToIsKickingFromClaim(e.getWhoClicked().getName());
                e.getWhoClicked().sendMessage(plugin.chatColor("&cVous avez annulé votre action."));
                e.setCancelled(true);
                e.getWhoClicked().closeInventory();
                return;
            }
            if(e.getCurrentItem().getItemMeta().getCustomModelData() == 401){
                //confirmer
                plugin.getPlayerManager().removePlayerToIsKickingFromClaim(e.getWhoClicked().getName());
                sql.removeMemberOfClaim((Player)e.getWhoClicked(), plugin.getPlayerManager().getPlayerToKick(((Player) e.getWhoClicked())), e.getWhoClicked().getLocation().getChunk());
                e.getWhoClicked().sendMessage(plugin.chatColor("&aVous avez bien expulsé "+plugin.getPlayerManager().getPlayerToKick((Player)e.getWhoClicked())+"!"));
                plugin.getPlayerManager().removePlayerToKick((Player)e.getWhoClicked());
                e.setCancelled(true);
                e.getWhoClicked().closeInventory();
                return;
            }
            e.setCancelled(true);
        }

    }

    @EventHandler
    private void onClaimInventoryClick(InventoryClickEvent e){
        if(!e.getView().getTitle().equals(plugin.chatColor("&lClaim"))) return;
        if(e.getCurrentItem() == null) return;

        if(e.getCurrentItem().getItemMeta().getCustomModelData() == 402){
            plugin.getPlayerManager().addPlayerToIsClaiming(e.getWhoClicked().getName(), e.getWhoClicked().getLocation().getChunk());
            e.getView().close();
            e.getWhoClicked().sendMessage("");
            e.getWhoClicked().sendMessage(plugin.chatColor("&eEcrivez le nom du claim !"));
        }
        if(e.getCurrentItem().getItemMeta().getCustomModelData() == 403){
            plugin.getPlayerManager().addPlayerToIsRemoving(e.getWhoClicked().getName(), e.getWhoClicked().getLocation().getChunk());
            e.getView().close();
            plugin.getInventoryManager().openConfirmInventory((Player) e.getWhoClicked());
        }
        if(e.getCurrentItem().getItemMeta().getCustomModelData() == 404){
            //removePlayerFromClaim
            plugin.getPlayerManager().addPlayerToIsKickingFromClaim(e.getWhoClicked().getName());
            e.getView().close();
            e.getWhoClicked().sendMessage("");
            if(sql.getMembersListOfClaim((Player) e.getWhoClicked(),e.getWhoClicked().getLocation().getChunk())){
                e.getWhoClicked().sendMessage(plugin.chatColor("&cVous n'avez aucun membre dans votre claim!"));
                plugin.getPlayerManager().removePlayerToIsKickingFromClaim(e.getWhoClicked().getName());
                e.getView().close();
                return;
            }
            e.getWhoClicked().sendMessage(plugin.chatColor("&eEcrivez le nom de la personne a kick de votre claim !"));
            e.getWhoClicked().sendMessage(plugin.chatColor("&eEcrit &fexit &epour annulez !"));
            e.getView().close();
        }
        if(e.getCurrentItem().getItemMeta().getCustomModelData() == 405){
            //addPlayerToClaim
            plugin.getPlayerManager().addPlayerToIsAddingToClaim(e.getWhoClicked().getName());
            e.getView().close();
            e.getWhoClicked().sendMessage("");
            e.getWhoClicked().sendMessage(plugin.chatColor("&eEcrivez le nom de la personne a inviter dans votre claim !"));
            e.getWhoClicked().sendMessage(plugin.chatColor("&eEcrit &fexit &epour annulez !"));
            e.getView().close();
        }
        e.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    private void onConfirmClaiming(AsyncPlayerChatEvent e){
        if(!plugin.getPlayerManager().isClaiming(e.getPlayer())) return;
        if(e.getMessage().equals("exit")){
            plugin.getPlayerManager().removePlayerToIsClaiming(e.getPlayer());
        }
        if(e.getMessage().split(" ").length==1){
            if(!plugin.getSqlConnection().getClaimName(e.getPlayer().getLocation().getChunk()).isEmpty()){
                e.getPlayer().sendMessage(plugin.chatColor("&cCette zone est deja prise !"));
                plugin.getPlayerManager().removePlayerToIsClaiming(e.getPlayer());
                e.setCancelled(true);
                return;
            }
            sql.addClaim(e.getPlayer(),e.getMessage(),e.getPlayer().getLocation().getChunk());
            plugin.getPlayerManager().removePlayerToIsClaiming(e.getPlayer());
            e.getPlayer().sendMessage(plugin.chatColor("&aVous venez de claim ici"));
        }
        e.setCancelled(true);
    }


    @EventHandler(priority = EventPriority.HIGHEST)
    private void onConfirmUnclaiming(PlayerChatEvent e){
        if(!plugin.getPlayerManager().isRemoving(e.getPlayer().getName())) {
        }
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    private void onConfirmKicking(PlayerChatEvent e){
        if(!plugin.getPlayerManager().isKickingFromClaim(e.getPlayer().getName())) return;
        if(e.getMessage().equals("exit")){
            plugin.getPlayerManager().removePlayerToIsKickingFromClaim(e.getPlayer().getName());
        }
        if(e.getMessage().split(" ").length!=1) return;
        List<String> members = sql.memberListOfClaim(sql.getClaimName(e.getPlayer().getLocation().getChunk()));
        if(members.contains(e.getMessage())){
            plugin.getPlayerManager().addPlayerToKick(e.getPlayer().getName(), e.getMessage());
            plugin.getInventoryManager().openConfirmInventory(e.getPlayer());
            e.setCancelled(true);
            return;
        }
        e.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    private void onConfirmAdding(PlayerChatEvent e){
        if(!plugin.getPlayerManager().isAddingToClaim(e.getPlayer().getName())) return;
        if(e.getMessage().equals("exit")){
            plugin.getPlayerManager().removePlayerToIsAddingToClaim(e.getPlayer().getName());
            e.getPlayer().sendMessage(plugin.chatColor("&cVous venez d'annulé votre action!"));
            e.setCancelled(true);
            return;
        }
        if(e.getMessage().split(" ").length!=1) {
            e.getPlayer().sendMessage(plugin.chatColor("&cEntrez un pseudo valide!"));
            e.setCancelled(true);
            return;
        }

        if(e.getPlayer().getName().equals(e.getMessage())){
            e.getPlayer().sendMessage(plugin.chatColor("&cVous ne pouvez pas vous ajoutez vous même !"));
            e.setCancelled(true);
            return;
        }
        if(Bukkit.getPlayer(e.getMessage()) == null || !playerIsOnline(e.getMessage())){

            e.getPlayer().sendMessage(plugin.chatColor("&cCe joueur n'est pas connecté"));
            e.setCancelled(true);
            return;
        }

        if(Objects.requireNonNull(Bukkit.getPlayer(e.getMessage())).isOnline()){

            if(sql.memberListOfClaim(sql.getClaimName(e.getPlayer().getLocation().getChunk())).contains(Objects.requireNonNull(Bukkit.getPlayer(e.getMessage())).getName())){
                e.getPlayer().sendMessage(plugin.chatColor("&cCe joueur fais déjà partis des membres du claim !"));
                plugin.getPlayerManager().removePlayerToIsAddingToClaim(e.getPlayer().getName());
                e.setCancelled(true);
                return;
            }
            if(plugin.getPlayerManager().canJoinClaim(Objects.requireNonNull(Bukkit.getPlayer(e.getMessage())).getName(), e.getPlayer().getName())) {
                e.getPlayer().sendMessage(plugin.chatColor("&cVous avez déjà envoyé une invitation a ce joueur."));
                plugin.getPlayerManager().removePlayerToIsAddingToClaim(e.getPlayer().getName());
                e.setCancelled(true);
                return;
            }
            if(plugin.getPlayerManager().hasInvited(Objects.requireNonNull(Bukkit.getPlayer(e.getMessage())).getName())) {
                e.getPlayer().sendMessage(plugin.chatColor("&cCe joueur à déjà une invitation en attente du claim:"+plugin.getPlayerManager().getPlayerClaimToAdd(Objects.requireNonNull(Bukkit.getPlayer(e.getMessage())).getName()) +"."));
                plugin.getPlayerManager().removePlayerToIsAddingToClaim(e.getPlayer().getName());
                e.setCancelled(true);
                return;
            }
            plugin.getPlayerManager().addPlayerToAdd(e.getPlayer().getName(), Objects.requireNonNull(Bukkit.getPlayer(e.getMessage())).getName());
            plugin.getPlayerManager().addPlayerClaimToAdd(Objects.requireNonNull(Bukkit.getPlayer(e.getMessage())).getName(), sql.getClaimName(e.getPlayer().getLocation().getChunk()));
            Objects.requireNonNull(Bukkit.getPlayer(e.getMessage())).sendMessage("");

            Objects.requireNonNull(Bukkit.getPlayer(e.getMessage())).sendMessage(plugin.chatColor("&eVous êtes invité dans le claim "+ sql.getClaimName(e.getPlayer().getLocation().getChunk())+" par "+e.getPlayer().getName()));

            TextComponent accepter = new TextComponent(plugin.chatColor("&aAccepter"));
            accepter.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/claim accept"));
            accepter.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(plugin.chatColor("Cliquer pour être membre du claim.")).create()));

            TextComponent refuse = new TextComponent(plugin.chatColor("&cRefuser"));
            refuse.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/claim refuse"));
            refuse.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(plugin.chatColor("Cliquer pour refuser l'invitation.")).create()));

            Objects.requireNonNull(Bukkit.getPlayer(e.getMessage())).sendMessage(plugin.chatColor( "&eCliquer ici pour:"));
            Objects.requireNonNull(Bukkit.getPlayer(e.getMessage())).spigot().sendMessage(accepter);
            Objects.requireNonNull(Bukkit.getPlayer(e.getMessage())).sendMessage(plugin.chatColor("&e ou "));
            Objects.requireNonNull(Bukkit.getPlayer(e.getMessage())).spigot().sendMessage(refuse);
            e.getPlayer().sendMessage(plugin.chatColor("&aUne invitation a été envoyé à "+Objects.requireNonNull(Bukkit.getPlayer(e.getMessage())).getName()));
            plugin.getPlayerManager().removePlayerToIsAddingToClaim(e.getPlayer().getName());
            e.setCancelled(true);
            BukkitTask bt = new BukkitRunnable(){
                @Override
                public void run() {
                    if(plugin.getPlayerManager().getPlayerToAdd(e.getPlayer()).equals(e.getMessage())) {
                        e.getPlayer().sendMessage(plugin.chatColor("&cLe joueur n'as pas repondu a votre invitation depuis maintenant 3 min"));
                        plugin.getPlayerManager().removePlayerClaimToAdd(e.getMessage());
                        plugin.getPlayerManager().removePlayerToAdd(e.getMessage());
                        if(Bukkit.getPlayer(e.getMessage()).isOnline()){
                            Bukkit.getPlayer(e.getMessage()).sendMessage(plugin.chatColor("&cL'invitation de "+e.getPlayer().getName()+" à expiré!"));
                        }
                    }

                }
            }.runTaskLaterAsynchronously(plugin,200L);

            return;
        }
        e.setCancelled(true);
    }

    private boolean playerIsOnline(String pseudo){
        for(Player p :Bukkit.getOnlinePlayers()){
            if(p.getName().equals(pseudo)) return true;
        }
        return false;
    }

}
