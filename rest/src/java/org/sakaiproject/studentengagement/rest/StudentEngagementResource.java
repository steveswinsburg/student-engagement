package org.sakaiproject.studentengagement.rest;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.sakaiproject.entitybroker.EntityView;
import org.sakaiproject.entitybroker.entityprovider.annotations.EntityCustomAction;
import org.sakaiproject.entitybroker.entityprovider.capabilities.ActionsExecutable;
import org.sakaiproject.entitybroker.entityprovider.capabilities.AutoRegisterEntityProvider;
import org.sakaiproject.entitybroker.entityprovider.capabilities.Describeable;
import org.sakaiproject.entitybroker.entityprovider.capabilities.Outputable;
import org.sakaiproject.entitybroker.entityprovider.extension.Formats;
import org.sakaiproject.entitybroker.util.AbstractEntityProvider;
import org.sakaiproject.studentengagement.api.StudentEngagementService;
import org.sakaiproject.studentengagement.dto.EngagementScore;

import lombok.Setter;

/**
 * Entity provider for the Student Engagement Service
 */
public class StudentEngagementResource extends AbstractEntityProvider implements AutoRegisterEntityProvider, ActionsExecutable, Outputable, Describeable {

	@Setter
	private StudentEngagementService engagementService;
	
	public final static String ENTITY_PREFIX = "engagement";
	
	@Override
	public String[] getHandledOutputFormats() {
		return new String[] { Formats.XML, Formats.JSON};
	}

	@Override
	public String getEntityPrefix() {
		return ENTITY_PREFIX;
	}
	
	/**
	 * Get the data. Format is: site/siteId/day
	 * 
	 * day must be ISO-8601 eg 2016-04-25
	 * 
	 * User must be a superuser, instructor in the site or the 'api.user' if it is defined in sakai.properties, as per API restrictions.
	 */
	@EntityCustomAction(action = "site", viewKey = EntityView.VIEW_LIST)
	public List<EngagementScore> getEngagementScoresForSite(EntityView view) {
		
		// get data
		String siteId = view.getPathSegment(2);
		String date = view.getPathSegment(3);
		
		// validation
		if (StringUtils.isBlank(siteId)) {
			throw new IllegalArgumentException(String.format("SiteId must be given, via the URL /%s/site/{siteId}/{date}.{format}", ENTITY_PREFIX));
		}
		if (StringUtils.isBlank(date)) {
			throw new IllegalArgumentException(String.format("Date must be given, via the URL /%s/site/{siteId}/{date}.{format}", ENTITY_PREFIX));
		}

		// get day
		LocalDate day;
		try {
			day = LocalDate.parse(view.getPathSegment(3));
		} catch (DateTimeParseException e) {
			throw new IllegalArgumentException("Date format was invalid, must be ISO-8601 format, ie YYYY-MM-DD");
		}
				
		List<EngagementScore> scores = engagementService.getEngagementScores(siteId, day);
		return scores;
	}

	
	
}
