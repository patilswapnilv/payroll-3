/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package payroll;

import java.awt.Color;
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
import payroll.model.ReportSaving;
import payroll.model.Transaction;
import payroll.model.Worker;

/**
 *
 * @author Edward
 */
public class ReportPrinter implements Printable {

    ArrayList<ReportCalculation> calculations = new ArrayList<ReportCalculation>();
    private ArrayList<String> headers = new ArrayList<String>();
    private ArrayList<Worker> selected = new ArrayList<Worker>();
    private String query = "";
    private Main parent;

    private ArrayList<Transaction> transactions = new ArrayList<Transaction>();
    private ArrayList<ReportSaving> savings = new ArrayList<ReportSaving>();

    final int printCap = 28;
    private int printerPointer = 0;
    private int pagesNeeded = 1;
    private int totalPage = 0;
    private int totalItemCounter = 0;
    private int currentPage = 0;
    private int itemCount = 0;
    private int x = 0;
    private int y = 0;

    boolean printOrNot = true;

    int workerIndex = 0;
    int transactionIndex = 0;
    int savingIndex = 0;

    //---------------------------------
    boolean printMainColumn = true;
    boolean printOverflowColumn = false;
    boolean isOverflow = false;
    boolean printSummary = true;

    int basicColumnSize = 0;
    int overflowCounter = 0;
    int workerCount = 0;

    public ReportPrinter(Main parent, ArrayList<Worker> selected, String query, ArrayList<ReportSaving> savings) {
        this.parent = parent;
        this.query = query;
        this.selected = selected;
        this.savings = savings;
        this.setup();
    }

    private void setup() {
        ResultSet results = Database.instance().execute(query);

        try {
            itemCount = 0;
            while (results.next()) {
                transactions.add(new Transaction(results.getInt(1)));
            }

            itemCount = transactions.size();
        } catch (SQLException ex) {
            System.err.println("Error: Unable to get results size. Detail: " + ex.getMessage());
            ex.printStackTrace();
        }

        for (Worker worker : selected) {
            calculations.add(new ReportCalculation(worker.getId()));
        }

        workerCount = selected.size();
        pagesNeeded = (int) Math.ceil((double) itemCount / printCap);
        totalPage = pagesNeeded;

        System.out.println("Page needed: " + pagesNeeded);
    }

    public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException {
        Graphics2D g = (Graphics2D) graphics;
        g.translate(pageFormat.getImageableX(), pageFormat.getImageableY());
        int size = 0;

        printOrNot = printOrNot ? false : true;

        if (pageIndex >= pagesNeeded) {
            System.out.println("No more pages");
            return NO_SUCH_PAGE;
        }

        if (printOrNot) {
            System.out.println("Printing...");
            // Initial the position for new page
            x = 20;
            y = 80;
            this.render_header(g);
            int workerLocalIndex = this.render_content(g);
            g.drawLine(15, y - 10, x - 5, y - 10);

            if (totalItemCounter >= itemCount) {

                if (printSummary) {
                    if (printMainColumn) {
                        x = basicColumnSize;
                    } else {
                        x = 20;
                    }

                    size = 180;
                    y += 5;
                    for (int i = workerIndex; i < workerCount; i ++) {
                        if (x + size > 780.0) {
                            printSummary = true;
                            break;
                        }

                        g.setFont(new Font("Calibri", Font.BOLD, 12));
                        g.drawString(Common.currency(calculations.get(i).getSalary()), x, y);
                        g.drawString(Common.currency(calculations.get(i).getLoan()), x + 50, y);
                        g.drawString(Common.currency(calculations.get(i).getBalance()), x + 110, y);

                        g.drawLine(x - 5, y + 5, x + 5 + size, y + 5);
                        g.drawLine(x - 5, y + 5, x - 5, y - 35);
                        g.drawLine(x + 5 + size, y + 5, x + 5 + size, y - 35);

                        x += size + 10;
                        printSummary = false;
                    }
                }
                
                if (savings.size() > 0) {
                    if (y + 60 > 580) {
                        pagesNeeded ++;
                    } else {
                        printSavings(g);
                    }
                }
            }

            workerIndex = workerLocalIndex;

            if (isOverflow && printOverflowColumn == false) {
                currentPage ++;
                printOverflowColumn = true;
                printMainColumn = false;
            } else if (isOverflow && printOverflowColumn) {
                printMainColumn = true;
                printOverflowColumn = false;
                workerIndex = 0;
            } else {
                currentPage ++;
            }
            
            g.setColor(Color.GRAY);
            g.drawString("Page " + currentPage, 20, (int) pageFormat.getHeight() - 20);
        }

        return PAGE_EXISTS;
    }

