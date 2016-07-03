SET SERVEROUTPUT ON;

/**
 * This script will generate a large number of events at random times and for all sites.
 
 * Prerequisites:
 * A number of sites exist.
 * A number of users exist, and they were added from the users.csv file.
 * A number of those same users were added to as many sites as you wish.
 * Once those three steps are done, run this script.
 *
 */
DECLARE 
  TYPE event_array_type IS TABLE OF VARCHAR2(50);
  event_type_array event_array_type := event_array_type(
    'annc.read',
    'asn.read.assignment',
    'asn.save.submission',
    'asn.submit.submission',
    'bbb.join',
    'calendar.read',
    'chat.new',
    'chat.read',
    'content.delete',
    'content.new',
    'content.read',
    'content.revise',
    'forums.delete',
    'forums.deleteforum',
    'forums.deletetopic',
    'forums.grade',
    'forums.movethread',
    'forums.new',
    'forums.newforum',
    'forums.read',
    'forums.response',
    'forums.reviseforum',
    'forums.revisetopic',
    'gradebook.studentView',
    'help.access',
    'help.search',
    'lessonbuilder.read',
    'messages.delete',
    'messages.deletefolder',
    'messages.forward',
    'messages.movedtodeletefolder',
    'messages.new',
    'messages.newfolder',
    'messages.read',
    'messages.reply',
    'messages.revisefolder',
    'messages.unread',
    'news.read',
    'pres.begin',
    'sam.assessment.submit',
    'syllabus.read',
    'webcontent.read',
    'webcontent.site.read',
    'yaft.message.created',
    'yaft.forum.created',
    'yaft.discussion.created',
    'clog.post.created',
    'clog.post.deleted',
    'clog.comment.created',
    'clog.comment.deleted',
    'clog.post.recycled',
    'clog.post.restored',
    'clog.post.withdrawn',
    'basiclti.launch');
    
  TYPE user_array_type IS TABLE OF VARCHAR2(200);
  user_id_array user_array_type;
  
  TYPE site_array_type IS TABLE OF VARCHAR2(200);
  site_id_array site_array_type;
    
  session_id VARCHAR2(100);
  rand_user NUMBER;
  rand_event NUMBER;
  num_participants NUMBER := 500;
  num_events NUMBER;
  event_time TIMESTAMP(6) WITH TIME ZONE;
  
BEGIN

  -- get all applicable sites
  SELECT ss.site_id
  BULK COLLECT INTO site_id_array
  FROM sakai_site ss
  WHERE ss.type IN ('project', 'course');
  
  -- get the test users
  SELECT su.user_id
  BULK COLLECT INTO user_id_array
  FROM sakai_user_id_map  su
  WHERE su.eid LIKE 'event_test%';
  
  FOR i IN 1..site_id_array.count LOOP
    
    FOR j IN 1..num_participants LOOP
      session_id := sys_guid();
      
      -- select one of the users at random
      rand_user := round(dbms_random.value()*10000);
      IF(rand_user = 0) 
          THEN rand_user := 1;
      END IF;
      
      -- how many events to generate for this run
      num_events := round(dbms_random.value()*54);        
      IF(num_events = 0) 
          THEN num_events := 1;
      END IF;

      -- create a session for the user
      INSERT INTO SAKAI_SESSION(session_id, session_user, session_active)
      VALUES (session_id, user_id_array(rand_user), 1);
      
      -- insert some events for the user
      FOR k IN 1..num_events LOOP
        
        -- pick one of the events (note 54 is the number of events we have)
        rand_event := round(dbms_random.value()*54);
        IF(rand_event = 0) 
          THEN rand_event := 1;
        END IF;
        
        -- determine a random time in the past, up to about 2 days ago
        event_time := sysdate - dbms_random.value() - round(dbms_random.value()*23)/24 - round(dbms_random.value()*59)/1440;

        -- insert the event
        INSERT INTO SAKAI_EVENT (event_id, event_date, event, ref, context, session_id, event_code) 
        VALUES (sakai_event_seq.nextval, event_time, event_type_array(rand_event), 'fake.ref', site_id_array(i), session_id, 'x');
      
      END LOOP;
    
    -- commit for every user
    COMMIT;
    END LOOP;
    
  END LOOP;
  
END;
/
