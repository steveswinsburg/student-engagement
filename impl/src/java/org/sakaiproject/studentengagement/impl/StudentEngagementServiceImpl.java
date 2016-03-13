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

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.site.api.SiteService;
import org.sakaiproject.studentengagement.api.StudentEngagementService;
import org.sakaiproject.studentengagement.dto.EngagementScore;
import org.sakaiproject.studentengagement.entity.EngagementScoreEntity;
import org.sakaiproject.studentengagement.persistence.StudentEngagementPersistenceService;

import lombok.Setter;

public class StudentEngagementServiceImpl implements StudentEngagementService {

	@Override
	public List<EngagementScore> getEngagementScores(final String siteId, final Date day) {

		// get students in site
		final List<String> userUuids = getStudents(siteId);

		// get scores from persistence and map them
		final List<EngagementScoreEntity> entities = this.persistenceService.getScores(userUuids, siteId, day);
		final List<EngagementScore> rval = mapToDto(entities);

		return rval;
	}

	@Override
	public void setEngagementScore(final String userUuid, final String siteId, final Date day) {

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

	@Setter
	StudentEngagementPersistenceService persistenceService;

	@Setter
	SiteService siteService;

}
