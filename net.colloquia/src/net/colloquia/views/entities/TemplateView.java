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

public class TemplateView
extends ColloquiaView
{
    private JTabbedPane tabPane;
    private ActivityDataSheet ds;
    private ColloquiaTextEditor notesEditor;
    private ColloquiaTextEditor descriptionEditor;
    private ColloquiaTextEditor objectivesEditor;

    private static final TemplateView instance = new TemplateView();

    // Constructor
    private TemplateView() {
        setLayout(new BorderLayout());

        ds = new ActivityDataSheet();
        notesEditor = new ColloquiaTextEditor(false);
        descriptionEditor = new ColloquiaTextEditor(false);
        objectivesEditor = new ColloquiaTextEditor(false);

        tabPane = new JTabbedPane();
        tabPane.setFont(ColloquiaConstants.plainFont11);
        add(tabPane);

        // Metadata / Description / Objectives
        PSplitPane split1 = new PSplitPane(JSplitPane.VERTICAL_SPLIT);
        PSplitPane split2 = new PSplitPane(JSplitPane.VERTICAL_SPLIT);

        split1.setTopComponent(ds);
        JPanel p1 = new JPanel(new BorderLayout());
        PLabel l1 = new PLabel(" " + LanguageManager.getString("12_3"));
        p1.setBackground(ColloquiaConstants.color1);
        p1.add(l1, BorderLayout.NORTH);
        p1.add(descriptionEditor, BorderLayout.CENTER);
        split1.setBottomComponent(p1);

        split2.setTopComponent(split1);
        JPanel p2 = new JPanel(new BorderLayout());
        PLabel l2 = new PLabel(" " + LanguageManager.getString("12_4"));
        p2.setBackground(ColloquiaConstants.color1);
        p2.add(l2, BorderLayout.NORTH);
        p2.add(objectivesEditor, BorderLayout.CENTER);
        split2.setBottomComponent(p2);

        tabPane.addTab("", null, split2);
        split1.setDividerLocation(120);
        split2.setDividerLocation(320);

        // Notes
        tabPane.addTab(LanguageManager.getString("12_5"), null, notesEditor);
    }

    public static TemplateView getInstance() {
        return instance;
    }

    public void setComponent(ColloquiaComponent tc, ColloquiaContainer parentGroup) {
        if(!(tc instanceof Activity) || parentGroup == null) return;
        this.tc = tc;
        this.parentGroup = parentGroup;

        MainFrame.getInstance().statusBar.setText(msgLoading + tc.getName() + ".......");

        // Update data Sheet
        ds.setComponent(tc);

        // Set name on tab
        tabPane.setTitleAt(0, tc.getName());

        // Description
        descriptionEditor.loadComponentFile(tc, DataFiler.DESCRIPTION, tc.isMine());

        // Objectives
        objectivesEditor.loadComponentFile(tc, DataFiler.OBJECTIVES, tc.isMine());

        // Notes
        notesEditor.loadComponentFile(tc, DataFiler.PERSONAL_NOTES, true);

        MainFrame.getInstance().statusBar.clearText();
    }

    public void updateView() {
        setComponent(tc, parentGroup);
    }


}

