package edu.wustl.cab2b.common.queryengine.result;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import edu.common.dynamicextensions.domaininterface.AttributeInterface;
import edu.wustl.cab2b.common.cache.AbstractEntityCache;

public class Record implements IRecord {
    private static final long serialVersionUID = 4657684475538003175L;

    private transient Map<AttributeInterface, String> attributesValues;

    private RecordId id;

    protected Record(Set<AttributeInterface> attributes, RecordId id) {
        if (attributes == null || attributes.isEmpty()) {
            throw new IllegalArgumentException();
        }
        if (id == null) {
            throw new IllegalArgumentException();
        }
        this.attributesValues = new HashMap<AttributeInterface, String>(attributes.size());
        for (AttributeInterface attribute : attributes) {
            attributesValues.put(attribute, "");
        }
        this.id = id;
    }

    public RecordId getRecordId() {
        return id;
    }

    public void putValueForAttribute(AttributeInterface attribute, String value) {
        if (!attributesValues.containsKey(attribute)) {
            throw new IllegalArgumentException("The attribute is invalid for this record.");
        }
        this.attributesValues.put(attribute, value);
    }

    public String getValueForAttribute(AttributeInterface attribute) {
        return attributesValues.get(attribute);
    }

    public Set<AttributeInterface> getAttributes() {
        return Collections.unmodifiableSet(attributesValues.keySet());
    }

    private void writeObject(ObjectOutputStream s) throws IOException {
        s.defaultWriteObject();
        AbstractEntityCache cache = AbstractEntityCache.getCache();

        Map<String, String> idValueMap = new HashMap<String, String>();
        boolean onlyIdsWritten = true;
        for (Map.Entry<AttributeInterface, String> entry : attributesValues.entrySet()) {
            AttributeInterface attribute = entry.getKey();
            Long entityId = attribute.getEntity().getId();
            if (!cache.isEntityPresent(entityId)) {
                s.writeObject(attributesValues);
                s.writeBoolean(false);
                return;
            }
            Long attributeId = attribute.getId();
            idValueMap.put(entityId + "_" + attributeId, entry.getValue());
        }
        s.writeObject(idValueMap);
        s.writeBoolean(onlyIdsWritten);
    }

    private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
        s.defaultReadObject();
        Map<?, ?> values = (Map<?, ?>) s.readObject();
        boolean onlyIdsWritten = s.readBoolean();

        if (!onlyIdsWritten) {
            attributesValues = (Map<AttributeInterface, String>) values;
            return;
        }
        Map<String, String> idValues = (Map<String, String>) values;
        AbstractEntityCache cache = AbstractEntityCache.getCache();
        attributesValues = new HashMap<AttributeInterface, String>();

        for (Map.Entry<String, String> entry : idValues.entrySet()) {
            String[] key = entry.getKey().split("_");
            Long entityId = Long.parseLong(key[0]);
            Long attributeId = Long.parseLong(key[1]);
            AttributeInterface attribute = cache.getEntityById(entityId).getAttributeByIdentifier(attributeId);
            attributesValues.put(attribute, entry.getValue());
        }
    }
}