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
import java.util.List;

import org.sakaiproject.studentengagement.api.StudentEngagementService;
import org.sakaiproject.studentengagement.dto.EngagementScore;
import org.sakaiproject.studentengagement.entity.EngagementScoreEntity;
import org.sakaiproject.studentengagement.persistence.StudentEngagementPersistenceService;

import lombok.Setter;

public class StudentEngagementServiceImpl implements StudentEngagementService {

	@Override
	public List<EngagementScore> getEngagementScores(final List<String> userUuids, final Date dateFrom, final Date dateTo) {

		final List<EngagementScoreEntity> entities = this.persistenceService.getScores(userUuids, dateFrom, dateTo);

		final List<EngagementScore> rval = mapToDto(entities);

		return rval;
	}

	/**
	 * Map the entities to DTOs
	 *
	 * @param entities List of {@link EngagementScoreEntity}
	 * @return
	 */
	private List<EngagementScore> mapToDto(final List<EngagementScoreEntity> entities) {
		final List<EngagementScore> rval = new ArrayList<>();
		entities.forEach(e -> {
			final EngagementScore s = new EngagementScore();
			s.setUserUuid(e.getUserUuid());
			s.setScore(e.getScore());
			s.setDay(e.getDay());
			rval.add(s);
		});

		return rval;
	}

	@Setter
	StudentEngagementPersistenceService persistenceService;

}
