/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package payroll;

/**
 *
 * @author edward
 */
public class Application {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        java.awt.EventQueue.invokeLater((new Runnable() {
            public void run() {
                new Main().setVisible(true);
            }
        }));
    }

}
