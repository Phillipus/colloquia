package net.colloquia.comms.messages;

import java.io.*;
import java.util.*;

import net.colloquia.*;
import net.colloquia.comms.*;
import net.colloquia.datamodel.*;
import net.colloquia.datamodel.entities.*;
import net.colloquia.io.*;
import net.colloquia.prefs.*;
import net.colloquia.util.*;
import net.colloquia.xml.*;


/**
 * A received Activity
 */
public class ActivityMessageIn
extends ComponentMessageIn
{
    protected String senderEmail;     // e-mail address of sender
    protected boolean foundMe;

    /**
    * Constructor
    */
    protected ActivityMessageIn(MessageInfo mInfo, File zipFile) {
        super(mInfo, zipFile);
    }

    /**
    * File the message
    */
    public MessageInfo fileMessage(boolean deleteFile) throws ColloquiaFileException {
        // Who sent this?
        senderEmail = mInfo.getFrom();

        // Make sure I didn't send it to myself
        UserPrefs prefs = UserPrefs.getUserPrefs();
        if(prefs.isMyEmailAddress(senderEmail)) {
	        if(deleteFile) deleteZipFile();
        	return null;
        }

        // Collect the Components
		Vector entities = collectComponents();

        // I wasn't found!!! Maybe they used a wrong e-mail alias or something
        // So let's put it in pending
        if(!foundMe) {
        	addToPending(deleteFile);
            return mInfo;
        }

        // Map local GUIDs to People (replacing their e-mail as GUID)
        mapLocalPeopleGUIDs(entities);

        // The topmost Activity's GUID is recorded in the mInfo
        String topActivityGUID =  mInfo.getActivityID();
        Entity topActivityEntity = getEntity(topActivityGUID, entities);
        if(topActivityEntity == null) return mInfo;   // Shouldn't happen!

        // Add Activity / components
        addActivity(topActivityEntity, entities);

        // Check for Deletions
        checkDeletions(topActivityEntity, entities);

        // Add text files
        addTextFiles();

        // Success
        mInfo.setState(MessageInfo.RCVD);

        // Delete the zip file if set
        if(deleteFile) deleteZipFile();

    	return mInfo;
	}

    protected Vector getPeople(Vector entities) {
        Vector v = new Vector();
        for(int i = 0; i < entities.size(); i++) {
            Entity entity = (Entity)entities.elementAt(i);
            ColloquiaComponent tc = entity.getComponent();
            if(tc instanceof Person) v.addElement(tc);
        }
        return v;
    }


    /**
    * Run through the Sent XML file and create all ColloquiaComponents
    */
    protected Vector collectComponents() throws ColloquiaFileException {
        Vector v = new Vector();
        String entryName = "__activity__";
        String s = Utils.extractZipEntry(zipFile, entryName);
        if(s == null) return v;

        String line, uline;
        String endTag = "*&*";
        Entity entity = null;
        Member member = null;

        XMLStringReader reader = new XMLStringReader(s);

        try {
            reader.open();

            while((line = reader.readLine()) != null) {
                // Get lower case line
                uline = line.toLowerCase();

                // Do we have a tag?
                XMLTag xmlTag = XMLTag.getXMLTag(line);
                if(xmlTag != null) {
                    // Where does it go? Check for member first
                    if(member != null) member.putProperty(xmlTag.tag, xmlTag.value);
                    else if(entity != null) entity.putProperty(xmlTag.tag, xmlTag.value);
                    continue;
                }

                // Person start tag
                if(uline.indexOf(Person.XMLStartTag) != -1) {
                    entity = new Entity(new Person("-", ""));
                    endTag = Person.XMLEndTag;
                    continue;
                }

                // Resource start tag
                if(uline.indexOf(Resource.XMLStartTag) != -1) {
                    entity = new Entity(new Resource("-", ""));
                    endTag = Resource.XMLEndTag;
                    continue;
                }

                // Assignment start tag
                if(uline.indexOf(Assignment.XMLStartTag) != -1) {
                    entity = new Entity(new Assignment("-", ""));
                    endTag = Assignment.XMLEndTag;
                    continue;
                }

                // Activity start tag
                if(uline.indexOf(Activity.XMLStartTag) != -1) {
                    entity = new Entity(new Activity("-", ""));
                    endTag = Activity.XMLEndTag;
                    continue;
                }

                // Member start tag
                if(uline.indexOf(MemberInfo.XMLStartTag) != -1) {
                    // Open a new Member
                    if(entity != null) member = new Member();
                    continue;
                }

                // End of new Component
                if((uline.indexOf(endTag) != -1) && (entity != null)) {
                    v.addElement(entity);
                    entity = null;
                    continue;
                }

                // End of new Member
                if(uline.indexOf(MemberInfo.XMLEndTag) != -1) {
                    // Add Member to parent Entity
                    if(entity != null && member != null) entity.addMember(member);
                    member = null;
                    continue;
                }

            }

            reader.close();
            return v;
        }
        catch(XMLReadException ex) {
            if(ColloquiaConstants.DEBUG) ex.printStackTrace();
            throw new ColloquiaFileException("Could not collect components", ex.getMessage());
        }
    }

    /**
    * Add local GUIDs to People replacing their e-mails as GUIDS
    */
    protected void mapLocalPeopleGUIDs(Vector entities) {
        Hashtable map = new Hashtable();
        String newGUID;
        UserPrefs prefs = UserPrefs.getUserPrefs();

        // Run thru the Entities and replace People GUIDs
        for(int i = 0; i < entities.size(); i++) {
            Entity entity = (Entity)entities.elementAt(i);
            ColloquiaComponent tc = entity.getComponent();

            // If a Person
            if(tc instanceof Person) {
                Person sentPerson = (Person)tc;
                String oldGUID = sentPerson.getGUID();
    	        if(prefs.isMyEmailAddress(oldGUID)) continue; // Don't do Me!
	            // Do we have them already?
                Person person = DataModel.getPersonByEmailAddress(sentPerson);
                // No - generate local GUID
                if(person == null) newGUID = ColloquiaComponent.generateGUID();
                // Yes - use existing local GUID
                else newGUID = person.getGUID();
                // Map old to new for member references
                map.put(oldGUID, newGUID);
                // Change
                sentPerson.setGUID(newGUID);
            }
        }

        // Now change the Member refs in the Activities
        for(int i = 0; i < entities.size(); i++) {
            Entity entity = (Entity)entities.elementAt(i);
            if(!entity.isActivity()) continue;
            Vector members = entity.getMembers();
            for(int j = 0; j < members.size(); j++) {
                Member member = (Member)members.elementAt(j);
                String memberGUID = member.getGUID();
                if(map.containsKey(memberGUID)) member.setGUID((String)map.get(memberGUID));
            }
        }
    }

    /**
    * Add or Update the Components
    * 1. Start off with the topmost Activity (the GUID of which is in the mInfo)
    * 2. Get this Activity from the Hashtable of Entities
    * 3. If I am a member of the Activity add/update the Activity to the DataModel
    * 4. Get the Activity's children
    * 5. If the child is a sub-Activity goto 2
    * 6. If I am a member of the Activity add/update the component to the DataModel
    */
    protected void addActivity(Entity activityEntity, Vector entities) {
        if(activityEntity == null || !activityEntity.isActivity()) return;
        Activity A = (Activity)activityEntity.getComponent();
        if(A == null) return;

        // Can only Add/Update Activity and its members if:-
        // 1. The Submitter (owner) of the Activity is the person who sent this
        // AND
        // 2. I am a member of that Activity
        // BUT even if it's not OK we must check sub-Activities
        boolean OK = activityEntity.isSubmitter(senderEmail) && activityEntity.amMember();

        // Add/Update this Activity if OK
        if(OK) {
            // Ascertain parent group indirectly
            String parentGUID = A.getProperty(ColloquiaContainer.PARENT_GUID);
            ColloquiaContainer parentGroup = (ColloquiaContainer)DataModel.getComponent(parentGUID);

            // No Parent so use top Activity
            if(parentGroup == null) parentGroup = DataModel.getActivityGroup();

            // Is it a new Activity or an Update?
            Activity oldA = (Activity)DataModel.getComponent(A.getGUID());

            // Existing Activity
            if(oldA != null) {
                // If oldA is invite, delete invite folder
                if(oldA.isInvite()) DataFiler.deleteFolder(DataFiler.getInviteFolder(oldA.getGUID(), false));

                // Update properties
                oldA.updateProperties(A);

                // Compare existing parent with sent Parent in case of a Move
                ColloquiaContainer oldParentGroup = oldA.getParent();
                // Move
                if(parentGroup != oldParentGroup) {
                    DataModel.moveComponent(oldA, oldParentGroup, parentGroup, false);
	            	parentGroup.setComponentFree(oldA, false);
                }

                A = oldA; // For further work below
            }

            // New Activity
            else {
            	DataModel.addComponent(A, parentGroup, false);
            }

           	// Set it non-free as it is a Sent Activity
           	parentGroup.setComponentFree(A, false);
        }

        // Activity members
        Vector members = activityEntity.getMembers();
        for(int i = 0; i < members.size(); i++) {
            Member member = (Member)members.elementAt(i);
            Entity entity = getEntity(member.getGUID(), entities);
            if(entity == null) continue;

            // If it's an Activity recurse
            if(entity.isActivity()) addActivity(entity, entities);

            // Else if OK then Add/Update member
            else if(OK) {
                ColloquiaComponent tc = entity.getComponent();
                // Do we have it already?
                ColloquiaComponent oldTC = DataModel.getComponent(tc.getGUID());
                // If we do then update it
                if(oldTC != null) {
                    // PENDING - should we do People?
                    // Maybe - because they might have changed their e-mail address?
                    oldTC.updateProperties(tc);
                    tc = oldTC;
                }
                // Add to Activity
                DataModel.addComponent(tc, A, false);
                // Set it non-free
                A.setComponentFree(tc, false);
            }
        } // for
    }

    /**
    * 1. Collect all components in the recipient's Activity
    * 2. Compare these with those in the sent Activity
    * 3. Is the component in my Activity a member of the Sent Activity?
    * 4. Yes - leave it
    * 5. No -  If my component's submitter == sent Activity's submitter set Free
    *          Else leave it (it must be the recipient's or somebody else's)
    */
    protected void checkDeletions(Entity activityEntity, Vector entities) {
        if(activityEntity == null || !activityEntity.isActivity()) return;

        // Can only set free if:-
        // 1. The Submitter (owner) of the Activity is the person who sent this
        // AND
        // 2. I am a member of that Activity
        // BUT even if it's not OK we must check sub-Activities
        boolean OK = activityEntity.isSubmitter(senderEmail) && activityEntity.amMember();

        if(OK) {
            // The Recipient's Activity in the DataModel
            Activity A = (Activity)DataModel.getComponent(activityEntity.getGUID());
            if(A == null) return;

            // Run thru all components in the recipient's Activity
            Vector children = A.getActiveComponents();
            for(int i = 0; i < children.size(); i++) {
                // Get member component on user's data
                ColloquiaComponent tc = (ColloquiaComponent)children.elementAt(i);
                // If a sub-Activity, who owns it?
                if(tc instanceof Activity) {
                    // Somebody else
                    if(!tc.isSubmitter(senderEmail)) continue;
                }
                // Was it in the Sent Activity?
                if(!activityEntity.containsMember(tc.getGUID())) {
                    // Set accordingly
                    A.setComponentFree(tc, true);
                }
            }
        }

        // Get sent Activity members so we can recurse
        Vector members = activityEntity.getMembers();
		for(int i = 0; i < members.size(); i++) {
            Member member = (Member)members.elementAt(i);
            Entity entity = getEntity(member.getGUID(), entities);
            if(entity == null) continue;
            // If it's an Activity recurse
            if(entity.isActivity()) checkDeletions(entity, entities);
        }

    }


    protected Entity getEntity(String GUID, Vector entities) {
        for(int i = 0; i < entities.size(); i++) {
            Entity entity = (Entity)entities.elementAt(i);
            if(entity.getGUID().equalsIgnoreCase(GUID)) return entity;
        }
        return null;
    }

    protected class Entity {
        private ColloquiaComponent tc;
        private Vector members;
        private UserPrefs prefs = UserPrefs.getUserPrefs();
        private boolean amMember = false;

        public Entity(ColloquiaComponent tc) {
            this.tc = tc;
            if(tc instanceof ColloquiaContainer) members = new Vector();
        }

        public void addMember(Member member) {
            String GUID = member.getGUID();
            // Don't add me as a member
            if(prefs.isMyEmailAddress(GUID)) {
            	amMember = true;
                foundMe = true;   // Got at least one!
            }
            else members.addElement(member);
        }

        public Vector getMembers() {
            return members;
        }

        public boolean isActivity() {
            return (tc instanceof Activity);
        }

        // Is owned by sender?
        public boolean isSubmitter(String senderEmail) {
            return tc.isSubmitter(senderEmail);
        }

        // Am I a member?
        public boolean amMember() {
            return amMember;
        }

        public boolean containsMember(String GUID) {
            for(int i = 0; i < members.size(); i++) {
                Member member = (Member)members.elementAt(i);
                if(GUID.equalsIgnoreCase(member.getGUID())) return true;
            }
            return false;
        }

        public ColloquiaComponent getComponent() {
            return tc;
        }

        public String getGUID() {
            return tc.getGUID();
        }

        public void putProperty(String tag, String value) {
            tc.putProperty(tag, value, false);
        }
    }


    protected class Member {
        Hashtable properties = new Hashtable();

        public void putProperty(String tag, String value) {
            properties.put(tag, value);
        }

        public String getGUID() {
            return (String)properties.get(ColloquiaComponent.GUID);
        }

        public void setGUID(String GUID) {
            properties.put(ColloquiaComponent.GUID, GUID);
        }
    }

}
