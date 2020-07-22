package net.colloquia.views.entities;

import java.awt.*;

import net.colloquia.*;
import net.colloquia.datamodel.entities.*;
import net.colloquia.views.summary.*;

/**
* The Top PeopleGroup
*/
public class PersonGroupView
extends ColloquiaView
{
    private People_SummaryView summaryView;

    private static final PersonGroupView instance = new PersonGroupView();

    private PersonGroupView() {
        summaryView = new People_SummaryView();
        setLayout(new BorderLayout());
        add(summaryView, BorderLayout.CENTER);
    }

    public static PersonGroupView getInstance() {
        return instance;
    }

    public void setComponent(ColloquiaComponent tc, ColloquiaContainer parentGroup) {
        if(!(tc instanceof PersonGroup) || parentGroup == null) return;
        this.tc = tc;
        this.parentGroup = parentGroup;
        MainFrame.getInstance().statusBar.setText(msgLoading + tc.getName() + ".......");
        summaryView.setGroup((PersonGroup)tc);
        MainFrame.getInstance().statusBar.clearText();
    }

    public void updateView() {
        setComponent(tc, parentGroup);
    }
}


