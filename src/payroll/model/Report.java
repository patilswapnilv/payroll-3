/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package payroll.model;

import java.util.ArrayList;

/**
 *
 * @author Tomato
 */
public class Report {

    private ArrayList<Transaction> transactions;
    // Worker which selected to be display
    private ArrayList<Worker> workers;

    public Report() {
    }

    public Report(ArrayList<Transaction> transactions, ArrayList<Worker> workers) {
        this.transactions = transactions;
        this.workers = workers;
    }

    public void setTransactions(ArrayList<Transaction> transactions) {
        this.transactions = transactions;
    }

    public void setWorkers(ArrayList<Worker> workers) {
        this.workers = workers;
    }

    public ArrayList<Transaction> getTransactions() {
        return transactions;
    }

    public ArrayList<Worker> getWorkers() {
        return workers;
    }
}
