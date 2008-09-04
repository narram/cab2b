package edu.wustl.cab2b.common.category;

import java.io.Serializable;

/**
 * @author Hrishikesh Rajpathak
 * @hibernate.class table="CAB2B_CATEGORY_POPULARITY"
 * @hibernate.cache usage="read-write"
 * 
 */
public class CategoryPopularity implements CategoryPopularityInterface, Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private Long entityId;

    private Long popularity;

    private Long id;

    /**
     * @return Returns the popularity.
     * 
     * @hibernate.property column="POPULARITY" type="long" length="30"
     *                     not-null="true"
     */
    public long getPopularity() {
        return popularity;
    }

    /**
     * @param popularity The popularity to set.
     */
    public void setPopularity(long popularity) {
        this.popularity = popularity;
    }

    /**
     * @param entityId The entityId to set.
     */
    public void setEntityId(long entityId) {
        this.entityId = entityId;
    }

    /**
     * @hibernate.property column="ENTITY_ID" type="long" length="30"
     *                     not-null="true"
     */
    public long getEntityId() {
        return entityId;
    }

    public synchronized void incPopularity() {
        popularity++;
    }

    // for hibernate
    /**
     * @hibernate.id name="id" column="IDENTIFIER" type="long" length="30"
     *                  unsaved-value="null" generator-class="native"
     */
    @SuppressWarnings("unused")
    private Long getId() {
        return id;
    }

    /**
     * @param id The id to set.
     */
    @SuppressWarnings("unused")
    private void setId(Long id) {
        this.id = id;
    }
}
