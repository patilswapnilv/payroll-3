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
import payroll.libraries.Database;
import payroll.model.Worker;

/**
 *
 * @author Edward
 */
public class Printer implements Printable {

    private ArrayList<String> headers = new ArrayList<String>();
    private ArrayList<Worker> selected = new ArrayList<Worker>();
    private ResultSet results;
    private String query = "";
    private Main parent;

    final int printCap = 30;
    private int printerPointer = 0;
    private int pagesNeeded = 1;
    private int itemCount = 0;
    private int x = 0;
    private int y = 0;

    boolean printOrNot = true;

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
                itemCount++;
            }
        } catch (SQLException ex) {
            System.err.println("Error: Unable to get results size. Detail: " + ex.getMessage());
            ex.printStackTrace();
        }

        pagesNeeded = (int) Math.ceil((double) itemCount / printCap);

        System.out.println("There are " + itemCount + " items, We need: " + pagesNeeded + " pages(s)");
    }

    public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException {
        Calendar date = Calendar.getInstance();
        Graphics2D g = (Graphics2D) graphics;
        g.translate(pageFormat.getImageableX(), pageFormat.getImageableY());

        printOrNot = printOrNot ? false : true;

        if (pageIndex >= pagesNeeded) {
            System.out.println("No more pages");
            return NO_SUCH_PAGE;
        }

        if (printOrNot) {
            System.out.println("Printing...");

            x = 10;
            y = 40;
            int size = 0;
            int tableMargin = 5;
            g.setFont(new Font("Calibri", Font.BOLD, 12));
            if (parent.chkMonthlyReportDate.isSelected()) {
                g.drawString("Tarikh", x, y);
                size = 50;
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
                size = 100;
                g.drawLine(x - 5, y + 5, x + 5 + size, y + 5);
                g.drawLine(x - 5, y - 35, x + 5 + size, y - 35);
                g.drawLine(x - 5, y + 5, x - 5, y - 35);
                g.drawLine(x + 5 + size, y + 5, x + 5 + size, y - 35);
                x += size + 10;
            }

            if (parent.chkMonthlyReportKiraanAsing.isSelected()) {
                g.drawString("Kiraan", x, y - 15);
                g.drawString("Asing", x, y);
                size = 80;
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
                g.drawString("Harga", x, y);
                g.drawString("Seton", x, y - 15);
                size = 50;
                g.drawLine(x - 5, y + 5, x + 5 + size, y + 5);
                g.drawLine(x - 5, y - 35, x + 5 + size, y - 35);
                g.drawLine(x - 5, y + 5, x - 5, y - 35);
                g.drawLine(x + 5 + size, y + 5, x + 5 + size, y - 35);
                x += size + 10;
            }

            if (parent.chkMonthlyReportTotalReceived.isSelected()) {
                g.drawString("Jumlah", x, y);
                g.drawString("Diterima", x, y - 15);
                size = 50;
                g.drawLine(x - 5, y + 5, x + 5 + size, y + 5);
                g.drawLine(x - 5, y - 35, x + 5 + size, y - 35);
                g.drawLine(x - 5, y + 5, x - 5, y - 35);
                g.drawLine(x + 5 + size, y + 5, x + 5 + size, y - 35);
                x += size + 10;
            }

            
        }

        return PAGE_EXISTS;
    }
}
