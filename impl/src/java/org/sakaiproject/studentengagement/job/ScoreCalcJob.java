package org.sakaiproject.studentengagement.job;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.SchedulerException;
import org.quartz.StatefulJob;
import org.sakaiproject.studentengagement.api.StudentEngagementService;
import org.sakaiproject.tool.api.SessionManager;
import org.sakaiproject.user.api.UserDirectoryService;

import lombok.Setter;

/**
 * This is the Student Engagement calculation job.
 *
 * <p>
 * This job uses the currently configured weightings for a collection of events and determines each student's engagement score for the day
 * </p>
 *
 * @author Steve Swinsburg (steve.swinsburg@gmail.com)
 *
 */
public class ScoreCalcJob implements StatefulJob {

	private static final Logger log = Logger.getLogger(ScoreCalcJob.class);

	private final String BEAN_ID = "org.sakaiproject.studentengagement.job.ScoreCalcJob";

	/**
	 * Calculate the score for this person
	 *
	 * @param person Person object
	 * @return
	 */
	private BigDecimal getScore(final String userUuid) {

		final BigDecimal score = new BigDecimal(0);

		return score;

	}

	@Override
	public void execute(final JobExecutionContext context) throws JobExecutionException {

		// abort if already running on THIS server node (cannot check other nodes)
		try {
			while (isJobCurrentlyRunning(context)) {
				final String beanId = context.getJobDetail().getJobDataMap().getString(this.BEAN_ID);
				log.warn("Another instance of " + beanId + " is currently running - Execution aborted.");
				return;
			}
		} catch (final SchedulerException e) {
			log.error("Aborting job execution due to " + e.toString(), e);
			return;
		}

		log.info("ScoreCalcJob run");

		// start a session for admin so we can get full profiles
		// final Session session = this.sessionManager.startSession();
		// this.sessionManager.setCurrentSession(session);
		// session.setUserEid("admin");
		// session.setUserId("admin");

		// get total possible score
		// final BigDecimal total = getTotal();
		final BigDecimal total = new BigDecimal(4);

		log.info("Total score possible: " + total.setScale(2, RoundingMode.HALF_UP));

		// get total number of records
		// final List<String> profileUuids = this.userDirectoryService.getAllSakaiPersonIds();

		// iterate over list getting a chunk of profiles at a time
		// for (final String userUuid : profileUuids) {

		// final Person person = this.profileLogic.getPerson(userUuid);
		// if (person == null) {
		// continue;
		// }

		// log.info("Processing user: " + userUuid + " (" + person.getDisplayName() + ")");

		// get score for user
		// final BigDecimal score = getScore(person);
		// final BigDecimal percentage = getScoreAsPercentage(score, total);
		// final int adjustedScore = getScoreOutOfTen(score, total);

		// save it
		// if (this.kudosLogic.updateKudos(userUuid, adjustedScore, percentage)) {
		// log.info("Kudos updated for user: " + userUuid + ", score: " + score.setScale(2, RoundingMode.HALF_UP) + ", percentage: "
		/// + percentage + ", adjustedScore: " + adjustedScore);
		// }

		// }

		// session.setUserId(null);
		// session.setUserEid(null);

		log.info("KudosJob finished");
	}

	/**
	 * Are multiples of this job currently running?
	 *
	 * @param context
	 * @return
	 * @throws SchedulerException
	 */
	private boolean isJobCurrentlyRunning(final JobExecutionContext context) throws SchedulerException {
		final String beanId = context.getJobDetail().getJobDataMap().getString(this.BEAN_ID);
		final List<JobExecutionContext> jobsRunning = context.getScheduler().getCurrentlyExecutingJobs();

		int jobsCount = 0;
		for (final JobExecutionContext j : jobsRunning) {
			if (StringUtils.equals(beanId, j.getJobDetail().getJobDataMap().getString(this.BEAN_ID))) {
				jobsCount++; // this job=1, any more and they are multiples.
			}
		}
		if (jobsCount > 1) {
			return true;
		}
		return false;
	}

	/**
	 * Helper for StringUtils.isNotBlank
	 *
	 * @param s1 String to check
	 * @return
	 */
	private boolean nb(final String s1) {
		return StringUtils.isNotBlank(s1);
	}

	/**
	 * Helper to get the value of the key from the RULES map
	 *
	 * @param key key to getvalue for
	 * @return BigDecimal value
	 */
	// private BigDecimal val(final String key) {
	// return this.RULES.get(key);
	// }

	/**
	 * Gets the total of all BigDecimals in the RULES map
	 *
	 * @param map
	 * @return
	 */
	// private BigDecimal getTotal() {

	// BigDecimal total = new BigDecimal("0");

	// if (this.RULES != null) {
	// for (final Map.Entry<String, BigDecimal> entry : this.RULES.entrySet()) {
	// total = total.add(entry.getValue());
	//// }
	// }
	// return total;
	// }

	/**
	 * Gets the score as a percentage, two decimal precision
	 *
	 * @param score score for user
	 * @param total total possible score
	 * @return
	 */
	private BigDecimal getScoreAsPercentage(final BigDecimal score, final BigDecimal total) {
		return score.divide(total, 4, RoundingMode.HALF_UP).multiply(new BigDecimal("100")).stripTrailingZeros();
	}

	/**
	 * Gets the score out of ten as an int, and rounded up
	 *
	 * @param score score for user
	 * @param total total possible score
	 * @return
	 */
	private static int getScoreOutOfTen(final BigDecimal score, final BigDecimal total) {
		return score.divide(total, 1, RoundingMode.HALF_UP).multiply(new BigDecimal("10")).intValue();
	}

	public void init() {
		log.info("KudosJob.init()");
	}

	@Setter
	private UserDirectoryService userDirectoryService;

	@Setter
	private StudentEngagementService studentEngagementService;

	@Setter
	private SessionManager sessionManager;

}
