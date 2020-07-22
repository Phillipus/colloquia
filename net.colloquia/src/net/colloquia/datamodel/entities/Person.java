package net.colloquia.datamodel.entities;

import java.util.*;

import net.colloquia.datamodel.*;
import net.colloquia.util.*;
import net.colloquia.xml.*;

import org.jdom.*;

public class Person
extends ColloquiaComponent
{
    // Additional property keys
    public static final String EMAIL = "e-mail";
    public static final String FAMILY_NAME = "family_name";
    public static final String GIVEN_NAME = "given_name";
    public static final String ADDRESS = "address";
    public static final String PHONE_NUMBER = "phone_number";
    public static final String PHOTOGRAPH = "photograph";
    public static final String ID = "id";

    public static final String ALIAS = "alias";

    // The Date that this Person last had my details
    public static final String DATE_LAST_SENT_MYDETAILS = "date_last_sent_mydetails";

    public Person(String name, String GUID) {
        super(name, GUID, PERSON);
    }

    public Vector getTableFieldValues() {
    	return table;
    }

    static Vector table = new Vector();
    static {
        table.addElement(new FieldValue(GIVEN_NAME, LanguageManager.getString("10_2"), false, true));
        table.addElement(new FieldValue(FAMILY_NAME, LanguageManager.getString("10_1"), false, true));
        table.addElement(new FieldValue(ADDRESS, LanguageManager.getString("10_3"), false, true));
        table.addElement(new FieldValue(PHONE_NUMBER, LanguageManager.getString("10_4"), false, true));
        table.addElement(new FieldValue(PHOTOGRAPH, LanguageManager.getString("10_5"), true, false));
        table.addElement(new FieldValue(EMAIL, LanguageManager.getString("10_6"), false, true));
        table.addElement(new FieldValue(URL, LanguageManager.getString("10_7"), false, true));
        table.addElement(new FieldValue(LOCAL_FILE, LanguageManager.getString("10_8"), true, false));
        table.addElement(new FieldValue(ID, LanguageManager.getString("10_9"), false, true));
        table.addElement(new FieldValue(SUBMITTER, LanguageManager.getString("10_10"), false, true));
    }

    public String getEmailAddress() {
        return getProperty(EMAIL);
    }

    public void setEmailAddress(String set, boolean update) {
        putProperty(EMAIL, set.trim(), update);
    }

    public String[] getAliases() {
        Vector v = new Vector();

    	Enumeration e = properties.keys();
        while(e.hasMoreElements()) {
        	String key = (String)e.nextElement();
            if(key.startsWith(ALIAS)) {
                String s = (String)properties.get(key);
            	v.addElement(s.trim());
            }
        }

        if(v.isEmpty()) return null;
        else {
            String[] s = new String[v.size()];
            v.copyInto(s);
            return s;
        }
    }

    public boolean isPerson(String emailAddress) {
        String email = emailAddress.trim();

        // Check primary e-mail address first
        if(email.equalsIgnoreCase(getEmailAddress())) return true;

        // No, check in aliases
    	String[] s = getAliases();
        if(s == null) return false;

        for(int i = 0; i < s.length; i++) {
            if(s[i].equalsIgnoreCase(email)) return true;
        }

        return false;
    }

    public boolean isPerson(String[] emailAddress) {
        for(int i = 0; i < emailAddress.length; i++) {
            if(isPerson(emailAddress[i])) return true;
        }
        return false;
    }

    /**
    * Over-ride this so that we can check submitter
    */
    public void updateProperties(ColloquiaComponent tc) {
        if(!(tc instanceof Person)) return;
        super.updateProperties(tc);
	    // Check for changed e-mail address
    	DataModel.updateSubmitter(this);
    }

    public ColloquiaComponent copy() {
        // Make a new one with new GUID
        Person person = new Person("", null);
        // Save GUID
        String newGUID = person.getGUID();
        person.setProperties((Hashtable)getProperties().clone());
        // Change some properties
        person.setGUID(newGUID);
        person.setSubmitter("ME", false);
        person.setPropertyDate(DATE_CREATED, Utils.getNow());
        person.setPropertyDate(DATE_MODIFIED, Utils.getNow());

        return person;
    }

    public static String XMLStartTag = "<person>";
    public static String XMLEndTag = "</person>";

    public void write2XML(XMLWriter writer) throws XMLWriteException {
        writer.writeln(XMLStartTag);
        super.write2XML(writer);
        writer.writeln(XMLEndTag);
    }

    public void write2Element(Element parent) {
    	Element element = new Element("person");
        super.write2Element(element);
        parent.addContent(element);
	}

    public SentComponent getSentComponent() {
        // If there is no e-mail address return null
        if(getEmailAddress().trim().equals("")) return null;
        return new SentPerson(this);
    }
}


