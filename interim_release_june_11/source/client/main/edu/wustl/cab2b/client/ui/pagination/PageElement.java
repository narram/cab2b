package edu.wustl.cab2b.client.ui.pagination;

/**
 *
 * @author chetan_bh
 */
public interface PageElement {

	public String getDescription();
	
	public void setDescription(String description);
	
	public String getDisplayName();
	
	public void setDisplayName(String displayName);
	
	public String getImageLocation();
	
	public void setImageLocation(String imageLocation);
	
	public String getLinkURL();
	
	public void setLinkURL(String linkURL);
	
	public Object getUserObject();
	
	public void setUserObject(Object userObject);
	
	public boolean isSelected();
	
	public void setSelected(boolean value);
}
