package net.colloquia.xml;

import java.io.*;

public class XMLFileReader
extends XMLReader
{
	protected File file;
    protected BufferedReader in;
    protected String err = "XMLFileReader Error: could not ";

	public XMLFileReader(File file) {
    	this.file = file;
    }

    public void open() throws XMLReadException {
        try {
	        if(file != null) in = new BufferedReader(new FileReader(file));
        }
        catch(IOException ex) {
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