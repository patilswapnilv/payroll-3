/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package payroll.model;

/**
 *
 * @author Edward
 */
public class ReportSaving {
    private double previous, current;
    private Worker worker;

    public ReportSaving() {
        this.previous = 0.00;
        this.current = 0.00;
        this.worker = new Worker();
    }

    public ReportSaving(double previous, double current, Worker worker) {
        this.previous = previous;
        this.current = current;
        this.worker = worker;
    }

    public double getCurrent() {
        return current;
    }

    public double getPrevious() {
        return previous;
    }

    public double getBalance() {
        return previous + current;
    }

    public Worker getWorker() {
        return worker;
    }

    public void setCurrent(double current) {
        this.current = current;
    }

    public void setPrevious(double previous) {
        this.previous = previous;
    }

    public void setWorker(Worker worker) {
        this.worker = worker;
    }

}
