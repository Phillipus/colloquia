package net.colloquia.datamodel;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import net.colloquia.datamodel.entities.*;
import net.colloquia.io.DataFiler;
import net.colloquia.util.LanguageManager;
import net.colloquia.util.Utils;
import net.colloquia.xml.XMLFileWriter;
import net.colloquia.xml.XMLUtils;
import net.colloquia.xml.XMLWriteException;
import net.colloquia.xml.XMLWriter;

import org.jdom.Document;
import org.jdom.Element;

public final class DataModel {
    // The Main Bunch of People
    private static PersonGroup peopleGroup;
    // The Main resource Group
    private static ResourceGroup resourceGroup;
    // The Top Main Bunch of Activities
    private static ActivityGroup activityGroup;
    // The Top Main Bunch of Templates
    private static TemplateGroup templateGroup;

    // List of GUIDs and their components
    private static Hashtable GUIDS;


    public static void initialise() {
        // This will just instantiate and ensure static stuff is set up
        addDataListener(new DataToViewAdaptor());
        init();
    }

    public static boolean isTopPeopleGroup(ColloquiaContainer group) {
        return group.getGUID().equals("1-1");
    }

    public static boolean isTopResourceGroup(ColloquiaContainer group) {
        return group.getGUID().equals("2-2");
    }

    public static boolean isTopActivityGroup(ColloquiaContainer group) {
        return group.getGUID().equals("3-3");
    }

    public static boolean isTemplatesGroup(ColloquiaContainer group) {
        return group.getGUID().equals("4-4");
    }

    public static boolean isCoreGroup(ColloquiaContainer group) {
        return isTopPeopleGroup(group) || isTopResourceGroup(group) ||
            isTopActivityGroup(group)  || isTemplatesGroup(group);
    }


    /**
     * Clears all data to default blank settings
     */
    public static void newData() {
        init();
        fireNewDataCreated();
    }

    private static void init() {
        peopleGroup = new PersonGroup(LanguageManager.getString("7"), "1-1");
        resourceGroup = new ResourceGroup(LanguageManager.getString("9"), "2-2");
        activityGroup = new ActivityGroup(LanguageManager.getString("1"), "3-3");
        templateGroup = new TemplateGroup(LanguageManager.getString("12"), "4-4");

        GUIDS = new Hashtable();
        addGUID(peopleGroup);
        addGUID(resourceGroup);
        addGUID(activityGroup);
        addGUID(templateGroup);
    }

    /**
    * Adds a Component with an existing MemberInfo
    */
    public static synchronized boolean addComponent(MemberInfo mInfo, boolean hot) {
        if(mInfo == null) return false;
        boolean result = mInfo.getParent().addComponent(mInfo, hot);
        if(result) fireComponentAdded(mInfo.getComponent(), mInfo.getParent());
        return result;
    }

    /**
    * Adds a Component which requires a new MemberInfo
    */
    public static synchronized boolean addComponent(ColloquiaComponent tc, ColloquiaContainer parent, boolean hot) {
        if(parent == null || tc == null) return false;
        boolean result = parent.addComponent(tc, hot);
        if(result) fireComponentAdded(tc, parent);
        return result;
    }

    /**
    * Removes a Component with an existing MemberInfo
    */
    public static synchronized boolean removeComponent(MemberInfo mInfo, boolean hot) {
        if(mInfo == null) return false;
        else return removeComponent(mInfo.getComponent(), mInfo.getParent(), hot);
    }

    /**
    * Removes a Component
    */
    public static synchronized boolean removeComponent(ColloquiaComponent tc, ColloquiaContainer parent, boolean hot) {
        if(parent == null || tc == null) return false;
        boolean result = parent.removeComponent(tc, hot);
        if(result) fireComponentRemoved(tc, parent);
        return result;
    }


    /**
    * Move a Component from sourceGroup to targetGroup
    * This is a straightforward Move and does no validation
    * as we do in ComponentTransferManager.move
    */

    public static synchronized void moveComponent(ColloquiaComponent tc, ColloquiaContainer sourceParent, ColloquiaContainer targetParent, boolean hot) {
        if(tc == null || sourceParent == null || targetParent == null) return;
        removeComponent(tc, sourceParent, hot);
        addComponent(tc, targetParent, hot);
    }


    // ==================================================================
    // ====================== Data Model Listener Stuff =================
    // ==================================================================

    // Data Model change listeners
    private static Vector listeners = new Vector();

