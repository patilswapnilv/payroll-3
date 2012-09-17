/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package payroll.model;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import payroll.libraries.Common;
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
    private ArrayList<Worker> workers = new ArrayList<Worker>();
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
            this.setCreated(Common.convertStringToDate(rs.getString("created")));
            this.setCustomerID(rs.getInt("customer_id"));
            this.setDate(Common.convertStringToDate(rs.getString("date")));
            this.setDescription(rs.getString("description"));
            this.setPricePerTon(rs.getDouble("price_per_ton"));
            this.setWages(rs.getDouble("wages"));
            this.setWeight(rs.getDouble("weight"));
            this.setNormalizedWorkerID(rs.getString("normalized_worker_id"));
            this.setType(rs.getInt("type"));
            this.setLoanAmount(rs.getDouble("loan_amount"));

            query = "SELECT * FROM worker WHERE worker_id IN (" + rs.getString("normalized_worker_id") + ")";
            rs = Database.instance().execute(query);

            while (rs.next()) {
                this.addWorker(new Worker(rs.getInt("worker_id"), rs.getString("code"), rs.getString("name"), new Date(rs.getDate("start_date").getTime()), new Date(rs.getDate("end_date").getTime()), rs.getBoolean("is_active")));
            }

            this._loaded = true;
        } catch (SQLException ex) {
            Logger.getLogger(Transaction2.class.getName()).log(Level.SEVERE, null, ex);
            System.err.println(ex.getMessage());
        }
    }

    public boolean save()
    {
        String query = "INSERT INTO transactions(type, loan_amount, customer_id, description, weight, price_per_ton, wages, date, normalized_worker_id, kiraan_asing) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement ps = Database.instance().createPreparedStatement(query);

        try {
            ps.setInt(1, this.type);
            ps.setDouble(2, this.loanAmount);
            ps.setInt(3, this.getCustomerID());
            ps.setString(4, this.getDescription());
            ps.setDouble(5, this.getWeight());
            ps.setDouble(6, this.getPricePerTon());
            ps.setDouble(7, this.getWages());
            ps.setString(8, this.getSQLDate());
            ps.setString(9, this.getNormalizedWorkerID());
            ps.setDouble(10, this.getKiraanAsing());

            id = Database.instance().insert(ps);

            if (id > 0) {
                for (Worker worker : workers) {
                    query = "INSERT INTO transaction_workers(transaction_id, worker_id) VALUES(" + id + ", " + worker.getId() + ")";
                    Database.instance().insert(query);
                    WorkerRecord record = new WorkerRecord();
                    record.setWorkerID(worker.getId());
                    record.setDate(this.getDate());
                    record.setAmount(this.getWagePerWorker());
                    record.setTransactionID(id);
                    if (this.getType() == Transaction.GENERAL)
                        record.setType(WorkerRecord.INCOME);
                    else {
                        record.setType(WorkerRecord.LOAN);
                        record.setAmount(0 - this.getLoanAmount());
                    }
                    record.save();
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

    public String getSQLCreated() {
        Calendar calender = Calendar.getInstance();
        calender.setTime(created);

        return Common.renderSQLDate(calender);
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

    public String getSQLDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        return Common.renderSQLDate(calendar);
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

    public String getCompiledWorkerCodes() {
        String codes = "";

        for (Worker worker : workers) {
            codes += ", " + worker.getCode();
        }

        codes = codes.substring(2, codes.length());

        return codes;
    }
}
