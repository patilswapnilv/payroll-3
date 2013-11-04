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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Hashtable;
import payroll.libraries.Common;
import payroll.model.ReportCalculation;
import payroll.model.ReportSalary;
import payroll.model.ReportSaving;
import payroll.model.Transaction;
import payroll.model.Worker;

/**
 *
 * @author Edward
 */
public class ReportPrinter1 implements Printable {

    ArrayList<ReportCalculation> calculations = new ArrayList<ReportCalculation>();
    private ArrayList<String> headers = new ArrayList<String>();
    private ArrayList<Worker> selected = new ArrayList<Worker>();
    private ArrayList<ReportSalary> salaries = new ArrayList<ReportSalary>();
    private Hashtable dates = new Hashtable();
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
    int salaryIndex = 0;

    //---------------------------------
    boolean printMainColumn = true;
    boolean printOverflowColumn = false;
    boolean isOverflow = false;
    boolean printSummary = true;
    boolean printSalary = false;
    boolean printSaving = false;
    ;

    int basicColumnSize = 0;
    int overflowCounter = 0;
    int workerCount = 0;

    int page = 1;

    String fontFamily = "Calibri";
    int fontSize = 8;

    //Report cell size
    int tarikh_size = 40;
    int pelanggan_size = 115;
    int keterangan_size = 75;
    int kiraan_asing_size = 30;
    int berat_kg_size = 25;
    int harga_seton_size = 35;
    int jumlah_diterima_size = 50;
    int upah_kerja_size = 20;
    int jumlah_gaji_size = 43;
    int jumlah_baki_size = 47;
    int worker_calc_size = 160;

    int worker_salary_size = 50;
    int worker_loan_size = 110;
    int worker_balance_size = 170;
    int worker_cell_page_max = 710;

    public ReportPrinter1(Main parent, ArrayList<Worker> selected,
            ArrayList<Transaction> transactions,
            ArrayList<ReportCalculation> calculations,
            ArrayList<ReportSaving> savings, ArrayList<ReportSalary> salaries,
            Hashtable dates) {
        this.parent = parent;
        this.transactions = transactions;
        this.selected = selected;
        this.savings = savings;
        this.calculations = calculations;
        this.salaries = salaries;
        this.dates = dates;
        this.setup();
    }

    private void setup() {

        itemCount = transactions.size();
        workerCount = selected.size();

        pagesNeeded = (int) Math.ceil((double) itemCount / printCap);
        totalPage = pagesNeeded;

        System.out.println("Page needed: " + pagesNeeded);
    }

    public int print(Graphics graphics, PageFormat pageFormat,
            int pageIndex) throws PrinterException {
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
            x = 10;
            y = 40;
            g.setFont(new Font(fontFamily, Font.BOLD, 12));
            g.drawString(new StringBuilder().append("Laporan Bulanan ("
                    + Common.renderDisplayDate((Calendar) dates.get("from"))
                    + " - " + Common.renderDisplayDate((Calendar) dates.get("to"))
                    + ")").toString(), x, y);
            g.setFont(new Font(fontFamily, Font.PLAIN, fontSize));
            x = 20;
            y = 80;
            this.render_header(g);
            int workerLocalIndex = this.render_content(g);
            g.drawLine(15, y - 10, x - 5, y - 10);

            if (totalItemCounter >= itemCount) {

                if (printSummary) {
                    this.render_summary(g);
                    printSalary = true;
                }

                if (printSalary) {
                    this.render_salaries(g);
                    printSaving = true;
                }

                if (printSaving) {
                    if (savings.size() > 0
                            && parent.chkMonthlyReportSaving.isSelected()) {
                        if (y + 60 > 580) {
                            pagesNeeded++;
                        } else {
                            render_savings(g);
                        }
                    }
                }
            }

            workerIndex = workerLocalIndex;

            if (isOverflow && printOverflowColumn == false) {
                currentPage++;
                printOverflowColumn = true;
                printMainColumn = false;
            } else if (isOverflow && printOverflowColumn) {
                printMainColumn = true;
                printOverflowColumn = false;
                workerIndex = 0;
            } else {
                currentPage++;
            }

            g.setColor(Color.GRAY);
            g.drawString(new StringBuilder().append("Page " + page++).toString(),
                    20, (int) pageFormat.getHeight() - 20);
        }

