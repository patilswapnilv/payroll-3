/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package payroll.libraries;

import java.text.DecimalFormat;

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
}
