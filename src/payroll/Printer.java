/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package payroll;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import payroll.libraries.Common;
import payroll.libraries.Database;
import payroll.model.ReportCalculation;
import payroll.model.Transaction;
import payroll.model.Worker;

/**
 *
 * @author Edward
 */
public class Printer implements Printable {

    ArrayList<ReportCalculation> calculations = new ArrayList<ReportCalculation>();
    private ArrayList<String> headers = new ArrayList<String>();
    private ArrayList<Worker> selected = new ArrayList<Worker>();
    private ResultSet results;
    private String query = "";
    private Main parent;

    private ArrayList<Integer> transactionIDs = new ArrayList<Integer>();

    final int printCap = 30;
    private int printerPointer = 0;
    private int pagesNeeded = 1;
    private int extraPage = -1;
    private int itemCount = 0;
    private int x = 0;
    private int y = 0;

    boolean printOrNot = true;

    boolean header = true;
    boolean workerHeader = true;
    boolean workerOnly = false;

    int workerHeaderIndex = 0;
    int workerIndex = 0;
    int transactionIndex = 0;

    public Printer(Main parent, ArrayList<Worker> selected, String query) {
        this.parent = parent;
        this.query = query;
        this.selected = selected;
        this.setup();
    }

    private void setup() {
        ResultSet results = Database.instance().execute(query);
        this.results = results;

        try {
            itemCount = 0;
            while (results.next()) {
                transactionIDs.add(results.getInt(1));
            }

            itemCount = transactionIDs.size();
        } catch (SQLException ex) {
            System.err.println("Error: Unable to get results size. Detail: " + ex.getMessage());
            ex.printStackTrace();
        }

        for (Worker worker : selected) {
            calculations.add(new ReportCalculation(worker.getId()));
        }

        pagesNeeded = (int) Math.ceil((double) itemCount / printCap);

        System.out.println("There are " + itemCount + " items, We need: " + pagesNeeded + " pages(s)");
    }

