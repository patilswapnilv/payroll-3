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

/**
 *
 * @author Edward
 */
public class Transaction {

    public static final int GENERAL = 1;
    public static final int LOAN = 2;

    private int id, customerID, type;
    private double weight, pricePerTon, wages, kiraanAsing, loanAmount;
    private ArrayList<Worker> workers;
    private Date date, created;
    private String description, normalizedWorkerID;

    private Customer customer;
    private boolean _loaded;

    public Transaction() {
        this.id = 0;
        this.customerID = 0;
        this.type = 0;
        this.weight = 0.0;
        this.pricePerTon = 0.0;
        this.wages = 0.0;
        this.kiraanAsing = 0.0;
        this.loanAmount = 0.0;
        this.workers = new ArrayList<Worker>();
        this.date = new Date();
        this.created = Calendar.getInstance().getTime();
        this.description = "";
        this.normalizedWorkerID = "";
    }

    public Transaction(int id) {
        this.id = id;
        this._load();
    }

    public Transaction(int id, int customerID, int type, double weight, double pricePerTon, double wages, double kiraanAsing, double loanAmount, ArrayList<Worker> workers, Date date, String description, String normalizedWorkerID) {
        this.id = id;
        this.customerID = customerID;
        this.type = type;
        this.weight = weight;
        this.pricePerTon = pricePerTon;
        this.wages = wages;
        this.kiraanAsing = kiraanAsing;
        this.loanAmount = loanAmount;
        this.workers = workers;
        this.date = date;
        this.description = description;
        this.normalizedWorkerID = normalizedWorkerID;
    }

    private void _load() {
        String query = "SELECT * FROM transactions WHERE id = " + this.id;
        ResultSet rs = Database.instance().execute(query);

        try {
            rs.next();
            this.setCreated(new Date(rs.getDate("created").getTime()));
            this.setCustomerID(rs.getInt("customer_id"));
            this.setDate(new Date(rs.getDate("date").getTime()));
            this.setDescription(rs.getString("description"));
            this.setPricePerTon(rs.getDouble("price_per_ton"));
            this.setWages(rs.getDouble("wages"));
            this.setWeight(rs.getDouble("weight"));
            this.setNormalizedWorkerID(rs.getString("normalized_worker_id"));

            query = "SELECT * FROM worker WHERE worker IN (" + rs.getString("workers") + ")";
            rs = Database.instance().execute(query);

            while (rs.next()) {
                this.addWorker(new Worker(rs.getInt("worker_id"), rs.getString("code"), rs.getString("name"), new Date(rs.getDate("start_date").getTime()), new Date(rs.getDate("end_date").getTime()), rs.getBoolean("is_active")));
            }

            this._loaded = true;
        } catch (SQLException ex) {
            Logger.getLogger(Transaction2.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public boolean save()
    {
        String query = "INSERT INTO transactions(type, loan_amount, customer_id, description, weight, price_per_ton, wages, date, created, normalized_worker_id) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement ps = Database.instance().createPreparedStatement(query);

        try {
            ps.setInt(1, this.type);
            ps.setDouble(2, this.loanAmount);
            ps.setInt(3, this.getCustomerID());
            ps.setString(4, this.getDescription());
            ps.setDouble(5, this.getWeight());
            ps.setDouble(6, this.getPricePerTon());
            ps.setDouble(7, this.getWages());
            ps.setDate(8, this.getSQLDate());
            ps.setDate(9, this.getSQLCreated());
            ps.setString(10, this.getNormalizedWorkerID());

            id = Database.instance().insert(ps);

            if (id > 0) {
                for (Worker worker : workers) {
                    query = "INSERT INTO transaction_workers(transaction_id, worker_id) VALUES(" + id + ", " + worker.getId() + ")";
                    Database.instance().insert(query);
                }
            }

            return id > 0 ? true : false;
        } catch (SQLException ex) {
            Logger.getLogger(Transaction.class.getName()).log(Level.SEVERE, null, ex);
            System.err.println(ex.getMessage());
        }

        return false;
    }

    public void setNormalizedWorkerID(String normalizedWorkerID) {
        this.normalizedWorkerID = normalizedWorkerID;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public void setCustomerID(int customerID) {
        this.customerID = customerID;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setKiraanAsing(double kiraanAsing) {
        this.kiraanAsing = kiraanAsing;
    }

    public void setLoanAmount(double loanAmount) {
        this.loanAmount = loanAmount;
    }

    public void setPricePerTon(double pricePerTon) {
        this.pricePerTon = pricePerTon;
    }

    public void setType(int type) {
        this.type = type;
    }

    public void setWages(double wages) {
        this.wages = wages;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public void setWorkers(ArrayList<Worker> workers) {
        this.workers = workers;
    }

    public void addWorker(Worker worker) {
        this.workers.add(worker);
    }

    public Date getCreated() {
        return created;
    }

    public String getNormalizedWorkerID() {
        String worker_id = "";
        for (Worker worker : workers) {
            worker_id += "" + worker.getId() + ",";
        }

        normalizedWorkerID = worker_id.substring(0, worker_id.length() - 1);
        return normalizedWorkerID;
    }

    public int getTotalWorkers() {
        return workers.size();
    }

    public java.sql.Date getSQLCreated() {
        return new java.sql.Date(created.getTime());
    }
    
    public Customer getCustomer() {
        if (customer == null) {
            customer = new Customer(customerID);
        }
        
        return customer;
    }

    public int getCustomerID() {
        return customerID;
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

    public int getId() {
        return id;
    }

    public double getKiraanAsing() {
        return kiraanAsing;
    }

    public double getLoanAmount() {
        return loanAmount;
    }

    public double getPricePerTon() {
        return pricePerTon;
    }

    public int getType() {
        return type;
    }

    public double getWages() {
        return wages;
    }

    public double getWeight() {
        return weight;
    }

    public ArrayList<Worker> getWorkers() {
        return workers;
    }

    public double getTotal() {
        return ((weight / 1000) * pricePerTon) + (kiraanAsing * pricePerTon);
    }

    public double getTotalSalary() {
        return ((weight / 1000) * wages) + (kiraanAsing * wages);
    }

    public double getBalance() {
        return this.getTotal() - this.getTotalSalary();
    }

    public double getWagePerWorker() {
        int size = workers.size() > 0 ? workers.size() : 1;
        return this.getTotalSalary() / size;
    }
}
