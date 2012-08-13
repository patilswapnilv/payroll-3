/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * WorkerReportFrame.java
 *
 * Created on Aug 12, 2012, 4:54:49 AM
 */

package payroll;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import javax.swing.table.DefaultTableModel;
import payroll.libraries.Common;
import payroll.libraries.Database;
import payroll.model.Worker;
import payroll.model.WorkerRecord;
import payroll.model.WorkerReport;

/**
 *
 * @author Edward
 */
public class WorkerReportFrame extends javax.swing.JFrame {

    private ArrayList<WorkerReport> reports = new ArrayList<WorkerReport>();
    
    private DefaultTableModel tableModel;
    private Worker worker = null;

    public static final int FULL = 1;
    public static final int INCOME = 2;
    public static final int SAVING = 3;
    
    /** Creates new form WorkerReportFrame */
    public WorkerReportFrame(int type, Worker worker, ArrayList<WorkerReport> reports) {
        initComponents();

        this.reports = reports;
        this.worker = worker;

        try {
            this.setExtendedState(this.MAXIMIZED_BOTH);
            this.setAlwaysOnTop(true);
            this.toFront();
            this.requestFocus();
            this.setAlwaysOnTop(false);
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
        }

        if (type == FULL) {
            this.full_setup();
        } else if (type == INCOME) {
            this.monthly_income_setup();
        } else if (type == SAVING) {
            this.saving_setup();
        }
    }

    private void full_setup() {
        tableModel = new DefaultTableModel(
            new Object[][] {},
            new String[] {
                "Tempoh", "Gaji", "Pinjaman", "Baki", "Simpanan", "Bayaran", "Baki"
            }
        );
        tblReport.setModel(tableModel);

        double totalSalary = 0.0, totalLoan = 0.0, totalBalance = 0.0, totalSaving = 0.0, totalPayment = 0.0, totalSavingBalance = 0.0;

        for (WorkerReport report : reports) {
            
            totalSalary += report.getSalary();
            totalLoan += report.getLoan();
            totalBalance += report.getBalance();
            totalSalary += report.getSaving();
            totalPayment += report.getPayment();
            totalSavingBalance += report.getSavingBalance();
            
            Object[] objects = new Object[] {
                new String(report.getMonth() + "/" + report.getYear()),
                new String(Common.currency(report.getSalary())),
                new String(Common.currency(report.getLoan())),
                new String(Common.currency(report.getBalance())),
                new String(Common.currency(report.getSaving())),
                new String(Common.currency(report.getPayment())),
                new String(Common.currency(report.getSavingBalance())),
            };

            tableModel.addRow(objects);
        }

        tableModel.addRow(new Object[] {
            new String("Jumlah"),
            new String(Common.currency(totalSalary)),
            new String(Common.currency(totalLoan)),
            new String(Common.currency(totalBalance)),
            new String(Common.currency(totalSaving)),
            new String(Common.currency(totalPayment)),
            new String(Common.currency(totalSavingBalance))
        });
    }

    private void monthly_income_setup() {
        tableModel = new DefaultTableModel(
            new Object[][] {},
            new String[] {
                "Tempoh", "Gaji", "Pinjaman", "Baki"
            }
        );
        tblReport.setModel(tableModel);

        double totalSalary = 0.0, totalLoan = 0.0, totalBalance = 0.0;

        for (WorkerReport report : reports) {

            totalSalary += report.getSalary();
            totalLoan += report.getLoan();
            totalBalance += report.getBalance();
            totalSalary += report.getSaving();

            Object[] objects = new Object[] {
                new String(report.getMonth() + "/" + report.getYear()),
                new String(Common.currency(report.getSalary())),
                new String(Common.currency(report.getLoan())),
                new String(Common.currency(report.getBalance())),
            };

            tableModel.addRow(objects);
        }
    }

    private void saving_setup() {
        tableModel = new DefaultTableModel(
            new Object[][] {},
            new String[] {
                "Tempoh", "Simpanan", "Bayaran", "Baki"
            }
        );
        tblReport.setModel(tableModel);

        double totalSaving = 0.0, totalPayment = 0.0, totalSavingBalance = 0.0;

        for (WorkerReport report : reports) {

            totalPayment += report.getPayment();
            totalSavingBalance += report.getSavingBalance();

            Object[] objects = new Object[] {
                new String(report.getMonth() + "/" + report.getYear()),
                new String(Common.currency(report.getSaving())),
                new String(Common.currency(report.getPayment())),
                new String(Common.currency(report.getSavingBalance())),
            };

            tableModel.addRow(objects);
        }
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        tblReport = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        tblReport.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        jScrollPane1.setViewportView(tblReport);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 980, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 578, Short.MAX_VALUE)
                .addContainerGap())
        );

        java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        setBounds((screenSize.width-1016)/2, (screenSize.height-638)/2, 1016, 638);
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tblReport;
    // End of variables declaration//GEN-END:variables

}