        return PAGE_EXISTS;
    }

    private void render_header(Graphics2D g) {
        int size = 0;
        g.setFont(new Font(fontFamily, Font.BOLD, fontSize));

        g.drawString("Tarikh", x, y);
        size = tarikh_size;
        g.drawLine(x - 5, y + 5, x + 5 + size, y + 5);
        g.drawLine(x - 5, y - 35, x + 5 + size, y - 35);
        g.drawLine(x - 5, y + 5, x - 5, y - 35);
        g.drawLine(x + 5 + size, y + 5, x + 5 + size, y - 35);
        x += size + 10;

        if (printMainColumn) {

            if (parent.chkMonthlyReportClientName1.isSelected()) {
                g.drawString("Pelanggan", x, y);
                size = pelanggan_size;
                g.drawLine(x - 5, y + 5, x + 5 + size, y + 5);
                g.drawLine(x - 5, y - 35, x + 5 + size, y - 35);
                g.drawLine(x - 5, y + 5, x - 5, y - 35);
                g.drawLine(x + 5 + size, y + 5, x + 5 + size, y - 35);
                x += size + 10;
            }

            if (parent.chkMonthlyReportDescription1.isSelected()) {
                g.drawString("Keterangan", x, y);
                size = keterangan_size;
                g.drawLine(x - 5, y + 5, x + 5 + size, y + 5);
                g.drawLine(x - 5, y - 35, x + 5 + size, y - 35);
                g.drawLine(x - 5, y + 5, x - 5, y - 35);
                g.drawLine(x + 5 + size, y + 5, x + 5 + size, y - 35);
                x += size + 10;
            }

            if (parent.chkMonthlyReportKiraanAsing1.isSelected()) {
                g.drawString("Kiraan", x, y - 15);
                g.drawString("Asing", x, y);
                size = kiraan_asing_size;
                g.drawLine(x - 5, y + 5, x + 5 + size, y + 5);
                g.drawLine(x - 5, y - 35, x + 5 + size, y - 35);
                g.drawLine(x - 5, y + 5, x - 5, y - 35);
                g.drawLine(x + 5 + size, y + 5, x + 5 + size, y - 35);
                x += size + 10;
            }

            if (parent.chkMonthlyReportWeight1.isSelected()) {
                g.drawString("Berat", x, y - 15);
                g.drawString("KG", x, y);
                size = berat_kg_size;
                g.drawLine(x - 5, y + 5, x + 5 + size, y + 5);
                g.drawLine(x - 5, y - 35, x + 5 + size, y - 35);
                g.drawLine(x - 5, y + 5, x - 5, y - 35);
                g.drawLine(x + 5 + size, y + 5, x + 5 + size, y - 35);
                x += size + 10;
            }

            if (parent.chkMonthlyReportPricePerTon1.isSelected()) {
                g.drawString("Harga", x, y - 15);
                g.drawString("Seton", x, y);
                size = harga_seton_size;
                g.drawLine(x - 5, y + 5, x + 5 + size, y + 5);
                g.drawLine(x - 5, y - 35, x + 5 + size, y - 35);
                g.drawLine(x - 5, y + 5, x - 5, y - 35);
                g.drawLine(x + 5 + size, y + 5, x + 5 + size, y - 35);
                x += size + 10;
            }

            if (parent.chkMonthlyReportTotalReceived1.isSelected()) {
                g.drawString("Jumlah", x, y - 15);
                g.drawString("Diterima", x, y);
                size = jumlah_diterima_size;
                g.drawLine(x - 5, y + 5, x + 5 + size, y + 5);
                g.drawLine(x - 5, y - 35, x + 5 + size, y - 35);
                g.drawLine(x - 5, y + 5, x - 5, y - 35);
                g.drawLine(x + 5 + size, y + 5, x + 5 + size, y - 35);
                x += size + 10;
            }

            if (parent.chkMonthlyReportWages1.isSelected()) {
                g.drawString("Upah", x, y - 15);
                g.drawString("Kerja", x, y);
                size = upah_kerja_size;
                g.drawLine(x - 5, y + 5, x + 5 + size, y + 5);
                g.drawLine(x - 5, y - 35, x + 5 + size, y - 35);
                g.drawLine(x - 5, y + 5, x - 5, y - 35);
                g.drawLine(x + 5 + size, y + 5, x + 5 + size, y - 35);
                x += size + 10;
            }

            if (parent.chkMonthlyReportSalary1.isSelected()) {
                g.drawString("Jumlah", x, y - 15);
                g.drawString("Gaji", x, y);
                size = jumlah_gaji_size;
                g.drawLine(x - 5, y + 5, x + 5 + size, y + 5);
                g.drawLine(x - 5, y - 35, x + 5 + size, y - 35);
                g.drawLine(x - 5, y + 5, x - 5, y - 35);
                g.drawLine(x + 5 + size, y + 5, x + 5 + size, y - 35);
                x += size + 10;
            }

            if (parent.chkMonthlyReportBalance1.isSelected()) {
                g.drawString("Jumlah", x, y - 15);
                g.drawString("Baki", x, y);
                size = jumlah_baki_size;
                g.drawLine(x - 5, y + 5, x + 5 + size, y + 5);
                g.drawLine(x - 5, y - 35, x + 5 + size, y - 35);
                g.drawLine(x - 5, y + 5, x - 5, y - 35);
                g.drawLine(x + 5 + size, y + 5, x + 5 + size, y - 35);
                x += size + 10;
            }

            basicColumnSize = x;
        }

        for (int i = workerIndex; i < workerCount; i++) {
            size = worker_calc_size;
            if (x + size > worker_cell_page_max) {
                isOverflow = true;
                printOverflowColumn = false;
                pagesNeeded++;
                break;
            }

            g.drawString(new StringBuilder().append(selected.get(i).getCode()
                    + " " + selected.get(i).getName()).toString(), x, y - 18);
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
        for (int i = transactionIndex; i < itemCount; i++) {
            // reset position
            x = 20;

            if (counter > printCap) {
                if (isOverflow == false || (isOverflow && printOverflowColumn)) {
                    transactionIndex = i;
                }
                break;
            }

            Transaction transaction = transactions.get(i);
            g.setFont(new Font(fontFamily, Font.PLAIN, fontSize));
            // draw line on left
            g.drawLine(x - 5, y + 5, x - 5, y - 35);

            g.drawString(Common.renderDisplayDate(transaction.getDate()), x, y);
            size = tarikh_size;
            x += size + 10;
            g.drawLine(x - 5, y + 5, x - 5, y - 35);

            if (printMainColumn) {
                if (parent.chkMonthlyReportClientName1.isSelected()) {
                    if (transaction.getType() == Transaction.GENERAL) {
                        g.drawString(transaction.getCustomer().getName(), x, y);
                    } else {
                        g.drawString("Pinjaman", x, y);
                    }
                    size = pelanggan_size;
                    x += size + 10;
                    g.drawLine(x - 5, y + 5, x - 5, y - 35);
                }

                if (parent.chkMonthlyReportDescription1.isSelected()) {
                    g.drawString(transaction.getDescription(), x, y);
                    size = keterangan_size;
                    x += size + 10;
                    g.drawLine(x - 5, y + 5, x - 5, y - 35);
                }

                if (parent.chkMonthlyReportKiraanAsing1.isSelected()) {
                    size = kiraan_asing_size;
                    x += size + 10;
                    if (transaction.getType() == Transaction.GENERAL) {
                        g.drawString("" + Common.currency(
                                transaction.getKiraanAsing()),
                                x - g.getFontMetrics().stringWidth(
                                        Common.currency(
                                                transaction.getKiraanAsing())) - 10, y);
                    }
                    g.drawLine(x - 5, y + 5, x - 5, y - 35);
                }

                if (parent.chkMonthlyReportWeight1.isSelected()) {
                    size = berat_kg_size;
                    x += size + 10;
                    if (transaction.getType() == Transaction.GENERAL) {
                        g.drawString("" + ((int) transaction.getWeight()), x - g.getFontMetrics().stringWidth("" + ((int) transaction.getWeight())) - 10, y);
                    }
                    g.drawLine(x - 5, y + 5, x - 5, y - 35);
                }

                if (parent.chkMonthlyReportPricePerTon1.isSelected()) {
                    size = harga_seton_size;
                    x += size + 10;
                    if (transaction.getType() == Transaction.GENERAL) {
                        g.drawString("" + Common.currency(transaction.getPricePerTon()), x - g.getFontMetrics().stringWidth(Common.currency(transaction.getPricePerTon())) - 10, y);
                    }
                    g.drawLine(x - 5, y + 5, x - 5, y - 35);
                }

                if (parent.chkMonthlyReportTotalReceived1.isSelected()) {
                    size = jumlah_diterima_size;
                    x += size + 10;
                    if (transaction.getType() == Transaction.GENERAL) {
                        g.drawString("" + Common.currency(transaction.getTotal()), x - g.getFontMetrics().stringWidth(Common.currency(transaction.getTotal())) - 10, y);
                    }
                    g.drawLine(x - 5, y + 5, x - 5, y - 35);
                }

                if (parent.chkMonthlyReportWages1.isSelected()) {
                    size = upah_kerja_size;
                    x += size + 10;
                    if (transaction.getType() == Transaction.GENERAL) {
                        g.drawString("" + Common.currency(transaction.getWages()), x - g.getFontMetrics().stringWidth(Common.currency(transaction.getWages())) - 10, y);
                    }
                    g.drawLine(x - 5, y + 5, x - 5, y - 35);
                }

                if (parent.chkMonthlyReportSalary1.isSelected()) {
                    size = jumlah_gaji_size;
                    x += size + 10;
                    if (transaction.getType() == Transaction.GENERAL) {
                        g.drawString("" + Common.currency(transaction.getTotalSalary()), x - g.getFontMetrics().stringWidth(Common.currency(transaction.getTotalSalary())) - 10, y);
                    }
                    g.drawLine(x - 5, y + 5, x - 5, y - 35);
                }

                if (parent.chkMonthlyReportBalance1.isSelected()) {
                    size = jumlah_baki_size;
                    x += size + 10;
                    if (transaction.getType() == Transaction.GENERAL) {
                        g.drawString("" + Common.currency(transaction.getBalance()), x - g.getFontMetrics().stringWidth(Common.currency(transaction.getBalance())) - 10, y);
                    }
                    g.drawLine(x - 5, y + 5, x - 5, y - 35);
                }

                totalItemCounter++;
            }

            for (int c = workerIndex; c < workerCount; c++) {
                size = worker_calc_size;
                if (x + size > worker_cell_page_max) {
                    workerIndexLocal = c;
                    break;
                }

                g.drawLine(x - 5, y + 5, x - 5, y - 35);
                String[] workerIds = transaction.getNormalizedWorkerID().split(",");

                if (Common.inArray(workerIds, selected.get(c).getId())) {
                    if (transaction.getType() == Transaction.GENERAL) {
                        calculations.get(c).setSalary(transaction.getWagePerWorker());
                        g.drawString("" + Common.currency(
                                transaction.getWagePerWorker()),
                                x + worker_salary_size - g.getFontMetrics().stringWidth(
                                        Common.currency(transaction.getWagePerWorker())) - 10, y);
                        g.drawString("" + Common.currency(
                                calculations.get(c).getBalance()),
                                x + worker_balance_size - g.getFontMetrics().stringWidth(
                                        Common.currency(calculations.get(c).getBalance())) - 10, y);
                    } else {
                        calculations.get(c).setLoan(transaction.getLoanAmount());
                        g.drawString("" + Common.currency(
                                transaction.getLoanAmount()),
                                x + worker_loan_size - g.getFontMetrics().stringWidth(
                                        Common.currency(transaction.getLoanAmount())) - 10, y);
                        g.drawString("" + Common.currency(
                                calculations.get(c).getBalance()),
                                x + worker_balance_size - g.getFontMetrics().stringWidth(
                                        Common.currency(calculations.get(c).getBalance())) - 10, y);
                    }

                }

                g.drawLine(x + 50 - 5, y + 5, x + 50 - 5, y - 35);
                g.drawLine(x + 110 - 5, y + 5, x + 110 - 5, y - 35);

                x += size + 10;

                g.drawLine(x - 5, y + 5, x - 5, y - 35);
            }

            // draw line on the right side
            g.drawLine(x - 5, y + 5, x - 5, y - 35);
            // increase the row size
            y += 15;
            counter++;
        }

        return workerIndexLocal;
    }

    private void render_summary(Graphics2D g) {
        int size = 0;
        if (printMainColumn) {
            x = basicColumnSize;
        } else {
            x = 70; //20 + 60 + 10; // add date size as well
        }

        size = worker_calc_size;
        y += 5;
        for (int i = workerIndex; i < workerCount; i++) {
            if (x + size > worker_cell_page_max) {
                printSummary = true;
                break;
            }

            g.setFont(new Font(fontFamily, Font.BOLD, fontSize));
            g.drawString(Common.currency(
                    calculations.get(i).getSalary()),
                    x + worker_salary_size - g.getFontMetrics().stringWidth(
                            Common.currency(calculations.get(i).getSalary())) - 10, y);
            g.drawString(Common.currency(
                    calculations.get(i).getLoan()),
                    x + worker_loan_size - g.getFontMetrics().stringWidth(
                            Common.currency(calculations.get(i).getLoan())) - 10, y);
            g.drawString(Common.currency(
                    calculations.get(i).getBalance()),
                    x + worker_balance_size - g.getFontMetrics().stringWidth(
                            Common.currency(calculations.get(i).getBalance())) - 10, y);

            g.drawLine(x - 5, y + 5, x + 5 + size, y + 5);
            g.drawLine(x - 5, y + 5, x - 5, y - 35);
            g.drawLine(x + 5 + size, y + 5, x + 5 + size, y - 35);

            x += size + 10;
            printSummary = false;
        }

        y += 20;
    }

    private void render_salaries(Graphics2D g) {
        if (salaries.size() == 0) {
            return;
        }

        int size = 0;
        size = worker_calc_size;
        int initial_y_axis = y;

        int counter = salaries.size();

        g.setFont(new Font(fontFamily, Font.PLAIN, fontSize));

        for (int i = salaryIndex; i < counter; i++) {
            x = 20;

            g.drawLine(x - 5, y + 5, x - 5, y - 15);

            if (y + 40 > 580) {
                salaryIndex = i;
                pagesNeeded++;
                printSalary = true;
                break;
            }

            if (printMainColumn) {
                size = 60;
                g.drawString(Common.renderDisplayDate(salaries.get(i).getDate()), x, y);
                x += size + 10;
                g.drawLine(x - 5, y + 5, x - 5, y - 15);

                g.drawString("Bayaran Gaji", x, y);
                g.drawLine(x - 5, y + 5, x - 5, y - 15);

                x = basicColumnSize;
            } else {
                x += 60 + 10; // add date field size
            }

            for (int c = workerIndex; c < workerCount; c++) {
                size = 180;

                if (x + size > worker_cell_page_max) {
                    printSalary = true;
                    break;
                }

                calculations.get(c).setPayment(salaries.get(i).getWorkerSalary(calculations.get(c).getWorkerID()));
                g.drawLine(x - 5, y + 5, x - 5, y - 15);
                x += size + 10;
                g.drawString(Common.currency(Math.abs(salaries.get(i).getWorkerSalary(selected.get(c).getId()))), x - g.getFontMetrics().stringWidth(Common.currency(Math.abs(salaries.get(i).getWorkerSalary(selected.get(c).getId())))) - 10, y);
                g.drawLine(x - 5, y + 5, x - 5, y - 15);
            }

            y += 12;

            printSalary = false;
        }

        if (!printSalary) {
            x = 20;
            g.drawLine(x - 5, y + 5, x - 5, y - 15);

            if (printMainColumn) {
                if (parent.chkMonthlyReportDate1.isSelected()) {
                    size = 60;
                    x += size + 10;
                    g.drawLine(x - 5, y + 5, x - 5, y - 15);
                }

                g.drawString("Baki", x, y);
                g.drawLine(x - 5, y + 5, x - 5, y - 15);

                x = basicColumnSize;
            } else {
                x += 60 + 10; // add date size
            }

            for (int c = workerIndex; c < workerCount; c++) {
                size = 180;

                if (x + size > worker_cell_page_max) {
                    printSalary = true;
                    break;
                }

                g.drawLine(x - 5, y + 5, x - 5, y - 15);
                x += size + 10;
                g.drawString(Common.currency(calculations.get(c).getTotalBalance()), x - g.getFontMetrics().stringWidth(Common.currency(calculations.get(c).getTotalBalance())) - 10, y);
                g.drawLine(x - 5, y + 5, x - 5, y - 15);
            }

            y += 12;

        }

        g.drawLine(15, initial_y_axis - 15, x - 5, initial_y_axis - 15);
        g.drawLine(15, y - 7, x - 5, y - 7);

    }

    private void render_savings(Graphics2D g) {
        int size = 0;

        x = 20;

        if (savingIndex == 0 && printMainColumn) {
            y += 30;
            g.setFont(new Font(fontFamily, Font.BOLD, 12));
            g.drawString("Simpanan Tetap", x, y);
            y += 20;
            g.setFont(new Font(fontFamily, Font.PLAIN, fontSize));

            g.drawLine(x - 5, y - 15, x - 5, y + 30); // left
            g.drawString("Baki Bulan Lalu", x, y);
            g.drawString("Bulan Ini", x, y + 12);
            g.drawString("Baki", x, y + 24);

            x += getSavingColspanSize();

            g.drawLine(15, y - 15, x + size - 5, y - 15); // top
            g.drawLine(15, y + 30, x + size - 5, y + 30); // bottom
        } else {
            g.setFont(new Font(fontFamily, Font.PLAIN, fontSize));
            x += 50;//60 + 10; // add date size;
            y += 50;
        }

        int count = savings.size();
        for (int i = savingIndex; i < count; i++) {
            ReportSaving saving = savings.get(i);

            g.drawLine(x - 5, y - 15, x - 5, y + 30);
            size = worker_calc_size;
            if (x + size > worker_cell_page_max) {
                savingIndex = i;
                break;
            }

            g.drawLine(x - 5, y - 15, x + size + 5, y - 15); // top
            g.drawLine(x - 5, y + 30, x + size + 5, y + 30); // bottom

            x += size + 10;

            g.drawString(Common.currency(saving.getPrevious()), x - g.getFontMetrics().stringWidth(Common.currency(saving.getPrevious())) - 10, y);
            g.drawString(Common.currency(saving.getCurrent()), x - g.getFontMetrics().stringWidth(Common.currency(saving.getCurrent())) - 10, y + 12);
            g.drawString(Common.currency(saving.getBalance()), x - g.getFontMetrics().stringWidth(Common.currency(saving.getBalance())) - 10, y + 24);

            g.drawLine(x - 5, y - 15, x - 5, y + 30);

        }

    }

    private int getSavingColspanSize() {
        int size = 0, total = 0;

        if (parent.chkMonthlyReportDate1.isSelected()) {
            size = 60;
            total += size + 10;
        }

        if (parent.chkMonthlyReportClientName1.isSelected()) {
            size = 80;
            total += size + 10;
        }

        if (parent.chkMonthlyReportDescription1.isSelected()) {
            size = 140;
            total += size + 10;
        }

        if (parent.chkMonthlyReportKiraanAsing1.isSelected()) {
            size = 50;
            total += size + 10;
        }

        if (parent.chkMonthlyReportWeight1.isSelected()) {
            size = 60;
            total += size + 10;
        }

        if (parent.chkMonthlyReportPricePerTon1.isSelected()) {
            size = 50;
            total += size + 10;
        }

        if (parent.chkMonthlyReportTotalReceived1.isSelected()) {
            size = 50;
            total += size + 10;
        }

        if (parent.chkMonthlyReportWages1.isSelected()) {
            size = 50;
            total += size + 10;
        }

        if (parent.chkMonthlyReportSalary1.isSelected()) {
            size = 50;
            total += size + 10;
        }

        if (parent.chkMonthlyReportBalance1.isSelected()) {
            size = 50;
            total += size + 10;
        }

        return total;
    }
}
