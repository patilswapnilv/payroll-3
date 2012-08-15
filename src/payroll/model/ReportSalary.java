/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package payroll.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;

/**
 *
 * @author Edward
 */
public class ReportSalary {
    private ArrayList<ReportSalaryRecord> records;
    private Hashtable workerDictionary = new Hashtable();
    private Date date;

    public ReportSalary(ArrayList<Worker> workers, Date date) {
        this.date = date;
        this.records = new ArrayList<ReportSalaryRecord>();

        int index = 0;
        for (Worker worker : workers) {
            records.add(new ReportSalaryRecord(worker));
            workerDictionary.put(worker.getId(), index);

            index ++;
        }
    }

    public void setWorkerSalary(int workerID, double amount) {
        int index = Integer.parseInt(workerDictionary.get(workerID).toString());
        records.get(index).setAmount(amount);
    }

    public double getWorkerSalary(int workerID) {
        int index = Integer.parseInt(workerDictionary.get(workerID).toString());
        return records.get(index).getAmount();
    }

    public Date getDate() {
        return date;
    }
}
