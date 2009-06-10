package edu.wustl.cab2b.common.datalist;

import java.rmi.RemoteException;

import javax.ejb.CreateException;
import javax.ejb.EJBHome;


public interface DataListHomeInterface extends EJBHome
{
	/**
     * This method creates the EJB Object.
     * @return The newly created EJB Object.
     */
    public DataListRemoteInterface create() throws RemoteException, CreateException;
}
