/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package payroll.model;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import payroll.libraries.Common;
import payroll.libraries.Database;

/**
 * 
 * @author Edward
 */
public class ReportCalculation {
	private double salary, loan, balance;
	private double payment;
	private int workerID;

	public ReportCalculation(int workerID) {
		this.salary = 0.0;
		this.loan = 0.0;
		this.balance = 0.0;
		this.workerID = workerID;
                
	}

	public ReportCalculation(double salary, double loan, double balance, int workerID) {
		this.salary = salary;
		this.loan = loan;
		this.balance = balance;
		this.workerID = workerID;
		this.payment = 0.0;
	}

	public double getBalance() {
		balance = salary - loan;
		return balance;
	}

	public double getLoan() {
		return loan;
	}

	public double getSalary() {
		return salary;
	}

	public int getWorkerID() {
		return workerID;
	}

	public void setLoan(double loan) {
		this.loan += loan;
	}

	public void setSalary(double salary) {
		this.salary += salary;
	}

	public void setPayment(double payment) {
		this.payment += payment;
	}

	public double getTotalBalance() {
		return this.getBalance() + payment;
	}

	public ReportCalculation getCalculationbyWorkingId(int workerID) {
		// TODO Auto-generated method stub
		Transaction trans = new Transaction();
		// Double salary;
		// Double loan;
		ReportCalculation calc = new ReportCalculation(workerID);
		String query = "SELECT * FROM transactions WHERE normalized_worker_id = " + workerID;
		ResultSet rs = Database.instance().execute(query);

		try {
			while (rs.next()) {
				if (rs.getInt("type") == 2) {
					//this.loan = rs.getDouble("loan_amount");
					calc.setLoan(rs.getDouble("loan_amount"));
					// this.loan +=rs.getDouble("loan_amount");
				}
				if (rs.getInt("type") == 1) {
					//this.salary = rs.getDouble("wages");
					calc.setSalary(rs.getDouble("wages"));

				}
			}
			calc.setBalance(calc.getSalary() - calc.getLoan());
		} catch (SQLException ex) {
			Logger.getLogger(Transaction2.class.getName()).log(Level.SEVERE, null, ex);
			System.err.println(ex.getMessage());
		}
		return calc;
	}

	public void setBalance(double balance) {
		this.balance = balance;
	}

}
