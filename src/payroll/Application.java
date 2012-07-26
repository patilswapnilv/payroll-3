/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package payroll;

import javax.swing.UIManager;
import payroll.libraries.Database;

/**
 *
 * @author edward
 */
public class Application {

    public static Database db = null;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        java.awt.EventQueue.invokeLater((new Runnable() {
            public void run() {
                db = Database.instance();
                try {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch (Exception ex) {
                    System.err.println(ex.getMessage());
                }
                new Main().setVisible(true);
            }
        }));
    }

}
