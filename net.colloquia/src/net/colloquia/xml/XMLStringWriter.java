package net.colloquia.xml;

import net.colloquia.*;

public class XMLStringWriter
extends XMLWriter
{
    private StringBuffer sb;
    private String err = "XMLStringWriter Error: could not ";

	public XMLStringWriter(StringBuffer sb) {
    	this.sb = sb;
    }

    public void open() {
        
    }
    
    public void close() {
        
    }

    public void write(String line) {
        if(sb != null && line != null) {
            sb.append(indent(line));
        }
    }

    public void writeln(String line) {
        if(sb != null && line != null) {
        	sb.append(indent(line));
        	sb.append(ColloquiaConstants.CR);
        }
    }

    public void write(XMLTag xmlTag) {
        write(xmlTag.marshall2XML());
    }
}
