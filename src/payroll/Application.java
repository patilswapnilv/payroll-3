/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package payroll;

import payroll.libraries.Database;

/**
 *
 * @author edward
 */
public class Application {

    static Database db = null;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        java.awt.EventQueue.invokeLater((new Runnable() {
            public void run() {
                db = new Database("payroll.sqlite");
                new Main().setVisible(true);
            }
        }));
    }

}
