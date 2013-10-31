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
//import java.sql.Savepoint;
import java.sql.Statement;
//import java.util.List;
//import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author edward
 */
public class Database {

    private Connection connection = null;
    private static Database _instance;

    public static Database instance() {
        if (Database._instance instanceof Database == false) {
            Database._instance = new Database();
        }


        return Database._instance;
    }

    public Database() {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:payroll.sqlite");
        } catch (SQLException ex) {
            Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void disconnect() {
        if (isConnected()) {
            try {
                connection.close();
            } catch (SQLException ex) {
                Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public boolean isConnected() {
        try {
            return !connection.isClosed();
        } catch (Exception ex) {
            return false;
        }
    }

    public ResultSet execute(String query) {
        ResultSet result = null;

        try {
            Statement statement = connection.createStatement();
            result = statement.executeQuery(query);
        } catch (SQLException ex) {
            Logger.getLogger(ex.getMessage()).log(Level.WARNING, null, ex);
        }

        return result;
    }

    public ResultSet execute(PreparedStatement ps) {
        ResultSet result = null;

        try {
            result = ps.executeQuery();
        } catch (SQLException ex) {
            Logger.getLogger(ex.getMessage()).log(Level.WARNING, null, ex);
            System.err.println(ex.getMessage());
        }

        return result;
    }

    public boolean update(String query) {
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

    public boolean update(PreparedStatement ps) {
        try {
            boolean updated = ps.executeUpdate() > 0;
            ps.close();

            return updated;
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
            Logger.getLogger(ex.getMessage()).log(Level.WARNING, null, ex);
        }

        return false;
    }

    public int insert(String query) {
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

    public int insert(PreparedStatement ps) {
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

    public int last_insert_id() {
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

    public PreparedStatement createPreparedStatement(String sql) {
        try {
            return connection.prepareStatement(sql);
        } catch (SQLException ex) {
            Logger.getLogger(ex.getMessage()).log(Level.WARNING, null, ex);
            return null;
        }
    }

    public void begin() {
        try {
            connection.setAutoCommit(false);
        } catch (SQLException ex) {
            Logger.getLogger(ex.getMessage()).log(Level.WARNING, null, ex);
            try {
                connection.setAutoCommit(true);
            } catch (SQLException ex1) {
                Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex1);
            }
        }
    }

    public boolean commit() {
        try {
            connection.commit();
            connection.setAutoCommit(true);
            return true;
        } catch (SQLException ex) {
            Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
        }

        return false;
    }

    public void rollback() {
        try {
            connection.rollback();
            connection.setAutoCommit(true);
        } catch (SQLException ex) {
            Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}