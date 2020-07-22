package net.colloquia.datamodel.entities;

import java.util.*;

import net.colloquia.datamodel.*;
import net.colloquia.io.*;
import net.colloquia.util.*;
import net.colloquia.xml.*;

import org.jdom.*;


public class Activity
extends ColloquiaContainer
{
    // Possible states
    public static final int FUTURE = 0;      // This is not used as of 4th June 2001
    public static final int LIVE = 1;
    public static final int COMPLETED = 2;
    public static final int TEMPLATE = 3;
    public static final int INVITE = 4;

    // Additional property keys
    public static final String STATE = "state";
    public static final String DUE_DATE = "due_date";
    public static final String START_DATE = "start_date";
    public static final String TUTOR = "tutor";
    public static final String INHERITS_PEOPLE = "inherits_people";
    public static final String ACCEPTS_RESOURCES = "acc_res";

    // Keys to MemberInfo properties
    // Assignment Grade
    public static final String ASSIGNMENT_GRADE = "assignment_grade";
    // Date last Sent Activity
    public static final String DATE_LAST_SENT_ACTIVITY = "date_last_sent_activity";
    // Person accepted this Activity?
    public static final String ACCEPT_DECLINE = "accepted";
    // Person needs updating with Activity
    public static final String HAS_ACTIVITY = "has_activity";

    public static final String HOT = "hot";

    // States of Activity Acceptance for an Activity and a Student
    public static final int ACCEPTED = 1;
    public static final int DECLINED = 2;
    public static final int PENDING = 3;    // Awaiting Acceptance

    private Vector people = new Vector();
    private Vector resources = new Vector();
    private Vector assignments = new Vector();
    private Vector activities = new Vector();

    /**
    * CONSTRUCTOR
    */
    public Activity(String name, String GUID) {
        super(name, GUID, ColloquiaComponent.ACTIVITY);
        setLive(false);
    }

    static Vector table = new Vector();
    static {
        table.addElement(new FieldValue(TUTOR, LanguageManager.getString("8_2"), false, true));
        table.addElement(new FieldValue(START_DATE, LanguageManager.getString("8_3"), false, true));
        table.addElement(new FieldValue(DUE_DATE, LanguageManager.getString("8_4"), false, true));
        table.addElement(new FieldValue(PHYSICAL_LOCATION, LanguageManager.getString("8_5"), false, false));
        table.addElement(new FieldValue(SUBMITTER, LanguageManager.getString("8_7"), false, true));
    }

    public Vector getTableFieldValues() {
    	return table;
    }

    /**
    * Adds a Component member to this Activity - this has to be either a Person,
    * Resource or Activity or Assignment
    * If tc is a Person or a Resource we have to check to see whether it needs
    * to be also added to the main People or Resources groups
    * @return true if added, false if not
    */
    protected synchronized boolean addComponent(ColloquiaComponent tc, MemberInfo mInfo, boolean hot) {
        if(tc == null) return false;
        Vector store;

        switch(tc.getType()) {
            case ColloquiaComponent.PERSON:
                // Can't add to Template
                if(isTemplate()) return false;
                // Add to main People folder if not in any People Group
                if(!DataModel.containsPerson((Person)tc))
	                DataModel.addComponent(tc, DataModel.getPeopleGroup(), false);
                store = people;
                break;
            case ColloquiaComponent.RESOURCE:
                // Add to main Resources folder if not in any Resource Group
                if(!DataModel.containsResource((Resource)tc))
                    DataModel.addComponent(tc, DataModel.getResourceGroup(), false);
                store = resources;
                break;
            case ColloquiaComponent.ACTIVITY:
                Activity A = (Activity)tc;
                store = activities;
                A.setParent(this);
                // If this is a Template make child a template
                if(isTemplate()) A.setTemplate(false);
                // If adding a Template make it Live
                else if(A.isTemplate()) A.setLive(true);
                break;
            case ColloquiaComponent.ASSIGNMENT:
                store = assignments;
                break;
            default:
                return false;
        }

        if(!store.contains(tc)) {
            tc.addMemberGroup(this);
            store.addElement(tc);
            // We can re-use an existing MemberInfo
            if(mInfo != null) memberInfoTable.put(tc, mInfo);
            // New one
            else {
                mInfo = new MemberInfo(tc, this);
            	memberInfoTable.put(tc, mInfo);
                // New Person in My Activity - PENDING state
                if((tc instanceof Person) && isMine()) {
                	setPersonAccepted((Person)tc, PENDING);
                    setPersonSentActivity((Person)tc, false);
                }
            }
            if(hot) setTimeStamp();
            return true;
        }

        return false;
    }

    /**
    * Removes a Component member from this Activity - this has to be either a Person,
    * Resource or Activity or Assignment
    * Will not remove components in sub-Activities
    * @return true if removed, false if not
    */
    public synchronized boolean removeComponent(ColloquiaComponent tc, boolean hot) {
        if(tc == null) return false;
        Vector store;

        switch(tc.getType()) {
            case ColloquiaComponent.PERSON:
                store = people;
                break;
            case ColloquiaComponent.RESOURCE:
                store = resources;
                break;
            case ColloquiaComponent.ACTIVITY:
                store = activities;
                break;
            case ColloquiaComponent.ASSIGNMENT:
                store = assignments;
                break;
            default:
                return false;
        }

        if(store.removeElement(tc)) {
            memberInfoTable.remove(tc);
            tc.removeMemberGroup(this);
            if(hot) setTimeStamp();
            return true;
        }

        return false;
    }

    public void setNotHot() {
        removeProperty(HOT, false);
        super.setTimeStamp();
    }

    public boolean isHot() {
        String val = getProperty(HOT);
        return val.toLowerCase().equals("true");
    }

    public void setTimeStamp() {
    	super.setTimeStamp();
        if(isMine()) putProperty(HOT, "true", false);
    }

    public void setInheritsPeople(boolean value, boolean update) {
        if(value) putProperty(INHERITS_PEOPLE, "true", update);
        else removeProperty(INHERITS_PEOPLE, update);
    }

    public boolean inheritsPeople() {
        String val = getProperty(INHERITS_PEOPLE);
        return val.toLowerCase().equals("true");
    }

    public void setAcceptsResources(boolean value, boolean update) {
        if(value) putProperty(ACCEPTS_RESOURCES, "true", update);
        else removeProperty(ACCEPTS_RESOURCES, update);
    }

    public boolean acceptsResources() {
        String val = getProperty(ACCEPTS_RESOURCES);
        return val.toLowerCase().equals("true");
    }

    /**
    * Set an Assignment Grade for a Person
    * This is for a tutor
    */
    public void setPersonAssignmentGrade(Person person, String grade) {
		MemberInfo mInfo = getMemberInfo(person);
        if(mInfo != null) {
			mInfo.putProperty(ASSIGNMENT_GRADE, grade);
        }
    }

    /**
    * Get an Assignment Grade for a Person
    * This is for a tutor
    */
    public String getPersonAssignmentGrade(Person person) {
	    MemberInfo mInfo = getMemberInfo(person);
        return mInfo == null ? "" : mInfo.getProperty(ASSIGNMENT_GRADE);
    }

    /**
    * Person Accepted Activity
    */
    public void setPersonAccepted(Person person, int accepted) {
		MemberInfo mInfo = getMemberInfo(person);
        if(mInfo != null) {
			mInfo.putProperty(ACCEPT_DECLINE, String.valueOf(accepted));
            setTimeStamp();
        }
    }

    /**
    * Query whether a Person member has ACCEPTED/DECLINED/PENDING/AWAITING an Activity
    */
    public int getPersonAcceptedStatus(Person person) {
		MemberInfo mInfo = getMemberInfo(person);
        if(mInfo != null) {
	        String accepted = mInfo.getProperty(ACCEPT_DECLINE);
        	// Backward compat default value = ACCEPTED prior to version 1.3
        	if(accepted.equals("")) return ACCEPTED;
        	else return Integer.parseInt(accepted);
        }
        else return PENDING;
    }

    /**
    * Person has been sent Activity
    */
    public void setPersonSentActivity(Person person, boolean value) {
		MemberInfo mInfo = getMemberInfo(person);
        if(mInfo != null) {
			if(value) mInfo.removeProperty(HAS_ACTIVITY);
            else mInfo.putProperty(HAS_ACTIVITY, "false");
        }
    }

    /**
    * Query whether a Person member has an Activity
    */
    public boolean doesPersonHaveActivity(Person person) {
		MemberInfo mInfo = getMemberInfo(person);
        if(mInfo != null) {
	        String has_activity = mInfo.getProperty(HAS_ACTIVITY);
        	return has_activity.equals("");
        }
        return true;
    }

    /*
    * Mark a Person as Removed from this Activity
    * removed = true, false = unremove
    * This is for a Tutor
    */
    public void setPersonRemoved(Person person, boolean removed) {
		MemberInfo mInfo = getMemberInfo(person);
        if(mInfo != null) {
            if(removed == false) mInfo.removeProperty(MemberInfo.REMOVED);
			else mInfo.putProperty(MemberInfo.REMOVED, String.valueOf(removed));
            setTimeStamp();
        }
    }

    /*
    * Is a Person marked as Removed from this Activity? (Greyed out)
    * This is for a Tutor
    */
    public boolean isPersonRemoved(Person person) {
		MemberInfo mInfo = getMemberInfo(person);
        if(mInfo != null) {
            String s = mInfo.getProperty(MemberInfo.REMOVED);
            return s.equalsIgnoreCase("true");
        }
        else return false;
    }

    /**
    * Set an Assignment Grade for this Activity
    * This is for a student
    */
    public void setAssignmentGrade(Assignment assignment, String grade) {
		MemberInfo mInfo = getMemberInfo(assignment);
        if(mInfo != null) mInfo.putProperty(ASSIGNMENT_GRADE, grade);
    }

    /**
    * Get an Assignment Grade for this Activity
    * This is for a student
    */
    public String getAssignmentGrade(Assignment assignment) {
	    MemberInfo mInfo = getMemberInfo(assignment);
        return mInfo == null ? "" : mInfo.getProperty(ASSIGNMENT_GRADE);
    }


    public Date getDateLastSentActivity(Person person) {
	    MemberInfo mInfo = getMemberInfo(person);
        if(mInfo != null) return mInfo.getPropertyDate(DATE_LAST_SENT_ACTIVITY);
        else return null;
    }

    public void setDateLastSentActivity(Person person, Date date) {
	    MemberInfo mInfo = getMemberInfo(person);
        if(mInfo != null) mInfo.setPropertyDate(DATE_LAST_SENT_ACTIVITY, date);
    }

    /**
    * Adds Person to sub-Activities that have the flag set
    * Returns array of those where added or null if not
    */
    public Activity[] addPersonToInheritingSubActivities(Person person) {
        Vector v = new Vector();

        Activity[] subs = getAllInheritingSubActivities();
        if(subs != null) {
            for(int i = 0; i < subs.length; i++) {
                boolean result = DataModel.addComponent(person, subs[i], true);
                if(result) v.addElement(subs[i]);
            }
        }

        if(v.isEmpty()) return null;
        else {
            Activity[] A = new Activity[v.size()];
            v.copyInto(A);
            return A;
        }
    }

    /**
    * Returns true if tc is allowed to be inserted into this Activity.
    * Returns false if not.
    */
    public boolean isInsertable(ColloquiaComponent tc) {
        if(tc == null || tc == this) return false;

        // Not insertable in Completed state
        if(getState() == COMPLETED) return false;

        // Not insertable in invite
        if(isInvite()) return false;

        // Is the component already in the folder?
        if(hasMember(tc)) return false;

        // Can't insert an Activity/Person/Assignment if this is not mine
        // But can insert Resource
        if(!isMine()) {
            if(tc instanceof Activity && !tc.isMine()) return false;
            if(tc instanceof Person) return false;
            if(tc instanceof PersonGroup) return false;
            if(tc instanceof Assignment) return false;
        }

        // Rules for moving an Activity
        if(tc instanceof Activity) {
            Activity group = (Activity)tc;
            // Can't move to a descendant sub-Group
            if(group.isDescendant(this)) return false;
            // Can't put Completed Activity in
            if(group.isCompleted()) return false;
        }

        // Only one Assignment
        if(tc instanceof Assignment && hasAssignment()) return false;
        if(tc instanceof Assignment) return true;

        // Can't insert Person into template
        if(tc instanceof Person && isTemplate()) return false;
        if(tc instanceof Person) return true;

        if(tc instanceof Resource) return true;

        if(tc instanceof ResourceGroup) return true;
        if(tc instanceof PersonGroup) return true;

        if(tc instanceof Activity) return true;

        return false;
    }

    /**
    * Returns true if we can send it
    */
    public boolean isSendable() {
        return isLive() && isMine();
    }

    public boolean canBeMadeLive() {
        if(isInvite()) return false;
        if(isTemplate()) return false;
        if(isLive()) return false;

        if(getParent() instanceof Activity) {
            Activity parentA = (Activity)getParent();
            if(parentA.isCompleted()) return false;
            if(parentA.isLive()) return true;
        }

        return true;
    }

    public boolean canBeMadeCompleted() {
        if(isInvite()) return false;
        else if(isTemplate()) return false;
        else if(isCompleted()) return false;
        else return true;
    }

    /**
    * Returns ALL People regardless of Accepted/Declined
    */
    public Vector getAllPeople() {
        return people;
    }

    /**
    * Returns only those people who are active in this Activity
    * If this Activity is Mine:
    *   They have Accepted the Activity
    * If this Activity is not mine:
    *   They are not freed / greyed out (MemberInfo.FREE = false)
    *   They are not removed (MemberInfo.REMOVED = false)
    */
    public Vector getActivePeople() {
        Vector v = new Vector();

    	// If this Activity is mine return all accepted/awaiting people
        if(isMine()) {
            for(int i = 0; i < people.size(); i++) {
                Person person = (Person)people.elementAt(i);
                if(isPersonRemoved(person) == false) {
                	int accepted = getPersonAcceptedStatus(person);
                	if(accepted == ACCEPTED) v.addElement(person);
                }
            }
        }

        // Else if this Activity is not mine, return people who are locked
        else {
            for(int i = 0; i < people.size(); i++) {
                Person person = (Person)people.elementAt(i);
                if(!isComponentFree(person)) v.addElement(person);
            }
        }

        return v;
    }

    /**
    * Returns only those people who are Pending in this Activity
    * If this Activity is Mine:
    *   They have not Accepted or Declined the Activity (MemberInfo.PENDING)
    * If this Activity is not mine (this should not happen):
    *   Return empty Vector
    */
    public Vector getPendingPeople() {
        Vector v = new Vector();

    	// If this Activity is mine return all Pending people
        if(isMine()) {
            for(int i = 0; i < people.size(); i++) {
                Person person = (Person)people.elementAt(i);
                int accepted = getPersonAcceptedStatus(person);
                if(accepted == PENDING) v.addElement(person);
            }
        }

        return v;
    }

    public Vector getResources() {
        return resources;
    }

    public Vector getAssignments() {
        return assignments;
    }

    public Vector getActivities() {
        return activities;
    }

    public boolean hasActivities() { return !activities.isEmpty(); }

    public boolean hasAssignment() { return !assignments.isEmpty(); }

    /**
    * Returns the single Assignment, or null if there isn't one
    */
    public Assignment getAssignment() {
        if(assignments.isEmpty()) return null;
        else return (Assignment)assignments.firstElement();
    }

    public boolean hasMember(ColloquiaComponent tc) {
        if(tc == null) return false;
        switch(tc.getType()) {
            case ColloquiaComponent.PERSON:
                return people.contains(tc);
            case ColloquiaComponent.RESOURCE:
                return resources.contains(tc);
            case ColloquiaComponent.ACTIVITY:
                return activities.contains(tc);
            case ColloquiaComponent.ASSIGNMENT:
                return assignments.contains(tc);
            default:
                return false;
        }
    }

    /**
    * Return ALL members, People, Resources, Assignment(s) and sub-As
    */
    public Vector getMembers() {
        Vector members = new Vector();

        for(int i = 0; i < people.size(); i++) {
            members.addElement(people.elementAt(i));
        }

        for(int i = 0; i < resources.size(); i++) {
            members.addElement(resources.elementAt(i));
        }

        for(int i = 0; i < assignments.size(); i++) {
            members.addElement(assignments.elementAt(i));
        }

        for(int i = 0; i < activities.size(); i++) {
            members.addElement(activities.elementAt(i));
        }

        return members;
    }

    /**
    * Return Active People, Resources, Assignment(s) and sub-As
    */
    public Vector getActiveComponents() {
        Vector members = new Vector();

        Vector activePeople = getActivePeople();
        for(int i = 0; i < activePeople.size(); i++) {
            members.addElement(activePeople.elementAt(i));
        }

        for(int i = 0; i < resources.size(); i++) {
            members.addElement(resources.elementAt(i));
        }

        for(int i = 0; i < assignments.size(); i++) {
            members.addElement(assignments.elementAt(i));
        }

        for(int i = 0; i < activities.size(); i++) {
            members.addElement(activities.elementAt(i));
        }

        return members;
    }

    /**
    * Returns true if tc is a descendant of this Group
    */
    public boolean isDescendant(ColloquiaComponent tc) {
        if(tc == null) return false;
        if(getMembers().contains(tc)) return true;

        // Sub Activities
        for(int i = 0; i < activities.size(); i++) {
            Activity A = (Activity)activities.elementAt(i);
            if(A.isDescendant(tc)) return true;
        }
        return false;
    }

    /**
    * Returns true if tc is allowed to be removed from this Group
    */
    public boolean isRemovable(ColloquiaComponent tc) {
        if(!hasMember(tc)) return false;

        // Can cut if this is a sub-Activity and is Invite
        if(tc instanceof Activity) {
            Activity subA = (Activity)tc;
            if(subA.isInvite()) return true;
        }

        // Can cut if this (parent) Activity is Completed
        if(isCompleted()) return true;

        // Can't cut if not free
        return isComponentFree(tc);
    }

    public boolean isMovable(ColloquiaComponent tc, ColloquiaContainer target) {
        return isRemovable(tc) && target.isInsertable(tc);
    }

    /**
    * Returns the number of parent Activities (depth value)
    */
    public int getNumberParentActivities() {
        int count = 0;

        ColloquiaContainer parentActivity = getParent();
        while(parentActivity != null) {
            if(parentActivity instanceof Activity) count++;
            parentActivity = parentActivity.getParent();
        }

        return count;
    }

    /**
    * Returns only Live sub-Activities
    */
    public Vector getLiveActivities() {
        Vector members = new Vector();
        for(int i = 0; i < activities.size(); i++) {
            Activity activity = (Activity)activities.elementAt(i);
            if(activity.isLive()) members.addElement(activity);
        }
        return members;
    }

    /**
    * Returns all subActivities right to the bottom
    */
    public Vector getAllSubActivities() {
        Vector v = new Vector();
        _getAllSubActivities(this, v);
        return v;
    }

    private void _getAllSubActivities(Activity A, Vector v) {
        Vector activities = A.getActivities();
        for(int i = 0; i < activities.size(); i++) {
            Activity activity = (Activity)activities.elementAt(i);
            v.addElement(activity);
            _getAllSubActivities(activity, v);
        }
    }

    /**
    * Returns all subActivities that inherit People from this Activity
    */
    public Activity[] getAllInheritingSubActivities() {
        Vector v = new Vector();
        _getAllInheritingSubActivities(this, v);
        if(v.isEmpty()) return null;
        else {
            Activity[] A = new Activity[v.size()];
            v.copyInto(A);
            return A;
        }
    }

    private void _getAllInheritingSubActivities(Activity A, Vector v) {
        Vector activities = A.getActivities();
        for(int i = 0; i < activities.size(); i++) {
            Activity activity = (Activity)activities.elementAt(i);
            if(activity.isMine() && activity.inheritsPeople() && !activity.isCompleted()) {
                v.addElement(activity);
            }
            if(activity.inheritsPeople()) _getAllInheritingSubActivities(activity, v);
        }
    }

    /**
    * Returns true if this Activity has members, or false if none.
    */
    public boolean hasMembers() {
        return !(people.isEmpty() && resources.isEmpty() && activities.isEmpty()
            && assignments.isEmpty());
    }

    public void setLive(boolean update) {
        putProperty(STATE, String.valueOf(LIVE), update);
        // Check sub-Activities
        for(int i = 0; i < activities.size(); i++) {
            Activity A = (Activity)activities.elementAt(i);
            if(A.isTemplate()) A.setLive(update);
        }
    }

    /**
    * If Mine only Mine sub-As are set and if not mine
    */
    public void setCompleted() {
        putProperty(STATE, String.valueOf(COMPLETED), false);
        setNotHot();
        // Check sub-Activities
        for(int i = 0; i < activities.size(); i++) {
            Activity A = (Activity)activities.elementAt(i);
            if(isMine() && A.isMine()) A.setCompleted();
            else if(!isMine() && !A.isMine()) A.setCompleted();
        }
    }

    public void setTemplate(boolean update) {
        putProperty(STATE, String.valueOf(TEMPLATE), update);
        // Check sub-Activities
        for(int i = 0; i < activities.size(); i++) {
            Activity A = (Activity)activities.elementAt(i);
            A.setTemplate(update);
        }
    }

    public int getState() {
        String state = getProperty(STATE);

        int st = Integer.parseInt(state);

        // Little work-around for the old FUTURE STATE
        if(st == FUTURE) {
        	st = LIVE;
            setLive(false);
        }

        return st;
    }

    /**
    * Set the ACCEPTED, DECLINED OR PENDING STATE
    */
    public void setAcceptedState(int state) {
        putProperty(ACCEPT_DECLINE, String.valueOf(state), false);
    }

    /**
    * For a recipient, get AcceptedState of Activity
    */
    public int getAcceptedState() {
        String state = getProperty(ACCEPT_DECLINE);
        return state.equals("") ? ACCEPTED : Integer.parseInt(state);
    }

    // These relate to the state of the Activity for a recipient
    public boolean isPending() { return getAcceptedState() == PENDING; }
    public boolean isAccepted() { return getAcceptedState() == ACCEPTED; }
    public boolean isDeclined() { return getAcceptedState() == DECLINED; }
    public boolean isLive() { return getState() == LIVE; }
    public boolean isCompleted() { return getState() == COMPLETED; }
    public boolean isTemplate() { return getState() == TEMPLATE; }
    public boolean isInvite() { return getState() == INVITE; }

    public static String XMLStartTag = "<activity>";
    public static String XMLEndTag = "</activity>";

    public void write2XML(XMLWriter writer) throws XMLWriteException {
        writer.writeln(isTemplate() ? TemplateGroup.XMLStartTag : XMLStartTag);
        super.write2XML(writer);
        writer.writeln(isTemplate() ? TemplateGroup.XMLEndTag : XMLEndTag);
    }

    public void write2Element(Element parent) {
    	Element element = new Element(isTemplate() ? "template" : "activity");
        super.write2Element(element);
        parent.addContent(element);
    }

    /**
    */
    public SentComponent getSentComponent() {
    	return new SentActivity(this);
    }

    /**
    * Returns a copy with new child members
    */
    public ColloquiaComponent copy() {
        // Make a new one with new GUID
        Activity A = new Activity("", null);
        // Save GUID
        String newGUID = A.getGUID();
        // Clone Properties
        A.setProperties((Hashtable)getProperties().clone());
        // Change some properties
        A.setGUID(newGUID);
        //A.setName("(" + getName() + ")", false);
        //A.setName(getName(), false);
        A.setSubmitter("ME", false);
        A.setPropertyDate(DATE_CREATED, Utils.getNow());
        A.setPropertyDate(DATE_MODIFIED, Utils.getNow());

        Vector members = getMembers();
        for(int i = 0; i < members.size(); i++) {
            ColloquiaComponent tc = (ColloquiaComponent)members.elementAt(i);
            if(tc instanceof Activity) tc = tc.copy();
            A.addComponent(tc, null, false);
        }

        copyTextFiles(A);

        return A;
    }

    /**
    * Returns a template copy
    */
    public Activity copyTemplate() {
        // Make a new one with new GUID
        Activity A = new Activity("", null);
        // Save GUID
        String newGUID = A.getGUID();
        // Clone Properties
        A.setProperties((Hashtable)getProperties().clone());
        // Change some properties
        A.setGUID(newGUID);
        A.setSubmitter("ME", false);
        A.setTemplate(false);
        A.setPropertyDate(DATE_CREATED, Utils.getNow());
        A.setPropertyDate(DATE_MODIFIED, Utils.getNow());

        Vector members = getMembers();
        for(int i = 0; i < members.size(); i++) {
            ColloquiaComponent tc = (ColloquiaComponent)members.elementAt(i);
            if(tc instanceof Person) continue;  // No People

            if(tc instanceof Activity) tc = ((Activity)tc).copyTemplate();
            // If not mine, make a copy
            //else if(!tc.isMine()) tc = tc.copy();
            A.addComponent(tc, null, false);
        }

        copyTextFiles(A);

        return A;
    }

    /**
    * Copy the text files associated with this to newCopy
    */
    public void copyTextFiles(ColloquiaComponent newCopy) {
        // Notes files
        String thisNotesFileName = DataFiler.getTextFileName(this, DataFiler.PERSONAL_NOTES, false);
        String copyNotesFileName = DataFiler.getTextFileName(newCopy, DataFiler.PERSONAL_NOTES, true);
        // Description files
        String thisDescFileName = DataFiler.getTextFileName(this, DataFiler.DESCRIPTION, false);
        String copyDescFileName = DataFiler.getTextFileName(newCopy, DataFiler.DESCRIPTION, true);
        // Objectives files
        String thisObjsFileName = DataFiler.getTextFileName(this, DataFiler.OBJECTIVES, false);
        String copyObjsFileName = DataFiler.getTextFileName(newCopy, DataFiler.OBJECTIVES, true);
        try {
            DataFiler.copyFile(thisNotesFileName, copyNotesFileName);
            DataFiler.copyFile(thisDescFileName, copyDescFileName);
            DataFiler.copyFile(thisObjsFileName, copyObjsFileName);
        }
        catch(java.io.IOException ex) {
            System.out.println("copyComponentFiles error: " + ex);
        }
    }
}

