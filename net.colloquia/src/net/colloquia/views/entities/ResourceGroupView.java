package net.colloquia.views.entities;

import java.awt.*;

import javax.swing.*;

import net.colloquia.*;
import net.colloquia.datamodel.entities.*;
import net.colloquia.views.summary.*;

public class ResourceGroupView
extends ColloquiaView
{
    private Resource_SummaryView summaryView;
    private static final ResourceGroupView instance = new ResourceGroupView();

    private JTabbedPane tabPane;

    private ResourceGroupView() {
        setLayout(new BorderLayout());

        summaryView = new Resource_SummaryView();

        tabPane = new JTabbedPane();
        tabPane.setFont(ColloquiaConstants.plainFont11);

		tabPane.addTab("", null, summaryView);

        add(tabPane);
    }

    public static ResourceGroupView getInstance() {
        return instance;
    }

    public void setComponent(ColloquiaComponent tc, ColloquiaContainer parentGroup) {
        if(!(tc instanceof ResourceGroup) || parentGroup == null) return;
        this.tc = tc;
        this.parentGroup = parentGroup;

        // Set name on tab
        tabPane.setTitleAt(0, tc.getName());

        MainFrame.getInstance().statusBar.setText(msgLoading + tc.getName() + ".......");

        summaryView.setGroup((ResourceGroup)tc);

        MainFrame.getInstance().statusBar.clearText();
    }

    public void updateView() {
        setComponent(tc, parentGroup);
    }
}


