/*  To change this template, choose Tools | Templates
 *  and open the template in the editor.
 */

/*  ClientForm.java
 *  Created on Jul 18, 2012, 10:12:40 PM
 */

package payroll;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JOptionPane;
import payroll.model.Customer;

/*  @author edward  */
public class ClientForm extends javax.swing.JDialog {

    private Customer customer;
    int id = 0;
    private boolean _loaded = false;

    /** Creates new form ClientForm */
    public ClientForm(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        customer = new Customer();
    }

    public ClientForm(java.awt.Frame parent, boolean modal, int id) {
        super(parent, modal);
        initComponents();
        customer = new Customer(id);
        this.id = id;
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

        btnEnd.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        btnEnd.setText("Tamat");
        btnEnd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEndActionPerformed(evt);
            }
        });

        btnSave.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
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
                .addContainerGap()
                .addComponent(btnSave, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnEnd, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnEnd)
                    .addComponent(btnSave))
                .addContainerGap())
        );

        jLabel1.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel1.setText("Kod dan Nama Pekerja");

        txtClientID.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N

        txtClientName.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N

        rbtnInactive.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        rbtnInactive.setText("Tiada Aktif");

        rbtnActive.setSelected(true);
        rbtnActive.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        rbtnActive.setText("Aktif");

        jLabel4.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel4.setText("Status");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 150, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(txtClientID, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtClientName, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(rbtnActive)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(rbtnInactive)))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(rbtnActive, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(rbtnInactive))
                .addGap(18, 18, 18)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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

        customer.setCode(txtClientID.getText());
        customer.setName(txtClientName.getText());
        customer.setStatus(rbtnActive.isSelected());

        boolean success = customer.save();

        if (success) {
            Main main = (Main) this.getParent();
            main.load_customers();
            JOptionPane.showMessageDialog(null, "Rekod Pelanggan baru ditambah", "Berjaya!", JOptionPane.INFORMATION_MESSAGE);
            this.dispose();
        } else {
            JOptionPane.showMessageDialog(null, "Rekod Pelanggan tidak dapat ditambah", "Kesilapan!", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btnSaveActionPerformed

    private boolean _check()
    {
        boolean valid = false;
        boolean valid_code = false;
        String message = "";
        String query = "SELECT COUNT(customer_id) FROM customer WHERE code = ?";

        if (this._loaded) {
            query += " AND customer_id != " + this.id;
        }

        PreparedStatement ps = Application.db.createPreparedStatement(query);
        try {
            ps.setString(1, txtClientID.getText());
            ResultSet rs = Application.db.execute(ps);
            rs.next();
            valid_code = rs.getInt(1) > 0 ? false : true;
            rs.close();
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }

        if (txtClientID.getText().isEmpty()) {
            message = "Sila lengkapkan Kod Pelanggan";
        } else if (txtClientName.getText().isEmpty()) {
            message = "Sila lengkapkan Nama Pelanggan";
        } else {
            valid = true;
        }
        if ( ! valid) {
            JOptionPane.showMessageDialog(null, message, "Kesilapan!", JOptionPane.ERROR_MESSAGE);
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
