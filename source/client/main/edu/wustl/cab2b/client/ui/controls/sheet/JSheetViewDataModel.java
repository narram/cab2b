/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.wustl.cab2b.client.ui.controls.sheet;

import javax.swing.*;
import javax.swing.table.*;

/**
 *
 * @author jasbir_sachdeva
 */
public class JSheetViewDataModel extends AbstractTableModel {

    JTable tblData;
    TableModel compositeDataModel;

    JSheetViewDataModel(JTable tblData, TableModel compositeDataModel) {
        this.tblData = tblData;
        this.compositeDataModel = compositeDataModel;
    }

    public int getRowCount() {
        return tblData.getRowCount();
    }

    public int getColumnCount() {
        return tblData.getColumnCount();
    }

    public Object getValueAt(int rowIndex, int columnIndex) {
        return tblData.getValueAt(rowIndex, columnIndex);
    }

    public int convertRowIndexToModel(int viewRowIndex) {
        return tblData.convertRowIndexToModel(viewRowIndex);
    }

    public int convertColumnIndexToModel(int viewColumnIndex) {
        return tblData.convertColumnIndexToModel(viewColumnIndex);
    }

    public TableModel getJSheetDataModel() {
        return compositeDataModel;
    }
}
