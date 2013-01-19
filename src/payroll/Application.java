/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package payroll;

import java.awt.Color;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.plaf.ColorUIResource;
import payroll.libraries.Database;

/**
 *
 * @author edward
 */
public class Application {

    public static int id = 1;
    public static Database db = null;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        java.awt.EventQueue.invokeLater((new Runnable() {
            public void run() {
                try {
                    db = Database.instance();
                    
                    /*try {
                        for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                            if ("Nimbus".equals(info.getName())) {
                                UIManager.setLookAndFeel(info.getClassName());
                                break;
                            }
                        }
                    } catch (Exception e) {
                        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                    }*/
                    

                    //new Login(null, true).setVisible(true);
                    String query = "PRAGMA table_info('transactions')";
                    ResultSet rs = db.execute(query);

                    try {
                        boolean found = false;
                        while (rs.next()) {
                            if (rs.getString("name").equalsIgnoreCase("wages_tax")) {
                                found = true;
                                break;
                            }
                        }


                        if ( ! found) {
                            query = "ALTER TABLE transactions ADD wages_tax DOUBLE DEFAULT 0.0 NOT NULL";
                            db.update(query);
                        }
                    } catch (SQLException ex) {
                        throw ex;
                    }
                    
                    new Main().setVisible(true);
                } catch (Exception ex) {
                    System.err.println(ex.getMessage());
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Unexpected Error occur!!", "ERROR", JOptionPane.ERROR_MESSAGE);
                    System.exit(1);
                }
            }
        }));
    }

}
