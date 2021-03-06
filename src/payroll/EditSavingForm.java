/*  To change this template, choose Tools | Templates
 *  and open the template in the editor.
 *  SavingForm.java
 *  Created on Jul 24, 2012, 1:03:21 PM
 */
package payroll;

import com.toedter.calendar.JDateChooser;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import javax.swing.JOptionPane;
import payroll.libraries.Database;
import payroll.model.Worker;
import payroll.model.WorkerRecord;

public class EditSavingForm extends javax.swing.JDialog {

    private ArrayList<Worker> workers = new ArrayList<Worker>();

    /**
     * Creates form EditSavingForm
     */
    public EditSavingForm(Main parent, boolean modal, Integer savingID)
            throws SQLException, ParseException {
        super(parent, modal);
        initComponents();

        workers = parent.workers;

        String query = "SELECT * FROM workerRecord WHERE id = " + savingID;

        ResultSet rs = Database.instance().execute(query);
        while (rs.next()) {
            this.workerRecordID.setText(savingID.toString());

            Integer workerID = rs.getInt("worker_id");
            for (Worker worker : workers) {
                if (worker.getId() == workerID) {
                    this.workerDetails.setText(worker.getCode() + " - "
                            + worker.getName());
                }
            }

            String savingDate = rs.getString("date");
            Date date = new SimpleDateFormat("yyyy-MM-dd",
                    Locale.ENGLISH).parse(savingDate);
            this.txtSavingDate.setDate(date);

            String desc = rs.getString("description");
            this.txtDescription.setText(desc);

            boolean is_pay = rs.getBoolean("is_pay");
            float saving = rs.getFloat("amount");
            if (is_pay) {
                this.txtPayAmount.setText(Float.toString(saving));
            } else {
                this.txtSavingAmount.setText(Float.toString(saving));
            }
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        txtSavingDate = new com.toedter.calendar.JDateChooser();
        txtSavingAmount = new javax.swing.JTextField();
        txtPayAmount = new javax.swing.JTextField();
        jPanel1 = new javax.swing.JPanel();
        btnEnd = new javax.swing.JButton();
        btnSave = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        txtDescription = new javax.swing.JTextArea();
        jLabel6 = new javax.swing.JLabel();
        workerRecordID = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        workerDetails = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Ubah Simpanan");
        setFocusable(false);

        jLabel2.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel2.setText("Tarikh");

        jLabel3.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel3.setText("Amaun Simpanan [ + ]");

        jLabel4.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel4.setText("Keterangan");

        jLabel5.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel5.setText("Amaun Bayaran [ - ]");

        txtSavingDate.setDateFormatString("dd/MM/yyyy");
        txtSavingDate.setDate(Calendar.getInstance().getTime());
        txtSavingDate.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        txtSavingDate.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                txtSavingDatePropertyChange(evt);
            }
        });

        txtSavingAmount.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N

        txtPayAmount.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        btnEnd.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        btnEnd.setText("Tutup");
        btnEnd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEndActionPerformed(evt);
            }
        });

        btnSave.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        btnSave.setText("Ubah");
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

        txtDescription.setColumns(20);
        txtDescription.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        txtDescription.setRows(5);
        jScrollPane1.setViewportView(txtDescription);

        jLabel6.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel6.setText("No. Rekod");

        workerRecordID.setEditable(false);
        workerRecordID.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N

        jLabel7.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel7.setText("Pekerja");

        workerDetails.setEditable(false);
        workerDetails.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtSavingDate, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel5, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtSavingAmount)
                            .addComponent(txtPayAmount)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 258, Short.MAX_VALUE)))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(workerDetails))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(workerRecordID)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(workerRecordID, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(workerDetails, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtSavingDate, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtSavingAmount, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtPayAmount, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        setSize(new java.awt.Dimension(438, 365));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void btnEndActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEndActionPerformed
        this.dispose();
}//GEN-LAST:event_btnEndActionPerformed

    private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveActionPerformed
        if (!this._check()) {
            return;
        }

        Date savingDate = new java.sql.Date(txtSavingDate.getDate().getTime());
        String desc = this.txtDescription.getText();

        int type = 0;
        double amount = 0;
        if (!txtSavingAmount.getText().isEmpty()) {
            amount = Double.parseDouble(txtSavingAmount.getText());
            type = WorkerRecord.SAVING;
        }
        if (!txtPayAmount.getText().isEmpty()) {
            amount = 0 - Double.parseDouble(txtPayAmount.getText());
            type = WorkerRecord.WITHDRAW;
        }

        String query = "UPDATE workerRecord SET "
                + "date = ?, amount = ?, type = ?, description = ?"
                + "WHERE id = "
                + Integer.parseInt(this.workerRecordID.getText());

        PreparedStatement ps = Database.instance().createPreparedStatement(query);
        try {           
            ps.setString(1, savingDate.toString());
            ps.setDouble(2, amount);
            ps.setInt(3, type);
            ps.setString(4, desc);
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }

        if (Database.instance().update(ps)) {
            JOptionPane.showMessageDialog(null, "Rekod Simpanan diubah.",
                    "Berjaya!", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(null,
                    "Rekod Simpanan tidak dapat diubah.", "Kesilapan!",
                    JOptionPane.ERROR_MESSAGE);
        }

        this.dispose();
    }//GEN-LAST:event_btnSaveActionPerformed

    private void txtSavingDatePropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_txtSavingDatePropertyChange
        if (evt.getNewValue() instanceof Date) {
            Date date = (Date) evt.getNewValue();

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);

            if (calendar.get(Calendar.YEAR) <= 2000) {
                int year = calendar.get(Calendar.YEAR) + 2000;
                calendar.set(Calendar.YEAR, year);
            }

            JDateChooser dateChooser = (JDateChooser) evt.getSource();
            dateChooser.setDate(calendar.getTime());
        }
    }//GEN-LAST:event_txtSavingDatePropertyChange

    private boolean _check() {
        boolean valid = false;
        String message = "";

        if (txtSavingDate.getDate() == null) {
            message = "Sila lengkapkan Tarikh";
        } else if ((txtSavingAmount.getText().isEmpty()) && (txtPayAmount.getText().isEmpty())) {
            message = "Sila lengkapkan Amaun Simpanan atau Amaun Bayaran";
        } else if ((!txtSavingAmount.getText().isEmpty()) && (!txtPayAmount.getText().isEmpty())) {
            message = "Sila pilih Amaun Simpanan atau Amaun Bayaran sahaja";
        } else if (!txtSavingAmount.getText().isEmpty()) {
            String savingAmount = txtSavingAmount.getText();
            try {
                Double.parseDouble(savingAmount);
                valid = true;
            } catch (NumberFormatException e) {
                message = "Sila lengkapkan Amaun Simpanan. contoh: 123.45";
            }
        } else if (!txtPayAmount.getText().isEmpty()) {
            String payAmount = txtPayAmount.getText();
            try {
                Double.parseDouble(payAmount);
                valid = true;
            } catch (NumberFormatException e) {
                message = "Sila lengkapkan Amaun Bayaran. contoh: 123.45";
            }
        } else {
            valid = true;
        }
        if (!valid) {
            JOptionPane.showMessageDialog(null, message, "Kesilapan!", JOptionPane.ERROR_MESSAGE);
        }

        return valid;
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnEnd;
    private javax.swing.JButton btnSave;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextArea txtDescription;
    private javax.swing.JTextField txtPayAmount;
    private javax.swing.JTextField txtSavingAmount;
    private com.toedter.calendar.JDateChooser txtSavingDate;
    private javax.swing.JTextField workerDetails;
    private javax.swing.JTextField workerRecordID;
    // End of variables declaration//GEN-END:variables
}