package net.colloquia.views.summary;

import net.colloquia.datamodel.entities.*;
import net.colloquia.util.*;

/**
 * A Table view of all Templates in a Group
 */
public class Template_SummaryView
extends SummaryView
{
    public Template_SummaryView() {
        super();
    }

    protected void init() {
        columnNames = new String[] {
            LanguageManager.getString("NAME")
        };
        propertyKeyNames = new String[] { Resource.NAME };
    }

    public void setGroup(ColloquiaContainer group) {
        super.setGroup(group);
        members = group.getMembers();
        model.fireTableDataChanged();
    }
}
