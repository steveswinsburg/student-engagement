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
package org.sakaiproject.studentengagement.impl;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.site.api.Site;
import org.sakaiproject.site.api.SiteService;
import org.sakaiproject.studentengagement.dto.EngagementScore;
import org.sakaiproject.studentengagement.entity.EngagementScoreEntity;
import org.sakaiproject.studentengagement.persistence.StudentEngagementPersistenceService;

@RunWith(MockitoJUnitRunner.class)
public class StudentEngagementServiceTestImplTest {

	@InjectMocks
	StudentEngagementServiceImpl impl = new StudentEngagementServiceImpl();

	@Mock
	StudentEngagementPersistenceService mockPersistenceService;

	@Mock
	SiteService mockSiteService;

	@Mock
	Site mockSite;

	List<String> userUuids = new ArrayList<>();
	String siteId;
	LocalDate today;
	Date dateFrom;
	Date dateTo;

	@Before
	public void setUp() {

		this.userUuids = new ArrayList<>();
		this.siteId = "siteId";
		this.today = LocalDate.now();
	}

	@Test
	public void testGetNoData() throws IdUnusedException {

		this.userUuids = getUserUuids(1);

		when(this.mockSiteService.getSite(this.siteId)).thenReturn(this.mockSite);
		when(this.mockSite.getUsersIsAllowed(Matchers.anyString())).thenReturn(new HashSet<>(this.userUuids));
		when(this.mockPersistenceService.getScores(this.userUuids, this.siteId, this.today))
				.thenReturn(getScoreEntities(0));

		final List<EngagementScore> scores = this.impl.getEngagementScores(this.siteId, this.today);
		assertEquals("Score list should be empty.", 0, scores.size());
	}

	@Test
	public void testGetOneScoreOneUser() throws IdUnusedException {

		this.userUuids = getUserUuids(1);

		when(this.mockSiteService.getSite(this.siteId)).thenReturn(this.mockSite);
		when(this.mockSite.getUsersIsAllowed(Matchers.anyString())).thenReturn(new HashSet<>(this.userUuids));
		when(this.mockPersistenceService.getScores(this.userUuids, this.siteId, this.today))
				.thenReturn(getScoreEntities(1));

		final List<EngagementScore> scores = this.impl.getEngagementScores(this.siteId, this.today);
		assertEquals("Score list should be one.", 1, scores.size());
	}

	private List<EngagementScoreEntity> getScoreEntities(final int number) {
		final List<EngagementScoreEntity> rval = new ArrayList<>();
		for (int i = 0; i < number; i++) {

			final EngagementScoreEntity e = new EngagementScoreEntity();
			e.setUserUuid("userUuid" + i);
			e.setScore(new BigDecimal(i));
			e.setDay(new Date());
			rval.add(e);
		}

		return rval;
	}

	private List<String> getUserUuids(final int number) {
		final List<String> rval = new ArrayList<>();
		for (int i = 0; i < number; i++) {
			rval.add("userUuid" + i);
		}

		return rval;
	}
}
