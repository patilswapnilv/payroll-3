/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package payroll;

import javax.swing.JOptionPane;
import javax.swing.UIManager;
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
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

                    //new Login(null, true).setVisible(true);
                    new Main().setVisible(true);
                } catch (Exception ex) {
                    System.err.println(ex.getMessage());
                    JOptionPane.showMessageDialog(null, "Unexpected Error occur!!", "ERROR", JOptionPane.ERROR_MESSAGE);
                    System.exit(1);
                }
            }
        }));
    }

}
