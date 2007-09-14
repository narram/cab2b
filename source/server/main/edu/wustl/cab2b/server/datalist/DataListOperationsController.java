package edu.wustl.cab2b.server.datalist;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.hibernate.HibernateException;
import edu.common.dynamicextensions.domain.DomainObjectFactory;
import edu.common.dynamicextensions.domain.Entity;
import edu.common.dynamicextensions.domaininterface.AbstractAttributeInterface;
import edu.common.dynamicextensions.domaininterface.AssociationInterface;
import edu.common.dynamicextensions.domaininterface.AttributeInterface;
import edu.common.dynamicextensions.domaininterface.EntityGroupInterface;
import edu.common.dynamicextensions.domaininterface.EntityInterface;
import edu.common.dynamicextensions.entitymanager.EntityManager;
import edu.common.dynamicextensions.entitymanager.EntityManagerInterface;
import edu.common.dynamicextensions.entitymanager.EntityRecordInterface;
import edu.common.dynamicextensions.entitymanager.EntityRecordResultInterface;
import edu.common.dynamicextensions.exception.DynamicExtensionsApplicationException;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.wustl.cab2b.common.IdName;
import edu.wustl.cab2b.common.datalist.CustomDataCategoryNode;
import edu.wustl.cab2b.common.datalist.DataListBusinessInterface;
import edu.wustl.cab2b.common.datalist.IDataRow;
import edu.wustl.cab2b.common.domain.DataListMetadata;
import edu.wustl.cab2b.common.domain.Experiment;
import edu.wustl.cab2b.common.errorcodes.ErrorCodeConstants;
import edu.wustl.cab2b.common.exception.CheckedException;
import edu.wustl.cab2b.common.exception.RuntimeException;
import edu.wustl.cab2b.common.queryengine.result.IRecord;
import edu.wustl.cab2b.server.cache.DatalistCache;
import edu.wustl.cab2b.server.experiment.ExperimentOperations;
import edu.wustl.cab2b.server.util.DynamicExtensionUtility;
import edu.wustl.cab2b.server.util.UtilityOperations;
import edu.wustl.common.util.dbManager.DBUtil;

public class DataListOperationsController {

	private DataListOperationsController() {

	}

	public static DataListMetadata saveDataList(IDataRow rootRecordDataRow,
			DataListMetadata dataListMetadata) {
		Map<EntityPair, AssociationInterface> associationForEntities = new HashMap<EntityPair, AssociationInterface>();
		Map<EntityInterface, DataListSaver<IRecord>> oldEntityToSaver = new HashMap<EntityInterface, DataListSaver<IRecord>>();
		Map<IDataRow, Map<AbstractAttributeInterface, Object>> dataRowToRecordsMap = new HashMap<IDataRow, Map<AbstractAttributeInterface, Object>>();

		EntityGroupInterface dataListEntityGroup = DataListUtil.getDatalistEntityGroup();
		// root stuff
		final EntityInterface rootEntity = getDomainObjectFactory().createEntity();
		rootEntity.setName("DataList_" + System.currentTimeMillis());
		rootEntity.addEntityGroupInterface(dataListEntityGroup);
		dataListEntityGroup.addEntity(rootEntity);

		oldEntityToSaver.put(null, new DataListSaver<IRecord>() {

			public EntityInterface getNewEntity() {
				return rootEntity;
			}

			public Map<AbstractAttributeInterface, Object> getRecordAsMap(IRecord record) {
				return null;
			}

			public void initialize(EntityInterface oldEntity) {

			}

		});
		dataRowToRecordsMap.put(rootRecordDataRow,
				new HashMap<AbstractAttributeInterface, Object>());
		// end root stuff.

		// init to first class level.
		List<IDataRow> currDataRows = new ArrayList<IDataRow>();
		currDataRows.addAll(rootRecordDataRow.getChildren());

		while (!currDataRows.isEmpty()) {
			List<IDataRow> nextDataRows = new ArrayList<IDataRow>();
			for (IDataRow classDataRow : currDataRows) {
				EntityInterface oldEntity = classDataRow.getEntity();
				DataListSaver<IRecord> saver = oldEntityToSaver.get(oldEntity);
				if (saver == null) {
					saver = (DataListSaver<IRecord>) DataListOperationsFactory
							.createDataListSaver(oldEntity);
					oldEntityToSaver.put(oldEntity, saver);
				}
				EntityInterface newEntity = saver.getNewEntity();
				IDataRow parentRecordDataRow = classDataRow.getParent();

				EntityInterface oldParentEntity = parentRecordDataRow.getEntity();
				EntityInterface newParentEntity = oldEntityToSaver.get(oldParentEntity)
						.getNewEntity();
				EntityPair entityPair = new EntityPair(newParentEntity, newEntity);
				AssociationInterface association = associationForEntities.get(entityPair);
				if (association == null) {
					association = DataListUtil.createAssociation(newParentEntity, newEntity);
					associationForEntities.put(entityPair, association);
				}

				for (IDataRow recordDataRow : classDataRow.getChildren()) {
					Map<AbstractAttributeInterface, Object> recordsMap = saver
							.getRecordAsMap(recordDataRow.getRecord());
					dataRowToRecordsMap.put(recordDataRow, recordsMap);

					Map<AbstractAttributeInterface, Object> parentRecordsMap = dataRowToRecordsMap
							.get(parentRecordDataRow);
					DataListUtil.getAssociatedRecordsList(parentRecordsMap, association).add(
							recordsMap);

					nextDataRows.addAll(recordDataRow.getChildren());
				}

			}
			currDataRows = nextDataRows;
		}

		// remove the sentinel.
		oldEntityToSaver.remove(null);
		EntityManagerInterface entityManager = EntityManager.getInstance();
		try {
			Long rootEntityId = entityManager.persistEntity(rootEntity, false).getId();
			dataListMetadata.setRootEntityId(rootEntityId);

			entityManager.insertData(rootEntity, dataRowToRecordsMap.get(rootRecordDataRow));

			for (DataListSaver<IRecord> saver : oldEntityToSaver.values()) {
				EntityInterface newEntity = saver.getNewEntity();
				Long originalEntityId = edu.wustl.cab2b.common.util.DataListUtil.getOriginEntity(
						newEntity).getId();
				dataListMetadata.addEntityInfo(newEntity.getId(), newEntity.getName(),
						originalEntityId);
				addToCache(newEntity);
			}
			saveDataListMetadata(dataListMetadata);
			return dataListMetadata;
		} catch (DynamicExtensionsSystemException e) {
			throw (new RuntimeException(e.getMessage(), e, ErrorCodeConstants.DATALIST_SAVE_ERROR));
		} catch (DynamicExtensionsApplicationException e) {
			throw (new RuntimeException(e.getMessage(), e, ErrorCodeConstants.DATALIST_SAVE_ERROR));
		}
	}

