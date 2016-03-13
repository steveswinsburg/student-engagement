
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
	 * Retrieve the engagement scores for a given site and day
	 *
	 * @param siteId siteId to get the scores for
	 * @param day the {@link Date} to get the scores for
	 * @return {@link EngagementScore} for the students in the site
	 */
	List<EngagementScore> getEngagementScores(final String siteId, Date day);

	/**
	 * Calculates and sets the engagement score for the day
	 *
	 * @param userUuid user to calculate for
	 * @param siteId site to calculate for
	 * @param day the day to store the score for
	 */
	void setEngagementScore(String userUuid, String siteId, Date day);

}
