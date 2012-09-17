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
import java.util.ArrayList;
import payroll.libraries.Common;
import payroll.model.Worker;
import payroll.model.WorkerReport;

/**
 *
 * @author Edward
 */
public class WorkerReportPrinter implements Printable {

    private final int printCap = 35;
    
    private ArrayList<WorkerReport> reports;
    private Worker worker;
    private int type;

    private int pagesNeeded = 0;
    private int itemCount = 0;

    private int currentIndex = 0;

    private int x = 0, y = 0;

    private double totalSalary = 0.0, totalLoan = 0.0, totalBalance = 0.0, totalSaving = 0.0, totalPayment = 0.0, totalSavingBalance = 0.0;

    public WorkerReportPrinter(int type, Worker worker, ArrayList<WorkerReport> reports) {
        this.reports = reports;
        this.worker = worker;
        this.type = type;

        itemCount = this.reports.size();
        pagesNeeded = (int) Math.ceil((double) itemCount / printCap);
    }

    public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException {
        Graphics2D g =(Graphics2D) graphics;

        if (pageIndex >= pagesNeeded) {
            System.out.println("No more pages");
            return NO_SUCH_PAGE;
        }

        System.out.println("Printing...");

        x = 20;
        y = 80;

        int size = 0;

        g.setFont(new Font("Calibri", Font.BOLD, 12));
        
        g.drawString("Tempoh", x, y);
        size = 80;
        g.drawLine(x - 5, y + 5, x + 5 + size, y + 5);
        g.drawLine(x - 5, y - 35, x + 5 + size, y - 35);
        g.drawLine(x - 5, y + 5, x - 5, y - 35);
        g.drawLine(x + 5 + size, y + 5, x + 5 + size, y - 35);
        x += size + 10;

        if (type == WorkerReportFrame.FULL) {
            printSalaryHeader(g);
            printSavingHeader(g);
        } else if (type == WorkerReportFrame.INCOME) {
            printSalaryHeader(g);
        } else if (type == WorkerReportFrame.SAVING) {
            printSavingHeader(g);
        }

        y += 20;

        int counter = 0;
        
        g.setFont(new Font("Calibri", Font.PLAIN, 12));
        
        for (int i = currentIndex; i < itemCount; i ++) {
            WorkerReport report = reports.get(i);

            totalSalary += report.getSalary();
            totalLoan += report.getLoan();
            totalBalance += report.getBalance();
            totalSaving += report.getSaving();
            totalSavingBalance += report.getSavingBalance();
            totalPayment += report.getPayment();

            x = 20;
            g.drawLine(x - 5, y + 5, x - 5, y - 35);
            
            if (counter > printCap) {
                currentIndex = i;
                g.drawLine(15, y - 10, x - 5, y - 10);
                return PAGE_EXISTS;
            }


            g.drawString(report.getMonth() + "/" + report.getYear(), x, y);
            size = 80;
            x += size + 10;

            if (type == WorkerReportFrame.FULL) {
                printSalaryContent(g, report);
                printSavingContent(g, report);
            } else if (type == WorkerReportFrame.INCOME) {
                printSalaryContent(g, report);
            } else if (type == WorkerReportFrame.SAVING) {
                printSavingContent(g, report);
            }
            g.drawLine(x - 5, y + 5, x - 5, y - 35);

            counter ++;
            y += 18;

            
        }

        g.drawLine(15, y - 10, x - 5, y - 10);

        x = 20;
        y += 5;
        g.drawLine(x - 5, y + 5, x - 5, y - 35);
        g.setFont(new Font("Calibri", Font.BOLD, 12));
        g.drawString("Jumlah", x, y);
        g.setFont(new Font("Calibri", Font.PLAIN, 12));
        size = 80;
        x += size + 10;
        
        if (type == WorkerReportFrame.FULL) {
            printSalarySummary(g);
            printSavingSummary(g);
        } else if (type == WorkerReportFrame.INCOME) {
            printSalarySummary(g);
        } else if (type == WorkerReportFrame.SAVING) {
            printSavingSummary(g);
        }
        g.drawLine(x - 5, y + 5, x - 5, y - 35);

        y += 15;
        g.drawLine(15, y - 10, x - 5, y - 10);

        return PAGE_EXISTS;
    }