	/**
	 * Returns entity record object
	 * 
	 * @param Long
	 *            entityId
	 * @return a EntityRecordResultInterface obj
	 */
	public static List<IRecord> getEntityRecords(Long entityId) {
		EntityInterface entity = DatalistCache.getInstance().getEntityWithId(entityId);
		DataListRetriever<IRecord> retriever = (DataListRetriever<IRecord>) DataListOperationsFactory
				.createDataListRetriever(entity);
		return retriever.getEntityRecords(null);

	}

	private static DomainObjectFactory getDomainObjectFactory() {
		return DomainObjectFactory.getInstance();
	}

	/**
	 * Saves data list metadata.
	 * 
	 * @see DataListBusinessInterface#saveDataListMetadata(DataListMetadata)
	 */
	private static void saveDataListMetadata(DataListMetadata datalistMetadata) {
		new DataListMetadataOperations().saveDataListMetadata(datalistMetadata);
	}

	private static void addToCache(EntityInterface newEntity) {
		DatalistCache.getInstance().addEntity(newEntity);
	}

	public static DataListMetadata saveDataCategory(IdName rootEntityIdName,
			Collection<AttributeInterface> selectedAttributeList, Long dataListId, String name)
			throws CheckedException, RemoteException {
		EntityInterface rootEntity;
		try {
			rootEntity = EntityManager.getInstance().getEntityByIdentifier(rootEntityIdName.getId());
		} catch (DynamicExtensionsSystemException e) {
			throw new CheckedException(e);
		} catch (DynamicExtensionsApplicationException e) {
			throw new CheckedException(e);
		}

		Map<AttributeInterface, AttributeInterface> oldToNewAttribute = new HashMap<AttributeInterface, AttributeInterface>();
		EntityInterface customEntity = DataListUtil.createNewEntity(rootEntity);
		// customEntity.setName("e11");
		for (AttributeInterface attributeInterface : selectedAttributeList) {
			AttributeInterface newAttribute = DataListUtil.createNewAttribute(attributeInterface);
			newAttribute.setName(attributeInterface.getName());
			customEntity.addAttribute(newAttribute);
			oldToNewAttribute.put(attributeInterface, newAttribute);
		}
		try {
			EntityManager.getInstance().persistEntity(customEntity);
			addToCache(customEntity);
		} catch (DynamicExtensionsSystemException e1) {
			throw new CheckedException(e1);
		} catch (DynamicExtensionsApplicationException e1) {
			throw new CheckedException(e1);
		}
		// popaulate data
		Map<EntityInterface, CustomDataCategoryNode> entityToDataCategoryPathMap = new HashMap<EntityInterface, CustomDataCategoryNode>();

		for (AttributeInterface attribute : selectedAttributeList) {
			EntityInterface entity = attribute.getEntity();
			CustomDataCategoryNode dataCategoryPath = entityToDataCategoryPathMap.get(entity);
			if (dataCategoryPath == null) {
				dataCategoryPath = new CustomDataCategoryNode();
				entityToDataCategoryPathMap.put(entity, dataCategoryPath);
			}
			dataCategoryPath.addAttribute(attribute);
		}

		joinTree(rootEntity, entityToDataCategoryPathMap);
		CustomDataCategoryNode rooDataCategoryNode = entityToDataCategoryPathMap.get(rootEntity);

		EntityRecordResultInterface recordResult = null;
		List<AbstractAttributeInterface> list = new ArrayList<AbstractAttributeInterface>();
		list.addAll(rootEntity.getAbstractAttributeCollection());
		try {
			recordResult = EntityManager.getInstance().getEntityRecords(rootEntity, list, null);
		} catch (DynamicExtensionsSystemException e) {
			throw new CheckedException(e);
		}

		Map<AbstractAttributeInterface, Object> attributeValues = new HashMap<AbstractAttributeInterface, Object>();

		processResult(recordResult, rooDataCategoryNode, attributeValues, oldToNewAttribute,
				customEntity);

		DataListMetadata customDataListMetadata = saveCustomDataListMetaData(customEntity, name,rootEntityIdName.getId());
		Collection experimentCollection;
		try {
			experimentCollection = new ExperimentOperations()
					.getExperimentsWithSimilarDataList(dataListId);
		} catch (HibernateException e) {
			throw new CheckedException(e);
		}

		saveDlMetaDataToAllExpts(experimentCollection, customDataListMetadata);

		try {
			DBUtil.closeSession();
		} catch (HibernateException e) {
			throw new CheckedException(e);
		}
		return customDataListMetadata;
	}

