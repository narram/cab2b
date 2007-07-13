package edu.wustl.cab2b.client.ui.dag.ambiguityresolver;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Set;

import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;

import edu.wustl.cab2b.client.ui.controls.Cab2bButton;
import edu.wustl.cab2b.client.ui.controls.Cab2bPanel;
import edu.wustl.cab2b.client.ui.controls.RadioButtonEditor;
import edu.wustl.cab2b.client.ui.controls.RadioButtonRenderer;
import edu.wustl.cab2b.client.ui.controls.RadioButtonTableModel;
import edu.wustl.cab2b.client.ui.controls.TextAreaRenderer;
import edu.wustl.common.querysuite.metadata.path.ICuratedPath;
import edu.wustl.common.querysuite.metadata.path.IPath;

public class AutoConnectAmbiguityResolver extends AbstractAmibuityResolver {
    private static final long serialVersionUID = 1L;

    private ICuratedPath[] curatedPaths;

    private ButtonGroup radioGroup = new ButtonGroup();

    /**
     * Key is a vector of source, target entity interface (which is the input to
     * ambiguity resolver). Value is a List of user selected paths, a path is a
     * PathInterfaces.
     */
    private ICuratedPath userSelectedPath;

    public AutoConnectAmbiguityResolver(Set<ICuratedPath> paths) {
        curatedPaths = paths.toArray(new ICuratedPath[0]);
        initializeGUI();
    }

    /**
     * Initializes GUI.
     */
    protected void initializeGUI() {
        JLabel jLabel = new JLabel(
                "There are multiple paths available for connecting selected categories. Select the appropriate paths.");
        add(jLabel, BorderLayout.NORTH);
        addTablePanel();
        buttonPanel = getButtonPanel();
        this.add(buttonPanel, BorderLayout.SOUTH);

        revalidate();
        updateUI();
    }
    
    /**
     * This method sets the renderers to the columns of the table and adds the table searchPanel to this object
     */
    protected void addTablePanel() {
        AbstractTableModel abstractTableModel = getAmbiguityTableModel();
        ambiguityPathTable = createAmbiguityPathTable(abstractTableModel);
      
        TableColumnModel tableColumnModel = ambiguityPathTable.getColumnModel();
        tableColumnModel.getColumn(0).setCellRenderer(new RadioButtonRenderer());
        tableColumnModel.getColumn(0).setCellEditor(new RadioButtonEditor(new JCheckBox()));
        tableColumnModel.getColumn(1).setCellRenderer(new TextAreaRenderer());
        tableColumnModel.getColumn(1).setCellEditor(null);
        
        Cab2bPanel tablePanel = createTablePanel(ambiguityPathTable);
        this.add(tablePanel, BorderLayout.CENTER);
    }

    /**
     * This method defines the action listnener for the buttons and returns the button searchPanel 
     * @return searchPanel of button
     */
    private Cab2bPanel getButtonPanel() {
        ActionListener submitButtonListener = new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                userSelectedPath = null;
                DefaultTableModel defaultTableModel = (DefaultTableModel) ambiguityPathTable.getModel();
                for (int i = 0; i < defaultTableModel.getRowCount(); i++) {
                    JRadioButton button = (JRadioButton) defaultTableModel.getValueAt(i, 0);
                    if (button.isSelected() == true) {
                        userSelectedPath = curatedPaths[i];
                        break;
                    }
                }

                if (parentWindow != null) {
                    parentWindow.setVisible(false);
                }
            }
        };

        ActionListener cancelButtonListener = new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                Cab2bButton closeButton = (Cab2bButton) actionEvent.getSource();
                JPanel jPanel = (JPanel) closeButton.getParent().getParent().getParent();
                JDialog jDialog = (JDialog) jPanel.getParent().getParent().getParent();
                jDialog.dispose();
            }
        };

        return createButtonPanel(submitButtonListener, cancelButtonListener);
    }

    /**
     * Returns data for the table component.
     * 
     * @param listOfPaths
     *            list of paths.
     * @return
     */
    protected AbstractTableModel getAmbiguityTableModel() {
        int rowIndex = 0;
        Object[][] ambiguityTableData = new Object[curatedPaths.length][3];
        int pathPopularity = (int) (1 / (float) curatedPaths.length * 100.00);
        for (int i = 0; i < curatedPaths.length; i++) {
            ambiguityTableData[rowIndex][0] = new JRadioButton();
            radioGroup.add((JRadioButton) ambiguityTableData[rowIndex][0]);

            Set<IPath> internalPaths = curatedPaths[i].getPaths();
            StringBuffer fullPathName = new StringBuffer();
            for (IPath internalPath : internalPaths) {
                fullPathName.append(getFullPathNames(internalPath)).append("\n");
            }
            ambiguityTableData[rowIndex][1] = fullPathName.toString();
            ambiguityTableData[rowIndex][2] = pathPopularity + " %";
            rowIndex++;
        }

        RadioButtonTableModel radioButtonTableModel = new RadioButtonTableModel(ambiguityTableData,
                AMBIGUITY_PATH_TABLE_HEADERS);
        return radioButtonTableModel;
    }

    /**
     * This method refreshes the table data
     */
    protected void refreshAmbiguityTable() {
        this.removeAll();
        JLabel jLabel = new JLabel(
                "There are multiple paths available for connecting selected categories. Select the appropriate paths.");
        add(jLabel, BorderLayout.NORTH);
        addTablePanel();
        this.add(buttonPanel, BorderLayout.SOUTH);

        revalidate();
        updateUI();
    }

    /**
     * This method returns the user selected path
     * @return the user selected path
     */
    public ICuratedPath getUserSelectedPath() {
        return userSelectedPath;
    }

}
