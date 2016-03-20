package org.sakaiproject.studentengagement.impl;
/*
 * Copyright (c) Orchestral Developments Ltd and the Orion Health group of companies (2001 - 2016).
 *
 * This document is copyright. Except for the purpose of fair reviewing, no part
 * of this publication may be reproduced or transmitted in any form or by any
 * means, electronic or mechanical, including photocopying, recording, or any
 * information storage and retrieval system, without permission in writing from
 * the publisher. Infringers of copyright render themselves liable for
 * prosecution.
 */

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;

import org.apache.commons.lang.StringUtils;
import org.sakaiproject.component.api.ServerConfigurationService;
import org.sakaiproject.entity.api.ResourceProperties;
import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.site.api.SiteService;
import org.sakaiproject.studentengagement.api.StudentEngagementService;
import org.sakaiproject.studentengagement.dto.EngagementEvent;
import org.sakaiproject.studentengagement.dto.EngagementScore;
import org.sakaiproject.studentengagement.dto.LastCompleteDay;
import org.sakaiproject.studentengagement.entity.EngagementScoreEntity;
import org.sakaiproject.studentengagement.persistence.StudentEngagementPersistenceService;
import org.sakaiproject.time.api.TimeService;
import org.sakaiproject.user.api.Preferences;
import org.sakaiproject.user.api.PreferencesService;

import lombok.Setter;
import lombok.extern.apachecommons.CommonsLog;

/**
 * Implementation of {@link StudentEngagementService}
 */
@CommonsLog
public class StudentEngagementServiceImpl implements StudentEngagementService {

	@Override
	public List<EngagementScore> getEngagementScores(final String siteId, final LocalDate day) {

		// get students in site
		final List<String> userUuids = getStudents(siteId);
		
		if(userUuids.isEmpty()){
			return Collections.emptyList();
		}
		
		// get scores from persistence and map them
		final List<EngagementScoreEntity> entities = this.persistenceService.getScores(userUuids, siteId, day);
		final List<EngagementScore> rval = mapToDto(entities);

		return rval;
	}
	
	@Override
	public void calculateAndSetEngagementScore(String siteId, LocalDate day) {
		// get students in site
		final List<String> userUuids = getStudents(siteId);
		
		if(userUuids.isEmpty()){
			log.info(String.format("Site: %s has no users. Nothing to do.", siteId));
			return;
		}
		
		for(String userUuid: userUuids) {
			calculateAndSetEngagementScore(userUuid, siteId, day);
		}
		
	}

	@Override
	public void calculateAndSetEngagementScore(final String userUuid, final String siteId, final LocalDate day) {

		// get day prior
		//final LocalDate yesterday = day.minusDays(1);

		// get the beginning of the given day
		//final LocalDateTime beginningOfDay = day.minusDays(1).atStartOfDay();
		//final LocalDate yesterdayServer = day.minusDays(1).with(LocalDateTime.MAX);

		// get the preferred timezone for the user
		// TODO optimise this into a map/cache that persists between runs so we dont need to hit it each time?
		//final ZoneId zoneId = getUserTimeZone(userUuid);

		//determine the end of yesterday for the user
		//final ZonedDateTime yesterdayEndUser = yesterdayServer.atStartOfDay(zoneId).with(LocalDateTime.MAX);

		//has yesterday finished for the user (the end of the day will be before before the current days beginning.
		//yesterdayEndUser.toInstant()day.

		log.info(String.format("Running for site: %s, user: %s and day: %s.", siteId, userUuid, day.toString()));
		
		
		List<EngagementEvent> events = this.persistenceService.getEvents(userUuid, siteId, day);
		events.forEach(e -> {
			
			log.info(e);
			
		});
		
		

	}

	/**
	 * Map the entities to DTOs
	 *
	 * @param entities List of {@link EngagementScoreEntity}
	 * @return
	 */
	protected List<EngagementScore> mapToDto(final List<EngagementScoreEntity> entities) {
		final List<EngagementScore> rval = new ArrayList<>();
		entities.forEach(e -> {
			final EngagementScore s = new EngagementScore();
			s.setUserUuid(e.getUserUuid());
			s.setSiteId(e.getSiteId());
			s.setScore(e.getScore());
			s.setDay(e.getDay());
			rval.add(s);
		});

		return rval;
	}

	/**
	 * Get the list of users in the site that are in the student role
	 *
	 * @param siteId the site id to query
	 * @return
	 */
	protected List<String> getStudents(final String siteId) {
		Set<String> userUuids = new HashSet<>();
		try {
			userUuids = this.siteService.getSite(siteId).getUsersIsAllowed("section.role.student");
		} catch (final IdUnusedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new ArrayList<>(userUuids);
	}

	/**
	 * Get a user's timezone as a {@link ZoneId} from their preferences.
	 *
	 * @param userUuid uuid of the user to get preferences for
	 * @return ZoneId of user preferences or the default timezone/ZoneId of the server if none is set
	 */
	protected ZoneId getUserTimeZone(final String userUuid) {

		TimeZone timezone;
		final Preferences prefs = this.preferencesService.getPreferences(userUuid);
		final ResourceProperties props = prefs.getProperties(TimeService.APPLICATION_ID);
		final String tzPref = props.getProperty(TimeService.TIMEZONE_KEY);

		if (StringUtils.isNotBlank(tzPref)) {
			timezone = TimeZone.getTimeZone(tzPref);
		} else {
			timezone = TimeZone.getDefault();
		}

		return timezone.toZoneId();
	}
	
	protected LastCompleteDay getLastCompleteDay(ZoneId zoneId){
		
		LastCompleteDay rval = new LastCompleteDay();
		
		ZonedDateTime now = ZonedDateTime.now();
		long nowSeconds = Instant.from(now).getEpochSecond();

		//TODO convert these to instants straight up, we dont need the zonedbits
		final ZonedDateTime localisedYesterdayStart = now.toLocalDate().minusDays(1).atStartOfDay(zoneId);
		final ZonedDateTime localisedNextDayStart = localisedYesterdayStart.toLocalDate().plusDays(1).atStartOfDay(zoneId);
		
		long localisedNextDayStartSeconds = Instant.from(localisedNextDayStart).getEpochSecond();
		
		//is the localised next day start less than right now? If so, that day is complete.
		if(localisedNextDayStartSeconds < nowSeconds) {
			System.out.println("day has ended");
			rval.setStart(Instant.from(localisedYesterdayStart).getEpochSecond());
			rval.setStart(Instant.from(localisedNextDayStart).getEpochSecond()-1);
		} else {
			System.out.println("day not over");
			//subtract another day and get the instants for start and emd
		}
		
		
		
		
		
		return null;
		
	}

	@Setter
	StudentEngagementPersistenceService persistenceService;

	@Setter
	SiteService siteService;

	@Setter
	ServerConfigurationService serverConfigurationService;

	@Setter
	PreferencesService preferencesService;

	

}
