package dev.rumble.tpaplugin.misc;

import java.sql.*;

import static dev.rumble.tpaplugin.TpaPlugin.dbUrl;

public class TpaHandler {
    public static boolean removeRequest(String origin, String destination){
        String sql1 = "DELETE FROM Requests WHERE origin=? AND destination=?";
        try(Connection conn = DriverManager.getConnection(dbUrl)){
            PreparedStatement s1 = conn.prepareStatement(sql1);
            s1.setString(1,origin);
            s1.setString(2,destination);
            int result = s1.executeUpdate();
            return result >= 1;

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
