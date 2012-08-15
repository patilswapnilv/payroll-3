/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package payroll.model;

/**
 *
 * @author Edward
 */
public class ReportSalaryRecord {
    private Worker worker;
    private double amount;

    public ReportSalaryRecord(Worker worker) {
        this.worker = worker;
        this.amount = 0.0;
    }

    public double getAmount() {
        return amount;
    }

    public Worker getWorker() {
        return worker;
    }

    public void setAmount(double amount) {
        this.amount += amount;
    }
}
