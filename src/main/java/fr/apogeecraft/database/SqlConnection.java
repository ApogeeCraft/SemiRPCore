package fr.apogeecraft.database;

import fr.apogeecraft.Core;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.List;

public class SqlConnection {
    public Connection connection;
    private Core plugin;
    public String urlbase,host,database, user,pass;

    public SqlConnection(Core plugin, String urlbase, String host, String database, String user, String pass) {
        this.plugin = plugin;
        this.urlbase = urlbase;
        this.host = host;
        this.database = database;
        this.user = user;
        this.pass = pass;
    }

    public void connection(){
        try{
            connection = DriverManager.getConnection(urlbase + host + "/" + database + "?autoReconnect=true&useSSL=false&useLegacyDatetimeCode=false&serverTimezone=Europe/Paris"  , user, pass);
            System.out.println("SQL connecté");
        }catch (SQLException e){
            e.printStackTrace();
            System.out.println("SQL error or disconnected");
        }

    }

    public void disconnect(){
        if(isConnected()){
            try{
                connection.close();
                System.out.println("SQL deconnecté");
            }catch (SQLException e){
                e.printStackTrace();
                System.out.println("SQL error");
            }
        }
    }

    public boolean isConnected(){ return connection != null; }

    public void reActive(){
        try {
            PreparedStatement q = connection.prepareStatement("INSERT INTO `reActive` (`reactive`) VALUES (?);");
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
            LocalDateTime now = LocalDateTime.now();
            q.setString(1, dtf.format(now));
            q.execute();
            q.close();
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    public double getMoneyPlayer(String name){
        try {
            PreparedStatement q = connection.prepareStatement("SELECT `money` FROM `Users` WHERE `pseudo` = ?");
            q.setString(1, name);
            ResultSet r = q.executeQuery();

            double money = 0.00;
            while (r.next()) money = r.getDouble("money");

            q.close();
            r.close();

            return money;
        } catch (SQLException e) {
            e.printStackTrace();
            return 0.00;
        }
    }

    public boolean hasAccount(Player p){
        try {
        PreparedStatement q = connection.prepareStatement("SELECT `uuid` FROM `Users` WHERE `uuid` = ?");
        q.setString(1, p.getUniqueId().toString());
        ResultSet r = q.executeQuery();

        boolean hasAccount = r.next();
        q.close();
        r.close();

        return hasAccount;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<String> memberListOfChest(Location location){
        try {
            PreparedStatement q = connection.prepareStatement("SELECT `memberList` FROM `ProtectChest` WHERE `X` = ? AND `Y` = ? AND `Z`= ?");
            q.setString(1, String.valueOf(location.getBlockX()));
            q.setString(2, String.valueOf(location.getBlockY()));
            q.setString(3, String.valueOf(location.getBlockZ()));
            ResultSet r = q.executeQuery();
            List<String> memberList = new ArrayList<>();
            String membersList = "";

            while(r.next()) membersList= r.getString("memberList");

            if(membersList != null) Collections.addAll(memberList, membersList.split(" "));

            if(memberList == null) memberList = new ArrayList<>();

            q.close();
            r.close();

            return memberList;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean playerIsMemberOfChest(String name,Location l){ return memberListOfChest(l).contains(name); }

    public void createChest(Player p, Location location){
        try {
            PreparedStatement q = connection.prepareStatement("INSERT INTO `ProtectChest` (`Owner`, `X`,`Y`, `Z`) VALUES (?, ?, ?, ?);");
            q.setString(1, p.getName());
            q.setInt(2, location.getBlockX());
            q.setInt(3, location.getBlockY());
            q.setInt(4, location.getBlockZ());
            q.execute();
            q.close();
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    public void addMemberToChest(Player p, String playerName,Location location){
        if(!playerIsOwnerOfChest(p, location)){
            p.sendMessage(plugin.chatColor("&cVous n'êtes pas le propriétaire du coffre"));
            return;
        }
        try {
            PreparedStatement q = connection.prepareStatement("UPDATE `ProtectChest` SET `memberList` = ? WHERE `Owner` = ? AND `X` = ? AND `Y` = ? AND `Z` = ?");

            List<String> memberList = memberListOfChest(location);
            if(memberList.contains(playerName)) {

                return;
            }
            memberList.add(playerName);


            StringBuilder newMemberList = new StringBuilder();

            for(String member : memberList){ newMemberList.append(member).append(" "); }


            q.setString(1, String.valueOf(newMemberList));
            q.setString(2, p.getName());
            q.setInt(3, location.getBlockX());
            q.setInt(4, location.getBlockY());
            q.setInt(5, location.getBlockZ());
            q.execute();
            q.close();

        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    public void removeMemberOfChest(Player p, String playerName,Location location){
        if(!playerIsOwnerOfChest(p, location)){
            p.sendMessage(plugin.chatColor("&cVous n'êtes pas le propriétaire du coffre"));
            return;
        }
        try {
            PreparedStatement q = connection.prepareStatement("UPDATE `ProtectChest` SET `memberList` = ? WHERE `Owner` = ? AND `X` = ? AND `Y` = ? AND `Z` = ?");

            List<String> memberList = memberListOfChest(location);
            if(!memberList.contains(playerName)) {
                return;
            }
            memberList.remove(playerName);
            StringBuilder newMemberList = new StringBuilder();

            for(String member : memberList){ newMemberList.append(member).append(" "); }


            q.setString(1, String.valueOf(newMemberList));
            q.setString(2, p.getName());
            q.setInt(3, location.getBlockX());
            q.setInt(4, location.getBlockY());
            q.setInt(5, location.getBlockZ());
            q.execute();
            q.close();
        }catch (SQLException e){
            e.printStackTrace();
        }
    }


    public boolean playerIsOwnerOfChest(Player p, Location location){
        try {
            PreparedStatement q = connection.prepareStatement("SELECT `X`,`Y`,`Z`, `Owner` FROM `ProtectChest` WHERE `X` = ? AND `Y` = ? AND `Z` = ?");

            q.setInt(1, location.getBlockX());
            q.setInt(2, location.getBlockY());
            q.setInt(3, location.getBlockZ());
            ResultSet r = q.executeQuery();

            String Owner = "";
            while (r.next()) Owner = r.getString("Owner");

            q.close();
            r.close();

            return Owner.equals(p.getName());
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return false;
    }

    public void createUsers(Player p, String password){
        try {
            PreparedStatement q = connection.prepareStatement("INSERT INTO `Users` (`uuid`, `pseudo`, `password`) VALUES (?, ?, ?);");
            q.setString(1, p.getUniqueId().toString());
            q.setString(2, p.getName());
            q.setString(3, password);
            q.execute();
            q.close();
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    public void updatePlayerFriend(Player p , Player target){
        try {
            PreparedStatement q = connection.prepareStatement("UPDATE `Friends` SET `friendUUID` = ? WHERE `uuid` = ?");
            q.setString(1, target.getUniqueId().toString());
            q.setString(2, p.getUniqueId().toString());
            q.executeUpdate();
            q.close();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public void setPlayerFriend(Player p , Player target){
        try {
            PreparedStatement q = connection.prepareStatement("INSERT INTO `Friends` (`friendUUID`, `uuid`) VALUES (?, ?);");
            q.setString(1, target.getUniqueId().toString());
            q.setString(2, p.getUniqueId().toString());
            q.execute();
            q.close();
        }catch (Exception ex){
            ex.printStackTrace();
            System.out.println("erreur");
        }
    }

    public boolean playerIsInFriendList(Player p, Player target) {
        try {
            PreparedStatement q = connection.prepareStatement("SELECT `friendUUID`, `uuid` FROM `Friends` WHERE `uuid` = ? AND `friendUUID` = ?");
            q.setString(1, p.getUniqueId().toString());
            q.setString(2, target.getUniqueId().toString());
            ResultSet r = q.executeQuery();

            boolean isInPlayerList = r.next();

            q.close();
            r.close();
            return isInPlayerList;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    public void removeChest(Location location){
        try{
            PreparedStatement q = connection.prepareStatement("DELETE FROM `ProtectChest` WHERE `X`= ? AND `Y`= ? AND `Z`=?");
            q.setInt(1, location.getBlockX());
            q.setInt(2, location.getBlockY());
            q.setInt(3, location.getBlockZ());
            q.execute();
            q.close();

        }catch (SQLException ex){
            ex.printStackTrace();
        }
    }

    public boolean playerHasAccount(Player p){
        try{
            PreparedStatement q = connection.prepareStatement("SELECT `UUID` FROM `users` WHERE `UUID` = ?");
            q.setString(1, p.getUniqueId().toString());
            ResultSet r = q.executeQuery();
            boolean hasAccount = r.next();
            q.close();
            r.close();
            return hasAccount;
        }catch (SQLException e){
            System.out.println("hA "+ e);
            return false;
        }
    }
    public void createPlayerAccount(Player p){
        try{
            PreparedStatement q = connection.prepareStatement("INSERT INTO `users` (`pseudo`,`UUID`) VALUES (?,?)");
            q.setString(1, p.getName());
            q.setString(2, p.getUniqueId().toString());
            q.execute();
            q.close();

            System.out.println("Le compte banque du joueur "+ p.getName() + " à été créé ");
        }catch (SQLException e){
            System.out.println("CA "+ e);
        }
    }

    public void updatePlayerAccount(Player p){
        try {
            PreparedStatement q = connection.prepareStatement("UPDATE `users` SET `pseudo` = ? WHERE `UUID` = ?");

            q.setString(1, p.getName());
            q.setString(2, p.getUniqueId().toString());
            q.execute();
            q.close();

            System.out.println("Le compte banque du joueur " + p.getName() + " à été rechargé");
        } catch (SQLException e) {
            System.out.println("CR " + e);
        }
    }

    public boolean checkPasswordUsers(Player p, String password){
        try {
            PreparedStatement q = connection.prepareStatement("SELECT `password` FROM `Users` WHERE `uuid` = ?");
            q.setString(1, p.getUniqueId().toString());
            ResultSet r = q.executeQuery();

            String passwordResult = null;

            while (r.next()) {
                passwordResult = r.getString("password");
            }
            q.close();
            r.close();

            return Base64.getEncoder().encodeToString(password.getBytes()).equals(passwordResult);
        }catch (SQLException e){
            e.printStackTrace();
            return false;
        }
    }
    public void getMembersList(Player p, Location location){
        String newString = "";
        p.sendMessage(plugin.chatColor("&e                                              "));
        p.sendMessage(plugin.chatColor("&e-----&f&l"+" Liste des membres "+"&e-----"));
        p.sendMessage(plugin.chatColor("&e                                              "));
        for(String player : memberListOfChest(location)){
            if(memberListOfChest(location).get(0) == player);
            else p.sendMessage(plugin.chatColor("&e                "+player+"&e                "));
        }
        p.sendMessage(plugin.chatColor("&e                                              "));
        p.sendMessage(plugin.chatColor("&e-------------------------------"));
    }


    //------------------------------------------//
    public boolean containsClaim(String name){
        try{
            PreparedStatement q = connection.prepareStatement("SELECT `name` FROM `ClaimList` WHERE `name` = ?");
            q.setString(1, name);
            ResultSet r = q.executeQuery();
            boolean exist = r.next();
            q.close();
            r.close();
            return exist;
        }catch (SQLException e){
            System.out.println("hA "+ e);
            return false;
        }
    }
    public boolean containsClaimInClaimTable(String name){
        try{
            PreparedStatement q = connection.prepareStatement("SELECT `name` FROM `Claim` WHERE `name` = ?");
            q.setString(1, name);
            ResultSet r = q.executeQuery();
            boolean exist = r.next();
            q.close();
            r.close();
            return exist;
        }catch (SQLException e){
            System.out.println("hA "+ e);
            return false;
        }
    }
    public String getClaimName(Chunk chunk){
        try {
            PreparedStatement q = connection.prepareStatement("SELECT `name` FROM `Claim` WHERE `X` = ?  AND `Z` = ?");
            q.setInt(1, chunk.getX());
            q.setInt(2, chunk.getZ());
            ResultSet r = q.executeQuery();

            String claimName = "";
            while (r.next()) claimName = r.getString("name");
            q.close();
            r.close();
            return claimName;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return "";
        }
    }
    public boolean chestIsPrivate(Location location){
        try {
            PreparedStatement q = connection.prepareStatement("SELECT `X`,`Y`,`Z`, `Owner` FROM `ProtectChest` WHERE `X` = ? AND `Y` = ? AND `Z` = ?");

            q.setInt(1, location.getBlockX());
            q.setInt(2, location.getBlockY());
            q.setInt(3, location.getBlockZ());
            ResultSet r = q.executeQuery();

            String Owner = "";
            while (r.next()) Owner = r.getString("Owner");

            q.close();
            r.close();

            return !Owner.isEmpty();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return false;
    }
    public String getOwnerOfChest(Location location){
        try {
            PreparedStatement q = connection.prepareStatement("SELECT `X`,`Y`,`Z`, `Owner` FROM `ProtectChest` WHERE `X` = ? AND `Y` = ? AND `Z` = ?");

            q.setInt(1, location.getBlockX());
            q.setInt(2, location.getBlockY());
            q.setInt(3, location.getBlockZ());
            ResultSet r = q.executeQuery();

            String Owner = "";
            while (r.next()) Owner = r.getString("Owner");

            q.close();
            r.close();

            return Owner;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return "";
    }
    public boolean playerIsOwnerOfClaim(Player p, Chunk chunk){

        try {
            PreparedStatement q = connection.prepareStatement("SELECT `owner`,`name` FROM `ClaimList` WHERE `name` = ?");

            q.setString(1, getClaimName(chunk));

            ResultSet r = q.executeQuery();

            String owner = "";
            while (r.next()) owner = r.getString("owner");

            q.close();
            r.close();
            return owner.equals(p.getName());
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return false;
    }
    public List<String> memberListOfClaim(String name){
        try {
            PreparedStatement q = connection.prepareStatement("SELECT `members` FROM `ClaimList` WHERE `name` = ?");
            q.setString(1, name);

            ResultSet r = q.executeQuery();
            List<String> memberList = new ArrayList<>();
            String membersList = "";

            while(r.next()) membersList= r.getString("members");

            if(membersList != null) Collections.addAll(memberList, membersList.split(" "));
            if(memberList == null) memberList = new ArrayList<>();
            q.close();
            r.close();

            return memberList;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean getMembersListOfClaim(Player p, Chunk chunk){
        int id = 0;
        for(String player : memberListOfClaim(getClaimName(chunk))){
            if(!player.isEmpty()) id++;
        }
        if(id==0) return true;

        p.sendMessage(plugin.chatColor("&e                                              "));
        p.sendMessage(plugin.chatColor("&e-----&f&l"+" Liste des membres "+"&e-----"));
        p.sendMessage(plugin.chatColor("&e                                              "));
        for(String player : memberListOfClaim(getClaimName(chunk))){
            if(!player.isEmpty()) p.sendMessage(plugin.chatColor("&e                "+player+"&e                "));
        }
        p.sendMessage(plugin.chatColor("&e                                              "));
        p.sendMessage(plugin.chatColor("&e-------------------------------"));
        return false;
    }


    public void removeFromClaimList(String name){
        try{
            PreparedStatement q = connection.prepareStatement("DELETE FROM `ClaimList` WHERE `name`= ?");
            q.setString(1, name);
            q.execute();
            q.close();
        }catch (SQLException ex){
            ex.printStackTrace();
        }
    }


    public void removeClaim(Chunk chunk){
        try{
            String name= "";
            try{
               name = getClaimName(chunk);
            }catch (Exception e){
                e.printStackTrace();
            }

            PreparedStatement q = connection.prepareStatement("DELETE FROM `Claim` WHERE `X`= ? AND `Z`= ?");
            q.setInt(1,chunk.getX());
            q.setInt(2,chunk.getZ());
            q.execute();
            q.close();

            if(!containsClaimInClaimTable(name)) removeFromClaimList(name);

        }catch (SQLException ex){
            ex.printStackTrace();
        }
    }

    public void addMemberToClaim(Player p, String playerName,Chunk chunk){
        if(!playerIsOwnerOfClaim(p, chunk)){
            p.sendMessage(plugin.chatColor("&cVous n'êtes pas le propriétaire du claim"));
            return;
        }
        try {
            PreparedStatement q = connection.prepareStatement("UPDATE `ClaimList` SET `members` = ? WHERE `name` = ?");

            List<String> memberList = memberListOfClaim(getClaimName(chunk));
            if(memberList.contains(playerName)) {
                p.sendMessage(plugin.chatColor("&cCette personne est déjà membre du claim."));
                return;
            }
            memberList.add(playerName);
            StringBuilder newMemberList = new StringBuilder();

            for(String member : memberList){ newMemberList.append(member).append(" "); }
            p.sendMessage(plugin.chatColor("&aVous avez ajouté &f&l" + playerName + "&a a ce claim."));

            q.setString(1, String.valueOf(newMemberList));
            q.setString(2, getClaimName(chunk));
            q.execute();
            q.close();

        }catch (SQLException e){
            e.printStackTrace();
        }
    }
    public void addMemberToClaim(String playerName,String claimName){
        try {
            PreparedStatement q = connection.prepareStatement("UPDATE `ClaimList` SET `members` = ? WHERE `name` = ?");

            List<String> memberList = memberListOfClaim(claimName);

            memberList.add(playerName);
            StringBuilder newMemberList = new StringBuilder();

            for(String member : memberList){ newMemberList.append(member).append(" "); }
            q.setString(1, String.valueOf(newMemberList));
            q.setString(2, claimName);
            q.execute();
            q.close();

        }catch (SQLException e){
            e.printStackTrace();
        }
    }
    public void createInClaimList(Player p,String name){
        try {
            PreparedStatement q = connection.prepareStatement("INSERT INTO `ClaimList` (`owner`, `name`) VALUES (?, ?);");
            q.setString(1, p.getName());
            q.setString(2, name);
            q.execute();
            q.close();
        }catch (SQLException e){
            e.printStackTrace();
        }
    }
    public void firstClaim(Player p,String name){
        try {
            if(containsClaim(name)) return;
            PreparedStatement q = connection.prepareStatement("INSERT INTO `ClaimList` (`name`,`owner`) VALUES (?, ?);");
            q.setString(1, name);
            q.setString(2,p.getName());
            q.execute();
            q.close();
        }catch (SQLException e){
            e.printStackTrace();
        }
    }
    public void addClaim(Player p,String name, Chunk chunk){
        firstClaim(p,name);
        try {
            PreparedStatement q = connection.prepareStatement("INSERT INTO `Claim` (`name`, `X`,`Z`) VALUES (?, ?, ?);");
            q.setString(1, name);
            q.setInt(2, chunk.getX());
            q.setInt(3, chunk.getZ());
            q.execute();
            q.close();
        }catch (SQLException e){
            e.printStackTrace();
        }
    }


    public void removeMemberOfClaim(Player p, String playerName,Chunk chunk){
        if(!playerIsOwnerOfClaim(p, chunk)){
            p.sendMessage(plugin.chatColor("&cVous n'êtes pas le propriétaire du coffre"));
            return;
        }
        try {
            PreparedStatement q = connection.prepareStatement("UPDATE `ClaimList` SET `members` = ? WHERE `name` = ?");

            List<String> memberList = memberListOfClaim(getClaimName(chunk));
            if(!memberList.contains(playerName)) {
                p.sendMessage(plugin.chatColor("&cCette personne n'est pas dans votre claim."));
                return;
            }
            memberList.remove(playerName);
            StringBuilder newMemberList = new StringBuilder();

            for(String member : memberList){ newMemberList.append(member).append(" "); }
            p.sendMessage(plugin.chatColor("&aCette personne a été viré !."));

            q.setString(1, String.valueOf(newMemberList));
            q.setString(2, getClaimName(chunk));
            q.execute();
            q.close();
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

}
