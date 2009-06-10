package edu.wustl.cab2b.client.ui.main;

import java.util.ArrayList;


public interface IComponent
{
    
    public String getCondition();
    
    public ArrayList<String> getValues();
    
    public  void setCondition(String str);
    
    public String getAttributeName();
    
    public void setValues(ArrayList<String> values);
    
    
}