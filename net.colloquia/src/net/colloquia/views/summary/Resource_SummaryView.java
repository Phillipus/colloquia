package net.colloquia.views.summary;

import net.colloquia.datamodel.entities.*;
import net.colloquia.util.*;
import net.colloquia.views.*;

/**
 * A Table view of all Resources in a Group
 */
public class Resource_SummaryView
extends SummaryView
{
    ComponentTableRenderer renderer;

    public Resource_SummaryView() {
        renderer = new ComponentTableRenderer(Resource.class);
        table.setDefaultRenderer(Object.class, renderer);
    }

    protected void init() {
        columnNames = new String[] {
            LanguageManager.getString("NAME"),
            LanguageManager.getString("11_1"),
            LanguageManager.getString("11_2")
        };
        propertyKeyNames = new String[] { Resource.NAME, Resource.TITLE, Resource.URL };
    }

    public void setGroup(ColloquiaContainer group) {
        super.setGroup(group);
        if(group instanceof Activity) members = ((Activity)group).getResources();
        else if(group instanceof ResourceGroup) members = ((ResourceGroup)group).getResources();
        model.fireTableDataChanged();
    }

}
