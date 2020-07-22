package net.colloquia.comms;

public class InvalidMessageException extends Exception {

    public InvalidMessageException(String reason, String debugMessage) {
        super(reason);
        if(debugMessage != null) System.out.println(debugMessage);
    }

}
