package org.sakaiproject.studentengagement.dto;

import java.time.LocalDate;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Class for wrapping up the start and end timings for a complete day.
 * Times are in seconds since epoch and indicate the first and last second of the day
 * 
 * Timezone info is not currently preserved, though it is used to determine the values in this class.
 * 
 * @author Steve Swinsburg (steve.swinsburg@gmail.com)
 *
 */
@ToString(includeFieldNames = true)
@Getter
@Setter
public class LastCompleteDay {

	/**
	 * The day we are working with
	 */
	LocalDate day;
	
	/**
	 * The first second of the day, measured since epoch.
	 */
	long start;
	
	/**
	 * The last second on the day, measured since epoch.
	 * 
	 * This will actually be the first second of the next day so calculations need to be less than this, not equal.
	 */
	long end;
}
