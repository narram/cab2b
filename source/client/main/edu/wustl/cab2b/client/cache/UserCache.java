package edu.wustl.cab2b.client.cache;

import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;

import edu.common.dynamicextensions.domaininterface.EntityGroupInterface;
import edu.common.dynamicextensions.domaininterface.EntityInterface;
import edu.common.dynamicextensions.exception.DynamicExtensionsApplicationException;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.wustl.cab2b.client.ui.util.CommonUtils;
import edu.wustl.cab2b.common.ejb.EjbNamesConstants;
import edu.wustl.cab2b.common.ejb.user.UserBusinessInterface;
import edu.wustl.cab2b.common.ejb.user.UserHomeInterface;
import edu.wustl.cab2b.common.user.UserInterface;
import edu.wustl.cab2b.common.util.Utility;
import edu.wustl.common.util.logger.Logger;

/**
 * @author Chandrakant Talele
 */
public class UserCache {
	UserInterface currentUser = null;

	Map<String, List<String>> entityGroupToURLs = null;

	/**
	 * The UserCache object. Needed for singleton
	 */
	protected static UserCache userCache = null;

	/**
	 * @return the singleton instance of the UserCache class.
	 */
	public static synchronized UserCache getInstance() {
		if (userCache == null) {
			userCache = new UserCache();
		}
		return userCache;
	}

	/**
	 * Private default constructor. To restrict the user from instantiating
	 * explicitly.
	 */
	protected UserCache() {

	}

	/**
	 * Refreshes the entity cache.
	 * 
	 * @throws RemoteException
	 */
	public final void init(UserInterface loggedInUser) {
		Logger.out.debug("Initialising UserCache...");
		currentUser = loggedInUser;
		entityGroupToURLs = populateServiceURLs(currentUser);
		Logger.out.debug("Initialising UserCache DONE");
	}

	/**
	 * @return the users
	 */
	public UserInterface getCurrentUser() {
		return currentUser;
	}

	public String[] getServiceURLs(EntityInterface entity) {
		EntityGroupInterface eg = Utility.getEntityGroup(entity);
		String name = eg.getLongName();
		if (entityGroupToURLs.containsKey(name)) {
			return entityGroupToURLs.get(name).toArray(new String[0]);
		} else {
			Logger.out.warn("Service URLs for this entity neither configured by user nor administrator");
		}
		return null;
	}

	/**
	 * Returns all the URLs of the data services which are exposing given entity
	 * 
	 * @param entity
	 *            Entity to check
	 * @return Returns the List of URLs
	 */
	private Map<String, List<String>> populateServiceURLs(UserInterface user) {
		Map<String, List<String>> map = null;
		UserBusinessInterface userBusinessInterface = (UserBusinessInterface) CommonUtils
				.getBusinessInterface(EjbNamesConstants.USER_BEAN, UserHomeInterface.class);
		try {
			map = userBusinessInterface.getServiceUrlsForUser(user);
		} catch (DynamicExtensionsSystemException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (DynamicExtensionsApplicationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return map;
	}
}