    public static synchronized void addDataListener(DataListener dl) {
        if(!listeners.contains(dl)) listeners.addElement(dl);
    }

    public static synchronized void removeDataListener(DataListener dl) {
        listeners.removeElement(dl);
    }

    /**
    * Tell our listeners that we have new blank data
    */
    public static void fireNewDataCreated() {
        for(int i = 0; i < listeners.size(); i++) {
            DataListener dl = (DataListener)listeners.elementAt(i);
            dl.newDataCreated();
        }
    }

    /**
    * Tell our listeners that we have added a new Component to a Container
    */
    public static void fireComponentAdded(ColloquiaComponent tc, ColloquiaContainer group) {
        for(int i = 0; i < listeners.size(); i++) {
            DataListener dl = (DataListener)listeners.elementAt(i);
            dl.componentAdded(tc, group);
        }
    }

    /**
    * Tell our listeners that we have removed a Component from a Container
    */
    public static void fireComponentRemoved(ColloquiaComponent tc, ColloquiaContainer group) {
        for(int i = 0; i < listeners.size(); i++) {
            DataListener dl = (DataListener)listeners.elementAt(i);
            dl.componentRemoved(tc, group);
        }
    }

    /**
    * Tell our listeners that the order of member components has changed
    */
    public static void fireComponentOrderChanged(Vector members, ColloquiaContainer group) {
        for(int i = 0; i < listeners.size(); i++) {
            DataListener dl = (DataListener)listeners.elementAt(i);
            dl.componentOrderChanged(members, group);
        }
    }

    //==========================================================================
    // ======================= UTILITY METHODS =================================
    //==========================================================================

    /**
    * Returns top People Group
    */
    public static PersonGroup getPeopleGroup() {
        return peopleGroup;
    }

   /**
    * Returns all People (no duplicates)
    */
    public static Vector getAllPeople() {
        Vector v = new Vector();
        _getAllPeople(peopleGroup, v);
        return v;
    }

    private static void _getAllPeople(PersonGroup group, Vector v) {
        if(group == null) return;

        // Do People in this group
        Vector members = group.getMembers();
        for(int i = 0; i < members.size(); i++) {
            ColloquiaComponent tc = (ColloquiaComponent)members.elementAt(i);
            switch(tc.getType()) {
                case ColloquiaComponent.PERSON:
                    if(!v.contains(tc)) v.addElement(tc);
                    break;
                case ColloquiaComponent.PERSON_GROUP:
                    _getAllPeople((PersonGroup)tc, v);
                    break;
            }
        }
    }

    /*
    * Returns all People Groups
    */
    public static Vector getAllPeopleGroups() {
        Vector v = new Vector();
        v.addElement(peopleGroup);
        _getAllPeopleGroups(peopleGroup, v);
        return v;
    }

    private static void _getAllPeopleGroups(PersonGroup group, Vector v) {
        if(group == null) return;

        Vector members = group.getMembers();
        for(int i = 0; i < members.size(); i++) {
            ColloquiaComponent tc = (ColloquiaComponent)members.elementAt(i);
            if(tc instanceof PersonGroup) {
                if(!v.contains(tc)) v.addElement(tc);
                _getAllPeopleGroups((PersonGroup)tc, v);
            }
        }
    }

    /**
    * Returns true if resource is in Person Group.
    * Searches all Person sub-Groups.
    */
    public static boolean containsPerson(Person person) {
        return containsPerson(person, peopleGroup);
    }

    private static boolean containsPerson(Person person, PersonGroup group) {
        if(person == null || group == null) return false;

        // Check top Group
        if(group.hasMember(person)) return true;

        // Check sub Groups by recursion
        Vector subGroups = group.getPeopleGroups();
        for(int i = 0; i < subGroups.size(); i++) {
            PersonGroup pGroup = (PersonGroup)subGroups.elementAt(i);
            if(containsPerson(person, pGroup)) return true;
        }

        return false;
    }

    /**
    * Returns ALL People who are members of Live Activities and who are Active
    */
    public static Vector getPeopleInLiveActivities() {
        Vector livePeople = new Vector();

        Vector allLiveActivities = getAllLiveActivities();
        for(int i = 0; i < allLiveActivities.size(); i++) {
            Activity A = (Activity)allLiveActivities.elementAt(i);
            Vector people = A.getActivePeople();
            for(int j = 0; j < people.size(); j++) {
                Person person = (Person)people.elementAt(j);
                if(!livePeople.contains(person)) livePeople.addElement(person);
            }
        }

        return livePeople;
    }

