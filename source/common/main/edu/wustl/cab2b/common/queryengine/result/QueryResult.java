package edu.wustl.cab2b.common.queryengine.result;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.common.dynamicextensions.domaininterface.EntityInterface;

public class QueryResult<R extends IRecord> implements IQueryResult<R> {
    private Map<String, List<R>> records;

    private EntityInterface outputEntity;

    private static final long serialVersionUID = -6100202491215936158L;

    protected QueryResult(EntityInterface outputEntity) {
        this.records = new HashMap<String, List<R>>();
    }

    public Map<String, List<R>> getRecords() {
        return Collections.unmodifiableMap(records);
    }

    public void addRecord(String url, R record) {
        getRecordsForUrl(url).add(record);
    }

    private List<R> getRecordsForUrl(String url) {
        List<R> val;
        if (records.containsKey(url)) {
            val = records.get(url);
        } else {
            val = new ArrayList<R>();
            records.put(url, val);
        }
        return val;
    }

    public void addRecords(String url, List<R> records) {
        List<R> existingRecords = getRecordsForUrl(url);
        existingRecords.addAll(records);
    }

    public EntityInterface getOutputEntity() {
        return outputEntity;
    }

}
