/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package payroll.libraries;

import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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

    public void disconnect()
    {
        if (isConnected()) {
            try {
                connection.close();
            } catch (SQLException ex) {
                Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
            }
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

    public ResultSet execute(String query)
    {
        ResultSet result = null;

        try {
            Statement statement = connection.createStatement();
            result = statement.executeQuery(query);
        } catch (SQLException ex) {
            Logger.getLogger(ex.getMessage()).log(Level.WARNING, null, ex);
        }

        return result;
    }

    public boolean update(String query)
    {
        try {
            Statement statement = connection.createStatement();
            statement.executeUpdate(query);

            return true;
        } catch (SQLException ex) {
            Logger.getLogger(ex.getMessage()).log(Level.WARNING, null, ex);
        }

        return false;
    }
}
