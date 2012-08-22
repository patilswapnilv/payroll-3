/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * WorkerForm.java
 *
 * Created on Jul 18, 2012, 9:48:26 PM
 */

package payroll;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JOptionPane;
import payroll.model.Worker;

/**
 *
 * @author edward
 */
public class WorkerForm extends javax.swing.JDialog {

    private Worker worker;
    int id = 0;
    private boolean _loaded = false;
    
    /** Creates new form WorkerForm */
    public WorkerForm(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        worker = new Worker();
    }

    public WorkerForm(java.awt.Frame parent, boolean modal, int id) {
        super(parent, modal);
        initComponents();
        worker = new Worker(id);
        this.id = id;
        this._load();
    }

    private void _load() {
        txtRegisterDate.setDate(worker.getRegisterDate());
        txtReturnDate.setDate(worker.getReturnDate());
        txtWorkerID.setText(worker.getCode());
        txtWorkerName.setText(worker.getName());
        if (worker.getStatus()) {
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

        btnGroupStatus = new javax.swing.ButtonGroup();
        jLabel1 = new javax.swing.JLabel();
        txtWorkerID = new javax.swing.JTextField();
        txtWorkerName = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        txtRegisterDate = new com.toedter.calendar.JDateChooser();
        jLabel3 = new javax.swing.JLabel();
        txtReturnDate = new com.toedter.calendar.JDateChooser();
        jLabel4 = new javax.swing.JLabel();
        rbtnActive = new javax.swing.JRadioButton();
        rbtnInactive = new javax.swing.JRadioButton();
        jPanel1 = new javax.swing.JPanel();
        btnEnd = new javax.swing.JButton();
        btnSave = new javax.swing.JButton();

        btnGroupStatus.add(rbtnActive);
        btnGroupStatus.add(rbtnInactive);

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Cipta Rekod Pekerja Baru");

        jLabel1.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel1.setText("Kod dan Nama Pekerja");

        txtWorkerID.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N

        txtWorkerName.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N

        jLabel2.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel2.setText("Tarikh Bermula");

        txtRegisterDate.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N

        jLabel3.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel3.setText("Tarikh Tamat");

        txtReturnDate.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N

        jLabel4.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel4.setText("Status");

        rbtnActive.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        rbtnActive.setSelected(true);
        rbtnActive.setText("Aktif");

        rbtnInactive.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        rbtnInactive.setText("Tidak Aktif");

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        btnEnd.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        btnEnd.setText("Tutup");
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
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(btnEnd, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnSave, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 151, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 151, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 151, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 151, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(txtWorkerID, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtWorkerName, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(txtReturnDate, javax.swing.GroupLayout.PREFERRED_SIZE, 266, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(rbtnActive)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(rbtnInactive))
                            .addComponent(txtRegisterDate, javax.swing.GroupLayout.PREFERRED_SIZE, 266, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(16, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtWorkerID, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtWorkerName, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtRegisterDate, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtReturnDate, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(rbtnActive, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(rbtnInactive))
                .addGap(18, 18, 18)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        txtRegisterDate.setDateFormatString("dd/MM/yyyy");
        txtReturnDate.setDateFormatString("dd/MM/yyyy");

        java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        setBounds((screenSize.width-463)/2, (screenSize.height-247)/2, 463, 247);
    }// </editor-fold>//GEN-END:initComponents

    private void btnEndActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEndActionPerformed
        this.dispose();
    }//GEN-LAST:event_btnEndActionPerformed

    private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveActionPerformed
        if ( ! this._check()) {
            return;
        }

        worker.setCode(txtWorkerID.getText());
        worker.setName(txtWorkerName.getText());
        worker.setRegisterDate(new java.sql.Date(txtRegisterDate.getDate().getTime()));
        worker.setReturnDate(new java.sql.Date(txtReturnDate.getDate().getTime()));
        worker.setStatus(rbtnActive.isSelected());

        boolean success = worker.save();

        if (success) {
            Main main = (Main) this.getParent();
            main.load_workers();
            JOptionPane.showMessageDialog(null, "Rekod Pekerja baru disimpan", "Berjaya!", JOptionPane.INFORMATION_MESSAGE);
            this.dispose();
        } else {
            JOptionPane.showMessageDialog(null, "Rekod Pekerja tidak dapat disimpan", "Kesilapan!", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btnSaveActionPerformed


    private boolean _check()
    {
        boolean valid = false;
        boolean valid_code = false;
        String message = "";
        String query = "SELECT COUNT(worker_id) FROM worker WHERE code = ?";

        if (this._loaded) {
            query += " AND worker_id != " + this.id;
        }
        
        PreparedStatement ps = Application.db.createPreparedStatement(query);
        try {
            ps.setString(1, txtWorkerID.getText());
            ResultSet rs = Application.db.execute(ps);
            rs.next();
            valid_code = rs.getInt(1) > 0 ? false : true;
            rs.close();
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }
        
        if (txtWorkerID.getText().isEmpty()) {
            message = "Sila lengkapkan Kod Pekerja";
        } else if ( ! valid_code) {
            message = "Kod Pekerja telah di guna";
        } else if (txtWorkerName.getText().isEmpty()) {
            message = "Sila lengkapkan Nama Pekerja";
        } else if (txtRegisterDate.getDate() == null) {
            message = "Sila lengkapkan Tarikh Bermula";
        } else if (txtReturnDate.getDate() == null) {
            message = "Sila lengkapkan Tarikh Tamat";
        } else if (txtReturnDate.getDate().before(txtRegisterDate.getDate())) {
            message = "Tarikh Tamat harus selepas Tarikh Bermula";
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
    private javax.swing.ButtonGroup btnGroupStatus;
    private javax.swing.JButton btnSave;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JRadioButton rbtnActive;
    private javax.swing.JRadioButton rbtnInactive;
    private com.toedter.calendar.JDateChooser txtRegisterDate;
    private com.toedter.calendar.JDateChooser txtReturnDate;
    private javax.swing.JTextField txtWorkerID;
    private javax.swing.JTextField txtWorkerName;
    // End of variables declaration//GEN-END:variables

}
