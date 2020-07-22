package net.colloquia.views.summary;

import net.colloquia.comms.index.*;
import net.colloquia.datamodel.*;
import net.colloquia.datamodel.entities.*;
import net.colloquia.util.*;
import net.colloquia.views.*;

/**
 * A Table view of all Activities and sub-Activities in an Activity
 */
public class Activity_SummaryView
extends SummaryView
{
    private ComponentTableRenderer renderer;

    public Activity_SummaryView() {
        super();

        // Set the default cell renderer for any type object
        renderer = new ComponentTableRenderer(Activity.class);
        table.setDefaultRenderer(Object.class, renderer);
    }

    protected void init() {
        columnNames = new String[] { LanguageManager.getString("0"),
                                     LanguageManager.getString("8_7"),
                                     LanguageManager.getString("8_3"),
                                     LanguageManager.getString("MESSAGES")
        };

        propertyKeyNames = new String[] {
        	Activity.NAME,
            Activity.SUBMITTER,
            Activity.START_DATE,
            ""
        };

        model = new ActivitySummaryModel();
    }

    public void setGroup(ColloquiaContainer group) {
        super.setGroup(group);
        // Top Level folder
        if(group instanceof ActivityGroup) members = group.getMembers();
        else if(group instanceof Activity) members = ((Activity)group).getActivities();
        else return;
        model.fireTableDataChanged();
    }


    private class ActivitySummaryModel extends DefaultSummaryModel {
        public Object getValueAt(int rowIndex, int columnIndex) {
            Activity A = (Activity)members.elementAt(rowIndex);

            switch(columnIndex) {
            	case 0:
                	return A.getName();

                case 1:
                    String text = A.getSubmitter();
                    if(text.equalsIgnoreCase("ME")) text = LanguageManager.getString("ME");
                    else {
                        Person person = DataModel.getPersonByEmailAddress(text);
                        if(person != null) text = person.getName();
                    }
                    return text;

                case 2:
                	return A.getProperty(Activity.START_DATE);

                case 3:
                    GroupMessageIndex index = new GroupMessageIndex(A.getGUID(), false);
                    int numMessages = MessageIndex.getTotalMessageCount(index);
                    if(numMessages == -1) return "?";
                    else return String.valueOf(numMessages);

                default:
                	return "";
            }
        }
    }
}

