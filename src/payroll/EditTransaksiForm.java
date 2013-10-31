/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package payroll;

import com.toedter.calendar.JDateChooser;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import javax.swing.JOptionPane;
import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;
import payroll.libraries.Common;
import payroll.libraries.Database;
import payroll.model.Customer;
import payroll.model.Transaction;
import payroll.model.Worker;

/**
 *
 * @author ho
 */
public class EditTransaksiForm extends javax.swing.JDialog {

    private ArrayList<Customer> customers = new ArrayList<Customer>();
    private ArrayList<Worker> workers = new ArrayList<Worker>();

    /**
     * Creates new form EditTransaksiForm
     */
    public EditTransaksiForm(Main parent, boolean modal, Integer transaksiID)
            throws SQLException, ParseException {

        super(parent, modal);
        initComponents();

        customers = parent.customers;
        workers = parent.workers;

        String query = "SELECT * FROM transactions WHERE id = " + transaksiID;

        ResultSet rs = Database.instance().execute(query);
        while (rs.next()) {
            this.transaksiRecordID.setText(transaksiID.toString());

            String transaksiDate = rs.getString("date");
            Date date = new SimpleDateFormat("yyyy-MM-dd",
                    Locale.ENGLISH).parse(transaksiDate);
            this.txtTransactionDate.setDate(date);

            Integer customerID = rs.getInt("customer_id");
            for (Customer customer : customers) {
                if (customer.getId() == customerID) {
                    this.CustomerDetails.setText(customer.getCode() + " - "
                            + customer.getName());
                }
            }

            String weightTxt = rs.getString("weight");
            this.txtTransactionWeight.setText(weightTxt);

            String pricePerTon = rs.getString("price_per_ton");
            this.txtTransactionPricePerTon.setText(pricePerTon);

            String wagesTxt = rs.getString("wages");
            this.txtTransactionWages.setText(wagesTxt);

            String transCalc = rs.getString("kiraan_asing");
            this.txtTransactionCalculate.setText(transCalc);

            String desc = rs.getString("description");
            this.txtTransactionDescription.setText(desc);

            String workerID_list = rs.getString("normalized_worker_id");
            List<String> myList = new ArrayList<String>(Arrays.asList(
                    workerID_list.split(",")));
            this.workerCount.setText(String.valueOf(myList.size()));

            String code_list = null;
            for (String item : myList) {
                String code = Worker.getCode(item);
                if (code_list == null) {
                    code_list = code;
                } else {
                    code_list = code_list + ", " + code;
                }
            }
            this.workerList.setText(code_list);

            double kiraanAsing = 0.0;
            if (Common.isDouble(this.txtTransactionCalculate.getText())) {
                kiraanAsing = Double.parseDouble(this.txtTransactionCalculate.getText());
            }

            double weight = Double.parseDouble(this.txtTransactionWeight.getText()),
                    price_per_ton = 0.0;

            if (!this.txtTransactionPricePerTon.getText().isEmpty()
                    && Common.isDouble(this.txtTransactionPricePerTon.getText())) {
                price_per_ton = Double.parseDouble(this.txtTransactionPricePerTon.getText());
            }

            double total = ((weight / 1000) * price_per_ton) + (kiraanAsing * price_per_ton);

            this.txtTransactionTotalReceived.setText(Common.currency(total));

            if (this.txtTransactionWages.getText().isEmpty()
                    || !Common.isDouble(this.txtTransactionWages.getText())) {
                return;
            }

            double wages = 0.0, wages_tax = 0.0, salary = 0.0, salary_tax;

            if (!this.txtTransactionWages.getText().isEmpty()
                    && Common.isDouble(this.txtTransactionWages.getText())) {
                wages = Double.parseDouble(this.txtTransactionWages.getText());
            }

            salary = ((weight / 1000) * wages) + (kiraanAsing * wages);
            salary_tax = ((weight / 1000) * wages_tax) + (kiraanAsing * wages_tax);
            this.txtTransactionSalary.setText(Common.currency(salary));

            this.txtTransactionBalance.setText(Common.currency(total - salary));

            int worker_count = Integer.parseInt(this.workerCount.getText());

            worker_count = worker_count > 0 ? worker_count : 1;
            this.txtTransactionPayPerPerson.setText(Common.currency(salary / worker_count));

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

        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        transaksiRecordID = new javax.swing.JTextField();
        txtTransactionSalary = new javax.swing.JTextField();
        txtTransactionDescription = new javax.swing.JTextField();
        txtTransactionPayPerPerson = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        btnSave = new javax.swing.JButton();
        cancelEditTransaksi = new javax.swing.JButton();
        txtTransactionDate = new com.toedter.calendar.JDateChooser();
        txtTransactionWeight = new javax.swing.JTextField();
        txtTransactionPricePerTon = new javax.swing.JTextField();
        txtTransactionBalance = new javax.swing.JTextField();
        txtTransactionTotalReceived = new javax.swing.JTextField();
        txtTransactionCalculate = new javax.swing.JTextField();
        txtTransactionWages = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        CustomerDetails = new javax.swing.JTextField();
        jLabel13 = new javax.swing.JLabel();
        workerCount = new javax.swing.JTextField();
        workerList = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Ubah Transaksi");

        jLabel11.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel11.setText("Kiraan Asing");

        jLabel12.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel12.setText("Upah Perseorangan");

        jLabel5.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel5.setText("Berat KG");

        jLabel6.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel6.setText("Harga Diterima Seton");

        jLabel1.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel1.setText("Rekod ID");

        jLabel3.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel3.setText("Kod & Nama Pelanggan");

        jLabel9.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel9.setText("Jumlah Gaji");

        jLabel10.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel10.setText("Jumlah Baki");

        jLabel7.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel7.setText("Jumlah Diterima");

        jLabel8.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel8.setText("Upah Kerja");

        transaksiRecordID.setEditable(false);
        transaksiRecordID.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N

        txtTransactionSalary.setEditable(false);
        txtTransactionSalary.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        txtTransactionSalary.setFocusable(false);

        txtTransactionDescription.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N

        txtTransactionPayPerPerson.setEditable(false);
        txtTransactionPayPerPerson.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        txtTransactionPayPerPerson.setFocusable(false);

        jLabel2.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel2.setText("Tarikh");

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        btnSave.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        btnSave.setText("Ubah");
        btnSave.setActionCommand("saveUbahTransaksi");
        btnSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveActionPerformed(evt);
            }
        });

        cancelEditTransaksi.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        cancelEditTransaksi.setText("Batal");
        cancelEditTransaksi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelEditTransaksiActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btnSave, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 172, Short.MAX_VALUE)
                .addComponent(cancelEditTransaksi, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnSave)
                    .addComponent(cancelEditTransaksi))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        txtTransactionDate.setDate(Calendar.getInstance().getTime());
        txtTransactionDate.setDateFormatString("dd/MM/yyyy");
        txtTransactionDate.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        txtTransactionDate.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                txtTransactionDatedateChange(evt);
            }
        });

        txtTransactionWeight.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        txtTransactionWeight.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtTransactionWeightActionPerformed(evt);
            }
        });
        txtTransactionWeight.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtTransactionWeightcalculateTransaction(evt);
            }
        });

        txtTransactionPricePerTon.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        txtTransactionPricePerTon.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtTransactionPricePerTonActionPerformed(evt);
            }
        });
        txtTransactionPricePerTon.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtTransactionPricePerToncalculateTransaction(evt);
            }
        });

        txtTransactionBalance.setEditable(false);
        txtTransactionBalance.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        txtTransactionBalance.setFocusable(false);

        txtTransactionTotalReceived.setEditable(false);
        txtTransactionTotalReceived.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        txtTransactionTotalReceived.setFocusable(false);

        txtTransactionCalculate.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        txtTransactionCalculate.setText("0");
        txtTransactionCalculate.setToolTipText("");
        txtTransactionCalculate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtTransactionCalculateActionPerformed(evt);
            }
        });
        txtTransactionCalculate.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtTransactionCalculatecalculateTransaction(evt);
            }
        });

        txtTransactionWages.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        txtTransactionWages.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtTransactionWagesActionPerformed(evt);
            }
        });
        txtTransactionWages.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtTransactionWagescalculateTransaction(evt);
            }
        });

        jLabel4.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel4.setText("Keterangan");

        CustomerDetails.setEditable(false);
        CustomerDetails.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N

        jLabel13.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel13.setText("Workers - ");

        workerCount.setEditable(false);
        workerCount.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N

        workerList.setEditable(false);
        workerList.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addComponent(jLabel6, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jLabel5, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addComponent(jLabel1))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(txtTransactionWeight, javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txtTransactionDate, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(transaksiRecordID, javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txtTransactionPricePerTon, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 238, Short.MAX_VALUE)
                                    .addComponent(CustomerDetails, javax.swing.GroupLayout.Alignment.LEADING)))
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel8, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel7, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txtTransactionTotalReceived)
                                    .addComponent(txtTransactionWages))))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                        .addComponent(jLabel12, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 150, Short.MAX_VALUE)
                                        .addComponent(jLabel11, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(jLabel10, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                    .addComponent(jLabel9, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 152, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(txtTransactionSalary)
                                    .addComponent(txtTransactionCalculate, javax.swing.GroupLayout.DEFAULT_SIZE, 238, Short.MAX_VALUE)
                                    .addComponent(txtTransactionBalance, javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txtTransactionPayPerPerson, javax.swing.GroupLayout.Alignment.LEADING)))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel13)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(workerCount, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(workerList)
                            .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtTransactionDescription, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(transaksiRecordID, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtTransactionDate, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(CustomerDetails, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtTransactionWeight, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtTransactionPricePerTon, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtTransactionTotalReceived, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtTransactionWages, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtTransactionSalary, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtTransactionBalance, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtTransactionCalculate, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtTransactionPayPerPerson, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel13)
                    .addComponent(workerCount, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(workerList, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtTransactionDescription, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void cancelEditTransaksiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelEditTransaksiActionPerformed
        this.dispose();
    }//GEN-LAST:event_cancelEditTransaksiActionPerformed

    private void txtTransactionDatedateChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_txtTransactionDatedateChange
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
    }//GEN-LAST:event_txtTransactionDatedateChange

    private void txtTransactionWeightActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtTransactionWeightActionPerformed
        if (txtTransactionWeight.getText().isEmpty()) {
            txtTransactionWeight.setText("0.00");
        }
        calculateTransaction(null);
    }//GEN-LAST:event_txtTransactionWeightActionPerformed

    private void calculateTransaction(java.awt.event.KeyEvent evt) {
        if (txtTransactionPricePerTon.getText().isEmpty()
                || txtTransactionWeight.getText().isEmpty()
                || !Common.isDouble(txtTransactionPricePerTon.getText())
                || !Common.isDouble(txtTransactionWeight.getText())) {
            return;
        }

        double kiraanAsing = 0.0;
        if (Common.isDouble(txtTransactionCalculate.getText())) {
            kiraanAsing = Double.parseDouble(txtTransactionCalculate.getText());
        }

        double weight = Double.parseDouble(txtTransactionWeight.getText()),
                price_per_ton = 0.0;

        if (!txtTransactionPricePerTon.getText().isEmpty()
                && Common.isDouble(txtTransactionPricePerTon.getText())) {
            price_per_ton = Double.parseDouble(txtTransactionPricePerTon.getText());
        }

        double total = ((weight / 1000) * price_per_ton) + (kiraanAsing * price_per_ton);

        txtTransactionTotalReceived.setText(Common.currency(total));

        if (txtTransactionWages.getText().isEmpty()
                || !Common.isDouble(txtTransactionWages.getText())) {
            return;
        }

        double wages = 0.0, wages_tax = 0.0, salary = 0.0, salary_tax;

        if (!txtTransactionWages.getText().isEmpty()
                && Common.isDouble(txtTransactionWages.getText())) {
            wages = Double.parseDouble(txtTransactionWages.getText());
        }

        salary = ((weight / 1000) * wages) + (kiraanAsing * wages);
        salary_tax = ((weight / 1000) * wages_tax) + (kiraanAsing * wages_tax);
        txtTransactionSalary.setText(Common.currency(salary));

        txtTransactionBalance.setText(Common.currency(total - salary));

        int worker_count = Integer.parseInt(this.workerCount.getText());
//        int row = tblTransactionInvolvedWorkers.getRowCount();
//        for (int i = 0; i < row; i++) {
//            if (Boolean.parseBoolean(tblTransactionInvolvedWorkers.getValueAt(i, 0).toString()) == true) {
//                worker_count++;
//            }
//        }

        worker_count = worker_count > 0 ? worker_count : 1;
        txtTransactionPayPerPerson.setText(Common.currency(salary / worker_count));
    }

    private void txtTransactionWeightcalculateTransaction(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtTransactionWeightcalculateTransaction
        if (txtTransactionPricePerTon.getText().isEmpty()
                || txtTransactionWeight.getText().isEmpty()
                || !Common.isDouble(txtTransactionPricePerTon.getText())
                || !Common.isDouble(txtTransactionWeight.getText())) {
            return;
        }

        double kiraanAsing = 0.0;
        if (Common.isDouble(txtTransactionCalculate.getText())) {
            kiraanAsing = Double.parseDouble(txtTransactionCalculate.getText());
        }

        double weight = Double.parseDouble(txtTransactionWeight.getText()),
                price_per_ton = 0.0;

        if (!txtTransactionPricePerTon.getText().isEmpty()
                && Common.isDouble(txtTransactionPricePerTon.getText())) {
            price_per_ton = Double.parseDouble(txtTransactionPricePerTon.getText());
        }

        double total = ((weight / 1000) * price_per_ton) + (kiraanAsing * price_per_ton);

        txtTransactionTotalReceived.setText(Common.currency(total));

        if (txtTransactionWages.getText().isEmpty()
                || !Common.isDouble(txtTransactionWages.getText())) {
            return;
        }

        double wages = 0.0, wages_tax = 0.0, salary = 0.0, salary_tax;

        if (!txtTransactionWages.getText().isEmpty()
                && Common.isDouble(txtTransactionWages.getText())) {
            wages = Double.parseDouble(txtTransactionWages.getText());
        }

        salary = ((weight / 1000) * wages) + (kiraanAsing * wages);
        salary_tax = ((weight / 1000) * wages_tax) + (kiraanAsing * wages_tax);
        txtTransactionSalary.setText(Common.currency(salary));

        txtTransactionBalance.setText(Common.currency(total - salary));

        int worker_count = Integer.parseInt(this.workerCount.getText());
//        int row = tblTransactionInvolvedWorkers.getRowCount();
//        for (int i = 0; i < row; i++) {
//            if (Boolean.parseBoolean(tblTransactionInvolvedWorkers.getValueAt(i, 0).toString()) == true) {
//                worker_count++;
//            }
//        }

        worker_count = worker_count > 0 ? worker_count : 1;
        txtTransactionPayPerPerson.setText(Common.currency(salary / worker_count));
    }//GEN-LAST:event_txtTransactionWeightcalculateTransaction

    private void txtTransactionPricePerTonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtTransactionPricePerTonActionPerformed
        if (txtTransactionPricePerTon.getText().isEmpty()) {
            txtTransactionPricePerTon.setText("0.00");
        }
        calculateTransaction(null);
    }//GEN-LAST:event_txtTransactionPricePerTonActionPerformed

    private void txtTransactionPricePerToncalculateTransaction(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtTransactionPricePerToncalculateTransaction
        if (txtTransactionPricePerTon.getText().isEmpty()
                || txtTransactionWeight.getText().isEmpty()
                || !Common.isDouble(txtTransactionPricePerTon.getText())
                || !Common.isDouble(txtTransactionWeight.getText())) {
            return;
        }

        double kiraanAsing = 0.0;
        if (Common.isDouble(txtTransactionCalculate.getText())) {
            kiraanAsing = Double.parseDouble(txtTransactionCalculate.getText());
        }

        double weight = Double.parseDouble(txtTransactionWeight.getText()),
                price_per_ton = 0.0;

        if (!txtTransactionPricePerTon.getText().isEmpty()
                && Common.isDouble(txtTransactionPricePerTon.getText())) {
            price_per_ton = Double.parseDouble(txtTransactionPricePerTon.getText());
        }

        double total = ((weight / 1000) * price_per_ton) + (kiraanAsing * price_per_ton);

        txtTransactionTotalReceived.setText(Common.currency(total));

        if (txtTransactionWages.getText().isEmpty()
                || !Common.isDouble(txtTransactionWages.getText())) {
            return;
        }

        double wages = 0.0, wages_tax = 0.0, salary = 0.0, salary_tax;

        if (!txtTransactionWages.getText().isEmpty()
                && Common.isDouble(txtTransactionWages.getText())) {
            wages = Double.parseDouble(txtTransactionWages.getText());
        }

        salary = ((weight / 1000) * wages) + (kiraanAsing * wages);
        salary_tax = ((weight / 1000) * wages_tax) + (kiraanAsing * wages_tax);
        txtTransactionSalary.setText(Common.currency(salary));

        txtTransactionBalance.setText(Common.currency(total - salary));

        int worker_count = Integer.parseInt(this.workerCount.getText());
//        int row = tblTransactionInvolvedWorkers.getRowCount();
//        for (int i = 0; i < row; i++) {
//            if (Boolean.parseBoolean(tblTransactionInvolvedWorkers.getValueAt(i,
//                    0).toString()) == true) {
//                worker_count++;
//            }
//        }

        worker_count = worker_count > 0 ? worker_count : 1;
        txtTransactionPayPerPerson.setText(Common.currency(salary / worker_count));
    }//GEN-LAST:event_txtTransactionPricePerToncalculateTransaction

    private void txtTransactionCalculateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtTransactionCalculateActionPerformed
        if (txtTransactionCalculate.getText().isEmpty()) {
            txtTransactionCalculate.setText("0.00");
        }
        calculateTransaction(null);
    }//GEN-LAST:event_txtTransactionCalculateActionPerformed

    private void txtTransactionCalculatecalculateTransaction(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtTransactionCalculatecalculateTransaction
        if (txtTransactionPricePerTon.getText().isEmpty()
                || txtTransactionWeight.getText().isEmpty()
                || !Common.isDouble(txtTransactionPricePerTon.getText())
                || !Common.isDouble(txtTransactionWeight.getText())) {
            return;
        }

        double kiraanAsing = 0.0;
        if (Common.isDouble(txtTransactionCalculate.getText())) {
            kiraanAsing = Double.parseDouble(txtTransactionCalculate.getText());
        }

        double weight = Double.parseDouble(txtTransactionWeight.getText()),
                price_per_ton = 0.0;

        if (!txtTransactionPricePerTon.getText().isEmpty()
                && Common.isDouble(txtTransactionPricePerTon.getText())) {
            price_per_ton = Double.parseDouble(txtTransactionPricePerTon.getText());
        }

        double total = ((weight / 1000) * price_per_ton) + (kiraanAsing * price_per_ton);

        txtTransactionTotalReceived.setText(Common.currency(total));

        if (txtTransactionWages.getText().isEmpty()
                || !Common.isDouble(txtTransactionWages.getText())) {
            return;
        }

        double wages = 0.0, wages_tax = 0.0, salary = 0.0, salary_tax;

        if (!txtTransactionWages.getText().isEmpty()
                && Common.isDouble(txtTransactionWages.getText())) {
            wages = Double.parseDouble(txtTransactionWages.getText());
        }

        salary = ((weight / 1000) * wages) + (kiraanAsing * wages);
        salary_tax = ((weight / 1000) * wages_tax) + (kiraanAsing * wages_tax);
        txtTransactionSalary.setText(Common.currency(salary));

        txtTransactionBalance.setText(Common.currency(total - salary));

        int worker_count = Integer.parseInt(this.workerCount.getText());
//        int row = tblTransactionInvolvedWorkers.getRowCount();
//        for (int i = 0; i < row; i++) {
//            if (Boolean.parseBoolean(tblTransactionInvolvedWorkers.getValueAt(i,
//                    0).toString()) == true) {
//                worker_count++;
//            }
//        }

        worker_count = worker_count > 0 ? worker_count : 1;
        txtTransactionPayPerPerson.setText(Common.currency(salary / worker_count));
    }//GEN-LAST:event_txtTransactionCalculatecalculateTransaction

    private void txtTransactionWagesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtTransactionWagesActionPerformed
        if (txtTransactionWages.getText().isEmpty()) {
            txtTransactionWages.setText("0.00");
        }
        calculateTransaction(null);
    }//GEN-LAST:event_txtTransactionWagesActionPerformed

    private void txtTransactionWagescalculateTransaction(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtTransactionWagescalculateTransaction
        if (txtTransactionPricePerTon.getText().isEmpty()
                || txtTransactionWeight.getText().isEmpty()
                || !Common.isDouble(txtTransactionPricePerTon.getText())
                || !Common.isDouble(txtTransactionWeight.getText())) {
            return;
        }

        double kiraanAsing = 0.0;
        if (Common.isDouble(txtTransactionCalculate.getText())) {
            kiraanAsing = Double.parseDouble(txtTransactionCalculate.getText());
        }

        double weight = Double.parseDouble(txtTransactionWeight.getText()),
                price_per_ton = 0.0;

        if (!txtTransactionPricePerTon.getText().isEmpty()
                && Common.isDouble(txtTransactionPricePerTon.getText())) {
            price_per_ton = Double.parseDouble(txtTransactionPricePerTon.getText());
        }

        double total = ((weight / 1000) * price_per_ton) + (kiraanAsing * price_per_ton);

        txtTransactionTotalReceived.setText(Common.currency(total));

        if (txtTransactionWages.getText().isEmpty()
                || !Common.isDouble(txtTransactionWages.getText())) {
            return;
        }

        double wages = 0.0, wages_tax = 0.0, salary = 0.0, salary_tax;

        if (!txtTransactionWages.getText().isEmpty()
                && Common.isDouble(txtTransactionWages.getText())) {
            wages = Double.parseDouble(txtTransactionWages.getText());
        }

        salary = ((weight / 1000) * wages) + (kiraanAsing * wages);
        salary_tax = ((weight / 1000) * wages_tax) + (kiraanAsing * wages_tax);
        txtTransactionSalary.setText(Common.currency(salary));

        txtTransactionBalance.setText(Common.currency(total - salary));

        int worker_count = Integer.parseInt(this.workerCount.getText());

        worker_count = worker_count > 0 ? worker_count : 1;
        txtTransactionPayPerPerson.setText(Common.currency(salary / worker_count));
    }//GEN-LAST:event_txtTransactionWagescalculateTransaction

    private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveActionPerformed
        if (!this._validate_transaction_form()) {
            return;
        }

