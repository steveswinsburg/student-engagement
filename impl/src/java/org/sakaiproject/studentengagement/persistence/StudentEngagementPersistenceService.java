
package org.sakaiproject.studentengagement.persistence;

import java.time.LocalDate;
import java.util.List;

import org.sakaiproject.studentengagement.dto.EngagementEvent;
import org.sakaiproject.studentengagement.entity.EngagementScoreEntity;

/**
 * Persistence service API
 */
public interface StudentEngagementPersistenceService {

	List<EngagementScoreEntity> getScores(final List<String> userUuids, final String siteId, final LocalDate day);

	List<EngagementEvent> getEvents(final String userUuid, final String siteId, final LocalDate day);

}