    /*
    * Go thru all components looking at submitter field
    * If it matches person's e-mail, update with primary e-mail address
    */
    public static synchronized void updateSubmitter(Person person) {
        String primaryEmail = person.getEmailAddress();

    	Enumeration e = GUIDS.elements();
        while(e.hasMoreElements()) {
            ColloquiaComponent tc = (ColloquiaComponent)e.nextElement();
            if(tc.isMine()) continue;
            String submitter = tc.getSubmitter();
            if(person.isPerson(submitter)) tc.setSubmitter(primaryEmail, false);
        }
    }

    /**
    * Returns a Person by their e-mail, or null if no match
    */
    public static Person getPersonByEmailAddress(String emailAddress) {
        if(emailAddress == null || emailAddress.length() == 0) return null;

        Vector v = getAllPeople();
        for(int i = 0; i < v.size(); i++) {
            Person person = (Person)v.elementAt(i);
            if(person.isPerson(emailAddress)) return person;
        }

        return null;
    }

    /**
    * Returns a Person by taking a matching Person object (like a template)
    * and going thru the emails for a match - it will check the Primary email address
    * first and then the aliases.
    * There is a Danger! - If there is another entity person on the tree who has
    * one of these alia as their primary e-mail addresses
    */
    public static Person getPersonByEmailAddress(Person templatePerson) {
        if(templatePerson == null) return null;

        // Check primary address first
        Person person = getPersonByEmailAddress(templatePerson.getEmailAddress());
        if(person != null) return person;

        // Now check for aliases
        String[] aliases = templatePerson.getAliases();
        if(aliases == null) return null;

        for(int i = 0; i < aliases.length; i++) {
            person = getPersonByEmailAddress(aliases[i]);
        	if(person != null) break;
        }

        return person;
    }

    /**
    * Returns top Resource Group
    */
    public static ResourceGroup getResourceGroup() {
        return resourceGroup;
    }

    /**
    * Returns all Resources (no duplicates)
    */
    public static Vector getAllResources() {
        Vector v = new Vector();
        _getAllResources(resourceGroup, v);
        return v;
    }

    private static void _getAllResources(ResourceGroup group, Vector v) {
        if(group == null) return;

        // Do Resources in this group
        Vector members = group.getMembers();
        for(int i = 0; i < members.size(); i++) {
            ColloquiaComponent tc = (ColloquiaComponent)members.elementAt(i);
            switch(tc.getType()) {
                case ColloquiaComponent.RESOURCE:
                    if(!v.contains(tc)) v.addElement(tc);
                    break;
                case ColloquiaComponent.RESOURCE_GROUP:
                    _getAllResources((ResourceGroup)tc, v);
                    break;
            }
        }
    }

    /*
    * Returns all Resource Groups
    */
    public static Vector getAllResourceGroups() {
        Vector v = new Vector();
        v.addElement(getResourceGroup());
        _getAllResourceGroups(resourceGroup, v);
        return v;
    }

    private static void _getAllResourceGroups(ResourceGroup group, Vector v) {
        if(group == null) return;

        Vector members = group.getMembers();
        for(int i = 0; i < members.size(); i++) {
            ColloquiaComponent tc = (ColloquiaComponent)members.elementAt(i);
            if(tc instanceof ResourceGroup) {
                if(!v.contains(tc)) v.addElement(tc);
                _getAllResourceGroups((ResourceGroup)tc, v);
            }
        }
    }


    /**
    * Returns true if resource is in Resource Group.
    * Searches all Resources sub-Groups.
    */
    public static boolean containsResource(Resource resource) {
        return containsResource(resource, resourceGroup);
    }

    private static boolean containsResource(Resource resource, ResourceGroup group) {
        if(resource == null || group == null) return false;

        // Check top Group
        if(group.hasMember(resource)) return true;

        // Check sub Groups by recursion
        Vector subGroups = group.getResourceGroups();
        for(int i = 0; i < subGroups.size(); i++) {
            ResourceGroup rGroup = (ResourceGroup)subGroups.elementAt(i);
            if(containsResource(resource, rGroup)) return true;
        }

        return false;
    }

