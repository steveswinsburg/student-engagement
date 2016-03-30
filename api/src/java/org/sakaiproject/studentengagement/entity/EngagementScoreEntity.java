package org.sakaiproject.studentengagement.entity;

import java.io.Serializable;
import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Persistent entity for the student engagement score
 *
 * @author Steve Swinsburg (steve.swinsburg@gmail.com)
 */
@ToString(includeFieldNames = true)
public class EngagementScoreEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * Auto assigned by Hibernate
	 */
	@Getter
	@Setter
	private long id;
	
	@Getter
	@Setter
	private String userUuid;

	@Getter
	@Setter
	private String siteId;
	
	/**
	 * Only need this as a string in ISO-8601 format
	 */
	@Getter
	@Setter
	private String day;

	@Getter
	@Setter
	private BigDecimal score;

	

}