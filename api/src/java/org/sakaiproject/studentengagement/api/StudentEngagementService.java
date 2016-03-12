
package org.sakaiproject.studentengagement.api;

import java.util.Date;
import java.util.List;

import org.sakaiproject.studentengagement.dto.EngagementScore;

/**
 * API for the Student Engagement Service
 *
 * @author Steve Swinsburg (steve.swinsburg@gmail.com)
 */
public interface StudentEngagementService {

	/**
	 * Fetch the engagement score for a set of users
	 *
	 * @param userUuid uuids to get the score for
	 * @param dateFrom the {@link Date} as the lower bound of the lookup
	 * @param dateTo the {@link Date} as the upper bound of the lookup
	 * @return {@link EngagementScore} for the users
	 */
	List<EngagementScore> getEngagementScores(final List<String> userUuids, Date dateFrom, Date dateTo);

}
