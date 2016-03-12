
package org.sakaiproject.studentengagement.persistence;

import java.util.Date;
import java.util.List;

import org.sakaiproject.studentengagement.entity.EngagementScoreEntity;

/**
 * Persistence service API
 */
public interface StudentEngagementPersistenceService {

	List<EngagementScoreEntity> getScores(final List<String> userUuids, Date dateFrom, Date dateTo);

}
