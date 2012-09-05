/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package payroll.model;

/**
 *
 * @author Edward
 */
public class WorkerReport {

    private int month, year;
    private double salary, loan, saving, payment, withdraw;

    public WorkerReport() {
    }

    public WorkerReport(int month, int year, double salary, double loan, double saving, double withdraw, double payment) {
        this.month = month;
        this.year = year;
        this.salary = salary;
        this.loan = loan;
        this.saving = saving;
        this.payment = payment;
        this.withdraw = withdraw;
    }

    public double getWithdraw() {
        return withdraw;
    }

    public double getLoan() {
        return loan;
    }

    public int getMonth() {
        return month;
    }

    public double getPayment() {
        return payment;
    }

    public double getSalary() {
        return salary;
    }

    public double getSaving() {
        return saving;
    }

    public int getYear() {
        return year;
    }

    public double getBalance() {
        return salary + loan;
    }

    public double getSavingBalance() {
        return getBalance() + saving + payment;
    }

    public void setLoan(double loan) {
        this.loan = loan;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public void setPayment(double payment) {
        this.payment = payment;
    }

    public void setSalary(double salary) {
        this.salary = salary;
    }

    public void setSaving(double saving) {
        this.saving = saving;
    }
    
    public void setYear(int year) {
        this.year = year;
    }

    public void setWithdraw(double withdraw) {
        this.withdraw = withdraw;
    }

}
