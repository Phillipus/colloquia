package net.colloquia.views.entities;

import java.awt.*;

import javax.swing.*;
import javax.swing.event.*;

import net.colloquia.*;
import net.colloquia.datamodel.entities.*;
import net.colloquia.gui.*;
import net.colloquia.gui.widgets.*;
import net.colloquia.io.*;
import net.colloquia.views.browser.*;
import net.colloquia.views.datatable.*;

/*
Assignment View Abstract class
*/
public abstract class AssignmentView
extends ColloquiaView
implements ChangeListener
{
    protected ColloquiaContainer parentGroup;
    protected AssignmentDataSheet ds;
    protected JTabbedPane tabPane;
    protected JPanel mainPanel;
    protected ComponentBrowserPanel browser;
    protected PSplitPane mainSplit;
    protected ColloquiaTextEditor notesEditor;
    protected ColloquiaTextEditor descriptionEditor;
    protected int lastTabSelected;

    protected AssignmentView() {
        setLayout(new BorderLayout());

        ds = new AssignmentDataSheet();
        notesEditor = new ColloquiaTextEditor(false);
        descriptionEditor = new ColloquiaTextEditor(false);
        browser = new ComponentBrowserPanel();

        tabPane = new JTabbedPane();
        tabPane.setFont(ColloquiaConstants.plainFont11);
        add(tabPane);

        mainPanel = new JPanel(new BorderLayout());

        mainSplit = new PSplitPane(JSplitPane.VERTICAL_SPLIT);
        mainPanel.add(mainSplit);
    }

    public void setComponent(ColloquiaComponent tc, ColloquiaContainer parentGroup) {
        if(!(tc instanceof Assignment) || parentGroup == null) return;
        this.tc = tc;
        this.parentGroup = parentGroup;

        // Set name on tab
        tabPane.setTitleAt(0, tc.getName());

        // update data Sheet
        ds.setComponent(tc);

        // Notes
        notesEditor.loadComponentFile(tc, DataFiler.PERSONAL_NOTES, true);

        // Description
        descriptionEditor.loadComponentFile(tc, DataFiler.DESCRIPTION, tc.isMine());

        // Browser
        browser.loadComponent(tc);
    }

    public void updateView() {
        setComponent(tc, parentGroup);
    }
}
