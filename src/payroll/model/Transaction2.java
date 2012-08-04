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
import org.apache.commons.lang3.StringUtils;
import payroll.libraries.Database;

/**
 *
 * @author Edward
 */
public class Transaction2 {

    private int id, customerID;
    private double weight, pricePerTon, wages, kiraanAsing;
    private ArrayList<Worker> workers;
    private Date date, created;
    private String description;

    private Customer customer;

    private boolean _loaded;

    public Transaction2() {
        this.id = 0;
        this.customerID = 0;
        this.weight = 0.0;
        this.pricePerTon = 0.0;
        this.wages = 0;
        this.workers = new ArrayList<Worker>();
        this.date = new Date();
        this.created = Calendar.getInstance().getTime();
        this.kiraanAsing = 0.0;
    }

    public Transaction2(int id) {
        this.id = id;
        this._load();
    }

    public Transaction2(int id, int customerID, String description, double weight, double pricePerTon, double wages, double kiraanAsing, ArrayList<Worker> workers, Date date) {
        this.id = id;
        this.customerID = customerID;
        this.description = description;
        this.weight = weight;
        this.pricePerTon = pricePerTon;
        this.wages = wages;
        this.kiraanAsing = kiraanAsing;
        this.workers = workers;
        this.date = date;
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
        String query = "INSERT INTO transactions(customer_id, description, weight, price_per_ton, wages, workers, date, created) VALUES(?, ?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement ps = Database.instance().createPreparedStatement(query);

        try {
            ps.setInt(1, this.getCustomerID());
            ps.setString(2, this.getDescription());
            ps.setDouble(3, this.getWeight());
            ps.setDouble(4, this.getPricePerTon());
            ps.setDouble(5, this.getWages());
            ps.setString(6, this.getNormalizedWorkers());
            ps.setDate(7, this.getSQLDate());
            ps.setDate(8, this.getSQLCreated());

            id = Database.instance().insert(ps);

            return id > 0 ? true : false;
        } catch (SQLException ex) {
            Logger.getLogger(Transaction2.class.getName()).log(Level.SEVERE, null, ex);
            System.err.println(ex.getMessage());
        }

        return false;
    }

    public Date getCreated() {
        if (created == null) {
            created = Calendar.getInstance().getTime();
        }
        
        return created;
    }

    public java.sql.Date getSQLCreated() {
        return new java.sql.Date(getCreated().getTime());
    }

    public int getCustomerID() {
        return customerID;
    }

    public Customer getCustomer() {
        if (customer == null) {
            customer = new Customer(customerID);
        }

        return customer;
    }

    public Date getDate() {
        return date;
    }

    public java.sql.Date getSQLDate() {
        return new java.sql.Date(date.getTime());
    }

    public int getId() {
        return id;
    }

    public double getPricePerTon() {
        return pricePerTon;
    }

    public double getWages() {
        return wages;
    }

    public double getWeight() {
        return weight;
    }

    public double getKiraanAsing() {
        return kiraanAsing;
    }

    public ArrayList<Worker> getWorkers() {
        return workers;
    }

    public String getNormalizedWorkers() {
        int[] ids = new int[workers.size()];
        int i = 0;

        for (Worker worker : workers) {
            ids[i] = worker.getId();
            i ++;
        }

        return StringUtils.join(ids, ',');
    }

    public String getDescription() {
        return description;
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

    public void setCreated(Date created) {
        this.created = created;
    }

    public void setCustomerID(int customerID) {
        this.customerID = customerID;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setPricePerTon(double pricePerTon) {
        this.pricePerTon = pricePerTon;
    }

    public void setWages(double wages) {
        this.wages = wages;
    }

    public void setKiraanAsing(double kiraanAsing) {
        this.kiraanAsing = kiraanAsing;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public void setWorkers(ArrayList<Worker> workers) {
        this.workers = workers;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void addWorker(Worker worker) {
        this.workers.add(worker);
    }

    public String[] denormalizedWorkerIds(String worker_ids) {
        return StringUtils.split(worker_ids, ',');
    }
}
