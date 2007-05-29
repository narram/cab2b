package edu.wustl.cab2b.server.queryengine.resulttransformers;

import edu.wustl.cab2b.common.exception.RuntimeException;
import edu.wustl.common.util.logger.Logger;
import gov.nih.nci.cagrid.cqlquery.Attribute;
import gov.nih.nci.cagrid.cqlquery.CQLQuery;
import gov.nih.nci.cagrid.cqlquery.Group;
import gov.nih.nci.cagrid.cqlquery.Object;
import gov.nih.nci.cagrid.cqlquery.Predicate;
import gov.nih.nci.cagrid.cqlquery.QueryModifier;
import gov.nih.nci.cagrid.cqlresultset.CQLAttributeResult;
import gov.nih.nci.cagrid.cqlresultset.CQLQueryResults;
import gov.nih.nci.cagrid.cqlresultset.TargetAttribute;
import gov.nih.nci.cagrid.data.client.DataServiceClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QueryResultTransformerUtil {

    public static List<Map<String, String>> getAttributeResult(String className, String[] attributeNames,
                                                               String idAttrName, String[] ids, String url) {
        List<Map<String, String>> records = new ArrayList<Map<String, String>>();
        if (ids.length == 0) {
            return records;
        }
        List<Attribute> constraints = new ArrayList<Attribute>(ids.length);
        for (String id : ids) {
            constraints.add(createIdConstraint(idAttrName, id));
        }
        Object target;
        if (constraints.size() == 1) {
            target = createTarget(className, constraints.get(0));
        } else {
            target = createTarget(className, createGroup(constraints));
        }

        CQLQuery cql = createCQLQuery(target, createQueryModifier(attributeNames));
        CQLAttributeResult[] attributeResults = executeCQL(cql, url).getAttributeResult();

        for (CQLAttributeResult attributeResult : attributeResults) {
            Map<String, String> oneRow = new HashMap<String, String>();
            records.add(oneRow);
            for (TargetAttribute targetAttribute : attributeResult.getAttribute()) {
                String val = targetAttribute.getValue();
                oneRow.put(targetAttribute.getName(), val == null ? "" : val);
            }
        }
        return records;
    }

    public static CQLQueryResults getCQLResultsById(String className, String idAttrName, String id, String url) {
        Object target = createTarget(className, createIdConstraint(idAttrName, id));
        return executeCQL(createCQLQuery(target), url);
    }

    private static Group createGroup(List<Attribute> constraints) {
        Group group = new Group();
        group.setAttribute(constraints.toArray(new Attribute[0]));
        return group;
    }

    private static Attribute createIdConstraint(String idAttrName, String id) {
        Attribute attribute = new Attribute();
        attribute.setName(idAttrName);
        attribute.setValue(id);
        attribute.setPredicate(Predicate.EQUAL_TO);
        return attribute;
    }

    private static CQLQueryResults executeCQL(CQLQuery cql, String url) {
        Logger.out.info("Executing CQL to fetch " + cql.getTarget().getName() + " from service " + url);
        try {
            DataServiceClient dataServiceClient = new DataServiceClient(url);
            CQLQueryResults results = dataServiceClient.query(cql);
            return results;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static Object createTarget(String className) {
        Object target = new Object();
        target.setName(className);
        return target;
    }

    private static Object createTarget(String className, Attribute attribute) {
        Object target = createTarget(className);
        target.setAttribute(attribute);
        return target;
    }

    private static Object createTarget(String className, Group group) {
        Object target = createTarget(className);
        target.setGroup(group);
        return target;
    }

    private static CQLQuery createCQLQuery(Object target) {
        CQLQuery cql = new CQLQuery();
        cql.setTarget(target);
        return cql;
    }

    private static CQLQuery createCQLQuery(Object target, QueryModifier queryModifier) {
        CQLQuery cql = createCQLQuery(target);
        cql.setQueryModifier(queryModifier);
        return cql;
    }

    private static QueryModifier createQueryModifier(String[] attributesNames) {
        QueryModifier queryModifier = new QueryModifier();
        queryModifier.setAttributeNames(attributesNames);
        return queryModifier;
    }
}
