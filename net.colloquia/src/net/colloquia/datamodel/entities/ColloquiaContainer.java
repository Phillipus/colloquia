package net.colloquia.datamodel.entities;

import java.util.*;

import net.colloquia.datamodel.*;
import net.colloquia.xml.*;

import org.jdom.*;

public abstract class ColloquiaContainer
extends ColloquiaComponent
{
    private ColloquiaContainer parent;
    // Table of MemberInfo classes
    protected Hashtable memberInfoTable = new Hashtable();

    // Properties key
    public static final String PARENT_GUID = "parent_guid";

    protected ColloquiaContainer(String name, String GUID, int type) {
        super(name, GUID, type);
    }

    public Vector getTableFieldValues() {
    	return null;
    }

    public ColloquiaContainer getParent() {
        return parent;
    }

    public void setParent(ColloquiaContainer parent) {
        this.parent = parent;
        putProperty(PARENT_GUID, parent == null ? "null" : parent.getGUID(), false);
    }

    /**
    * Return all the MemberInfos
    */
    public Hashtable getMemberInfoTable() {
        return memberInfoTable;
    }

    // Free
    public boolean isComponentFree(ColloquiaComponent tc) {
        MemberInfo mInfo = getMemberInfo(tc);
        if(mInfo != null) {
            return !mInfo.getProperty(MemberInfo.FREE).equalsIgnoreCase("false");
        }
        else return false;
    }

    public void setComponentFree(ColloquiaComponent tc, boolean free) {
        MemberInfo mInfo = getMemberInfo(tc);
        if(mInfo != null) {
        	if(free == false) mInfo.putProperty(MemberInfo.FREE, "false");
            else mInfo.removeProperty(MemberInfo.FREE);
        }
    }

    public MemberInfo getMemberInfo(ColloquiaComponent tc) {
        if(tc == null) return null;
        return (MemberInfo)memberInfoTable.get(tc);
    }

    public boolean addComponent(ColloquiaComponent tc, boolean hot) {
        return addComponent(tc, null, hot);
    }

    public boolean addComponent(MemberInfo mInfo, boolean hot) {
        ColloquiaComponent tc = mInfo.getComponent();
        ColloquiaContainer parent = mInfo.getParent();
        if(parent != null && tc != null && parent == this) return addComponent(tc, mInfo, hot);
        else return false;
    }

    protected abstract boolean addComponent(ColloquiaComponent tc, MemberInfo mInfo, boolean hot);

    public void write2XML(XMLWriter writer) throws XMLWriteException {
        super.write2XML(writer);
        // Write member info
        Vector members = getMembers();
        for(int i = 0; i < members.size(); i++) {
            ColloquiaComponent member = (ColloquiaComponent)members.elementAt(i);
            MemberInfo mInfo = getMemberInfo(member);
            mInfo.write2XML(writer);
        }
    }

    public void write2Element(Element parent) {
    	super.write2Element(parent);
        // Write member info
        Vector members = getMembers();
        for(int i = 0; i < members.size(); i++) {
            ColloquiaComponent member = (ColloquiaComponent)members.elementAt(i);
            MemberInfo mInfo = getMemberInfo(member);
            mInfo.write2Element(parent);
        }
	}

    public abstract boolean removeComponent(ColloquiaComponent tc, boolean hot);

    public abstract boolean hasMember(ColloquiaComponent tc);
    public abstract boolean hasMembers();
    public abstract Vector getMembers();
    public abstract boolean isInsertable(ColloquiaComponent tc);
    public abstract boolean isRemovable(ColloquiaComponent tc);
    public abstract boolean isMovable(ColloquiaComponent tc, ColloquiaContainer target);
    public abstract boolean isDescendant(ColloquiaComponent tc);

    //============================ DEBUG =======================================
    public void printMemberInfos(Person person) {
    	Hashtable table = getMemberInfoTable();
        Enumeration e = table.keys();
        while(e.hasMoreElements()) {
        	ColloquiaComponent tc = (ColloquiaComponent)e.nextElement();
            System.out.println(tc);
        }
    }
}

