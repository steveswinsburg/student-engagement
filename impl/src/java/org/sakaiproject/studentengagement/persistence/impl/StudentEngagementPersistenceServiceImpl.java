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
package org.sakaiproject.studentengagement.persistence.impl;

import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.sakaiproject.studentengagement.entity.EngagementScoreEntity;
import org.sakaiproject.studentengagement.persistence.StudentEngagementPersistenceService;
import org.springframework.orm.hibernate4.support.HibernateDaoSupport;

public class StudentEngagementPersistenceServiceImpl extends HibernateDaoSupport implements StudentEngagementPersistenceService {

	@SuppressWarnings("unchecked")
	@Override
	public List<EngagementScoreEntity> getScores(final List<String> userUuids, final String siteId, final Date dateFrom,
			final Date dateTo) {

		final Session session = getSessionFactory().getCurrentSession();
		final Criteria criteria = session.createCriteria(EngagementScoreEntity.class);

		// TODO note that this does not yet support more than 1000 uuids, it will need to be manually split
		criteria.add(Restrictions.in("userUuid", userUuids));

		criteria.add(Restrictions.eq("siteId", siteId));
		criteria.add(Restrictions.ge("day", dateFrom));
		criteria.add(Restrictions.lt("day", dateTo));
		final List<EngagementScoreEntity> entities = criteria.list();
		return entities;

	}

}
