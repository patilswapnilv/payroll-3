/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package payroll.model;

/**
 *
 * @author Edward
 */
public class ReportCalculation {
    private double salary, loan, balance;
    private double payment;
    private int workerID;

    public ReportCalculation(int workerID) {
        this.salary = 0.0;
        this.loan = 0.0;
        this.balance = 0.0;
        this.workerID = workerID;
    }

    public ReportCalculation(double salary, double loan, double balance, int workerID) {
        this.salary = salary;
        this.loan = loan;
        this.balance = balance;
        this.workerID = workerID;
        this.payment = 0.0;
    }

    public double getBalance() {
        balance = salary - loan;
        return balance;
    }

    public double getLoan() {
        return loan;
    }

    public double getSalary() {
        return salary;
    }

    public int getWorkerID() {
        return workerID;
    }

    public void setLoan(double loan) {
        this.loan += loan;
    }

    public void setSalary(double salary) {
        this.salary += salary;
    }

    public void setPayment(double payment) {
        this.payment += payment;
    }

    public double getTotalBalance() {
        return this.getBalance() + payment;
    }
}