    private void printSalaryHeader(Graphics2D g) {
        int size = 0;
        
        g.drawString("Gaji", x, y);
        size = 60;
        g.drawLine(x - 5, y + 5, x + 5 + size, y + 5);
        g.drawLine(x - 5, y - 35, x + 5 + size, y - 35);
        g.drawLine(x - 5, y + 5, x - 5, y - 35);
        g.drawLine(x + 5 + size, y + 5, x + 5 + size, y - 35);
        x += size + 10;

        g.drawString("Pinjaman", x, y);
        size = 60;
        g.drawLine(x - 5, y + 5, x + 5 + size, y + 5);
        g.drawLine(x - 5, y - 35, x + 5 + size, y - 35);
        g.drawLine(x - 5, y + 5, x - 5, y - 35);
        g.drawLine(x + 5 + size, y + 5, x + 5 + size, y - 35);
        x += size + 10;

        g.drawString("Baki", x, y);
        size = 60;
        g.drawLine(x - 5, y + 5, x + 5 + size, y + 5);
        g.drawLine(x - 5, y - 35, x + 5 + size, y - 35);
        g.drawLine(x - 5, y + 5, x - 5, y - 35);
        g.drawLine(x + 5 + size, y + 5, x + 5 + size, y - 35);
        x += size + 10;

        if (type == WorkerReportFrame.INCOME) {
            g.drawString("Bayaran Gaji", x, y);
            size = 60;
            g.drawLine(x - 5, y + 5, x + 5 + size, y + 5);
            g.drawLine(x - 5, y - 35, x + 5 + size, y - 35);
            g.drawLine(x - 5, y + 5, x - 5, y - 35);
            g.drawLine(x + 5 + size, y + 5, x + 5 + size, y - 35);
            x += size + 10;
        }

    }

    private void printSavingHeader(Graphics2D g) {
        int size = 0;
        
        g.drawString("Simpanan", x, y);
        size = 60;
        g.drawLine(x - 5, y + 5, x + 5 + size, y + 5);
        g.drawLine(x - 5, y - 35, x + 5 + size, y - 35);
        g.drawLine(x - 5, y + 5, x - 5, y - 35);
        g.drawLine(x + 5 + size, y + 5, x + 5 + size, y - 35);
        x += size + 10;

        g.drawString("Bayaran", x, y);
        size = 60;
        g.drawLine(x - 5, y + 5, x + 5 + size, y + 5);
        g.drawLine(x - 5, y - 35, x + 5 + size, y - 35);
        g.drawLine(x - 5, y + 5, x - 5, y - 35);
        g.drawLine(x + 5 + size, y + 5, x + 5 + size, y - 35);
        x += size + 10;

        g.drawString("Baki", x, y);
        size = 60;
        g.drawLine(x - 5, y + 5, x + 5 + size, y + 5);
        g.drawLine(x - 5, y - 35, x + 5 + size, y - 35);
        g.drawLine(x - 5, y + 5, x - 5, y - 35);
        g.drawLine(x + 5 + size, y + 5, x + 5 + size, y - 35);
        x += size + 10;
    }

    private void printSalaryContent(Graphics2D g, WorkerReport report) {
        int size = 0;

        g.drawString(Common.currency(report.getSalary()), x, y);
        size = 60;
        x += size + 10;

        g.drawString(Common.currency(report.getLoan()), x, y);
        size = 60;
        x += size + 10;

        g.drawString(Common.currency(report.getBalance()), x, y);
        size = 60;
        x += size + 10;

        if (type == WorkerReportFrame.INCOME) {
            g.drawString(Common.currency(report.getPayment()), x, y);
            size = 60;
            x += size + 10;
        }
    }

    private void printSavingContent(Graphics2D g, WorkerReport report) {
        int size = 0;

        g.drawString(Common.currency(report.getSaving()), x, y);
        size = 60;
        x += size + 10;

        g.drawString(Common.currency(report.getPayment()), x, y);
        size = 60;
        x += size + 10;

        g.drawString(Common.currency(report.getSavingBalance()), x, y);
        size = 60;
        x += size + 10;
    }

    private void printSalarySummary(Graphics2D g) {
        int size = 0;

        g.drawString(Common.currency(totalSalary), x, y);
        size = 60;
        x += size + 10;

        g.drawString(Common.currency(totalLoan), x, y);
        size = 60;
        x += size + 10;

        g.drawString(Common.currency(totalBalance), x, y);
        size = 60;
        x += size + 10;

        if (type == WorkerReportFrame.INCOME) {
             g.drawString(Common.currency(totalPayment), x, y);
        size = 60;
        x += size + 10;
        }
    }

    private void printSavingSummary(Graphics2D g) {
        int size = 0;
        
        g.drawString(Common.currency(totalSaving), x, y);
        size = 60;
        x += size + 10;

        g.drawString(Common.currency(totalPayment), x, y);
        size = 60;
        x += size + 10;

        g.drawString(Common.currency(totalSavingBalance), x, y);
        size = 60;
        x += size + 10;
    }
}
