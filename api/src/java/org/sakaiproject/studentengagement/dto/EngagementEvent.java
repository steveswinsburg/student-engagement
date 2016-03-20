package org.sakaiproject.studentengagement.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * DTO for an event
 *
 * @author Steve Swinsburg (steve.swinsburg@gmail.com)
 */
@ToString(includeFieldNames = true)
@Getter
@Setter
public class EngagementEvent {

	private String event;
	private String siteId;
	private String userUuid;

}
