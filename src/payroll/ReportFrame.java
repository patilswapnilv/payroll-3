/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * ReportFrame.java
 *
 * Created on Aug 5, 2012, 4:30:26 PM
 */

package payroll;

import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;

/**
 *
 * @author Edward
 */
public class ReportFrame extends javax.swing.JFrame {

    /** Creates new form ReportFrame */
    public ReportFrame() {
        initComponents();
        try {
            this.setExtendedState(this.MAXIMIZED_BOTH);
            this.setAlwaysOnTop(true);
            this.toFront();
            this.requestFocus();
            this.setAlwaysOnTop(false);
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
        }
        
        this._setup();
    }

    private void _setup() {
        HTMLEditorKit kit = (HTMLEditorKit) content.getEditorKit();
        StyleSheet css = kit.getStyleSheet();
        css.addRule("body { font-family: arial; }");
        css.addRule("table, tr, td { border-style: solid; }");
        css.addRule("table { border-collapse: collapse; }");
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane2 = new javax.swing.JScrollPane();
        content = new javax.swing.JEditorPane();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        content.setContentType("text/html");
        content.setEditable(false);
        jScrollPane2.setViewportView(content);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 980, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 578, Short.MAX_VALUE)
                .addContainerGap())
        );

        java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        setBounds((screenSize.width-1016)/2, (screenSize.height-638)/2, 1016, 638);
    }// </editor-fold>//GEN-END:initComponents

    /**
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new ReportFrame().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    public javax.swing.JEditorPane content;
    private javax.swing.JScrollPane jScrollPane2;
    // End of variables declaration//GEN-END:variables

}