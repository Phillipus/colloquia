package net.colloquia.io;

public class ColloquiaFileException extends Exception {

    public ColloquiaFileException(String reason, String debugMessage) {
        super(reason);
        if(debugMessage != null) System.out.println(debugMessage);
    }

}