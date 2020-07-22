package net.colloquia.views.entities;

import java.awt.*;

import javax.swing.*;

import net.colloquia.*;
import net.colloquia.datamodel.entities.*;
import net.colloquia.gui.*;
import net.colloquia.gui.widgets.*;
import net.colloquia.io.*;
import net.colloquia.util.*;
import net.colloquia.views.datatable.*;
import net.colloquia.views.summary.*;

public abstract class ActivityView
extends ColloquiaView
{
    protected ActivityDataSheet ds;
    protected JTabbedPane tabPane;

    protected ColloquiaTextEditor notesEditor;
    protected ColloquiaTextEditor descriptionEditor;
    protected ColloquiaTextEditor objectivesEditor;

    protected PeopleInActivity_SummaryView personSummaryView;
    protected Resource_SummaryView resourceSummaryView;
    protected Activity_SummaryView activitySummaryView;

    // Constructor
    protected ActivityView() {
        setLayout(new BorderLayout());

        ds = new ActivityDataSheet();

        notesEditor = new ColloquiaTextEditor(false);
        descriptionEditor = new ColloquiaTextEditor(false);
        objectivesEditor = new ColloquiaTextEditor(false);

        personSummaryView = new PeopleInActivity_SummaryView();
        resourceSummaryView = new Resource_SummaryView();
        activitySummaryView = new Activity_SummaryView();

        tabPane = new JTabbedPane();
        tabPane.setFont(ColloquiaConstants.plainFont11);
        add(tabPane);

        // Metadata / Description / Objectives
        PSplitPane split1 = new PSplitPane(JSplitPane.VERTICAL_SPLIT);
        PSplitPane split2 = new PSplitPane(JSplitPane.VERTICAL_SPLIT);

        // DataSheet
        split1.setTopComponent(ds);
        JPanel p1 = new JPanel(new BorderLayout());
        PLabel l1 = new PLabel(" " + LanguageManager.getString("12_3"));
        p1.setBackground(ColloquiaConstants.color3);
        p1.add(l1, BorderLayout.NORTH);
        // Description
        p1.add(descriptionEditor, BorderLayout.CENTER);
        split1.setBottomComponent(p1);

        // Objectives
        split2.setTopComponent(split1);
        JPanel p2 = new JPanel(new BorderLayout());
        PLabel l2 = new PLabel(" " + LanguageManager.getString("12_4"));
        p2.setBackground(ColloquiaConstants.color3);
        p2.add(l2, BorderLayout.NORTH);
        p2.add(objectivesEditor, BorderLayout.CENTER);
        split2.setBottomComponent(p2);

        tabPane.addTab(LanguageManager.getString("12_3"), null, split2);
        split1.setDividerLocation(120);
        split2.setDividerLocation(320);

        // Notes
        tabPane.addTab(LanguageManager.getString("12_5"), null, notesEditor);

        // People Summary View
        tabPane.addTab(LanguageManager.getString("7"), null, personSummaryView);

        // Resource Summary View
        tabPane.addTab(LanguageManager.getString("9"), null, resourceSummaryView);

        // Activity Summary View
        tabPane.addTab(LanguageManager.getString("5"), null, activitySummaryView);
    }

    public void setComponent(ColloquiaComponent tc, ColloquiaContainer parentGroup) {
        if(!(tc instanceof Activity) || parentGroup == null) return;
        this.tc = tc;
        this.parentGroup = parentGroup;

        // Update data Sheet
        ds.setComponent(tc);

        // Description
        descriptionEditor.loadComponentFile(tc, DataFiler.DESCRIPTION, tc.isMine());

        // Objectives
        objectivesEditor.loadComponentFile(tc, DataFiler.OBJECTIVES, tc.isMine());

        // Notes
        notesEditor.loadComponentFile(tc, DataFiler.PERSONAL_NOTES, true);

        // Person Summary View
        personSummaryView.setGroup((Activity)tc);
        // Resource Summary View
        resourceSummaryView.setGroup((Activity)tc);
        // Activity Summary View
        activitySummaryView.setGroup((Activity)tc);
    }

    public void updateView() {
        setComponent(tc, parentGroup);
    }
}
