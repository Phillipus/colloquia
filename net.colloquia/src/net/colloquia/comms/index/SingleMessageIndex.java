package net.colloquia.comms.index;

import java.io.*;

import net.colloquia.io.*;

public class SingleMessageIndex
extends MessageIndex
{
	String activityGUID, personGUID;

    public SingleMessageIndex(String activityGUID, String personGUID, boolean doLoad) {
        super();
        this.activityGUID  = activityGUID;
        this.personGUID = personGUID;
        if(doLoad) load();
    }

    protected File getFileName(boolean checkFolder) {
        return new File(DataFiler.getActivityMailFolder(activityGUID, checkFolder) + personGUID + ".index");
    }

    protected int getType() {
        return SINGLE_MESSAGEBOX;
    }

    protected int getSortKey(int column) {
        switch(column) {
            case 3:
                return SORT_SUBJECT;
            case 4:
                return SORT_DATE_SENT;
            default:
                return 0;
        }
    }
}