    /**
    * Return an instance count of a Resource within Resource Groups
    * But not Activities
    */
    public static int getTopResourceCount(Resource resource) {
        int sum = 0;

        Vector v = getAllResourceGroups();
        for(int i = 0; i < v.size(); i++) {
        	ResourceGroup rg = (ResourceGroup)v.elementAt(i);
            if(rg.hasMember(resource)) sum++;
        }

        return sum;
    }

    /**
    * Return an instance count of a Person within Person Groups
    * But not Activities
    */
    public static int getTopPersonCount(Person person) {
        int sum = 0;

        Vector v = getAllPeopleGroups();
        for(int i = 0; i < v.size(); i++) {
        	PersonGroup pg = (PersonGroup)v.elementAt(i);
            if(pg.hasMember(person)) sum++;
        }

        return sum;
    }

    /**
    * Returns top Activity Group
    */
    public static ActivityGroup getActivityGroup() {
        return activityGroup;
    }

    /**
    * Returns ALL LIVE Activities and sub-Activities
    */
    public static Vector getAllLiveActivities() {
        Vector v = new Vector();
        Vector allActivities = getAllActivities();

        for(int i = 0; i < allActivities.size(); i++) {
            Activity A = (Activity)allActivities.elementAt(i);
            if(A.isLive()) v.addElement(A);
        }

        return v;
    }

    /**
    * Returns all Activities in the DataModel
    * But not those in Templates
    */
    public static Vector getAllActivities() {
        Vector v = new Vector();
        _getAllActivities(activityGroup, v);
        return v;
    }

    private static void _getAllActivities(ColloquiaContainer group, Vector v) {
        Vector members = null;
        if(group instanceof ActivityGroup) members = group.getMembers();
        else if(group instanceof Activity) members = ((Activity)group).getActivities();

        for(int i = 0; i < members.size(); i++) {
            Activity A = (Activity)members.elementAt(i);
            v.addElement(A);
            // sub-As
            _getAllActivities(A, v);
        }
    }

    /*
    * Sets them all hot!
    */
    /*
    public static void setActivitiesHot() {
        Vector acts = getAllActivities();
        for(int i = 0; i < acts.size(); i++) {
            Activity A = (Activity)acts.elementAt(i);
            if(A.isMine() && !A.isHot()) {
                Date A_Date = A.getPropertyDate(ColloquiaComponent.DATE_MODIFIED);
                Vector tcs = A.getMembers();
                for(int j = 0; j < tcs.size(); j++) {
                    ColloquiaComponent tc = (ColloquiaComponent)tcs.elementAt(j);
                    Date tc_Date = tc.getPropertyDate(ColloquiaComponent.DATE_MODIFIED);
                    if(tc_Date.after(A_Date)) {
                        A.setTimeStamp();
                        break;
                    }
                }
            }
        }
    }
    */

    public static Vector getHotActivities() {
    	Vector v = new Vector();
        Vector acts = getAllActivities();
        for(int i = 0; i < acts.size(); i++) {
            Activity A = (Activity)acts.elementAt(i);
            if(A.isMine() && A.isHot()) v.addElement(A);
        }
        return v;
    }

    /**
    * Returns top Templates Group
    */
    public static TemplateGroup getTemplateGroup() {
        return templateGroup;
    }

    /**
    * Returns ALL Templates and sub-Templates
    */
    public static Vector getAllTemplates() {
        Vector v = new Vector();
        _getAllTemplates(templateGroup, v);
        return v;
    }

    private static void _getAllTemplates(ColloquiaContainer group, Vector v) {
        Vector members = null;
        if(group instanceof TemplateGroup) members = group.getMembers();
        else if(group instanceof Activity) members = ((Activity)group).getActivities();

        for(int i = 0; i < members.size(); i++) {
            Activity A = (Activity)members.elementAt(i);
            v.addElement(A);
            // sub-As
            _getAllTemplates(A, v);
        }
    }

    /**
    * Returns all Assignments in Activities (no duplicates)
    * Including those in Templates
    */
    public static Vector getAllAssignments() {
        Vector v = new Vector();

        Vector activities = getAllActivities();
        for(int i = 0; i < activities.size(); i++) {
            Activity A = (Activity)activities.elementAt(i);
            Vector assignments = A.getAssignments();
            for(int j = 0; j < assignments.size(); j++) {
                Assignment ass = (Assignment)assignments.elementAt(j);
                if(!v.contains(ass)) v.addElement(ass);
            }
        }

        Vector templates = getAllTemplates();
        for(int i = 0; i < templates.size(); i++) {
            Activity A = (Activity)templates.elementAt(i);
            Vector assignments = A.getAssignments();
            for(int j = 0; j < assignments.size(); j++) {
                Assignment ass = (Assignment)assignments.elementAt(j);
                if(!v.contains(ass)) v.addElement(ass);
            }
        }

        return v;
    }

