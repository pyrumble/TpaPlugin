package dev.rumble.tpaplugin;

import dev.rumble.tpaplugin.cmds.*;
import org.bukkit.plugin.java.JavaPlugin;
import dev.rumble.utils.ColoredMsg;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;


public final class TpaPlugin extends JavaPlugin{
    public final static String prefix = "&6&l[TpaPlugin]&r ";
    public final static String dbUrl = "jdbc:sqlite:/plugins/TpaPlugin/requests.db";
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
        getCommand("tparequests").setExecutor(new TpaRequests(this));
        ColoredMsg.sendToConsole("\n\t&rVersion: "
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
}
