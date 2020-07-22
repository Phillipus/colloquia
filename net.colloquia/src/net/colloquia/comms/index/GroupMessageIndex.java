package net.colloquia.comms.index;

import java.io.*;

import net.colloquia.io.*;

public class GroupMessageIndex
extends MessageIndex
{
    String activityGUID;

    public GroupMessageIndex(String activityGUID, boolean doLoad) {
        this.activityGUID = activityGUID;
        if(doLoad) load();
    }

    protected File getFileName(boolean checkFolder) {
        return new File(DataFiler.getActivityMailFolder(activityGUID, checkFolder) + "group.index");
    }

    protected int getType() {
        return GROUP_MESSAGEBOX;
    }

    protected int getSortKey(int column) {
        switch(column) {
            case 3:
                return SORT_FROM;
            case 4:
                return SORT_SUBJECT;
            case 5:
                return SORT_DATE_SENT;
            default:
                return 0;
        }
    }
}
