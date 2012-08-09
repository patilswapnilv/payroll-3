/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package payroll.model;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import payroll.libraries.Database;

/**
 *
 * @author Edward
 */
public class Customer {
    int customer_id;
    String code, name;
    boolean status;

    private boolean _loaded;

    public Customer() {
        customer_id = 0;
        code = null;
        name = null;
        status = false;
    }

    public Customer(int customer_id) {
        this._load(customer_id);
    }

    public Customer(int customer_id, String code, String name, boolean status) {
        this.customer_id = customer_id;
        this.code = code;
        this.name = name;
        this.status = status;
    }

    private boolean _load(int customer_id) {
        String query = "SELECT * FROM customer WHERE customer_id = " + this.customer_id;
        ResultSet rs = Database.instance().execute(query);

        try {
            this.setCode(rs.getString("code"));
            this.setName(rs.getString("name"));
            this.setStatus(rs.getBoolean("is_active"));
            this.customer_id = customer_id;
            this._loaded = true;
        } catch (SQLException ex) {
            Logger.getLogger(Customer.class.getName()).log(Level.SEVERE, null, ex);
        }

        return this.loaded();
    }

    public boolean loaded()
    {
        return this._loaded;
    }

    public int getId() {
        return customer_id;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public boolean getStatus() {
        return status;
    }

    public void setId(int customer_id) {
        this.customer_id = customer_id;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }
}
