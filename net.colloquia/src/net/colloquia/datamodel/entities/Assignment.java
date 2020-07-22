package net.colloquia.datamodel.entities;

import java.util.*;

import net.colloquia.datamodel.*;
import net.colloquia.util.*;
import net.colloquia.xml.*;

import org.jdom.*;

public class Assignment
extends ColloquiaComponent
{

    // Additional property keys
    public static final String ASSESSABLE = "assessable";
    public static final String TITLE = "title";
    public static final String TYPE = "type";
    public static final String TUTOR = "tutor";
    public static final String DUE_DATE = "due_date";
    public static final String DATE_SET = "date_set";

    /**
    * Constructor
    */
    public Assignment(String name, String GUID) {
        super(name, GUID, ColloquiaComponent.ASSIGNMENT);
        // SetAssessable
        setAssessable(true, false);
    }

    static Vector table = new Vector();
    static {
        table.addElement(new FieldValue(TITLE, LanguageManager.getString("9_1"), false, true));
        table.addElement(new FieldValue(URL, LanguageManager.getString("9_2"), false, true));
        table.addElement(new FieldValue(LOCAL_FILE, LanguageManager.getString("9_9"), true, false));
        table.addElement(new FieldValue(TYPE, LanguageManager.getString("9_3"), false, true));
        table.addElement(new FieldValue(TUTOR, LanguageManager.getString("9_4"), false, true));
        table.addElement(new FieldValue(DATE_SET, LanguageManager.getString("9_5"), false, true));
        table.addElement(new FieldValue(DUE_DATE, LanguageManager.getString("9_6"), false, true));
        table.addElement(new FieldValue(ASSESSABLE, LanguageManager.getString("9_7"), false, true));
        table.addElement(new FieldValue(PHYSICAL_LOCATION, LanguageManager.getString("9_8"), false, true));
        table.addElement(new FieldValue(SUBMITTER, LanguageManager.getString("9_10"), false, true));
    }

    public Vector getTableFieldValues() {
    	return table;
    }

    public void setAssessable(boolean value, boolean update) {
        putProperty(ASSESSABLE, value ? "yes" : "no", update);
    }

    public boolean isAssessable() {
        String val = getProperty(ASSESSABLE);
        return val.toLowerCase().equals("yes");
    }

    public ColloquiaComponent copy() {
        // Make a new one with new GUID
        Assignment ass = new Assignment("", null);
        // Save GUID
        String newGUID = ass.getGUID();
        ass.setProperties((Hashtable)getProperties().clone());
        // Change some properties
        ass.setGUID(newGUID);
        ass.setSubmitter("ME", false);
        ass.setPropertyDate(DATE_CREATED, Utils.getNow());
        ass.setPropertyDate(DATE_MODIFIED, Utils.getNow());

        return ass;
    }

    public static String XMLStartTag = "<assignment>";
    public static String XMLEndTag = "</assignment>";

    public void write2XML(XMLWriter writer) throws XMLWriteException {
        writer.writeln(XMLStartTag);
        super.write2XML(writer);
        writer.writeln(XMLEndTag);
    }

    public void write2Element(Element parent) {
    	Element element = new Element("assignment");
        super.write2Element(element);
        parent.addContent(element);
	}

    public SentComponent getSentComponent() {
        return new SentAssignment(this);
    }

}


