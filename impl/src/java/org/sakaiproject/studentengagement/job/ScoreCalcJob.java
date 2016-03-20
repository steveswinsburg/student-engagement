package org.sakaiproject.studentengagement.job;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.SchedulerException;
import org.quartz.StatefulJob;
import org.sakaiproject.site.api.SiteService;
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
public class ScoreCalcJob implements Job {

	private static final Logger log = Logger.getLogger(ScoreCalcJob.class);

	private final String BEAN_ID = "org.sakaiproject.studentengagement.job.ScoreCalcJob";


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
		
		LocalDate today = LocalDate.now().minusDays(1);
		
		//TODO is this the correct call?
		List<String> allSiteIds = siteService.getSiteIds(SiteService.SelectionType.NON_USER, null, null, null, SiteService.SortType.NONE, null);

		for(String siteId: allSiteIds) {
			this.studentEngagementService.calculateAndSetEngagementScore(siteId, today);
		}
		
		
		
		// start a session for admin so we can get full profiles
		// final Session session = this.sessionManager.startSession();
		// this.sessionManager.setCurrentSession(session);
		// session.setUserEid("admin");
		// session.setUserId("admin");
		

		// get total possible score
		// final BigDecimal total = getTotal();
		//final BigDecimal total = new BigDecimal(4);

		//log.info("Total score possible: " + total.setScale(2, RoundingMode.HALF_UP));

		

		log.info("ScoreCalcJob finished");
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

	

	public void init() {
		log.info("ScoreCalcJob.init()");
	}

	@Setter
	private UserDirectoryService userDirectoryService;

	@Setter
	private StudentEngagementService studentEngagementService;

	@Setter
	private SessionManager sessionManager;
	
	@Setter
	private SiteService siteService;

}
