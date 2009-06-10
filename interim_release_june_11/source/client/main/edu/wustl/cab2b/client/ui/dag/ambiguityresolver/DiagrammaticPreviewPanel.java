package edu.wustl.cab2b.client.ui.dag.ambiguityresolver;

import java.awt.Dimension;

import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.JXTitledPanel;

import edu.wustl.cab2b.client.ui.RiverLayout;
import edu.wustl.cab2b.client.ui.WindowUtilities;
import edu.wustl.cab2b.client.ui.controls.Cab2bPanel;
import edu.wustl.cab2b.client.ui.controls.Cab2bTitledPanel;

/**
 * A Panel to display the simple diagrammatic view of the 
 * Query being constructed.
 * 
 * @author chetan_bh
 */
public class DiagrammaticPreviewPanel extends Cab2bTitledPanel
{
	
	JXPanel parentPanel;
	
	public DiagrammaticPreviewPanel()
	{
		initGUI();
	}
	
	private void initGUI()
	{
		//this.setLayout(new RiverLayout());
		this.setTitle("Diagrammatic Preview");
		parentPanel = new Cab2bPanel();
		parentPanel.setLayout(new RiverLayout());
		parentPanel.setPreferredSize(new Dimension(400, 200));
		this.add(parentPanel);
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		DiagrammaticPreviewPanel diagrammaticPreviewPanel = new DiagrammaticPreviewPanel();
		WindowUtilities.showInFrame(diagrammaticPreviewPanel, "Diagrammatic Preview");
	}

}
