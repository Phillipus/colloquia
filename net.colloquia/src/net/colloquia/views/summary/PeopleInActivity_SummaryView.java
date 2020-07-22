package net.colloquia.views.summary;

import net.colloquia.comms.index.*;
import net.colloquia.datamodel.entities.*;
import net.colloquia.util.*;
import net.colloquia.views.*;

/**
 * A Table view of all People in an Activity
 */
public class PeopleInActivity_SummaryView
extends SummaryView
{
    Activity A;
    ActivityPeopleTableRenderer renderer;

    public PeopleInActivity_SummaryView() {
        renderer = new ActivityPeopleTableRenderer(A, members);
        table.setDefaultRenderer(Object.class, renderer);
    }

    protected void init() {
        columnNames = new String[] {
            LanguageManager.getString("NAME"),
            LanguageManager.getString("10_1"),
            LanguageManager.getString("10_2"),
            LanguageManager.getString("MESSAGES")
        };

        propertyKeyNames = new String[] {
        	Person.NAME,
            Person.FAMILY_NAME,
            Person.GIVEN_NAME,
            ""
        };

        model = new PersonSummaryModel();
    }

    public void setGroup(ColloquiaContainer group) {
        super.setGroup(group);
        A = (Activity)group;
        members = A.getAllPeople();
        renderer.setGroup(A, members);
        model.fireTableDataChanged();
    }

    static String na = LanguageManager.getString("12_7");
    static String none = LanguageManager.getString("12_8");


    protected class PersonSummaryModel extends DefaultSummaryModel {
        // getValueAt
        public Object getValueAt(int rowIndex, int columnIndex) {
            Person person = (Person)members.elementAt(rowIndex);

            switch(columnIndex) {
                // Name
                case 0:
                    return person.getName();
                // Family Name
                case 1:
                    return person.getProperty(Person.FAMILY_NAME);
                // Given Name
                case 2:
                    return person.getProperty(Person.GIVEN_NAME);
                // Number of Messages
                case 3:
                    SingleMessageIndex index = new SingleMessageIndex(A.getGUID(), person.getGUID(), false);
                    int numMessages = MessageIndex.getTotalMessageCount(index);
                    if(numMessages == -1) return "?";
                    else return String.valueOf(numMessages);

                default:
                    return "";
            }
        }
    }
}


