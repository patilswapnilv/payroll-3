/*  To change this template, choose Tools | Templates
 *  and open the template in the editor.
 */

/*  SavingForm.java
 *  Created on Jul 24, 2012, 1:03:21 PM
 */

package payroll;

//import java.beans.PropertyChangeEvent;
//import java.beans.PropertyChangeListener;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import payroll.libraries.Common;
import payroll.libraries.Database;
import payroll.model.Customer;
import payroll.model.Transaction;
import payroll.model.Worker;
import payroll.model.WorkerRecord;


/**
 * @author minglih.khor
 */
public class SavingForm extends javax.swing.JDialog {

    private Worker worker;
    private WorkerRecord workerRecord;
    int id = 0;
    private int _current_worker_id = 0;

    /** Creates new form SavingForm */
    public SavingForm(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        workerRecord = new WorkerRecord();
    }

    public SavingForm(java.awt.Frame parent, boolean modal, int id) {
        super(parent, modal);
        initComponents();
        workerRecord = new WorkerRecord();
        this.id = id;
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        txtWorkerCode = new javax.swing.JTextField();
        txtWorkerName = new javax.swing.JTextField();
        txtSavingDate = new com.toedter.calendar.JDateChooser();
        txtSavingAmount = new javax.swing.JTextField();
        txtPayAmount = new javax.swing.JTextField();
        jPanel1 = new javax.swing.JPanel();
        btnEnd = new javax.swing.JButton();
        btnSave = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        txtDescription = new javax.swing.JTextArea();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Simpanan Tetap");
        setFocusable(false);

        jLabel1.setText("Pekerja");

        jLabel2.setText("Tarikh");

        jLabel3.setText("Amaun Simpanan [+]");

        jLabel4.setText("Keterangan");

        jLabel5.setText("Amaun Bayaran [-]");

        txtWorkerCode.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtWorkerCodeActionPerformed(evt);
            }
        });

        txtSavingDate.setDateFormatString("dd/MM/yyyy");

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
                .addContainerGap(320, Short.MAX_VALUE)
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

        txtDescription.setColumns(20);
        txtDescription.setRows(5);
        jScrollPane1.setViewportView(txtDescription);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                            .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(txtWorkerCode, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(txtWorkerName))
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                            .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(txtSavingDate, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                            .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(txtSavingAmount))
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                            .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(txtPayAmount))
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                            .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtWorkerCode, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtWorkerName, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
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
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 87, Short.MAX_VALUE)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        setBounds((screenSize.width-616)/2, (screenSize.height-438)/2, 616, 438);
    }// </editor-fold>//GEN-END:initComponents

    private void btnEndActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEndActionPerformed
        this.dispose();
}//GEN-LAST:event_btnEndActionPerformed

    private void txtWorkerCodeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtWorkerCodeActionPerformed
        String worker_code = txtWorkerCode.getText();

        String query = "SELECT * FROM worker WHERE code LIKE ? AND is_active = 1 LIMIT 1";

        PreparedStatement ps = Application.db.createPreparedStatement(query);
        try {
            ps.setString(1, worker_code + "%");
        } catch (SQLException ex) {
            Error.error(ex, "");
        }

        ResultSet rs = Application.db.execute(ps);
        try {
            rs.next();
            txtWorkerCode.setText(rs.getString("code"));
            txtWorkerName.setText(rs.getString("name"));
            this._current_worker_id = rs.getInt("worker_id");
            rs.close();
        } catch (SQLException ex) {
            this._reset_worker_form();
            JOptionPane.showMessageDialog(null, "Tiada Rekod Pekerja ini", "Kesilapan!", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_txtWorkerCodeActionPerformed

    private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveActionPerformed
        if ( ! this._check()) {
            return;
        }

        workerRecord.setWorkerID(_current_worker_id);
        workerRecord.setDate(new java.sql.Date(txtSavingDate.getDate().getTime()));
        workerRecord.setDescription(txtDescription.getText());
        if (!txtSavingAmount.getText().isEmpty()) {
            workerRecord.setAmount(Double.parseDouble(txtSavingAmount.getText()));
            workerRecord.setType(WorkerRecord.SAVING);
        }
        if (!txtPayAmount.getText().isEmpty()) {
            workerRecord.setAmount(0 - Double.parseDouble(txtPayAmount.getText()));
            workerRecord.setType(WorkerRecord.WITHDRAW);
        }
        
        

        boolean success = workerRecord.save();

        if (success) {
            JOptionPane.showMessageDialog(null, "Rekod Transaksi baru ditambah", "Berjaya!", JOptionPane.INFORMATION_MESSAGE);
            this.dispose();
        } else {
            JOptionPane.showMessageDialog(null, "Rekod Transaksi tidak dapat ditambah", "Kesilapan!", JOptionPane.ERROR_MESSAGE);
        }

    }//GEN-LAST:event_btnSaveActionPerformed

    private boolean _check()
    {
        boolean valid = false;
        boolean valid_code = false;
        String message = "";
        String query = "SELECT COUNT(worker_id) FROM worker WHERE code = ?";

        if (txtWorkerCode.getText().isEmpty()) {
            message = "Sila lengkapkan Kod Pekerja";
        } else if (txtSavingDate.getDate() == null) {
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
            } catch(NumberFormatException e){
                message = "Sila lengkapkan Amaun Simpanan. contoh: 123.45";
            }
            //DecimalFormat df = new DecimalFormat("0.00");
            //txtSavingAmount.setText("" + df.format(savingAmount));
        } else if (!txtPayAmount.getText().isEmpty()) {
            String payAmount = txtPayAmount.getText();
            try {
                Double.parseDouble(payAmount);
                valid = true;
            } catch(NumberFormatException e){
                message = "Sila lengkapkan Amaun Bayaran. contoh: 123.45";
            }
            //DecimalFormat df = new DecimalFormat("0.00");
            //txtSavingAmount.setText("" + df.format(savingAmount));
        } else {
            valid = true;
        }
        if ( ! valid) {
            JOptionPane.showMessageDialog(null, message, "Kesilapan!", JOptionPane.ERROR_MESSAGE);
        }

        return valid;
    }


    private void _reset_worker_form() {
        txtWorkerName.setText("");
    }


    /**
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                SavingForm dialog = new SavingForm(new javax.swing.JFrame(), true);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnEnd;
    private javax.swing.JButton btnSave;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextArea txtDescription;
    private javax.swing.JTextField txtPayAmount;
    private javax.swing.JTextField txtSavingAmount;
    private com.toedter.calendar.JDateChooser txtSavingDate;
    private javax.swing.JTextField txtWorkerCode;
    private javax.swing.JTextField txtWorkerName;
    // End of variables declaration//GEN-END:variables

}