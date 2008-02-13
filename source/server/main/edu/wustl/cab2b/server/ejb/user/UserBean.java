package edu.wustl.cab2b.server.ejb.user;

import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;

import edu.common.dynamicextensions.exception.DynamicExtensionsApplicationException;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.wustl.cab2b.common.ejb.user.UserBusinessInterface;
import edu.wustl.cab2b.common.user.User;
import edu.wustl.cab2b.common.user.UserInterface;
import edu.wustl.cab2b.server.ejb.AbstractStatelessSessionBean;
import edu.wustl.cab2b.server.user.UserOperations;

public class UserBean extends AbstractStatelessSessionBean implements UserBusinessInterface {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public void insertUser(User user) throws RemoteException {
		new UserOperations().insertUser(user);
	}

	public void updateUser(User user) throws RemoteException {
		new UserOperations().updateUser(user);
	}

	public Map<String, List<String>> getServiceUrlsForUser(UserInterface user) throws DynamicExtensionsSystemException, DynamicExtensionsApplicationException, RemoteException {
		return new UserOperations().getServiceURLsForUser(user);
	}

	public UserInterface getUserByName(String user) throws RemoteException {
		return new UserOperations().getUserByName(user);
	}

}