package net.colloquia.xml;

import org.jdom.*;

public interface XML {
    // System independant line separator carriage return
    public static String CR = System.getProperty("line.separator");

    void write2XML(XMLWriter writer) throws XMLWriteException;

    void write2Element(Element parent);

    // Parse the XML file and add to your properties
    void unMarshallXML(XMLReader reader) throws XMLReadException;
}
