/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package payroll.model;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import payroll.libraries.Database;
import payroll.Application;
import payroll.libraries.Common;

/**
 * @author ho
 */
public class WorkerRecord {

    public static final int PAYMENT = 1;
    public static final int LOAN = 2;
    public static final int SAVING = 3;
    public static final int INCOME = 4;
    public static final int WITHDRAW = 5;

    private int id, workerID, type, transaction_id;
    private double amount;
    private String description;
    private Date date, created;

    private boolean _loaded = false;

    public WorkerRecord() {
        this.id = 0;
        this.workerID = 0;
        this.amount = 0.0;
        this.description = "";
        this.date = new Date();
        this.created = Calendar.getInstance().getTime();
        this.type = 0;
        this.transaction_id = 0;
    }

    public WorkerRecord(int id) {
        this.id = id;
        this.transaction_id = 0;
        this._load();
    }

    public WorkerRecord(int id, int workerID, int type, double amount, String description, Date date) {
        this.id = id;
        this.workerID = workerID;
        this.type = type;
        this.amount = amount;
        this.description = description;
        this.date = date;
        this.transaction_id = 0;
    }

    private void _load() {
        String query = "SELECT * FROM workerRecord WHERE id = " + this.id;
        ResultSet rs = Database.instance().execute(query);

        try {
            rs.next();
            this.setCreated(Common.convertStringToDate(rs.getString("created")));
            this.setWorkerID(rs.getInt("worker_id"));
            this.setDate(Common.convertStringToDate(rs.getString("date")));
            this.setDescription(rs.getString("description"));
            this.setAmount(rs.getDouble("amount"));
            this.setType(rs.getInt("type"));
            this.setTransactionID(rs.getInt("transaction_id"));
            this._loaded = true;
        } catch (SQLException ex) {
            Logger.getLogger(WorkerRecord.class.getName()).log(Level.SEVERE, null, ex);
        }        
    }

    public boolean save()
    {
        String query = "";

        if ( ! this._loaded) {
            query = "INSERT INTO workerRecord(created, worker_id, date, description, amount, type, transaction_id) VALUES(?, ?, ?, ?, ?, ?, ?)";
        }

        PreparedStatement ps = Application.db.createPreparedStatement(query);

        try {
            Calendar dateCalendar = Calendar.getInstance();
            dateCalendar.setTime(this.getDate());
            Calendar createdCalendar = Calendar.getInstance();
            createdCalendar.setTime(this.getCreated());

            ps.setString(1, Common.renderSQLDate(createdCalendar));
            ps.setInt(2, this.getWorkerID());
            ps.setString(3, Common.renderSQLDate(dateCalendar));
            ps.setString(4, this.getDescription());
            ps.setDouble(5, this.amount);
            ps.setInt(6, this.getType());
            ps.setInt((7), this.getTransactionID());
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

    public void setId(int id) {
        this.id = id;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public void setLoaded(boolean _loaded) {
        this._loaded = _loaded;
    }

    public void setType(int type) {
        this.type = type;
    }

    public void setTransactionID(int transaction_id) {
        this.transaction_id = transaction_id;
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

    public double getAmount() {
        return amount;
    }

    public Date getCreated() {
        return created;
    }

    public int getId() {
        return id;
    }

    public int getType() {
        return type;
    }

    public int getTransactionID() {
        return transaction_id;
    }

    public static double getWorkerSalary(int workerID, Date dateFrom , Date dateTo) {
        double salary = 0.00;
        String query = "SELECT SUM(amount) AS total_salary FROM workerRecord WHERE worker_id = " + workerID + " AND type = 4 ";

        if (dateFrom != null) {
            query += "AND date >= \"" + Common.renderDisplayDate(dateFrom) + "\" ";
        }

        if (dateTo != null) {
            query += "AND date <= \"" + Common.renderDisplayDate(dateTo) + "\" ";
        }

        ResultSet rs = Database.instance().execute(query);
        try {
            rs.next();

            salary = rs.getDouble("total_salary");
            return salary;
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }

        return 0.0;
    }

}
