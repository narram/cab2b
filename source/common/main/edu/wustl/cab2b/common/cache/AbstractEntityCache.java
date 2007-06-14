package edu.wustl.cab2b.common.cache;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.common.dynamicextensions.domaininterface.AssociationInterface;
import edu.common.dynamicextensions.domaininterface.AttributeInterface;
import edu.common.dynamicextensions.domaininterface.EntityGroupInterface;
import edu.common.dynamicextensions.domaininterface.EntityInterface;
import edu.common.dynamicextensions.domaininterface.PermissibleValueInterface;
import edu.wustl.cab2b.common.beans.MatchedClass;
import edu.wustl.cab2b.common.exception.RuntimeException;
import edu.wustl.cab2b.common.util.Constants;
import edu.wustl.cab2b.common.util.Utility;
import edu.wustl.cab2b.server.util.InheritanceUtil;
import edu.wustl.common.querysuite.metadata.category.Category;
import edu.wustl.common.util.logger.Logger;

/**
 * This class is used to cache the Entity and its Attribute objects.
 * 
 * @author Chandrakant Talele
 * @author gautam_shetty
 * @author Rahul Ner
 */
public abstract class AbstractEntityCache implements IEntityCache, Serializable {
    protected static List<Category> categories;

    private static final long serialVersionUID = 1234567890L;

    /**
     * The EntityCache object. Needed for singleton
     */
    protected static AbstractEntityCache entityCache = null;

    public static AbstractEntityCache getCache() {
        if (entityCache == null) {
            throw new UnsupportedOperationException("Cache not present.");
        }
        return entityCache;
    }

    /**
     * Map with KEY as dynamic extension Entity's identifier and Value as Entity
     * object
     */
    protected Map<Long, EntityInterface> idVsEntity = new HashMap<Long, EntityInterface>();

    /**
     * Map with KEY as dynamic extension Association's identifier and Value as
     * Association object
     */
    protected Map<Long, AssociationInterface> idVsAssociation = new HashMap<Long, AssociationInterface>();

    /**
     * This map holds all the original association. Associations which are
     * replicated by cab2b are not present in this map Key : String to identify
     * a parent association uniquely.Generated by
     * {@link InheritanceUtil#generateUniqueId(AssociationInterface)} Value :
     * Original association for given string identifier
     */
    protected Map<String, AssociationInterface> originalAssociations = new HashMap<String, AssociationInterface>();

    /**
     * Map with KEY as a permissible value (PV) and VALUE as its Entity. This is
     * needed because there is no back pointer from PV to Entity
     */
    protected Map<PermissibleValueInterface, EntityInterface> permissibleValueVsEntity = new HashMap<PermissibleValueInterface, EntityInterface>();

    /**
     * Map with key as category id and value as category.
     */
    protected Map<Long, Category> categoryIdToCategory;

    /**
     * Map with key as category's entity id and value as category.
     */
    protected Map<Long, Category> entityIdToCategory;

    /**
     * Private default constructor. To restrict the user from instantiating
     * explicitly.
     */
    protected AbstractEntityCache() {
        refreshCache();
    }

    /**
     * Refreshes the entity cache.
     */
    public final void refreshCache() {
        init();
    }

    // /**
    // * @return the singleton instance of the EntityCache class.
    // */
    // public static synchronized AbstractEntityCache getInstance() {
    // if (entityCache == null) {
    // entityCache = new AbstractEntityCache();
    // }
    // return entityCache;
    // }

    /**
     * Initializes the entity cache.
     */
    private void init() throws RuntimeException {
        // try {
        Logger.out.debug("Initialising cache, this may take few minutes...");
        // EntityManagerInterface entityManager = EntityManager.getInstance();
        Collection<EntityGroupInterface> entityGroups = getCab2bEntityGroups();
        createCache(entityGroups);

        Logger.out.debug("Initialising cache DONE");
        // } catch (DynamicExtensionsSystemException dynSysExp) {
        // throw new RuntimeException(dynSysExp.getMessage(), dynSysExp);
        // // } catch (DynamicExtensionsApplicationException dynAppExp) {
        // // throw new RuntimeException(dynAppExp.getMessage(), dynAppExp);
        // }

        cacheCategories();
    }

    private void cacheCategories() {
        for (Category category : categories) {
            categoryIdToCategory.put(category.getId(), category);
            entityIdToCategory.put(category.getCategoryEntity().getId(), category);
        }
    }

    /**
     * @param entityGroups
     */
    private void createCache(Collection<EntityGroupInterface> entityGroups) {
        for (EntityGroupInterface entityGroup : entityGroups) {
            if (entityGroup.getName().equals(Constants.DATALIST_ENTITY_GROUP_NAME)) {
                continue; // Ignoring entity group of datalist for caching
            }
            for (EntityInterface entity : entityGroup.getEntityCollection()) {
                addEntityToCache(entity);
            }
        }
    }

    /**
     * @param entity
     */
    private void createAssociationCache(EntityInterface entity) {
        for (AssociationInterface association : entity.getAssociationCollection()) {
            idVsAssociation.put(association.getId(), association);
            if (!Utility.isInherited(association)) {
                originalAssociations.put(Utility.generateUniqueId(association), association);
            }
        }
    }

    /**
     * @param entity
     */
    private void createPermissibleValueCache(EntityInterface entity) {
        for (AttributeInterface attribute : entity.getAttributeCollection()) {
            for (PermissibleValueInterface value : Utility.getPermissibleValues(attribute)) {
                permissibleValueVsEntity.put(value, entity);
            }
        }
    }

