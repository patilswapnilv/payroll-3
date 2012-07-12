/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package payroll.libraries;

import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author edward
 */
public class Database
{
    private Connection connection = null;

    public Database(String database)
    {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:" + database + ".db");
        } catch (SQLException ex) {
            Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public boolean isConnected()
    {
        try {
            return ! connection.isClosed();
        } catch (Exception ex) {
            return false;
        }
    }
}
