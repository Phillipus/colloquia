package net.colloquia.comms.index;

import java.io.*;

import net.colloquia.io.*;

public class AssignmentMessageIndex
extends MessageIndex
{
	String activityGUID, personGUID;

    public AssignmentMessageIndex(String activityGUID, String personGUID, boolean doLoad) {
        super();
        this.activityGUID  = activityGUID;
        this.personGUID = personGUID;
        if(doLoad) load();
    }

    protected File getFileName(boolean checkFolder) {
        return new File(DataFiler.getAssignmentMailFolder(activityGUID, checkFolder) + personGUID + ".index");
    }

    protected int getType() {
        return ASSIGNMENT_MESSAGEBOX;
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
