package net.colloquia.datamodel.entities;

import java.util.*;

import net.colloquia.datamodel.*;
import net.colloquia.xml.*;

/**
* This acts as a filter
*/
public abstract class SentComponent {
    Hashtable properties;
    String startTag, endTag;

    public SentComponent(ColloquiaComponent tc, String startTag, String endTag) {
        this.startTag = startTag;
        this.endTag = endTag;
        properties = (Hashtable)tc.getProperties().clone();
        removeNonSent(tc);
    }

    protected void removeNonSent(ColloquiaComponent tc) {
    	Vector v = tc.getTableFieldValues();
        for(int i = 0; i < v.size(); i++) {
        	FieldValue fv = (FieldValue)v.elementAt(i);
            if(!fv.isSendable) removeProperty(fv.key);
        }
    }

    public void removeProperty(String key) {
        properties.remove(key);
    }

    public void setProperty(String key, String value) {
        properties.put(key, value);
    }

    public void write2XML(XMLWriter writer) throws XMLWriteException {
        writer.writeln(startTag);

        Enumeration e = properties.keys();
        while(e.hasMoreElements()) {
            String tag = (String)e.nextElement();
            String value = (String)properties.get(tag);
            writer.write(new XMLTag(tag, value));
        }

        writer.writeln(endTag);
    }
}
