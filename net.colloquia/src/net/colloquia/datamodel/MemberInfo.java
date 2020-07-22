package net.colloquia.datamodel;

import java.util.*;

import net.colloquia.datamodel.entities.*;
import net.colloquia.xml.*;

import org.jdom.*;

/**
* A class that links a ColloquiaComponent to a ColloquiaContainer parent
* This allows us to associate contextual information for that Component in a parent Group
* Examples - whether a component can be removed from a Group.
* 		   - a student's Assignment grade.
* Each ColloquiaContainer has a Hashtable of MemberInfo classes
* If a ColloquiaComponent is inserted into a ColloquiaContainer, the ColloquiaComponent
* has a new MemberInfo instance associated with it in this table.
*/
public class MemberInfo
implements XML
{
    private ColloquiaComponent tc;
    private ColloquiaContainer parent;

    public static String XMLStartTag = "<member>";
    public static String XMLEndTag = "</member>";

    private Hashtable properties = new Hashtable();

    // Member can be removed
    public static final String FREE = "free";
    // Member has been notionally removed but is still there
    public static final String REMOVED = "removed";

    public MemberInfo(ColloquiaComponent tc, ColloquiaContainer parent) {
        this.tc = tc;
        this.parent = parent;
        setGUID(tc.getGUID());
    }

    public MemberInfo(Hashtable properties) {
        this.properties = properties;
    }

    public Hashtable getProperties() {
        return properties;
    }

    public void setProperties(Hashtable properties) {
        this.properties = properties;
    }

    public void putProperty(String key, String value) {
        if(key == null || value == null) return;
        properties.put(key.toLowerCase(), value);
    }

    public String getProperty(String key) {
        String value = (String)properties.get(key.toLowerCase());
        return value == null ? "" : value;
    }

    public void removeProperty(String key) {
        if(key != null) properties.remove(key.toLowerCase());
    }

    public void setPropertyDate(String propertyName, Date date) {
        putProperty(propertyName, date == null ? "" : String.valueOf(date.getTime()));
    }

    public Date getPropertyDate(String propertyName) {
        String val = getProperty(propertyName);
        if(val.equals("")) return null;
        else return new Date(Long.parseLong(val));
    }

    public ColloquiaComponent getComponent() {
        return tc;
    }

    public ColloquiaContainer getParent() {
        return parent;
    }

    //==================== CONVENIENCE METHODS ==============================

    // GUID
    private void setGUID(String id) {
       putProperty(ColloquiaComponent.GUID, id);
    }

    public String getGUID() {
        return getProperty(ColloquiaComponent.GUID);
    }

    /**
    * Writes this MemberInfo in XML Format as a String of XML properties
    */
    public void write2XML(XMLWriter writer) throws XMLWriteException {
        writer.writeln(XMLStartTag);

        // Properties
        Hashtable props = getProperties();
        Enumeration e = props.keys();
        while(e.hasMoreElements()) {
            String tag = (String)e.nextElement();
            String value = (String)props.get(tag);
            writer.write(new XMLTag(tag, value));
        }

        writer.writeln(XMLEndTag);
    }

    public void write2Element(Element parent) {
    	Element element = new Element("member");

        // Properties
        Hashtable props = getProperties();
        Enumeration e = props.keys();
        while(e.hasMoreElements()) {
            String tag = (String)e.nextElement();
            String value = (String)props.get(tag);
            // Do we really want to save these as attributes???
            if(tag.equalsIgnoreCase(ColloquiaComponent.GUID) || tag.equalsIgnoreCase(FREE)) {
            	element.setAttribute(tag, value);
            	continue;
            }
            Element el = new Element(tag);
            el.setText(value);
            element.addContent(el);
        }

        parent.addContent(element);
	}

    public void unMarshallXML(XMLReader reader) throws XMLReadException {
        String line;
        XMLTag xmlTag;
        while((line = reader.readLine()) != null) {
            xmlTag = XMLTag.getXMLTag(line);
            if(xmlTag != null) putProperty(xmlTag.tag, xmlTag.value);
        }
    }
}
