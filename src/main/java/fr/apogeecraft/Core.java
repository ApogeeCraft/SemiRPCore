package fr.apogeecraft;

import fr.apogeecraft.database.SqlConnection;
import fr.apogeecraft.manager.authentification.AuthManager;
import fr.apogeecraft.manager.authentification.InventoryManager;
import fr.apogeecraft.manager.claim.PlayerManager;
import fr.apogeecraft.utils.RegisterCommands;
import fr.apogeecraft.utils.RegisterListeners;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class Core extends JavaPlugin {
    private SqlConnection sql;
    private AuthManager authManager;
    private InventoryManager inventoryManager;
    private PlayerManager playerManager;
    private FileConfiguration config;

    @Override
    public void onEnable() {
        loadConfig();
        connectToSQL("127.0.0.1",3306, "ApogeeServ","Admin", "56er46gqerg!qe");
        registerManagers();

        new RegisterListeners(this);
        new RegisterCommands(this);
    }

    @Override
    public void onDisable() {
        sql.disconnect();
    }

    private void registerManagers(){
        inventoryManager = new InventoryManager(this);
        authManager = new AuthManager();
        playerManager = new PlayerManager();
    }

    public void connectToSQL(String host, int port, String database, String username,String password ){
        sql = new SqlConnection(this,"jdbc:mysql://",host+":"+port,database,username,password);
        sql.connection();
    }

    private void loadConfig(){
        saveDefaultConfig();

        File configFile = new File(getDataFolder(),"config.yml");
        config = new YamlConfiguration();
        try {
            config.load(configFile);
        } catch (InvalidConfigurationException | IOException ioException) {
            ioException.printStackTrace();

        }
    }

    @Override
    public FileConfiguration getConfig() { return config; }
    public SqlConnection getSqlConnection(){ return sql; }
    public LuckPerms getLuckPermsProvider(){ return LuckPermsProvider.get();}
    public String getCurrency(){ return getConfig().getConfigurationSection("semiRP").getString("currency"); }
    public AuthManager getAuthManager(){ return authManager; }
    public InventoryManager getInventoryManager() { return inventoryManager; }
    public PlayerManager getPlayerManager() { return playerManager; }

    private final Pattern pattern = Pattern.compile("#[A-fA-F0-9]{6}");

    public String chatColor(String text){
        Matcher match = pattern.matcher(text);
        while(match.find()){
            String color = text.substring(match.start(), match.end());
            text = text.replace(color, ChatColor.of(color)+ "");
            match = pattern.matcher(text);
        }
        return ChatColor.translateAlternateColorCodes('&',text);
    }
}