	private static void saveDlMetaDataToAllExpts(Collection<Experiment> experimentCollection,
			DataListMetadata customDataListMetadata) {
		for (Experiment expt : experimentCollection) {
			expt.addDataListMetadata(customDataListMetadata);
			new UtilityOperations().update(expt);
		}
	}

	private static void processResult(EntityRecordResultInterface recordResult,
			CustomDataCategoryNode dataCategoryNode,
			Map<AbstractAttributeInterface, Object> attributeValues,
			Map<AttributeInterface, AttributeInterface> oldToNewAttribute,
			EntityInterface customEntity) throws CheckedException {

		for (EntityRecordInterface record : recordResult.getEntityRecordList()) {
			for (AttributeInterface oldAttribute : dataCategoryNode.getAttributeList()) {
				int index = recordResult.getEntityRecordMetadata().getAttributeList().indexOf(
						oldAttribute);
				Object value = record.getRecordValueList().get(index);
				attributeValues.put(oldToNewAttribute.get(oldAttribute), value);

			}
			Set<AssociationInterface> associationList = dataCategoryNode.getAssociationList();
			if (associationList.size() == 0) {
				try {
					EntityManager.getInstance().insertData(customEntity, attributeValues);
					System.out.println("inserted");

				} catch (DynamicExtensionsApplicationException e) {
					throw new CheckedException(e);
				} catch (DynamicExtensionsSystemException e) {
					throw new CheckedException(e);
				}
			} else {
				for (AssociationInterface association : dataCategoryNode.getAssociationList()) {
					int index = recordResult.getEntityRecordMetadata().getAttributeList().indexOf(
							association);
					EntityRecordResultInterface associationResult = (EntityRecordResultInterface) record
							.getRecordValueList().get(index);
					CustomDataCategoryNode associationDataCategoryNode = dataCategoryNode
							.getAssociationDetails(association);

					processResult(associationResult, associationDataCategoryNode, attributeValues,
							oldToNewAttribute, customEntity);
				}
			}
		}

	}

	private static void joinTree(EntityInterface entity,
			Map<EntityInterface, CustomDataCategoryNode> entityToDataCategoryPathMap) {

		for (AssociationInterface association : entity.getAssociationCollection()) {
			EntityInterface targetEntity = association.getTargetEntity();

			joinTree(targetEntity, entityToDataCategoryPathMap);

			if (entityToDataCategoryPathMap.containsKey(targetEntity)) {

				CustomDataCategoryNode parentdataCategoryPath = entityToDataCategoryPathMap
						.get(entity);
				if (parentdataCategoryPath == null) {
					parentdataCategoryPath = new CustomDataCategoryNode();
					entityToDataCategoryPathMap.put(entity, parentdataCategoryPath);
				}
				parentdataCategoryPath.addAssociation(association, entityToDataCategoryPathMap
						.get(targetEntity));

			}
		}

	}

	/**
	 * @param entity
	 * @param name
	 * @param originalEntityId id of origianl root entoity
	 * @return 
	 */
	private static DataListMetadata saveCustomDataListMetaData(EntityInterface entity, String name, Long originalEntityId) {
		DataListMetadata dataList = new DataListMetadata();
		dataList.setName(name);
		dataList.setCreatedOn(new Date());
		dataList.setLastUpdatedOn(new Date());
		dataList.setCustomDataCategory(true);
		dataList.addEntityInfo(entity.getId(),name, originalEntityId);
		new DataListMetadataOperations().saveDataListMetadata(dataList);

		return dataList;
	}
}
