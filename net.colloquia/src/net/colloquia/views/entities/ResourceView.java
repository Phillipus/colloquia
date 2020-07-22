package net.colloquia.views.entities;

import java.awt.*;

import javax.swing.*;
import javax.swing.event.*;

import net.colloquia.*;
import net.colloquia.datamodel.entities.*;
import net.colloquia.gui.*;
import net.colloquia.gui.widgets.*;
import net.colloquia.io.*;
import net.colloquia.util.*;
import net.colloquia.views.browser.*;
import net.colloquia.views.datatable.*;
import net.colloquia.views.summary.*;

public class ResourceView
extends ColloquiaView
implements ChangeListener
{
    private ResourceDataSheet ds;
    private JTabbedPane tabPane;
    private ColloquiaTextEditor notesEditor;
    private ColloquiaTextEditor descriptionEditor;
    private ComponentBrowserPanel browser;
    protected ResourceActivity_SummaryView activitySummaryResourceView;
    private PSplitPane mainSplit;
    private JPanel mainPanel;
    private int lastTabSelected;

    private static final ResourceView instance = new ResourceView();

    private ResourceView() {
        setLayout(new BorderLayout());

        ds = new ResourceDataSheet();
        browser = new ComponentBrowserPanel();
        notesEditor = new ColloquiaTextEditor(false);
        descriptionEditor = new ColloquiaTextEditor(false);
        activitySummaryResourceView = new ResourceActivity_SummaryView();

        mainPanel = new JPanel(new BorderLayout());

        tabPane = new JTabbedPane();
        tabPane.setFont(ColloquiaConstants.plainFont11);

        tabPane.addTab(LanguageManager.getString("8"), null, mainPanel);
        tabPane.addTab(LanguageManager.getString("12_3"), null, new JPanel());
        tabPane.addTab(LanguageManager.getString("12_5"), null, new JPanel());
        tabPane.addTab(LanguageManager.getString("1"), null, new JPanel());

        add(tabPane);

        mainSplit = new PSplitPane(JSplitPane.VERTICAL_SPLIT);
        mainPanel.add(mainSplit);

        mainSplit.setTopComponent(ds);
        mainSplit.setBottomComponent(browser);

        // Listen to tab changes
        tabPane.addChangeListener(this);
    }

    public static ResourceView getInstance() {
        return instance;
    }

    public void setComponent(ColloquiaComponent tc, ColloquiaContainer parentGroup) {
        if(!(tc instanceof Resource) || parentGroup == null) return;
        this.tc = tc;
        this.parentGroup = parentGroup;

        MainFrame.getInstance().statusBar.setText(msgLoading + tc.getName() + ".......");

        // Set name on tab
        tabPane.setTitleAt(0, tc.getName());

        // update data Sheet
        ds.setComponent(tc);

        // Get the description text file
        descriptionEditor.loadComponentFile(tc, DataFiler.DESCRIPTION, tc.isMine());

        // Get the notes text file name
        notesEditor.loadComponentFile(tc, DataFiler.PERSONAL_NOTES, true);

        // Activity Summary view
        activitySummaryResourceView.setResource((Resource)tc);

        // Browser
        browser.loadComponent(tc);

        MainFrame.getInstance().statusBar.clearText();
    }

    public void updateView() {
        setComponent(tc, parentGroup);
    }


    /** Click on tab pane */
    public void stateChanged(ChangeEvent e) {
        int tab = tabPane.getSelectedIndex();
        int dividerLocation = mainSplit.getDividerLocation();
        tabPane.setComponentAt(lastTabSelected, new JPanel());
        tabPane.setComponentAt(tab, mainPanel);

        switch(tab) {
            case 0:
                mainSplit.setTopComponent(ds);
                break;
            case 1:
                mainSplit.setTopComponent(descriptionEditor);
                mainSplit.setBottomComponent(browser);
                break;
            case 2:
                mainSplit.setTopComponent(notesEditor);
                mainSplit.setBottomComponent(browser);
                break;
            case 3:
                mainSplit.setTopComponent(activitySummaryResourceView);
                mainSplit.setBottomComponent(browser);
                break;
        }

        if(dividerLocation != 0) mainSplit.setDividerLocation(dividerLocation);
        lastTabSelected = tab;
    }

}

