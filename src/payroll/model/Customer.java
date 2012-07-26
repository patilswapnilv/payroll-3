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
    private int id;
    private String code, name;
    private boolean status;

    private boolean _loaded;

    public Customer() {
        id = 0;
        code = null;
        name = null;
        status = false;
    }

    public Customer(int id) {
        this.id = id;
        this._load();
    }

    public Customer(int id, String code, String name, boolean status) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.status = status;
    }

    private void _load() {
        String query = "SELECT * FROM customer WHERE customer_id = " + this.id;
        ResultSet rs = Database.instance().execute(query);

        try {
            rs.next();
            this.setCode(rs.getString("code"));
            this.setName(rs.getString("name"));
            this.setStatus(rs.getBoolean("is_active"));
            this._loaded = true;
        } catch (SQLException ex) {
            Logger.getLogger(Customer.class.getName()).log(Level.SEVERE, null, ex);
            System.err.println(ex.getMessage());
        }
    }

    public int getId() {
        return id;
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

    public void setId(int id) {
        this.id = id;
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
