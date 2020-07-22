package net.colloquia.xml;

public abstract class XMLReader {

    public abstract void open() throws XMLReadException;
    public abstract void close() throws XMLReadException;
    public abstract String readLine() throws XMLReadException;

}
