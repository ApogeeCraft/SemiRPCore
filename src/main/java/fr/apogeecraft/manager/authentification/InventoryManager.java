package fr.apogeecraft.manager.authentification;

import fr.apogeecraft.Core;
import fr.apogeecraft.database.SqlConnection;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.List;

public class InventoryManager {
    private Core plugin;
    private SqlConnection sql;
    public InventoryManager(Core plugin){
        this.plugin = plugin;
        this.sql = plugin.getSqlConnection();
    }

    private Inventory claimInventory(Player p){
        Inventory inv = Bukkit.createInventory(null,9,plugin.chatColor("&lClaim"));

        if(sql.playerIsOwnerOfClaim(p,p.getLocation().getChunk())){
            inv.setItem(0, createItem(Material.IRON_INGOT, 1, true,403,"Unclaim","Click pour claim"));
            inv.setItem(7, createItem(Material.IRON_INGOT, 1, true,404,"Supprimer un membre","Click pour ajouter un membre"));
            inv.setItem(8, createItem(Material.IRON_INGOT, 1, true,405,"Ajouter un membre","Click pour supprimer un membre"));
        }
        else if(sql.getClaimName(p.getLocation().getChunk()).isEmpty()){
            inv.setItem(0, createItem(Material.IRON_INGOT, 1, true,402,"Claim","Click pour claim"));
        }
        return inv;
    }
    private Inventory confirmInventory(Player p){
        Inventory inv = Bukkit.createInventory(null,9,plugin.chatColor("&lConfirmation"));
        inv.setItem(0, createItem(Material.IRON_INGOT, 1, true,400,"Annuler","Click pour annuler"));
        inv.setItem(8, createItem(Material.IRON_INGOT, 1, true,401,"Confirmer","Click pour confirmer"));
        return inv;
    }


    private ItemStack createItem(Material material, int amount, boolean isEnchant,int customModelData,String name, String... lore){
        ItemStack is = new ItemStack(material,amount);
        ItemMeta meta = is.getItemMeta();
        List<String> loreList = Arrays.asList(lore);
        assert meta != null;
        meta.setLore(loreList);
        if(!name.isEmpty())meta.setDisplayName(name);

        if(isEnchant){
            meta.addEnchant(Enchantment.KNOCKBACK, 1, true);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }
        meta.setCustomModelData(customModelData);
        is.setItemMeta(meta);
        return is;
    }
    public void openConfirmInventory(Player p){ p.openInventory(confirmInventory(p)); }
    public void openClaimInventory(Player p){ p.openInventory(claimInventory(p)); }
}