    /**
    * Returns the component from a GUID or null if not found
    */
    public static ColloquiaComponent getComponent(String GUID) {
        return (ColloquiaComponent)GUIDS.get(GUID);
    }

    /**
    * Add an entry to the GUID table
    */
    public static synchronized void addGUID(ColloquiaComponent tc) {
        GUIDS.put(tc.getGUID(), tc);
    }

    /**
    * Remove an entry from the GUID table
    */
    public static synchronized void removeGUID(ColloquiaComponent tc) {
        GUIDS.remove(tc.getGUID());
    }

    /**
    * Checks whether GUID exists
    */
    public static boolean containsGUID(String GUID) {
        return GUIDS.containsKey(GUID);
    }

    public static boolean hasMember(ColloquiaComponent tc) {
        return tc == null ? false : containsGUID(tc.getGUID());
    }

    /*
    * Sort a Vector of ColloquiaComponent Members by Property Key - Bubble Sort
    */
    public static synchronized void sortComponents(Vector components, ColloquiaContainer parentGroup, String sortKey, boolean ascending) {
        ColloquiaComponent tc1, tc2;
        String s1, s2;
        boolean hasActivities = false;

        for(int i = 0; i < components.size(); i++) {
            for(int j = 0; j < components.size() - 1; j++) {
                tc1 = (ColloquiaComponent)components.elementAt(j);
                tc2 = (ColloquiaComponent)components.elementAt(j + 1);
                s1 = tc1.getProperty(sortKey);
                s2 = tc2.getProperty(sortKey);
                int comparison = Utils.compare(s1, s2);
                if((ascending && comparison > 0) || (!ascending && comparison < 0)) {
                    components.removeElementAt(j);
                    components.insertElementAt(tc1, j + 1);
                }
                if(tc1 instanceof Activity || tc2 instanceof Activity) hasActivities = true;
            }
        }

        if(hasActivities) sortActivities(components);

        fireComponentOrderChanged(components, parentGroup);
    }

    /*
    * Sort a Vector of Activities - Live - Completed
    * Only works if Live = 1, Completed = 2
    */
    public static synchronized void sortActivities(Vector members) {
        ColloquiaComponent tc1, tc2;
        Activity A1, A2;
        for(int i = 0; i < members.size(); i++) {
            for(int j = 0; j < members.size() - 1; j++) {
                tc1 = (ColloquiaComponent)members.elementAt(j);
                tc2 = (ColloquiaComponent)members.elementAt(j + 1);
                if(tc1 instanceof Activity && tc2 instanceof Activity) {
                    A1 = (Activity)tc1;
                    A2 = (Activity)tc2;
                    if(A1.getState() > A2.getState()) {   // Completed > Live - 2 > 1
                        members.removeElementAt(j);
                        members.insertElementAt(tc1, j + 1);
                    }
                }
            }
        }
    }

    //======================== XML ====================================

    public static synchronized void save() throws XMLWriteException {
        // Write to a temp file first in case of disaster
        String tmp = DataFiler.generateTmpFileName(DataFiler.getTempFolder(true));
        File tmpFile = new File(tmp);

        XMLFileWriter writer = new XMLFileWriter(tmpFile);
        writer.open();
        write2XML(writer);
        writer.close();

        // Uh oh.......
        if(!tmpFile.exists() || tmpFile.length() == 0) {
            System.out.println("Error saving data!");
            return;
        }

        // New file
        String fileName = DataFiler.getDataFileName(true);
        File dataFile = new File(fileName);

        // Rename data to a backup
        if(dataFile.exists()) {
            File bakFile = new File(Utils.getFileName(fileName) + ".bak");
            // Delete previous backup
            if(bakFile.exists()) bakFile.delete();
            // Rename existing file to backup
            dataFile.renameTo(bakFile);
        }

        // Now copy over
        tmpFile.renameTo(dataFile);
    }

