package net.colloquia.comms.index;

import java.io.*;

import net.colloquia.io.*;

public class PendingboxMessageIndex
extends MessageIndex
{

    public PendingboxMessageIndex() {
        super();
        load();
    }

    protected File getFileName(boolean checkFolder) {
        return new File(DataFiler.getPendingMailFolder(checkFolder) + "pending.index");
    }

    protected int getType() {
        return PENDINGBOX;
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
