package org.sakaiproject.studentengagement.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Class for wrapping up the start and end timings for a complete day.
 * Times are in seconds since epoch and indicate the first and last second of the day
 * 
 * @author Steve Swinsburg (steve.swinsburg@gmail.com)
 *
 */
@ToString(includeFieldNames = true)
@Getter
@Setter
public class LastCompleteDay {

	long start;
	
	/**
	 * This will actually be the first second of the next day so calculations need to be less than this, not equal
	 */
	long end;
}
