package fr.apogeecraft;

import fr.apogeecraft.database.SqlConnection;
import fr.apogeecraft.listeners.jdaListener.DiscordListener;
import fr.apogeecraft.listeners.jdaListener.JDAExecutor;
import fr.apogeecraft.manager.authentification.AuthManager;
import fr.apogeecraft.manager.authentification.InventoryManager;
import fr.apogeecraft.manager.chest.ChestManager;
import fr.apogeecraft.manager.claim.PlayerManager;
import fr.apogeecraft.utils.Lag;
import fr.apogeecraft.utils.RegisterCommands;
import fr.apogeecraft.utils.RegisterListeners;


import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import javax.security.auth.login.LoginException;
import java.io.File;
import java.io.IOException;

import java.util.Calendar;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class Core extends JavaPlugin {
    private JDA jda;

    private SqlConnection sql;
    private AuthManager authManager;
    private InventoryManager inventoryManager;
    private PlayerManager playerManager;
    private ChestManager chestManager;
    private FileConfiguration config;

    @Override
    public void onLoad(){
        try {
            jda= JDABuilder.createDefault("ODU3Njk2NDM3NTE2ODk0MjM5.YNTWDQ.mFa_M16TppqpW8eUKITRhfUHq44").addEventListeners(new DiscordListener(this)).build();
        } catch (LoginException e) {
            e.printStackTrace();
        }
        super.onLoad();
    }


    @Override
    public void onEnable() {
        Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(this, new Lag(), 100L, 1L);
        loadConfig();
        connectToSQL("127.0.0.1",3306, "ApogeeServ","Admin", "56er46gqerg!qe");
        registerManagers();
        new RegisterListeners(this);
        new RegisterCommands(this);
        new JDAExecutor(this);
    }

    @Override
    public void onDisable() {
        sql.disconnect();
        EmbedBuilder messageEmbed = new EmbedBuilder();
        messageEmbed.setTitle("Stats:").setFooter("Mis a jour à "+Calendar.getInstance().getTime().getHours() +":"+Calendar.getInstance().getTime().getMinutes());
        messageEmbed.setDescription("```fix\nServeur fermé```");
        Objects.requireNonNull(getJda().getTextChannelById("870663275241689088")).editMessageEmbedsById("870728228774490113", messageEmbed.build()).queue();
    }

    private void registerManagers(){
        inventoryManager = new InventoryManager(this);
        authManager = new AuthManager();
        playerManager = new PlayerManager();
        chestManager = new ChestManager();

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
    public ChestManager getChestManager() { return chestManager; }
    public JDA getJda(){ return jda;}

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
