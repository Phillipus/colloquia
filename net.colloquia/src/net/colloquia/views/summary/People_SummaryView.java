package net.colloquia.views.summary;

import net.colloquia.datamodel.entities.*;
import net.colloquia.util.*;
import net.colloquia.views.*;

/**
 * A Table view of all People in a PeopleGroup
 */
public class People_SummaryView extends SummaryView {
    ComponentTableRenderer renderer;

    public People_SummaryView() {
        renderer = new ComponentTableRenderer(Person.class);
        table.setDefaultRenderer(Object.class, renderer);
    }

    protected void init() {
        columnNames = new String[] {
            LanguageManager.getString("NAME"),
            LanguageManager.getString("10_1"),
            LanguageManager.getString("10_2"),
            LanguageManager.getString("10_6")
        };

        propertyKeyNames = new String[] {
        	Person.NAME,
            Person.FAMILY_NAME,
            Person.GIVEN_NAME,
            Person.EMAIL
        };
    }

    public void setGroup(ColloquiaContainer group) {
        super.setGroup(group);
        members = ((PersonGroup)group).getPeople();
        //members = group.getMembers();
        model.fireTableDataChanged();
    }
}

