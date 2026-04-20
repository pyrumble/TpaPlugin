package dev.rumble.tpaplugin.cmds;

import dev.rumble.tpaplugin.misc.TpaHandler;
import dev.rumble.utils.ColoredMsg;
import dev.rumble.utils.UsefulMethods;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import dev.rumble.tpaplugin.TpaPlugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class TpaAccept implements CommandExecutor{
    private final JavaPlugin plugin;
    public TpaAccept(JavaPlugin plugin){
        this.plugin = plugin;
    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String alias, String[] args) {
        if (UsefulMethods.isNotPlayer(sender)){
            ColoredMsg.sendToConsole(
                    TpaPlugin.prefix + plugin.getConfig().getString("messages.playerOnlyCommand")
            );
            return true;
        }
        if (args.length != 1){
            ColoredMsg.sendToPlayer(((Player) sender), TpaPlugin.prefix + "&c/tpaaccept <player>");
            return  true;
        }
        // UUID userUUID = TpaPlugin.tpaRequests.get(((Player) sender).getUniqueId());
        Player requester = Bukkit.getPlayer(args[0]);
        if(requester == null){
            ((Player) sender).getUniqueId();
            ColoredMsg.sendToPlayer(((Player) sender),
                    TpaPlugin.prefix + plugin.getConfig().getString("messages.userDisconnected"));
            return true;
        }
        String origin;
        String sql1 = "SELECT origin FROM Requests WHERE origin=? AND destination=?";
        try(Connection conn = DriverManager.getConnection(TpaPlugin.dbUrl);
            PreparedStatement ps = conn.prepareStatement(sql1)){
            ps.setString(1, requester.getName());
            ps.setString(2, sender.getName());
            ResultSet result = ps.executeQuery();
            origin = result.getString("origin");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        if(origin != null) {
            String messageDestination = plugin.getConfig().getString("messages.requestAcceptedDestination");
            messageDestination = messageDestination.replace("{player}", origin);
            String messageOrigin = plugin.getConfig().getString("messages.requestAcceptedOrigin");
            messageOrigin = messageOrigin.replace("{player}", sender.getName());
            ColoredMsg.sendToPlayer(((Player) sender),
                    TpaPlugin.prefix + messageDestination);
            ColoredMsg.sendToPlayer(requester,
                    TpaPlugin.prefix + messageOrigin);
                requester.teleport(((Player) sender));
            TpaHandler.removeRequest(origin,sender.getName());

        }else{
            ColoredMsg.sendToPlayer(
                    ((Player) sender), TpaPlugin.prefix +  plugin.getConfig().getString("messages.noTpRequest"));
        }

        return true;
    }
}
