package fr.apogeecraft.manager.chest;

import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;

public class ChestManager {
    List<Material> blockInvalid = new ArrayList<>();

    public ChestManager(){
        setInvalidBlockList();
    }

    private void setInvalidBlockList(){
        blockInvalid.add(Material.HOPPER);
        blockInvalid.add(Material.CHEST);
    }

    public List<Material> getBlockInvalid() {
        return blockInvalid;
    }
}
