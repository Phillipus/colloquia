package net.colloquia.datamodel.entities;

import java.util.*;

import net.colloquia.datamodel.*;
import net.colloquia.xml.*;

import org.jdom.*;

public class PersonGroup extends ColloquiaContainer {
    // We use separate Vectors because of Sorting
    private Vector people = new Vector();
    private Vector peopleGroups = new Vector();

    public PersonGroup(String name, String GUID)  {
        super(name, GUID, ColloquiaComponent.PERSON_GROUP);
    }

    /**
    * Add a Person to this Group
    * @return true if successful
    */
    protected synchronized boolean addComponent(ColloquiaComponent tc, MemberInfo mInfo, boolean hot) {
        if(tc == null) return false;
        Vector store;

        switch(tc.getType()) {
            case ColloquiaComponent.PERSON:
                store = people;
                break;

            case ColloquiaComponent.PERSON_GROUP:
                store = peopleGroups;
                ((ColloquiaContainer)tc).setParent(this);
                break;

            default:
                return false;
        }

        if(!store.contains(tc)) {
            tc.addMemberGroup(this);
            store.addElement(tc);
            // We can re-use an existing MemberInfo
            if(mInfo != null) memberInfoTable.put(tc, mInfo);
            else memberInfoTable.put(tc, new MemberInfo(tc, this));
            return true;
        }

        return false;
    }

    /**
    * Remove a Person from this Group
    * @return true if removed, false if not
    */
    public synchronized boolean removeComponent(ColloquiaComponent tc, boolean hot) {
        if(tc == null) return false;
        Vector store;

        switch(tc.getType()) {
            case ColloquiaComponent.PERSON:
                store = people;
                break;
            case ColloquiaComponent.PERSON_GROUP:
                store = peopleGroups;
                break;
            default:
                return false;
        }

        if(store.removeElement(tc)) {
            memberInfoTable.remove(tc);
            tc.removeMemberGroup(this);
            return true;
        }

        return false;
    }

    public Vector getPeople() {
        return people;
    }

    public Vector getPeopleGroups() {
        return peopleGroups;
    }

    /**
    * Returns true if person is in this People folder.
    */
    public boolean hasMember(ColloquiaComponent tc) {
        if(tc == null) return false;
        switch(tc.getType()) {
            case ColloquiaComponent.PERSON:
                return people.contains(tc);
            case ColloquiaComponent.PERSON_GROUP:
                return peopleGroups.contains(tc);
            default:
                return false;
        }
    }

    public boolean hasMembers() {
        return !(people.isEmpty() && peopleGroups.isEmpty());
    }

    /**
    * Return ALL members, both People and Groups
    */
    public Vector getMembers() {
        Vector members = new Vector();
        for(int i = 0; i < people.size(); i++) {
            members.addElement(people.elementAt(i));
        }
        for(int i = 0; i < peopleGroups.size(); i++) {
            members.addElement(peopleGroups.elementAt(i));
        }
        return members;
    }

    public boolean isInsertable(ColloquiaComponent tc) {
        if(tc == null || tc == this) return false;
        if(!(tc instanceof Person) && !(tc instanceof PersonGroup)) return false;

        // Is the component already in the folder?
        if(hasMember(tc)) return false;

        // Person Group rules for moving a Person Group
        if(tc instanceof PersonGroup) {
            PersonGroup group = (PersonGroup)tc;
            // Can't move to a descendant sub-Group
            if(group.isDescendant(this)) return false;
        }

        return true;
    }

    /**
    * Returns true if tc is allowed to be removed from this Group
    */
    public boolean isRemovable(ColloquiaComponent tc) {
        if(tc == null) return false;
        if(!hasMember(tc)) return false;

        // Can't cut if not free
        if(!isComponentFree(tc)) return false;

        // Count activity membership - can't cut if in Activities
        if(tc instanceof Person) {
            int totalInstances = tc.getInstanceCount();
            if(totalInstances == 1) return true;

            int topInstances = DataModel.getTopPersonCount((Person)tc);
            if(topInstances > 1) return true;

        	int activityInstances = tc.getActivities().size();
        	if(activityInstances > 0) return false;
        }

        // If we are deleting a PersonGroup we have to drill in to check
        // Whether we can remove children
        else if(tc instanceof PersonGroup) {
            PersonGroup pg = (PersonGroup)tc;
        	Vector v = pg.getMembers();
            for(int i = 0; i < v.size(); i++) {
                ColloquiaComponent tc1 = (ColloquiaComponent)v.elementAt(i);
                if(!pg.isRemovable(tc1)) return false;
            }
        }

        return true;
    }

    public boolean isMovable(ColloquiaComponent tc, ColloquiaContainer target) {
        if(tc == null) return false;
        if(!hasMember(tc)) return false;
        if(target.hasMember(tc)) return false;
        // Resource Group rules for moving a Person Group
        if(tc instanceof PersonGroup) {
            PersonGroup group = (PersonGroup)tc;
            // Can't move to a descendant sub-Group
            if(group.isDescendant(target)) return false;
        }
        return (target instanceof PersonGroup);
    }

    /**
    * Returns true if tc is a descendant of this Group
    */
    public boolean isDescendant(ColloquiaComponent tc) {
        if(tc == null) return false;
        if(people.contains(tc)) return true;
        if(peopleGroups.contains(tc)) return true;
        // Sub Person Groups
        for(int i = 0; i < peopleGroups.size(); i++) {
            PersonGroup group = (PersonGroup)peopleGroups.elementAt(i);
            if(group.isDescendant(tc)) return true;
        }
        return false;
    }

    public boolean isMine() { return true; } // Over-ride this

    public ColloquiaComponent copy() {
        PersonGroup pg = new PersonGroup("", null);
        pg.setName(getName() + " (copy)", false);

        Vector members = getMembers();
        for(int i = 0; i < members.size(); i++) {
            ColloquiaComponent tc = (ColloquiaComponent)members.elementAt(i);
            if(tc instanceof PersonGroup) tc = tc.copy();
            pg.addComponent(tc, null, false);
        }

        return pg;
    }

    public static String XMLStartTag = "<people_group>";
    public static String XMLEndTag = "</people_group>";
    public static String oldXMLStartTag = "<people group>";
    public static String oldXMLEndTag = "</people group>";

    public void write2XML(XMLWriter writer) throws XMLWriteException {
        writer.writeln(XMLStartTag);
        super.write2XML(writer);
        writer.writeln(XMLEndTag);
    }

    public void write2Element(Element parent) {
    	Element element = new Element("people_group");
        super.write2Element(element);
        parent.addContent(element);
	}

    // We don't send this type of Group
    public SentComponent getSentComponent() {
		return null;
    }

}


