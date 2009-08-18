/**
 * 
 */
package edu.wustl.cab2bwebapp.bizlogic;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import edu.wustl.cab2b.common.errorcodes.ErrorCodeConstants;
import edu.wustl.cab2b.common.exception.RuntimeException;
import edu.wustl.cab2b.common.queryengine.querystatus.AbstractStatus;
import edu.wustl.cab2b.common.queryengine.querystatus.QueryStatus;
import edu.wustl.cab2b.common.user.UserInterface;
import edu.wustl.cab2b.server.queryengine.querystatus.QueryURLStatusOperations;
import edu.wustl.cab2bwebapp.bizlogic.executequery.QueryBizLogic;

/**
 * @author deepak_shingan
 *
 */
public class UserBackgroundQueries {
    private Map<UserInterface, Set<QueryBizLogic>> userToBackgroundQueries = null;

    private static UserBackgroundQueries ref = null;

    //TODO - take file path from a property file.
    private final String filePath = "C:\\result\\";

    /**
     * private constructor for singleton class. 
     */
    private UserBackgroundQueries() {
        userToBackgroundQueries = new HashMap<UserInterface, Set<QueryBizLogic>>();
    }

    /**
     * @return UserBackgroundQueries
     */
    public static UserBackgroundQueries getInstance() {
        if (ref == null) {
            ref = new UserBackgroundQueries();
        }
        return ref;
    }

    /*(non-Javadoc)
    * @see java.lang.Object#clone()*/
    public Object clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException();
    }

    /**
     * Adds currently running query for background execution for the current user and updating properties in database.
     */
    public void addBackgroundQuery(UserInterface user, QueryBizLogic queryBizLogic) {
        //Update and saving to database.
        QueryStatus qStatus = queryBizLogic.getStatus();
        qStatus.setVisible(Boolean.TRUE);
        new QueryURLStatusOperations().updateQueryStatus(qStatus);

        //Adding to user Vs background query map.
        Set<QueryBizLogic> bizLogicSet = userToBackgroundQueries.get(user);
        if (bizLogicSet == null) {
            bizLogicSet = new HashSet<QueryBizLogic>();
            userToBackgroundQueries.put(user, bizLogicSet);
        }
        bizLogicSet.add(queryBizLogic);
    }

    /**
     * Call update in database for all queries executing in background. 
     */
    public synchronized void updateAllBackgroundQueryStatus() {
        Set<UserInterface> users = userToBackgroundQueries.keySet();
        if (users != null) {
            for (UserInterface user : users) {
                updateBackgroundQueryStatusForUser(user);
            }
        }
    }

    /**
     * Call update in database for all queries executing in background for the given user.
     * @param user
     */
    public synchronized void updateBackgroundQueryStatusForUser(UserInterface user) {

        Set<QueryBizLogic> querieLogics = userToBackgroundQueries.get(user);
        if (querieLogics != null) {
            for (QueryBizLogic queryBizLogic : querieLogics) {
                QueryStatus status = queryBizLogic.getStatus();

                //If query is complete Write results into map and remove it's reference from map.
                if (status.equals(AbstractStatus.Complete) || status.equals(AbstractStatus.Complete_With_Error)) {
                    try {
                        String fileName = queryBizLogic.exportToCSV(filePath);
                        status.setFileName(fileName);
                    } catch (IOException e) {
                        e.printStackTrace();
                        throw new RuntimeException("Error while saving CSV file.", ErrorCodeConstants.IO_0001);
                    }
                    querieLogics.remove(queryBizLogic);
                }
                //updating query status in database.                
                new QueryURLStatusOperations().updateQueryStatus(status);
            }
        }
    }

}
