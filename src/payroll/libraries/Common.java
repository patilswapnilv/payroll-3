/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package payroll.libraries;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import javax.swing.DefaultListModel;

/**
 *
 * @author Edward
 */
public class Common {
    public static boolean isDouble(String value) {
        try {
            Double.parseDouble(value);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    public static String currency(double value) {
        DecimalFormat df = new DecimalFormat("#,###,###,##0.00");
        return df.format(value);
    }

    public static boolean inArray(String[] haystack, String needle) {
        for (int i = 0; i < haystack.length ; i ++) {
            if (haystack[i].equals(needle)) {
                return true;
            }
        }

        return false;
    }

    public static boolean inArray(String[] haystack, int needle) {
        for (int i = 0; i < haystack.length ; i ++) {
            if (haystack[i].equals("" + needle)) {
                return true;
            }
        }

        return false;
    }

    public static String renderSQLDate(Calendar calender) {
        int year, month, day;

        year = calender.get(Calendar.YEAR);
        month = calender.get(Calendar.MONTH) + 1;
        day = calender.get(Calendar.DAY_OF_MONTH);

        return year + "-" + (month > 9 ? month : "0" + month) + "-" + (day > 9 ? day : "0" + day);
    }

    public static String renderDisplayDate(Date date) {
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");

        return df.format(date);
    }

    public static String renderDisplayDate(Calendar calender) {
        int year, month, day;

        year = calender.get(Calendar.YEAR);
        month = calender.get(Calendar.MONTH) + 1;
        day = calender.get(Calendar.DAY_OF_MONTH);

        return (day > 9 ? day : "0" + day) + "/" + (month > 9 ? month : "0" + month) + "/" + year;
    }
}
