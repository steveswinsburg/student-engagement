# student-engagement
Student Engagement

This is a Sakai app that provides a metric for student engagement based on site activity.

There are two parts:

1. The data collection job
2. The API to build and retrieve the data

## Building
Simply `mvn clean install sakai:deploy` into your Sakai installation.

## How the scoring works
TODO

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