//        transaction.setPricePerTon(Double.parseDouble(txtTransactionPricePerTon.getText()));
//        transaction.setWages(Double.parseDouble(txtTransactionWages.getText()));       
//        transaction.setKiraanAsing(Double.parseDouble(txtTransactionCalculate.getText()));

        Integer recordID = Integer.parseInt(this.transaksiRecordID.getText());

        String query = "UPDATE transactions SET"
                + " date = ?, weight = ?, price_per_ton = ?,"
                + " wages = ?, kiraan_asing = ?, description = ?"
                + " WHERE id = "
                + recordID;

        Date transaksiDate = new java.sql.Date(txtTransactionDate.getDate().getTime());
        String desc = this.txtTransactionDescription.getText();
        Double weight = Double.parseDouble(this.txtTransactionWeight.getText());
        Double pricePerTon = Double.parseDouble(this.txtTransactionPricePerTon.getText());
        Double wages = Double.parseDouble(this.txtTransactionWages.getText());
        Double kiraanAsing = Double.parseDouble(this.txtTransactionCalculate.getText());

        PreparedStatement ps = Database.instance().createPreparedStatement(query);
        try {
            ps.setString(1, transaksiDate.toString());
            ps.setDouble(2, weight);
            ps.setDouble(3, pricePerTon);
            ps.setDouble(4, wages);
            ps.setDouble(5, kiraanAsing);
            ps.setString(6, desc);
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }

        boolean update_workerRecord = true;
        if (Database.instance().update(ps)) {
            JOptionPane.showMessageDialog(null,
                    "Rekod Transaksi berjaya diubah.", "Berjaya!",
                    JOptionPane.INFORMATION_MESSAGE);
        } else {
            update_workerRecord = false;
            JOptionPane.showMessageDialog(null,
                    "Rekod Transaksi tidak dapat diubah.", "Kesilapan!",
                    JOptionPane.ERROR_MESSAGE);
        }

        if (update_workerRecord) {

            query = "UPDATE workerRecord SET"
                    + " date = ?, amount = ?"
                    + " WHERE transaction_id = "
                    + recordID;

            PreparedStatement ps2 = Database.instance().createPreparedStatement(query);

            Double payPerPerson = Double.parseDouble(this.txtTransactionPayPerPerson.getText());
            try {
                ps2.setString(1, transaksiDate.toString());
                ps2.setDouble(2, payPerPerson);
            } catch (SQLException ex) {
                System.err.println(ex.getMessage());
            }

            if (Database.instance().update(ps2)) {
                JOptionPane.showMessageDialog(null,
                        "Rekod Pekerja terlibat diubah.", "Berjaya!",
                        JOptionPane.INFORMATION_MESSAGE);
            } else {
                update_workerRecord = false;
                JOptionPane.showMessageDialog(null,
                        "Rekod Pekerja terlibat tidak dapat diubah.",
                        "Kesilapan!", JOptionPane.ERROR_MESSAGE);
            }
        }

        this.dispose();
    }//GEN-LAST:event_btnSaveActionPerformed

    private boolean _validate_transaction_form() {
        String message = "";

        if (txtTransactionPricePerTon.getText().isEmpty()) {
            txtTransactionPricePerTon.setText("0.00");
        }

        if (txtTransactionDate.getDate() == null) {
            message = "Kesilapan Tarikh";
        } else if (txtTransactionWeight.getText().isEmpty()) {
            message = "Sila isikan Berat";
        } else if (!Common.isDouble(txtTransactionWeight.getText())) {
            message = "Kesilapan Berat";
        } else if (txtTransactionPricePerTon.getText().isEmpty()) {
            message = "Sila isikan Harga";
        } else if (!Common.isDouble(txtTransactionPricePerTon.getText())) {
            message = "Kesilapan Harga Seton";
        } else if (txtTransactionWages.getText().isEmpty()) {
            message = "Sila isikan Upah Pekerja";
        } else if (!Common.isDouble(txtTransactionWages.getText())) {
            message = "Kesilapan Upah";
        } else if (!Common.isDouble(txtTransactionCalculate.getText())) {
            message = "Kesilapan Kiraan Asing";
        }

        if (message.isEmpty()) {
            return true;
        }

        JOptionPane.showMessageDialog(null, message, "Kesilapan!",
                JOptionPane.WARNING_MESSAGE);

        return false;
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField CustomerDetails;
    private javax.swing.JButton btnSave;
    private javax.swing.JButton cancelEditTransaksi;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JTextField transaksiRecordID;
    private javax.swing.JTextField txtTransactionBalance;
    private javax.swing.JTextField txtTransactionCalculate;
    private com.toedter.calendar.JDateChooser txtTransactionDate;
    private javax.swing.JTextField txtTransactionDescription;
    private javax.swing.JTextField txtTransactionPayPerPerson;
    private javax.swing.JTextField txtTransactionPricePerTon;
    private javax.swing.JTextField txtTransactionSalary;
    private javax.swing.JTextField txtTransactionTotalReceived;
    private javax.swing.JTextField txtTransactionWages;
    private javax.swing.JTextField txtTransactionWeight;
    private javax.swing.JTextField workerCount;
    private javax.swing.JTextField workerList;
    // End of variables declaration//GEN-END:variables
}