    public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException {
        Graphics2D g = (Graphics2D) graphics;
        g.translate(pageFormat.getImageableX(), pageFormat.getImageableY());

        printOrNot = printOrNot ? false : true;

        if (pageIndex >= pagesNeeded) {
            System.out.println("No more pages");
            return NO_SUCH_PAGE;
        }

        if (printOrNot) {
            System.out.println("Printing...");

            x = 20;
            y = 80;
            int size = 0;

            if (header) {
                g.setFont(new Font("Calibri", Font.BOLD, 12));
                if (parent.chkMonthlyReportDate.isSelected()) {
                    g.drawString("Tarikh", x, y);
                    size = 60;
                    g.drawLine(x - 5, y + 5, x + 5 + size, y + 5);
                    g.drawLine(x - 5, y - 35, x + 5 + size, y - 35);
                    g.drawLine(x - 5, y + 5, x - 5, y - 35);
                    g.drawLine(x + 5 + size, y + 5, x + 5 + size, y - 35);
                    x += size + 10;

                }

                if (parent.chkMonthlyReportClientName.isSelected()) {
                    g.drawString("Pelanggan", x, y);
                    size = 80;
                    g.drawLine(x - 5, y + 5, x + 5 + size, y + 5);
                    g.drawLine(x - 5, y - 35, x + 5 + size, y - 35);
                    g.drawLine(x - 5, y + 5, x - 5, y - 35);
                    g.drawLine(x + 5 + size, y + 5, x + 5 + size, y - 35);
                    x += size + 10;
                }

                if (parent.chkMonthlyReportDescription.isSelected()) {
                    g.drawString("Keterangan", x, y);
                    size = 120;
                    g.drawLine(x - 5, y + 5, x + 5 + size, y + 5);
                    g.drawLine(x - 5, y - 35, x + 5 + size, y - 35);
                    g.drawLine(x - 5, y + 5, x - 5, y - 35);
                    g.drawLine(x + 5 + size, y + 5, x + 5 + size, y - 35);
                    x += size + 10;
                }

                if (parent.chkMonthlyReportKiraanAsing.isSelected()) {
                    g.drawString("Kiraan", x, y - 15);
                    g.drawString("Asing", x, y);
                    size = 50;
                    g.drawLine(x - 5, y + 5, x + 5 + size, y + 5);
                    g.drawLine(x - 5, y - 35, x + 5 + size, y - 35);
                    g.drawLine(x - 5, y + 5, x - 5, y - 35);
                    g.drawLine(x + 5 + size, y + 5, x + 5 + size, y - 35);
                    x += size + 10;
                }

                if (parent.chkMonthlyReportWeight.isSelected()) {
                    g.drawString("Berat KG", x, y);
                    size = 50;
                    g.drawLine(x - 5, y + 5, x + 5 + size, y + 5);
                    g.drawLine(x - 5, y - 35, x + 5 + size, y - 35);
                    g.drawLine(x - 5, y + 5, x - 5, y - 35);
                    g.drawLine(x + 5 + size, y + 5, x + 5 + size, y - 35);
                    x += size + 10;
                }

                if (parent.chkMonthlyReportPricePerTon.isSelected()) {
                    g.drawString("Seton", x, y);
                    g.drawString("Harga", x, y - 15);
                    size = 50;
                    g.drawLine(x - 5, y + 5, x + 5 + size, y + 5);
                    g.drawLine(x - 5, y - 35, x + 5 + size, y - 35);
                    g.drawLine(x - 5, y + 5, x - 5, y - 35);
                    g.drawLine(x + 5 + size, y + 5, x + 5 + size, y - 35);
                    x += size + 10;
                }

                if (parent.chkMonthlyReportTotalReceived.isSelected()) {
                    g.drawString("Diterima", x, y);
                    g.drawString("Jumlah", x, y - 15);
                    size = 50;
                    g.drawLine(x - 5, y + 5, x + 5 + size, y + 5);
                    g.drawLine(x - 5, y - 35, x + 5 + size, y - 35);
                    g.drawLine(x - 5, y + 5, x - 5, y - 35);
                    g.drawLine(x + 5 + size, y + 5, x + 5 + size, y - 35);
                    x += size + 10;
                }
                header = false;
            }
            
            if (workerHeader) {
                int total = selected.size();
                for (int i = workerHeaderIndex; i < total; i ++) {
                    System.out.println("X index: " + x);
                    size = 180;
                    if (x + size > 780.0) {
                        workerHeaderIndex = i;
                        workerHeader = true;
                        pagesNeeded ++;
                        break;
                    }
                    
                    g.drawString(selected.get(i).getCode() + " " + selected.get(i).getName(), x, y - 18);
                    g.drawString("Gaji", x, y);
                    g.drawString("Pinjaman", x + 50, y);
                    g.drawString("Baki", x + 110, y);
                    

                    g.drawLine(x - 5 + 50, y + 5, x - 5 + 50, y - 15);
                    g.drawLine(x - 5 + 110, y + 5, x - 5 + 110, y - 15);

                    g.drawLine(x - 5, y + 5, x + 5 + size, y + 5);
                    g.drawLine(x - 5, y - 15, x + 5 + size, y - 15);
                    g.drawLine(x - 5, y - 35, x + 5 + size, y - 35);
                    g.drawLine(x - 5, y + 5, x - 5, y - 35);
                    g.drawLine(x + 5 + size, y + 5, x + 5 + size, y - 35);
                    x += size + 10;

                    workerHeader = false;
                }
            }

            x = 20;
            y += 20;

            int transactionCount = transactionIDs.size();
            for (int i = 0; i < transactionCount; i ++) {
                Transaction transaction = new Transaction(transactionIDs.get(i));
                g.setFont(new Font("Calibri", Font.PLAIN, 12));
                if ( ! workerOnly) {
                    workerIndex = 0;
                    if (parent.chkMonthlyReportDate.isSelected()) {
                        g.drawString(Common.renderDisplayDate(transaction.getDate()), x, y);
                        size = 60;
                        x += size + 10;
                    }

                    if (parent.chkMonthlyReportClientName.isSelected()) {
                        if (transaction.getType() == Transaction.GENERAL)
                            g.drawString(transaction.getCustomer().getName(), x, y);
                        else
                            g.drawString("Pinjaman", x, y);
                        size = 80;
                        x += size + 10;
                    }

                    if (parent.chkMonthlyReportDescription.isSelected()) {
                        g.drawString(transaction.getDescription(), x, y);
                        size = 120;
                        x += size + 10;
                    }

                    if (parent.chkMonthlyReportKiraanAsing.isSelected()) {
                        if (transaction.getType() == Transaction.GENERAL)
                            g.drawString("" + transaction.getKiraanAsing(), x, y);
                        size = 50;
                        x += size + 10;
                    }

                    if (parent.chkMonthlyReportWeight.isSelected()) {
                        if (transaction.getType() == Transaction.GENERAL)
                            g.drawString("" + transaction.getWeight(), x, y);
                        size = 50;
                        x += size + 10;
                    }

                    if (parent.chkMonthlyReportPricePerTon.isSelected()) {
                        if (transaction.getType() == Transaction.GENERAL)
                            g.drawString("" + transaction.getPricePerTon(), x, y);
                        size = 50;
                        x += size + 10;
                    }

                    if (parent.chkMonthlyReportTotalReceived.isSelected()) {
                        if (transaction.getType() == Transaction.GENERAL)
                            g.drawString("" + transaction.getTotal(), x, y);
                        size = 50;
                        x += size + 10;
                    }
                }

                int total = selected.size();
                int i2 = workerOnly ? workerIndex : 0;
                for (int c = i2; c < total; c ++) {
                    size = 180;
                    if (c + size > 780.0) {
                        workerIndex = c;
                        break;
                    }

                    String[] workerIds = transaction.getNormalizedWorkerID().split(",");

                    if (Common.inArray(workerIds, selected.get(c).getId())) {
                        if (transaction.getType() == Transaction.GENERAL) {
                            calculations.get(c).setSalary(transaction.getWagePerWorker());
                            g.drawString("" + transaction.getWagePerWorker(), x, y);
                            g.drawString("" + calculations.get(c).getBalance(), x + 110, y);
                        } else {
                            calculations.get(c).setLoan(transaction.getLoanAmount());
                            g.drawString("" + transaction.getLoanAmount(), x + 50, y);
                            g.drawString("" + calculations.get(c).getBalance(), x + 110, y);
                        }
 
                    }
                }
            }

            if (workerIndex < selected.size()) {
                workerOnly = true;
            }

            y += 12;
        }

        return PAGE_EXISTS;
    }
}
