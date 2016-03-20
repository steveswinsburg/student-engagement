package org.sakaiproject.studentengagement.rest;

import java.time.LocalDate;
import java.util.List;

import org.sakaiproject.entitybroker.EntityView;
import org.sakaiproject.entitybroker.entityprovider.EntityProvider;
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

public class StudentEngagementResource extends AbstractEntityProvider implements EntityProvider, AutoRegisterEntityProvider, ActionsExecutable, Outputable, Describeable {

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
	 * site/siteId/day
	 * 
	 * day must be ISO-8601 eg 2016-04-25
	 */
	@EntityCustomAction(action = "site", viewKey = EntityView.VIEW_LIST)
	public List<EngagementScore> getEngagementScoresForSite(EntityView view) {
		
		// get siteId
		String siteId = view.getPathSegment(2);

		// get day
		LocalDate day = LocalDate.parse(view.getPathSegment(3));
		
		//TODO validation
		System.out.println(siteId);
		System.out.println(day.toString());
		
		List<EngagementScore> scores = engagementService.getEngagementScores(siteId, day);
		return scores;
		
	}

	
	
}
