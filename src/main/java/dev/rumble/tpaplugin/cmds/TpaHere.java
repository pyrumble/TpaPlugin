package dev.rumble.tpaplugin.cmds;

import dev.rumble.tpaplugin.TpaPlugin;
import dev.rumble.tpaplugin.misc.TpaRequest;
import dev.rumble.utils.ColoredMsg;
import dev.rumble.utils.UsefulMethods;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class TpaHere implements CommandExecutor {
    private final JavaPlugin plugin;
    public TpaHere(JavaPlugin plugin){
        this.plugin = plugin;
    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (UsefulMethods.isNotPlayer(sender)){
            ColoredMsg.sendToConsole(
                    plugin.getConfig().getString("messages.playerOnlyCommand")
            );
        }
        String sql1 = "SELECT origin, destination FROM Requests WHERE destination=?";
        try(Connection conn = DriverManager.getConnection(TpaPlugin.dbUrl);
            PreparedStatement ps = conn.prepareStatement(sql1)){
            ps.setString(1, sender.getName());
            ResultSet result = ps.executeQuery();
            List<TpaRequest> userReceivedRequests = new ArrayList<>();

            while (result.next()) {
                userReceivedRequests.add(new TpaRequest(
                        result.getString("origin"),
                        result.getString("destination")
                ));
            }
            String text = "";
            if (!userReceivedRequests.isEmpty()){
                text = text +
                        "&6--------------" +
                        "&bPending tp requests" +
                        "&6--------------&r\n";
                for (TpaRequest request: userReceivedRequests){
                    text = text.concat(request.origin + " &7->&r " + request.destination + "\n");
                }
                text = text.concat( "&6-----------------------------");

                }
            else{
                text = plugin.getConfig().getString("messages.noTpRequests");
            }
            ColoredMsg.sendToPlayer((Player) sender, text);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return true;
    }
}
