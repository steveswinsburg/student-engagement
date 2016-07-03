
package org.sakaiproject.studentengagement.persistence;

import java.time.LocalDate;
import java.util.List;

import org.sakaiproject.studentengagement.dto.CompleteDay;
import org.sakaiproject.studentengagement.dto.EngagementEvent;
import org.sakaiproject.studentengagement.entity.EngagementScoreEntity;

/**
 * Persistence service API
 */
public interface StudentEngagementPersistenceService {

	/**
	 * Get the scores for a set of users in a site for a given day
	 * @param userUuids
	 * @param siteId
	 * @param day
	 * @return
	 */
	List<EngagementScoreEntity> getScores(final List<String> userUuids, final String siteId, final LocalDate day);

	/**
	 * Get the events from SAKAI_EVENT and SAKAI_SESSION table for the given user, site and day
	 * 
	 * Note that day stores the exact seconds that we want the events for
	 * 
	 * @param userUuid
	 * @param siteId
	 * @param day
	 * @return
	 */
	List<EngagementEvent> getEvents(final String userUuid, final String siteId, final CompleteDay day);
	
	/**
	 * Persist the scores
	 * @param entities list of {@link EngagementScoreEntity}
	 */
	void setScores(List<EngagementScoreEntity> entities);

}
