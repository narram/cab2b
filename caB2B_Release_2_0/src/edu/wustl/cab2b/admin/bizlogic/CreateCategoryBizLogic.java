package edu.wustl.cab2b.admin.bizlogic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.common.dynamicextensions.domaininterface.AttributeInterface;
import edu.wustl.cab2b.admin.flex.DAGLink;
import edu.wustl.cab2b.admin.flex.DAGNode;
import edu.wustl.cab2b.server.cache.EntityCache;
import edu.wustl.cab2b.server.category.InputCategorialAttribute;
import edu.wustl.cab2b.server.category.InputCategorialClass;
import edu.wustl.cab2b.server.category.InputCategory;

/**
 *  
 * @author  lalit_chand
 */
public class CreateCategoryBizLogic {
    private Set<DAGNode> dagNodeSet = null;

    private Set<DAGLink> dagLinkSet = null;

    private Map<String, String> attributeNameMap = null;

    public CreateCategoryBizLogic(
            Set<DAGNode> dagNodeSet,
            Set<DAGLink> dagLinkSet,
            Map<String, String> attributeNameMap) {
        this.dagNodeSet = dagNodeSet;
        this.dagLinkSet = dagLinkSet;
        this.attributeNameMap = attributeNameMap;
    }

    /**
     * 
     * @param dagNode
     * @return the List of all InputCategorialAttribute instance for a given InputCategorial instance.
     */
    public List<InputCategorialAttribute> getInputCategorialAttribute(DAGNode dagNode) {
        List<String> attributesList = dagNode.getAttributeList();
        List<InputCategorialAttribute> inputCategorialAttributeList = new ArrayList<InputCategorialAttribute>();
        for (String attribute : attributesList) {
            String[] attributeName = attribute.split(":");

            InputCategorialAttribute inputCategorialAttribute = new InputCategorialAttribute();
            StringBuffer key = new StringBuffer();
            key.append("").append(dagNode.getNodeId()).append(".").append(attributeName[0]);
            inputCategorialAttribute.setDisplayName(attributeNameMap.get(key.toString()));
            AttributeInterface attributeInterface = EntityCache.getInstance().getAttributeById(
                                                                                               new Long(
                                                                                                       attributeName[1]));
            inputCategorialAttribute.setDynamicExtAttribute(attributeInterface);
            inputCategorialAttributeList.add(inputCategorialAttribute);
        }

        return inputCategorialAttributeList;
    }

    /**
     * 
     * @param nodeId
     * @return It retuns the parent path 
     */
    public long getParentPathId(long nodeId) {
        DAGLink path = null;
        for (DAGLink link : dagLinkSet) {
            if (link.getDestinatioNodeId() == nodeId) {
                path = link;
                break;
            }
        }

        long parentPathId = -1L;
        if (path != null) {
            parentPathId = new Long(path.getPathId());
        }

        return parentPathId;
    }

    /**
     * 
     * @param nodeId
     * @return It returns all the children nodes
     */

    public List<DAGNode> getDirectChildren(long nodeId) {
        List<DAGNode> nodeLink = new ArrayList<DAGNode>();
        long searchNodeId = nodeId;
        for (DAGLink link : dagLinkSet) {
            if (link.getSourceNodeId() == searchNodeId) {
                for (DAGNode dagNode : dagNodeSet) {
                    if (link.getDestinatioNodeId() == dagNode.getNodeId()) {
                        nodeLink.add(dagNode);
                    }
                }
            }
        }

        return nodeLink;
    }

    /**
     * 
     * @param name
     * @param description
     * @return this method returns the InputCategory instances . 
     */
    public InputCategory getInputCategory(String name, String description) {
        InputCategory inputCategory = new InputCategory();
        inputCategory.setName(name);
        inputCategory.setDescription(description);
        inputCategory.setSubCategories(new ArrayList<InputCategory>());
        List<InputCategorialClass> inputCategorialClass = getInputCategorialClass();
        for (int i = 0; i < inputCategorialClass.size(); i++) {
            if (inputCategorialClass.get(i).getPathFromParent() == -1L) {
                inputCategory.setRootCategorialClass(inputCategorialClass.get(i));
                break;
            }
        }

        return inputCategory;
    }

    /**
     * This methods return the list containig all the InputCategorialClass instances.
     *  
     * @return List of InputCategorialClass
     */
    public List<InputCategorialClass> getInputCategorialClass() {
        Map<DAGNode, InputCategorialClass> map = new HashMap<DAGNode, InputCategorialClass>();
        List<InputCategorialClass> listInputCategorialClass = new ArrayList<InputCategorialClass>();
        for (DAGNode dagNode : dagNodeSet) {
            InputCategorialClass inputCategorialClass = new InputCategorialClass();
            inputCategorialClass.setAttributeList(getInputCategorialAttribute(dagNode));
            if (getParentPathId(dagNode.getNodeId()) != -1L) {
                inputCategorialClass.setPathFromParent(getParentPathId(dagNode.getNodeId()));
            } else {
                inputCategorialClass.setPathFromParent(-1L);
            }
            map.put(dagNode, inputCategorialClass);
            listInputCategorialClass.add(inputCategorialClass);
        }

        Set<DAGNode> keySet = map.keySet();
        for (DAGNode dagNode : keySet) {
            List<InputCategorialClass> listCategorialClass = new ArrayList<InputCategorialClass>();
            InputCategorialClass inputCategorialClass = map.get(dagNode);

            List<DAGNode> listNode = getDirectChildren(dagNode.getNodeId());
            for (DAGNode temp : listNode) {
                listCategorialClass.add(map.get(temp));
            }

            inputCategorialClass.setChildren(listCategorialClass);
        }

        return listInputCategorialClass;
    }

}
