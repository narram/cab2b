/*
 * SheetTestFrame.java
 *
 * Created on October 4, 2007, 3:31 PM
 */

package edu.wustl.cab2b.client.ui.controls.sheet;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.util.*;

import javax.swing.table.DefaultTableModel;

/**
 *
 * @author  jasbir_sachdeva
 */
public class SheetTestFrame extends javax.swing.JFrame {
    
    private static final long serialVersionUID = 1L;


    /**
     * Creates new form SheetTestFrame
     */
    public SheetTestFrame() {
        super(" Party-Mutable-JTable Testing ");
        initComponents();
        Dimension ss = Toolkit.getDefaultToolkit().getScreenSize();
        setBounds( ss.width/8, ss.height/8, ss.width*6/8, ss.height*6/8);
        
        setUpTableComponent();
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        pack();
    }// </editor-fold>//GEN-END:initComponents
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new SheetTestFrame().setVisible(true);
            }
        });
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
    
    private void setUpTableComponent() {
        JSheet pmt = new JSheet();
        getContentPane().add( pmt);
        
        //  Set fixed data model...
        pmt.setReadOnlyDataModel( new SampleTableDataModel());
//        pmt.setConsoleVisible( false);
//        pmt.setMagnifyingGlassVisible( false);
    }
    
    
    static class SampleTableDataModel extends DefaultTableModel {
        private static final long serialVersionUID = 1L;
        static Date date = null;
        static {
            Calendar c = Calendar.getInstance();
            c.set(10,10,10);
            date = c.getTime();
        }
        static Object rows[][] = {
            {"AMZN", "Amazon", 41.28,new Date(),"Pune"},
            {"EBAY", "eBay", 41.57,new Date(),"Mumbai"},
            {"GOOG", "Google", 388.33,new Date(),"Bangalore"},
            {"MSFT", "Microsoft", 26.56,new Date(),"Hydrabad"},
            {"NOK", "Nokia Corp", 17.13,new Date(),"Bangalore"},
            {"ORCL", "Oracle Corp.", 12.52,new Date(),"Pune"},
            {"SUNW", "Sun Microsystems", 3.86,new Date(),"Mumbai"},
            {"TWX",  "SunJava", 17.66,new Date(),"Pune"},
            {"VOD",  "Vodafone Group", 26.02,new Date(),"Bangalore"},
            {"AMZN", "Amazon", 41.28,new Date(),"Pune"},
            {"EBAY", "eBay", 41.57,new Date(),"Mumbai"},
            {"GOOG", "Google", 388.33,new Date(),"Bangalore"},
            {"MSFT", "Microsoft", 26.56,new Date(),"Hydrabad"},
            {"NOK", "Nokia Corp", 17.13,new Date(),"Bangalore"},
            {"ORCL", "Oracle Corp.", 12.52,new Date(),"Pune"},
            {"SUNW", "Sun Microsystems", 3.86,new Date(),"Mumbai"},
            {"TWX",  "SunJava", 17.66,new Date(),"Pune"},
            {"VOD",  "Vodafone Group", 26.02,new Date(),"Bangalore"},
            {"AMZN", "Amazon", 41.28,new Date(),"Pune"},
            {"EBAY", "eBay", 41.57,new Date(),"Mumbai"},
            {"GOOG", "Google", 388.33,new Date(),"Bangalore"},
            {"MSFT", "Microsoft", 26.56,new Date(),"Hydrabad"},
            {"NOK", "Nokia Corp", 17.13,new Date(),"Bangalore"},
            {"ORCL", "Oracle Corp.", 12.52,new Date(),"Pune"},
            {"SUNW", "Sun Microsystems", 3.86,new Date(),"Mumbai"},
            {"TWX",  "SunJava", 17.66,new Date(),"Pune"},
            {"VOD",  "Vodafone Group", 26.02,new Date(),"Bangalore"},
            {"YHOO", "Yahoo!", 3700.69,date,"Bangalore"}
        };
        static Object columns[] = {"Symbol", "Name", "Price","Date","City"};
        
        SampleTableDataModel() {
            
            super( rows, columns);
        }
        
        @Override
        public Class<?> getColumnClass(int column) {
            Class<?> returnValue;
            if ((column >= 0) && (column < getColumnCount())) {
                returnValue = getValueAt(0, column).getClass();
            } else {
                returnValue = Object.class;
            }
            return returnValue;
        }
        
        @Override
        public boolean isCellEditable(int arg0, int arg1) {
            // TODO Auto-generated method stub
            return false;
        }
        
    }
}
