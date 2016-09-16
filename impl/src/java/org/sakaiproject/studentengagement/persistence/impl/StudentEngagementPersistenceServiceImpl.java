
package org.sakaiproject.studentengagement.persistence.impl;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.hibernate.type.StandardBasicTypes;
import org.sakaiproject.db.api.SqlService;
import org.sakaiproject.studentengagement.dto.CompleteDay;
import org.sakaiproject.studentengagement.dto.EngagementEvent;
import org.sakaiproject.studentengagement.entity.EngagementScoreEntity;
import org.sakaiproject.studentengagement.persistence.StudentEngagementPersistenceService;
import org.springframework.orm.hibernate4.support.HibernateDaoSupport;

import lombok.Setter;

/**
 * Implementation of {@link StudentEngagementPersistenceService}
 * @author Steve Swinsburg (steve.swinsburg@gmail.com)
 *
 */
public class StudentEngagementPersistenceServiceImpl extends HibernateDaoSupport implements StudentEngagementPersistenceService {

	private String dbVendor;

	@Setter
	SqlService sqlService;

	/**
	 * Setup stuff
	 */
	public void init() {
		this.dbVendor = this.sqlService.getVendor();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<EngagementScoreEntity> getScores(final List<String> userUuids, final String siteId, final LocalDate day) {

		final Session session = getSessionFactory().getCurrentSession();
		final Criteria criteria = session.createCriteria(EngagementScoreEntity.class);

		// TODO note that this does not yet support more than 1000 uuids, it will need to be manually split
		criteria.add(Restrictions.in("userUuid", userUuids));
		criteria.add(Restrictions.eq("siteId", siteId));
		criteria.add(Restrictions.eq("day", day.toString()));
		
		final List<EngagementScoreEntity> entities = criteria.list();
		return entities;

	}

	@Override
	public List<EngagementEvent> getEvents(final String userUuid, final String siteId, final CompleteDay day) {
		String queryString = 
			"SELECT " +
			"  e.event " +
			", e.context " +
			", s.session_user " +
			"FROM SAKAI_EVENT e " +
			"JOIN SAKAI_SESSION s " +
			"  ON e.session_id = s.session_id " +
			"WHERE s.session_user = :user_id " +
			"AND e.context = :site_id ";
			if (StringUtils.equalsIgnoreCase(this.dbVendor, "oracle")) {
				//queryString += "AND e.event_date BETWEEN TO_DATE(:start_of_day, 'YYYY-MM-DD HH24:MI:SS') AND TO_DATE(:end_of_day, 'YYYY-MM-DD HH24:MI:SS') ";
				queryString += "AND e.event_date BETWEEN " +
								"CAST (DATE '1970-01-01' + (1/24/60/60) * :start_of_day AS TIMESTAMP) AND " +
								"CAST (DATE '1970-01-01' + (1/24/60/60) * :end_of_day AS TIMESTAMP) ";
			} else {
				// mysql
				queryString += "AND e.event_date BETWEEN FROM_UNIXTIME( :start_of_day ) AND FROM_UNIXTIME( :end_of_day ) ";
			}
			
			queryString += "ORDER BY e.event_date ASC";
			
		final Session session = getSessionFactory().getCurrentSession();
		SQLQuery query = session.createSQLQuery(queryString);
		
		//set the params
		query.setString("user_id", userUuid);
		query.setString("site_id", siteId);
		query.setLong("start_of_day", day.getStart());
		query.setLong("end_of_day", day.getEnd());
		
		//set return types
		query.addScalar("event", StandardBasicTypes.STRING);
		query.addScalar("context", StandardBasicTypes.STRING);
		query.addScalar("session_user", StandardBasicTypes.STRING);
				
		List<Object[]> list = query.list();
		
		List<EngagementEvent> rval = new ArrayList<EngagementEvent>();
		
		//TODO enhance to use ResultTransformer
		for(Object[] item: list) {
			EngagementEvent event = new EngagementEvent();
			event.setEvent((String) item[0]);
			event.setSiteId((String) item[1]);
			event.setUserUuid((String) item[2]);
			
			rval.add(event);
		}
		
		return rval;
	}
	
	@Override
	public void setScores(List<EngagementScoreEntity> entities) {
		
		final Session session = getSessionFactory().getCurrentSession();
		
		//TODO convert to a batch persist
		entities.forEach(e -> {		
			session.save(e);
		});
	}

}
