/*L
 * Copyright Georgetown University, Washington University.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/cab2b/LICENSE.txt for details.
 */

/**
 * 
 */
package edu.wustl.cab2b.admin.action;

import java.util.Map;

import org.apache.struts2.interceptor.SessionAware;

import edu.wustl.cab2b.admin.util.AdminConstants;
import edu.wustl.cab2b.server.util.UtilityOperations;

/**
 * @author atul_jawale
 * 
 */
public class Logout implements SessionAware {
    private Map session;

    /**
     * @return the session
     */
    public Map getSession() {
        return session;
    }

    /**
     * sets session
     * 
     * @param session
     */
    public void setSession(final Map session) {
        this.session = session;
    }

    /**
     * 
     * @return result
     */
    public String execute() {
        UtilityOperations.refreshCache();
        session.clear();
        return AdminConstants.SUCCESS;
    }
}
