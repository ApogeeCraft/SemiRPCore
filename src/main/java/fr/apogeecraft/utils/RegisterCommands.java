package fr.apogeecraft.utils;

import fr.apogeecraft.Core;
import fr.apogeecraft.commands.admins.Day;
import fr.apogeecraft.commands.admins.Gamemode;
import fr.apogeecraft.commands.authentification.login;
import fr.apogeecraft.commands.authentification.register;
import fr.apogeecraft.commands.others.skull;
import fr.apogeecraft.commands.protection.chest.ChestProtect;
import fr.apogeecraft.commands.protection.claim.ClaimProtect;
import fr.apogeecraft.commands.users.Money;
import org.bukkit.command.CommandExecutor;

import java.util.Objects;


public class RegisterCommands {
    private final Core plugin;

    public RegisterCommands(Core plugin){
        this.plugin = plugin;
        registerAllCommands();
    }

    private void registerAllCommands(){
        registerCommands("register",new register(plugin));
        registerCommands("login",new login(plugin));
        registerCommands("skull",new skull());
        registerCommands("day",new Day());
        registerCommands("gamemode",new Gamemode(plugin));
        registerCommands("money",new Money(plugin));
        registerCommands("chest",new ChestProtect(plugin));
        registerCommands("claim",new ClaimProtect(plugin));
    }

    private void registerCommands(String command, CommandExecutor commandExecutor){
        Objects.requireNonNull(plugin.getCommand(command)).setExecutor(commandExecutor);
    }

}
