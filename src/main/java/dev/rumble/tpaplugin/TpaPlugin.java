package dev.rumble.tpaplugin;

import dev.rumble.tpaplugin.cmds.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.java.JavaPlugin;
import dev.rumble.utils.ColoredMsg;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;


public final class TpaPlugin extends JavaPlugin implements TabCompleter {
    public final static String prefix = "&6&l[TpaPlugin]&r ";
    public final static String dbUrl = "jdbc:sqlite:plugins/TpaPlugin/requests.db";
    @Override
    public void onEnable() {
        saveDefaultConfig();
        try(Connection conn = DriverManager.getConnection(dbUrl)){
            String sql = """
                        CREATE TABLE IF NOT EXISTS Requests(
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        origin TEXT NOT NULL COLLATE NOCASE,
                        destination TEXT NOT NULL COLLATE NOCASE
                        )""";
            Statement s = conn.createStatement();
            s.execute(sql);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        getCommand("tpa").setExecutor(new Tpa(this));
        getCommand("tpaaccept").setExecutor(new TpaAccept(this));
        getCommand("tpadeny").setExecutor(new TpaDeny(this));
        getCommand("tpahere").setExecutor(new TpaHere(this));
        getCommand("tpaplugin").setExecutor(new TPP(this));
        getCommand("tpaplugin").setTabCompleter(this);
        ColoredMsg.sendToConsole("\n"+prefix +"\n&rVersion: "
                +"&6"+ getDescription().getVersion()
                + "\n&rDev: " + getDescription().getAuthors()
        );
    }

    @Override
    public void onDisable() {
        saveConfig();
        if (getConfig().getBoolean("clearRequests")){
            try(Connection conn = DriverManager.getConnection(dbUrl)){
                String sql = "DROP TABLE IF EXISTS Requests;";
                Statement s = conn.createStatement();
                s.execute(sql);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        ColoredMsg.sendToConsole("¡Gracias por usar mi plugin!\n-pyrumble ");
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {

        if (command.getName().equalsIgnoreCase("tpaplugin")) {

            if (args.length == 1) {
                return List.of("reload");
            }
        }

        return Collections.emptyList();
    }
}
