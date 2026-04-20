package dev.rumble.tpaplugin.cmds;

import dev.rumble.tpaplugin.TpaPlugin;
import dev.rumble.utils.ColoredMsg;
import dev.rumble.utils.UsefulMethods;
import net.md_5.bungee.api.chat.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class Tpa implements CommandExecutor {
    private final  JavaPlugin plugin;
    public Tpa(JavaPlugin plugin){
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
        if (args.length == 1) {
            Player to = Bukkit.getPlayer(args[0]);
            if (to == null) {
                ColoredMsg.sendToPlayer((Player) sender, plugin.getConfig().getString("messages.userDisconnectedOrNotFound"));
                return true;
            }
            if (to == sender) {
                ColoredMsg.sendToPlayer((Player) sender, plugin.getConfig().getString("messages.userAutoTpException"));
                return true;
            } else {

                String destination;
                String sql1 = "SELECT origin, destination FROM Requests WHERE origin=? AND destination=?";
                String sql2 = "SELECT origin FROM Requests WHERE destination=?";
                try(Connection conn = DriverManager.getConnection(TpaPlugin.dbUrl);
                    // Check if there's a tp request from 'origin' (player 1) to 'destination' (player 2)
                    PreparedStatement ps1 = conn.prepareStatement(sql1)){
                    ps1.setString(1, sender.getName());
                    ps1.setString(2, to.getName());
                    ResultSet result = ps1.executeQuery();
                    destination = result.getString("destination");
                    if  (destination != null){
                        ColoredMsg.sendToPlayer(((Player) sender),
                                TpaPlugin.prefix + plugin.getConfig().getString("messages.pendingRequestException"));
                        return true;
                    }
                    // Check if the destination has more than 10 requests
                    PreparedStatement ps2 = conn.prepareStatement(sql2);
                    ps2.setString(1,to.getName());
                    ResultSet result2 = ps2.executeQuery();
                    List<String> destinationRequests = new ArrayList<>();
                    while (result2.next()){
                        destinationRequests.add(result2.getString(1));
                    }
                    if (destinationRequests.toArray().length > 10){
                        ColoredMsg.sendToPlayer((Player) sender,
                                TpaPlugin.prefix + plugin.getConfig().getString("messages.maxTpRequests"));
                        return true;
                    }


                } catch (Exception e) {
                    throw new RuntimeException(e);
                }

                    String sql3 = "INSERT INTO Requests(origin, destination) VALUES(?, ?)";
                    try(Connection conn = DriverManager.getConnection(TpaPlugin.dbUrl);
                        PreparedStatement ps = conn.prepareStatement(sql3)){
                        ps.setString(1, sender.getName());
                        ps.setString(2, to.getName());
                        ps.executeUpdate();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                    String messageDestination = plugin.getConfig().getString("messages.requestSentDestination");
                    messageDestination = messageDestination.replace("{player}", sender.getName());
                    ColoredMsg.sendToPlayer(to,TpaPlugin.prefix + messageDestination);
                    TextComponent space = new TextComponent(" ");
                    TextComponent actions = new TextComponent(
                            ChatColor.translateAlternateColorCodes('&', "&eActions:"));
                    TextComponent accept = new TextComponent(
                            ChatColor.translateAlternateColorCodes('&', "&a[✔]"));
                    accept.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tpaaccept " + sender.getName()));
                    accept.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Click to accept").create()));
                    TextComponent deny = new TextComponent(
                            ChatColor.translateAlternateColorCodes('&', "&c[❌]"));
                    deny.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tpadeny " + sender.getName()));
                    deny.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Click to deny").create()));
                    BaseComponent[] components = {actions,space ,accept,space,deny};
                    to.spigot().sendMessage(components);
                    String messageOrigin = plugin.getConfig().getString("messages.requestSentOrigin");
                    messageOrigin = messageOrigin.replace("{player}", to.getName());
                    ColoredMsg.sendToPlayer(((Player) sender),
                            TpaPlugin.prefix + messageOrigin);
                }
            } else ColoredMsg.sendToPlayer(((Player) sender),"&c/tpa <player>");
        return true;
        }

    }
