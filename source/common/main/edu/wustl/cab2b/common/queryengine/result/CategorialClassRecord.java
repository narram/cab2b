package edu.wustl.cab2b.common.queryengine.result;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.common.dynamicextensions.domaininterface.AttributeInterface;
import edu.wustl.common.querysuite.metadata.category.CategorialClass;

public class CategorialClassRecord extends Record implements
        ICategorialClassRecord {
    private static final long serialVersionUID = -7902568245257677861L;

    private Map<CategorialClass, List<ICategorialClassRecord>> childrenCategorialClassRecords;

    private CategorialClass categorialClass;

    protected CategorialClassRecord(
            CategorialClass categorialClass,
            Set<AttributeInterface> attributes, String id) {
        super(attributes, id);
        childrenCategorialClassRecords = new HashMap<CategorialClass, List<ICategorialClassRecord>>();
        this.categorialClass = categorialClass;
    }

    public CategorialClass getCategorialClass() {
        return categorialClass;
    }

    public void setCategorialClass(CategorialClass categorialClass) {
        this.categorialClass = categorialClass;
    }

    public Map<CategorialClass, List<ICategorialClassRecord>> getChildrenCategorialClassRecords() {
        return childrenCategorialClassRecords;
    }

    public void setChildrenCategorialClassRecords(
                                                  Map<CategorialClass, List<ICategorialClassRecord>> childrenCategorialClassRecords) {
        this.childrenCategorialClassRecords = childrenCategorialClassRecords;
    }

    public void addCategorialClassRecords(
                                          CategorialClass catClass,
                                          List<ICategorialClassRecord> catClassRecs) {
        List<ICategorialClassRecord> existingRecs = getChildrenCategorialClassRecords().get(
                                                                                            catClass);
        if (existingRecs == null) {
            getChildrenCategorialClassRecords().put(
                                                    catClass,
                                                    new ArrayList<ICategorialClassRecord>(
                                                            catClassRecs));
        } else {
            existingRecs.addAll(catClassRecs);
        }
    }
}
