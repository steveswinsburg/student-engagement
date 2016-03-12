package org.sakaiproject.studentengagement.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import lombok.Getter;
import lombok.Setter;

/**
 * Persistent entity for the student engagement score
 *
 * @author Steve Swinsburg (steve.swinsburg@gmail.com)
 */
public class EngagementScoreEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@Getter
	@Setter
	private String userUuid;

	/**
	 * Calculated score as a percentage based on the settings avaialble at the time
	 */
	@Getter
	@Setter
	private BigDecimal score;

	@Getter
	@Setter
	private Date day;

}