# student-engagement
Student Engagement

This is a Sakai app that provides a metric for student engagement based on site activity.

There are two parts:

1. The data collection job
2. The API to build and retrieve the data

## Building
Simply `mvn clean install sakai:deploy` into your Sakai installation.

## Configuration
In sakai.properties, set the values for each of the events you want to be considered. You can use either of the configuration formats below. Each entry must have a `:` between the event name and the value. Decimal values are supported.

````
student.engagement.event.values=annc.read:5,asn.read.assignment:10,asn.save.submission:20.3
````
OR

````
student.engagement.event.values.count=3
student.engagement.event.values.1=annc.read:5
student.engagement.event.values.2=asn.read.assignment:10
student.engagement.event.values.3=asn.save.submission:20.3
````

In Sakai, login as an administrator and navigate to the Admin Workspace > Job Scheduler. 
Setup the `Student Engagement SCore Calculator` job to run once per day.

__This job must only run once per day!__

## How the scoring works
Every event that is generated by a user for the last complete and entire day in their local timezone, _before_ the day that the job runs, is collected. If any event matches the configured set, the score will be added. A total score will be created and saved for the student in each site and for the day in their local timezone. If the day is not complete, the previous day's set of events will be used.

If a user has no events for the day, their score will be 0 for that day.

## REST API

The REST API is currentluy provided via the Sakai EntityBroker sub-system.

Retrieve events for a site and day:

`GET http://your.sakai.server/direct/engagement/site/SITE_ID/YYYY-MM-DD.json|xml`


## Java API

The Java API provides service methods to retrieve and calculate engagement scores for a site.

The `org.sakaiproject.studentengagement.api.StudentEngagementService` bean provides the following methods:

````
/**
 * Retrieve the engagement scores for a given site and day
 *
 * @param siteId siteId to get the scores for
 * @param day the {@link LocalDate} to get the scores for. Note that this is a date without a time-zone in the ISO-8601 calendar system,
 *            such as <code>2016-03-15</code>.
 * @return {@link EngagementScore} for the students in the site
 */
List<EngagementScore> getEngagementScores(final String siteId, LocalDate day);

/**
 * Calculates the engagement score for the give site and day
 *
 * @param siteId site to calculate for
 * @param day The day on the server that will be the basis of all event calculations. Passing in <code>LocalDate.now()</code> is a good
 *            option :) You could also pass in a different date instance if you need to process events for a different day. Note that
 *            passing in today's date will grab the events for the last completed day in every user's timezone so don't modify the day
 *            instance unless you know that is what you want to do.
 *            
 *            Note also that if the last complete day for a student is a whole day different to the passed in day, then this will be
 *            the one persisted with the score for that student
 */
void calculateEngagementScores(String siteId, LocalDate day);
````




