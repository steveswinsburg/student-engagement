package org.sakaiproject.studentengagement.dto;

import java.math.BigDecimal;
import java.util.Date;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * DTO for the engagement score
 *
 * @author Steve Swinsburg (steve.swinsburg@gmail.com)
 */
@ToString(includeFieldNames = true)
@Getter
@Setter
public class EngagementScore {

	private String userUuid;
	private String siteId;
	private BigDecimal score;
	private Date day;

}
