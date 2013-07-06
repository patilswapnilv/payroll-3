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
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Hashtable;
import payroll.libraries.Common;
import payroll.model.Worker;
import payroll.model.WorkerReport;

/**
 *
 * @author Edward
 */
public class SavingReportPrinter implements Printable {

    private final int printCap = 28;
    
    private int pagesNeeded = 0;
    private int itemCount = 0;
    private int currentIndex = 0;
    private int currentSubIndex = -1;
    private int page = 0;
    private int x = 0, y = 0;
    private int _y = 0;

    private String[] keys;

    private boolean printOrNot = true;
    
    private FontMetrics fontMetrics;

    private Hashtable savings, previous, totals;
    private ArrayList<Worker> selected;


    public SavingReportPrinter(Hashtable savings, Hashtable previous, ArrayList<Worker> selected) {
        this.savings = savings;
        this.previous = previous;
        this.selected = selected;

        totals = new Hashtable();
        itemCount = Integer.parseInt(savings.remove("count").toString());
        pagesNeeded = (int) Math.ceil((double) itemCount / printCap);
        keys = (String[]) savings.keySet().toArray(new String[0]);
        Arrays.sort(keys);
    }

    public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException {
        Graphics2D g =(Graphics2D) graphics;

        if (pageIndex >= pagesNeeded) {
            System.out.println("No more pages");
            return NO_SUCH_PAGE;
        }

        printOrNot = printOrNot ? false : true;

        if (printOrNot) {
            System.out.println("Printing...");

            x = 20;
            y = 80;

            int size = 0;

            g.setFont(new Font("Calibri", Font.BOLD, 10));

            this.printHeader(g);
            x = 20;
            y += 20;

            g.setFont(new Font("Calibri", Font.PLAIN, 9));
            this.printPreviousBalance(g);
            x = 20;
            y += 20;
            this.printContent(g);

            if (pageIndex + 1 == pagesNeeded) {
                x = 20;
                this.printSummary(g);
            }
            System.out.println("Print Page " + (pageIndex + 1) + " out of " + pagesNeeded);
        }

        return PAGE_EXISTS;
    }

    private void printHeader(Graphics2D g) {
        int size = 0;

        // Drop first column
        size = 80;
        g.drawLine(x - 5, y + 5, x + 5 + size, y + 5);
        g.drawLine(x - 5, y - 15, x + 5 + size, y - 15);
        g.drawLine(x - 5, y + 5, x - 5, y - 15);
        g.drawLine(x + 5 + size, y + 5, x + 5 + size, y - 15);
        x += size + 10;

        for (Worker worker : selected) {
            g.drawString(worker.getCode() + " " + worker.getName(), x, y);
            size = 80;
            g.drawLine(x - 5, y + 5, x + 5 + size, y + 5);
            g.drawLine(x - 5, y - 15, x + 5 + size, y - 15);
            g.drawLine(x - 5, y + 5, x - 5, y - 15);
            g.drawLine(x + 5 + size, y + 5, x + 5 + size, y - 15);
            x += size + 10;
        }
    }

    private void printPreviousBalance(Graphics2D g) {
        int size = 0;

        g.drawString("Baki Bulan Dahulu", x, y);
        size = 80;
        g.drawLine(x - 5, y + 5, x + 5 + size, y + 5);
        g.drawLine(x - 5, y - 15, x + 5 + size, y - 15);
        g.drawLine(x - 5, y + 5, x - 5, y - 15);
        g.drawLine(x + 5 + size, y + 5, x + 5 + size, y - 15);
        x += size + 10;

        for (Worker worker : selected) {
            double amount = 0.0;
            
            if (this.previous.get(worker.getId()) != null) {
                amount = Double.parseDouble(this.previous.get(worker.getId()).toString());
            }

            g.drawString(Common.currency(amount), x - g.getFontMetrics().stringWidth(Common.currency(amount)) - 10 + size, y);
            size = 80;
            g.drawLine(x - 5, y + 5, x + 5 + size, y + 5);
            g.drawLine(x - 5, y - 15, x + 5 + size, y - 15);
            g.drawLine(x - 5, y + 5, x - 5, y - 15);
            g.drawLine(x + 5 + size, y + 5, x + 5 + size, y - 15);
            x += size + 10;
        }
    }

