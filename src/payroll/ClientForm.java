/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * ClientForm.java
 *
 * Created on Jul 18, 2012, 10:12:40 PM
 */

package payroll;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import payroll.model.Customer;

/**
 *
 * @author edward
 */
public class ClientForm extends javax.swing.JDialog {

    private Customer customer;
    int customer_id = 0;
    private boolean _loaded = false;

    /** Creates new form ClientForm */
    public ClientForm(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        customer = new Customer();
    }

    public ClientForm(java.awt.Frame parent, boolean modal, int customer_id) {
        super(parent, modal);
        initComponents();
        customer = new Customer(customer_id);
        this.customer_id = customer_id;
        this._load();
    }

    private void _load() {
        txtClientID.setText(customer.getCode());
        txtClientName.setText(customer.getName());
        if (customer.getStatus()) {
            rbtnActive.setSelected(true);
        } else {
            rbtnInactive.setSelected(true);
        }
        this._loaded = true;
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        jPanel1 = new javax.swing.JPanel();
        btnEnd = new javax.swing.JButton();
        btnSave = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        txtClientID = new javax.swing.JTextField();
        txtClientName = new javax.swing.JTextField();
        rbtnInactive = new javax.swing.JRadioButton();
        rbtnActive = new javax.swing.JRadioButton();
        jLabel4 = new javax.swing.JLabel();

        buttonGroup1.add(rbtnActive);
        buttonGroup1.add(rbtnInactive);

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Cipta Rekod Pelanggan Baru");

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        btnEnd.setText("Tamat");
        btnEnd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEndActionPerformed(evt);
            }
        });

        btnSave.setText("Rekodkan");
        btnSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(314, Short.MAX_VALUE)
                .addComponent(btnSave, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnEnd, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(btnSave, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnEnd, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        jLabel1.setText("Kod dan Nama Pekerja");

        rbtnInactive.setText("Tiada Aktif");

        rbtnActive.setSelected(true);
        rbtnActive.setText("Aktif");

        jLabel4.setText("Status");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtClientID, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtClientName, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(rbtnActive)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(rbtnInactive)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtClientID, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtClientName, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(rbtnActive, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(rbtnInactive)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 241, Short.MAX_VALUE)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnEndActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEndActionPerformed
        this.dispose();
}//GEN-LAST:event_btnEndActionPerformed

    private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveActionPerformed
        if ( ! this._check()) {
            return;
        }

        boolean active = rbtnActive.isSelected();

        String query = "";

        if (this.customer_id == 0) {
            query = "INSERT INTO customer(code, name, is_active) VALUES(?, ?, ?)";
        } else {
            query = "UPDATE customer SET code = ?, name = ?, is_active = ? WHERE customer_id = " + this.customer_id;
        }
        PreparedStatement ps = Application.db.createPreparedStatement(query);

        try {
            ps.setString(1, txtClientID.getText());
            ps.setString(2, txtClientName.getText());
            ps.setBoolean(3, active);
        } catch (SQLException ex) {
            Logger.getLogger(ex.getMessage()).log(Level.WARNING, null, ex);
        }

        boolean success = false;
        if (this.customer_id == 0) {
            this.customer_id = Application.db.insert(ps);
            success = this.customer_id != 0 ? true : false;
        } else {
            success = Application.db.update(ps);
        }

        if (success) {
            JOptionPane.showMessageDialog(null, "Rekod Pelanggan baru ditambah", "Berjaya!", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(null, "Rekod Pelanggan tidak dapat ditambah", "Kesilapan!", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btnSaveActionPerformed

    private boolean _check()
    {
        boolean valid = false;
        String message = "";
        if (txtClientID.getText().isEmpty()) {
            message = "Sila lengkapkan Kod Pelanggan";
        } else if (txtClientName.getText().isEmpty()) {
            message = "Silal lengkapkan Nama Pelanggan";
        } else {
            valid = true;
        }
        if ( ! valid) {
            JOptionPane.showMessageDialog(null, message, "Kesilapan", JOptionPane.ERROR_MESSAGE);
        }

        return valid;
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnEnd;
    private javax.swing.JButton btnSave;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JRadioButton rbtnActive;
    private javax.swing.JRadioButton rbtnInactive;
    private javax.swing.JTextField txtClientID;
    private javax.swing.JTextField txtClientName;
    // End of variables declaration//GEN-END:variables

}
