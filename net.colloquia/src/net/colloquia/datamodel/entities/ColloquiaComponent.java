package net.colloquia.datamodel.entities;

import java.util.*;

import net.colloquia.datamodel.*;
import net.colloquia.prefs.*;
import net.colloquia.util.*;
import net.colloquia.xml.*;

import org.jdom.*;

/**
 * This is the ancestor of all Colloquia entities
 */
public abstract class ColloquiaComponent
implements XML, PropertyTableModel
{
    // Entity types
    public static final int PERSON = 0;
    public static final int RESOURCE = 1;
    public static final int ASSIGNMENT = 2;
    public static final int ACTIVITY = 3;
    public static final int GROUP = 4;
    public static final int PERSON_GROUP = 5;
    public static final int RESOURCE_GROUP = 6;
    public static final int ACTIVITY_GROUP = 7;
    public static final int TEMPLATE_GROUP = 8;

    // This is for internal use, it's not saved
    private int entityType;
    private String guid;
    // For convenience / debugging
    public String name;

    // Instances of Groups that this component is a member of
    protected Vector memberGroups = new Vector();

    // Common Keys to main properties
    public static final String NAME = "name";
    public static final String GUID = "guid";
    public static final String DATE_CREATED = "date_created";
    public static final String DATE_MODIFIED = "date_modified";
    public static final String SUBMITTER = "submitter";
    public static final String LOCAL_FILE = "local_file";
    public static final String URL = "url";
    public static final String PHYSICAL_LOCATION = "physical_location";
    public static final String EXTERNAL_BROWSER = "external_browser";

    /**
    * Constructor for new Component with an existing GUID
    * We don't always want to generate a GUID - opening, copying already have them
    * If GUID is null, a new GUID will be generated
    */
    protected ColloquiaComponent(String name, String GUID, int entityType) {
        initComponent(name, entityType);
        if(GUID == null) setGUID(generateGUID());
        else setGUID(GUID);
    }

    /**
    * Init for a new Component
    */
    private void initComponent(String name, int entityType) {
        setName(name, false);
        this.entityType = entityType;
        setPropertyDate(DATE_CREATED, Utils.getNow());
        setPropertyDate(DATE_MODIFIED, getPropertyDate(DATE_CREATED));
        setSubmitter("ME", false);
    }

    // Properties
    protected Hashtable properties = new Hashtable();
    public Hashtable getProperties() { return properties; }  // getTableData
    public void setProperties(Hashtable set) { properties = set; } // setTableData

    /**
     * Put a property in the property table
     * @param key
     * @param value
     * @param update If true, reset this Resource's timestamp to now
     */
    public void putProperty(String key, String value, boolean update) { // setTableElement
        if(key == null || value == null) return;
        if(key.equals("")) return;

        key = key.toLowerCase();

        // 'Me' - for backward compat
        if(value.equalsIgnoreCase("me")) value = value.toUpperCase();

        // For debugging
        if(key.equals(NAME)) name = value;
        if(key.equals(GUID)) guid = value;

        properties.put(key, value);

        //* update means set Date Modified
        if(update) setTimeStamp(key);
    }

    public String getProperty(String key) {
        String value = (String)properties.get(key.toLowerCase());
        return value == null ? "" : value;
    }

    public void removeProperty(String key, boolean update) {
    	properties.remove(key.toLowerCase());
        //* update means set Date Modified
        if(update) setTimeStamp(key);
    }

    // =========================== TABLE STUFF ===============================


    public int getTableRowCount() {
    	return getTableFieldValues().size();
    }

    public String getTableRowName(int row) {
        if(row >= getTableFieldValues().size()) return "";
    	FieldValue fv = (FieldValue)getTableFieldValues().elementAt(row);
        return fv.friendlyName;
    }

    public String getTableRowValue(int row) {
        String key = getTableRowKey(row);
        return getProperty(key);
    }

    public String getTableRowKey(int row) {
        if(row >= getTableFieldValues().size()) return "";
    	FieldValue fv = (FieldValue)getTableFieldValues().elementAt(row);
        return fv.key;
    }

    public FieldValue getTableFieldValue(int row) {
        if(row >= getTableFieldValues().size()) return null;
    	return (FieldValue)getTableFieldValues().elementAt(row);
    }


    /**
    * Factory method for creating a component
    */
    public static ColloquiaComponent createComponent(String name, String GUID, int type) {
        switch(type) {
            // PERSON
            case PERSON:
                return new Person(name, GUID);
            // RESOURCE
            case RESOURCE:
                return new Resource(name, GUID);
            // RESOURCE GROUP
            case RESOURCE_GROUP:
                return new ResourceGroup(name, GUID);
            // ACTIVITY
            case ACTIVITY:
                return new Activity(name, GUID);
            // ASSIGNMENT
            case ASSIGNMENT:
                return new Assignment(name, GUID);
            // GROUP
            case GROUP:
                return new Group(name, GUID);
            // Activity GROUP
            case ACTIVITY_GROUP:
                return new ActivityGroup(name, GUID);
            // PERSON GROUP
            case PERSON_GROUP:
                return new PersonGroup(name, GUID);
            default:
                return null;
        }
    }

    //========================== CONVENIENCE ==================================

    public int getType() {
        return entityType;
    }

    public void setName(String name, boolean update) {
        putProperty(NAME, name, update);
    }

    public String getName() {
        return getProperty(NAME);
    }

    public void rename(String newName) {
        if(newName != null) {
            // Save old name
            String oldName = getName();

            // Trim spaces and check for empty string
            newName = newName.trim();
            if(newName.length() == 0) return;

            // Same as before?
            if(oldName.equals(newName)) return;

            // Set to this new name
            setName(newName, true);
        }
    }

    public void setGUID(String id) {
        putProperty(GUID, id, false);
    }

    public String getGUID() {
        return getProperty(GUID);
    }

    public void setTimeStamp(String key) {
        if(key.equalsIgnoreCase(SUBMITTER)) return;
        else if(key.equalsIgnoreCase(LOCAL_FILE)) return;
        else setTimeStamp();
    }

    public void setTimeStamp() {
        setPropertyDate(DATE_MODIFIED, Utils.getNow());
    }

    public void setPropertyDate(String propertyName, Date date) {
        putProperty(propertyName, String.valueOf(date.getTime()), false);
    }

    public Date getPropertyDate(String propertyName) {
        String val = getProperty(propertyName);
        if(val.equals("")) return null;
        else return new Date(Long.parseLong(val));
    }

    public void setURL(String set, boolean update) {
        putProperty(URL, set, update);
    }

    public String getURL() {
        return getProperty(URL);
    }

    public String getLocalFile() {
        return getProperty(LOCAL_FILE);
    }

    public void setLocalFile(String set, boolean update) {
        putProperty(LOCAL_FILE, set, update);
    }

    public String getSubmitter() {
        return getProperty(SUBMITTER);
    }

    public void setSubmitter(String set, boolean update) {
        if(set.equalsIgnoreCase("ME")) set = set.toUpperCase();
        putProperty(SUBMITTER, set, update);
    }

    /** Returns true if this is mine */
    public boolean isMine() {
        return getSubmitter().equalsIgnoreCase("ME");
    }

    /**
    * Returns true if person e-mail matches this submitter's e-mail
    */
    public boolean isSubmitter(Person person) {
        if(person == null) return false;
        return isSubmitter(person.getEmailAddress());
    }

    /**
    * Returns true if e-mail matches this submitter's e-mail
    */
    public boolean isSubmitter(String eMail) {
        if(eMail == null) return false;
        String submitter = getSubmitter();
        return submitter.equalsIgnoreCase(eMail);
    }

    public void setExternalBrowser(boolean value, boolean update) {
        if(value) putProperty(EXTERNAL_BROWSER, "true", update);
        else removeProperty(EXTERNAL_BROWSER, update);
    }

    public boolean isExternalBrowser() {
        String val = getProperty(EXTERNAL_BROWSER);
        return val.toLowerCase().equals("true");
    }

    public abstract ColloquiaComponent copy();
	public abstract SentComponent getSentComponent();

    /**
    * Update this with the values of tc's properties
    */
    public void updateProperties(ColloquiaComponent tc) {
        Hashtable props = tc.getProperties();

        Enumeration e = props.keys();
        while(e.hasMoreElements()) {
            String key = (String)e.nextElement();
            String value = (String)props.get(key);
            if(!key.equalsIgnoreCase(GUID)) putProperty(key, value, false);
        }
    }

    public Vector getMemberGroups() {
        return memberGroups;
    }

    public void addMemberGroup(ColloquiaContainer parentGroup) {
        // New one - so add GUID to central bank
        if(getInstanceCount() == 0) DataModel.addGUID(this);
        if(!memberGroups.contains(parentGroup)) memberGroups.addElement(parentGroup);
    }

    public void removeMemberGroup(ColloquiaContainer parentGroup) {
        memberGroups.removeElement(parentGroup);
        // Gone!
        if(getInstanceCount() == 0) DataModel.removeGUID(this);
    }

    public int getInstanceCount() {
        return memberGroups.size();
    }

    /**
    * Returns a Vector of all Activities that this component is a member of
    */
    public Vector getActivities() {
        Vector v = new Vector();
        for(int i = 0; i < memberGroups.size(); i++) {
            ColloquiaContainer group = (ColloquiaContainer)memberGroups.elementAt(i);
            if(group instanceof Activity) v.addElement(group);
        }
        return v;
    }

    // Over-ridden so tree displays object name
    public String toString() { return getName(); }

    /**
    * Write this Component in XML Format as a String of XML properties
    */
    public void write2XML(XMLWriter writer) throws XMLWriteException {
        // Properties
        Hashtable props = getProperties();
        Enumeration e = props.keys();
        while(e.hasMoreElements()) {
            String tag = (String)e.nextElement();
            String value = (String)props.get(tag);
            writer.write(new XMLTag(tag, value));
        }
    }

    public void write2Element(Element parent) {
        // Properties
        Hashtable props = getProperties();
        Enumeration e = props.keys();
        while(e.hasMoreElements()) {
            String tag = (String)e.nextElement();
            String value = (String)props.get(tag);
            // Do we really want to save these as attributes???
            if(tag.equalsIgnoreCase(GUID) || tag.equalsIgnoreCase(NAME)) {
            	parent.setAttribute(tag, value);
            	continue;
            }
            Element element = new Element(tag);
            element.setText(value);
            parent.addContent(element);
        }
	}

    /**
    * XML implementation
    */
    public void unMarshallXML(XMLReader reader) throws XMLReadException {
        String line;
        XMLTag xmlTag;
        while((line = reader.readLine()) != null) {
            xmlTag = XMLTag.getXMLTag(line);
            if(xmlTag != null) putProperty(xmlTag.tag, xmlTag.value, false);
        }
    }

    /**
    * Generates a pseudo-random key ID for a ColloquiaComponent
    */
    public static String generateGUID() {
        int num1, num2;
        String guid, hash;
        Random rand;

        // Get hashCode from user's e-mail address
        UserPrefs prefs = UserPrefs.getUserPrefs();
        String myEmailAddress = prefs.getProperty(UserPrefs.EMAIL_ADDRESS);
        if(myEmailAddress.length() != 0) {
            num1 = myEmailAddress.hashCode();
        }
        // If no hashCode, generate number
        else {
            rand = new Random();
            num1 = rand.nextInt();
        }

        if(num1 < 0) num1 *= -1;  // remove sign

        hash = String.valueOf(num1 + "-");

        // Loop while generated key already exists
        do {
            try { Thread.sleep(25); } catch(InterruptedException ex) {}
            rand = new Random();
            num2 = rand.nextInt();
            if(num2 < 0) num2 *= -1;
            guid = hash + String.valueOf(num2);
        } while(DataModel.containsGUID(guid));

        return guid;
    }

    /**
    * Copy the text files associated with this to newCopy
    * Over-ride please!
    */
    public void copyTextFiles(ColloquiaComponent newCopy) {

    }
}

