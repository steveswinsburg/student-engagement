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

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.sakaiproject.component.api.ServerConfigurationService;
import org.sakaiproject.entity.api.ResourceProperties;
import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.site.api.SiteService;
import org.sakaiproject.studentengagement.api.StudentEngagementService;
import org.sakaiproject.studentengagement.dto.EngagementEvent;
import org.sakaiproject.studentengagement.dto.EngagementScore;
import org.sakaiproject.studentengagement.dto.CompleteDay;
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

	@Setter
	StudentEngagementPersistenceService persistenceService;

	@Setter
	SiteService siteService;

	@Setter
	ServerConfigurationService serverConfigurationService;

	@Setter
	PreferencesService preferencesService;
	
	Map<String,BigDecimal> eventValues;
	
	/**
	 * Setup config map
	 */
	public void init() {
		
		String[] values = this.serverConfigurationService.getStrings("student.engagement.event.values");
		if(values == null) {
			log.warn("Student engagement event values are not set. Please configure 'student.engagement.event.values' to use this feature.");
			return;
		}
		
		eventValues = Arrays.asList(values)
				.stream()
				.map(elem -> elem.split(":"))
				.collect(Collectors.toMap(e -> e[0], e -> new BigDecimal(e[1])));

		log.info("Student engagement config: " + eventValues);
		
	}
	
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
	public void calculateEngagementScores(final String siteId, final LocalDate day) {

		log.info(String.format("Calculating for site: %s", siteId));
		
		// get students in site
		final List<String> userUuids = getStudents(siteId);
		
		if(userUuids.isEmpty()){
			log.info(String.format("Site: %s has no users. Nothing to do.", siteId));
			return;
		}
		
		List<EngagementScoreEntity> entities = new ArrayList<>();
		
		// for every user, get the last completed day, get the events, process them and create an entitty for persisting
		userUuids.forEach(userUuid -> {
			
			//TODO cache this so we only look this up once per user, not once per user per site
			CompleteDay completeDay = this.getLastCompleteDay(userUuid);
			
			log.info(String.format("User: %s, Day: %s", userUuid, completeDay.toString()));
			
			List<EngagementEvent> events = this.persistenceService.getEvents(userUuid, siteId, completeDay);
			
			BigDecimal score = this.calculateScore(events);
			
			EngagementScoreEntity entity = new EngagementScoreEntity();
			entity.setDay(completeDay.getDay().toString());
			entity.setSiteId(siteId);
			entity.setUserUuid(userUuid);
			entity.setScore(score);
			
			entities.add(entity);
			
		});
		
		//persist the entities
		this.persistenceService.setScores(entities);
		
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
	
	/**
	 * Get the last complete day for the given user. 
	 * @param userUuid user to get the day for
	 * @return
	 */
	protected CompleteDay getLastCompleteDay(String userUuid){
		
		CompleteDay rval = new CompleteDay();
		
		//get zoneId for the user
		ZoneId zoneId = getUserTimeZone(userUuid);
		
		// determine right now
		final ZonedDateTime now = ZonedDateTime.now();
		final long nowSeconds = Instant.from(now).getEpochSecond();

		// determine the start of yesterday
		final ZonedDateTime localisedYesterdayStart = now.toLocalDate().minusDays(1).atStartOfDay(zoneId);		
		
		// get the epoch seconds at the end of the localised yesterday
		final long localisedYesterdayEndSeconds = Instant.from(localisedYesterdayStart.toLocalDate().plusDays(1).atStartOfDay(zoneId)).getEpochSecond()-1;

		
		// Rule: is the localised yesterday end less than right now? If so, that day is complete. If not, subtract another day and use those values.
		if(localisedYesterdayEndSeconds < nowSeconds) {
			log.debug("Day is complete: " + localisedYesterdayStart);
			
			rval.setDay(localisedYesterdayStart.toLocalDate());
			rval.setStart(Instant.from(localisedYesterdayStart).getEpochSecond());
			rval.setEnd(localisedYesterdayEndSeconds);
		
		} else {
			log.debug("Day not complete: " + localisedYesterdayStart);
			
			//subtract another whole day and get the seconds
			//for these purposes we use the 'yesterday' start minus one second, as the end of the previous day rather than recalculate the day
			rval.setDay(localisedYesterdayStart.minusDays(1).toLocalDate());
			rval.setStart(Instant.from(localisedYesterdayStart.minusDays(1)).getEpochSecond());
			rval.setStart(Instant.from(localisedYesterdayStart).getEpochSecond()-1);
		}
		
		return rval;
		
	}
	
	/**
	 * Calculate the score given the list of events
	 * @param events list of {@link EngagementEvent}s
	 * @return
	 */
	protected BigDecimal calculateScore(List<EngagementEvent> events) {
		
		BigDecimal rval = BigDecimal.ZERO;
		
		//TODO convert to lambda
		for(EngagementEvent e: events) {
			if(this.eventValues.containsKey(e.getEvent())) {
				rval = rval.add(this.eventValues.get(e.getEvent()));
			}
		};
		
		return rval;
	}


}
