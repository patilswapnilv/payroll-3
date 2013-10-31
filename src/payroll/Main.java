package payroll;

import com.toedter.calendar.JDateChooser;
import java.awt.Font;
import java.awt.print.PageFormat;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.standard.MediaPrintableArea;
import javax.swing.DefaultListModel;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.lobobrowser.html.UserAgentContext;
import org.lobobrowser.html.parser.DocumentBuilderImpl;
import org.lobobrowser.html.parser.InputSourceImpl;
import org.lobobrowser.html.test.SimpleHtmlRendererContext;
import org.lobobrowser.html.test.SimpleUserAgentContext;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;
import payroll.libraries.Common;
import payroll.libraries.Database;
import payroll.libraries.ExtensionFileFilter;
import payroll.model.Customer;
import payroll.model.ReportCalculation;
import payroll.model.ReportSalary;
import payroll.model.ReportSaving;
import payroll.model.Transaction;
import payroll.model.Worker;
import payroll.model.WorkerRecord;
import payroll.model.WorkerReport;

/**
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 *
 * Main2.java Created on Jul 17, 2012, 11:44:34 PM
 */
public class Main extends javax.swing.JFrame {

    private int _current_worker_id = 0;
    private int _current_client_id = 0;
    public Hashtable workers_hash = new Hashtable();
    public Hashtable customers_hash = new Hashtable();
    public ArrayList<Worker> workers = new ArrayList<Worker>();
    public ArrayList<Customer> customers = new ArrayList<Customer>();
    public ArrayList<Worker> workers_all = new ArrayList<Worker>();
    public ArrayList<Customer> customers_all = new ArrayList<Customer>();
    private ArrayList<Integer> loaded_loan_ids = new ArrayList<Integer>();
    private ArrayList<Integer> loaded_saving_ids = new ArrayList<Integer>();
    private ArrayList<Integer> loaded_payment_ids = new ArrayList<Integer>();
    private ArrayList<Integer> loaded_transaction_ids = new ArrayList<Integer>();
    private ArrayList<Integer> loaded_transaction_tax_ids = new ArrayList<Integer>();

    /**
     * Creates new form Main
     */
    public Main() {
        initComponents();

        try {
            this.setExtendedState(Main.MAXIMIZED_BOTH);
            this.setAlwaysOnTop(true);
            this.toFront();
            this.requestFocus();
            this.setAlwaysOnTop(false);
        } catch (Exception ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            System.err.println(ex.getMessage());
        }

        this.load_workers();
        this.load_customers();

        Calendar dateFrom = Calendar.getInstance();
        Calendar dateTo = Calendar.getInstance();

        dateFrom.set(Calendar.DAY_OF_MONTH, dateFrom.getActualMinimum(Calendar.DAY_OF_MONTH));
        dateTo.set(Calendar.DAY_OF_MONTH, dateTo.getActualMaximum(Calendar.DAY_OF_MONTH));

        txtTransactionListFrom.setDate(dateFrom.getTime());
        txtTransactionListTo.setDate(dateTo.getTime());
        txtTransactionListFrom1.setDate(dateFrom.getTime());
        txtTransactionListTo1.setDate(dateTo.getTime());
        txtSavingDateFrom.setDate(dateFrom.getTime());
        txtSavingDateTo.setDate(dateTo.getTime());
        txtMonthlyReportDateFrom.setDate(dateFrom.getTime());
        txtMonthlyReportDateTo.setDate(dateTo.getTime());
        txtMonthlyReportDateFrom1.setDate(dateFrom.getTime());
        txtMonthlyReportDateTo1.setDate(dateTo.getTime());
        txtLoanDateFrom.setDate(dateFrom.getTime());
        txtLoanDateTo.setDate(dateTo.getTime());
        txtSavingReportDateFrom.setDate(dateFrom.getTime());
        txtSavingReportDateTo.setDate(dateTo.getTime());

        tblLoan.getTableHeader().setFont(new Font("Arial", Font.PLAIN, 14));
        tblPay.getTableHeader().setFont(new Font("Arial", Font.PLAIN, 14));
        tblSaving.getTableHeader().setFont(new Font("Arial", Font.PLAIN, 14));
        tblTransactionList.getTableHeader().setFont(new Font("Arial", Font.PLAIN, 14));
        tblTransactionList1.getTableHeader().setFont(new Font("Arial", Font.PLAIN, 14));

        new Login(this, true).setVisible(true);
    }

    public void load_workers() {
        workers.clear();
        workers_hash.clear();

        String query = "SELECT * FROM worker WHERE is_active = 1 ORDER BY code";
        ResultSet rs = Database.instance().execute(query);

        try {
            while (rs.next()) {
                Worker worker = new Worker(rs.getInt("worker_id"), rs.getString("code"), rs.getString("name"), Common.convertStringToDate(rs.getString("start_date")), Common.convertStringToDate(rs.getString("end_date")), rs.getBoolean("is_active"));
                workers.add(worker);
                workers_hash.put(worker.getId(), worker);
            }
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }

        cbxPaymentWorkers.removeAllItems();
        cbxReportWorkers.removeAllItems();
        cbxSavingWorkers.removeAllItems();
        cbxLoanWorkers.removeAllItems();

        cbxPaymentWorkers.addItem(new String());
        cbxReportWorkers.addItem(new String());
        cbxSavingWorkers.addItem(new String());
        cbxLoanWorkers.addItem(new String());

        for (Worker worker : workers) {
            cbxPaymentWorkers.addItem(new String(worker.getCode() + " - " + worker.getName()));
            cbxReportWorkers.addItem(new String(worker.getCode() + " - " + worker.getName()));
            cbxSavingWorkers.addItem(new String(worker.getCode() + " - " + worker.getName()));
            cbxLoanWorkers.addItem(new String(worker.getCode() + " - " + worker.getName()));
        }

        this.load_workers_all();

    }

    public void load_customers() {
        customers.clear();
        customers_hash.clear();

        String query = "SELECT * FROM customer WHERE is_active = 1 ORDER BY code";
        ResultSet rs = Database.instance().execute(query);

        try {
            while (rs.next()) {
                Customer customer = new Customer(rs.getInt("customer_id"), rs.getString("code"), rs.getString("name"), rs.getBoolean("is_active"));
                customers.add(customer);
                customers_hash.put(customer.getId(), customer);
            }
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }

        cbxTransactionClients.removeAllItems();
        cbxTransactionListClients.removeAllItems();
        cbxTransactionListClients1.removeAllItems();

        cbxTransactionClients.addItem(new String());
        cbxTransactionListClients.addItem(new String());
        cbxTransactionListClients1.addItem(new String());

        for (Customer customer : customers) {
            cbxTransactionClients.addItem(new String(customer.getCode() + " - " + customer.getName()));
            cbxTransactionListClients.addItem(new String(customer.getCode() + " - " + customer.getName()));
            cbxTransactionListClients1.addItem(new String(customer.getCode() + " - " + customer.getName()));
        }

        this.load_customers_all();
    }

    public void load_customers_all() {
        customers_all.clear();
        String query = "SELECT * FROM customer ORDER BY code";
        ResultSet rs = Database.instance().execute(query);

        try {
            while (rs.next()) {
                customers_all.add(new Customer(rs.getInt("customer_id"), rs.getString("code"), rs.getString("name"), rs.getBoolean("is_active")));
            }
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }

        cbxClients.removeAllItems();
        cbxClients.addItem(new String());

        for (Customer customer : customers_all) {
            cbxClients.addItem(new String(customer.getCode() + " - " + customer.getName()));
        }
    }

    public void load_workers_all() {
        workers_all.clear();

        String query = "SELECT * FROM worker ORDER BY code";
        ResultSet rs = Database.instance().execute(query);

        try {
            while (rs.next()) {
                workers_all.add(new Worker(rs.getInt("worker_id"), rs.getString("code"), rs.getString("name"), Common.convertStringToDate(rs.getString("start_date")), Common.convertStringToDate(rs.getString("end_date")), rs.getBoolean("is_active")));
            }
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }

        cbxWorkers.removeAllItems();

        cbxWorkers.addItem(new String());

        for (Worker worker : workers_all) {
            cbxWorkers.addItem(new String(worker.getCode() + " - " + worker.getName()));
        }
    }

    private void _prepare_transaction_tab() {
        DefaultTableModel involveWorkerTableModel = (DefaultTableModel) tblTransactionInvolvedWorkers.getModel();
        DefaultListModel loanWorkerModel = new DefaultListModel();
        listTransactionLoanWorkers.setModel(loanWorkerModel);
        int row = tblTransactionInvolvedWorkers.getRowCount();

        TableModelListener tableModelListener = new TableModelListener() {
            public void tableChanged(TableModelEvent e) {
                calculateTransaction(null);
            }
        };

        tblTransactionInvolvedWorkers.getModel().addTableModelListener(tableModelListener);

        listTransactionLoanWorkers.removeAll();

        for (int i = 0; i < row; i++) {
            involveWorkerTableModel.removeRow(0);
        }

        for (Worker worker : workers) {
            Object[] workerObjects = new Object[]{
                new Boolean(false),
                new String(worker.getCode()),
                new String(worker.getName())
            };
            involveWorkerTableModel.addRow(workerObjects);
            loanWorkerModel.addElement(new String(worker.getCode() + " | " + worker.getName()));
        }
        listTransactionLoanWorkers.setFixedCellHeight(25);
        tblTransactionInvolvedWorkers.setRowHeight(25);
    }

    private void _prepare_monthly_report_tab() {
        DefaultTableModel workersTableModel = (DefaultTableModel) tblMonthlyReportWorkers.getModel();

        int row = tblMonthlyReportWorkers.getRowCount();

        for (int i = 0; i < row; i++) {
            workersTableModel.removeRow(0);
        }

        for (Worker worker : workers) {
            Object[] workerObjects = new Object[]{
                new Boolean(false),
                new String(worker.getCode()),
                new String(worker.getName())
            };

            workersTableModel.addRow(workerObjects);
        }

        tblMonthlyReportWorkers.setRowHeight(25);
    }

    private void _prepare_monthly_report_tab_tax() {
        DefaultTableModel workersTableModel = (DefaultTableModel) tblMonthlyReportWorkers1.getModel();

        int row = tblMonthlyReportWorkers1.getRowCount();

        for (int i = 0; i < row; i++) {
            workersTableModel.removeRow(0);
        }

        for (Worker worker : workers) {
            Object[] workerObjects = new Object[]{
                new Boolean(false),
                new String(worker.getCode()),
                new String(worker.getName())
            };

            workersTableModel.addRow(workerObjects);
        }

        tblMonthlyReportWorkers1.setRowHeight(25);
    }

