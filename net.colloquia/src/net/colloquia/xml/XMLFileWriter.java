package net.colloquia.xml;

import java.io.*;

public class XMLFileWriter
extends XMLWriter
{
	private File file;
    private BufferedWriter out;
    private String err = "XMLFileWriter Error: could not ";

	public XMLFileWriter(File file) {
    	this.file = file;
    }

    public void open() throws XMLWriteException {
        try {
	        if(file != null) out = new BufferedWriter(new FileWriter(file));
        }
        catch(IOException ex) {
            throw new XMLWriteException(err + "open " + file.getPath(), null);
        }
    }

    public void close() throws XMLWriteException {
        try {
            if(out != null) {
                out.flush();
                out.close();
            }
        }
        catch(IOException ex) {
            throw new XMLWriteException(err + "close " + file.getPath(), null);
        }
    }

    public void write(String line) throws XMLWriteException  {
    	try {
            if(out != null && line != null) out.write(indent(line));
        }
        catch(IOException ex) {
            throw new XMLWriteException(err + "write " + file.getPath(), null);
        }
    }

    public void writeln(String line) throws XMLWriteException  {
        try {
            if(out != null && line != null) {
                out.write(indent(line));
                out.newLine();
            }
        }
        catch(IOException ex) {
            throw new XMLWriteException(err + "writeln " + file.getPath(), null);
        }
    }

    public void write(XMLTag xmlTag) throws XMLWriteException {
        write(xmlTag.marshall2XML());
    }

}