    /**
     * @see edu.wustl.cab2b.common.entityCache.IEntityCache#getEntityOnEntityParameters(java.util.Collection)
     */
    public MatchedClass getEntityOnEntityParameters(Collection<EntityInterface> patternEntityCollection) {
        MatchedClass matchedClass = new MatchedClass();
        for (EntityInterface cachedEntity : idVsEntity.values()) {
            for (EntityInterface patternEntity : patternEntityCollection) {
                if (CompareUtil.compare(cachedEntity, patternEntity)) {
                    matchedClass.addEntity(cachedEntity);
                }
            }
        }
        return matchedClass;
    }

    /**
     * Returns the Entity objects whose Attribute fields match with the
     * respective not null fields in the passed Attribute object.
     * 
     * @param entity The entity object.
     * @return the Entity objects whose Attribute fields match with the
     *         respective not null fields in the passed Attribute object.
     */
    public MatchedClass getEntityOnAttributeParameters(Collection<AttributeInterface> patternAttributeCollection) {
        MatchedClass matchedClass = new MatchedClass();
        for (EntityInterface entity : idVsEntity.values()) {
            for (AttributeInterface cachedAttribute : entity.getAttributeCollection()) {
                for (AttributeInterface patternAttribute : patternAttributeCollection) {
                    if (CompareUtil.compare(cachedAttribute, patternAttribute)) {
                        matchedClass.addAttribute(cachedAttribute);
                        matchedClass.addEntity(cachedAttribute.getEntity());
                    }
                }
            }
        }
        return matchedClass;
    }

    /**
     * Returns the Entity objects whose Permissible value fields match with the
     * respective not null fields in the passed Permissible value object.
     * 
     * @param entity The entity object.
     * @return the Entity objects whose Permissible value fields match with the
     *         respective not null fields in the passed Permissible value
     *         object.
     */
    public MatchedClass getEntityOnPermissibleValueParameters(
                                                              Collection<PermissibleValueInterface> patternPermissibleValueCollection) {
        MatchedClass matchedClass = new MatchedClass();
        for (PermissibleValueInterface cachedPermissibleValue : permissibleValueVsEntity.keySet()) {
            for (PermissibleValueInterface patternPermissibleValue : patternPermissibleValueCollection) {
                if (CompareUtil.compare(cachedPermissibleValue, patternPermissibleValue)) {
                    matchedClass.addEntity(permissibleValueVsEntity.get(cachedPermissibleValue));
                }
            }
        }
        return matchedClass;

    }

    /**
     * Returns the Entity for given Identifier
     * 
     * @param id Id of the entity
     * @return Actual Entity for given id.
     */
    public EntityInterface getEntityById(Long id) {
        EntityInterface entity = idVsEntity.get(id);
        if (entity == null) {
            throw new RuntimeException("Entity with given id is not present in cache : " + id);
        }
        return entity;
    }

    /**
     * Checks if entity with given id is present in cache.
     * 
     * @param id the entity id
     * @return <code>true</code> - if entity with given id is present in
     *         cache; <code>false</code> otherwise.
     */
    public boolean isEntityPresent(Long id) {
        return idVsEntity.containsKey(id);
    }

    /**
     * Returns the Association for given Identifier
     * 
     * @param id Id of the Association
     * @return Actual Association for given id.
     */
    public AssociationInterface getAssociationById(Long id) {
        AssociationInterface association = idVsAssociation.get(id);
        if (association == null) {
            throw new RuntimeException("Entity with given id is not present in cache : " + id);
        }
        return association;
    }

    /**
     * Returns the Association for given string. Passed string MUST be of format
     * SourceEntityName +
     * {@link edu.wustl.cab2b.common.util.Constants#CONNECTOR} + TargetRoleName +
     * {@link edu.wustl.cab2b.common.util.Constants#CONNECTOR} +
     * TargetEntityName It can be generated by
     * {@link Utility#generateUniqueId(AssociationInterface)}
     * 
     * @param uniqueStringIdentifier unique String Identifier
     * @return Actual Association for given string identifier.
     */
    public AssociationInterface getAssociationByUniqueStringIdentifier(String uniqueStringIdentifier) {
        AssociationInterface association = originalAssociations.get(uniqueStringIdentifier);
        if (association == null) {
            throw new RuntimeException(
                    "Association with given source entity name and target role name is not present in cache : "
                            + uniqueStringIdentifier);
        }
        return association;
    }

    /**
     * @see edu.wustl.cab2b.common.entityCache.IEntityCache#addEntity(edu.common.dynamicextensions.domaininterface.EntityInterface)
     */
    public void addEntityToCache(EntityInterface entity) {
        idVsEntity.put(entity.getId(), entity);
        createAssociationCache(entity);
        createPermissibleValueCache(entity);
    }

    public Category getCategoryById(Long id) {
        Category category = categoryIdToCategory.get(id);
        if (category == null) {
            throw new RuntimeException("Category with given id not found.");
        }
        return category;
    }

    public Category getCategoryByEntityId(Long id) {
        Category category = entityIdToCategory.get(id);
        if (category == null) {
            throw new RuntimeException("Category with given entity id not found.");
        }
        return category;
    }

    protected abstract Collection<EntityGroupInterface> getCab2bEntityGroups();
}