    private void _prepare_saving_report_tab() {
        DefaultTableModel workersTableModel = (DefaultTableModel) tblSavingReportWorkers.getModel();

        int row = tblSavingReportWorkers.getRowCount();

        for (int i = 0; i < row; i++) {
            workersTableModel.removeRow(0);
        }

        for (Worker worker : workers) {
            Object[] workerObjects = new Object[]{
                new Boolean(false),
                new String(worker.getCode()),
                new String(worker.getName())
            };

            workersTableModel.addRow(workerObjects);
        }

        tblSavingReportWorkers.setRowHeight(25);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTabbedPane3 = new javax.swing.JTabbedPane();
        btnGroupMonthlyReport = new javax.swing.ButtonGroup();
        btnGroupWorkerReportType = new javax.swing.ButtonGroup();
        groupTransactionType = new javax.swing.ButtonGroup();
        buttonGroupMonthlyReportTax = new javax.swing.ButtonGroup();
        btnGroupSavingReport = new javax.swing.ButtonGroup();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel2 = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();
        jButton6 = new javax.swing.JButton();
        jButton7 = new javax.swing.JButton();
        jButton8 = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jPanel10 = new javax.swing.JPanel();
        btnRecord = new javax.swing.JButton();
        txtTransactionNew = new javax.swing.JButton();
        txtTransactionEnd = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        listTransactionLoanWorkers = new javax.swing.JList();
        btnTransactionNewLoan = new javax.swing.JButton();
        jLabel36 = new javax.swing.JLabel();
        jLabel54 = new javax.swing.JLabel();
        jPanel22 = new javax.swing.JPanel();
        cboxTransactionAllWorkers = new javax.swing.JCheckBox();
        jScrollPane3 = new javax.swing.JScrollPane();
        tblTransactionInvolvedWorkers = new javax.swing.JTable();
        jLabel29 = new javax.swing.JLabel();
        jPanel29 = new javax.swing.JPanel();
        txtTransactionDate = new com.toedter.calendar.JDateChooser();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        cbxTransactionClients = new javax.swing.JComboBox();
        jLabel5 = new javax.swing.JLabel();
        txtTransactionWeight = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        txtTransactionPricePerTon = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        txtTransactionTotalReceived = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        txtTransactionWages = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        txtTransactionSalary = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        txtTransactionBalance = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        txtTransactionCalculate = new javax.swing.JTextField();
        jLabel12 = new javax.swing.JLabel();
        txtTransactionPayPerPerson = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        txtTransactionDescription = new javax.swing.JTextField();
        txtTransactionSalary1 = new javax.swing.JTextField();
        txtTransactionBalance1 = new javax.swing.JTextField();
        txtTransactionPayPerPerson1 = new javax.swing.JTextField();
        txtTransactionWagesTax = new javax.swing.JTextField();
        jLabel41 = new javax.swing.JLabel();
        jPanel26 = new javax.swing.JPanel();
        jLabel37 = new javax.swing.JLabel();
        txtTransactionListFrom = new com.toedter.calendar.JDateChooser();
        txtTransactionListTo = new com.toedter.calendar.JDateChooser();
        jLabel39 = new javax.swing.JLabel();
        btnTransactionSave = new javax.swing.JButton();
        cbxTransactionListClients = new javax.swing.JComboBox();
        jPanel27 = new javax.swing.JPanel();
        btnTransactionListEnd = new javax.swing.JButton();
        btnTransactionListDelete = new javax.swing.JButton();
        ubahTransaksi = new javax.swing.JButton();
        jPanel28 = new javax.swing.JPanel();
        jScrollPane6 = new javax.swing.JScrollPane();
        tblTransactionList = new javax.swing.JTable();
        jLabel38 = new javax.swing.JLabel();
        jLabel40 = new javax.swing.JLabel();
        jPanel30 = new javax.swing.JPanel();
        jLabel42 = new javax.swing.JLabel();
        txtTransactionListFrom1 = new com.toedter.calendar.JDateChooser();
        txtTransactionListTo1 = new com.toedter.calendar.JDateChooser();
        jLabel43 = new javax.swing.JLabel();
        btnTransactionSave1 = new javax.swing.JButton();
        cbxTransactionListClients1 = new javax.swing.JComboBox();
        jPanel31 = new javax.swing.JPanel();
        btnTransactionListEnd1 = new javax.swing.JButton();
        btnTransactionListDelete1 = new javax.swing.JButton();
        jPanel32 = new javax.swing.JPanel();
        jScrollPane7 = new javax.swing.JScrollPane();
        tblTransactionList1 = new javax.swing.JTable();
        jLabel44 = new javax.swing.JLabel();
        jLabel45 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        txtSavingDateFrom = new com.toedter.calendar.JDateChooser();
        jLabel17 = new javax.swing.JLabel();
        txtSavingDateTo = new com.toedter.calendar.JDateChooser();
        txtSavingCurrentSaving = new javax.swing.JTextField();
        jPanel8 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblSaving = new javax.swing.JTable();
        jPanel9 = new javax.swing.JPanel();
        btnSavingEnd = new javax.swing.JButton();
        btnSavingCancel = new javax.swing.JButton();
        btnSavingNew = new javax.swing.JButton();
        EditSimpanan = new javax.swing.JButton();
        cbxSavingWorkers = new javax.swing.JComboBox();
        btnSavingSearch = new javax.swing.JButton();
        jPanel39 = new javax.swing.JPanel();
        jLabel48 = new javax.swing.JLabel();
        jLabel49 = new javax.swing.JLabel();
        cbxLoanWorkers = new javax.swing.JComboBox();
        jLabel50 = new javax.swing.JLabel();
        txtLoanDateFrom = new com.toedter.calendar.JDateChooser();
        jLabel51 = new javax.swing.JLabel();
        txtLoanDateTo = new com.toedter.calendar.JDateChooser();
        btnLoanSearch = new javax.swing.JButton();
        jPanel41 = new javax.swing.JPanel();
        btnLoanEnd = new javax.swing.JButton();
        btnLoanCancel = new javax.swing.JButton();
        editPinjaman = new javax.swing.JButton();
        jScrollPane9 = new javax.swing.JScrollPane();
        tblLoan = new javax.swing.JTable();
        jPanel11 = new javax.swing.JPanel();
        jLabel18 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        jPanel12 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblPay = new javax.swing.JTable();
        jPanel13 = new javax.swing.JPanel();
        btnPayEnd = new javax.swing.JButton();
        btnPayCancel = new javax.swing.JButton();
        btnPayNew = new javax.swing.JButton();
        cbxPaymentWorkers = new javax.swing.JComboBox();
        btnPaymentSearch = new javax.swing.JButton();
        txtPaymentMonth = new com.toedter.calendar.JMonthChooser();
        txtPaymentYear = new com.toedter.calendar.JYearChooser();
        txtPaymentMonthTo = new com.toedter.calendar.JMonthChooser();
        txtPaymentYearTo = new com.toedter.calendar.JYearChooser();
        jPanel5 = new javax.swing.JPanel();
        jTabbedPane4 = new javax.swing.JTabbedPane();
        jPanel19 = new javax.swing.JPanel();
        jLabel31 = new javax.swing.JLabel();
        jLabel32 = new javax.swing.JLabel();
        jLabel33 = new javax.swing.JLabel();
        rbtnWorkerMonthlyIncome = new javax.swing.JRadioButton();
        rbtnWorkerSaving = new javax.swing.JRadioButton();
        rbtnWorkerReportFull = new javax.swing.JRadioButton();
        jPanel25 = new javax.swing.JPanel();
        btnReportWorkerEnd = new javax.swing.JButton();
        btnReportWorkerPrint = new javax.swing.JButton();
        btnReportWorkerExport = new javax.swing.JButton();
        btnReportWorkerGenerate = new javax.swing.JButton();
        txtReportWorkerMonth = new com.toedter.calendar.JMonthChooser();
        txtReportWorkerYear = new com.toedter.calendar.JYearChooser();
        txtReportWorkerYearTo = new com.toedter.calendar.JYearChooser();
        txtReportWorkerMonthTo = new com.toedter.calendar.JMonthChooser();
        cbxReportWorkers = new javax.swing.JComboBox();
        jPanel38 = new javax.swing.JPanel();
        jPanel40 = new javax.swing.JPanel();
        rbtnSavingReportCurrentMonth = new javax.swing.JRadioButton();
        rbtnSavingReportPreviousMonth = new javax.swing.JRadioButton();
        rbtnMonhtlyReportDateRange2 = new javax.swing.JRadioButton();
        txtSavingReportDateFrom = new com.toedter.calendar.JDateChooser();
        jLabel52 = new javax.swing.JLabel();
        txtSavingReportDateTo = new com.toedter.calendar.JDateChooser();
        jPanel42 = new javax.swing.JPanel();
        jScrollPane10 = new javax.swing.JScrollPane();
        tblSavingReportWorkers = new javax.swing.JTable();
        cboxMonthlyReportAllWorkers2 = new javax.swing.JCheckBox();
        jLabel53 = new javax.swing.JLabel();
        jPanel43 = new javax.swing.JPanel();
        btnSavingReportEnd = new javax.swing.JButton();
        btnSavingReportPrint = new javax.swing.JButton();
        btnSavingReportExport = new javax.swing.JButton();
        btnSavingReportGenerate = new javax.swing.JButton();
        jPanel18 = new javax.swing.JPanel();
        jPanel20 = new javax.swing.JPanel();
        chkMonthlyReportDate = new javax.swing.JCheckBox();
        chkMonthlyReportClientName = new javax.swing.JCheckBox();
        chkMonthlyReportDescription = new javax.swing.JCheckBox();
        chkMonthlyReportWeight = new javax.swing.JCheckBox();
        chkMonthlyReportPricePerTon = new javax.swing.JCheckBox();
        chkMonthlyReportTotalReceived = new javax.swing.JCheckBox();
        chkMonthlyReportWages = new javax.swing.JCheckBox();
        chkMonthlyReportSalary = new javax.swing.JCheckBox();
        chkMonthlyReportBalance = new javax.swing.JCheckBox();
        chkMonthlyReportKiraanAsing = new javax.swing.JCheckBox();
        chkMonthlyReportSaving = new javax.swing.JCheckBox();
        chkMonthlyReportSalaryPayment = new javax.swing.JCheckBox();
        jPanel21 = new javax.swing.JPanel();
        rbtnMonhtlyReportCurrentMonth = new javax.swing.JRadioButton();
        rbtnMonthlyReportLastMonth = new javax.swing.JRadioButton();
        rbtnMonhtlyReportDateRange = new javax.swing.JRadioButton();
        txtMonthlyReportDateFrom = new com.toedter.calendar.JDateChooser();
        jLabel30 = new javax.swing.JLabel();
        txtMonthlyReportDateTo = new com.toedter.calendar.JDateChooser();
        jPanel23 = new javax.swing.JPanel();
        btnMonthlyReportEnd = new javax.swing.JButton();
        btnMonthlyReportPrint = new javax.swing.JButton();
        btnMonthlyReportExport = new javax.swing.JButton();
        btnMonthlyReportGenerate = new javax.swing.JButton();
        jPanel24 = new javax.swing.JPanel();
        jScrollPane5 = new javax.swing.JScrollPane();
        tblMonthlyReportWorkers = new javax.swing.JTable();
        cboxMonthlyReportAllWorkers = new javax.swing.JCheckBox();
        jLabel34 = new javax.swing.JLabel();
        jPanel33 = new javax.swing.JPanel();
        jPanel34 = new javax.swing.JPanel();
        chkMonthlyReportDate1 = new javax.swing.JCheckBox();
        chkMonthlyReportClientName1 = new javax.swing.JCheckBox();
        chkMonthlyReportDescription1 = new javax.swing.JCheckBox();
        chkMonthlyReportWeight1 = new javax.swing.JCheckBox();
        chkMonthlyReportPricePerTon1 = new javax.swing.JCheckBox();
        chkMonthlyReportTotalReceived1 = new javax.swing.JCheckBox();
        chkMonthlyReportWages1 = new javax.swing.JCheckBox();
        chkMonthlyReportSalary1 = new javax.swing.JCheckBox();
        chkMonthlyReportBalance1 = new javax.swing.JCheckBox();
        chkMonthlyReportKiraanAsing1 = new javax.swing.JCheckBox();
        chkMonthlyReportSaving1 = new javax.swing.JCheckBox();
        chkMonthlyReportSalaryPayment1 = new javax.swing.JCheckBox();
        jPanel35 = new javax.swing.JPanel();
        rbtnMonhtlyReportCurrentMonth1 = new javax.swing.JRadioButton();
        rbtnMonthlyReportLastMonth1 = new javax.swing.JRadioButton();
        rbtnMonhtlyReportDateRange1 = new javax.swing.JRadioButton();
        txtMonthlyReportDateFrom1 = new com.toedter.calendar.JDateChooser();
        jLabel46 = new javax.swing.JLabel();
        txtMonthlyReportDateTo1 = new com.toedter.calendar.JDateChooser();
        jPanel36 = new javax.swing.JPanel();
        btnMonthlyReportEnd1 = new javax.swing.JButton();
        btnMonthlyReportPrint1 = new javax.swing.JButton();
        btnMonthlyReportExport1 = new javax.swing.JButton();
        btnMonthlyReportGenerate1 = new javax.swing.JButton();
        jPanel37 = new javax.swing.JPanel();
        jScrollPane8 = new javax.swing.JScrollPane();
        tblMonthlyReportWorkers1 = new javax.swing.JTable();
        cboxMonthlyReportAllWorkers1 = new javax.swing.JCheckBox();
        jLabel47 = new javax.swing.JLabel();
        jPanel6 = new javax.swing.JPanel();
        jTabbedPane2 = new javax.swing.JTabbedPane();
        jPanel17 = new javax.swing.JPanel();
        jLabel21 = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        jLabel24 = new javax.swing.JLabel();
        jLabel25 = new javax.swing.JLabel();
        jLabel26 = new javax.swing.JLabel();
        txtProfileWorkerRegisterDay = new javax.swing.JTextField();
        txtProfileWorkerReturnDay = new javax.swing.JTextField();
        txtProfileWorkerRegisterMonth = new javax.swing.JTextField();
        txtProfileWorkerReturnMonth = new javax.swing.JTextField();
        txtProfileWorkerRegisterYear = new javax.swing.JTextField();
        txtProfileWorkerReturnYear = new javax.swing.JTextField();
        txtProfileWorkerCurrentSaving = new javax.swing.JTextField();
        txtProfileWorkerStatus = new javax.swing.JTextField();
        jPanel15 = new javax.swing.JPanel();
        btnProfileWorkerEnd = new javax.swing.JButton();
        btnProfileWorkerDelete = new javax.swing.JButton();
        btnProfileWorkerEdit = new javax.swing.JButton();
        btnProfileWorkerNew = new javax.swing.JButton();
        cbxWorkers = new javax.swing.JComboBox();
        jPanel14 = new javax.swing.JPanel();
        jPanel16 = new javax.swing.JPanel();
        btnProfileClientEnd = new javax.swing.JButton();
        btnProfileClientDelete = new javax.swing.JButton();
        btnProfileClientEdit = new javax.swing.JButton();
        btnProfileClientNew = new javax.swing.JButton();
        jLabel27 = new javax.swing.JLabel();
        txtProfileClientStatus = new javax.swing.JTextField();
        jLabel28 = new javax.swing.JLabel();
        cbxClients = new javax.swing.JComboBox();
        jPanel7 = new javax.swing.JPanel();
        jLabel35 = new javax.swing.JLabel();
        txtPassword = new javax.swing.JPasswordField();
        txtPasswordConfirm = new javax.swing.JPasswordField();
        btnPasswordChange = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setIconImage(new javax.swing.ImageIcon(getClass().getResource("/payroll/images/icon.png")).getImage());

        jTabbedPane1.setTabLayoutPolicy(javax.swing.JTabbedPane.SCROLL_TAB_LAYOUT);
        jTabbedPane1.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
        jTabbedPane1.setName("Payroll Software"); // NOI18N
        jTabbedPane1.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jTabbedPane1StateChanged(evt);
            }
        });

        jButton1.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jButton1.setText("Transaksi");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jButton2.setText("Senarai Transaksi");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jButton3.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jButton3.setText("Simpanan");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jButton4.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jButton4.setText("Bayaran Gaji");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        jButton5.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jButton5.setText("Laporan");
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        jButton6.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jButton6.setText("Profil");
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });

        jButton7.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jButton7.setText("Tutup");
        jButton7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton7ActionPerformed(evt);
            }
        });

        jButton8.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jButton8.setText("Kata Laluan");
        jButton8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton8ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton6, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton5, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton8, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton7, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(960, 960, 960))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(32, 32, 32)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jButton5, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jButton8, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jButton6, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton7, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(318, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("File", jPanel2);

        jPanel1.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel1.setText("Transaksi Baru Dan Pinjam");

        jPanel10.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        btnRecord.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        btnRecord.setText("Rekodkan");
        btnRecord.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRecordActionPerformed(evt);
            }
        });

        txtTransactionNew.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        txtTransactionNew.setText("Transaksi Baru");
        txtTransactionNew.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtTransactionNewActionPerformed(evt);
            }
        });

        txtTransactionEnd.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        txtTransactionEnd.setText("Tutup");
        txtTransactionEnd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtTransactionEndActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel10Layout.createSequentialGroup()
                .addGap(449, 449, 449)
                .addComponent(btnRecord, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(txtTransactionNew, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(txtTransactionEnd, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtTransactionNew, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnRecord, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtTransactionEnd, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        jPanel4.setFocusable(false);

        listTransactionLoanWorkers.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        listTransactionLoanWorkers.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        listTransactionLoanWorkers.setFocusable(false);
        jScrollPane4.setViewportView(listTransactionLoanWorkers);

        btnTransactionNewLoan.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        btnTransactionNewLoan.setText("Pinjaman Baru");
        btnTransactionNewLoan.setFocusable(false);
        btnTransactionNewLoan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTransactionNewLoanActionPerformed(evt);
            }
        });

        jLabel36.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        jLabel36.setFocusable(false);

        jLabel54.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jLabel54.setText("Pinjaman");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 326, Short.MAX_VALUE)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jLabel54, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel36, javax.swing.GroupLayout.DEFAULT_SIZE, 248, Short.MAX_VALUE))
                    .addComponent(btnTransactionNewLoan, javax.swing.GroupLayout.DEFAULT_SIZE, 326, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel36, javax.swing.GroupLayout.DEFAULT_SIZE, 20, Short.MAX_VALUE)
                    .addComponent(jLabel54, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 337, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnTransactionNewLoan)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        cboxTransactionAllWorkers.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        cboxTransactionAllWorkers.setText("Semua Pekerja");
        cboxTransactionAllWorkers.setFocusable(false);
        cboxTransactionAllWorkers.setIcon(new javax.swing.ImageIcon(getClass().getResource("/payroll/images/untick-icon.png"))); // NOI18N
        cboxTransactionAllWorkers.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/payroll/images/tick-icon.png"))); // NOI18N
        cboxTransactionAllWorkers.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboxTransactionAllWorkersActionPerformed(evt);
            }
        });

        tblTransactionInvolvedWorkers.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        tblTransactionInvolvedWorkers.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "", "", ""
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Boolean.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                true, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblTransactionInvolvedWorkers.setFocusable(false);
        tblTransactionInvolvedWorkers.setTableHeader(null);
        jScrollPane3.setViewportView(tblTransactionInvolvedWorkers);
        tblTransactionInvolvedWorkers.getColumnModel().getColumn(0).setResizable(false);
        tblTransactionInvolvedWorkers.getColumnModel().getColumn(0).setPreferredWidth(20);
        tblTransactionInvolvedWorkers.getColumnModel().getColumn(1).setResizable(false);
        tblTransactionInvolvedWorkers.getColumnModel().getColumn(1).setPreferredWidth(50);
        tblTransactionInvolvedWorkers.getColumnModel().getColumn(2).setResizable(false);

        jLabel29.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jLabel29.setText("Pekerja Terlibat");
        jLabel29.setFocusable(false);

        javax.swing.GroupLayout jPanel22Layout = new javax.swing.GroupLayout(jPanel22);
        jPanel22.setLayout(jPanel22Layout);
        jPanel22Layout.setHorizontalGroup(
            jPanel22Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel22Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel22Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel29, javax.swing.GroupLayout.DEFAULT_SIZE, 273, Short.MAX_VALUE)
                    .addComponent(cboxTransactionAllWorkers, javax.swing.GroupLayout.DEFAULT_SIZE, 273, Short.MAX_VALUE)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 273, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel22Layout.setVerticalGroup(
            jPanel22Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel22Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel29, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 320, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cboxTransactionAllWorkers)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        txtTransactionDate.setDate(Calendar.getInstance().getTime());
        txtTransactionDate.setDateFormatString("dd/MM/yyyy");
        txtTransactionDate.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        txtTransactionDate.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                dateChange(evt);
            }
        });

        jLabel2.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel2.setText("Tarikh");

        jLabel3.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel3.setText("Kod & Nama Pelanggan");

        cbxTransactionClients.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N

        jLabel5.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel5.setText("Berat KG");

        txtTransactionWeight.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        txtTransactionWeight.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtTransactionWeightActionPerformed(evt);
            }
        });
        txtTransactionWeight.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                calculateTransaction(evt);
            }
        });

        jLabel6.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel6.setText("Harga Diterima Seton");

        txtTransactionPricePerTon.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        txtTransactionPricePerTon.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtTransactionPricePerTonActionPerformed(evt);
            }
        });
        txtTransactionPricePerTon.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                calculateTransaction(evt);
            }
        });

        jLabel7.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel7.setText("Jumlah Diterima");

        txtTransactionTotalReceived.setEditable(false);
        txtTransactionTotalReceived.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        txtTransactionTotalReceived.setFocusable(false);

        jLabel8.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel8.setText("Upah Kerja");

        txtTransactionWages.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        txtTransactionWages.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtTransactionWagesActionPerformed(evt);
            }
        });
        txtTransactionWages.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                calculateTransaction(evt);
            }
        });

        jLabel9.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel9.setText("Jumlah Gaji");

        txtTransactionSalary.setEditable(false);
        txtTransactionSalary.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        txtTransactionSalary.setFocusable(false);

        jLabel10.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel10.setText("Jumlah Baki");

        txtTransactionBalance.setEditable(false);
        txtTransactionBalance.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        txtTransactionBalance.setFocusable(false);

        jLabel11.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel11.setText("Kiraan Asing");

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
                calculateTransaction(evt);
            }
        });

        jLabel12.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel12.setText("Upah Perseorangan");

        txtTransactionPayPerPerson.setEditable(false);
        txtTransactionPayPerPerson.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        txtTransactionPayPerPerson.setFocusable(false);

        jLabel4.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel4.setText("Keterangan");

        txtTransactionDescription.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N

        txtTransactionSalary1.setEditable(false);
        txtTransactionSalary1.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        txtTransactionSalary1.setFocusable(false);

        txtTransactionBalance1.setEditable(false);
        txtTransactionBalance1.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        txtTransactionBalance1.setFocusable(false);

        txtTransactionPayPerPerson1.setEditable(false);
        txtTransactionPayPerPerson1.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        txtTransactionPayPerPerson1.setFocusable(false);

        txtTransactionWagesTax.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        txtTransactionWagesTax.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtTransactionWagesTaxActionPerformed(evt);
            }
        });
        txtTransactionWagesTax.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                calculateTransaction(evt);
            }
        });

        jLabel41.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel41.setText("Upah Kerja (TAX)");

        javax.swing.GroupLayout jPanel29Layout = new javax.swing.GroupLayout(jPanel29);
        jPanel29.setLayout(jPanel29Layout);
        jPanel29Layout.setHorizontalGroup(
            jPanel29Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel29Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel29Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel29Layout.createSequentialGroup()
                        .addGroup(jPanel29Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel6, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel5, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel29Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtTransactionDate, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(cbxTransactionClients, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(txtTransactionWeight)
                            .addComponent(txtTransactionPricePerTon)))
                    .addGroup(jPanel29Layout.createSequentialGroup()
                        .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 230, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel29Layout.createSequentialGroup()
                        .addGroup(jPanel29Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel8, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel7, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel29Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtTransactionTotalReceived)
                            .addGroup(jPanel29Layout.createSequentialGroup()
                                .addComponent(txtTransactionWages, javax.swing.GroupLayout.PREFERRED_SIZE, 148, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE))))
                    .addGroup(jPanel29Layout.createSequentialGroup()
                        .addGroup(jPanel29Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel41, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel29Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel29Layout.createSequentialGroup()
                                .addComponent(txtTransactionPayPerPerson, javax.swing.GroupLayout.PREFERRED_SIZE, 148, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 45, Short.MAX_VALUE)
                                .addComponent(txtTransactionPayPerPerson1, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(txtTransactionCalculate)
                            .addGroup(jPanel29Layout.createSequentialGroup()
                                .addGroup(jPanel29Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(txtTransactionSalary, javax.swing.GroupLayout.DEFAULT_SIZE, 148, Short.MAX_VALUE)
                                    .addComponent(txtTransactionBalance))
                                .addGap(18, 18, Short.MAX_VALUE)
                                .addGroup(jPanel29Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(txtTransactionSalary1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 150, Short.MAX_VALUE)
                                    .addComponent(txtTransactionBalance1, javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(txtTransactionWagesTax)))))
                    .addComponent(txtTransactionDescription))
                .addContainerGap())
        );
        jPanel29Layout.setVerticalGroup(
            jPanel29Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel29Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel29Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtTransactionDate, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel29Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbxTransactionClients, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel29Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtTransactionWeight, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel29Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtTransactionPricePerTon, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel29Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtTransactionTotalReceived, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel29Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtTransactionWages, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel29Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel41, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtTransactionWagesTax, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel29Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtTransactionSalary, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtTransactionSalary1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel29Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtTransactionBalance, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtTransactionBalance1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel29Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtTransactionCalculate, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel29Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtTransactionPayPerPerson, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtTransactionPayPerPerson1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtTransactionDescription, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(51, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jPanel29, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(18, 18, 18)
                        .addComponent(jPanel22, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(104, 104, 104)
                        .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel29, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel22, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jTabbedPane1.addTab("Transaksi", jPanel1);

        jLabel37.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel37.setText("Petanyaan Transaksi");

        txtTransactionListFrom.setDateFormatString("dd/MM/yyyy");
        txtTransactionListFrom.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        txtTransactionListFrom.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                dateChange(evt);
            }
        });

        txtTransactionListTo.setDateFormatString("dd/MM/yyyy");
        txtTransactionListTo.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        txtTransactionListTo.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                dateChange(evt);
            }
        });

        jLabel39.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel39.setText("Hingga");

        btnTransactionSave.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        btnTransactionSave.setText("Cari");
        btnTransactionSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTransactionSaveActionPerformed(evt);
            }
        });

        jPanel27.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        btnTransactionListEnd.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        btnTransactionListEnd.setText("Tutup");
        btnTransactionListEnd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTransactionListEndActionPerformed(evt);
            }
        });

        btnTransactionListDelete.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        btnTransactionListDelete.setText("Batalkan");
        btnTransactionListDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTransactionListDeleteActionPerformed(evt);
            }
        });

        ubahTransaksi.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        ubahTransaksi.setText("Ubah");
        ubahTransaksi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ubahTransaksiActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel27Layout = new javax.swing.GroupLayout(jPanel27);
        jPanel27.setLayout(jPanel27Layout);
        jPanel27Layout.setHorizontalGroup(
            jPanel27Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel27Layout.createSequentialGroup()
                .addGap(501, 501, 501)
                .addComponent(ubahTransaksi)
                .addGap(18, 18, 18)
                .addComponent(btnTransactionListDelete, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnTransactionListEnd)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel27Layout.setVerticalGroup(
            jPanel27Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel27Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel27Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnTransactionListEnd)
                    .addComponent(btnTransactionListDelete, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(ubahTransaksi))
                .addContainerGap())
        );

        tblTransactionList.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        tblTransactionList.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "", "Tarikh", "Kod & Name Pelanggan", "Keterangan", "Berat KG", "Harga Diterima Seton", "Jumlah Diterima", "Upah Kerja", "Jumlah Gaji", "Kiraan Asing"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane6.setViewportView(tblTransactionList);
        tblTransactionList.getColumnModel().getColumn(0).setResizable(false);
        tblTransactionList.getColumnModel().getColumn(0).setPreferredWidth(40);
        tblTransactionList.getColumnModel().getColumn(1).setResizable(false);

        javax.swing.GroupLayout jPanel28Layout = new javax.swing.GroupLayout(jPanel28);
        jPanel28.setLayout(jPanel28Layout);
        jPanel28Layout.setHorizontalGroup(
            jPanel28Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane6, javax.swing.GroupLayout.DEFAULT_SIZE, 1278, Short.MAX_VALUE)
        );
        jPanel28Layout.setVerticalGroup(
            jPanel28Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane6, javax.swing.GroupLayout.DEFAULT_SIZE, 359, Short.MAX_VALUE)
        );

        jLabel38.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel38.setText("Kod & Nama ");

        jLabel40.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel40.setText("Tarikh Tempoh");

        javax.swing.GroupLayout jPanel26Layout = new javax.swing.GroupLayout(jPanel26);
        jPanel26.setLayout(jPanel26Layout);
        jPanel26Layout.setHorizontalGroup(
            jPanel26Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel26Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel26Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel28, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel27, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel26Layout.createSequentialGroup()
                        .addGroup(jPanel26Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel37)
                            .addGroup(jPanel26Layout.createSequentialGroup()
                                .addGroup(jPanel26Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(jLabel38, javax.swing.GroupLayout.DEFAULT_SIZE, 145, Short.MAX_VALUE)
                                    .addComponent(jLabel40, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel26Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addGroup(jPanel26Layout.createSequentialGroup()
                                        .addComponent(txtTransactionListFrom, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(jLabel39)
                                        .addGap(12, 12, 12)
                                        .addComponent(txtTransactionListTo, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addComponent(btnTransactionSave, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(cbxTransactionListClients, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                        .addGap(0, 773, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel26Layout.setVerticalGroup(
            jPanel26Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel26Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel37, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel26Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel38, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbxTransactionListClients, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel26Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtTransactionListTo, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel39, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtTransactionListFrom, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel40, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnTransactionSave, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel28, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel27, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jTabbedPane1.addTab("Senarai Transaksi", jPanel26);

        jLabel42.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel42.setText("Petanyaan Transaksi");

        txtTransactionListFrom1.setDateFormatString("dd/MM/yyyy");
        txtTransactionListFrom1.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        txtTransactionListFrom1.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                dateChange(evt);
            }
        });

        txtTransactionListTo1.setDateFormatString("dd/MM/yyyy");
        txtTransactionListTo1.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        txtTransactionListTo1.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                dateChange(evt);
            }
        });

        jLabel43.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel43.setText("Hingga");

        btnTransactionSave1.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        btnTransactionSave1.setText("Cari");
        btnTransactionSave1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTransactionSave1ActionPerformed(evt);
            }
        });

        jPanel31.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        btnTransactionListEnd1.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        btnTransactionListEnd1.setText("Tutup");
        btnTransactionListEnd1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTransactionListEnd1ActionPerformed(evt);
            }
        });

        btnTransactionListDelete1.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        btnTransactionListDelete1.setText("Batalkan");
        btnTransactionListDelete1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTransactionListDelete1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel31Layout = new javax.swing.GroupLayout(jPanel31);
        jPanel31.setLayout(jPanel31Layout);
        jPanel31Layout.setHorizontalGroup(
            jPanel31Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel31Layout.createSequentialGroup()
                .addGap(549, 549, 549)
                .addComponent(btnTransactionListDelete1, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnTransactionListEnd1)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel31Layout.setVerticalGroup(
            jPanel31Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel31Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel31Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnTransactionListEnd1)
                    .addComponent(btnTransactionListDelete1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        tblTransactionList1.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        tblTransactionList1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "", "Tarikh", "Kod & Name Pelanggan", "Keterangan", "Berat KG", "Harga Diterima Seton", "Jumlah Diterima", "Upah Kerja (TAX)", "Jumlah Gaji", "Kiraan Asing"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane7.setViewportView(tblTransactionList1);
        tblTransactionList1.getColumnModel().getColumn(0).setResizable(false);
        tblTransactionList1.getColumnModel().getColumn(0).setPreferredWidth(40);
        tblTransactionList1.getColumnModel().getColumn(1).setResizable(false);

        javax.swing.GroupLayout jPanel32Layout = new javax.swing.GroupLayout(jPanel32);
        jPanel32.setLayout(jPanel32Layout);
        jPanel32Layout.setHorizontalGroup(
            jPanel32Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane7, javax.swing.GroupLayout.DEFAULT_SIZE, 1278, Short.MAX_VALUE)
        );
        jPanel32Layout.setVerticalGroup(
            jPanel32Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane7, javax.swing.GroupLayout.DEFAULT_SIZE, 359, Short.MAX_VALUE)
        );

        jLabel44.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel44.setText("Kod & Nama ");

        jLabel45.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel45.setText("Tarikh Tempoh");

        javax.swing.GroupLayout jPanel30Layout = new javax.swing.GroupLayout(jPanel30);
        jPanel30.setLayout(jPanel30Layout);
        jPanel30Layout.setHorizontalGroup(
            jPanel30Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel30Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel30Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel32, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel31, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel30Layout.createSequentialGroup()
                        .addGroup(jPanel30Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel42)
                            .addGroup(jPanel30Layout.createSequentialGroup()
                                .addGroup(jPanel30Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(jLabel44, javax.swing.GroupLayout.DEFAULT_SIZE, 145, Short.MAX_VALUE)
                                    .addComponent(jLabel45, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel30Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addGroup(jPanel30Layout.createSequentialGroup()
                                        .addComponent(txtTransactionListFrom1, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(jLabel43)
                                        .addGap(12, 12, 12)
                                        .addComponent(txtTransactionListTo1, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addComponent(btnTransactionSave1, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(cbxTransactionListClients1, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                        .addGap(0, 773, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel30Layout.setVerticalGroup(
            jPanel30Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel30Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel42, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel30Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel44, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbxTransactionListClients1, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel30Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtTransactionListTo1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel43, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtTransactionListFrom1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel45, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnTransactionSave1, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel32, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel31, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jTabbedPane1.addTab("Senarai Transaksi (Tax)", jPanel30);

        jPanel3.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N

        jLabel13.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel13.setText("Simpanan Tetap");

        jLabel14.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel14.setText("Nama Pekerja");

        jLabel15.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel15.setText("Tempoh Tarikh");

        jLabel16.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel16.setText("Baki Simpanan Terkini");

        txtSavingDateFrom.setDateFormatString("dd/MM/yyyy");
        txtSavingDateFrom.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        txtSavingDateFrom.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                dateChange(evt);
            }
        });

        jLabel17.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel17.setText("Hingga");

        txtSavingDateTo.setDateFormatString("dd/MM/yyyy");
        txtSavingDateTo.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        txtSavingDateTo.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                dateChange(evt);
            }
        });

        txtSavingCurrentSaving.setEditable(false);
        txtSavingCurrentSaving.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        txtSavingCurrentSaving.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                getSavingDetails(evt);
            }
        });

        tblSaving.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        tblSaving.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "", "Tarikh", "Keterangan", "Simpanan +", "Bayaran -", "Baki"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblSaving.setSelectionMode(javax.swing.ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        jScrollPane1.setViewportView(tblSaving);
        tblSaving.getColumnModel().getColumn(4).setHeaderValue("Bayaran -");
        tblSaving.getColumnModel().getColumn(5).setHeaderValue("Baki");

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 1278, Short.MAX_VALUE)
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 349, Short.MAX_VALUE)
        );

        jPanel9.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        btnSavingEnd.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        btnSavingEnd.setText("Tutup");
        btnSavingEnd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSavingEndActionPerformed(evt);
            }
        });

        btnSavingCancel.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        btnSavingCancel.setText("Batalkan");
        btnSavingCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSavingCancelActionPerformed(evt);
            }
        });

        btnSavingNew.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        btnSavingNew.setText("Transaksi Baru");
        btnSavingNew.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSavingNewActionPerformed(evt);
            }
        });

        EditSimpanan.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        EditSimpanan.setText("Ubah");
        EditSimpanan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                EditSimpananActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel9Layout.createSequentialGroup()
                .addGap(385, 385, 385)
                .addComponent(btnSavingNew)
                .addGap(18, 18, 18)
                .addComponent(EditSimpanan, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnSavingCancel, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnSavingEnd)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel9Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnSavingEnd)
                    .addComponent(btnSavingCancel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnSavingNew)
                    .addComponent(EditSimpanan))
                .addContainerGap())
        );

        cbxSavingWorkers.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N

        btnSavingSearch.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        btnSavingSearch.setText("Cari");
        btnSavingSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSavingSearchActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel8, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel9, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel13, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel3Layout.createSequentialGroup()
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(jLabel16, javax.swing.GroupLayout.DEFAULT_SIZE, 160, Short.MAX_VALUE)
                                    .addComponent(jLabel15, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jLabel14, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel3Layout.createSequentialGroup()
                                        .addComponent(txtSavingCurrentSaving, javax.swing.GroupLayout.PREFERRED_SIZE, 355, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(18, 18, 18)
                                        .addComponent(btnSavingSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 66, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                        .addComponent(cbxSavingWorkers, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addGroup(jPanel3Layout.createSequentialGroup()
                                            .addComponent(txtSavingDateFrom, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                            .addComponent(jLabel17)
                                            .addGap(18, 18, 18)
                                            .addComponent(txtSavingDateTo, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE))))))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(cbxSavingWorkers, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jLabel17, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txtSavingDateTo, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txtSavingDateFrom, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(6, 6, 6)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txtSavingCurrentSaving, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btnSavingSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jPanel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jTabbedPane1.addTab("Simpanan", jPanel3);

        jLabel48.setFont(jLabel48.getFont().deriveFont(jLabel48.getFont().getStyle() | java.awt.Font.BOLD));
        jLabel48.setText("Pinjaman");

        jLabel49.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel49.setText("Nama Pekerja");

        cbxLoanWorkers.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N

        jLabel50.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel50.setText("Tempoh Tarikh");

        txtLoanDateFrom.setDateFormatString("dd/MM/yyyy");
        txtLoanDateFrom.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        txtLoanDateFrom.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                dateChange(evt);
            }
        });

        jLabel51.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel51.setText("Hingga");

        txtLoanDateTo.setDateFormatString("dd/MM/yyyy");
        txtLoanDateTo.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        txtLoanDateTo.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                dateChange(evt);
            }
        });

        btnLoanSearch.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        btnLoanSearch.setText("Cari");
        btnLoanSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLoanSearchActionPerformed(evt);
            }
        });

        jPanel41.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        btnLoanEnd.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        btnLoanEnd.setText("Tutup");
        btnLoanEnd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLoanEndActionPerformed(evt);
            }
        });

        btnLoanCancel.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        btnLoanCancel.setText("Batalkan");
        btnLoanCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLoanCancelActionPerformed(evt);
            }
        });

        editPinjaman.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        editPinjaman.setText("Ubah");
        editPinjaman.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editPinjamanActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel41Layout = new javax.swing.GroupLayout(jPanel41);
        jPanel41.setLayout(jPanel41Layout);
        jPanel41Layout.setHorizontalGroup(
            jPanel41Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel41Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(editPinjaman)
                .addGap(18, 18, 18)
                .addComponent(btnLoanCancel, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnLoanEnd)
                .addGap(515, 515, 515))
        );
        jPanel41Layout.setVerticalGroup(
            jPanel41Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel41Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel41Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnLoanEnd)
                    .addComponent(btnLoanCancel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(editPinjaman))
                .addContainerGap())
        );

        tblLoan.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        tblLoan.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "", "Tarikh", "Keterangan", "Pinjaman"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblLoan.setSelectionMode(javax.swing.ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        jScrollPane9.setViewportView(tblLoan);

        javax.swing.GroupLayout jPanel39Layout = new javax.swing.GroupLayout(jPanel39);
        jPanel39.setLayout(jPanel39Layout);
        jPanel39Layout.setHorizontalGroup(
            jPanel39Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel39Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel39Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel48, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel39Layout.createSequentialGroup()
                        .addGroup(jPanel39Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jLabel49, javax.swing.GroupLayout.DEFAULT_SIZE, 140, Short.MAX_VALUE)
                            .addComponent(jLabel50, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 140, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel39Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jPanel39Layout.createSequentialGroup()
                                .addComponent(txtLoanDateFrom, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel51)
                                .addGap(18, 18, 18)
                                .addComponent(txtLoanDateTo, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnLoanSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 66, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(cbxLoanWorkers, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addComponent(jPanel41, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane9, javax.swing.GroupLayout.DEFAULT_SIZE, 1278, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel39Layout.setVerticalGroup(
            jPanel39Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel39Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel48, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel39Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel49, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbxLoanWorkers, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel39Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel50, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel51, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtLoanDateTo, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtLoanDateFrom, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnLoanSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(54, 54, 54)
                .addComponent(jScrollPane9, javax.swing.GroupLayout.DEFAULT_SIZE, 349, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel41, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jTabbedPane1.addTab("Pinjaman", jPanel39);

        jPanel11.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N

        jLabel18.setFont(jLabel18.getFont().deriveFont(jLabel18.getFont().getStyle() | java.awt.Font.BOLD));
        jLabel18.setText("Bayaran Gaji");

        jLabel19.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel19.setText("Nama Pekerja");

        jLabel20.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel20.setText("Tempoh Tarikh");

        jLabel22.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel22.setText("Hingga");

        tblPay.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        tblPay.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "", "Tarikh", "Keterangan", "Baki +", "Bayaran -", "Simpanan Tetap"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblPay.setSelectionMode(javax.swing.ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        jScrollPane2.setViewportView(tblPay);
        tblPay.getColumnModel().getColumn(0).setResizable(false);
        tblPay.getColumnModel().getColumn(0).setPreferredWidth(25);

        javax.swing.GroupLayout jPanel12Layout = new javax.swing.GroupLayout(jPanel12);
        jPanel12.setLayout(jPanel12Layout);
        jPanel12Layout.setHorizontalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 1278, Short.MAX_VALUE)
        );
        jPanel12Layout.setVerticalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 388, Short.MAX_VALUE)
        );

        jPanel13.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        btnPayEnd.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        btnPayEnd.setText("Tutup");
        btnPayEnd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPayEndActionPerformed(evt);
            }
        });

        btnPayCancel.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        btnPayCancel.setText("Batalkan");
        btnPayCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPayCancelActionPerformed(evt);
            }
        });

        btnPayNew.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        btnPayNew.setText("Transaksi Baru");
        btnPayNew.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPayNewActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel13Layout = new javax.swing.GroupLayout(jPanel13);
        jPanel13.setLayout(jPanel13Layout);
        jPanel13Layout.setHorizontalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel13Layout.createSequentialGroup()
                .addGap(449, 449, 449)
                .addComponent(btnPayNew, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnPayCancel, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnPayEnd)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel13Layout.setVerticalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel13Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnPayEnd, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnPayCancel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnPayNew, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        cbxPaymentWorkers.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N

        btnPaymentSearch.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        btnPaymentSearch.setText("Cari");
        btnPaymentSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPaymentSearchActionPerformed(evt);
            }
        });

        txtPaymentMonth.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N

        txtPaymentMonthTo.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N

        javax.swing.GroupLayout jPanel11Layout = new javax.swing.GroupLayout(jPanel11);
        jPanel11.setLayout(jPanel11Layout);
        jPanel11Layout.setHorizontalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel12, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel13, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel18, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel11Layout.createSequentialGroup()
                        .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jLabel20, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel19, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(cbxPaymentWorkers, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel11Layout.createSequentialGroup()
                                .addComponent(txtPaymentMonth, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtPaymentYear, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(10, 10, 10)
                                .addComponent(jLabel22)
                                .addGap(10, 10, 10)
                                .addComponent(txtPaymentMonthTo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtPaymentYearTo, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnPaymentSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addContainerGap())
        );
        jPanel11Layout.setVerticalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel18, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel19, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbxPaymentWorkers, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(jLabel22, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(txtPaymentMonthTo, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 26, Short.MAX_VALUE)
                        .addComponent(txtPaymentYearTo, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnPaymentSearch))
                    .addComponent(jLabel20, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(txtPaymentMonth, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 26, Short.MAX_VALUE)
                        .addComponent(txtPaymentYear, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(9, 9, 9)
                .addComponent(jPanel12, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel13, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jTabbedPane1.addTab("Bayaran Gaji", jPanel11);

        jTabbedPane4.setFont(new java.awt.Font("Arial", 0, 15)); // NOI18N
        jTabbedPane4.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jTabbedPane4StateChanged(evt);
            }
        });

        jLabel31.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel31.setText("Nama Pekerja");

        jLabel32.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel32.setText("Hingga");

        jLabel33.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel33.setText("Tempoh Tarikh");

        btnGroupWorkerReportType.add(rbtnWorkerMonthlyIncome);
        rbtnWorkerMonthlyIncome.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        rbtnWorkerMonthlyIncome.setSelected(true);
        rbtnWorkerMonthlyIncome.setText("Ringkasan Pendapatan Bulanan");
        rbtnWorkerMonthlyIncome.setIcon(new javax.swing.ImageIcon(getClass().getResource("/payroll/images/untick-icon.png"))); // NOI18N
        rbtnWorkerMonthlyIncome.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/payroll/images/tick-icon.png"))); // NOI18N

        btnGroupWorkerReportType.add(rbtnWorkerSaving);
        rbtnWorkerSaving.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        rbtnWorkerSaving.setText("Ringkasan Simpanan Tetap");
        rbtnWorkerSaving.setIcon(new javax.swing.ImageIcon(getClass().getResource("/payroll/images/untick-icon.png"))); // NOI18N
        rbtnWorkerSaving.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/payroll/images/tick-icon.png"))); // NOI18N

        btnGroupWorkerReportType.add(rbtnWorkerReportFull);
        rbtnWorkerReportFull.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        rbtnWorkerReportFull.setText("Akaun Lengkap Pekerja");
        rbtnWorkerReportFull.setIcon(new javax.swing.ImageIcon(getClass().getResource("/payroll/images/untick-icon.png"))); // NOI18N
        rbtnWorkerReportFull.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/payroll/images/tick-icon.png"))); // NOI18N

        jPanel25.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        btnReportWorkerEnd.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        btnReportWorkerEnd.setText("Tutup");
        btnReportWorkerEnd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnReportWorkerEndActionPerformed(evt);
            }
        });

        btnReportWorkerPrint.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        btnReportWorkerPrint.setText("Cetakkan");
        btnReportWorkerPrint.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnReportWorkerPrintActionPerformed(evt);
            }
        });

        btnReportWorkerExport.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        btnReportWorkerExport.setText("Format Excel");
        btnReportWorkerExport.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnReportWorkerExportActionPerformed(evt);
            }
        });

        btnReportWorkerGenerate.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        btnReportWorkerGenerate.setText("Senarai Transaksi");
        btnReportWorkerGenerate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnReportWorkerGenerateActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel25Layout = new javax.swing.GroupLayout(jPanel25);
        jPanel25.setLayout(jPanel25Layout);
        jPanel25Layout.setHorizontalGroup(
            jPanel25Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel25Layout.createSequentialGroup()
                .addGap(355, 355, 355)
                .addComponent(btnReportWorkerGenerate, javax.swing.GroupLayout.PREFERRED_SIZE, 158, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnReportWorkerExport)
                .addGap(18, 18, 18)
                .addComponent(btnReportWorkerPrint)
                .addGap(18, 18, 18)
                .addComponent(btnReportWorkerEnd)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel25Layout.setVerticalGroup(
            jPanel25Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel25Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel25Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnReportWorkerEnd)
                    .addComponent(btnReportWorkerPrint)
                    .addComponent(btnReportWorkerExport)
                    .addComponent(btnReportWorkerGenerate))
                .addContainerGap())
        );

        txtReportWorkerMonth.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N

        txtReportWorkerMonthTo.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N

        javax.swing.GroupLayout jPanel19Layout = new javax.swing.GroupLayout(jPanel19);
        jPanel19.setLayout(jPanel19Layout);
        jPanel19Layout.setHorizontalGroup(
            jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel19Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel19Layout.createSequentialGroup()
                        .addComponent(jPanel25, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addContainerGap())
                    .addGroup(jPanel19Layout.createSequentialGroup()
                        .addGroup(jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel31, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel33, javax.swing.GroupLayout.DEFAULT_SIZE, 120, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel19Layout.createSequentialGroup()
                                .addGroup(jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(rbtnWorkerMonthlyIncome, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 522, Short.MAX_VALUE)
                                    .addComponent(rbtnWorkerSaving, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 522, Short.MAX_VALUE)
                                    .addComponent(rbtnWorkerReportFull, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 522, Short.MAX_VALUE))
                                .addGap(637, 637, 637))
                            .addGroup(jPanel19Layout.createSequentialGroup()
                                .addGroup(jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(cbxReportWorkers, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(jPanel19Layout.createSequentialGroup()
                                        .addGroup(jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel19Layout.createSequentialGroup()
                                                .addComponent(txtReportWorkerMonthTo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(txtReportWorkerYearTo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel19Layout.createSequentialGroup()
                                                .addComponent(txtReportWorkerMonth, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(txtReportWorkerYear, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                        .addGap(18, 18, 18)
                                        .addComponent(jLabel32, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGap(0, 908, Short.MAX_VALUE))))))
        );
        jPanel19Layout.setVerticalGroup(
            jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel19Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel31, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbxReportWorkers, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel33, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtReportWorkerMonth, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtReportWorkerYear, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel32, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtReportWorkerMonthTo, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtReportWorkerYearTo, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(rbtnWorkerMonthlyIncome)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(rbtnWorkerSaving, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(rbtnWorkerReportFull)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 231, Short.MAX_VALUE)
                .addComponent(jPanel25, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jTabbedPane4.addTab("Laporan Pekerja", jPanel19);

        btnGroupSavingReport.add(rbtnSavingReportCurrentMonth);
        rbtnSavingReportCurrentMonth.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        rbtnSavingReportCurrentMonth.setSelected(true);
        rbtnSavingReportCurrentMonth.setText("Bulan Ini");
        rbtnSavingReportCurrentMonth.setIcon(new javax.swing.ImageIcon(getClass().getResource("/payroll/images/untick-icon.png"))); // NOI18N
        rbtnSavingReportCurrentMonth.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/payroll/images/tick-icon.png"))); // NOI18N

        btnGroupSavingReport.add(rbtnSavingReportPreviousMonth);
        rbtnSavingReportPreviousMonth.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        rbtnSavingReportPreviousMonth.setText("Bulan Lepas");
        rbtnSavingReportPreviousMonth.setIcon(new javax.swing.ImageIcon(getClass().getResource("/payroll/images/untick-icon.png"))); // NOI18N
        rbtnSavingReportPreviousMonth.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/payroll/images/tick-icon.png"))); // NOI18N

        btnGroupSavingReport.add(rbtnMonhtlyReportDateRange2);
        rbtnMonhtlyReportDateRange2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/payroll/images/untick-icon.png"))); // NOI18N
        rbtnMonhtlyReportDateRange2.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/payroll/images/tick-icon.png"))); // NOI18N

        txtSavingReportDateFrom.setDateFormatString("dd/MM/yyyy");
        txtSavingReportDateFrom.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        txtSavingReportDateFrom.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                dateChange(evt);
            }
        });

        jLabel52.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel52.setText("Hingga");

        txtSavingReportDateTo.setDateFormatString("dd/MM/yyyy");
        txtSavingReportDateTo.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        txtSavingReportDateTo.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                dateChange(evt);
            }
        });

        javax.swing.GroupLayout jPanel40Layout = new javax.swing.GroupLayout(jPanel40);
        jPanel40.setLayout(jPanel40Layout);
        jPanel40Layout.setHorizontalGroup(
            jPanel40Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel40Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel40Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(rbtnSavingReportCurrentMonth, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 308, Short.MAX_VALUE)
                    .addComponent(rbtnSavingReportPreviousMonth, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 308, Short.MAX_VALUE)
                    .addGroup(jPanel40Layout.createSequentialGroup()
                        .addComponent(rbtnMonhtlyReportDateRange2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtSavingReportDateFrom, javax.swing.GroupLayout.DEFAULT_SIZE, 109, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel52)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtSavingReportDateTo, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(8, 8, 8)))
                .addContainerGap())
        );
        jPanel40Layout.setVerticalGroup(
            jPanel40Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel40Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(rbtnSavingReportCurrentMonth, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 3, Short.MAX_VALUE)
                .addComponent(rbtnSavingReportPreviousMonth)
                .addGroup(jPanel40Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel40Layout.createSequentialGroup()
                        .addGap(8, 8, 8)
                        .addGroup(jPanel40Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtSavingReportDateFrom, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel52, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtSavingReportDateTo, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel40Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(rbtnMonhtlyReportDateRange2, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(22, Short.MAX_VALUE))
        );

        tblSavingReportWorkers.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        tblSavingReportWorkers.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "", "", ""
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Boolean.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                true, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblSavingReportWorkers.setFocusable(false);
        tblSavingReportWorkers.setTableHeader(null);
        jScrollPane10.setViewportView(tblSavingReportWorkers);
        tblSavingReportWorkers.getColumnModel().getColumn(0).setResizable(false);
        tblSavingReportWorkers.getColumnModel().getColumn(0).setPreferredWidth(20);
        tblSavingReportWorkers.getColumnModel().getColumn(1).setResizable(false);
        tblSavingReportWorkers.getColumnModel().getColumn(1).setPreferredWidth(50);
        tblSavingReportWorkers.getColumnModel().getColumn(2).setResizable(false);

        cboxMonthlyReportAllWorkers2.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        cboxMonthlyReportAllWorkers2.setText("Semua Pekerja");
        cboxMonthlyReportAllWorkers2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/payroll/images/untick-icon.png"))); // NOI18N
        cboxMonthlyReportAllWorkers2.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/payroll/images/tick-icon.png"))); // NOI18N
        cboxMonthlyReportAllWorkers2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboxMonthlyReportAllWorkers2ActionPerformed(evt);
            }
        });

        jLabel53.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        jLabel53.setText("Senarai Pekerja");
        jLabel53.setFocusable(false);

        javax.swing.GroupLayout jPanel42Layout = new javax.swing.GroupLayout(jPanel42);
        jPanel42.setLayout(jPanel42Layout);
        jPanel42Layout.setHorizontalGroup(
            jPanel42Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel42Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel42Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane10, javax.swing.GroupLayout.DEFAULT_SIZE, 292, Short.MAX_VALUE)
                    .addComponent(jLabel53, javax.swing.GroupLayout.DEFAULT_SIZE, 292, Short.MAX_VALUE)
                    .addComponent(cboxMonthlyReportAllWorkers2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel42Layout.setVerticalGroup(
            jPanel42Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel42Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel53, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane10, javax.swing.GroupLayout.PREFERRED_SIZE, 333, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cboxMonthlyReportAllWorkers2)
                .addContainerGap(30, Short.MAX_VALUE))
        );

        jPanel43.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        btnSavingReportEnd.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        btnSavingReportEnd.setText("Tutup");
        btnSavingReportEnd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSavingReportEndActionPerformed(evt);
            }
        });

        btnSavingReportPrint.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        btnSavingReportPrint.setText("Cetakkan");
        btnSavingReportPrint.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSavingReportPrintActionPerformed(evt);
            }
        });

        btnSavingReportExport.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        btnSavingReportExport.setText("Format Excel");
        btnSavingReportExport.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSavingReportExportActionPerformed(evt);
            }
        });

        btnSavingReportGenerate.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        btnSavingReportGenerate.setText("Senarai Transaksi");
        btnSavingReportGenerate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSavingReportGenerateActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel43Layout = new javax.swing.GroupLayout(jPanel43);
        jPanel43.setLayout(jPanel43Layout);
        jPanel43Layout.setHorizontalGroup(
            jPanel43Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel43Layout.createSequentialGroup()
                .addGap(337, 337, 337)
                .addComponent(btnSavingReportGenerate)
                .addGap(18, 18, 18)
                .addComponent(btnSavingReportExport, javax.swing.GroupLayout.PREFERRED_SIZE, 129, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnSavingReportPrint, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnSavingReportEnd)
                .addContainerGap(432, Short.MAX_VALUE))
        );
        jPanel43Layout.setVerticalGroup(
            jPanel43Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel43Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel43Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnSavingReportEnd)
                    .addComponent(btnSavingReportPrint)
                    .addComponent(btnSavingReportExport)
                    .addComponent(btnSavingReportGenerate))
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel38Layout = new javax.swing.GroupLayout(jPanel38);
        jPanel38.setLayout(jPanel38Layout);
        jPanel38Layout.setHorizontalGroup(
            jPanel38Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel38Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel38Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel38Layout.createSequentialGroup()
                        .addComponent(jPanel40, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(27, 27, 27)
                        .addComponent(jPanel42, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jPanel43, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel38Layout.setVerticalGroup(
            jPanel38Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel38Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel38Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel42, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel38Layout.createSequentialGroup()
                        .addComponent(jPanel40, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel43, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jTabbedPane4.addTab("Laporan Simpanan", jPanel38);

        chkMonthlyReportDate.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        chkMonthlyReportDate.setSelected(true);
        chkMonthlyReportDate.setText("Tarikh");
        chkMonthlyReportDate.setEnabled(false);
        chkMonthlyReportDate.setIcon(new javax.swing.ImageIcon(getClass().getResource("/payroll/images/untick-icon.png"))); // NOI18N
        chkMonthlyReportDate.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/payroll/images/tick-icon.png"))); // NOI18N

        chkMonthlyReportClientName.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        chkMonthlyReportClientName.setSelected(true);
        chkMonthlyReportClientName.setText("Name Pelanggan");
        chkMonthlyReportClientName.setIcon(new javax.swing.ImageIcon(getClass().getResource("/payroll/images/untick-icon.png"))); // NOI18N
        chkMonthlyReportClientName.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/payroll/images/tick-icon.png"))); // NOI18N

        chkMonthlyReportDescription.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        chkMonthlyReportDescription.setSelected(true);
        chkMonthlyReportDescription.setText("Keterangan");
        chkMonthlyReportDescription.setIcon(new javax.swing.ImageIcon(getClass().getResource("/payroll/images/untick-icon.png"))); // NOI18N
        chkMonthlyReportDescription.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/payroll/images/tick-icon.png"))); // NOI18N

        chkMonthlyReportWeight.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        chkMonthlyReportWeight.setSelected(true);
        chkMonthlyReportWeight.setText("Berat KG");
        chkMonthlyReportWeight.setIcon(new javax.swing.ImageIcon(getClass().getResource("/payroll/images/untick-icon.png"))); // NOI18N
        chkMonthlyReportWeight.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/payroll/images/tick-icon.png"))); // NOI18N

        chkMonthlyReportPricePerTon.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        chkMonthlyReportPricePerTon.setSelected(true);
        chkMonthlyReportPricePerTon.setText("Harga Diterma Seton");
        chkMonthlyReportPricePerTon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/payroll/images/untick-icon.png"))); // NOI18N
        chkMonthlyReportPricePerTon.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/payroll/images/tick-icon.png"))); // NOI18N

        chkMonthlyReportTotalReceived.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        chkMonthlyReportTotalReceived.setSelected(true);
        chkMonthlyReportTotalReceived.setText("Jumlah Diterima");
        chkMonthlyReportTotalReceived.setIcon(new javax.swing.ImageIcon(getClass().getResource("/payroll/images/untick-icon.png"))); // NOI18N
        chkMonthlyReportTotalReceived.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/payroll/images/tick-icon.png"))); // NOI18N

        chkMonthlyReportWages.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        chkMonthlyReportWages.setSelected(true);
        chkMonthlyReportWages.setText("Upah Kerja");
        chkMonthlyReportWages.setIcon(new javax.swing.ImageIcon(getClass().getResource("/payroll/images/untick-icon.png"))); // NOI18N
        chkMonthlyReportWages.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/payroll/images/tick-icon.png"))); // NOI18N

        chkMonthlyReportSalary.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        chkMonthlyReportSalary.setSelected(true);
        chkMonthlyReportSalary.setText("Jumlah Gaji");
        chkMonthlyReportSalary.setIcon(new javax.swing.ImageIcon(getClass().getResource("/payroll/images/untick-icon.png"))); // NOI18N
        chkMonthlyReportSalary.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/payroll/images/tick-icon.png"))); // NOI18N

        chkMonthlyReportBalance.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        chkMonthlyReportBalance.setSelected(true);
        chkMonthlyReportBalance.setText("Jumlah Baki");
        chkMonthlyReportBalance.setIcon(new javax.swing.ImageIcon(getClass().getResource("/payroll/images/untick-icon.png"))); // NOI18N
        chkMonthlyReportBalance.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/payroll/images/tick-icon.png"))); // NOI18N

        chkMonthlyReportKiraanAsing.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        chkMonthlyReportKiraanAsing.setSelected(true);
        chkMonthlyReportKiraanAsing.setText("Kiraan Asing");
        chkMonthlyReportKiraanAsing.setIcon(new javax.swing.ImageIcon(getClass().getResource("/payroll/images/untick-icon.png"))); // NOI18N
        chkMonthlyReportKiraanAsing.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/payroll/images/tick-icon.png"))); // NOI18N

        chkMonthlyReportSaving.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        chkMonthlyReportSaving.setSelected(true);
        chkMonthlyReportSaving.setText("Simpanan Tetap");
        chkMonthlyReportSaving.setIcon(new javax.swing.ImageIcon(getClass().getResource("/payroll/images/untick-icon.png"))); // NOI18N
        chkMonthlyReportSaving.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/payroll/images/tick-icon.png"))); // NOI18N

        chkMonthlyReportSalaryPayment.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        chkMonthlyReportSalaryPayment.setSelected(true);
        chkMonthlyReportSalaryPayment.setText("Bayaran Gaji");
        chkMonthlyReportSalaryPayment.setIcon(new javax.swing.ImageIcon(getClass().getResource("/payroll/images/untick-icon.png"))); // NOI18N
        chkMonthlyReportSalaryPayment.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/payroll/images/tick-icon.png"))); // NOI18N

        javax.swing.GroupLayout jPanel20Layout = new javax.swing.GroupLayout(jPanel20);
        jPanel20.setLayout(jPanel20Layout);
        jPanel20Layout.setHorizontalGroup(
            jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel20Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(chkMonthlyReportSalaryPayment, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(chkMonthlyReportSaving, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(chkMonthlyReportKiraanAsing, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(chkMonthlyReportBalance, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(chkMonthlyReportSalary, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(chkMonthlyReportDate, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 200, Short.MAX_VALUE)
                    .addComponent(chkMonthlyReportClientName, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 200, Short.MAX_VALUE)
                    .addComponent(chkMonthlyReportDescription, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 200, Short.MAX_VALUE)
                    .addComponent(chkMonthlyReportWeight, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 200, Short.MAX_VALUE)
                    .addComponent(chkMonthlyReportPricePerTon, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 200, Short.MAX_VALUE)
                    .addComponent(chkMonthlyReportTotalReceived, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 200, Short.MAX_VALUE)
                    .addComponent(chkMonthlyReportWages, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 200, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel20Layout.setVerticalGroup(
            jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel20Layout.createSequentialGroup()
                .addComponent(chkMonthlyReportDate)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkMonthlyReportClientName, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkMonthlyReportDescription, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkMonthlyReportWeight, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkMonthlyReportPricePerTon, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkMonthlyReportTotalReceived, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(chkMonthlyReportWages, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(chkMonthlyReportSalary, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(chkMonthlyReportBalance, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(chkMonthlyReportKiraanAsing, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(chkMonthlyReportSaving, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(chkMonthlyReportSalaryPayment, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        btnGroupMonthlyReport.add(rbtnMonhtlyReportCurrentMonth);
        rbtnMonhtlyReportCurrentMonth.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        rbtnMonhtlyReportCurrentMonth.setSelected(true);
        rbtnMonhtlyReportCurrentMonth.setText("Bulan Ini");
        rbtnMonhtlyReportCurrentMonth.setIcon(new javax.swing.ImageIcon(getClass().getResource("/payroll/images/untick-icon.png"))); // NOI18N
        rbtnMonhtlyReportCurrentMonth.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/payroll/images/tick-icon.png"))); // NOI18N

        btnGroupMonthlyReport.add(rbtnMonthlyReportLastMonth);
        rbtnMonthlyReportLastMonth.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        rbtnMonthlyReportLastMonth.setText("Bulan Lepas");
        rbtnMonthlyReportLastMonth.setIcon(new javax.swing.ImageIcon(getClass().getResource("/payroll/images/untick-icon.png"))); // NOI18N
        rbtnMonthlyReportLastMonth.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/payroll/images/tick-icon.png"))); // NOI18N

        btnGroupMonthlyReport.add(rbtnMonhtlyReportDateRange);
        rbtnMonhtlyReportDateRange.setIcon(new javax.swing.ImageIcon(getClass().getResource("/payroll/images/untick-icon.png"))); // NOI18N
        rbtnMonhtlyReportDateRange.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/payroll/images/tick-icon.png"))); // NOI18N

        txtMonthlyReportDateFrom.setDateFormatString("dd/MM/yyyy");
        txtMonthlyReportDateFrom.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        txtMonthlyReportDateFrom.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                dateChange(evt);
            }
        });

        jLabel30.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel30.setText("Hingga");

        txtMonthlyReportDateTo.setDateFormatString("dd/MM/yyyy");
        txtMonthlyReportDateTo.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        txtMonthlyReportDateTo.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                dateChange(evt);
            }
        });

        javax.swing.GroupLayout jPanel21Layout = new javax.swing.GroupLayout(jPanel21);
        jPanel21.setLayout(jPanel21Layout);
        jPanel21Layout.setHorizontalGroup(
            jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel21Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(rbtnMonhtlyReportCurrentMonth, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 295, Short.MAX_VALUE)
                    .addComponent(rbtnMonthlyReportLastMonth, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 295, Short.MAX_VALUE)
                    .addGroup(jPanel21Layout.createSequentialGroup()
                        .addComponent(rbtnMonhtlyReportDateRange)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtMonthlyReportDateFrom, javax.swing.GroupLayout.DEFAULT_SIZE, 95, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel30)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtMonthlyReportDateTo, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(8, 8, 8)))
                .addContainerGap())
        );
        jPanel21Layout.setVerticalGroup(
            jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel21Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(rbtnMonhtlyReportCurrentMonth, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(rbtnMonthlyReportLastMonth, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(txtMonthlyReportDateFrom, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(rbtnMonhtlyReportDateRange, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel30, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtMonthlyReportDateTo, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18))
        );

        jPanel23.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        btnMonthlyReportEnd.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        btnMonthlyReportEnd.setText("Tutup");
        btnMonthlyReportEnd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMonthlyReportEndActionPerformed(evt);
            }
        });

        btnMonthlyReportPrint.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        btnMonthlyReportPrint.setText("Cetakkan");
        btnMonthlyReportPrint.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMonthlyReportPrintActionPerformed(evt);
            }
        });

        btnMonthlyReportExport.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        btnMonthlyReportExport.setText("Format Excel");
        btnMonthlyReportExport.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMonthlyReportExportActionPerformed(evt);
            }
        });

        btnMonthlyReportGenerate.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        btnMonthlyReportGenerate.setText("Senarai Transaksi");
        btnMonthlyReportGenerate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMonthlyReportGenerateActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel23Layout = new javax.swing.GroupLayout(jPanel23);
        jPanel23.setLayout(jPanel23Layout);
        jPanel23Layout.setHorizontalGroup(
            jPanel23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel23Layout.createSequentialGroup()
                .addGap(382, 382, 382)
                .addComponent(btnMonthlyReportGenerate)
                .addGap(18, 18, 18)
                .addComponent(btnMonthlyReportExport, javax.swing.GroupLayout.PREFERRED_SIZE, 129, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnMonthlyReportPrint, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnMonthlyReportEnd)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel23Layout.setVerticalGroup(
            jPanel23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel23Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnMonthlyReportEnd)
                    .addComponent(btnMonthlyReportPrint)
                    .addComponent(btnMonthlyReportExport)
                    .addComponent(btnMonthlyReportGenerate))
                .addContainerGap())
        );

        tblMonthlyReportWorkers.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        tblMonthlyReportWorkers.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "", "", ""
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Boolean.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                true, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblMonthlyReportWorkers.setCellSelectionEnabled(true);
        tblMonthlyReportWorkers.setFocusable(false);
        tblMonthlyReportWorkers.setTableHeader(null);
        jScrollPane5.setViewportView(tblMonthlyReportWorkers);
        tblMonthlyReportWorkers.getColumnModel().getColumn(0).setResizable(false);
        tblMonthlyReportWorkers.getColumnModel().getColumn(0).setPreferredWidth(20);
        tblMonthlyReportWorkers.getColumnModel().getColumn(1).setResizable(false);
        tblMonthlyReportWorkers.getColumnModel().getColumn(1).setPreferredWidth(50);
        tblMonthlyReportWorkers.getColumnModel().getColumn(2).setResizable(false);

        cboxMonthlyReportAllWorkers.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        cboxMonthlyReportAllWorkers.setText("Semua Pekerja");
        cboxMonthlyReportAllWorkers.setIcon(new javax.swing.ImageIcon(getClass().getResource("/payroll/images/untick-icon.png"))); // NOI18N
        cboxMonthlyReportAllWorkers.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/payroll/images/tick-icon.png"))); // NOI18N
        cboxMonthlyReportAllWorkers.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboxMonthlyReportAllWorkersActionPerformed(evt);
            }
        });

        jLabel34.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        jLabel34.setText("Senarai Pekerja");
        jLabel34.setFocusable(false);

        javax.swing.GroupLayout jPanel24Layout = new javax.swing.GroupLayout(jPanel24);
        jPanel24.setLayout(jPanel24Layout);
        jPanel24Layout.setHorizontalGroup(
            jPanel24Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel24Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel24Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane5, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(cboxMonthlyReportAllWorkers, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel34, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel24Layout.setVerticalGroup(
            jPanel24Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel24Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel34, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 235, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cboxMonthlyReportAllWorkers, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel18Layout = new javax.swing.GroupLayout(jPanel18);
        jPanel18.setLayout(jPanel18Layout);
        jPanel18Layout.setHorizontalGroup(
            jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel18Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel23, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel18Layout.createSequentialGroup()
                        .addComponent(jPanel20, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jPanel21, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jPanel24, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(133, 133, 133)))
                .addContainerGap())
        );
        jPanel18Layout.setVerticalGroup(
            jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel18Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel18Layout.createSequentialGroup()
                        .addComponent(jPanel21, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel24, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 13, Short.MAX_VALUE))
                    .addComponent(jPanel20, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel23, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jTabbedPane4.addTab("Laporan Bulanan", jPanel18);

        chkMonthlyReportDate1.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        chkMonthlyReportDate1.setSelected(true);
        chkMonthlyReportDate1.setText("Tarikh");
        chkMonthlyReportDate1.setEnabled(false);
        chkMonthlyReportDate1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/payroll/images/untick-icon.png"))); // NOI18N
        chkMonthlyReportDate1.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/payroll/images/tick-icon.png"))); // NOI18N

        chkMonthlyReportClientName1.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        chkMonthlyReportClientName1.setSelected(true);
        chkMonthlyReportClientName1.setText("Name Pelanggan");
        chkMonthlyReportClientName1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/payroll/images/untick-icon.png"))); // NOI18N
        chkMonthlyReportClientName1.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/payroll/images/tick-icon.png"))); // NOI18N

        chkMonthlyReportDescription1.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        chkMonthlyReportDescription1.setSelected(true);
        chkMonthlyReportDescription1.setText("Keterangan");
        chkMonthlyReportDescription1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/payroll/images/untick-icon.png"))); // NOI18N
        chkMonthlyReportDescription1.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/payroll/images/tick-icon.png"))); // NOI18N

        chkMonthlyReportWeight1.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        chkMonthlyReportWeight1.setSelected(true);
        chkMonthlyReportWeight1.setText("Berat KG");
        chkMonthlyReportWeight1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/payroll/images/untick-icon.png"))); // NOI18N
        chkMonthlyReportWeight1.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/payroll/images/tick-icon.png"))); // NOI18N

        chkMonthlyReportPricePerTon1.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        chkMonthlyReportPricePerTon1.setSelected(true);
        chkMonthlyReportPricePerTon1.setText("Harga Diterma Seton");
        chkMonthlyReportPricePerTon1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/payroll/images/untick-icon.png"))); // NOI18N
        chkMonthlyReportPricePerTon1.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/payroll/images/tick-icon.png"))); // NOI18N

        chkMonthlyReportTotalReceived1.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        chkMonthlyReportTotalReceived1.setSelected(true);
        chkMonthlyReportTotalReceived1.setText("Jumlah Diterima");
        chkMonthlyReportTotalReceived1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/payroll/images/untick-icon.png"))); // NOI18N
        chkMonthlyReportTotalReceived1.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/payroll/images/tick-icon.png"))); // NOI18N

        chkMonthlyReportWages1.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        chkMonthlyReportWages1.setSelected(true);
        chkMonthlyReportWages1.setText("Upah Kerja (Tax)");
        chkMonthlyReportWages1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/payroll/images/untick-icon.png"))); // NOI18N
        chkMonthlyReportWages1.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/payroll/images/tick-icon.png"))); // NOI18N

        chkMonthlyReportSalary1.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        chkMonthlyReportSalary1.setSelected(true);
        chkMonthlyReportSalary1.setText("Jumlah Gaji");
        chkMonthlyReportSalary1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/payroll/images/untick-icon.png"))); // NOI18N
        chkMonthlyReportSalary1.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/payroll/images/tick-icon.png"))); // NOI18N

        chkMonthlyReportBalance1.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        chkMonthlyReportBalance1.setSelected(true);
        chkMonthlyReportBalance1.setText("Jumlah Baki");
        chkMonthlyReportBalance1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/payroll/images/untick-icon.png"))); // NOI18N
        chkMonthlyReportBalance1.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/payroll/images/tick-icon.png"))); // NOI18N

        chkMonthlyReportKiraanAsing1.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        chkMonthlyReportKiraanAsing1.setSelected(true);
        chkMonthlyReportKiraanAsing1.setText("Kiraan Asing");
        chkMonthlyReportKiraanAsing1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/payroll/images/untick-icon.png"))); // NOI18N
        chkMonthlyReportKiraanAsing1.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/payroll/images/tick-icon.png"))); // NOI18N

        chkMonthlyReportSaving1.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        chkMonthlyReportSaving1.setSelected(true);
        chkMonthlyReportSaving1.setText("Simpanan Tetap");
        chkMonthlyReportSaving1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/payroll/images/untick-icon.png"))); // NOI18N
        chkMonthlyReportSaving1.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/payroll/images/tick-icon.png"))); // NOI18N

        chkMonthlyReportSalaryPayment1.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        chkMonthlyReportSalaryPayment1.setSelected(true);
        chkMonthlyReportSalaryPayment1.setText("Bayaran Gaji");
        chkMonthlyReportSalaryPayment1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/payroll/images/untick-icon.png"))); // NOI18N
        chkMonthlyReportSalaryPayment1.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/payroll/images/tick-icon.png"))); // NOI18N

        javax.swing.GroupLayout jPanel34Layout = new javax.swing.GroupLayout(jPanel34);
        jPanel34.setLayout(jPanel34Layout);
        jPanel34Layout.setHorizontalGroup(
            jPanel34Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel34Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel34Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(chkMonthlyReportDescription1, javax.swing.GroupLayout.DEFAULT_SIZE, 200, Short.MAX_VALUE)
                    .addComponent(chkMonthlyReportDate1, javax.swing.GroupLayout.DEFAULT_SIZE, 200, Short.MAX_VALUE)
                    .addComponent(chkMonthlyReportClientName1, javax.swing.GroupLayout.DEFAULT_SIZE, 200, Short.MAX_VALUE)
                    .addComponent(chkMonthlyReportWeight1, javax.swing.GroupLayout.DEFAULT_SIZE, 200, Short.MAX_VALUE)
                    .addComponent(chkMonthlyReportPricePerTon1, javax.swing.GroupLayout.DEFAULT_SIZE, 200, Short.MAX_VALUE)
                    .addComponent(chkMonthlyReportTotalReceived1, javax.swing.GroupLayout.DEFAULT_SIZE, 200, Short.MAX_VALUE)
                    .addComponent(chkMonthlyReportWages1, javax.swing.GroupLayout.DEFAULT_SIZE, 200, Short.MAX_VALUE)
                    .addComponent(chkMonthlyReportSalary1, javax.swing.GroupLayout.DEFAULT_SIZE, 200, Short.MAX_VALUE)
                    .addComponent(chkMonthlyReportBalance1, javax.swing.GroupLayout.DEFAULT_SIZE, 200, Short.MAX_VALUE)
                    .addComponent(chkMonthlyReportKiraanAsing1, javax.swing.GroupLayout.DEFAULT_SIZE, 200, Short.MAX_VALUE)
                    .addComponent(chkMonthlyReportSaving1, javax.swing.GroupLayout.DEFAULT_SIZE, 200, Short.MAX_VALUE)
                    .addComponent(chkMonthlyReportSalaryPayment1, javax.swing.GroupLayout.DEFAULT_SIZE, 200, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel34Layout.setVerticalGroup(
            jPanel34Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel34Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(chkMonthlyReportDate1, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkMonthlyReportClientName1, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkMonthlyReportDescription1, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkMonthlyReportWeight1, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkMonthlyReportPricePerTon1, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkMonthlyReportTotalReceived1, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkMonthlyReportWages1, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkMonthlyReportSalary1, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkMonthlyReportBalance1, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkMonthlyReportKiraanAsing1, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkMonthlyReportSaving1, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkMonthlyReportSalaryPayment1, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        buttonGroupMonthlyReportTax.add(rbtnMonhtlyReportCurrentMonth1);
        rbtnMonhtlyReportCurrentMonth1.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        rbtnMonhtlyReportCurrentMonth1.setSelected(true);
        rbtnMonhtlyReportCurrentMonth1.setText("Bulan Ini");
        rbtnMonhtlyReportCurrentMonth1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/payroll/images/untick-icon.png"))); // NOI18N
        rbtnMonhtlyReportCurrentMonth1.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/payroll/images/tick-icon.png"))); // NOI18N

        buttonGroupMonthlyReportTax.add(rbtnMonthlyReportLastMonth1);
        rbtnMonthlyReportLastMonth1.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        rbtnMonthlyReportLastMonth1.setText("Bulan Lepas");
        rbtnMonthlyReportLastMonth1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/payroll/images/untick-icon.png"))); // NOI18N
        rbtnMonthlyReportLastMonth1.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/payroll/images/tick-icon.png"))); // NOI18N

        buttonGroupMonthlyReportTax.add(rbtnMonhtlyReportDateRange1);
        rbtnMonhtlyReportDateRange1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/payroll/images/untick-icon.png"))); // NOI18N
        rbtnMonhtlyReportDateRange1.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/payroll/images/tick-icon.png"))); // NOI18N

        txtMonthlyReportDateFrom1.setDateFormatString("dd/MM/yyyy");
        txtMonthlyReportDateFrom1.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        txtMonthlyReportDateFrom1.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                dateChange(evt);
            }
        });

        jLabel46.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel46.setText("Hingga");

        txtMonthlyReportDateTo1.setDateFormatString("dd/MM/yyyy");
        txtMonthlyReportDateTo1.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        txtMonthlyReportDateTo1.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                dateChange(evt);
            }
        });

        javax.swing.GroupLayout jPanel35Layout = new javax.swing.GroupLayout(jPanel35);
        jPanel35.setLayout(jPanel35Layout);
        jPanel35Layout.setHorizontalGroup(
            jPanel35Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel35Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel35Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(rbtnMonhtlyReportCurrentMonth1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 309, Short.MAX_VALUE)
                    .addComponent(rbtnMonthlyReportLastMonth1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 309, Short.MAX_VALUE)
                    .addGroup(jPanel35Layout.createSequentialGroup()
                        .addComponent(rbtnMonhtlyReportDateRange1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtMonthlyReportDateFrom1, javax.swing.GroupLayout.DEFAULT_SIZE, 109, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel46)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtMonthlyReportDateTo1, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(8, 8, 8)))
                .addContainerGap())
        );
        jPanel35Layout.setVerticalGroup(
            jPanel35Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel35Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(rbtnMonhtlyReportCurrentMonth1, javax.swing.GroupLayout.PREFERRED_SIZE, 33, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(rbtnMonthlyReportLastMonth1, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel35Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtMonthlyReportDateFrom1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel46, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtMonthlyReportDateTo1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(rbtnMonhtlyReportDateRange1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 29, Short.MAX_VALUE))
                .addContainerGap())
        );

        jPanel36.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        btnMonthlyReportEnd1.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        btnMonthlyReportEnd1.setText("Tutup");
        btnMonthlyReportEnd1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMonthlyReportEnd1ActionPerformed(evt);
            }
        });

        btnMonthlyReportPrint1.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        btnMonthlyReportPrint1.setText("Cetakkan");
        btnMonthlyReportPrint1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMonthlyReportPrint1ActionPerformed(evt);
            }
        });

        btnMonthlyReportExport1.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        btnMonthlyReportExport1.setText("Format Excel");
        btnMonthlyReportExport1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMonthlyReportExport1ActionPerformed(evt);
            }
        });

        btnMonthlyReportGenerate1.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        btnMonthlyReportGenerate1.setText("Senarai Transaksi");
        btnMonthlyReportGenerate1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMonthlyReportGenerate1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel36Layout = new javax.swing.GroupLayout(jPanel36);
        jPanel36.setLayout(jPanel36Layout);
        jPanel36Layout.setHorizontalGroup(
            jPanel36Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel36Layout.createSequentialGroup()
                .addGap(367, 367, 367)
                .addComponent(btnMonthlyReportGenerate1)
                .addGap(18, 18, 18)
                .addComponent(btnMonthlyReportExport1, javax.swing.GroupLayout.PREFERRED_SIZE, 129, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnMonthlyReportPrint1, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnMonthlyReportEnd1)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel36Layout.setVerticalGroup(
            jPanel36Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel36Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel36Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnMonthlyReportEnd1)
                    .addComponent(btnMonthlyReportPrint1)
                    .addComponent(btnMonthlyReportExport1)
                    .addComponent(btnMonthlyReportGenerate1))
                .addContainerGap())
        );

        tblMonthlyReportWorkers1.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        tblMonthlyReportWorkers1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "", "", ""
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Boolean.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                true, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblMonthlyReportWorkers1.setFocusable(false);
        tblMonthlyReportWorkers1.setTableHeader(null);
        jScrollPane8.setViewportView(tblMonthlyReportWorkers1);
        tblMonthlyReportWorkers1.getColumnModel().getColumn(0).setResizable(false);
        tblMonthlyReportWorkers1.getColumnModel().getColumn(0).setPreferredWidth(20);
        tblMonthlyReportWorkers1.getColumnModel().getColumn(1).setResizable(false);
        tblMonthlyReportWorkers1.getColumnModel().getColumn(1).setPreferredWidth(50);
        tblMonthlyReportWorkers1.getColumnModel().getColumn(2).setResizable(false);

        cboxMonthlyReportAllWorkers1.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        cboxMonthlyReportAllWorkers1.setText("Semua Pekerja");
        cboxMonthlyReportAllWorkers1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/payroll/images/untick-icon.png"))); // NOI18N
        cboxMonthlyReportAllWorkers1.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/payroll/images/tick-icon.png"))); // NOI18N
        cboxMonthlyReportAllWorkers1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboxMonthlyReportAllWorkers1ActionPerformed(evt);
            }
        });

        jLabel47.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        jLabel47.setText("Senarai Pekerja");
        jLabel47.setFocusable(false);

        javax.swing.GroupLayout jPanel37Layout = new javax.swing.GroupLayout(jPanel37);
        jPanel37.setLayout(jPanel37Layout);
        jPanel37Layout.setHorizontalGroup(
            jPanel37Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel37Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel37Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane8, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 301, Short.MAX_VALUE)
                    .addComponent(cboxMonthlyReportAllWorkers1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 301, Short.MAX_VALUE)
                    .addComponent(jLabel47, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 301, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel37Layout.setVerticalGroup(
            jPanel37Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel37Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel47, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane8, javax.swing.GroupLayout.PREFERRED_SIZE, 233, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cboxMonthlyReportAllWorkers1)
                .addContainerGap(8, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel33Layout = new javax.swing.GroupLayout(jPanel33);
        jPanel33.setLayout(jPanel33Layout);
        jPanel33Layout.setHorizontalGroup(
            jPanel33Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel33Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel33Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel36, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel33Layout.createSequentialGroup()
                        .addComponent(jPanel34, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel33Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jPanel37, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jPanel35, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(133, 133, 133)))
                .addContainerGap())
        );
        jPanel33Layout.setVerticalGroup(
            jPanel33Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel33Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel33Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel33Layout.createSequentialGroup()
                        .addComponent(jPanel35, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel37, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 5, Short.MAX_VALUE))
                    .addComponent(jPanel34, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel36, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jTabbedPane4.addTab("Laporan Bulanan (Tax)", jPanel33);

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane4)
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jTabbedPane4))
        );

        jTabbedPane1.addTab("Laporan", jPanel5);

        jTabbedPane2.setFont(new java.awt.Font("Arial", 0, 15)); // NOI18N

        jLabel21.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel21.setText("Nama Pekerja");

        jLabel23.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel23.setText("Tarikh Daftar");

        jLabel24.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel24.setText("Tarikh Pulang");

        jLabel25.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel25.setText("Baki Simpanan Kini");

        jLabel26.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel26.setText("Status");

        txtProfileWorkerRegisterDay.setEditable(false);
        txtProfileWorkerRegisterDay.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        txtProfileWorkerRegisterDay.setFocusable(false);

        txtProfileWorkerReturnDay.setEditable(false);
        txtProfileWorkerReturnDay.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        txtProfileWorkerReturnDay.setFocusable(false);

        txtProfileWorkerRegisterMonth.setEditable(false);
        txtProfileWorkerRegisterMonth.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        txtProfileWorkerRegisterMonth.setFocusable(false);

        txtProfileWorkerReturnMonth.setEditable(false);
        txtProfileWorkerReturnMonth.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        txtProfileWorkerReturnMonth.setFocusable(false);

        txtProfileWorkerRegisterYear.setEditable(false);
        txtProfileWorkerRegisterYear.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        txtProfileWorkerRegisterYear.setFocusable(false);

        txtProfileWorkerReturnYear.setEditable(false);
        txtProfileWorkerReturnYear.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        txtProfileWorkerReturnYear.setFocusable(false);

        txtProfileWorkerCurrentSaving.setEditable(false);
        txtProfileWorkerCurrentSaving.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        txtProfileWorkerCurrentSaving.setFocusable(false);

        txtProfileWorkerStatus.setEditable(false);
        txtProfileWorkerStatus.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        txtProfileWorkerStatus.setFocusable(false);

        jPanel15.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        btnProfileWorkerEnd.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        btnProfileWorkerEnd.setText("Tutup");
        btnProfileWorkerEnd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnProfileWorkerEndActionPerformed(evt);
            }
        });

        btnProfileWorkerDelete.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        btnProfileWorkerDelete.setText("Batal Rekod");

        btnProfileWorkerEdit.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        btnProfileWorkerEdit.setText("Ubah Rekod");
        btnProfileWorkerEdit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnProfileWorkerEditActionPerformed(evt);
            }
        });

        btnProfileWorkerNew.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        btnProfileWorkerNew.setText("Pekerja Baru");
        btnProfileWorkerNew.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnProfileWorkerNewActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel15Layout = new javax.swing.GroupLayout(jPanel15);
        jPanel15.setLayout(jPanel15Layout);
        jPanel15Layout.setHorizontalGroup(
            jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel15Layout.createSequentialGroup()
                .addGap(422, 422, 422)
                .addComponent(btnProfileWorkerNew)
                .addGap(18, 18, 18)
                .addComponent(btnProfileWorkerEdit)
                .addGap(18, 18, 18)
                .addComponent(btnProfileWorkerDelete)
                .addGap(18, 18, 18)
                .addComponent(btnProfileWorkerEnd)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel15Layout.setVerticalGroup(
            jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel15Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnProfileWorkerEnd)
                    .addComponent(btnProfileWorkerDelete, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnProfileWorkerEdit)
                    .addComponent(btnProfileWorkerNew))
                .addContainerGap())
        );

        cbxWorkers.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        cbxWorkers.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cbxWorkersItemStateChanged(evt);
            }
        });

        javax.swing.GroupLayout jPanel17Layout = new javax.swing.GroupLayout(jPanel17);
        jPanel17.setLayout(jPanel17Layout);
        jPanel17Layout.setHorizontalGroup(
            jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel17Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel17Layout.createSequentialGroup()
                        .addComponent(jPanel15, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addContainerGap())
                    .addGroup(jPanel17Layout.createSequentialGroup()
                        .addGroup(jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel17Layout.createSequentialGroup()
                                .addComponent(jLabel26, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtProfileWorkerStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 166, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel17Layout.createSequentialGroup()
                                .addComponent(jLabel21, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel17Layout.createSequentialGroup()
                                        .addComponent(txtProfileWorkerRegisterDay, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(txtProfileWorkerRegisterMonth, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(txtProfileWorkerRegisterYear, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addComponent(cbxWorkers, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addContainerGap(993, Short.MAX_VALUE))
                    .addGroup(jPanel17Layout.createSequentialGroup()
                        .addGroup(jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel17Layout.createSequentialGroup()
                                .addComponent(jLabel23, javax.swing.GroupLayout.DEFAULT_SIZE, 731, Short.MAX_VALUE)
                                .addGap(466, 466, 466))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel17Layout.createSequentialGroup()
                                .addGroup(jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(jLabel24, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jLabel25, javax.swing.GroupLayout.PREFERRED_SIZE, 120, Short.MAX_VALUE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addGroup(jPanel17Layout.createSequentialGroup()
                                        .addComponent(txtProfileWorkerReturnDay, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(txtProfileWorkerReturnMonth, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(txtProfileWorkerReturnYear, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addComponent(txtProfileWorkerCurrentSaving))
                                .addGap(0, 907, Short.MAX_VALUE)))
                        .addGap(86, 86, 86))))
        );
        jPanel17Layout.setVerticalGroup(
            jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel17Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel21, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbxWorkers, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel23, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtProfileWorkerRegisterDay, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtProfileWorkerRegisterMonth, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtProfileWorkerRegisterYear, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel24, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtProfileWorkerReturnDay, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtProfileWorkerReturnMonth, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtProfileWorkerReturnYear, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel25, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtProfileWorkerCurrentSaving, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel26, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtProfileWorkerStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 299, Short.MAX_VALUE)
                .addComponent(jPanel15, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jTabbedPane2.addTab("Profil Pekerja", jPanel17);

        jPanel16.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        btnProfileClientEnd.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        btnProfileClientEnd.setText("Tutup");
        btnProfileClientEnd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnProfileClientEndActionPerformed(evt);
            }
        });

        btnProfileClientDelete.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        btnProfileClientDelete.setText("Batal Rekod");

        btnProfileClientEdit.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        btnProfileClientEdit.setText("Ubah Rekod");
        btnProfileClientEdit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnProfileClientEditActionPerformed(evt);
            }
        });

        btnProfileClientNew.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        btnProfileClientNew.setText("Pelanggan Baru");
        btnProfileClientNew.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnProfileClientNewActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel16Layout = new javax.swing.GroupLayout(jPanel16);
        jPanel16.setLayout(jPanel16Layout);
        jPanel16Layout.setHorizontalGroup(
            jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel16Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnProfileClientNew)
                .addGap(18, 18, 18)
                .addComponent(btnProfileClientEdit)
                .addGap(18, 18, 18)
                .addComponent(btnProfileClientDelete)
                .addGap(18, 18, 18)
                .addComponent(btnProfileClientEnd)
                .addGap(371, 371, 371))
        );
        jPanel16Layout.setVerticalGroup(
            jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel16Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnProfileClientEnd)
                    .addComponent(btnProfileClientDelete, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnProfileClientEdit)
                    .addComponent(btnProfileClientNew))
                .addContainerGap())
        );

        jLabel27.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel27.setText("Nama Pelanggan");

        txtProfileClientStatus.setEditable(false);
        txtProfileClientStatus.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        txtProfileClientStatus.setFocusable(false);

        jLabel28.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel28.setText("Status");

        cbxClients.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        cbxClients.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cbxClientsItemStateChanged(evt);
            }
        });

        javax.swing.GroupLayout jPanel14Layout = new javax.swing.GroupLayout(jPanel14);
        jPanel14.setLayout(jPanel14Layout);
        jPanel14Layout.setHorizontalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel14Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel16, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel14Layout.createSequentialGroup()
                        .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jLabel28, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel27, javax.swing.GroupLayout.DEFAULT_SIZE, 120, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(cbxClients, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtProfileClientStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap())
        );
        jPanel14Layout.setVerticalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel14Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel27, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbxClients, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel28, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtProfileClientStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 392, Short.MAX_VALUE)
                .addComponent(jPanel16, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jTabbedPane2.addTab("Profil Pelanggan", jPanel14);

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane2)
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jTabbedPane2))
        );

        jTabbedPane1.addTab("Profil", jPanel6);

        jLabel35.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel35.setText("Kata Laluan Baru");

        txtPassword.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        txtPassword.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtPasswordActionPerformed(evt);
            }
        });

        txtPasswordConfirm.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        txtPasswordConfirm.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtPasswordConfirmActionPerformed(evt);
            }
        });

        btnPasswordChange.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        btnPasswordChange.setText("Ubah");
        btnPasswordChange.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPasswordChangeActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel35, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnPasswordChange, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtPasswordConfirm, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtPassword, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(1019, Short.MAX_VALUE))
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel35, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtPassword, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtPasswordConfirm, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnPasswordChange)
                .addContainerGap(466, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Kata Laluan", jPanel7);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jTabbedPane1)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 600, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnProfileWorkerNewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnProfileWorkerNewActionPerformed
        new WorkerForm(this, true).setVisible(true);
    }//GEN-LAST:event_btnProfileWorkerNewActionPerformed

    private void btnProfileWorkerEditActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnProfileWorkerEditActionPerformed
        if (this._current_worker_id > 0) {
            new WorkerForm(this, true, this._current_worker_id).setVisible(true);
        } else {
        }
    }//GEN-LAST:event_btnProfileWorkerEditActionPerformed

    private void btnProfileClientNewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnProfileClientNewActionPerformed
        new ClientForm(this, true).setVisible(true);
    }//GEN-LAST:event_btnProfileClientNewActionPerformed

    private void btnProfileClientEditActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnProfileClientEditActionPerformed
        if (this._current_client_id > 0) {
            new ClientForm(this, true, this._current_client_id).setVisible(true);
        } else {
        }
    }//GEN-LAST:event_btnProfileClientEditActionPerformed

    private void btnSavingNewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSavingNewActionPerformed
        new SavingForm(this, true).setVisible(true);
    }//GEN-LAST:event_btnSavingNewActionPerformed

    private void btnPayNewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPayNewActionPerformed
        new SalaryForm(this, true).setVisible(true);
    }//GEN-LAST:event_btnPayNewActionPerformed

    private void _clearTransactionForm() {
        cbxTransactionClients.setSelectedIndex(0);
        txtTransactionDate.setDate(Calendar.getInstance().getTime());
        txtTransactionDescription.setText("");
        txtTransactionWeight.setText("");
        txtTransactionPricePerTon.setText("");
        txtTransactionWagesTax.setText("");
        txtTransactionTotalReceived.setText("");
        txtTransactionWages.setText("");
        txtTransactionSalary.setText("");
        txtTransactionBalance.setText("");
        txtTransactionCalculate.setText("0");
        txtTransactionPayPerPerson.setText("");

        txtTransactionBalance1.setText("");
        txtTransactionSalary1.setText("");
        txtTransactionPayPerPerson1.setText("");

        this._prepare_transaction_tab();
    }

    private void jTabbedPane1StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jTabbedPane1StateChanged
        if (jTabbedPane1.getSelectedIndex() == 1) {
            this._prepare_transaction_tab();
        } else if (jTabbedPane1.getSelectedIndex() == 7 && jTabbedPane4.getSelectedIndex() == 0) {
            this._prepare_monthly_report_tab();
        } else if (jTabbedPane1.getSelectedIndex() == 7 && jTabbedPane4.getSelectedIndex() == 1) {
            this._prepare_monthly_report_tab_tax();
        } else if (jTabbedPane1.getSelectedIndex() == 7 && jTabbedPane4.getSelectedIndex() == 3) {
            this._prepare_saving_report_tab();
        }
    }//GEN-LAST:event_jTabbedPane1StateChanged

    private void btnMonthlyReportExportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMonthlyReportExportActionPerformed
        JFileChooser dialog = new JFileChooser();
        dialog.setFileFilter(new ExtensionFileFilter("Microsoft Excel", "xls"));

        if (dialog.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) {
            return;
        }

        File file = dialog.getSelectedFile();

        String filename = file.getPath();
        if (!filename.endsWith(".xls")) {
            filename += ".xls";
        }

        if (new File(filename).exists()) {
            if (JOptionPane.showConfirmDialog(null, "Overwrite the existing file?", "", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) != JOptionPane.YES_OPTION) {
                return;
            }
        }

        ArrayList<String> columns = this.getReportColumns();
        ArrayList<Worker> selected = this.getReportSelectedWorkers();
        ArrayList<ReportCalculation> calculations = this.getReportCalculations(selected);
        ArrayList<Transaction> transactions = this.getReportTransasctions(selected);
        ArrayList<ReportSalary> salaries = this.getReportSalaries(selected);

        int workerCount = selected.size();
        int index = 0;
        int pos = 0;

        for (Worker worker : selected) {
            calculations.add(new ReportCalculation(worker.getId()));
        }

        Workbook workbook = new HSSFWorkbook();
        Sheet sheet = (Sheet) workbook.createSheet("Laporan");

        Row firstHeaderRow = sheet.createRow(0);
        Row secondHeaderRow = sheet.createRow(1);

        // <editor-fold defaultstate="collapsed" desc="render header">
        index = 0;
        for (String column : columns) {
            sheet.addMergedRegion(new CellRangeAddress(0, 1, pos, pos));
            Cell cell = firstHeaderRow.createCell(index);
            cell.setCellValue(column);
            index++;
            pos++;
        }

        for (Worker worker : selected) {
            sheet.addMergedRegion(new CellRangeAddress(0, 0, pos, pos + 2));
            Cell cell = firstHeaderRow.createCell(index);
            cell.setCellValue(worker.getCode() + " " + worker.getName());

            cell = secondHeaderRow.createCell(index);
            cell.setCellValue("Gaji");
            cell = secondHeaderRow.createCell(index + 1);
            cell.setCellValue("Pinjaman");
            cell = secondHeaderRow.createCell(index + 2);
            cell.setCellValue("Baki");
            index += 3;
            pos += 3;
        }
        sheet.createFreezePane(0, 2);
        // </editor-fold>

        // <editor-fold defaultstate="collapsed" desc="render content">
        int rowIndex = 2;
        for (Transaction transaction : transactions) {
            index = 0;

            Row row = sheet.createRow(rowIndex);
            Cell cell = null;

            cell = row.createCell(index);
            if (chkMonthlyReportDate.isSelected()) {
                cell.setCellValue(Common.renderDisplayDate(transaction.getDate()));
                index++;
                cell = row.createCell(index);
            }
            if (chkMonthlyReportClientName.isSelected()) {
                if (transaction.getType() == Transaction.GENERAL) {
                    cell.setCellValue(transaction.getCustomer().getName());
                } else {
                    cell.setCellValue("Pinjaman");
                }
                index++;
                cell = row.createCell(index);
            }
            if (chkMonthlyReportDescription.isSelected()) {
                cell.setCellValue(transaction.getDescription());
                index++;
                cell = row.createCell(index);
            }
            if (chkMonthlyReportWeight.isSelected()) {
                if (transaction.getType() == Transaction.GENERAL) {
                    cell.setCellValue((int) transaction.getWeight());

                }
                index++;
                cell = row.createCell(index);
            }
            if (chkMonthlyReportPricePerTon.isSelected()) {
                if (transaction.getType() == Transaction.GENERAL) {
                    cell.setCellValue(Common.currency(transaction.getPricePerTon()));

                }
                index++;
                cell = row.createCell(index);
            }
            if (chkMonthlyReportTotalReceived.isSelected()) {
                if (transaction.getType() == Transaction.GENERAL) {
                    cell.setCellValue(Common.currency(transaction.getTotal()));

                }
                index++;
                cell = row.createCell(index);
            }
            if (chkMonthlyReportWages.isSelected()) {
                if (transaction.getType() == Transaction.GENERAL) {
                    cell.setCellValue(Common.currency(transaction.getWages()));

                }
                index++;
                cell = row.createCell(index);
            }
            if (chkMonthlyReportSalary.isSelected()) {
                if (transaction.getType() == Transaction.GENERAL) {
                    cell.setCellValue(Common.currency(transaction.getTotalSalary()));
                }
                index++;
                cell = row.createCell(index);
            }
            if (chkMonthlyReportBalance.isSelected()) {
                if (transaction.getType() == Transaction.GENERAL) {
                    cell.setCellValue(Common.currency(transaction.getBalance()));

                }
                index++;
                cell = row.createCell(index);
            }
            if (chkMonthlyReportKiraanAsing.isSelected()) {
                if (transaction.getType() == Transaction.GENERAL) {
                    cell.setCellValue(Common.currency(transaction.getKiraanAsing()));

                }
                index++;
                cell = row.createCell(index);
            }

            String[] workerIds = transaction.getNormalizedWorkerID().split(",");
            for (int i = 0; i < workerCount; i++) {
                if (Common.inArray(workerIds, selected.get(i).getId())) {
                    if (transaction.getType() == Transaction.GENERAL) {
                        calculations.get(i).setSalary(transaction.getWagePerWorker());
                        cell = row.createCell(index++);
                        cell.setCellValue(Common.currency(transaction.getWagePerWorker()));
                        cell = row.createCell(index++);
                        cell.setCellValue("0.00");
                        cell = row.createCell(index++);
                        cell.setCellValue(Common.currency(calculations.get(i).getBalance()));
                    } else {
                        calculations.get(i).setLoan(transaction.getLoanAmount());
                        cell = row.createCell(index++);
                        cell.setCellValue("0.00");
                        cell = row.createCell(index++);
                        cell.setCellValue(transaction.getLoanAmount());
                        cell = row.createCell(index++);
                        cell.setCellValue(Common.currency(calculations.get(i).getBalance()));
                    }
                } else {
                    cell = row.createCell(index++);
                    cell = row.createCell(index++);
                    cell = row.createCell(index++);
                }
            }

            rowIndex++;
        }
        // </editor-fold>

        // <editor-fold defaultstate="collapsed" desc="render summary">
        Row summaryRow = sheet.createRow(rowIndex);
        index = columns.size();

        sheet.addMergedRegion(new CellRangeAddress(rowIndex, rowIndex, 0, columns.size() - 1));

        for (int i = 0; i < workerCount; i++) {
            ReportCalculation calculation = calculations.get(i);
            Cell cell = summaryRow.createCell(index++);
            cell.setCellValue(Common.currency(calculation.getSalary()));
            cell = summaryRow.createCell(index++);
            cell.setCellValue(Common.currency(calculation.getLoan()));
            cell = summaryRow.createCell(index++);
            cell.setCellValue(Common.currency(calculation.getBalance()));
        }
        // </editor-fold>

        rowIndex++;

        if (chkMonthlyReportSalaryPayment.isSelected()) {
            for (ReportSalary salary : salaries) {
                index = 0;
                Row row = sheet.createRow(rowIndex);
                Cell cell = null;

                if (chkMonthlyReportDate.isSelected()) {
                    cell = row.createCell(index++);
                    cell.setCellValue(Common.renderDisplayDate(salary.getDate()));
                }

                sheet.addMergedRegion(new CellRangeAddress(rowIndex, rowIndex, index, columns.size() - 1));

                cell = row.createCell(index);
                cell.setCellValue("Bayaran Gaji");

                index = columns.size();

                for (int i = 0; i < workerCount; i++) {
                    double salaryValue = salary.getWorkerSalary(selected.get(i).getId());
                    cell = row.createCell(index++);
                    sheet.addMergedRegion(new CellRangeAddress(rowIndex, rowIndex, index - 1, index + 1));
                    cell.setCellValue(Common.currency(Math.abs(salaryValue)));
                    calculations.get(i).setPayment(salaryValue);

                    index += 2;
                }
                rowIndex++;
            }

            Row salarySummaryRow = sheet.createRow(rowIndex);

            index = 0;

            if (chkMonthlyReportDate.isSelected()) {
                index++;
            }

            salarySummaryRow.createCell(index).setCellValue("Baki");

            index = columns.size();

            for (int i = 0; i < workerCount; i++) {
                ReportCalculation calculation = calculations.get(i);
                Cell cell = salarySummaryRow.createCell(index++);
                sheet.addMergedRegion(new CellRangeAddress(rowIndex, rowIndex, index - 1, index + 1));
                cell.setCellValue(Common.currency(calculation.getTotalBalance()));

                index += 2;
            }

            rowIndex++;
        }

        if (chkMonthlyReportSaving.isSelected()) {
            rowIndex++;

            sheet.createRow(rowIndex++).createCell(0).setCellValue("Simpanan Tetap");
            Row previousBalanceRow = sheet.createRow(rowIndex);
            sheet.addMergedRegion(new CellRangeAddress(rowIndex, rowIndex, 0, columns.size() - 1));
            previousBalanceRow.createCell(0).setCellValue("Baki Bulan Lalu");

            Row currentBalanceRow = sheet.createRow(rowIndex + 1);
            sheet.addMergedRegion(new CellRangeAddress(rowIndex + 1, rowIndex + 1, 0, columns.size() - 1));
            currentBalanceRow.createCell(0).setCellValue("Bulan Ini");

            Row balanceRow = sheet.createRow(rowIndex + 2);
            sheet.addMergedRegion(new CellRangeAddress(rowIndex + 2, rowIndex + 2, 0, columns.size() - 1));
            balanceRow.createCell(0).setCellValue("Jumlah Baki");

            ArrayList<ReportSaving> savings = this.getReportSavings(selected);

            index = columns.size();
            for (ReportSaving saving : savings) {
                sheet.addMergedRegion(new CellRangeAddress(rowIndex, rowIndex, index, index + 2));
                sheet.addMergedRegion(new CellRangeAddress(rowIndex + 1, rowIndex + 1, index, index + 2));
                sheet.addMergedRegion(new CellRangeAddress(rowIndex + 2, rowIndex + 2, index, index + 2));
                previousBalanceRow.createCell(index).setCellValue(saving.getPrevious());
                currentBalanceRow.createCell(index).setCellValue(saving.getCurrent());
                balanceRow.createCell(index).setCellValue(saving.getBalance());

                index += 3;
            }
        }



        FileOutputStream out = null;
        try {
            out = new FileOutputStream(filename);
            workbook.write(out);
            out.close();
            JOptionPane.showMessageDialog(null, "Report generated to " + filename, "", JOptionPane.INFORMATION_MESSAGE);
        } catch (FileNotFoundException ex) {
            JOptionPane.showMessageDialog(null, "The process cannot access the file because it is being used by another process", "Error", JOptionPane.ERROR_MESSAGE);
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                out.close();
            } catch (IOException ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }//GEN-LAST:event_btnMonthlyReportExportActionPerformed

    private void btnMonthlyReportGenerateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMonthlyReportGenerateActionPerformed
        // get selected worker to be display
        Hashtable dates = this.getReportSelectedDateRange();
        ArrayList<String> columns = this.getReportColumns();
        ArrayList<Worker> selected = this.getReportSelectedWorkers();
        ArrayList<Transaction> transactions = this.getReportTransasctions(selected);
        ArrayList<ReportSaving> savings = this.getReportSavings(selected);
        ArrayList<ReportCalculation> calculations = this.getReportCalculations(selected);
        ArrayList<ReportSalary> salaries = this.getReportSalaries(selected);

        String html = "<html>";
        String css = "<style type=\"text/css\">";
        String table = "<table cellpadding=\"4\" cellspacing=\"0\">";
        String header = "";
        String content = "<tbody>";
        String saving_content = "";

        // <editor-fold defaultstate="collapsed" desc="render table header">
        header += "<thead><tr valign=\"top\">";
        String rowspan = selected.size() > 0 ? " rowspan=\"2\"" : "";
        for (String column : columns) {
            header += "<td" + rowspan + ">" + column + "</td>";
        }

        if (selected.size() > 0) {
            for (Worker worker : selected) {
                header += "<td colspan=\"3\">" + worker.getCode() + " " + worker.getName() + "</td>";
            }
            header += "</tr><tr>" + StringUtils.repeat("<td>Gaji</td><td>Pinjaman</td><td>Baki</td>", selected.size());
        }
        header += "</tr>";
        header += "</thead>";
        // </editor-fold>

        // <editor-fold defaultstate="collapsed" desc="prepare css">
        css += "table { border-collapse: collapse; font-family: calibri; font-size: 12px; } ";
        css += "table tr td.first { border-left: 1px solid #333; } ";
        css += "table tr td.last { border-right: 1px solid #333; } ";
        css += "table thead tr, table thead td, table tr.summary, table tr.summary td { border: 1px solid #333; } ";
        css += "table thead tr td { padding: 5px 20px 5px 5px; } ";
        css += "table tr td.worker_transaction { border-left: 1px solid #333; } ";
        css += "table tr.salary td { border: 1px solid #333 } ";
        css += "table tr.summary td.blank { border-left: none; border-bottom: none; } ";
        css += "table tr.saving_summary, table tr.saving_summary td { border : 1px solid #333; } ";
        css += "h3 { font-size: 14px; }";
        css += "table tr.content-body td { text-align: right;}";
        // </editor-fold>

        // <editor-fold defaultstate="collapsed" desc="render transaction content">
        for (Transaction transaction : transactions) {
            int first = 0;
            content += "<tr class=\"content-body\">";
            if (chkMonthlyReportDate.isSelected()) {
                content += "<td" + (first++ == 0 ? " class=\"first\"" : "") + " style=\"text-align: left; \">"
                        + Common.renderDisplayDate(transaction.getDate()) + "</td>";
            }

            if (chkMonthlyReportClientName.isSelected()) {
                if (transaction.getType() == Transaction.GENERAL) {
                    content += "<td" + (first++ == 0 ? " class=\"first\"" : "") + " style=\"text-align: left; \">"
                            + transaction.getCustomer().getName() + "</td>";

                } else {
                    content += "<td" + (first++ == 0 ? " class=\"first\"" : "")
                            + " style=\"text-align: left; \">Pinjaman</td>";

                }
            }
            if (chkMonthlyReportDescription.isSelected()) {
                content += "<td" + (first++ == 0 ? " class=\"first\"" : "") + " style=\"text-align: left; \">"
                        + transaction.getDescription() + "</td>";

            }
            if (chkMonthlyReportWeight.isSelected()) {
                if (transaction.getType() == Transaction.GENERAL) {
                    content += "<td" + (first++ == 0 ? " class=\"first\"" : "") + ">"
                            + ((int) transaction.getWeight()) + "</td>";
                } else {
                    content += "<td" + (first++ == 0 ? " class=\"first\"" : "") + "></td>";
                }
            }

            if (chkMonthlyReportPricePerTon.isSelected()) {
                if (transaction.getType() == Transaction.GENERAL) {
                    content += "<td" + (first++ == 0 ? " class=\"first\"" : "") + ">"
                            + Common.currency(transaction.getPricePerTon()) + "</td>";
                } else {
                    content += "<td" + (first++ == 0 ? " class=\"first\"" : "") + "></td>";
                }
            }

            if (chkMonthlyReportTotalReceived.isSelected()) {
                if (transaction.getType() == Transaction.GENERAL) {
                    content += "<td" + (first++ == 0 ? " class=\"first\"" : "") + ">"
                            + Common.currency(transaction.getTotal()) + "</td>";
                } else {
                    content += "<td" + (first++ == 0 ? " class=\"first\"" : "") + "></td>";
                }
            }

            if (chkMonthlyReportWages.isSelected()) {
                if (transaction.getType() == Transaction.GENERAL) {
                    content += "<td" + (first++ == 0 ? " class=\"first\"" : "") + ">"
                            + Common.currency(transaction.getWages()) + "</td>";
                } else {
                    content += "<td" + (first++ == 0 ? " class=\"first\"" : "") + "></td>";

                }
            }

            if (chkMonthlyReportSalary.isSelected()) {
                if (transaction.getType() == Transaction.GENERAL) {
                    content += "<td" + (first++ == 0 ? " class=\"first\"" : "") + ">"
                            + Common.currency(transaction.getTotalSalary()) + "</td>";
                } else {
                    content += "<td" + (first++ == 0 ? " class=\"first\"" : "") + "></td>";
                }
            }

            if (chkMonthlyReportBalance.isSelected()) {
                if (transaction.getType() == Transaction.GENERAL) {
                    content += "<td" + (first++ == 0 ? " class=\"first\"" : "") + ">"
                            + Common.currency(transaction.getBalance()) + "</td>";
                } else {
                    content += "<td" + (first++ == 0 ? " class=\"first\"" : "") + "></td>";
                }
            }

            if (chkMonthlyReportKiraanAsing.isSelected()) {
                if (transaction.getType() == Transaction.GENERAL) {
                    content += "<td" + (first++ == 0 ? " class=\"first\"" : "") + ">"
                            + transaction.getKiraanAsing() + "</td>";
                } else {
                    content += "<td" + (first++ == 0 ? " class=\"first\"" : "") + "></td>";
                }
            }

            String[] workerIds = transaction.getNormalizedWorkerID().split(",");

            /* add missing savings */
            int index = 0;
            for (Worker worker : selected) {
                if (Common.inArray(workerIds, worker.getId())) {
                    if (transaction.getType() == Transaction.GENERAL) {
                        calculations.get(index).setSalary(transaction.getWagePerWorker());
                        content += "<td class=\"worker_transaction\">"
                                + Common.currency(transaction.getWagePerWorker()) + "</td>";
                        content += "<td class=\"worker_savings\">"
                                + Common.currency(transaction.getLoanAmount()) + "</td>";
                        content += "<td" + (index == selected.size() - 1 ? " class=\"last\"" : "") + ">"
                                + Common.currency(calculations.get(index).getBalance()) + "</td>";
                    } else {
                        calculations.get(index).setLoan(transaction.getLoanAmount());
                        content += "<td class=\"worker_transaction\">0.00</td>";
                        content += "<td>" + Common.currency(transaction.getLoanAmount()) + "</td>";
                        content += "<td" + (index == selected.size() - 1 ? " class=\"last\"" : "") + ">" + Common.currency(calculations.get(index).getBalance()) + "</td>";
                    }
                } else {
                    content += "<td class=\"worker_transaction\"></td><td></td><td" + (index == selected.size() - 1 ? " class=\"last\"" : "") + "></td>";
                }

                index++;
            }
            content += "</tr>";
        }
        // </editor-fold>

        // <editor-fold defaultstate="collapsed" desc="render transaction worker summary">
        if (selected.size() > 0) {
            content += "<tr class=\"summary\">";
            content += "<td class=\"blank\" colspan=\"" + columns.size() + "\"></td>";
            for (ReportCalculation rc : calculations) {
                content += "<td style=\"text-align: right; \">" + Common.currency(rc.getSalary()) + "</td>";
                content += "<td style=\"text-align: right; \">" + Common.currency(rc.getLoan()) + "</td>";
                content += "<td style=\"text-align: right; \">" + Common.currency(rc.getBalance()) + "</td>";
            }
            content += "</tr>";
        }
        // </editor-fold>

        // <editor-fold defaultstate="collapsed" desc="render salaries report">
        if (salaries.size() > 0 && chkMonthlyReportSalaryPayment.isSelected()) {
            int size = columns.size();
            for (ReportSalary rs : salaries) {
                content += "<tr class=\"salary\">";


                size = columns.size();
                if (chkMonthlyReportDate.isSelected()) {
                    content += "<td>" + Common.renderDisplayDate(rs.getDate()) + "</td>";
                    size--;
                }

                content += "<td colspan=\"" + size + "\">Bayaran Gaji</td>";

                for (ReportCalculation rc : calculations) {
                    rc.setPayment(rs.getWorkerSalary(rc.getWorkerID()));
                    content += "<td colspan=\"3\" style=\"text-align: right; \">" + Common.currency(Math.abs(rs.getWorkerSalary(rc.getWorkerID()))) + "</td>";
                }

                content += "</tr>";
            }

            content += "<tr class=\"salary\">";

            if (chkMonthlyReportDate.isSelected()) {
                content += "<td>&nbsp;</td>";
            }

            content += "<td colspan=\"" + size + "\">Baki</td>";

            for (ReportCalculation rc : calculations) {
                content += "<td colspan=\"3\" style=\"text-align: right; \">" + Common.currency(rc.getTotalBalance()) + "</td>";
            }

            content += "</tr>";

        }
        // </editor-fold>

        // <editor-fold defaultstate="collapsed" desc="render report saving summary">
        if (chkMonthlyReportSaving.isSelected() && selected.size() > 0) {
            content += "<tr><td colspan=\"" + (selected.size() + columns.size()) + "\"><br /><b><u>Simpanan Tetap</u></b></td></tr>";

            /* Baki Lalu */
            content += "<tr class=\"saving_summary\">";

            int colspan_size = columns.size();

            content += "<td colspan=\"" + colspan_size + "\">";
            content += "Baki Bulan Lalu<br />";
            content += "Bulan Ini<br />";
            content += "Baki";
            content += "</td>";

            for (ReportSaving saving : savings) {
                content += "<td colspan=\"3\" style=\"text-align: right; \">";
                content += Common.currency(saving.getPrevious()) + "<br />";
                content += Common.currency(saving.getCurrent()) + "<br />";
                content += Common.currency(saving.getBalance());
                content += "</td>";
            }

            content += "</tr>";
        }
        // </editor-fold>

        content += "</tbody>";

        table += header + content;
        table += "</table>";

        css += "</style>";
        html += "<head>" + css + "</head>";
        html += "<body>";
        html += "<h3>Laporan Bulanan (" + Common.renderDisplayDate((Calendar) dates.get("from")) + " - " + Common.renderDisplayDate((Calendar) dates.get("to")) + ")</h3>";
        html += table;
        html += "</body>";
        html += "</html>";

        ReportFrame form = new ReportFrame();
        UserAgentContext context = new SimpleUserAgentContext();
        SimpleHtmlRendererContext rcontext = new SimpleHtmlRendererContext(form.html, context);
        DocumentBuilderImpl dbi = new DocumentBuilderImpl(context);
        Reader reader = new StringReader(html);
        Document document = null;

        try {
            document = dbi.parse(new InputSourceImpl(reader, "127.0.0.1"));
        } catch (SAXException ex) {
            System.err.println(ex.getMessage());
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }

        form.html.setDocument(document, rcontext);
        form.setVisible(true);
    }//GEN-LAST:event_btnMonthlyReportGenerateActionPerformed

    private ArrayList<String> getReportColumns() {
        ArrayList<String> headers = new ArrayList<String>();

        if (chkMonthlyReportDate.isSelected()) {
            headers.add("Tarikh");
        }
        if (chkMonthlyReportClientName.isSelected()) {
            headers.add("Nama Pelanggan");
        }
        if (chkMonthlyReportDescription.isSelected()) {
            headers.add("Keterangan");
        }
        if (chkMonthlyReportWeight.isSelected()) {
            headers.add("Berat KG");
        }
        if (chkMonthlyReportPricePerTon.isSelected()) {
            headers.add("Harga Diterima Seton");
        }
        if (chkMonthlyReportTotalReceived.isSelected()) {
            headers.add("Jumlah Diterima");
        }
        if (chkMonthlyReportWages.isSelected()) {
            headers.add("Upah Kerja");
        }
        if (chkMonthlyReportSalary.isSelected()) {
            headers.add("Jumlah Gaji");
        }
        if (chkMonthlyReportBalance.isSelected()) {
            headers.add("Jumlah Baki");
        }
        if (chkMonthlyReportKiraanAsing.isSelected()) {
            headers.add("Kiraan Asing");
        }

        return headers;
    }

    private ArrayList<Worker> getReportSelectedWorkers() {
        ArrayList<Worker> selected = new ArrayList<Worker>();
        int row = tblMonthlyReportWorkers.getRowCount();

        for (int i = 0; i < row; i++) {
            if (Boolean.parseBoolean(tblMonthlyReportWorkers.getValueAt(i, 0).toString())) {
                selected.add(workers.get(i));
            }
        }

        return selected;
    }

    private ArrayList<Transaction> getReportTransasctions(ArrayList<Worker> selected) {
        ArrayList<Transaction> transactions = new ArrayList<Transaction>();
        Hashtable dates = getReportSelectedDateRange();
        String id = Worker.join(selected);

        String query = "SELECT DISTINCT id, type, loan_amount, customer_id, description, weight, price_per_ton, wages, wages_tax, kiraan_asing, date, created, normalized_worker_id FROM transactions ";
        query += "INNER JOIN transaction_workers ON transactions.id = transaction_workers.transaction_id ";
        query += "WHERE date >= '" + Common.renderSQLDate((Calendar) dates.get("from")) + "' AND date <= '" + Common.renderSQLDate((Calendar) dates.get("to")) + "' ";
        query += id.isEmpty() ? "AND type = 1 ORDER BY date" : "AND worker_id IN (" + id + ") ORDER BY DATE";

        ResultSet rs = Database.instance().execute(query);

        try {
            while (rs.next()) {
                ArrayList<Worker> involver = Worker.bulk(rs.getString("normalized_worker_id"));
                transactions.add(new Transaction(rs.getInt("id"), rs.getInt("customer_id"), rs.getInt("type"), rs.getDouble("weight"), rs.getDouble("price_per_ton"), rs.getDouble("wages"), rs.getDouble("wages_tax"), rs.getDouble("kiraan_asing"), rs.getDouble("loan_amount"), involver, Common.convertStringToDate(rs.getString("date")), rs.getString("description"), rs.getString("normalized_worker_id")));
            }

            rs.close();
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }

        return transactions;
    }

    private ArrayList<ReportCalculation> getReportCalculations(ArrayList<Worker> selected) {
        ArrayList<ReportCalculation> calculations = new ArrayList<ReportCalculation>();

        for (Worker worker : selected) {
            calculations.add(new ReportCalculation(worker.getId()));
        }

        return calculations;
    }

    private ArrayList<ReportSaving> getReportSavings(ArrayList<Worker> selected) {
        ArrayList<ReportSaving> saving = new ArrayList<ReportSaving>();
        Hashtable dates = this.getReportSelectedDateRange();

        if (!chkMonthlyReportSaving.isSelected()) {
            return saving;
        }

        String query = "";
        ResultSet rs = null;
        for (Worker worker : selected) {
            try {
                query = "SELECT ";
                query += "(SELECT SUM(amount) FROM workerRecord WHERE worker_id = " + worker.getId() + " AND date < '" + Common.renderSQLDate((Calendar) dates.get("from")) + "' AND type IN (" + WorkerRecord.SAVING + "," + WorkerRecord.WITHDRAW + ")) AS previous, ";
                query += "(SELECT SUM(amount) FROM workerRecord WHERE worker_id = " + worker.getId() + " AND date >= '" + Common.renderSQLDate((Calendar) dates.get("from")) + "' AND date <= '" + Common.renderSQLDate((Calendar) dates.get("to")) + "' AND type IN (" + WorkerRecord.SAVING + "," + WorkerRecord.WITHDRAW + ")) AS current ";

                rs = Database.instance().execute(query);
                rs.next();

                saving.add(new ReportSaving(rs.getDouble("previous"), rs.getDouble("current"), worker));
                rs.close();
            } catch (SQLException ex) {
                System.err.println(ex.getMessage());
            }
        }

        return saving;

    }

    private ArrayList<ReportSalary> getReportSalaries(ArrayList<Worker> selected) {
        ArrayList<ReportSalary> salaries = new ArrayList<ReportSalary>();
        Hashtable dates = this.getReportSelectedDateRange();

        String id = "";
        for (Worker worker : selected) {
            id += worker.getId() + ",";
        }

        id = id.isEmpty() ? "0" : id.substring(0, id.length() - 1);

        String query = "SELECT worker_id, date, amount FROM workerRecord WHERE worker_id IN (" + id + ") AND type = " + WorkerRecord.PAYMENT + " ";
        query += "AND date >= '" + Common.renderSQLDate((Calendar) dates.get("from")) + "' ";
        query += "AND date <= '" + Common.renderSQLDate((Calendar) dates.get("to")) + "' ";
        query += "ORDER BY date";

        ResultSet rs = Database.instance().execute(query);

        try {
            String date = "";
            ReportSalary salary = null;

            while (rs.next()) {

                if (!date.equals(rs.getString("date"))) {
                    date = rs.getString("date");

                    if (salary != null) {
                        salaries.add(salary);
                    }

                    salary = new ReportSalary(workers, Common.convertStringToDate(rs.getString("date")));
                }

                salary.setWorkerSalary(rs.getInt("worker_id"), rs.getDouble("amount"));
            }

            if (salary != null) {
                salaries.add(salary);
            }

            rs.close();
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }

        return salaries;
    }

    private Hashtable getReportSelectedDateRange() {
        Hashtable values = new Hashtable();

        Calendar dateFrom = Calendar.getInstance(), dateTo = Calendar.getInstance();

        if (rbtnMonhtlyReportCurrentMonth.isSelected()) {
            dateFrom.set(Calendar.DAY_OF_MONTH, 1);
            dateTo.set(Calendar.DAY_OF_MONTH, dateTo.getActualMaximum(Calendar.DAY_OF_MONTH));
        } else if (rbtnMonthlyReportLastMonth.isSelected()) {
            dateFrom.set(Calendar.DAY_OF_MONTH, 1);
            dateTo.set(Calendar.DAY_OF_MONTH, dateTo.getActualMaximum(Calendar.DAY_OF_MONTH));
            dateFrom.roll(Calendar.MONTH, -1);
            dateTo.roll(Calendar.MONTH, -1);
        } else {
            dateFrom.setTime(txtMonthlyReportDateFrom.getDate());
            dateTo.setTime(txtMonthlyReportDateTo.getDate());
        }

        values.put("from", dateFrom);
        values.put("to", dateTo);

        return values;
    }

    private void cboxMonthlyReportAllWorkersActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboxMonthlyReportAllWorkersActionPerformed
        boolean selected = cboxMonthlyReportAllWorkers.isSelected();
        int row = tblMonthlyReportWorkers.getRowCount();
        for (int i = 0; i < row; i++) {
            tblMonthlyReportWorkers.setValueAt(selected, i, 0);
        }
    }//GEN-LAST:event_cboxMonthlyReportAllWorkersActionPerformed

    private void jTabbedPane4StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jTabbedPane4StateChanged
        jTabbedPane1StateChanged(evt);
    }//GEN-LAST:event_jTabbedPane4StateChanged

    private void btnMonthlyReportPrintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMonthlyReportPrintActionPerformed
        ArrayList<Worker> selected = this.getReportSelectedWorkers();
        ArrayList<ReportSaving> savings = this.getReportSavings(selected);
        ArrayList<Transaction> transactions = this.getReportTransasctions(selected);
        ArrayList<ReportCalculation> calculations = this.getReportCalculations(selected);
        ArrayList<ReportSalary> salaries = this.getReportSalaries(selected);
        Hashtable dates = getReportSelectedDateRange();

        PrinterJob job = PrinterJob.getPrinterJob();

        PageFormat format = job.defaultPage();
        format.setOrientation(PageFormat.PORTRAIT);

        job.setPrintable(new ReportPrinter(this, selected, transactions, calculations, savings, salaries, dates), format);

        if (job.printDialog() == true) {
            try {
                HashPrintRequestAttributeSet attr = new HashPrintRequestAttributeSet();
                attr.add(new MediaPrintableArea(0, 0, 210, 297, MediaPrintableArea.MM));
                job.print(attr);
            } catch (PrinterException ex) {
                System.err.println(ex.getMessage());
            }
        }
    }//GEN-LAST:event_btnMonthlyReportPrintActionPerformed

    private void getSavingDetails(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_getSavingDetails
        DefaultTableModel savingTableModel = (DefaultTableModel) tblSaving.getModel();

        int rowCount = tblSaving.getRowCount();

        for (int i = 0; i < rowCount; i++) {
            savingTableModel.removeRow(0);
        }



        if (cbxSavingWorkers.getSelectedIndex() == 0) {
            JOptionPane.showMessageDialog(null, "Sila pilih pekerja.", "Kesilapan!", JOptionPane.ERROR_MESSAGE);
            return;
        } else if (txtSavingDateFrom.getDate() == null || txtSavingDateTo.getDate() == null) {
            JOptionPane.showMessageDialog(null, "Sila isikan tempoh tarikh.", "Kesilapan!", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Worker selected = workers.get(cbxSavingWorkers.getSelectedIndex() - 1);

        String query = "SELECT * FROM workerRecord WHERE type IN (2, 3) AND worker_id = " + selected.getId() + " ";

        if (txtSavingDateFrom.getDate() != null) {
            Calendar calender = Calendar.getInstance();
            calender.setTime(txtSavingDateFrom.getDate());
            query += "AND date >= \"" + Common.renderSQLDate(calender) + "\" ";
        }

        if (txtSavingDateTo.getDate() != null) {
            Calendar calender = Calendar.getInstance();
            calender.setTime(txtSavingDateTo.getDate());
            query += "AND date <= \"" + Common.renderSQLDate(calender) + "\" ";
        }

        ResultSet results = Database.instance().execute(query);
        int counter = 0;
        double balance = 0.0;

        try {
            while (results.next()) {
                WorkerRecord record = new WorkerRecord(results.getInt("id"), results.getInt("worker_id"), results.getInt("type"), results.getDouble("amount"), results.getString("description"), Common.convertStringToDate(results.getString("date")));
                balance += record.getAmount();

                Object[] objects = new Object[]{
                    new Integer(counter + 1),
                    new String(Common.renderDisplayDate(record.getDate())),
                    new String(record.getDescription()),
                    new String(Common.currency(record.getAmount() > 0 ? record.getAmount() : 0.00)),
                    new String(Common.currency(record.getAmount() < 0 ? record.getAmount() : 0.00)),
                    new String(Common.currency(balance))
                };

                savingTableModel.addRow(objects);
                counter++;
            }
            results.close();
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }

        txtSavingCurrentSaving.setText(Common.currency(this.getWorkerCurrentSaving(selected.getId())));
    }//GEN-LAST:event_getSavingDetails

    private double getWorkerCurrentSaving(int worker_id) {
        String query = "SELECT SUM(amount) as current_saving FROM workerRecord WHERE type IN (2,3) AND worker_id = " + worker_id;

        ResultSet result = Database.instance().execute(query);

        try {
            result.next();
            return result.getDouble("current_saving");
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }

        return 0.0;
    }

    private void btnReportWorkerPrintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnReportWorkerPrintActionPerformed
        int monthFrom = txtReportWorkerMonth.getMonth() + 1;
        int monthTo = txtReportWorkerMonthTo.getMonth() + 1;
        int yearFrom = txtReportWorkerYear.getYear();
        int yearTo = txtReportWorkerYearTo.getYear();



        if (cbxReportWorkers.getSelectedIndex() == 0) {
            JOptionPane.showMessageDialog(null, "Sila Pilih Pekerja", "Kesilapan!", JOptionPane.ERROR_MESSAGE);
            return;
        } else if (monthTo < monthFrom && yearTo <= yearFrom) {
            JOptionPane.showMessageDialog(null, "Terdapat Kesilapan Tempoh Tarikh", "Kesilapan!", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Worker selected = workers.get(cbxReportWorkers.getSelectedIndex() - 1);

        PrinterJob job = PrinterJob.getPrinterJob();
        PageFormat format = job.defaultPage();
        format.setOrientation(PageFormat.PORTRAIT);

        int type = WorkerReportFrame.FULL;
        ArrayList<WorkerReport> reports = getWorkerReports();

        if (rbtnWorkerSaving.isSelected()) {
            type = WorkerReportFrame.SAVING;
        } else if (rbtnWorkerMonthlyIncome.isSelected()) {
            type = WorkerReportFrame.INCOME;
        }

        job.setPrintable(new WorkerReportPrinter(type, selected, reports), format);

        if (job.printDialog() == true) {
            try {
                HashPrintRequestAttributeSet attr = new HashPrintRequestAttributeSet();
                attr.add(new MediaPrintableArea(0, 0, 210, 297, MediaPrintableArea.MM));
                job.print(attr);
            } catch (PrinterException ex) {
                System.err.println(ex.getMessage());
            }
        }
    }//GEN-LAST:event_btnReportWorkerPrintActionPerformed

    private void btnReportWorkerExportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnReportWorkerExportActionPerformed
        int monthFrom = txtReportWorkerMonth.getMonth() + 1;
        int monthTo = txtReportWorkerMonthTo.getMonth() + 1;
        int yearFrom = txtReportWorkerYear.getYear();
        int yearTo = txtReportWorkerYearTo.getYear();

        if (cbxReportWorkers.getSelectedIndex() == 0) {
            JOptionPane.showMessageDialog(null, "Silih Pilih Pekerja", "Kesilapan!", JOptionPane.ERROR_MESSAGE);
            return;
        } else if (monthTo < monthFrom && yearTo <= yearFrom) {
            JOptionPane.showMessageDialog(null, "Terdapat Kesilapan Tempoh Tarikh", "Kesilapan!", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Worker selected = workers.get(cbxReportWorkers.getSelectedIndex());

        JFileChooser dialog = new JFileChooser();
        dialog.setFileFilter(new ExtensionFileFilter("Microsoft Excel", "xls"));

        if (dialog.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) {
            return;
        }

        File file = dialog.getSelectedFile();

        String filename = file.getPath();

        if (!filename.endsWith(".xls")) {
            filename += ".xls";
        }

        if (new File(filename).exists()) {
            if (JOptionPane.showConfirmDialog(null, "Overwrite the existing file?", "", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) != JOptionPane.YES_OPTION) {
                return;
            }
        }

        int type = WorkerReportFrame.FULL;
        ArrayList<WorkerReport> reports = getWorkerReports();

        if (rbtnWorkerSaving.isSelected()) {
            type = WorkerReportFrame.SAVING;
        } else if (rbtnWorkerMonthlyIncome.isSelected()) {
            type = WorkerReportFrame.INCOME;
        }

        int index = 0;
        int pos = 0;

        Workbook workbook = new HSSFWorkbook();
        Sheet sheet = (Sheet) workbook.createSheet("Laporan");

        Row header = sheet.createRow(pos);

        Cell cell = header.createCell(index++);
        cell.setCellValue("Tempoh");

        if (type == WorkerReportFrame.INCOME || type == WorkerReportFrame.FULL) {
            cell = header.createCell(index++);
            cell.setCellValue("Gaji");
            cell = header.createCell(index++);
            cell.setCellValue("Pinjaman");
            cell = header.createCell(index++);
            cell.setCellValue("Baki");
            cell = header.createCell(index++);
            cell.setCellValue("Bayaran Gaji");
        }

        if (type == WorkerReportFrame.SAVING || type == WorkerReportFrame.FULL) {
            cell = header.createCell(index++);
            cell.setCellValue("Simpanan +");
            cell = header.createCell(index++);
            cell.setCellValue("Simpanan -");
            cell = header.createCell(index++);
            cell.setCellValue("Simpanana Tetap");
        }

        pos++;
        double totalSalary = 0.0, totalLoan = 0.0, totalBalance = 0.0, totalSaving = 0.0, totalPayment = 0.0, totalSavingBalance = 0.0, totalWithdraw = 0.0;

        for (WorkerReport report : reports) {
            Row row = sheet.createRow(pos++);

            index = 0;
            totalSalary += report.getSalary();
            totalLoan += report.getLoan();
            totalBalance += report.getBalance();
            totalPayment += report.getPayment();
            totalWithdraw += report.getWithdraw();
            totalSavingBalance += report.getSavingBalance();
            totalSaving += report.getSaving();

            cell = row.createCell(index++);
            cell.setCellValue(report.getMonth() + "/" + report.getYear());

            if (type == WorkerReportFrame.INCOME || type == WorkerReportFrame.FULL) {
                cell = row.createCell(index++);
                cell.setCellValue(Common.currency(report.getSalary()));
                cell = row.createCell(index++);
                cell.setCellValue(Common.currency(report.getLoan()));
                cell = row.createCell(index++);
                cell.setCellValue(Common.currency(report.getBalance()));
                cell = row.createCell(index++);
                cell.setCellValue(Common.currency(report.getPayment()));
            }

            if (type == WorkerReportFrame.SAVING || type == WorkerReportFrame.FULL) {
                cell = row.createCell(index++);
                cell.setCellValue(Common.currency(report.getSaving()));
                cell = row.createCell(index++);
                cell.setCellValue(Common.currency(report.getWithdraw()));
                cell = row.createCell(index++);
                cell.setCellValue(Common.currency(report.getSavingBalance()));
            }
        }

        index = 0;
        Row footer = sheet.createRow(pos++);
        cell = footer.createCell(index++);
        cell.setCellValue("Jumlah");

        if (type == WorkerReportFrame.INCOME || type == WorkerReportFrame.FULL) {
            cell = footer.createCell(index++);
            cell.setCellValue(Common.currency(totalSalary));
            cell = footer.createCell(index++);
            cell.setCellValue(Common.currency(totalLoan));
            cell = footer.createCell(index++);
            cell.setCellValue(Common.currency(totalBalance));
            cell = footer.createCell(index++);
            cell.setCellValue(Common.currency(totalPayment));
        }

        if (type == WorkerReportFrame.SAVING || type == WorkerReportFrame.FULL) {
            cell = footer.createCell(index++);
            cell.setCellValue(Common.currency(totalSaving));
            cell = footer.createCell(index++);
            cell.setCellValue(Common.currency(totalWithdraw));
            cell = footer.createCell(index++);
            cell.setCellValue(Common.currency(totalSavingBalance));
        }


        FileOutputStream out = null;

        try {
            out = new FileOutputStream(filename);
            workbook.write(out);
            out.close();
            JOptionPane.showMessageDialog(null, "Report generated to " + filename, "", JOptionPane.INFORMATION_MESSAGE);
        } catch (FileNotFoundException ex) {
            JOptionPane.showMessageDialog(null, "The process cannot access the file because it is being used by another process", "Error", JOptionPane.ERROR_MESSAGE);
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                out.close();
            } catch (IOException ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_btnReportWorkerExportActionPerformed

    private void btnReportWorkerGenerateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnReportWorkerGenerateActionPerformed
        int monthFrom = txtReportWorkerMonth.getMonth() + 1;
        int monthTo = txtReportWorkerMonthTo.getMonth() + 1;
        int yearFrom = txtReportWorkerYear.getYear();
        int yearTo = txtReportWorkerYearTo.getYear();

        if (cbxReportWorkers.getSelectedIndex() == 0) {
            JOptionPane.showMessageDialog(null, "Silih Pilih Pekerja", "Kesilapan!",
                    JOptionPane.ERROR_MESSAGE);
            return;
        } else if (monthTo < monthFrom && yearTo <= yearFrom) {
            JOptionPane.showMessageDialog(null, "Terdapat Kesilapan Tempoh Tarikh", "Kesilapan!",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        Worker selected = workers.get(cbxReportWorkers.getSelectedIndex() - 1);

        int type = WorkerReportFrame.FULL;
        ArrayList<WorkerReport> reports = getWorkerReports();

        if (rbtnWorkerSaving.isSelected()) {
            type = WorkerReportFrame.SAVING;
        } else if (rbtnWorkerMonthlyIncome.isSelected()) {
            type = WorkerReportFrame.INCOME;
        }

        WorkerReportFrame frame = new WorkerReportFrame(type, selected, reports);
        frame.setVisible(true);
    }//GEN-LAST:event_btnReportWorkerGenerateActionPerformed

    private ArrayList<WorkerReport> getWorkerReports() {
        ArrayList<WorkerReport> reports = new ArrayList<WorkerReport>();
        int monthFrom = txtReportWorkerMonth.getMonth() + 1;
        int monthTo = txtReportWorkerMonthTo.getMonth() + 1;
        int yearFrom = txtReportWorkerYear.getYear();
        int yearTo = txtReportWorkerYearTo.getYear();
        int month = monthFrom, year = yearFrom;

        Worker selected = cbxReportWorkers.getSelectedIndex() == 0 ? new Worker() : workers.get(cbxReportWorkers.getSelectedIndex() - 1);

        while (year < yearTo || (month <= monthTo && year <= yearTo)) {
            double salary = 0.0, loan = 0.0, saving = 0.0, withdraw = 0.0, payment = 0.0;

            try {
                String query = "SELECT ";
                query += "(SELECT SUM(amount) AS amount FROM workerRecord WHERE worker_id = " + selected.getId() + " AND strftime(\"%m\", date) = \"" + (month > 9 ? month : "0" + month) + "\" AND strftime(\"%Y\", date) = \"" + year + "\" AND type = " + WorkerRecord.INCOME + ") AS salary, ";
                query += "(SELECT SUM(amount) AS amount FROM workerRecord WHERE worker_id = " + selected.getId() + " AND strftime(\"%m\", date) = \"" + (month > 9 ? month : "0" + month) + "\" AND strftime(\"%Y\", date) = \"" + year + "\" AND type = " + WorkerRecord.LOAN + ") AS loan, ";
                query += "(SELECT SUM(amount) AS amount FROM workerRecord WHERE worker_id = " + selected.getId() + " AND strftime(\"%m\", date) = \"" + (month > 9 ? month : "0" + month) + "\" AND strftime(\"%Y\", date) = \"" + year + "\" AND type = " + WorkerRecord.SAVING + ") AS saving, ";
                query += "(SELECT SUM(amount) AS amount FROM workerRecord WHERE worker_id = " + selected.getId() + " AND strftime(\"%m\", date) = \"" + (month > 9 ? month : "0" + month) + "\" AND strftime(\"%Y\", date) = \"" + year + "\" AND type = " + WorkerRecord.PAYMENT + ") AS payment, ";
                query += "(SELECT SUM(amount) AS amount FROM workerRecord WHERE worker_id = " + selected.getId() + " AND strftime(\"%m\", date) = \"" + (month > 9 ? month : "0" + month) + "\" AND strftime(\"%Y\", date) = \"" + year + "\" AND type = " + WorkerRecord.WITHDRAW + ") AS withdraw";
                // salary
                ResultSet rs = Database.instance().execute(query);
                rs.next();

                salary = rs.getDouble("salary");
                loan = rs.getDouble("loan");
                saving = rs.getDouble("saving");
                withdraw = rs.getDouble("withdraw");
                payment = rs.getDouble("payment");

                rs.close();

            } catch (SQLException ex) {
                System.err.println(ex.getMessage());
            }

            reports.add(new WorkerReport(month, year, salary, loan, saving, withdraw, payment));

            month++;

            if (month > 12) {
                month = 1;
                year++;
            }
        }

        return reports;
    }

    private void btnPasswordChangeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPasswordChangeActionPerformed
        String password = "";
        char[] passwords = txtPassword.getPassword(), confirms = txtPasswordConfirm.getPassword();


        if (!Arrays.equals(passwords, confirms)) {
            JOptionPane.showMessageDialog(null, "Kata Laluan tidak betul.", "Kesilapan!", JOptionPane.ERROR_MESSAGE);
            return;
        }

        for (Character ch : passwords) {
            password += ch;
        }

        password = Common.md5(password.getBytes());
        String query = "UPDATE accesses SET access_key = ? WHERE id = " + Application.id;
        PreparedStatement ps = Database.instance().createPreparedStatement(query);

        try {
            ps.setString(1, password);
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }

        if (Database.instance().update(ps)) {
            JOptionPane.showMessageDialog(null, "Kata Laluan berjaya ditukar.", "Berjaya!", JOptionPane.INFORMATION_MESSAGE);
            txtPassword.setText("");
            txtPasswordConfirm.setText("");
        } else {
            JOptionPane.showMessageDialog(null, "Kata Laluan tidak dapat ditukar.", "Kesilapan!", JOptionPane.ERROR_MESSAGE);
        }

    }//GEN-LAST:event_btnPasswordChangeActionPerformed

    private void txtPasswordActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtPasswordActionPerformed
        btnPasswordChangeActionPerformed(evt);
    }//GEN-LAST:event_txtPasswordActionPerformed

    private void txtPasswordConfirmActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtPasswordConfirmActionPerformed
        btnPasswordChangeActionPerformed(evt);
    }//GEN-LAST:event_txtPasswordConfirmActionPerformed

    private void btnSavingEndActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSavingEndActionPerformed
        System.exit(0);
    }//GEN-LAST:event_btnSavingEndActionPerformed

    private void btnPayEndActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPayEndActionPerformed
        System.exit(0);
    }//GEN-LAST:event_btnPayEndActionPerformed

    private void btnMonthlyReportEndActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMonthlyReportEndActionPerformed
        System.exit(0);
    }//GEN-LAST:event_btnMonthlyReportEndActionPerformed

    private void btnReportWorkerEndActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnReportWorkerEndActionPerformed
        System.exit(0);
    }//GEN-LAST:event_btnReportWorkerEndActionPerformed

    private void btnProfileWorkerEndActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnProfileWorkerEndActionPerformed
        System.exit(0);
    }//GEN-LAST:event_btnProfileWorkerEndActionPerformed

    private void btnProfileClientEndActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnProfileClientEndActionPerformed
        System.exit(0);
    }//GEN-LAST:event_btnProfileClientEndActionPerformed

    private void btnSavingSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSavingSearchActionPerformed
        DefaultTableModel savingTableModel = (DefaultTableModel) tblSaving.getModel();

        int rowCount = tblSaving.getRowCount();

        for (int i = 0; i < rowCount; i++) {
            savingTableModel.removeRow(0);
        }

        loaded_saving_ids.clear();

        Worker selected = cbxSavingWorkers.getSelectedIndex() == 0 ? null : workers.get(cbxSavingWorkers.getSelectedIndex() - 1);

        if (selected == null) {
            JOptionPane.showMessageDialog(null, "Sila pilih Pekerja.", "Kesilapan!", JOptionPane.ERROR_MESSAGE);
            return;
        } else if (txtSavingDateFrom.getDate() == null || txtSavingDateTo.getDate() == null) {
            JOptionPane.showMessageDialog(null, "Sila pilih tempoh tarikh.", "Kesilapan!", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String query = "SELECT * FROM workerRecord WHERE type IN (5, 3) AND worker_id = " + selected.getId() + " ";

        if (txtSavingDateFrom.getDate() != null) {
            Calendar calender = Calendar.getInstance();
            calender.setTime(txtSavingDateFrom.getDate());
            query += "AND date >= \"" + Common.renderSQLDate(calender) + "\" ";
        }

        if (txtSavingDateTo.getDate() != null) {
            Calendar calender = Calendar.getInstance();
            calender.setTime(txtSavingDateTo.getDate());
            query += "AND date <= \"" + Common.renderSQLDate(calender) + "\" ";
        }

        ResultSet results = Database.instance().execute(query);
        int counter = 0;
        double balance = 0.0;

        try {
            while (results.next()) {
                WorkerRecord record = new WorkerRecord(results.getInt("id"), results.getInt("worker_id"),
                        results.getInt("type"), results.getDouble("amount"),
                        results.getString("description"),
                        Common.convertStringToDate(results.getString("date")));
                balance += record.getAmount();

                loaded_saving_ids.add(record.getId());

                Object[] objects = new Object[]{
                    new Integer(counter + 1),
                    new String(Common.renderDisplayDate(record.getDate())),
                    new String(record.getDescription()),
                    new String(Common.currency(record.getAmount() > 0 ? record.getAmount() : 0.00)),
                    new String(Common.currency(record.getAmount() < 0 ? record.getAmount() : 0.00)),
                    new String(Common.currency(balance))
                };

                savingTableModel.addRow(objects);
                counter++;
            }
            results.close();
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }

        txtSavingCurrentSaving.setText(Common.currency(this.getWorkerCurrentSaving(selected.getId())));
    }//GEN-LAST:event_btnSavingSearchActionPerformed

    private void btnPaymentSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPaymentSearchActionPerformed
        DefaultTableModel paymentTableModel = (DefaultTableModel) tblPay.getModel();

        int rowCount = tblPay.getRowCount();

        for (int i = 0; i < rowCount; i++) {
            paymentTableModel.removeRow(0);
        }

        Worker selected = cbxPaymentWorkers.getSelectedIndex() == 0 ? null : workers.get(cbxPaymentWorkers.getSelectedIndex() - 1);

        if (selected == null) {
            JOptionPane.showMessageDialog(null, "Sila pilih Pekerja", "Kesilapan!", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int monthFrom = txtPaymentMonth.getMonth() + 1;
        int monthTo = txtPaymentMonthTo.getMonth() + 1;
        int yearFrom = txtPaymentYear.getYear();
        int yearTo = txtPaymentYearTo.getYear();
        int month = monthFrom, year = yearFrom;

        int counter = 1;

        double salary = 0.0, loan = 0.0, saving = 0.0, withdraw = 0.0, payment = 0.0;

        try {
            String query = "SELECT amount, type, date FROM workerRecord WHERE worker_id = "
                    + selected.getId() + " AND date >= \"" + yearFrom + "-"
                    + (monthFrom > 9 ? monthFrom : "0" + monthFrom)
                    + "01\" AND date <= \"" + yearTo
                    + "-" + (monthTo > 9 ? monthTo : "0" + monthTo)
                    + "-32\" AND type IN (" + WorkerRecord.INCOME + ","
                    + WorkerRecord.LOAN + "," + WorkerRecord.SAVING + ","
                    + WorkerRecord.PAYMENT + ") ORDER BY date";

//            System.out.println(query);
            ResultSet rs = Database.instance().execute(query);
            while (rs.next()) {
                int type = rs.getInt("type");


                salary = 0.0;
                saving = 0.0;
                payment = 0.0;

                if (type == WorkerRecord.INCOME || type == WorkerRecord.LOAN) {
                    salary = rs.getDouble("amount");
                } else if (type == WorkerRecord.SAVING) {
                    saving = rs.getDouble("amount");
                } else if (type == WorkerRecord.PAYMENT) {
                    payment = rs.getDouble("amount");
                }

                Object[] objects = new Object[]{
                    new Integer(counter++),
                    new String(Common.renderDisplayDate(Common.convertStringToDate(rs.getString("date")))),
                    new String("Gaji"),
                    new String(Common.currency(salary)),
                    new String(Common.currency(payment)),
                    new String(Common.currency(saving))
                };

                paymentTableModel.addRow(objects);

            }

            rs.close();

        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }
    }//GEN-LAST:event_btnPaymentSearchActionPerformed

    private void cbxClientsItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cbxClientsItemStateChanged
        if (cbxClients.getSelectedIndex() == 0 || cbxClients.getItemCount() - 1 != customers_all.size()) {
            txtProfileClientStatus.setText("");
            _current_client_id = 0;
            return;
        }

        Customer client = customers_all.get(cbxClients.getSelectedIndex() - 1);
        txtProfileClientStatus.setText(client.getStatus() ? "Aktif" : "Tidak Aktif");
        _current_client_id = client.getId();
    }//GEN-LAST:event_cbxClientsItemStateChanged

    private void cbxWorkersItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cbxWorkersItemStateChanged
        if (cbxWorkers.getSelectedIndex() == 0 || cbxWorkers.getItemCount() - 1 != workers_all.size()) {
            txtProfileWorkerRegisterDay.setText("");
            txtProfileWorkerRegisterMonth.setText("");
            txtProfileWorkerRegisterYear.setText("");
            txtProfileWorkerReturnDay.setText("");
            txtProfileWorkerReturnMonth.setText("");
            txtProfileWorkerReturnYear.setText("");
            txtProfileWorkerStatus.setText("");
            this._current_worker_id = 0;
            return;
        }

        Worker worker = workers_all.get(cbxWorkers.getSelectedIndex() - 1);

        Calendar register_date = Calendar.getInstance();
        register_date.setTime(worker.getRegisterDate());
        txtProfileWorkerRegisterDay.setText("" + register_date.get(Calendar.DAY_OF_MONTH));
        txtProfileWorkerRegisterMonth.setText("" + (register_date.get(Calendar.MONTH) + 1));
        txtProfileWorkerRegisterYear.setText("" + register_date.get(Calendar.YEAR));
        Calendar return_date = Calendar.getInstance();
        return_date.setTime(worker.getReturnDate());
        txtProfileWorkerReturnDay.setText("" + return_date.get(Calendar.DAY_OF_MONTH));
        txtProfileWorkerReturnMonth.setText("" + (return_date.get(Calendar.MONTH) + 1));
        txtProfileWorkerReturnYear.setText("" + return_date.get(Calendar.YEAR));
        txtProfileWorkerStatus.setText(worker.getStatus() ? "Aktif" : "Tidak Aktif");
        this._current_worker_id = worker.getId();

        txtProfileWorkerCurrentSaving.setText(Common.currency(this.getWorkerCurrentSaving(worker.getId())));
    }//GEN-LAST:event_cbxWorkersItemStateChanged

    private void btnSavingCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSavingCancelActionPerformed
        if (tblSaving.getSelectedRowCount() == 0) {
            JOptionPane.showMessageDialog(null,
                    "Sila pilih dari tabel daftar di atas", "Kesilapan",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (JOptionPane.showConfirmDialog(null,
                "Anda memastikan batalkan sebanyak "
                + tblSaving.getSelectedRowCount()
                + " rekod dari tabel daftar di atas?", "",
                JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
            return;
        }

        String id = "";
        for (Integer index : tblSaving.getSelectedRows()) {
            id += "," + loaded_saving_ids.get(index);
        }

        id = id.substring(1, id.length());

        String query = "DELETE FROM workerRecord WHERE id IN (" + id + ")";

        if (Database.instance().update(query)) {
            JOptionPane.showMessageDialog(null,
                    "Rekod telah dibatal dari sistem", "",
                    JOptionPane.INFORMATION_MESSAGE);
            btnSavingSearchActionPerformed(evt);
        } else {
            String msg = "Tidak dapat membatalkan rekod dari sistem.";
            JOptionPane.showMessageDialog(null, msg,
                    "", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btnSavingCancelActionPerformed

    private void btnPayCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPayCancelActionPerformed
        if (tblPay.getSelectedColumnCount() == 0) {
            JOptionPane.showMessageDialog(null, "Sila pilih dari tabel daftar di atas",
                    "Kesilapan", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (JOptionPane.showConfirmDialog(null, "Anda memastikan batalkan sebanyak "
                + tblPay.getSelectedRowCount() + " rekod dari tabel daftar di atas?",
                "", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
            return;
        }

        ArrayList<Integer[]> selected = new ArrayList<Integer[]>();
        for (Integer index : tblPay.getSelectedRows()) {
            String date = tblPay.getValueAt(index, 1).toString();
            String[] dates = date.split("/");
            selected.add(new Integer[]{Integer.parseInt(dates[0]), Integer.parseInt(dates[1])});
        }

        for (Integer[] dates : selected) {
            String query = "DELETE FROM workerRecord WHERE strftime(\"%m\", date) = \"" + (dates[0] > 9 ? dates[0] : "0" + dates[0]) + "\" AND strftime(\"%Y\", date) = \"" + dates[1] + "\" AND type = " + WorkerRecord.PAYMENT;
            if (!Database.instance().update(query)) {
                JOptionPane.showMessageDialog(null, "Tidak dapat membatalkan rekod dari sistem. Sila cuba sebentar lagi.", "", JOptionPane.ERROR_MESSAGE);
                btnPaymentSearchActionPerformed(evt);
                return;
            }
        }

        JOptionPane.showMessageDialog(null, "Rekod telatblTransactionInvolvedWorkers.getColumnModel().getColumn(0).setPreferredWidth(20);h dibatal dari sistem", "", JOptionPane.INFORMATION_MESSAGE);
        btnPaymentSearchActionPerformed(evt);
    }//GEN-LAST:event_btnPayCancelActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        jTabbedPane1.setSelectedIndex(1);
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        jTabbedPane1.setSelectedIndex(2);
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        jTabbedPane1.setSelectedIndex(4);
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        jTabbedPane1.setSelectedIndex(6);
    }//GEN-LAST:event_jButton4ActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        jTabbedPane1.setSelectedIndex(7);
    }//GEN-LAST:event_jButton5ActionPerformed

    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
        jTabbedPane1.setSelectedIndex(8);
    }//GEN-LAST:event_jButton6ActionPerformed

    private void jButton7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton7ActionPerformed
        System.exit(0);
    }//GEN-LAST:event_jButton7ActionPerformed

    private void btnTransactionSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTransactionSaveActionPerformed
        DefaultTableModel tableModel = (DefaultTableModel) tblTransactionList.getModel();

        int rowCount = tblTransactionList.getRowCount();
        for (int i = 0; i < rowCount; i++) {
            tableModel.removeRow(0);
        }

        loaded_transaction_ids.clear();

        if (txtTransactionListFrom.getDate() == null || txtTransactionListTo.getDate() == null) {
            JOptionPane.showMessageDialog(null, "Sila pilih tempoh tarikh.", "Kesilapan!", JOptionPane.ERROR_MESSAGE);
            return;
        }

        tableModel = new DefaultTableModel(
                new Object[][]{},
                new String[]{
            "", "Tarikh", "Name Pelanggan", "Keterangan", "Berat KG", "Harga Diterima Seton", "Jumlah Diterima", "Upah Kerja", "Jumlah Gaji", "Kiraan Asing", "Pekerja Terlibat", "Jumlah Pinjaman"
        });

        tblTransactionList.setModel(tableModel);

        tblTransactionList.getColumnModel().getColumn(0).setPreferredWidth(30);
        tblTransactionList.getColumnModel().getColumn(1).setPreferredWidth(60);
        tblTransactionList.getColumnModel().getColumn(2).setPreferredWidth(120);
        tblTransactionList.getColumnModel().getColumn(3).setPreferredWidth(250);

        String query = "SELECT * FROM transactions WHERE date >= '"
                + Common.renderSQLDate(txtTransactionListFrom.getDate())
                + "' AND date <= '"
                + Common.renderSQLDate(txtTransactionListTo.getDate())
                + "' AND wages > 0";

        if (cbxTransactionListClients.getSelectedIndex() > 0) {
            query += " AND customer_id = "
                    + customers.get(cbxTransactionListClients.getSelectedIndex() - 1).getId();
        }

        query += " ORDER BY date DESC";

        System.out.println(query);

        ResultSet rs = Database.instance().execute(query);

        int counter = 1;
        try {
            while (rs.next()) {
                Transaction transaction = new Transaction(rs.getInt("id"), rs.getInt("customer_id"), rs.getInt("type"), rs.getDouble("weight"), rs.getDouble("price_per_ton"), rs.getDouble("wages"), rs.getDouble("wages_tax"), rs.getDouble("kiraan_asing"), rs.getDouble("loan_amount"), new ArrayList<Worker>(), Common.convertStringToDate(rs.getString("date")), rs.getString("description"), rs.getString("normalized_worker_id"));
                ArrayList<Worker> involved_workers = new ArrayList<Worker>();

                String[] ids = rs.getString("normalized_worker_id").split(",");

                for (String id : ids) {
                    if (workers_hash.containsKey(Integer.parseInt(id))) {
                        involved_workers.add((Worker) workers_hash.get(Integer.parseInt(id)));
                    }
                }

                transaction.setWorkers(involved_workers);

                Object[] objects = null;

                objects = new Object[]{
                    new Integer(counter++),
                    new String(Common.renderDisplayDate(transaction.getDate())),
                    new String(transaction.getCustomerID() == 0 ? "" : transaction.getCustomer().getCode() + " - " + transaction.getCustomer().getName()),
                    new String(transaction.getDescription()),
                    new String(transaction.getWeight() == 0.0 ? "" : "" + transaction.getWeight()),
                    new String(transaction.getPricePerTon() == 0.0 ? "" : Common.currency(transaction.getPricePerTon())),
                    new String(transaction.getTotal() == 0.0 ? "" : Common.currency(transaction.getTotal())),
                    new String(transaction.getWages() == 0.0 ? "" : Common.currency(transaction.getWages())),
                    new String(transaction.getTotalSalary() == 0.0 ? "" : Common.currency(transaction.getTotalSalary())),
                    new String(transaction.getKiraanAsing() == 0.0 ? "" : Common.currency(transaction.getKiraanAsing())),
                    new String(transaction.getCompiledWorkerCodes()),
                    new String(transaction.getLoanAmount() == 0.0 ? "" : Common.currency(transaction.getLoanAmount()))
                };

                tableModel.addRow(objects);
                loaded_transaction_ids.add(rs.getInt("id"));
            }
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }

    }//GEN-LAST:event_btnTransactionSaveActionPerformed

    private void btnTransactionListEndActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTransactionListEndActionPerformed
        System.exit(0);
    }//GEN-LAST:event_btnTransactionListEndActionPerformed

    private void btnTransactionListDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTransactionListDeleteActionPerformed
        if (tblTransactionList.getSelectedColumnCount() == 0) {
            JOptionPane.showMessageDialog(null, "Sila pilih dari tabel daftar di atas",
                    "Kesilapan", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (JOptionPane.showConfirmDialog(null, "Anda memastikan batalkan sebanyak "
                + tblTransactionList.getSelectedRowCount()
                + " rekod dari tabel daftar di atas?", "",
                JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
            return;
        }

        String id = "";
        for (Integer index : tblTransactionList.getSelectedRows()) {
            id += "," + loaded_transaction_ids.get(index);
        }

        id = id.substring(1, id.length());

        boolean success = true;
        Database.instance().begin();
        String query = "DELETE FROM transactions WHERE id IN (" + id + ")";
        success = success && Database.instance().update(query);
        query = "DELETE FROM workerRecord WHERE transaction_id IN (" + id + ")";
        success = success && Database.instance().update(query);
        query = "DELETE FROM transaction_workers WHERE transaction_id IN (" + id + ")";
        success = success && Database.instance().update(query);


        if (success && Database.instance().commit()) {
            JOptionPane.showMessageDialog(null, "Rekod telah dibatal dari sistem",
                    "", JOptionPane.INFORMATION_MESSAGE);
            btnTransactionSaveActionPerformed(evt);
        } else {
            Database.instance().rollback();
            JOptionPane.showMessageDialog(null,
                    "Tidak dapat membatalkan rekod dari sistem. Sila cuba sebentar lagi.",
                    "", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btnTransactionListDeleteActionPerformed

    private void jButton8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton8ActionPerformed
        jTabbedPane1.setSelectedIndex(7);
    }//GEN-LAST:event_jButton8ActionPerformed

    private void btnTransactionSave1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTransactionSave1ActionPerformed
        DefaultTableModel tableModel = (DefaultTableModel) tblTransactionList1.getModel();

        int rowCount = tblTransactionList1.getRowCount();
        for (int i = 0; i < rowCount; i++) {
            tableModel.removeRow(0);
        }

        loaded_transaction_tax_ids.clear();

        if (txtTransactionListFrom1.getDate() == null || txtTransactionListTo1.getDate() == null) {
            JOptionPane.showMessageDialog(null, "Sila pilih tempoh tarikh.", "Kesilapan!", JOptionPane.ERROR_MESSAGE);
            return;
        }

        tableModel = new DefaultTableModel(
                new Object[][]{},
                new String[]{
            "", "Tarikh", "Name Pelanggan", "Keterangan", "Berat KG", "Harga Diterima Seton", "Jumlah Diterima", "Upah Kerja", "Jumlah Gaji", "Kiraan Asing", "Pekerja Terlibat", "Jumlah Pinjaman"
        });

        tblTransactionList1.setModel(tableModel);

        tblTransactionList1.getColumnModel().getColumn(0).setPreferredWidth(30);
        tblTransactionList1.getColumnModel().getColumn(1).setPreferredWidth(60);
        tblTransactionList1.getColumnModel().getColumn(2).setPreferredWidth(120);
        tblTransactionList1.getColumnModel().getColumn(3).setPreferredWidth(250);

        String query = "SELECT * FROM transactions WHERE date >= '"
                + Common.renderSQLDate(txtTransactionListFrom1.getDate())
                + "' AND date <= '"
                + Common.renderSQLDate(txtTransactionListTo1.getDate())
                + "' AND wages_tax > 0";

        if (cbxTransactionListClients1.getSelectedIndex() > 0) {
            query += " AND customer_id = " + customers.get(cbxTransactionListClients1.getSelectedIndex() - 1).getId();
        }

        ResultSet rs = Database.instance().execute(query);

        int counter = 1;
        try {
            while (rs.next()) {
                Transaction transaction = new Transaction(rs.getInt("id"), rs.getInt("customer_id"), rs.getInt("type"), rs.getDouble("weight"), rs.getDouble("price_per_ton"), rs.getDouble("wages"), rs.getDouble("wages_tax"), rs.getDouble("kiraan_asing"), rs.getDouble("loan_amount"), new ArrayList<Worker>(), Common.convertStringToDate(rs.getString("date")), rs.getString("description"), rs.getString("normalized_worker_id"));
                ArrayList<Worker> involved_workers = new ArrayList<Worker>();

                String[] ids = rs.getString("normalized_worker_id").split(",");

                for (String id : ids) {
                    if (workers_hash.containsKey(Integer.parseInt(id))) {
                        involved_workers.add((Worker) workers_hash.get(Integer.parseInt(id)));
                    }
                }

                transaction.setWorkers(involved_workers);

                Object[] objects = null;

                objects = new Object[]{
                    new Integer(counter++),
                    new String(Common.renderDisplayDate(transaction.getDate())),
                    new String(transaction.getCustomerID() == 0 ? "" : transaction.getCustomer().getCode() + " - " + transaction.getCustomer().getName()),
                    new String(transaction.getDescription()),
                    new String(transaction.getWeight() == 0.0 ? "" : "" + transaction.getWeight()),
                    new String(transaction.getPricePerTon() == 0.0 ? "" : Common.currency(transaction.getPricePerTon())),
                    new String(transaction.getTotal() == 0.0 ? "" : Common.currency(transaction.getTotal())),
                    new String(transaction.getWagesTax() == 0.0 ? "" : Common.currency(transaction.getWagesTax())),
                    new String(transaction.getTotalSalary() == 0.0 ? "" : Common.currency(transaction.getTotalSalary())),
                    new String(transaction.getKiraanAsing() == 0.0 ? "" : Common.currency(transaction.getKiraanAsing())),
                    new String(transaction.getCompiledWorkerCodes()),
                    new String(transaction.getLoanAmount() == 0.0 ? "" : Common.currency(transaction.getLoanAmount()))
                };

                tableModel.addRow(objects);
                loaded_transaction_tax_ids.add(rs.getInt("id"));
            }
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }
    }//GEN-LAST:event_btnTransactionSave1ActionPerformed

    private void btnTransactionListEnd1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTransactionListEnd1ActionPerformed
        System.exit(0);
    }//GEN-LAST:event_btnTransactionListEnd1ActionPerformed

    private void btnTransactionListDelete1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTransactionListDelete1ActionPerformed
        if (tblTransactionList.getSelectedColumnCount() == 0) {
            JOptionPane.showMessageDialog(null, "Sila pilih dari tabel daftar di atas", "Kesilapan", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (JOptionPane.showConfirmDialog(null, "Anda memastikan batalkan sebanyak " + tblTransactionList.getSelectedRowCount() + " rekod dari tabel daftar di atas?", "", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
            return;
        }

        String id = "";
        for (Integer index : tblTransactionList.getSelectedRows()) {
            id += "," + loaded_transaction_tax_ids.get(index);
        }

        id = id.substring(1, id.length());

        boolean success = true;
        Database.instance().begin();
        String query = "DELETE FROM transactions WHERE id IN (" + id + ")";
        success = success && Database.instance().update(query);
        query = "DELETE FROM workerRecord WHERE transaction_id IN (" + id + ")";
        success = success && Database.instance().update(query);
        query = "DELETE FROM transaction_workers WHERE transaction_id IN (" + id + ")";
        success = success && Database.instance().update(query);


        if (success && Database.instance().commit()) {
            JOptionPane.showMessageDialog(null, "Rekod telah dibatal dari sistem",
                    "", JOptionPane.INFORMATION_MESSAGE);
            btnTransactionSaveActionPerformed(evt);
        } else {
            Database.instance().rollback();
            JOptionPane.showMessageDialog(null,
                    "Tidak dapat membatalkan rekod dari sistem. Sila cuba sebentar lagi.",
                    "", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btnTransactionListDelete1ActionPerformed

    private void btnMonthlyReportEnd1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMonthlyReportEnd1ActionPerformed
        System.exit(0);
    }//GEN-LAST:event_btnMonthlyReportEnd1ActionPerformed

    private void btnMonthlyReportPrint1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMonthlyReportPrint1ActionPerformed
        ArrayList<Worker> selected = this.getReportSelectedWorkers1();
        ArrayList<ReportSaving> savings = this.getReportSavings1(selected);
        ArrayList<Transaction> transactions = this.getReportTransasctions1(selected);
        ArrayList<ReportCalculation> calculations = this.getReportCalculations1(selected);
        ArrayList<ReportSalary> salaries = this.getReportSalaries1(selected);
        Hashtable dates = getReportSelectedDateRange1();

        PrinterJob job = PrinterJob.getPrinterJob();

        PageFormat format = job.defaultPage();
        format.setOrientation(PageFormat.LANDSCAPE);

        job.setPrintable(new ReportPrinter1(this, selected, transactions, calculations, savings, salaries, dates), format);

        if (job.printDialog() == true) {
            try {
                HashPrintRequestAttributeSet attr = new HashPrintRequestAttributeSet();
                attr.add(new MediaPrintableArea(0, 0, 210, 297, MediaPrintableArea.MM));
                job.print(attr);
            } catch (PrinterException ex) {
                System.err.println(ex.getMessage());
            }
        }
    }//GEN-LAST:event_btnMonthlyReportPrint1ActionPerformed

    private void btnMonthlyReportExport1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMonthlyReportExport1ActionPerformed
        JFileChooser dialog = new JFileChooser();
        dialog.setFileFilter(new ExtensionFileFilter("Microsoft Excel", "xls"));

        if (dialog.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) {
            return;
        }

        File file = dialog.getSelectedFile();

        String filename = file.getPath();
        if (!filename.endsWith(".xls")) {
            filename += ".xls";
        }

        if (new File(filename).exists()) {
            if (JOptionPane.showConfirmDialog(null, "Overwrite the existing file?", "", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) != JOptionPane.YES_OPTION) {
                return;
            }
        }

        ArrayList<String> columns = this.getReportColumns1();
        ArrayList<Worker> selected = this.getReportSelectedWorkers1();
        ArrayList<ReportCalculation> calculations = this.getReportCalculations1(selected);
        ArrayList<Transaction> transactions = this.getReportTransasctions1(selected);
        ArrayList<ReportSalary> salaries = this.getReportSalaries1(selected);

        int workerCount = selected.size();
        int index = 0;
        int pos = 0;

        for (Worker worker : selected) {
            calculations.add(new ReportCalculation(worker.getId()));
        }

        Workbook workbook = new HSSFWorkbook();
        Sheet sheet = (Sheet) workbook.createSheet("Laporan");

        Row firstHeaderRow = sheet.createRow(0);
        Row secondHeaderRow = sheet.createRow(1);

        // <editor-fold defaultstate="collapsed" desc="render header">
        index = 0;
        for (String column : columns) {
            sheet.addMergedRegion(new CellRangeAddress(0, 1, pos, pos));
            Cell cell = firstHeaderRow.createCell(index);
            cell.setCellValue(column);
            index++;
            pos++;
        }

        for (Worker worker : selected) {
            sheet.addMergedRegion(new CellRangeAddress(0, 0, pos, pos + 2));
            Cell cell = firstHeaderRow.createCell(index);
            cell.setCellValue(worker.getCode() + " " + worker.getName());

            cell = secondHeaderRow.createCell(index);
            cell.setCellValue("Gaji");
            cell = secondHeaderRow.createCell(index + 1);
            cell.setCellValue("Pinjaman");
            cell = secondHeaderRow.createCell(index + 2);
            cell.setCellValue("Baki");
            index += 3;
            pos += 3;
        }
        sheet.createFreezePane(0, 2);
        // </editor-fold>

        // <editor-fold defaultstate="collapsed" desc="render content">
        int rowIndex = 2;
        for (Transaction transaction : transactions) {
            index = 0;

            Row row = sheet.createRow(rowIndex);
            Cell cell = null;

            cell = row.createCell(index);
            if (chkMonthlyReportDate.isSelected()) {
                cell.setCellValue(Common.renderDisplayDate(transaction.getDate()));
                index++;
                cell = row.createCell(index);
            }
            if (chkMonthlyReportClientName.isSelected()) {
                if (transaction.getType() == Transaction.GENERAL) {
                    cell.setCellValue(transaction.getCustomer().getName());
                } else {
                    cell.setCellValue("Pinjaman");
                }
                index++;
                cell = row.createCell(index);
            }
            if (chkMonthlyReportDescription.isSelected()) {
                cell.setCellValue(transaction.getDescription());
                index++;
                cell = row.createCell(index);
            }
            if (chkMonthlyReportWeight.isSelected()) {
                if (transaction.getType() == Transaction.GENERAL) {
                    cell.setCellValue((int) transaction.getWeight());

                }
                index++;
                cell = row.createCell(index);
            }
            if (chkMonthlyReportPricePerTon.isSelected()) {
                if (transaction.getType() == Transaction.GENERAL) {
                    cell.setCellValue(Common.currency(transaction.getPricePerTon()));

                }
                index++;
                cell = row.createCell(index);
            }
            if (chkMonthlyReportTotalReceived.isSelected()) {
                if (transaction.getType() == Transaction.GENERAL) {
                    cell.setCellValue(Common.currency(transaction.getTotal()));

                }
                index++;
                cell = row.createCell(index);
            }
            if (chkMonthlyReportWages.isSelected()) {
                if (transaction.getType() == Transaction.GENERAL) {
                    cell.setCellValue(Common.currency(transaction.getWages()));

                }
                index++;
                cell = row.createCell(index);
            }
            if (chkMonthlyReportSalary.isSelected()) {
                if (transaction.getType() == Transaction.GENERAL) {
                    cell.setCellValue(Common.currency(transaction.getTotalSalary()));
                }
                index++;
                cell = row.createCell(index);
            }
            if (chkMonthlyReportBalance.isSelected()) {
                if (transaction.getType() == Transaction.GENERAL) {
                    cell.setCellValue(Common.currency(transaction.getBalance()));

                }
                index++;
                cell = row.createCell(index);
            }
            if (chkMonthlyReportKiraanAsing.isSelected()) {
                if (transaction.getType() == Transaction.GENERAL) {
                    cell.setCellValue(Common.currency(transaction.getKiraanAsing()));

                }
                index++;
                cell = row.createCell(index);
            }

            String[] workerIds = transaction.getNormalizedWorkerID().split(",");
            for (int i = 0; i < workerCount; i++) {
                if (Common.inArray(workerIds, selected.get(i).getId())) {
                    if (transaction.getType() == Transaction.GENERAL) {
                        calculations.get(i).setSalary(transaction.getWagePerWorker());
                        cell = row.createCell(index++);
                        cell.setCellValue(Common.currency(transaction.getWagePerWorker()));
                        cell = row.createCell(index++);
                        cell = row.createCell(index++);
                        cell.setCellValue(Common.currency(calculations.get(i).getBalance()));
                    } else {
                        calculations.get(i).setLoan(transaction.getLoanAmount());
                        cell = row.createCell(index++);
                        cell = row.createCell(index++);
                        cell.setCellValue(transaction.getLoanAmount());
                        cell = row.createCell(index++);
                        cell.setCellValue(Common.currency(calculations.get(i).getBalance()));
                    }
                } else {
                    cell = row.createCell(index++);
                    cell = row.createCell(index++);
                    cell = row.createCell(index++);
                }
            }

            rowIndex++;
        }
        // </editor-fold>

        // <editor-fold defaultstate="collapsed" desc="render summary">
        Row summaryRow = sheet.createRow(rowIndex);
        index = columns.size();

        sheet.addMergedRegion(new CellRangeAddress(rowIndex, rowIndex, 0, columns.size() - 1));

        for (int i = 0; i < workerCount; i++) {
            ReportCalculation calculation = calculations.get(i);
            Cell cell = summaryRow.createCell(index++);
            cell.setCellValue(Common.currency(calculation.getSalary()));
            cell = summaryRow.createCell(index++);
            cell.setCellValue(Common.currency(calculation.getLoan()));
            cell = summaryRow.createCell(index++);
            cell.setCellValue(Common.currency(calculation.getBalance()));
        }
        // </editor-fold>

        rowIndex++;

        if (chkMonthlyReportSalaryPayment.isSelected()) {
            for (ReportSalary salary : salaries) {
                index = 0;
                Row row = sheet.createRow(rowIndex);
                Cell cell = null;

                if (chkMonthlyReportDate.isSelected()) {
                    cell = row.createCell(index++);
                    cell.setCellValue(Common.renderDisplayDate(salary.getDate()));
                }

                sheet.addMergedRegion(new CellRangeAddress(rowIndex, rowIndex, index, columns.size() - 1));

                cell = row.createCell(index);
                cell.setCellValue("Bayaran Gaji");

                index = columns.size();

                for (int i = 0; i < workerCount; i++) {
                    double salaryValue = salary.getWorkerSalary(selected.get(i).getId());
                    cell = row.createCell(index++);
                    sheet.addMergedRegion(new CellRangeAddress(rowIndex, rowIndex, index - 1, index + 1));
                    cell.setCellValue(Common.currency(Math.abs(salaryValue)));
                    calculations.get(i).setPayment(salaryValue);

                    index += 2;
                }
                rowIndex++;
            }

            Row salarySummaryRow = sheet.createRow(rowIndex);

            index = 0;

            if (chkMonthlyReportDate.isSelected()) {
                index++;
            }

            salarySummaryRow.createCell(index).setCellValue("Baki");

            index = columns.size();

            for (int i = 0; i < workerCount; i++) {
                ReportCalculation calculation = calculations.get(i);
                Cell cell = salarySummaryRow.createCell(index++);
                sheet.addMergedRegion(new CellRangeAddress(rowIndex, rowIndex, index - 1, index + 1));
                cell.setCellValue(Common.currency(calculation.getTotalBalance()));

                index += 2;
            }

            rowIndex++;
        }

        if (chkMonthlyReportSaving.isSelected()) {
            rowIndex++;

            sheet.createRow(rowIndex++).createCell(0).setCellValue("Simpanan Tetap");
            Row previousBalanceRow = sheet.createRow(rowIndex);
            sheet.addMergedRegion(new CellRangeAddress(rowIndex, rowIndex, 0, columns.size() - 1));
            previousBalanceRow.createCell(0).setCellValue("Baki Bulan Lalu");

            Row currentBalanceRow = sheet.createRow(rowIndex + 1);
            sheet.addMergedRegion(new CellRangeAddress(rowIndex + 1, rowIndex + 1, 0, columns.size() - 1));
            currentBalanceRow.createCell(0).setCellValue("Bulan Ini");

            Row balanceRow = sheet.createRow(rowIndex + 2);
            sheet.addMergedRegion(new CellRangeAddress(rowIndex + 2, rowIndex + 2, 0, columns.size() - 1));
            balanceRow.createCell(0).setCellValue("Jumlah Baki");

            ArrayList<ReportSaving> savings = this.getReportSavings(selected);

            index = columns.size();
            for (ReportSaving saving : savings) {
                sheet.addMergedRegion(new CellRangeAddress(rowIndex, rowIndex, index, index + 2));
                sheet.addMergedRegion(new CellRangeAddress(rowIndex + 1, rowIndex + 1, index, index + 2));
                sheet.addMergedRegion(new CellRangeAddress(rowIndex + 2, rowIndex + 2, index, index + 2));
                previousBalanceRow.createCell(index).setCellValue(saving.getPrevious());
                currentBalanceRow.createCell(index).setCellValue(saving.getCurrent());
                balanceRow.createCell(index).setCellValue(saving.getBalance());

                index += 3;
            }
        }



        FileOutputStream out = null;
        try {
            out = new FileOutputStream(filename);
            workbook.write(out);
            out.close();
            JOptionPane.showMessageDialog(null, "Report generated to " + filename, "", JOptionPane.INFORMATION_MESSAGE);
        } catch (FileNotFoundException ex) {
            JOptionPane.showMessageDialog(null, "The process cannot access the file because it is being used by another process", "Error", JOptionPane.ERROR_MESSAGE);
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                out.close();
            } catch (IOException ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_btnMonthlyReportExport1ActionPerformed

    private void btnMonthlyReportGenerate1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMonthlyReportGenerate1ActionPerformed
        // get selected worker to be display
        Hashtable dates = this.getReportSelectedDateRange1();
        ArrayList<String> columns = this.getReportColumns1();
        ArrayList<Worker> selected = this.getReportSelectedWorkers1();
        ArrayList<Transaction> transactions = this.getReportTransasctions1(selected);
        ArrayList<ReportSaving> savings = this.getReportSavings1(selected);
        ArrayList<ReportCalculation> calculations = this.getReportCalculations1(selected);
        ArrayList<ReportSalary> salaries = this.getReportSalaries1(selected);

        String html = "<html>";
        String css = "<style type=\"text/css\">";
        String table = "<table cellpadding=\"4\" cellspacing=\"0\">";
        String header = "";
        String content = "<tbody>";
        String saving_content = "";

        // <editor-fold defaultstate="collapsed" desc="render table header">
        header += "<thead><tr valign=\"top\">";
        String rowspan = selected.size() > 0 ? " rowspan=\"2\"" : "";
        for (String column : columns) {
            header += "<td" + rowspan + ">" + column + "</td>";
        }

        if (selected.size() > 0) {
            for (Worker worker : selected) {
                header += "<td colspan=\"3\">" + worker.getCode() + " " + worker.getName() + "</td>";
            }
            header += "</tr><tr>" + StringUtils.repeat("<td>Gaji</td><td>Pinjaman</td><td>Baki</td>", selected.size());
        }
        header += "</tr>";
        header += "</thead>";
        // </editor-fold>

        // <editor-fold defaultstate="collapsed" desc="prepare css">
        css += "table { border-collapse: collapse; font-family: calibri; font-size: 12px; } ";
        css += "table tr td.first { border-left: 1px solid #333; } ";
        css += "table tr td.last { border-right: 1px solid #333; } ";
        css += "table thead tr, table thead td, table tr.summary, table tr.summary td { border: 1px solid #333; } ";
        css += "table thead tr td { padding: 5px 20px 5px 5px; } ";
        css += "table tr td.worker_transaction { border-left: 1px solid #333; } ";
        css += "table tr.salary td { border: 1px solid #333 } ";
        css += "table tr.summary td.blank { border-left: none; border-bottom: none; } ";
        css += "table tr.saving_summary, table tr.saving_summary td { border : 1px solid #333; } ";
        css += "h3 { font-size: 14px; }";
        css += "table tr.content-body td { text-align: right;}";
        // </editor-fold>

        // <editor-fold defaultstate="collapsed" desc="render transaction content">
        for (Transaction transaction : transactions) {
            int first = 0;
            content += "<tr class=\"content-body\">";
            if (chkMonthlyReportDate.isSelected()) {
                content += "<td" + (first++ == 0 ? " class=\"first\"" : "")
                        + " style=\"text-align: left; \">"
                        + Common.renderDisplayDate(transaction.getDate()) + "</td>";
            }
            if (chkMonthlyReportClientName.isSelected()) {
                if (transaction.getType() == Transaction.GENERAL) {
                    content += "<td" + (first++ == 0 ? " class=\"first\"" : "")
                            + " style=\"text-align: left; \">"
                            + transaction.getCustomer().getName() + "</td>";
                } else {
                    content += "<td" + (first++ == 0 ? " class=\"first\"" : "")
                            + " style=\"text-align: left; \">Pinjaman</td>";
                }
            }

            if (chkMonthlyReportDescription.isSelected()) {
                content += "<td" + (first++ == 0 ? " class=\"first\"" : "")
                        + " style=\"text-align: left; \">"
                        + transaction.getDescription() + "</td>";
            }

            if (chkMonthlyReportWeight.isSelected()) {
                if (transaction.getType() == Transaction.GENERAL) {
                    content += "<td" + (first++ == 0 ? " class=\"first\"" : "") + ">"
                            + ((int) transaction.getWeight()) + "</td>";
                } else {
                    content += "<td" + (first++ == 0 ? " class=\"first\"" : "") + "></td>";
                }
            }
            if (chkMonthlyReportPricePerTon.isSelected()) {
                if (transaction.getType() == Transaction.GENERAL) {
                    content += "<td" + (first++ == 0 ? " class=\"first\"" : "") + ">"
                            + Common.currency(transaction.getPricePerTon()) + "</td>";
                } else {
                    content += "<td" + (first++ == 0 ? " class=\"first\"" : "") + "></td>";
                }
            }

            if (chkMonthlyReportTotalReceived.isSelected()) {
                if (transaction.getType() == Transaction.GENERAL) {
                    content += "<td" + (first++ == 0 ? " class=\"first\"" : "") + ">"
                            + Common.currency(transaction.getTotal()) + "</td>";
                } else {
                    content += "<td" + (first++ == 0 ? " class=\"first\"" : "") + "></td>";
                }
            }

            if (chkMonthlyReportWages.isSelected()) {
                if (transaction.getType() == Transaction.GENERAL) {
                    content += "<td" + (first++ == 0 ? " class=\"first\"" : "") + ">"
                            + Common.currency(transaction.getWages()) + "</td>";
                } else {
                    content += "<td" + (first++ == 0 ? " class=\"first\"" : "") + "></td>";
                }
            }

            if (chkMonthlyReportSalary.isSelected()) {
                if (transaction.getType() == Transaction.GENERAL) {
                    content += "<td" + (first++ == 0 ? " class=\"first\"" : "") + ">"
                            + Common.currency(transaction.getTotalSalary()) + "</td>";
                } else {
                    content += "<td" + (first++ == 0 ? " class=\"first\"" : "") + "></td>";
                }
            }

            if (chkMonthlyReportBalance.isSelected()) {
                if (transaction.getType() == Transaction.GENERAL) {
                    content += "<td" + (first++ == 0 ? " class=\"first\"" : "") + ">"
                            + Common.currency(transaction.getBalance()) + "</td>";
                } else {
                    content += "<td" + (first++ == 0 ? " class=\"first\"" : "") + "></td>";
                }
            }

            if (chkMonthlyReportKiraanAsing.isSelected()) {
                if (transaction.getType() == Transaction.GENERAL) {
                    content += "<td" + (first++ == 0 ? " class=\"first\"" : "") + ">"
                            + transaction.getKiraanAsing() + "</td>";
                } else {
                    content += "<td" + (first++ == 0 ? " class=\"first\"" : "") + "></td>";
                }
            }

            String[] workerIds = transaction.getNormalizedWorkerID().split(",");

            int index = 0;
            for (Worker worker : selected) {
                if (Common.inArray(workerIds, worker.getId())) {
                    if (transaction.getType() == Transaction.GENERAL) {
                        calculations.get(index).setSalary(transaction.getWagePerWorker());
                        content += "<td class=\"worker_transaction\">"
                                + Common.currency(transaction.getWagePerWorker()) + "</td>";
                        content += "<td>" + Common.currency(calculations.get(index).getLoan()) + "</td>";
                        content += "<td" + (index == selected.size() - 1 ? " class=\"last\"" : "")
                                + ">" + Common.currency(calculations.get(index).getBalance()) + "</td>";
                    } else {
                        calculations.get(index).setLoan(transaction.getLoanAmount());
                        content += "<td class=\"worker_transaction\"></td>";
                        content += "<td>" + Common.currency(transaction.getLoanAmount()) + "</td>";
                        content += "<td" + (index == selected.size() - 1 ? " class=\"last\"" : "") + ">" + Common.currency(calculations.get(index).getBalance()) + "</td>";
                    }
                } else {
                    content += "<td class=\"worker_transaction\"></td><td></td><td" + (index == selected.size() - 1 ? " class=\"last\"" : "") + "></td>";
                }

                index++;
            }
            content += "</tr>";
        }
        // </editor-fold>

        // <editor-fold defaultstate="collapsed" desc="render transaction worker summary">
        if (selected.size() > 0) {
            content += "<tr class=\"summary\">";
            content += "<td class=\"blank\" colspan=\"" + columns.size() + "\"></td>";
            for (ReportCalculation rc : calculations) {
                content += "<td style=\"text-align: right; \">" + Common.currency(rc.getSalary()) + "</td>";
                content += "<td style=\"text-align: right; \">" + Common.currency(rc.getLoan()) + "</td>";
                content += "<td style=\"text-align: right; \">" + Common.currency(rc.getBalance()) + "</td>";
            }
            content += "</tr>";
        }
        // </editor-fold>

        // <editor-fold defaultstate="collapsed" desc="render salaries report">
        if (salaries.size() > 0 && chkMonthlyReportSalaryPayment.isSelected()) {
            int size = columns.size();
            for (ReportSalary rs : salaries) {
                content += "<tr class=\"salary\">";


                size = columns.size();
                if (chkMonthlyReportDate.isSelected()) {
                    content += "<td>" + Common.renderDisplayDate(rs.getDate()) + "</td>";
                    size--;
                }

                content += "<td colspan=\"" + size + "\">Bayaran Gaji</td>";

                for (ReportCalculation rc : calculations) {
                    rc.setPayment(rs.getWorkerSalary(rc.getWorkerID()));
                    content += "<td colspan=\"3\" style=\"text-align: right; \">" + Common.currency(Math.abs(rs.getWorkerSalary(rc.getWorkerID()))) + "</td>";
                }

                content += "</tr>";
            }

            content += "<tr class=\"salary\">";

            if (chkMonthlyReportDate.isSelected()) {
                content += "<td>&nbsp;</td>";
            }

            content += "<td colspan=\"" + size + "\">Baki</td>";

            for (ReportCalculation rc : calculations) {
                content += "<td colspan=\"3\" style=\"text-align: right; \">" + Common.currency(rc.getTotalBalance()) + "</td>";
            }

            content += "</tr>";

        }
        // </editor-fold>

        // <editor-fold defaultstate="collapsed" desc="render report saving summary">
        if (chkMonthlyReportSaving.isSelected() && selected.size() > 0) {
            content += "<tr><td colspan=\"" + (selected.size() + columns.size()) + "\"><br /><b><u>Simpanan Tetap</u></b></td></tr>";

            /* Baki Lalu */
            content += "<tr class=\"saving_summary\">";

            int colspan_size = columns.size();

            content += "<td colspan=\"" + colspan_size + "\">";
            content += "Baki Bulan Lalu<br />";
            content += "Bulan Ini<br />";
            content += "Baki";
            content += "</td>";

            for (ReportSaving saving : savings) {
                content += "<td colspan=\"3\" style=\"text-align: right; \">";
                content += Common.currency(saving.getPrevious()) + "<br />";
                content += Common.currency(saving.getCurrent()) + "<br />";
                content += Common.currency(saving.getBalance());
                content += "</td>";
            }

            content += "</tr>";
        }
        // </editor-fold>

        content += "</tbody>";

        table += header + content;
        table += "</table>";

        css += "</style>";
        html += "<head>" + css + "</head>";
        html += "<body>";
        html += "<h3>Laporan Bulanan (" + Common.renderDisplayDate((Calendar) dates.get("from")) + " - " + Common.renderDisplayDate((Calendar) dates.get("to")) + ")</h3>";
        html += table;
        html += "</body>";
        html += "</html>";

        ReportFrame form = new ReportFrame();
        UserAgentContext context = new SimpleUserAgentContext();
        SimpleHtmlRendererContext rcontext = new SimpleHtmlRendererContext(form.html, context);
        DocumentBuilderImpl dbi = new DocumentBuilderImpl(context);
        Reader reader = new StringReader(html);
        Document document = null;

        try {
            document = dbi.parse(new InputSourceImpl(reader, "127.0.0.1"));
        } catch (SAXException ex) {
            System.err.println(ex.getMessage());
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }

        form.html.setDocument(document, rcontext);
        form.setVisible(true);
    }//GEN-LAST:event_btnMonthlyReportGenerate1ActionPerformed

    private void cboxMonthlyReportAllWorkers1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboxMonthlyReportAllWorkers1ActionPerformed
        boolean selected = cboxMonthlyReportAllWorkers1.isSelected();
        int row = tblMonthlyReportWorkers1.getRowCount();
        for (int i = 0; i < row; i++) {
            tblMonthlyReportWorkers1.setValueAt(selected, i, 0);
        }
    }//GEN-LAST:event_cboxMonthlyReportAllWorkers1ActionPerformed

    private void btnLoanSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLoanSearchActionPerformed
        DefaultTableModel loanTableModel = (DefaultTableModel) tblLoan.getModel();

        int rowCount = tblLoan.getRowCount();

        for (int i = 0; i < rowCount; i++) {
            loanTableModel.removeRow(0);
        }

        loaded_loan_ids.clear();

        Worker selected = cbxLoanWorkers.getSelectedIndex() == 0 ? null : workers.get(cbxLoanWorkers.getSelectedIndex() - 1);

        if (selected == null) {
            JOptionPane.showMessageDialog(null, "Sila pilih Pekerja.", "Kesilapan!", JOptionPane.ERROR_MESSAGE);
            return;
        } else if (txtLoanDateFrom.getDate() == null || txtLoanDateTo.getDate() == null) {
            JOptionPane.showMessageDialog(null, "Sila pilih tempoh tarikh.", "Kesilapan!", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String query = "SELECT * FROM workerRecord WHERE type = 2 AND worker_id = " + selected.getId() + " ";

        if (txtLoanDateFrom.getDate() != null) {
            Calendar calender = Calendar.getInstance();
            calender.setTime(txtLoanDateFrom.getDate());
            query += "AND date >= \"" + Common.renderSQLDate(calender) + "\" ";
        }

        if (txtLoanDateTo.getDate() != null) {
            Calendar calender = Calendar.getInstance();
            calender.setTime(txtLoanDateTo.getDate());
            query += "AND date <= \"" + Common.renderSQLDate(calender) + "\" ";
        }

        query += " ORDER BY date";

        ResultSet results = Database.instance().execute(query);
        int counter = 0;

        try {
            while (results.next()) {
                WorkerRecord record = new WorkerRecord(results.getInt("id"), results.getInt("worker_id"), results.getInt("type"), results.getDouble("amount"), results.getString("description"), Common.convertStringToDate(results.getString("date")));
                loaded_loan_ids.add(record.getId());

                Object[] objects = new Object[]{
                    new Integer(counter + 1),
                    new String(Common.renderDisplayDate(record.getDate())),
                    new String(record.getDescription()),
                    new String(Common.currency(record.getAmount())),};

                loanTableModel.addRow(objects);
                counter++;
            }
            results.close();
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }
    }//GEN-LAST:event_btnLoanSearchActionPerformed

    private void btnLoanEndActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLoanEndActionPerformed
        System.exit(0);
    }//GEN-LAST:event_btnLoanEndActionPerformed

    private void btnLoanCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLoanCancelActionPerformed
        if (tblLoan.getSelectedRowCount() == 0) {
            JOptionPane.showMessageDialog(null, "Sila pilih dari tabel daftar di atas",
                    "Kesilapan", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (JOptionPane.showConfirmDialog(null, "Anda memastikan batalkan sebanyak "
                + tblLoan.getSelectedRowCount() + " rekod dari tabel daftar di atas?",
                "", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
            return;
        }

        String id = "";
        for (Integer index : tblLoan.getSelectedRows()) {
            id += "," + loaded_loan_ids.get(index);
        }

        id = id.substring(1, id.length());

        String query = "DELETE FROM workerRecord WHERE id IN (" + id + ")";

        if (Database.instance().update(query)) {
            JOptionPane.showMessageDialog(null, "Rekod telah dibatal dari sistem",
                    "", JOptionPane.INFORMATION_MESSAGE);
            btnLoanSearchActionPerformed(evt);
        } else {
            String error_msg = "Tidak dapat membatalkan rekod dari sistem. "
                    + "Sila cuba sebentar lagi.";
            JOptionPane.showMessageDialog(null, error_msg, "",
                    JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btnLoanCancelActionPerformed

    private void cboxMonthlyReportAllWorkers2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboxMonthlyReportAllWorkers2ActionPerformed
        boolean selected = cboxMonthlyReportAllWorkers2.isSelected();
        int row = tblSavingReportWorkers.getRowCount();
        for (int i = 0; i < row; i++) {
            tblSavingReportWorkers.setValueAt(selected, i, 0);
        }
    }//GEN-LAST:event_cboxMonthlyReportAllWorkers2ActionPerformed

    private void btnSavingReportEndActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSavingReportEndActionPerformed
        System.exit(0);
    }//GEN-LAST:event_btnSavingReportEndActionPerformed

    private void btnSavingReportPrintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSavingReportPrintActionPerformed
        ArrayList<Worker> selected = this.getSavingReportSelectedWorker();
        Hashtable savings = this.getSavingReportEntries(selected);
        Hashtable previous = this.getSavingReportPreviousBalance(selected);

        PrinterJob job = PrinterJob.getPrinterJob();

        PageFormat format = job.defaultPage();
        format.setOrientation(PageFormat.LANDSCAPE);

        job.setPrintable(new SavingReportPrinter(savings, previous, selected), format);

        if (job.printDialog() == true) {
            try {
                HashPrintRequestAttributeSet attr = new HashPrintRequestAttributeSet();
                attr.add(new MediaPrintableArea(0, 0, 210, 297, MediaPrintableArea.MM));
                job.print(attr);
            } catch (PrinterException ex) {
                System.err.println(ex.getMessage());
            }
        }
    }//GEN-LAST:event_btnSavingReportPrintActionPerformed

    private void btnSavingReportExportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSavingReportExportActionPerformed
        ArrayList<Worker> selected = this.getSavingReportSelectedWorker();
        Hashtable dates = this.getSavingReportSelectedDateRange();
        Hashtable totals = new Hashtable();

        if (selected.size() == 0) {
            JOptionPane.showMessageDialog(this, "Sila pilih pekerja yang terlibat", "", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Hashtable savings = this.getSavingReportEntries(selected);
        Hashtable previous = this.getSavingReportPreviousBalance(selected);
        int savings_count = Integer.parseInt(savings.remove("count").toString());

        JFileChooser dialog = new JFileChooser();
        dialog.setFileFilter(new ExtensionFileFilter("Microsoft Excel", "xls"));

        if (dialog.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) {
            return;
        }

        File file = dialog.getSelectedFile();

        String filename = file.getPath();

        if (!filename.endsWith(".xls")) {
            filename += ".xls";
        }

        if (new File(filename).exists()) {
            if (JOptionPane.showConfirmDialog(null, "Overwrite the existing file?", "", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) != JOptionPane.YES_OPTION) {
                return;
            }
        }
        int index = 0;

        Workbook workbook = new HSSFWorkbook();
        Sheet sheet = (Sheet) workbook.createSheet("Laporan");
        Row header = sheet.createRow(0);

        index = 1;
        for (Worker worker : selected) {
            Cell cell = header.createCell(index);
            cell.setCellValue(worker.getCode() + " " + worker.getName());
            index++;
        }

        Row row_balance = sheet.createRow(1);
        row_balance.createCell(0).setCellValue("Baki Bulan Lalu");

        index = 1;
        for (Worker worker : selected) {
            Object previous_amount = previous.get(worker.getId());
            double amount = 0.0;

            if (previous_amount != null) {
                amount = Double.parseDouble(previous_amount.toString());
            }

            Cell cell = row_balance.createCell(index);
            cell.setCellValue(Common.currency(amount));

            index++;
        }

        int rowIndex = 2;
        int subRowIndex = 0;

        String[] keys = (String[]) savings.keySet().toArray(new String[0]);
        Arrays.sort(keys);

        for (String date : keys) {
            Hashtable entry = (Hashtable) savings.get(date);
            Row row = sheet.createRow(rowIndex++);
            subRowIndex = rowIndex;

            row.createCell(0).setCellValue(Common.renderDisplayDate(Common.convertStringToDate(date)));
            index = 1;

            for (Worker worker : selected) {
                Cell cell = row.createCell(index);
                ArrayList<Double> worker_entries = (ArrayList<Double>) entry.get(worker.getId());
                int counter = 0;
                double total = 0.0;

                if (totals.get(worker.getId()) != null) {
                    total = Double.parseDouble(totals.get(worker.getId()).toString());
                }

                if (worker_entries != null) {
                    for (Double amount : worker_entries) {
                        if (counter > 0) {
                            Row subRow = sheet.createRow(subRowIndex++);
                            cell = subRow.createCell(index);
                        }

                        cell.setCellValue(Common.currency(amount));
                        total += amount;
                        counter++;
                    }
                }

                totals.put(worker.getId(), total);
                index++;
            }

            rowIndex = subRowIndex;
        }

        Row row_sumamry = sheet.createRow(rowIndex);
        row_sumamry.createCell(0).setCellValue("Baki");

        index = 1;
        for (Worker worker : selected) {
            Cell cell = row_sumamry.createCell(index++);
            double previous_total = 0.0, current_total = 0.0;

            if (previous.get(worker.getId()) != null) {
                previous_total = Double.parseDouble(previous.get(worker.getId()).toString());
            }

            if (totals.get(worker.getId()) != null) {
                current_total = Double.parseDouble(totals.get(worker.getId()).toString());
            }

            cell.setCellValue(Common.currency(previous_total + current_total));
        }

        FileOutputStream out = null;

        try {
            out = new FileOutputStream(filename);
            workbook.write(out);
            out.close();
            JOptionPane.showMessageDialog(null, "Report generated to " + filename, "", JOptionPane.INFORMATION_MESSAGE);
        } catch (FileNotFoundException ex) {
            JOptionPane.showMessageDialog(null, "The process cannot access the file because it is being used by another process", "Error", JOptionPane.ERROR_MESSAGE);
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                out.close();
            } catch (IOException ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_btnSavingReportExportActionPerformed

    private void btnSavingReportGenerateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSavingReportGenerateActionPerformed
        ArrayList<Worker> selected = this.getSavingReportSelectedWorker();
        Hashtable dates = this.getSavingReportSelectedDateRange();
        Hashtable totals = new Hashtable();

        if (selected.size() == 0) {
            JOptionPane.showMessageDialog(this, "Sila pilih pekerja yang terlibat", "", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Hashtable savings = this.getSavingReportEntries(selected);
        Hashtable previous = this.getSavingReportPreviousBalance(selected);
        int savings_counter = Integer.parseInt(savings.remove("count").toString());

        String html = "<html>"
                + "<head>"
                + "<style>"
                + "table tr td { border: 1px solid #ccc; vertical-align: top; }"
                + "table tbody tr td.currency {text-align: right;}"
                + "</style>"
                + "</head>"
                + "<body>";

        html += "<h3>Laporan Simpanan (" + Common.renderDisplayDate((Calendar) dates.get("from")) + " - " + Common.renderDisplayDate((Calendar) dates.get("to")) + ")</h3>";
        html += "<table width=\"100%\">";
        html += "<thead><tr><td></td>";

        for (Worker worker : selected) {
            html += "<td>" + worker.getCode() + " " + worker.getName() + "</td>";
        }

        html += "</tr></thead>";
        html += "<tbody><tr><td>Baki Bulan Lalu</td>";

        for (Worker worker : selected) {
            Object previous_amount = previous.get(worker.getId());
            double amount = 0.0;

            if (previous_amount != null) {
                amount = Double.parseDouble(previous_amount.toString());
            }



            html += "<td class=\"currency\">" + Common.currency(amount) + "</td>";
        }

        html += "</tr>";

        String[] keys = (String[]) savings.keySet().toArray(new String[0]);
        Arrays.sort(keys);

        for (String date : keys) {

            Hashtable entry = (Hashtable) savings.get(date);

            html += "<tr><td>" + Common.renderDisplayDate(Common.convertStringToDate(date)) + "</td>";

            for (Worker worker : selected) {
                ArrayList<Double> worker_entries = (ArrayList<Double>) entry.get(worker.getId());
                int counter = 0;
                double total = 0.0;

                if (totals.get(worker.getId()) != null) {
                    total = Double.parseDouble(totals.get(worker.getId()).toString());
                }

                html += "<td class=\"currency\">";

                if (worker_entries != null) {
                    for (Double amount : worker_entries) {
                        if (counter > 0) {
                            html += "<br />";
                        }

                        html += Common.currency(amount);
                        total += amount;
                        counter++;
                    }
                }
                html += "</td>";

                totals.put(worker.getId(), total);
            }

            html += "</tr>";
        }

        html += "<tr><td>Baki</td>";

        for (Worker worker : selected) {
            double previous_total = 0.0, current_total = 0.0;

            if (previous.get(worker.getId()) != null) {
                previous_total = Double.parseDouble(previous.get(worker.getId()).toString());
            }

            if (totals.get(worker.getId()) != null) {
                current_total = Double.parseDouble(totals.get(worker.getId()).toString());
            }

            html += "<td class=\"currency\">" + Common.currency(previous_total + current_total) + "</td>";
        }

        html += "</tr>";


        html += "</tbody></table></body></html>";

        SavingReportFrame form = new SavingReportFrame();
        UserAgentContext context = new SimpleUserAgentContext();
        SimpleHtmlRendererContext rcontext = new SimpleHtmlRendererContext(form.html, context);
        DocumentBuilderImpl dbi = new DocumentBuilderImpl(context);
        Reader reader = new StringReader(html);
        Document document = null;

        try {
            document = dbi.parse(new InputSourceImpl(reader, "127.0.0.1"));
        } catch (SAXException ex) {
            System.err.println(ex.getMessage());
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }

        form.html.setDocument(document, rcontext);
        form.setVisible(true);
    }//GEN-LAST:event_btnSavingReportGenerateActionPerformed

    private void dateChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_dateChange
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
    }//GEN-LAST:event_dateChange

    private void EditSimpananActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_EditSimpananActionPerformed
        String msg = "Sila pilih 1 rekod simpanan untuk diubah";
        if (tblSaving.getSelectedRowCount() == 0) {
            JOptionPane.showMessageDialog(null, msg, "Kesilapan",
                    JOptionPane.ERROR_MESSAGE);
        } else if (tblSaving.getSelectedRowCount() > 1) {
            JOptionPane.showMessageDialog(null, msg, "Kesilapan",
                    JOptionPane.ERROR_MESSAGE);
        } else {

            Integer id = 0;
            for (Integer index : tblSaving.getSelectedRows()) {
                id = loaded_saving_ids.get(index);
            }

            try {
                new EditSavingForm(this, true, id).setVisible(true);
            } catch (SQLException ex) {
                msg = "Database error: " + ex.toString();
                JOptionPane.showMessageDialog(null, msg, "Kesilapan",
                        JOptionPane.ERROR_MESSAGE);
            } catch (ParseException ex) {
                msg = "Error: " + ex.toString();
                JOptionPane.showMessageDialog(null, msg, "Kesilapan",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }//GEN-LAST:event_EditSimpananActionPerformed

    private void editPinjamanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editPinjamanActionPerformed
        String msg = "Sila pilih 1 rekod pinjaman untuk diubah";
        if (tblLoan.getSelectedRowCount() == 0) {
            JOptionPane.showMessageDialog(null, msg, "Kesilapan",
                    JOptionPane.ERROR_MESSAGE);
        } else if (tblLoan.getSelectedRowCount() > 1) {
            JOptionPane.showMessageDialog(null, msg, "Kesilapan",
                    JOptionPane.ERROR_MESSAGE);
        } else {

            Integer id = 0;
            for (Integer index : tblLoan.getSelectedRows()) {
                id = loaded_loan_ids.get(index);
            }

            try {
                new EditLoanForm(this, true, id).setVisible(true);
            } catch (SQLException ex) {
                msg = "Database error: " + ex.toString();
                JOptionPane.showMessageDialog(null, msg, "Kesilapan",
                        JOptionPane.ERROR_MESSAGE);
            } catch (ParseException ex) {
                msg = "Error: " + ex.toString();
                JOptionPane.showMessageDialog(null, msg, "Kesilapan",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }//GEN-LAST:event_editPinjamanActionPerformed

    private void calculateTransaction(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_calculateTransaction
        if (txtTransactionPricePerTon.getText().isEmpty() || txtTransactionWeight.getText().isEmpty() || !Common.isDouble(txtTransactionPricePerTon.getText()) || !Common.isDouble(txtTransactionWeight.getText())) {
            return;
        }

        double kiraanAsing = 0.0;
        if (Common.isDouble(txtTransactionCalculate.getText())) {
            kiraanAsing = Double.parseDouble(txtTransactionCalculate.getText());
        }

        double weight = Double.parseDouble(txtTransactionWeight.getText()), price_per_ton = 0.0;

        if (!txtTransactionPricePerTon.getText().isEmpty() && Common.isDouble(txtTransactionPricePerTon.getText())) {
            price_per_ton = Double.parseDouble(txtTransactionPricePerTon.getText());
        }

        double total = ((weight / 1000) * price_per_ton) + (kiraanAsing * price_per_ton);

        txtTransactionTotalReceived.setText(Common.currency(total));

        //        if (txtTransactionWages.getText().isEmpty()) {
        //            txtTransactionWages.setText("0.00");
        //        }
        //
        //        if (txtTransactionWagesTax.getText().isEmpty()) {
        //            txtTransactionWagesTax.setText("0.00");
        //        }

        if ((txtTransactionWages.getText().isEmpty() || !Common.isDouble(txtTransactionWages.getText())) && (txtTransactionWagesTax.getText().isEmpty() || !Common.isDouble(txtTransactionWagesTax.getText()))) {
            return;
        }

        double wages = 0.0, wages_tax = 0.0, salary = 0.0, salary_tax;

        if (!txtTransactionWages.getText().isEmpty() && Common.isDouble(txtTransactionWages.getText())) {
            wages = Double.parseDouble(txtTransactionWages.getText());
        }

        if (!txtTransactionWagesTax.getText().isEmpty() && Common.isDouble(txtTransactionWagesTax.getText())) {
            wages_tax = Double.parseDouble(txtTransactionWagesTax.getText());
        }

        salary = ((weight / 1000) * wages) + (kiraanAsing * wages);
        salary_tax = ((weight / 1000) * wages_tax) + (kiraanAsing * wages_tax);
        txtTransactionSalary.setText(Common.currency(salary));

        txtTransactionBalance.setText(Common.currency(total - salary));
        if (salary_tax > 0) {
            txtTransactionBalance1.setText(Common.currency(total - salary_tax));
            txtTransactionSalary1.setText(Common.currency(salary_tax));
        } else {
            txtTransactionBalance1.setText(Common.currency(0));
            txtTransactionSalary1.setText(Common.currency(0));
        }

        int worker_count = 0;
        int row = tblTransactionInvolvedWorkers.getRowCount();
        for (int i = 0; i < row; i++) {
            if (Boolean.parseBoolean(tblTransactionInvolvedWorkers.getValueAt(i, 0).toString()) == true) {
                worker_count++;
            }
        }

        worker_count = worker_count > 0 ? worker_count : 1;
        txtTransactionPayPerPerson.setText(Common.currency(salary / worker_count));

        if (salary_tax > 0) {
            txtTransactionPayPerPerson1.setText(Common.currency(salary_tax / worker_count));
        } else {
            txtTransactionPayPerPerson1.setText(Common.currency(0));
        }
    }//GEN-LAST:event_calculateTransaction

    private void txtTransactionWagesTaxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtTransactionWagesTaxActionPerformed
        calculateTransaction(null);
    }//GEN-LAST:event_txtTransactionWagesTaxActionPerformed

    private void txtTransactionCalculateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtTransactionCalculateActionPerformed
        if (txtTransactionCalculate.getText().isEmpty()) {
            txtTransactionCalculate.setText("0.00");
        }
        calculateTransaction(null);
    }//GEN-LAST:event_txtTransactionCalculateActionPerformed

    private void txtTransactionWagesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtTransactionWagesActionPerformed
        if (txtTransactionWages.getText().isEmpty()) {
            txtTransactionWages.setText("0.00");
        }
        calculateTransaction(null);
    }//GEN-LAST:event_txtTransactionWagesActionPerformed

    private void txtTransactionPricePerTonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtTransactionPricePerTonActionPerformed
        if (txtTransactionPricePerTon.getText().isEmpty()) {
            txtTransactionPricePerTon.setText("0.00");
        }
        calculateTransaction(null);
    }//GEN-LAST:event_txtTransactionPricePerTonActionPerformed

    private void txtTransactionWeightActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtTransactionWeightActionPerformed
        if (txtTransactionWeight.getText().isEmpty()) {
            txtTransactionWeight.setText("0.00");
        }
        calculateTransaction(null);
    }//GEN-LAST:event_txtTransactionWeightActionPerformed

    private void cboxTransactionAllWorkersActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboxTransactionAllWorkersActionPerformed
        boolean selected = cboxTransactionAllWorkers.isSelected();
        int row = tblTransactionInvolvedWorkers.getRowCount();
        for (int i = 0; i < row; i++) {
            tblTransactionInvolvedWorkers.setValueAt(selected, i, 0);
        }
    }//GEN-LAST:event_cboxTransactionAllWorkersActionPerformed

    private void btnTransactionNewLoanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTransactionNewLoanActionPerformed
        if (listTransactionLoanWorkers.getSelectedIndex() < 0) {
            return;
        }

        int index = listTransactionLoanWorkers.getSelectedIndex();
        Worker worker = workers.get(index);
        new LoanForm(this, true, worker).setVisible(true);
    }//GEN-LAST:event_btnTransactionNewLoanActionPerformed

    private void txtTransactionEndActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtTransactionEndActionPerformed
        System.exit(0);
    }//GEN-LAST:event_txtTransactionEndActionPerformed

    private void txtTransactionNewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtTransactionNewActionPerformed
        this._clearTransactionForm();
    }//GEN-LAST:event_txtTransactionNewActionPerformed

    private void btnRecordActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRecordActionPerformed
        if (!this._validate_transaction_form()) {
            return;
        } else if (JOptionPane.showConfirmDialog(null,
                "Anda pasti merekodkan transaksi baru? Transaksi direkodkan tidak boleh dibatalkan.",
                "", JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE) != JOptionPane.YES_OPTION) {
            return;
        }

        Customer selected = cbxTransactionClients.getSelectedIndex() > 0 ? customers.get(cbxTransactionClients.getSelectedIndex() - 1) : new Customer();

        Transaction transaction = new Transaction();
        transaction.setType(Transaction.GENERAL);
        transaction.setCustomerID(selected.getId());
        transaction.setDescription(txtTransactionDescription.getText());
        transaction.setWeight(Double.parseDouble(txtTransactionWeight.getText()));
        transaction.setPricePerTon(Double.parseDouble(txtTransactionPricePerTon.getText()));
        transaction.setWages(Double.parseDouble(txtTransactionWages.getText()));
        transaction.setDate(txtTransactionDate.getDate());
        transaction.setKiraanAsing(Double.parseDouble(txtTransactionCalculate.getText()));
        transaction.setWagesTax(Double.parseDouble(txtTransactionWagesTax.getText()));

        int row = tblTransactionInvolvedWorkers.getRowCount();

        for (int i = 0; i < row; i++) {
            if (Boolean.parseBoolean(tblTransactionInvolvedWorkers.getValueAt(i,
                    0).toString()) == true) {
                transaction.addWorker(workers.get(i));
            }
        }

        if (transaction.save()) {
            JOptionPane.showMessageDialog(null,
                    "Transaksi berjaya direkodkan.", "Berjaya!",
                    JOptionPane.INFORMATION_MESSAGE);
            this._clearTransactionForm();
        } else {
            JOptionPane.showMessageDialog(null,
                    "Transaksi tidak dapat direkodkan.", "Kesilapan!",
                    JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btnRecordActionPerformed

    private void ubahTransaksiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ubahTransaksiActionPerformed
        String msg = "Sila pilih 1 rekod Transaksi untuk diubah";
        if (tblTransactionList.getSelectedRowCount() == 0) {
            JOptionPane.showMessageDialog(null, msg, "Kesilapan",
                    JOptionPane.ERROR_MESSAGE);
        } else if (tblTransactionList.getSelectedRowCount() > 1) {
            JOptionPane.showMessageDialog(null, msg, "Kesilapan",
                    JOptionPane.ERROR_MESSAGE);
        } else {

            Integer id = 0;
            for (Integer index : tblTransactionList.getSelectedRows()) {
                id = loaded_transaction_ids.get(index);
            }

            try {
                new EditTransaksiForm(this, true, id).setVisible(true);
            } catch (SQLException ex) {
                msg = "Database error: " + ex.toString();
                JOptionPane.showMessageDialog(null, msg, "Kesilapan",
                        JOptionPane.ERROR_MESSAGE);
            } catch (ParseException ex) {
                msg = "Error: " + ex.toString();
                JOptionPane.showMessageDialog(null, msg, "Kesilapan",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }//GEN-LAST:event_ubahTransaksiActionPerformed

    private ArrayList<Worker> getSavingReportSelectedWorker() {
        ArrayList<Worker> selected = new ArrayList<Worker>();
        int row = tblSavingReportWorkers.getRowCount();

        for (int i = 0; i < row; i++) {
            if (Boolean.parseBoolean(tblSavingReportWorkers.getValueAt(i, 0).toString())) {
                selected.add(workers.get(i));
            }
        }

        return selected;
    }

    private Hashtable getSavingReportPreviousBalance(ArrayList<Worker> selected) {
        Hashtable savings = new Hashtable();
        Hashtable dates = this.getSavingReportSelectedDateRange();

        String query = "";
        String id = Worker.join(selected);

        ResultSet rs = null;
        try {
            query = "SELECT SUM(amount) as previous, worker_id FROM workerRecord WHERE date < '"
                    + Common.renderSQLDate((Calendar) dates.get("from"))
                    + "' AND type IN (" + WorkerRecord.SAVING + ","
                    + WorkerRecord.WITHDRAW + ") AND worker_id IN ("
                    + id + ") GROUP BY worker_id";

            rs = Database.instance().execute(query);

            while (rs.next()) {
                savings.put(rs.getInt("worker_id"), rs.getDouble("previous"));
            }

            rs.close();
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }

        return savings;
    }

    private Hashtable getSavingReportEntries(ArrayList<Worker> workers) {
        Hashtable dates = this.getSavingReportSelectedDateRange();
        Hashtable records = new Hashtable();
        String id = Worker.join(workers);

        String query = "SELECT * FROM workerRecord WHERE type = 3 ";
        query += "AND date >= '" + Common.renderSQLDate((Calendar) dates.get("from"))
                + "' AND date <= '" + Common.renderSQLDate((Calendar) dates.get("to")) + "' ";
        query += workers.isEmpty() ? "ORDER BY date ASC" : "AND worker_id IN ("
                + id + ") ORDER BY date ASC";

        int counter = 0;
        try {
            ResultSet rs = Database.instance().execute(query);

            while (rs.next()) {
                Hashtable entry = (Hashtable) records.get(rs.getString("date"));

                if (entry == null) {
                    entry = new Hashtable();
                }

                ArrayList<Double> worker_entry = (ArrayList<Double>) entry.get(rs.getInt("worker_id"));

                if (worker_entry == null) {
                    worker_entry = new ArrayList<Double>();
                }

                worker_entry.add(rs.getDouble("amount"));
                entry.put(rs.getInt("worker_id"), worker_entry);
                records.put(rs.getString("date"), entry);
                counter++;
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }

        records.put("count", counter);

        return records;
    }

    private Hashtable getSavingReportSelectedDateRange() {
        Hashtable values = new Hashtable();

        Calendar dateFrom = Calendar.getInstance(), dateTo = Calendar.getInstance();

        if (rbtnSavingReportCurrentMonth.isSelected()) {
            dateFrom.set(Calendar.DAY_OF_MONTH, 1);
            dateTo.set(Calendar.DAY_OF_MONTH, dateTo.getActualMaximum(Calendar.DAY_OF_MONTH));
        } else if (rbtnSavingReportPreviousMonth.isSelected()) {
            dateFrom.set(Calendar.DAY_OF_MONTH, 1);
            dateTo.set(Calendar.DAY_OF_MONTH, dateTo.getActualMaximum(Calendar.DAY_OF_MONTH));
            dateFrom.roll(Calendar.MONTH, -1);
            dateTo.roll(Calendar.MONTH, -1);
        } else {
            dateFrom.setTime(txtSavingReportDateFrom.getDate());
            dateTo.setTime(txtSavingReportDateTo.getDate());
        }

        values.put("from", dateFrom);
        values.put("to", dateTo);

        return values;
    }

    private void _reset_worker_form() {
        txtProfileWorkerCurrentSaving.setText("");
        txtProfileWorkerRegisterDay.setText("");
        txtProfileWorkerRegisterMonth.setText("");
        txtProfileWorkerRegisterYear.setText("");
        txtProfileWorkerReturnDay.setText("");
        txtProfileWorkerReturnMonth.setText("");
        txtProfileWorkerReturnYear.setText("");
        txtProfileWorkerStatus.setText("");
    }

    private boolean _validate_transaction_form() {
        String message = "";

        if (txtTransactionPricePerTon.getText().isEmpty()) {
            txtTransactionPricePerTon.setText("0.00");
        }

        if (cbxTransactionListClients.getSelectedIndex() == 0
                && txtTransactionWeight.getText().isEmpty()
                && txtTransactionPricePerTon.getText().isEmpty()
                && txtTransactionWages.getText().isEmpty()
                && !txtTransactionDescription.getText().isEmpty()) {
            txtTransactionCalculate.setText("0.00");
            txtTransactionWeight.setText("0");
            txtTransactionPricePerTon.setText("0.00");
            txtTransactionWages.setText("0.00");
            txtTransactionCalculate.setText("0");
        } else {
            if (txtTransactionDate.getDate() == null) {
                message = "Kesilapan Tarikh";
            } else if (cbxTransactionClients.getSelectedIndex() == 0) {
                message = "Sila Pilih Pelanggan";
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
            } else if (!Common.isDouble(txtTransactionWagesTax.getText())) {
                message = "Kesilapan Upah Kerja (Tax) atau tiada 0";
            }
        }

        int row = tblTransactionInvolvedWorkers.getRowCount();
        int counter = 0;

        for (int i = 0; i < row; i++) {
            if (Boolean.parseBoolean(tblTransactionInvolvedWorkers.getValueAt(i, 0).toString()) == true) {
                counter++;
            }
        }

        if (counter == 0) {
            message = "Sila Pilih Pekerja yang Terlibat";
        }

        if (message.isEmpty()) {
            return true;
        }

        JOptionPane.showMessageDialog(null, message, "Kesilapan!",
                JOptionPane.WARNING_MESSAGE);

        return false;
    }

    private ArrayList<String> getReportColumns1() {
        ArrayList<String> headers = new ArrayList<String>();

        if (chkMonthlyReportDate1.isSelected()) {
            headers.add("Tarikh");
        }
        if (chkMonthlyReportClientName1.isSelected()) {
            headers.add("Nama Pelanggan");
        }
        if (chkMonthlyReportDescription1.isSelected()) {
            headers.add("Keterangan");
        }
        if (chkMonthlyReportWeight1.isSelected()) {
            headers.add("Berat KG");
        }
        if (chkMonthlyReportPricePerTon1.isSelected()) {
            headers.add("Harga Seton (TAX)");
        }
        if (chkMonthlyReportTotalReceived1.isSelected()) {
            headers.add("Jumlah Diterima");
        }
        if (chkMonthlyReportWages1.isSelected()) {
            headers.add("Upah Kerja");
        }
        if (chkMonthlyReportSalary1.isSelected()) {
            headers.add("Jumlah Gaji");
        }
        if (chkMonthlyReportBalance1.isSelected()) {
            headers.add("Jumlah Baki");
        }
        if (chkMonthlyReportKiraanAsing1.isSelected()) {
            headers.add("Kiraan Asing");
        }

        return headers;
    }

    private ArrayList<Worker> getReportSelectedWorkers1() {
        ArrayList<Worker> selected = new ArrayList<Worker>();
        int row = tblMonthlyReportWorkers1.getRowCount();

        for (int i = 0; i < row; i++) {
            if (Boolean.parseBoolean(tblMonthlyReportWorkers1.getValueAt(i, 0).toString())) {
                selected.add(workers.get(i));
            }
        }

        return selected;
    }

    private ArrayList<Transaction> getReportTransasctions1(ArrayList<Worker> selected) {
        ArrayList<Transaction> transactions = new ArrayList<Transaction>();
        Hashtable dates = getReportSelectedDateRange1();
        String id = Worker.join(selected);

        String query = "SELECT DISTINCT id, type, loan_amount, customer_id, description, weight, price_per_ton, wages_tax, kiraan_asing, date, created, normalized_worker_id FROM transactions ";
        query += "INNER JOIN transaction_workers ON transactions.id = transaction_workers.transaction_id ";
        query += "WHERE date >= '" + Common.renderSQLDate((Calendar) dates.get("from")) + "' AND date <= '" + Common.renderSQLDate((Calendar) dates.get("to")) + "' ";
        query += id.isEmpty() ? "AND type = 1 ORDER BY date" : "AND worker_id IN (" + id + ") AND wages_tax > 0 ORDER BY DATE";

        ResultSet rs = Database.instance().execute(query);

        try {
            while (rs.next()) {
                ArrayList<Worker> involver = Worker.bulk(rs.getString("normalized_worker_id"));
                transactions.add(new Transaction(rs.getInt("id"),
                        rs.getInt("customer_id"), rs.getInt("type"),
                        rs.getDouble("weight"), rs.getDouble("price_per_ton"),
                        rs.getDouble("wages_tax"), rs.getDouble("wages_tax"),
                        rs.getDouble("kiraan_asing"), rs.getDouble("loan_amount"),
                        involver, Common.convertStringToDate(rs.getString("date")),
                        rs.getString("description"), rs.getString("normalized_worker_id")));
            }

            rs.close();
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }

        return transactions;
    }

    private ArrayList<ReportCalculation> getReportCalculations1(ArrayList<Worker> selected) {
        ArrayList<ReportCalculation> calculations = new ArrayList<ReportCalculation>();

        for (Worker worker : selected) {
            // ReportCalculation repCalc = new ReportCalculation(worker.getId());
            //calculations.add(repCalc.getCalculationbyWorkingId(worker.getId()));
            calculations.add(new ReportCalculation(worker.getId()));
        }

        return calculations;
    }

    private ArrayList<ReportSalary> getReportSalaries1(ArrayList<Worker> selected) {
        ArrayList<ReportSalary> salaries = new ArrayList<ReportSalary>();
        Hashtable dates = this.getReportSelectedDateRange1();

        String id = "";
        for (Worker worker : selected) {
            id += worker.getId() + ",";
        }

        id = id.isEmpty() ? "0" : id.substring(0, id.length() - 1);

        String query = "SELECT worker_id, date, amount FROM workerRecord WHERE worker_id IN ("
                + id + ") AND type = " + WorkerRecord.PAYMENT + " ";
        query += "AND date >= '" + Common.renderSQLDate((Calendar) dates.get("from")) + "' ";
        query += "AND date <= '" + Common.renderSQLDate((Calendar) dates.get("to")) + "' ";
        query += "ORDER BY date";

        ResultSet rs = Database.instance().execute(query);

        try {
            String date = "";
            ReportSalary salary = null;

            while (rs.next()) {

                if (!date.equals(rs.getString("date"))) {
                    date = rs.getString("date");

                    if (salary != null) {
                        salaries.add(salary);
                    }

                    salary = new ReportSalary(workers,
                            Common.convertStringToDate(rs.getString("date")));
                }

                salary.setWorkerSalary(rs.getInt("worker_id"),
                        rs.getDouble("amount"));
            }

            if (salary != null) {
                salaries.add(salary);
            }

            rs.close();
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }

        return salaries;
    }

    private Hashtable getReportSelectedDateRange1() {
        Hashtable values = new Hashtable();

        Calendar dateFrom = Calendar.getInstance(), dateTo = Calendar.getInstance();

        if (rbtnMonhtlyReportCurrentMonth1.isSelected()) {
            dateFrom.set(Calendar.DAY_OF_MONTH, 1);
            dateTo.set(Calendar.DAY_OF_MONTH, dateTo.getActualMaximum(Calendar.DAY_OF_MONTH));
        } else if (rbtnMonthlyReportLastMonth1.isSelected()) {
            dateFrom.set(Calendar.DAY_OF_MONTH, 1);
            dateTo.set(Calendar.DAY_OF_MONTH, dateTo.getActualMaximum(Calendar.DAY_OF_MONTH));
            dateFrom.roll(Calendar.MONTH, -1);
            dateTo.roll(Calendar.MONTH, -1);
        } else {
            dateFrom.setTime(txtMonthlyReportDateFrom1.getDate());
            dateTo.setTime(txtMonthlyReportDateTo1.getDate());
        }

        values.put("from", dateFrom);
        values.put("to", dateTo);

        return values;
    }

    private ArrayList<ReportSaving> getReportSavings1(ArrayList<Worker> selected) {
        ArrayList<ReportSaving> saving = new ArrayList<ReportSaving>();
        Hashtable dates = this.getReportSelectedDateRange1();

        if (!chkMonthlyReportSaving1.isSelected()) {
            return saving;
        }

        String query = "";
        ResultSet rs = null;
        for (Worker worker : selected) {
            try {
                query = "SELECT ";
                query += "(SELECT SUM(amount) FROM workerRecord WHERE worker_id = "
                        + worker.getId() + " AND date < '"
                        + Common.renderSQLDate((Calendar) dates.get("from"))
                        + "' AND type IN (" + WorkerRecord.SAVING + ","
                        + WorkerRecord.WITHDRAW + ")) AS previous, ";
                query += "(SELECT SUM(amount) FROM workerRecord WHERE worker_id = "
                        + worker.getId() + " AND date >= '"
                        + Common.renderSQLDate((Calendar) dates.get("from"))
                        + "' AND date <= '" + Common.renderSQLDate((Calendar) dates.get("to"))
                        + "' AND type IN (" + WorkerRecord.SAVING + ","
                        + WorkerRecord.WITHDRAW + ")) AS current ";

                rs = Database.instance().execute(query);
                rs.next();

                saving.add(new ReportSaving(rs.getDouble("previous"),
                        rs.getDouble("current"), worker));
                rs.close();
            } catch (SQLException ex) {
                System.err.println(ex.getMessage());
            }
        }

        return saving;

    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton EditSimpanan;
    private javax.swing.ButtonGroup btnGroupMonthlyReport;
    private javax.swing.ButtonGroup btnGroupSavingReport;
    private javax.swing.ButtonGroup btnGroupWorkerReportType;
    private javax.swing.JButton btnLoanCancel;
    private javax.swing.JButton btnLoanEnd;
    private javax.swing.JButton btnLoanSearch;
    private javax.swing.JButton btnMonthlyReportEnd;
    private javax.swing.JButton btnMonthlyReportEnd1;
    private javax.swing.JButton btnMonthlyReportExport;
    private javax.swing.JButton btnMonthlyReportExport1;
    private javax.swing.JButton btnMonthlyReportGenerate;
    private javax.swing.JButton btnMonthlyReportGenerate1;
    private javax.swing.JButton btnMonthlyReportPrint;
    private javax.swing.JButton btnMonthlyReportPrint1;
    private javax.swing.JButton btnPasswordChange;
    private javax.swing.JButton btnPayCancel;
    private javax.swing.JButton btnPayEnd;
    private javax.swing.JButton btnPayNew;
    private javax.swing.JButton btnPaymentSearch;
    private javax.swing.JButton btnProfileClientDelete;
    private javax.swing.JButton btnProfileClientEdit;
    private javax.swing.JButton btnProfileClientEnd;
    private javax.swing.JButton btnProfileClientNew;
    private javax.swing.JButton btnProfileWorkerDelete;
    private javax.swing.JButton btnProfileWorkerEdit;
    private javax.swing.JButton btnProfileWorkerEnd;
    private javax.swing.JButton btnProfileWorkerNew;
    private javax.swing.JButton btnRecord;
    private javax.swing.JButton btnReportWorkerEnd;
    private javax.swing.JButton btnReportWorkerExport;
    private javax.swing.JButton btnReportWorkerGenerate;
    private javax.swing.JButton btnReportWorkerPrint;
    private javax.swing.JButton btnSavingCancel;
    private javax.swing.JButton btnSavingEnd;
    private javax.swing.JButton btnSavingNew;
    private javax.swing.JButton btnSavingReportEnd;
    private javax.swing.JButton btnSavingReportExport;
    private javax.swing.JButton btnSavingReportGenerate;
    private javax.swing.JButton btnSavingReportPrint;
    private javax.swing.JButton btnSavingSearch;
    private javax.swing.JButton btnTransactionListDelete;
    private javax.swing.JButton btnTransactionListDelete1;
    private javax.swing.JButton btnTransactionListEnd;
    private javax.swing.JButton btnTransactionListEnd1;
    private javax.swing.JButton btnTransactionNewLoan;
    private javax.swing.JButton btnTransactionSave;
    private javax.swing.JButton btnTransactionSave1;
    private javax.swing.ButtonGroup buttonGroupMonthlyReportTax;
    private javax.swing.JCheckBox cboxMonthlyReportAllWorkers;
    private javax.swing.JCheckBox cboxMonthlyReportAllWorkers1;
    private javax.swing.JCheckBox cboxMonthlyReportAllWorkers2;
    private javax.swing.JCheckBox cboxTransactionAllWorkers;
    private javax.swing.JComboBox cbxClients;
    private javax.swing.JComboBox cbxLoanWorkers;
    private javax.swing.JComboBox cbxPaymentWorkers;
    private javax.swing.JComboBox cbxReportWorkers;
    private javax.swing.JComboBox cbxSavingWorkers;
    private javax.swing.JComboBox cbxTransactionClients;
    private javax.swing.JComboBox cbxTransactionListClients;
    private javax.swing.JComboBox cbxTransactionListClients1;
    private javax.swing.JComboBox cbxWorkers;
    public javax.swing.JCheckBox chkMonthlyReportBalance;
    public javax.swing.JCheckBox chkMonthlyReportBalance1;
    public javax.swing.JCheckBox chkMonthlyReportClientName;
    public javax.swing.JCheckBox chkMonthlyReportClientName1;
    public javax.swing.JCheckBox chkMonthlyReportDate;
    public javax.swing.JCheckBox chkMonthlyReportDate1;
    public javax.swing.JCheckBox chkMonthlyReportDescription;
    public javax.swing.JCheckBox chkMonthlyReportDescription1;
    public javax.swing.JCheckBox chkMonthlyReportKiraanAsing;
    public javax.swing.JCheckBox chkMonthlyReportKiraanAsing1;
    public javax.swing.JCheckBox chkMonthlyReportPricePerTon;
    public javax.swing.JCheckBox chkMonthlyReportPricePerTon1;
    public javax.swing.JCheckBox chkMonthlyReportSalary;
    public javax.swing.JCheckBox chkMonthlyReportSalary1;
    public javax.swing.JCheckBox chkMonthlyReportSalaryPayment;
    public javax.swing.JCheckBox chkMonthlyReportSalaryPayment1;
    public javax.swing.JCheckBox chkMonthlyReportSaving;
    public javax.swing.JCheckBox chkMonthlyReportSaving1;
    public javax.swing.JCheckBox chkMonthlyReportTotalReceived;
    public javax.swing.JCheckBox chkMonthlyReportTotalReceived1;
    public javax.swing.JCheckBox chkMonthlyReportWages;
    public javax.swing.JCheckBox chkMonthlyReportWages1;
    public javax.swing.JCheckBox chkMonthlyReportWeight;
    public javax.swing.JCheckBox chkMonthlyReportWeight1;
    private javax.swing.JButton editPinjaman;
    private javax.swing.ButtonGroup groupTransactionType;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton7;
    private javax.swing.JButton jButton8;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel36;
    private javax.swing.JLabel jLabel37;
    private javax.swing.JLabel jLabel38;
    private javax.swing.JLabel jLabel39;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel40;
    private javax.swing.JLabel jLabel41;
    private javax.swing.JLabel jLabel42;
    private javax.swing.JLabel jLabel43;
    private javax.swing.JLabel jLabel44;
    private javax.swing.JLabel jLabel45;
    private javax.swing.JLabel jLabel46;
    private javax.swing.JLabel jLabel47;
    private javax.swing.JLabel jLabel48;
    private javax.swing.JLabel jLabel49;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel50;
    private javax.swing.JLabel jLabel51;
    private javax.swing.JLabel jLabel52;
    private javax.swing.JLabel jLabel53;
    private javax.swing.JLabel jLabel54;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel13;
    private javax.swing.JPanel jPanel14;
    private javax.swing.JPanel jPanel15;
    private javax.swing.JPanel jPanel16;
    private javax.swing.JPanel jPanel17;
    private javax.swing.JPanel jPanel18;
    private javax.swing.JPanel jPanel19;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel20;
    private javax.swing.JPanel jPanel21;
    private javax.swing.JPanel jPanel22;
    private javax.swing.JPanel jPanel23;
    private javax.swing.JPanel jPanel24;
    private javax.swing.JPanel jPanel25;
    private javax.swing.JPanel jPanel26;
    private javax.swing.JPanel jPanel27;
    private javax.swing.JPanel jPanel28;
    private javax.swing.JPanel jPanel29;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel30;
    private javax.swing.JPanel jPanel31;
    private javax.swing.JPanel jPanel32;
    private javax.swing.JPanel jPanel33;
    private javax.swing.JPanel jPanel34;
    private javax.swing.JPanel jPanel35;
    private javax.swing.JPanel jPanel36;
    private javax.swing.JPanel jPanel37;
    private javax.swing.JPanel jPanel38;
    private javax.swing.JPanel jPanel39;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel40;
    private javax.swing.JPanel jPanel41;
    private javax.swing.JPanel jPanel42;
    private javax.swing.JPanel jPanel43;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane10;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JScrollPane jScrollPane8;
    private javax.swing.JScrollPane jScrollPane9;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTabbedPane jTabbedPane2;
    private javax.swing.JTabbedPane jTabbedPane3;
    private javax.swing.JTabbedPane jTabbedPane4;
    private javax.swing.JList listTransactionLoanWorkers;
    private javax.swing.JRadioButton rbtnMonhtlyReportCurrentMonth;
    private javax.swing.JRadioButton rbtnMonhtlyReportCurrentMonth1;
    private javax.swing.JRadioButton rbtnMonhtlyReportDateRange;
    private javax.swing.JRadioButton rbtnMonhtlyReportDateRange1;
    private javax.swing.JRadioButton rbtnMonhtlyReportDateRange2;
    private javax.swing.JRadioButton rbtnMonthlyReportLastMonth;
    private javax.swing.JRadioButton rbtnMonthlyReportLastMonth1;
    private javax.swing.JRadioButton rbtnSavingReportCurrentMonth;
    private javax.swing.JRadioButton rbtnSavingReportPreviousMonth;
    private javax.swing.JRadioButton rbtnWorkerMonthlyIncome;
    private javax.swing.JRadioButton rbtnWorkerReportFull;
    private javax.swing.JRadioButton rbtnWorkerSaving;
    private javax.swing.JTable tblLoan;
    private javax.swing.JTable tblMonthlyReportWorkers;
    private javax.swing.JTable tblMonthlyReportWorkers1;
    private javax.swing.JTable tblPay;
    private javax.swing.JTable tblSaving;
    private javax.swing.JTable tblSavingReportWorkers;
    private javax.swing.JTable tblTransactionInvolvedWorkers;
    private javax.swing.JTable tblTransactionList;
    private javax.swing.JTable tblTransactionList1;
    private com.toedter.calendar.JDateChooser txtLoanDateFrom;
    private com.toedter.calendar.JDateChooser txtLoanDateTo;
    private com.toedter.calendar.JDateChooser txtMonthlyReportDateFrom;
    private com.toedter.calendar.JDateChooser txtMonthlyReportDateFrom1;
    private com.toedter.calendar.JDateChooser txtMonthlyReportDateTo;
    private com.toedter.calendar.JDateChooser txtMonthlyReportDateTo1;
    private javax.swing.JPasswordField txtPassword;
    private javax.swing.JPasswordField txtPasswordConfirm;
    private com.toedter.calendar.JMonthChooser txtPaymentMonth;
    private com.toedter.calendar.JMonthChooser txtPaymentMonthTo;
    private com.toedter.calendar.JYearChooser txtPaymentYear;
    private com.toedter.calendar.JYearChooser txtPaymentYearTo;
    private javax.swing.JTextField txtProfileClientStatus;
    private javax.swing.JTextField txtProfileWorkerCurrentSaving;
    private javax.swing.JTextField txtProfileWorkerRegisterDay;
    private javax.swing.JTextField txtProfileWorkerRegisterMonth;
    private javax.swing.JTextField txtProfileWorkerRegisterYear;
    private javax.swing.JTextField txtProfileWorkerReturnDay;
    private javax.swing.JTextField txtProfileWorkerReturnMonth;
    private javax.swing.JTextField txtProfileWorkerReturnYear;
    private javax.swing.JTextField txtProfileWorkerStatus;
    private com.toedter.calendar.JMonthChooser txtReportWorkerMonth;
    private com.toedter.calendar.JMonthChooser txtReportWorkerMonthTo;
    private com.toedter.calendar.JYearChooser txtReportWorkerYear;
    private com.toedter.calendar.JYearChooser txtReportWorkerYearTo;
    private javax.swing.JTextField txtSavingCurrentSaving;
    private com.toedter.calendar.JDateChooser txtSavingDateFrom;
    private com.toedter.calendar.JDateChooser txtSavingDateTo;
    private com.toedter.calendar.JDateChooser txtSavingReportDateFrom;
    private com.toedter.calendar.JDateChooser txtSavingReportDateTo;
    private javax.swing.JTextField txtTransactionBalance;
    private javax.swing.JTextField txtTransactionBalance1;
    private javax.swing.JTextField txtTransactionCalculate;
    private com.toedter.calendar.JDateChooser txtTransactionDate;
    private javax.swing.JTextField txtTransactionDescription;
    private javax.swing.JButton txtTransactionEnd;
    private com.toedter.calendar.JDateChooser txtTransactionListFrom;
    private com.toedter.calendar.JDateChooser txtTransactionListFrom1;
    private com.toedter.calendar.JDateChooser txtTransactionListTo;
    private com.toedter.calendar.JDateChooser txtTransactionListTo1;
    private javax.swing.JButton txtTransactionNew;
    private javax.swing.JTextField txtTransactionPayPerPerson;
    private javax.swing.JTextField txtTransactionPayPerPerson1;
    private javax.swing.JTextField txtTransactionPricePerTon;
    private javax.swing.JTextField txtTransactionSalary;
    private javax.swing.JTextField txtTransactionSalary1;
    private javax.swing.JTextField txtTransactionTotalReceived;
    private javax.swing.JTextField txtTransactionWages;
    private javax.swing.JTextField txtTransactionWagesTax;
    private javax.swing.JTextField txtTransactionWeight;
    private javax.swing.JButton ubahTransaksi;
    // End of variables declaration//GEN-END:variables
}
