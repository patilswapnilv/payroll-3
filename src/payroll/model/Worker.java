/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package payroll.model;

import java.util.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import payroll.Application;
//import payroll.Main;
import payroll.libraries.Common;
import payroll.libraries.Database;

/*  @author Tomato  */
public class Worker {

    int id;
    String code;
    String name;
    Date registerDate;
    Date returnDate;
    boolean status;

    private boolean _loaded = false;

    public Worker() {
        id = 0;
        code = null;
        name = null;
        registerDate = null;
        returnDate = null;
        status = false;
    }

    public Worker(int id) {
        this._load(id);
    }

    public Worker(int id, String code, String name, Date registerDate, Date returnDate, boolean status) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.registerDate = registerDate;
        this.returnDate = returnDate;
        this.status = status;
    }

    private boolean _load(int id)
    {
        String query = "SELECT * FROM worker WHERE worker_id = " + id;
        ResultSet rs = Application.db.execute(query);
        try {
            this.code = rs.getString("code");
            this.name = rs.getString("name");
            this.registerDate = Common.convertStringToDate(rs.getString("start_date"));
            this.returnDate = Common.convertStringToDate(rs.getString("end_date"));
            this.status = rs.getBoolean("is_active");
            this.id = id;
            this._loaded = true;
        } catch (SQLException ex) {
            Logger.getLogger(ex.getMessage()).log(Level.WARNING, null, ex);
        }

        return this.loaded();
    }

    public boolean loaded()
    {
        return this._loaded;
    }

    public boolean save()
    {
        String query = "";

        if ( ! this._loaded) {
            query = "INSERT INTO worker(code, name, start_date, end_date, is_active) VALUES(?, ?, ?, ?, ?)";
        } else {
            query = "UPDATE worker SET code = ?, name = ?, start_date = ?, end_date = ?, is_active = ? WHERE worker_id = " + id;
        }

        PreparedStatement ps = Application.db.createPreparedStatement(query);

        try {
            ps.setString(1, this.getCode());
            ps.setString(2, this.getName());
            ps.setString(3, Common.renderSQLDate(this.getRegisterDate()));
            ps.setString(4, Common.renderSQLDate(this.getReturnDate()));
            ps.setBoolean(5, this.getStatus());
        } catch (SQLException ex) {
            Logger.getLogger(ex.getMessage()).log(Level.WARNING, null, ex);
            System.err.println(ex.getMessage());
        }

        if ( ! this._loaded) {
            this.id = Database.instance().insert(ps);
            return this.id != 0 ? true : false;
        } else {
            return Application.db.update(ps);
        }
    }

    public String getCode() {
        return code;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Date getRegisterDate() {
        return registerDate;
    }

    public Date getReturnDate() {
        return returnDate;
    }

    public java.sql.Date getSQLRegisterDate() {
        return new java.sql.Date(registerDate.getTime());
    }

    public java.sql.Date getSQLReturnDate() {
        return new java.sql.Date(returnDate.getTime());
    }

    public boolean getStatus() {
        return status;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setLoaded(boolean _loaded) {
        this._loaded = _loaded;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setRegisterDate(Date registerDate) {
        this.registerDate = registerDate;
    }

    public void setReturnDate(Date returnDate) {
        this.returnDate = returnDate;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public static String join(ArrayList<Worker> workers) {
        String id = "";

        for (Worker worker : workers) {
            id += worker.getId() + ",";
        }

        id = id.isEmpty() ? "" : id.substring(0, id.length() - 1);

        return id;
    }

    public static ArrayList<Worker> bulk(String id) {
        ArrayList<Worker> workers = new ArrayList<Worker>();

        String query = "SELECT * FROM worker WHERE worker_id IN (" + id + ")";
        ResultSet rs = Database.instance().execute(query);

        try {
            while (rs.next()) {
                workers.add(new Worker(rs.getInt("worker_id"), rs.getString("code"), rs.getString("name"), Common.convertStringToDate(rs.getString("start_date")), Common.convertStringToDate(rs.getString("end_date")), rs.getBoolean("is_active")));
            }
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }

        return workers;
    }
    
    public static String getCode(String id) {
        String query = "SELECT code FROM worker WHERE worker_id = " + id;
        ResultSet rs = Database.instance().execute(query);
        String code = null;
        try {
            while (rs.next()) {        
                code = rs.getString("code");
            }
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }                
        return code;
    }
    
}
