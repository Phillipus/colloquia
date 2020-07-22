package net.colloquia.util;

public class PrintCancelledException extends Exception {

    public PrintCancelledException(String reason, String debugMessage) {
        super(reason);
        if(debugMessage != null) System.out.println(debugMessage);
    }

}