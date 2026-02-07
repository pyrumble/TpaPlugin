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
    public static ConcurrentHashMap<UUID, UUID> tpaRequests = new ConcurrentHashMap<>();
    public final static String prefix = "&6&l[TpaPlugin]&r ";
    public final static String dbUrl = "jdbc:sqlite:requests.db";
    @Override
    public void onEnable() {
        try(Connection conn = DriverManager.getConnection(dbUrl)){
            String sql = """
                        CREATE TABLE IF NOT EXISTS Requests(
                        id INTEGER AUTOINCREMENT NOT NULL,
                        origin VARCHAR NOT NULL,
                        destination VARCHAR NOT NULL
                        )""";
            Statement s = conn.createStatement();
            s.execute(sql);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        getCommand("tpa").setExecutor(new Tpa());
        getCommand("tpaaccept").setExecutor(new TpaAccept());
        getCommand("tpadeny").setExecutor(new TpaDeny());
        ColoredMsg.sendToConsole(prefix + "El plugin se inicio &acorrectamente\n\t&rVersion: "
                +"&6"+ getDescription().getVersion()
                + " &r| Desarrollador: pyrumble"
        );
    }

    @Override
    public void onDisable() {
        ColoredMsg.sendToConsole(prefix + "Se desactivo &acorrectamente el plugin\n"
                + "¡Gracias por usar mi plugin!\n-pyrumble ");
    }
}
