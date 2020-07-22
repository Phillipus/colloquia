package net.colloquia.datamodel.entities;

import java.util.*;

import net.colloquia.datamodel.*;
import net.colloquia.xml.*;



/**
* A Vanilla group that does nothing
*/
public class Group extends ColloquiaContainer {
    public Group(String name, String GUID)  {
        super(name, GUID, ColloquiaComponent.GROUP);
    }

    public boolean hasMember(ColloquiaComponent tc) { return false; }
    public boolean hasMembers() { return false; }
    public Vector getMembers() { return new Vector(); }
    public boolean isInsertable(ColloquiaComponent tc) { return false; }
    public boolean isDescendant(ColloquiaComponent tc) { return false; }
    public boolean isRemovable(ColloquiaComponent tc) { return false; }
    public boolean isMovable(ColloquiaComponent tc, ColloquiaContainer target) { return false; }

    public boolean addComponent(ColloquiaComponent tc, MemberInfo mInfo, boolean hot) { return false; }
    public boolean addComponent(MemberInfo mInfo, boolean hot) { return false; }
    public boolean removeComponent(ColloquiaComponent tc, boolean hot) { return false; }

    public boolean isMine() { return true; } // Over-ride this
    public ColloquiaComponent copy() { return null; }

    public static String XMLStartTag = "<group>";
    public static String XMLEndTag = "</group>";

    public void write2XML(XMLWriter writer) throws XMLWriteException {
        writer.writeln(XMLStartTag);
        super.write2XML(writer);
        writer.writeln(XMLEndTag);
    }

    // We don't send this type of Group
    public SentComponent getSentComponent() {
		return null;
    }

}


