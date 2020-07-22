package net.colloquia.comms.index;

import java.io.*;

import net.colloquia.io.*;

public class OutboxMessageIndex
extends MessageIndex
{

    public OutboxMessageIndex() {
        super();
        load();
    }

    protected File getFileName(boolean checkFolder) {
        return new File(DataFiler.getMailFolder(checkFolder) + "outbox.index");
    }

    protected int getType() {
        return OUTBOX;
    }

    protected int getSortKey(int column) {
        switch(column) {
            case 2:
                return SORT_TO;
            case 3:
                return SORT_ACTIVITY;
            case 4:
                return SORT_SUBJECT;
            default:
                return 0;
        }
    }
}