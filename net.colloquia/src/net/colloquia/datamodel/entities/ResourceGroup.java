package net.colloquia.datamodel.entities;

import java.util.*;

import net.colloquia.datamodel.*;
import net.colloquia.xml.*;

import org.jdom.*;

public class ResourceGroup
extends ColloquiaContainer
{
    // We use separate Vectors because of Sorting
    private Vector resources = new Vector();
    private Vector resourceGroups = new Vector();

    public ResourceGroup(String name, String GUID)  {
        super(name, GUID, ColloquiaComponent.RESOURCE_GROUP);
    }

    protected synchronized boolean addComponent(ColloquiaComponent tc, MemberInfo mInfo, boolean hot) {
        if(tc == null) return false;
        Vector store;

        switch(tc.getType()) {
            case ColloquiaComponent.RESOURCE:
                store = resources;
                break;

            case ColloquiaComponent.RESOURCE_GROUP:
                store = resourceGroups;
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
    * Remove a Resource from this Group
    * @return true if removed, false if not
    */
    public synchronized boolean removeComponent(ColloquiaComponent tc, boolean hot) {
        if(tc == null) return false;
        Vector store;

        switch(tc.getType()) {
            case ColloquiaComponent.RESOURCE:
                store = resources;
                break;
            case ColloquiaComponent.RESOURCE_GROUP:
                store = resourceGroups;
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

    public Vector getResources() {
        return resources;
    }

    public Vector getResourceGroups() {
        return resourceGroups;
    }

    /**
    * Returns the first Resource member found by name
    */
    public Resource getResource(String name) {
        if(name == null) return null;
    	Vector v = getResources();
        for(int i = 0; i < v.size(); i++) {
        	Resource resource = (Resource)v.elementAt(i);
            if(resource.getName().equalsIgnoreCase(name)) return resource;
        }
        return null;
    }

    /**
    * Returns the first ResourceGroup member found by name
    */
    public ResourceGroup getResourceGroup(String name) {
        if(name == null) return null;
    	Vector v = getResourceGroups();
        for(int i = 0; i < v.size(); i++) {
        	ResourceGroup rg = (ResourceGroup)v.elementAt(i);
            if(rg.getName().equalsIgnoreCase(name)) return rg;
        }
        return null;
    }

    /**
    * Returns true if resource is in this Resource folder.
    * Or ResourceGroup
    */
    public boolean hasMember(ColloquiaComponent tc) {
        if(tc == null) return false;
        switch(tc.getType()) {
            case ColloquiaComponent.RESOURCE:
                return resources.contains(tc);
            case ColloquiaComponent.RESOURCE_GROUP:
                return resourceGroups.contains(tc);
            default:
                return false;
        }
    }

    public boolean hasMembers() {
        return !(resources.isEmpty() && resourceGroups.isEmpty());
    }

    /**
    * Return ALL members, both Resources and Groups
    */
    public Vector getMembers() {
        Vector members = new Vector();
        for(int i = 0; i < resources.size(); i++) {
            members.addElement(resources.elementAt(i));
        }
        for(int i = 0; i < resourceGroups.size(); i++) {
            members.addElement(resourceGroups.elementAt(i));
        }
        return members;
    }

    /**
    * Returns true if tc is allowed to be inserted into this Group.
    * Returns false if not.
    */
    public boolean isInsertable(ColloquiaComponent tc) {
        if(tc == null || tc == this) return false;
        if(!(tc instanceof Resource) && !(tc instanceof ResourceGroup)) return false;

        // Is the component already in the folder?
        if(hasMember(tc)) return false;

        // Resource Group rules for moving a Resource Group
        if(tc instanceof ResourceGroup) {
            ResourceGroup group = (ResourceGroup)tc;
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
        if(tc instanceof Resource) {
            int totalInstances = tc.getInstanceCount();
            if(totalInstances == 1) return true;

            int topInstances = DataModel.getTopResourceCount((Resource)tc);
            if(topInstances > 1) return true;

        	int activityInstances = tc.getActivities().size();
        	if(activityInstances > 0) return false;
        }

        // If we are deleting a ResourceGroup we have to drill in to check
        // Whether we can remove children
        else if(tc instanceof ResourceGroup) {
            ResourceGroup rg = (ResourceGroup)tc;
        	Vector v = rg.getMembers();
            for(int i = 0; i < v.size(); i++) {
                ColloquiaComponent tc1 = (ColloquiaComponent)v.elementAt(i);
                if(!rg.isRemovable(tc1)) return false;
            }
        }

        return true;
    }

    public boolean isMovable(ColloquiaComponent tc, ColloquiaContainer target) {
        if(tc == null) return false;
        if(!hasMember(tc)) return false;
        if(target.hasMember(tc)) return false;
        // Resource Group rules for moving a Resource Group
        if(tc instanceof ResourceGroup) {
            ResourceGroup group = (ResourceGroup)tc;
            // Can't move to a descendant sub-Group
            if(group.isDescendant(target)) return false;
        }
        return (target instanceof ResourceGroup);
    }

    /**
    * Returns true if tc is a descendant of this Group
    */
    public boolean isDescendant(ColloquiaComponent tc) {
        if(tc == null) return false;
        if(resources.contains(tc)) return true;
        if(resourceGroups.contains(tc)) return true;
        // Sub Resource Groups
        for(int i = 0; i < resourceGroups.size(); i++) {
            ResourceGroup group = (ResourceGroup)resourceGroups.elementAt(i);
            if(group.isDescendant(tc)) return true;
        }
        return false;
    }

    public boolean isMine() { return true; } // Over-ride this

    public ColloquiaComponent copy() {
        ResourceGroup rg = new ResourceGroup("", null);
        rg.setName(getName() + " (copy)", false);

        Vector members = getMembers();
        for(int i = 0; i < members.size(); i++) {
            ColloquiaComponent tc = (ColloquiaComponent)members.elementAt(i);
            if(tc instanceof ResourceGroup) tc = tc.copy();
            rg.addComponent(tc, null, false);
        }

        return rg;
    }

    public static String XMLStartTag = "<resource_group>";
    public static String XMLEndTag = "</resource_group>";
    public static String oldXMLStartTag = "<resource group>";
    public static String oldXMLEndTag = "</resource group>";

    public void write2XML(XMLWriter writer) throws XMLWriteException {
        writer.writeln(XMLStartTag);
        super.write2XML(writer);
        writer.writeln(XMLEndTag);
    }

    public void write2Element(Element parent) {
    	Element element = new Element("resource_group");
        super.write2Element(element);
        parent.addContent(element);
	}

    // We don't send this type of Group
    public SentComponent getSentComponent() {
		return null;
    }
}