    private static void write2XML(XMLWriter writer) throws XMLWriteException {
        // Header line
        writer.writeln("<toomoldata>");
        // Data Type
        writer.writeln("<datatype>MAINDATA</datatype>");
        // Version
        writer.writeln("<version>1.2</version>");

        write2XML(ColloquiaComponent.PERSON, writer);
        write2XML(ColloquiaComponent.PERSON_GROUP, writer);
        write2XML(ColloquiaComponent.RESOURCE, writer);
        write2XML(ColloquiaComponent.RESOURCE_GROUP, writer);
        write2XML(ColloquiaComponent.ASSIGNMENT, writer);
        write2XML(ColloquiaComponent.ACTIVITY, writer);
        write2XML(ColloquiaComponent.TEMPLATE_GROUP, writer);

        // Last line
        writer.writeln("</toomoldata>");
    }

    private static void write2XML(int type, XMLWriter writer) throws XMLWriteException {
        Vector v = null;
        String startBlock = "", endBlock = "";

        switch(type) {
            case ColloquiaComponent.PERSON:
                v = getAllPeople();
                startBlock = "<people>";
                endBlock = "</people>";
                break;
            case ColloquiaComponent.PERSON_GROUP:
                v = getAllPeopleGroups();
                startBlock = "<people_groups>";
                endBlock = "</people_groups>";
                break;
            case ColloquiaComponent.RESOURCE:
                v = getAllResources();
                startBlock = "<resources>";
                endBlock = "</resources>";
                break;
            case ColloquiaComponent.RESOURCE_GROUP:
                v = getAllResourceGroups();
                startBlock = "<resource_groups>";
                endBlock = "</resource_groups>";
                break;
            case ColloquiaComponent.ASSIGNMENT:
                v = getAllAssignments();
                startBlock = "<assignments>";
                endBlock = "</assignments>";
                break;
            case ColloquiaComponent.ACTIVITY:
                v = getAllActivities();
                v.addElement(getActivityGroup());
                startBlock = "<activities>";
                endBlock = "</activities>";
                break;
            case ColloquiaComponent.TEMPLATE_GROUP:
                v = getAllTemplates();
                v.addElement(getTemplateGroup());
                startBlock = "<templates>";
                endBlock = "</templates>";
                break;

            default:
                return;
        }

        // Start Block
        writer.writeln(startBlock);

        for(int i = 0; i < v.size(); i++) {
            ColloquiaComponent tc = (ColloquiaComponent)v.elementAt(i);
            tc.write2XML(writer);
        }

        // End Block
        writer.writeln(endBlock);
    }

    //=============================== XML 2 ====================================

    public static synchronized void save2() throws XMLWriteException {
        // Write to a temp file first in case of disaster
        String tmp = DataFiler.generateTmpFileName(DataFiler.getTempFolder(true));
        File tmpFile = new File(tmp);

        Element root = new Element("colloquia");
        root.setAttribute("data_version", "1.3");

        write2Element(new Element("people"), getAllPeople(), root);
        write2Element(new Element("people_groups"), getAllPeopleGroups(), root);
        write2Element(new Element("resources"), getAllResources(), root);
        write2Element(new Element("resource_groups"), getAllResourceGroups(), root);
        write2Element(new Element("assignments"), getAllAssignments(), root);
        Vector acts = getAllActivities(); acts.addElement(getActivityGroup());
        write2Element(new Element("activities"), acts, root);
        Vector templates = getAllTemplates(); templates.addElement(getTemplateGroup());
        write2Element(new Element("templates"), templates, root);

        try {
	    	Document doc = new Document(root);
        	XMLUtils.write2XMLFile(doc, tmpFile);
        }
        catch(IOException ex) {
            throw new XMLWriteException("Could not save file", ex.getMessage());
        }

        // Uh oh.......
        if(!tmpFile.exists() || tmpFile.length() == 0) {
            System.out.println("Error saving data!");
            return;
        }

        // New file
        String fileName = DataFiler.getDataFileName(true);
        File dataFile = new File(fileName);

        // Rename data to a backup
        if(dataFile.exists()) {
            File bakFile = new File(Utils.getFileName(fileName) + ".bak");
            // Delete previous backup
            if(bakFile.exists()) bakFile.delete();
            // Rename existing file to backup
            dataFile.renameTo(bakFile);
        }

        // Now copy over
        tmpFile.renameTo(dataFile);
    }

    private static void write2Element(Element parent, Vector group, Element root) {
        for(int i = 0; i < group.size(); i++) {
            ColloquiaComponent tc = (ColloquiaComponent)group.elementAt(i);
            tc.write2Element(parent);
        }
        root.addContent(parent);
    }
}
