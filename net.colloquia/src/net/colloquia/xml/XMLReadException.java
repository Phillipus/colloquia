package net.colloquia.xml;

public class XMLReadException extends Exception {

    public XMLReadException(String reason, String debugMessage) {
        super(reason);
        if(debugMessage != null) System.out.println(debugMessage);
    }

}