/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package payroll.libraries;

import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Map;
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
            connection = DriverManager.getConnection("jdbc:sqlite:" + database);
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

    public ResultSet execute(PreparedStatement ps)
    {
        ResultSet result = null;

        try {
            result = ps.executeQuery();
        } catch (SQLException ex) {
            Logger.getLogger(ex.getMessage()).log(Level.WARNING, null, ex);
            System.err.println(ex.getMessage());
        }

        return result;
    }

    public boolean update(String query)
    {
        try {
            Statement statement = connection.createStatement();
            boolean updated = statement.executeUpdate(query) > 0;
            statement.close();

            return updated;
        } catch (SQLException ex) {
            Logger.getLogger(ex.getMessage()).log(Level.WARNING, null, ex);
        }

        return false;
    }

    public boolean update(PreparedStatement ps)
    {
        try {
            boolean updated = ps.executeUpdate() > 0;
            ps.close();

            return updated;
        } catch (SQLException ex) {
            Logger.getLogger(ex.getMessage()).log(Level.WARNING, null, ex);
        }

        return false;
    }

    public int insert(String query)
    {
        try {
            Statement statement = connection.createStatement();
            if (statement.executeUpdate(query) > 0) {
                statement.close();
                return this.last_insert_id();
            }
        } catch (SQLException ex) {
            Logger.getLogger(ex.getMessage()).log(Level.WARNING, null, ex);
            System.err.println(ex.getMessage());
        }

        return 0;
    }

    public int insert(PreparedStatement ps)
    {
        try {
            if (ps.executeUpdate() > 0) {
                ps.close();
                return this.last_insert_id();
            }
        } catch (SQLException ex) {
            Logger.getLogger(ex.getMessage()).log(Level.WARNING, null, ex);
            System.err.println(ex.getMessage());
        }

        return 0;
    }

    public int last_insert_id()
    {
        ResultSet rs = this.execute("SELECT last_insert_rowid()");
        try {
            rs.next();
            int id = rs.getInt(1);
            rs.close();
            return id;
        } catch (SQLException ex) {
            Logger.getLogger(ex.getMessage()).log(Level.WARNING, null, ex);
            System.err.println(ex.getMessage());
        }

        return 0;

    }

    public PreparedStatement createPreparedStatement(String sql)
    {
        try {
            return connection.prepareStatement(sql);
        } catch (SQLException ex) {
            Logger.getLogger(ex.getMessage()).log(Level.WARNING, null, ex);
            return null;
        }
    }
}
