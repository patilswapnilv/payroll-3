/*  To change this template, choose Tools | Templates
 *  and open the template in the editor.
 */

/*  SavingForm.java
 *  Created on Jul 24, 2012, 1:03:21 PM
 */

package payroll;

//import java.beans.PropertyChangeEvent;
//import java.beans.PropertyChangeListener;
//import java.sql.PreparedStatement;
//import java.sql.ResultSet;
//import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import javax.swing.JOptionPane;
import payroll.model.Worker;
import payroll.model.WorkerRecord;


/**
 * @author minglih.khor
 */
public class SavingForm extends javax.swing.JDialog {

    private ArrayList<Worker> workers = new ArrayList<Worker>();
    private WorkerRecord workerRecord;

    /** Creates new form SavingForm */
    public SavingForm(Main parent, boolean modal) {
        super(parent, modal);
        initComponents();
        workerRecord = new WorkerRecord();

        workers = parent.workers;

        this.setup();
    }

    private void setup() {
        cbxSalaryWorkers.removeAllItems();
        cbxSalaryWorkers.addItem(new String());

        for (Worker worker : workers) {
            cbxSalaryWorkers.addItem(new String(worker.getCode() + " - " + worker.getName()));
        }
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
        txtSavingDate = new com.toedter.calendar.JDateChooser();
        txtSavingAmount = new javax.swing.JTextField();
        txtPayAmount = new javax.swing.JTextField();
        jPanel1 = new javax.swing.JPanel();
        btnEnd = new javax.swing.JButton();
        btnSave = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        txtDescription = new javax.swing.JTextArea();
        cbxSalaryWorkers = new javax.swing.JComboBox();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Simpanan Tetap");
        setFocusable(false);

        jLabel1.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel1.setText("Pekerja");

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
        txtSavingDate.setFont(new java.awt.Font("Arial", 0, 12));

        txtSavingAmount.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N

        txtPayAmount.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        btnEnd.setFont(new java.awt.Font("Arial", 0, 14));
        btnEnd.setText("Tutup");
        btnEnd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEndActionPerformed(evt);
            }
        });

        btnSave.setFont(new java.awt.Font("Arial", 0, 14));
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
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 132, Short.MAX_VALUE)
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

        cbxSalaryWorkers.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 140, Short.MAX_VALUE)
                            .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, 140, Short.MAX_VALUE)
                            .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, 140, Short.MAX_VALUE)
                            .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, 140, Short.MAX_VALUE)
                            .addComponent(jLabel5, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 140, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(cbxSalaryWorkers, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(txtSavingAmount)
                                .addComponent(txtSavingDate, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(txtPayAmount)
                                .addComponent(jScrollPane1)))))
                .addContainerGap(16, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbxSalaryWorkers, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
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
                .addGap(18, 18, 18)
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        setBounds((screenSize.width-438)/2, (screenSize.height-334)/2, 438, 334);
    }// </editor-fold>//GEN-END:initComponents

    private void btnEndActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEndActionPerformed
        this.dispose();
}//GEN-LAST:event_btnEndActionPerformed

    private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveActionPerformed
        if ( ! this._check()) {
            return;
        }

        Worker current = workers.get(cbxSalaryWorkers.getSelectedIndex() - 1);

        workerRecord.setWorkerID(current.getId());
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
        String message = "";

        if (cbxSalaryWorkers.getSelectedIndex() == 0) {
            message = "Sila Pilih Pekerja";
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


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnEnd;
    private javax.swing.JButton btnSave;
    private javax.swing.JComboBox cbxSalaryWorkers;
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
    // End of variables declaration//GEN-END:variables

}