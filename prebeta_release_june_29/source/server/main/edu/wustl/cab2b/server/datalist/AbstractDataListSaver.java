package edu.wustl.cab2b.server.datalist;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import edu.common.dynamicextensions.domain.DomainObjectFactory;
import edu.common.dynamicextensions.domaininterface.AbstractAttributeInterface;
import edu.common.dynamicextensions.domaininterface.AttributeInterface;
import edu.common.dynamicextensions.domaininterface.EntityGroupInterface;
import edu.common.dynamicextensions.domaininterface.EntityInterface;
import edu.wustl.cab2b.common.queryengine.result.IRecord;
import edu.wustl.cab2b.common.queryengine.result.RecordId;
import edu.wustl.cab2b.common.util.AttributeInterfaceComparator;
import edu.wustl.cab2b.server.util.DynamicExtensionUtility;

import static edu.wustl.cab2b.common.util.DataListUtil.ORIGIN_ENTITY_ID_KEY;
import static edu.wustl.cab2b.common.util.DataListUtil.SOURCE_ENTITY_ID_KEY;

/**
 * Skeletal implementation of a {@link DataListSaver}. A concrete
 * implementation need only implement the method
 * <code>populateNewEntity()</code> to add attributes and/or associations to
 * the newly created entity.
 * <p>
 * A new entity is created on a call to <code>initialize()</code>. The new
 * entity has tags for
 * {@link edu.wustl.cab2b.common.util.DataListUtil#ORIGIN_ENTITY_ID_KEY} and
 * {@link edu.wustl.cab2b.common.util.DataListUtil#SOURCE_ENTITY_ID_KEY}. Two
 * attributes (for id and url of the record) are added to this new entity. These
 * are called "virtual attributes". A virtual attribute is one present in the
 * new entity, but is not in the valuesMap of {@link IRecord}.
 * <p>
 * populateNewEntity()</code> is then called; subclasses are expected to
 * populate the new entity with attributes/associations appropriately. e.g. the
 * default saver merely copies non-virtual attributes from the old entity,
 * whereas FooBarSaver would create an additional virtual attribute in the new
 * entity called "foo".
 * 
 * @author srinath_k
 * 
 * @param <R>
 */
public abstract class AbstractDataListSaver<R extends IRecord> implements DataListSaver<R> {
    protected EntityInterface newEntity;

    protected boolean initialized = false;

    public void initialize(EntityInterface oldEntity) {
        this.newEntity = createNewEntity(oldEntity);
        populateNewEntity(oldEntity);
        setInitialized(true);
    }

    protected final boolean isInitialized() {
        return initialized;
    }

    protected final void setInitialized(boolean initialized) {
        this.initialized = initialized;
    }

    public final Map<AbstractAttributeInterface, Object> getRecordAsMap(R record) {
        if (!isInitialized()) {
            throw new IllegalStateException();
        }
        Map<AbstractAttributeInterface, Object> recordsMap = transformToMap(record);
        putRecordIdInMap(record.getRecordId(), recordsMap, newEntity);

        return recordsMap;
    }

    protected Map<AbstractAttributeInterface, Object> transformToMap(R record) {
        List<AttributeInterface> recordAttributes = new ArrayList<AttributeInterface>(record.getAttributes());

        List<AttributeInterface> newEntityAttributes = new ArrayList<AttributeInterface>(
                newEntity.getAttributeCollection());

        for (Iterator<AttributeInterface> iter = newEntityAttributes.iterator(); iter.hasNext();) {
            if (DataListUtil.isVirtualAttribute(iter.next())) {
                iter.remove();
            }
        }
        // set of nonVirtual attributes in newEntity equals (by name) set of
        // attributes in
        // record (FOR THE DEFAULT CODE).
        if (!(recordAttributes.size() == newEntityAttributes.size())) {
            throw new IllegalArgumentException();
        }
        Collections.sort(recordAttributes, new AttributeInterfaceComparator());
        Collections.sort(newEntityAttributes, new AttributeInterfaceComparator());

        Map<AbstractAttributeInterface, Object> recordsMap = new HashMap<AbstractAttributeInterface, Object>();
        for (int i = 0; i < newEntityAttributes.size(); i++) {
            recordsMap.put(newEntityAttributes.get(i), record.getValueForAttribute(recordAttributes.get(i)));
        }
        return recordsMap;
    }

    public final EntityInterface getNewEntity() {
        if (!isInitialized()) {
            throw new IllegalStateException();
        }
        return this.newEntity;
    }

    private EntityInterface createNewEntity(EntityInterface oldEntity) {
        EntityInterface newEntity = getDomainObjectFactory().createEntity();
        EntityGroupInterface dataListEntityGroup = DataListUtil.getDatalistEntityGroup();
        newEntity.addEntityGroupInterface(dataListEntityGroup);
        dataListEntityGroup.addEntity(newEntity);

        newEntity.setName(oldEntity.getName());

        DynamicExtensionUtility.addTaggedValue(newEntity, ORIGIN_ENTITY_ID_KEY,
                                               getOriginEntityId(oldEntity).toString());
        DynamicExtensionUtility.addTaggedValue(newEntity, SOURCE_ENTITY_ID_KEY, oldEntity.getId().toString());

        DynamicExtensionUtility.addTaggedValue(newEntity,
                                               edu.wustl.cab2b.common.util.Constants.ENTITY_DISPLAY_NAME,
                                               edu.wustl.cab2b.common.util.Utility.getDisplayName(oldEntity));
        addVirtualAttributes(newEntity);

        return newEntity;
    }

    private Long getOriginEntityId(EntityInterface oldEntity) {
        return edu.wustl.cab2b.common.util.DataListUtil.getOriginEntity(oldEntity).getId();
    }

    protected abstract void populateNewEntity(EntityInterface oldEntity);

    protected final void addVirtualAttributes(EntityInterface entity) {
        AttributeInterface idAttribute = getDomainObjectFactory().createStringAttribute();
        idAttribute.setName(DataListUtil.ID_ATTRIBUTE_NAME);
        DataListUtil.markVirtual(idAttribute);

        AttributeInterface urlAttribute = getDomainObjectFactory().createStringAttribute();
        urlAttribute.setName(DataListUtil.URL_ATTRIBUTE_NAME);
        DataListUtil.markVirtual(urlAttribute);

        entity.addAttribute(idAttribute);
        entity.addAttribute(urlAttribute);
    }

    protected final void putRecordIdInMap(RecordId recordId, Map<AbstractAttributeInterface, Object> map,
                                          EntityInterface entity) {
        map.put(DataListUtil.getVirtualIdAttribute(entity), recordId.getId());
        map.put(DataListUtil.getVirtualUrlAttribute(entity), recordId.getUrl());
    }

    protected final DomainObjectFactory getDomainObjectFactory() {
        return DomainObjectFactory.getInstance();
    }
}