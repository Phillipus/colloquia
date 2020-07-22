package net.colloquia.comms.index;

import java.io.*;

import net.colloquia.io.*;

public class InboxMessageIndex
extends MessageIndex
{

    public InboxMessageIndex() {
        super();
        load();
    }

    protected File getFileName(boolean checkFolder) {
        return new File(DataFiler.getMailFolder(checkFolder) + "inbox.index");
    }

    protected int getType() {
        return INBOX;
    }

    protected int getSortKey(int column) {
        switch(column) {
            case 2:
                return SORT_FROM;
            case 3:
                return SORT_ACTIVITY;
            case 4:
                return SORT_SUBJECT;
            case 5:
                return SORT_DATE_SENT;
            default:
                return 0;
        }
    }
}
