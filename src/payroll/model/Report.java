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

    private Transaction transaction;
    private ArrayList<Worker> workers;

    public Report() {
    }

    public Report(Transaction transaction, ArrayList<Worker> workers) {
        this.transaction = transaction;
        this.workers = workers;
    }

    public void setTransaction(Transaction transaction) {
        this.transaction = transaction;
    }

    public void setWorkers(ArrayList<Worker> workers) {
        this.workers = workers;
    }

    public Transaction getTransaction() {
        return transaction;
    }

    public ArrayList<Worker> getWorkers() {
        return workers;
    }

}
