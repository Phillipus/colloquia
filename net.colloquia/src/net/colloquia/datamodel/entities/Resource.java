package net.colloquia.datamodel.entities;

import java.util.*;

import net.colloquia.datamodel.*;
import net.colloquia.util.*;
import net.colloquia.xml.*;

import org.jdom.*;

public class Resource
extends ColloquiaComponent
{

    // Additional property keys
    public static final String TITLE = "title";
    public static final String TYPE = "type";
    public static final String CATEGORY = "category";
    public static final String ISSUES = "issues";
    public static final String AUTHOR = "author";
    public static final String DATE = "date";
    public static final String SOURCE = "source";

    // Constructor
    public Resource(String name, String GUID) {
        super(name, GUID, ColloquiaComponent.RESOURCE);
    }

    // =========================== TABLE STUFF ===============================

    static Vector table = new Vector();
    static {
        table.addElement(new FieldValue(TITLE, LanguageManager.getString("11_1"), false, true));
        table.addElement(new FieldValue(URL, LanguageManager.getString("11_2"), false, true));
        table.addElement(new FieldValue(LOCAL_FILE, LanguageManager.getString("11_10"), true, false));
        table.addElement(new FieldValue(PHYSICAL_LOCATION, LanguageManager.getString("11_9"), false, true));
        table.addElement(new FieldValue(TYPE, LanguageManager.getString("11_3"), false, true));
        table.addElement(new FieldValue(CATEGORY, LanguageManager.getString("11_4"), false, true));
        table.addElement(new FieldValue(ISSUES, LanguageManager.getString("11_5"), false, true));
        table.addElement(new FieldValue(AUTHOR, LanguageManager.getString("11_6"), false, true));
        table.addElement(new FieldValue(DATE, LanguageManager.getString("11_7"), false, true));
        table.addElement(new FieldValue(SOURCE, LanguageManager.getString("11_8"), false, true));
        table.addElement(new FieldValue(SUBMITTER, LanguageManager.getString("11_11"), false, true));
    }

    public Vector getTableFieldValues() {
    	return table;
    }

    public ColloquiaComponent copy() {
        // Make a new one with new GUID
        Resource resource = new Resource("", null);
        // Save GUID
        String newGUID = resource.getGUID();
        resource.setProperties((Hashtable)getProperties().clone());
        // Change some properties
        resource.setGUID(newGUID);
        resource.setSubmitter("ME", false);
        resource.setPropertyDate(DATE_CREATED, Utils.getNow());
        resource.setPropertyDate(DATE_MODIFIED, Utils.getNow());

        return resource;
    }

    public static String XMLStartTag = "<resource>";
    public static String XMLEndTag = "</resource>";

    public void write2XML(XMLWriter writer) throws XMLWriteException {
        writer.writeln(XMLStartTag);
        super.write2XML(writer);
        writer.writeln(XMLEndTag);
    }

    public void write2Element(Element parent) {
    	Element element = new Element("resource");
        super.write2Element(element);
        parent.addContent(element);
	}

    public SentComponent getSentComponent() {
        return new SentResource(this);
    }
}

