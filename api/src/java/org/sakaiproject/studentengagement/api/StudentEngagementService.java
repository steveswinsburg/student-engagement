
package org.sakaiproject.studentengagement.api;

import java.time.LocalDate;
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
	 * @param day the {@link LocalDate} to get the scores for. Note that this is a date without a time-zone in the ISO-8601 calendar system,
	 *            such as <code>2016-03-15</code>.
	 * @return {@link EngagementScore} for the students in the site
	 */
	List<EngagementScore> getEngagementScores(final String siteId, LocalDate day);

	/**
	 * Calculates the engagement score for the give site and day
	 *
	 * @param siteId site to calculate for
	 * @param day The day on the server that will be the basis of all event calculations. Passing in <code>LocalDate.now()</code> is a good
	 *            option :) You could also pass in a different date instance if you need to process events for a different day. Note that
	 *            passing in today's date will grab the events for the last completed day in every user's timezone so don't modify the day
	 *            instance unless you know that is what you want to do.
	 *            
	 *            Note also that if the last complete day for a student is a whole day different to the passed in day, then this will be
	 *            the one persisted with the score for that student
	 */
	void calculateEngagementScores(String siteId, LocalDate day);

}
