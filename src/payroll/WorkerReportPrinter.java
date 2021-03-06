/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package payroll;

import java.awt.Font;
import java.awt.FontMetrics;
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

    private final int printCap = 28;

    private ArrayList<WorkerReport> reports;
    private Worker worker;
    private int type;

    private int pagesNeeded = 0;
    private int itemCount = 0;

    private int currentIndex = 0;

    private boolean printOrNot = true;

    private int x = 0, y = 0;

    private double totalSalary = 0.0, totalLoan = 0.0, totalBalance = 0.0, totalSaving = 0.0, totalPayment = 0.0, totalSavingBalance = 0.0, totalWithdraw = 0.0;

    private FontMetrics fontMetrics;

    String fontFamily = "Calibri";
    int fontSize = 8;

    //Report cell size
    int tarikh_size = 30;

    public WorkerReportPrinter(int type, Worker worker, ArrayList<WorkerReport> reports) {
        this.reports = reports;
        this.worker = worker;
        this.type = type;

        itemCount = this.reports.size();
        pagesNeeded = (int) Math.ceil((double) itemCount / printCap);
    }

    public int print(Graphics graphics, PageFormat pageFormat,
            int pageIndex) throws PrinterException {
        Graphics2D g = (Graphics2D) graphics;

        if (pageIndex >= pagesNeeded) {
//            System.out.println("No more pages");
            return NO_SUCH_PAGE;
        }

        printOrNot = printOrNot ? false : true;

        if (printOrNot) {
//            System.out.println("Printing...");

            x = 20;
            y = 80;

            int size = 0;

            g.setFont(new Font(fontFamily, Font.BOLD, fontSize));

            g.drawString("Tempoh", x, y);
            size = tarikh_size;
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

            g.setFont(new Font(fontFamily, Font.PLAIN, fontSize));

            for (int i = currentIndex; i < itemCount; i++) {
                WorkerReport report = reports.get(i);

                x = 20;
                g.drawLine(x - 5, y + 5, x - 5, y - 35);

                if (counter > printCap) {
//                    System.out.println(counter);
                    currentIndex = i;
                    g.drawLine(15, y - 10, x - 5, y - 10);
                    return PAGE_EXISTS;
                }

                g.drawString(new StringBuilder().append(report.getMonth() + "/"
                        + report.getYear()).toString(), x, y);
                size = tarikh_size;
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

                counter++;
                y += 18;

                totalSalary += report.getSalary();
                totalLoan += report.getLoan();
                totalBalance += report.getBalance();
                totalSaving += report.getSaving();
                totalSavingBalance += report.getSavingBalance();
                totalPayment += report.getPayment();
                totalWithdraw += report.getWithdraw();
            }

            g.drawLine(15, y - 10, x - 5, y - 10);

            x = 20;
            y += 5;
            g.drawLine(x - 5, y + 5, x - 5, y - 35);
            g.setFont(new Font(fontFamily, Font.BOLD, fontSize));
            g.drawString("Jumlah", x, y);
            g.setFont(new Font(fontFamily, Font.PLAIN, fontSize));
            size = tarikh_size;
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
        }

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

        g.drawString("Bayaran Gaji", x, y);
        size = 60;
        g.drawLine(x - 5, y + 5, x + 5 + size, y + 5);
        g.drawLine(x - 5, y - 35, x + 5 + size, y - 35);
        g.drawLine(x - 5, y + 5, x - 5, y - 35);
        g.drawLine(x + 5 + size, y + 5, x + 5 + size, y - 35);
        x += size + 10;

    }

    private void printSavingHeader(Graphics2D g) {
        int size = 0;

        g.drawString("Simpanan +", x, y);
        size = 60;
        g.drawLine(x - 5, y + 5, x + 5 + size, y + 5);
        g.drawLine(x - 5, y - 35, x + 5 + size, y - 35);
        g.drawLine(x - 5, y + 5, x - 5, y - 35);
        g.drawLine(x + 5 + size, y + 5, x + 5 + size, y - 35);
        x += size + 10;

        g.drawString("Simpanan -", x, y);
        size = 60;
        g.drawLine(x - 5, y + 5, x + 5 + size, y + 5);
        g.drawLine(x - 5, y - 35, x + 5 + size, y - 35);
        g.drawLine(x - 5, y + 5, x - 5, y - 35);
        g.drawLine(x + 5 + size, y + 5, x + 5 + size, y - 35);
        x += size + 10;

        g.drawString("Simpanan Tetap", x, y);
        size = 80;
        g.drawLine(x - 5, y + 5, x + 5 + size, y + 5);
        g.drawLine(x - 5, y - 35, x + 5 + size, y - 35);
        g.drawLine(x - 5, y + 5, x - 5, y - 35);
        g.drawLine(x + 5 + size, y + 5, x + 5 + size, y - 35);
        x += size + 10;
    }

    private void printSalaryContent(Graphics2D g, WorkerReport report) {
        int size = 0;

        size = 60;
        x += size + 10;
        g.drawString(Common.currency(report.getSalary()),
                x - g.getFontMetrics().stringWidth(Common.currency(
                                report.getSalary())) - 10, y);

        size = 60;
        x += size + 10;
        g.drawString(Common.currency(report.getLoan()),
                x - g.getFontMetrics().stringWidth(Common.currency(
                                report.getLoan())) - 10, y);

        size = 60;
        x += size + 10;
        g.drawString(Common.currency(report.getBalance()),
                x - g.getFontMetrics().stringWidth(Common.currency(
                                report.getBalance())) - 10, y);

        size = 60;
        x += size + 10;
        g.drawString(Common.currency(report.getPayment()),
                x - g.getFontMetrics().stringWidth(Common.currency(
                                report.getPayment())) - 10, y);
    }

    private void printSavingContent(Graphics2D g, WorkerReport report) {
        int size = 0;

        size = 60;
        x += size + 10;
        g.drawString(Common.currency(report.getSaving()),
                x - g.getFontMetrics().stringWidth(Common.currency(
                                report.getSaving())) - 10, y);

        size = 60;
        x += size + 10;
        g.drawString(Common.currency(report.getWithdraw()),
                x - g.getFontMetrics().stringWidth(Common.currency(
                                report.getWithdraw())) - 10, y);

        size = 80;
        x += size + 10;
        g.drawString(Common.currency(report.getSavingBalance()),
                x - g.getFontMetrics().stringWidth(Common.currency(
                                report.getSavingBalance())) - 10, y);
    }

    private void printSalarySummary(Graphics2D g) {
        int size = 0;

        size = 60;
        x += size + 10;
        g.drawString(Common.currency(totalSalary),
                x - g.getFontMetrics().stringWidth(Common.currency(
                                totalSalary)) - 10, y);

        size = 60;
        x += size + 10;
        g.drawString(Common.currency(totalLoan),
                x - g.getFontMetrics().stringWidth(Common.currency(
                                totalLoan)) - 10, y);

        size = 60;
        x += size + 10;
        g.drawString(Common.currency(totalBalance),
                x - g.getFontMetrics().stringWidth(Common.currency(
                                totalBalance)) - 10, y);

        size = 60;
        x += size + 10;
        g.drawString(Common.currency(totalPayment),
                x - g.getFontMetrics().stringWidth(Common.currency(
                                totalPayment)) - 10, y);
    }

    private void printSavingSummary(Graphics2D g) {
        int size = 0;

        size = 60;
        x += size + 10;
        g.drawString(Common.currency(totalSaving),
                x - g.getFontMetrics().stringWidth(Common.currency(
                                totalSaving)) - 10, y);

        size = 60;
        x += size + 10;
        g.drawString(Common.currency(totalWithdraw),
                x - g.getFontMetrics().stringWidth(Common.currency(
                                totalWithdraw)) - 10, y);

        size = 80;
        x += size + 10;
        g.drawString(Common.currency(totalSavingBalance),
                x - g.getFontMetrics().stringWidth(Common.currency(
                                totalSavingBalance)) - 10, y);
    }
}