package net.colloquia.xml;

import java.io.*;

public class XMLStringReader
extends XMLReader
{
	private String xmlString;
    private BufferedReader in;
    private String err = "XMLStringReader Error: could not ";

	public XMLStringReader(String xmlString) {
    	this.xmlString = xmlString;
    }

    public void open() throws XMLReadException {
        try {
            if(xmlString != null) in = new BufferedReader(new StringReader(xmlString));
        }
        catch(Exception ex) {
            throw new XMLReadException(err + "open", null);
        }
    }

    public void close() throws XMLReadException {
        try {
            if(in != null) in.close();
        }
        catch(IOException ex) {
            throw new XMLReadException(err + "close", null);
        }
    }

    public String readLine() throws XMLReadException {
        String line = null;
        try {
            if(in != null) line = in.readLine();
        }
        catch(IOException ex) {
            throw new XMLReadException(err + "readLine", null);
        }
        return line;
    }

}