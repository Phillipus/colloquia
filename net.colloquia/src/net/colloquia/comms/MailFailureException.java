package net.colloquia.comms;

public class MailFailureException extends Exception {

    public MailFailureException(String reason, String debugMessage) {
        super(reason);
        if(debugMessage != null) System.out.println(debugMessage);
    }

}