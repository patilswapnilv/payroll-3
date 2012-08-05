/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package payroll.model;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import payroll.libraries.Database;
import payroll.Application;

/**
 * @author ho
 */
public class WorkerRecord {

    private int id, workerID;
    private double amount;
    private String description;
    boolean isPay;
    private Date date, created;

    private boolean _loaded = false;

    public WorkerRecord() {
        this.id = 0;
        this.workerID = 0;
        this.amount = 0.0;
        this.description = "";
        this.isPay = false;
        this.date = new Date();
        this.created = Calendar.getInstance().getTime();
    }

    public WorkerRecord(int id) {
        this.id = id;
        this._load();
    }

    public WorkerRecord(int id, int workerID, double amount, String description, boolean isPay, Date date) {
        this.id = id;
        this.workerID = workerID;
        this.amount = amount;
        this.description = description;
        this.isPay = isPay;
        this.date = date;
    }

    private void _load() {
        String query = "SELECT * FROM workerRecord WHERE id = " + this.id;
        ResultSet rs = Database.instance().execute(query);

        try {
            rs.next();
            this.setCreated(new Date(rs.getDate("created").getTime()));
            this.setWorkerID(rs.getInt("worker_id"));
            this.setDate(new Date(rs.getDate("date").getTime()));
            this.setDescription(rs.getString("description"));
            this.setAmount(rs.getDouble("amount"));
            this.setIsPay(rs.getBoolean("is_pay"));
            this._loaded = true;
        } catch (SQLException ex) {
            Logger.getLogger(WorkerRecord.class.getName()).log(Level.SEVERE, null, ex);
        }        
    }

    public boolean save()
    {
        String query = "";

        if ( ! this._loaded) {
            query = "INSERT INTO workerRecord(created, worker_id, date, description, amount, is_pay) VALUES(?, ?, ?, ?, ?)";
        }

        PreparedStatement ps = Application.db.createPreparedStatement(query);

        try {
            ps.setDate(1, this.getSQLCreated());
            ps.setInt(2, this.getWorkerID());
            ps.setDate(3, this.getSQLDate());
            ps.setString(4, this.getDescription());
            ps.setDouble(5, this.amount);
            ps.setBoolean(6, this.getIsPay());
        } catch (SQLException ex) {
            Logger.getLogger(ex.getMessage()).log(Level.WARNING, null, ex);
            System.err.println(ex.getMessage());
        }

        if ( ! this._loaded) {
            this.id = Application.db.insert(ps);
            return this.id != 0 ? true : false;
        } else {
            return Application.db.update(ps);
        }
        
    }

    public boolean loaded()
    {
        return this._loaded;
    }

    public void setCreated(Date created) {
        this.created = created;
    }    

    public void setWorkerID(int workerID) {
        this.workerID = workerID;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public void setIsPay(boolean isPay) {
        this.isPay = isPay;
    }

    public void setLoaded(boolean _loaded) {
        this._loaded = _loaded;
    }

    public java.sql.Date getSQLCreated() {
        return new java.sql.Date(created.getTime());
    }

    public int getWorkerID() {
        return workerID;
    }

    public Date getDate() {
        return date;
    }

    public java.sql.Date getSQLDate() {
        return new java.sql.Date(date.getTime());
    }

    public String getDescription() {
        return description;
    }

    public boolean getIsPay() {
        return isPay;
    }

}
