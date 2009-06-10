package edu.wustl.cab2b.common.datalist;

import java.rmi.RemoteException;
import java.util.List;

import edu.common.dynamicextensions.entitymanager.EntityRecord;
import edu.common.dynamicextensions.entitymanager.EntityRecordResultInterface;
import edu.wustl.cab2b.common.BusinessInterface;
import edu.wustl.cab2b.common.domain.DataListMetadata;


public interface DataListBusinessInterface extends BusinessInterface
{
	
	/**
	 * Saves the data list.
	 * @param dataList
	 * @return data list id.
	 * @throws RemoteException
	 */
	public Long saveDataList(DataList dataList) throws RemoteException;

	/**
	 * Retrieves annotation information for all the data lists stored.
	 * @return list of all available data list metadata.
	 * @throws RemoteException
	 */
	public List<DataListMetadata> retrieveAllDataListMetadata() throws RemoteException;
	
	/**
	 * Retrives annotation for given datalist id.
	 * @param id
	 * @return data list metadata
	 * @throws RemoteException
	 */
	public DataListMetadata retrieveDataListMetadata(Long id) throws RemoteException;
	
	
	/**
	 * Returns a data list along with annotation.
	 * @param dataListId
	 * @return a data list.
	 * @throws RemoteException
	 */
	public DataList retrieveDataList(Long dataListId) throws RemoteException;
	
	public EntityRecordResultInterface getEntityRecord(Long entityId) throws RemoteException;
	
}
