
package edu.wustl.cab2b.server.ejb.datalist;

import java.rmi.RemoteException;
import java.util.List;

import edu.common.dynamicextensions.exception.DynamicExtensionsApplicationException;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.wustl.cab2b.common.datalist.DataList;
import edu.wustl.cab2b.common.datalist.DataListBusinessInterface;
import edu.wustl.cab2b.common.domain.DataListMetadata;
import edu.wustl.cab2b.server.datalist.DataListOperations;
import edu.wustl.cab2b.server.ejb.AbstractStatelessSessionBean;
import edu.wustl.common.exception.BizLogicException;
import edu.wustl.common.security.exceptions.UserNotAuthorizedException;
import edu.wustl.common.util.dbManager.DAOException;

public class DataListBean extends AbstractStatelessSessionBean 
						implements DataListBusinessInterface
{
	private static final long serialVersionUID = 1234567890L;

	/**
	 * @throws DynamicExtensionsSystemException 
	 * @throws ClassNotFoundException 
	 * @throws DAOException 
	 * @see DataListBusinessInterface#retrieveAllDataListMetadata()
	 */
	public List<DataListMetadata> retrieveAllDataListMetadata() throws 
			RemoteException, DynamicExtensionsSystemException, DAOException, ClassNotFoundException
	{
		return (new DataListOperations()).retrieveAllDataListMetadata();
	}

	/**
	 * @see DataListBusinessInterface#retreiveDataList(Long)
	 */
	public DataList retrieveDataList(Long dataListId) throws BizLogicException,
			UserNotAuthorizedException, RemoteException
	{
		return (new DataListOperations()).retrieveDataList(dataListId);
	}
	
	public DataListMetadata retrieveDataListMetadata(Long id) throws RemoteException, DAOException
	{
		List results = (new DataListOperations()).retrieve("DataListMetadata", "id", id);
		if(results != null && results.size() > 0)
			return (DataListMetadata)results.get(0);
		return null;
	}
	
	/**
	 * @throws UserNotAuthorizedException 
	 * @throws BizLogicException 
	 * @throws DAOException 
	 * @throws RemoteException 
	 * @throws ClassNotFoundException 
	 * @see DataListBusinessInterface#saveDataList(DataList)
	 */
	public Long saveDataList(DataList dataList) throws DynamicExtensionsApplicationException,
			DynamicExtensionsSystemException, DAOException, BizLogicException, UserNotAuthorizedException
	{
		return (new DataListOperations()).save(dataList);
	}
}