    private void render_header(Graphics2D g) {
        int size = 0;
        g.setFont(new Font("Calibri", Font.BOLD, 12));
        
        if (printMainColumn) {
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
                size = 140;
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
                size = 60;
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

            basicColumnSize = x;
        }

        for (int i = workerIndex; i < workerCount; i ++) {
            size = 180;
            if (x + size > 780.0) {
                isOverflow = true;
                printOverflowColumn = false;
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

        }
        
        y += 20;
    }

    private int render_content(Graphics2D g) {
        int size = 0;
        int counter = 0;
        int workerIndexLocal = 0;
        for (int i = transactionIndex; i < itemCount; i ++) {
            // reset position
            x = 20;

            if (counter > printCap) {
                if (isOverflow == false || (isOverflow && printOverflowColumn)) transactionIndex = i;
                break;
            }

            Transaction transaction = transactions.get(i);
            g.setFont(new Font("Calibri", Font.PLAIN, 12));
            // draw line on left
            g.drawLine(x - 5, y + 5, x - 5, y - 35);
            if (printMainColumn) {
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
                    size = 140;
                    x += size + 10;
                }

                if (parent.chkMonthlyReportKiraanAsing.isSelected()) {
                    if (transaction.getType() == Transaction.GENERAL)
                        g.drawString("" + Common.currency(transaction.getKiraanAsing()), x, y);
                    size = 50;
                    x += size + 10;
                }

                if (parent.chkMonthlyReportWeight.isSelected()) {
                    if (transaction.getType() == Transaction.GENERAL)
                        g.drawString("" + Common.currency(transaction.getWeight()), x, y);
                    size = 60;
                    x += size + 10;
                }

                if (parent.chkMonthlyReportPricePerTon.isSelected()) {
                    if (transaction.getType() == Transaction.GENERAL)
                        g.drawString("" + Common.currency(transaction.getPricePerTon()), x, y);
                    size = 50;
                    x += size + 10;
                }

                if (parent.chkMonthlyReportTotalReceived.isSelected()) {
                    if (transaction.getType() == Transaction.GENERAL)
                        g.drawString("" + Common.currency(transaction.getTotal()), x, y);
                    size = 50;
                    x += size + 10;
                }
            }
            for (int c = workerIndex; c < workerCount; c ++) {
                size = 180;
                if (x + size > 780.0) {
                    workerIndexLocal = c;
                    break;
                }

                g.drawLine(x - 5, y + 5, x - 5, y - 35);
                String[] workerIds = transaction.getNormalizedWorkerID().split(",");

                if (Common.inArray(workerIds, selected.get(c).getId())) {
                    if (transaction.getType() == Transaction.GENERAL) {
                        calculations.get(c).setSalary(transaction.getWagePerWorker());
                        g.drawString("" + Common.currency(transaction.getWagePerWorker()), x, y);
                        g.drawString("" + Common.currency(calculations.get(c).getBalance()), x + 110, y);
                    } else {
                        calculations.get(c).setLoan(transaction.getLoanAmount());
                        g.drawString("" +Common.currency( transaction.getLoanAmount()), x + 50, y);
                        g.drawString("" + Common.currency(calculations.get(c).getBalance()), x + 110, y);
                    }
                }

                x += size + 10;
            }

            // draw line on the right side
            g.drawLine(x - 5, y + 5, x - 5, y - 35);
            // increase the row size
            y += 15;
            counter ++;
            totalItemCounter ++;
        }

        return workerIndexLocal;
    }

    private void printSavings(Graphics2D g) {
        int size = 0;
        
        x = 20;

        if (savingIndex == 0) {
            y += 30;
            g.setFont(new Font("Calibri", Font.BOLD, 12));
            g.drawString("Simpanan Tetap", x, y);
            y += 20;
            g.setFont(new Font("Calibri", Font.PLAIN, 12));


            g.drawLine(x - 5, y - 15, x - 5, y + 30); // left
            g.drawString("Baki Bulan Lalu", x, y);
            g.drawString("Bulan Ini", x, y + 12);
            g.drawString("Baki", x, y + 24);

            x += getSavingColspanSize();

            g.drawLine(15, y - 15, x + size + 5, y - 15); // top
            g.drawLine(15, y + 30, x + size + 5, y + 30); // bottom
        } else {
            g.setFont(new Font("Calibri", Font.PLAIN, 12));
            y += 50;
        }

        int count = savings.size();
        for (int i = savingIndex; i < count; i ++) {
            ReportSaving saving = savings.get(i);
            
            g.drawLine(x - 5, y - 15, x - 5, y + 30);
            size = 180;
            if (x + size > 780.0) {
                savingIndex = i;
                break;
            }

            g.drawString(Common.currency(saving.getPrevious()), x, y);
            g.drawString(Common.currency(saving.getCurrent()), x, y + 12);
            g.drawString(Common.currency(saving.getBalance()), x, y + 24);

            g.drawLine(x - 5, y - 15, x + size + 5, y - 15); // top
            g.drawLine(x - 5, y + 30, x + size + 5, y + 30); // bottom

            x += size + 10;

            g.drawLine(x - 5, y - 15, x - 5, y + 30);


        }
        
    }

    private int getSavingColspanSize() {
        int size = 0, total = 0;

        if (parent.chkMonthlyReportDate.isSelected()) {
            size = 60;
            total += size + 10;
        }

        if (parent.chkMonthlyReportClientName.isSelected()) {
            size = 80;
            total += size + 10;
        }

        if (parent.chkMonthlyReportDescription.isSelected()) {
            size = 140;
            total += size + 10;
        }

        if (parent.chkMonthlyReportKiraanAsing.isSelected()) {
            size = 50;
            total += size + 10;
        }

        if (parent.chkMonthlyReportWeight.isSelected()) {
            size = 60;
            total += size + 10;
        }

        if (parent.chkMonthlyReportPricePerTon.isSelected()) {
            size = 50;
            total += size + 10;
        }

        if (parent.chkMonthlyReportTotalReceived.isSelected()) {
            size = 50;
            total += size + 10;
        }

        return total;
    }
}
