package net.colloquia.xml;

public class XMLWriteException extends Exception {

    public XMLWriteException(String reason, String debugMessage) {
        super(reason);
        if(debugMessage != null) System.out.println(debugMessage);
    }

}