    private void printContent(Graphics2D g) {
        int size = 0;

        for (int i = currentIndex; i < keys.length; i ++) {
            if (i > printCap) {
                break;
            }
            
            String date = keys[i];
            x = 20;

            g.drawString(Common.renderDisplayDate(Common.convertStringToDate(date)), x, y);
            size = 80;
            g.drawLine(x - 5, y + 5, x + 5 + size, y + 5);
            g.drawLine(x - 5, y - 15, x + 5 + size, y - 15);
            g.drawLine(x - 5, y + 5, x - 5, y - 15);
            g.drawLine(x + 5 + size, y + 5, x + 5 + size, y - 15);
            x += size + 10;

            Hashtable saving = (Hashtable) savings.get(date);

            for (Worker worker : selected) {
                ArrayList<Double> amounts = (ArrayList<Double>) saving.get(worker.getId());
                int counter = amounts != null ? amounts.size() : 0;
                double total = 0.0;

                if (totals.get(worker.getId()) != null) {
                    total = Double.parseDouble(totals.get(worker.getId()).toString());
                }

                size = 80;
                for (int a = 0; a < counter; a ++) {
                    if (currentSubIndex >= 0) {
                        a = currentSubIndex;
                    }

                    if (a >= counter) {
                        break;
                    }
                    
                    double amount = amounts.get(a);
                    total += amount;
                    
                    g.drawString(Common.currency(amount), x, y + (a * 20));
                    
                    g.drawLine(x - 5, y + 5 + (a * 20), x + 5 + size, y + 5 + (a * 20));
                    g.drawLine(x - 5, y - 15 + (a * 20), x + 5 + size, y - 15 + (a * 20));
                    g.drawLine(x - 5, y + 5 + (a * 20), x - 5, y - 15 + (a * 20));
                    g.drawLine(x + 5 + size, y + 5 + (a * 20), x + 5 + size, y - 15 + (a * 20));
                    currentSubIndex ++;
                }
                
                g.drawLine(x - 5, y + 5 , x + 5 + size, y + 5);
                g.drawLine(x - 5, y - 15, x + 5 + size, y - 15);
                g.drawLine(x - 5, y + 5, x - 5, y - 15 );
                g.drawLine(x + 5 + size, y + 5, x + 5 + size, y - 15);
                x += size + 10;

                totals.put(worker.getId(), total);
            }


            if (currentSubIndex > 0) {
                y += ((currentSubIndex - 1) * 20);
            }

            y += 20;
            
            currentIndex++;
            currentSubIndex = -1;
        }
    }

    private void printSummary(Graphics2D g) {
          int size = 0;

        g.drawString("Baki", x, y);
        size = 80;
        g.drawLine(x - 5, y + 5, x + 5 + size, y + 5);
        g.drawLine(x - 5, y - 15, x + 5 + size, y - 15);
        g.drawLine(x - 5, y + 5, x - 5, y - 15);
        g.drawLine(x + 5 + size, y + 5, x + 5 + size, y - 15);
        x += size + 10;

        for (Worker worker : selected) {
            double amount = 0.0;

            if (this.totals.get(worker.getId()) != null) {
                amount = Double.parseDouble(this.totals.get(worker.getId()).toString());
            }

            g.drawString(Common.currency(amount), x - g.getFontMetrics().stringWidth(Common.currency(amount)) - 10 + size, y);
            size = 80;
            g.drawLine(x - 5, y + 5, x + 5 + size, y + 5);
            g.drawLine(x - 5, y - 15, x + 5 + size, y - 15);
            g.drawLine(x - 5, y + 5, x - 5, y - 15);
            g.drawLine(x + 5 + size, y + 5, x + 5 + size, y - 15);
            x += size + 10;
        }
    }
}
