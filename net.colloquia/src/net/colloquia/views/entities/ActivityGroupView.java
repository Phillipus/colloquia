package net.colloquia.views.entities;

import java.awt.*;

import net.colloquia.*;
import net.colloquia.datamodel.entities.*;
import net.colloquia.views.summary.*;

/**
* A Table view of all Activities in the Main Activities folder
*/
public class ActivityGroupView
extends ColloquiaView
{
    private Activity_SummaryView summaryView;

    private static final ActivityGroupView instance = new ActivityGroupView();

    public ActivityGroupView() {
        summaryView = new Activity_SummaryView();
        setLayout(new BorderLayout());
        add(summaryView, BorderLayout.CENTER);
    }

    public static ActivityGroupView getInstance() {
        return instance;
    }

    public void setComponent(ColloquiaComponent tc, ColloquiaContainer parentGroup) {
        if(!(tc instanceof ActivityGroup) || parentGroup == null) return;
        this.tc = tc;
        this.parentGroup = parentGroup;

        MainFrame.getInstance().statusBar.setText(msgLoading + tc.getName() + ".......");
        summaryView.setGroup((ActivityGroup)tc);
        MainFrame.getInstance().statusBar.clearText();
    }

    public void updateView() {
        setComponent(tc, parentGroup);
    }
}


