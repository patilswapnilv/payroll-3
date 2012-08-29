/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package payroll.model;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import payroll.Application;
import payroll.libraries.Database;


/*  @author Edward  */
public class Customer {
    int id;
    String code;
    String name;
    boolean status;

    private boolean _loaded = false;

    public Customer() {
        id = 0;
        code = null;
        name = null;
        status = false;
    }

    public Customer(int id) {
        code = null;
        name = null;
        status = false;
        this._load(id);
    }

    public Customer(int id, String code, String name, boolean status) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.status = status;
    }

    private boolean _load(int id) {
        String query = "SELECT * FROM customer WHERE customer_id = " + id;
        ResultSet rs = Database.instance().execute(query);
        try {
            rs.next();
            this.code = rs.getString("code");
            this.name = rs.getString("name");
            this.status = rs.getBoolean("is_active");
            this.id = id;
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

    public boolean save()
    {
        String query = "";

        if ( ! this._loaded) {
            query = "INSERT INTO customer(code, name, is_active) VALUES(?, ?, ?)";
        } else {
            query = "UPDATE customer SET code = ?, name = ?, is_active = ? WHERE customer_id = " + id;
        }

        PreparedStatement ps = Application.db.createPreparedStatement(query);

        try {
            ps.setString(1, this.getCode());
            ps.setString(2, this.getName());
            ps.setBoolean(3, this.getStatus());
        } catch (SQLException ex) {
            Logger.getLogger(ex.getMessage()).log(Level.WARNING, null, ex);
            System.err.println(ex.getMessage());
        }

        if ( ! this._loaded) {
            this.id = Application.db.insert(ps);
            return this.id != 0 ? true : false;
        } else {
            return Application.db.update(ps);
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
