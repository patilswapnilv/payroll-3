/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package payroll;

import java.util.Hashtable;
import java.util.Set;

/**
 *
 * @author Edward
 */
public class Saving {

    Hashtable<Integer, Double> savings;

    public Saving() {
        this.savings = new Hashtable<Integer, Double>();
    }

    public void addSaving(int workerID, double  saving) {
        this.savings.put(workerID, saving);
    }

    public double getSaving(int workerID) {
        if (this.savings.containsKey(workerID)) {
            return this.savings.get(workerID);
        }

        return 0.0;
    }

    public boolean isEmpty() {
        return this.savings.isEmpty();
    }
}
