/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package payroll.model;

import java.util.ArrayList;

/**
 *
 * @author Edward
 */
public class ReportSalary {
    private ArrayList<WorkerRecord> records;
    private Worker worker;

    public ReportSalary() {
        this.worker = new Worker();
        records = new ArrayList<WorkerRecord>();
    }

    public ReportSalary(ArrayList<WorkerRecord> records, Worker worker) {
        this.records = records;
        this.worker = worker;
    }

    public Worker getWorker() {
        return worker;
    }

    public ArrayList<WorkerRecord> getRecords() {
        return records;
    }

    public void setWorker(Worker worker) {
        this.worker = worker;
    }

    public void addRecord(WorkerRecord record) {
        this.records.add(record);
    }
}
