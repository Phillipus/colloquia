package net.colloquia.datamodel.entities;

import java.util.*;

import net.colloquia.datamodel.*;
import net.colloquia.xml.*;

import org.jdom.*;

/**
* This is the topmost Activity Group
*/
public class ActivityGroup
extends ColloquiaContainer
{
    private Vector activities = new Vector();

    public ActivityGroup(String name, String GUID)  {
        super(name, GUID, ColloquiaComponent.ACTIVITY_GROUP);
    }

    protected synchronized boolean addComponent(ColloquiaComponent tc, MemberInfo mInfo, boolean hot) {
        if(tc == null || !(tc instanceof Activity)) return false;

        Activity A = (Activity)tc;

        if(!activities.contains(A)) {
            A.addMemberGroup(this);
            A.setParent(this);
            // If adding a Template make it Live
            if(A.isTemplate()) A.setLive(true);
            activities.addElement(A);
            // We can re-use an existing MemberInfo
            if(mInfo != null) memberInfoTable.put(A, mInfo);
            else memberInfoTable.put(A, new MemberInfo(A, this));
            return true;
        }
        else return false;
    }

    public synchronized boolean removeComponent(ColloquiaComponent tc, boolean hot) {
        if(tc == null) return false;

        if(activities.removeElement(tc)) {
            memberInfoTable.remove(tc);
            tc.removeMemberGroup(this);
            return true;
        }
        else return false;
    }

    public boolean hasMember(ColloquiaComponent tc) {
        if(tc == null) return false;
        return activities.contains(tc);
    }

    public boolean hasMembers() {
        return !activities.isEmpty();
    }

    public Vector getMembers() {
        return activities;
    }

    public boolean isInsertable(ColloquiaComponent tc) {
        if(tc == null || tc == this) return false;
        if(!(tc instanceof Activity)) return false;

        // Is the component already in the folder?
        if(hasMember(tc)) return false;

        // Can't put Completed Activity in
        if(((Activity)tc).isCompleted()) return false;

        return true;
    }

    /**
    * Returns true if tc is a descendant of this Group
    */
    public boolean isDescendant(ColloquiaComponent tc) {
        if(tc == null) return false;
        if(activities.contains(tc)) return true;

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
        if(tc == null || tc == this) return false;
        if(!hasMember(tc)) return false;

        if(tc instanceof Activity) {
            Activity A = (Activity)tc;
            // Can cut if this is a sub-Activity and is Invite
            if(A.isInvite()) return true;
            // Can cut if this Activity is Completed
            if(A.isCompleted()) return true;
        }

        // Can't cut if not free
        return isComponentFree(tc);
    }

    public boolean isMovable(ColloquiaComponent tc, ColloquiaContainer target) {
        return isRemovable(tc) && target.isInsertable(tc);
    }

    public boolean isMine() { return true; } // Over-ride this
    public ColloquiaComponent copy() { return null; }

    public static String XMLStartTag = "<activity>";
    public static String XMLEndTag = "</activity>";

    public void write2XML(XMLWriter writer) throws XMLWriteException {
        writer.writeln(XMLStartTag);
        super.write2XML(writer);
        writer.writeln(XMLEndTag);
    }

    public void write2Element(Element parent) {
    	Element element = new Element("activity");
        super.write2Element(element);
        parent.addContent(element);
	}

    // We don't send the top Activity Group
    public SentComponent getSentComponent() {
		return null;
    }

}
