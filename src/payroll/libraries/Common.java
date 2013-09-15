/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package payroll.libraries;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

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

    public static String renderSQLDate(Date date) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");

        return df.format(date);
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

    public static Date convertStringToDate(String date) {

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        try {
            return df.parse(date);
        } catch (ParseException ex) {
            return new Date();
        }
    }

    public static String md5(byte[] defaultBytes)
    {
        StringBuffer hexString = new StringBuffer();
        try
        {
            MessageDigest algorithm = MessageDigest.getInstance("MD5");
            algorithm.reset();
            algorithm.update(defaultBytes);
            byte messageDigest[] = algorithm.digest();


            for(int i = 0; i < messageDigest.length;i ++)
            {
                String hex=Integer.toHexString(0xFF & messageDigest[i]);
                if(hex.length() == 1)
                    hexString.append('0');
                hexString.append(hex);
            }
        }
        catch(NoSuchAlgorithmException ex)
        {
            System.err.println(ex.getMessage());
        }
        
        return hexString.toString();
    }
}