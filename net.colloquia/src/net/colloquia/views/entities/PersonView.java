package net.colloquia.views.entities;

import java.awt.*;

import javax.swing.*;
import javax.swing.event.*;

import net.colloquia.*;
import net.colloquia.datamodel.entities.*;
import net.colloquia.gui.*;
import net.colloquia.gui.widgets.*;
import net.colloquia.io.*;
import net.colloquia.views.*;
import net.colloquia.views.browser.*;
import net.colloquia.views.datatable.*;
import net.colloquia.views.summary.*;

/**
The ancestor abstract class for the 3 person views
*/
public abstract class PersonView
extends ColloquiaView
implements ChangeListener
{
    protected PersonDataSheet ds;
    protected PhotoPanel photoPanel;
    protected JPanel mainPanel;
    protected ComponentBrowserPanel browser;
    protected JTabbedPane tabPane;
    protected ColloquiaTextEditor notesEditor;
    protected PersonsActivity_SummaryView activitySummaryPeopleView;
    protected PSplitPane mainSplit;
    protected PSplitPane topSplit;
    protected int lastTabSelected;

    protected PersonView() {
        setLayout(new BorderLayout());

        mainPanel = new JPanel(new BorderLayout());

        photoPanel = new PhotoPanel();
        ds = new PersonDataSheet(photoPanel);
        notesEditor = new ColloquiaTextEditor(false);
        activitySummaryPeopleView = new PersonsActivity_SummaryView();
        browser = new ComponentBrowserPanel();

        tabPane = new JTabbedPane();
        tabPane.setFont(ColloquiaConstants.plainFont11);
        tabPane.addChangeListener(this);
        add(tabPane);

        topSplit = new PSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        topSplit.setLeftComponent(photoPanel);
        topSplit.setRightComponent(ds);

        mainSplit = new PSplitPane(JSplitPane.VERTICAL_SPLIT);

        mainPanel.add(mainSplit);
        mainSplit.setTopComponent(topSplit);
        mainSplit.setBottomComponent(browser);
    }

    public void setComponent(ColloquiaComponent tc, ColloquiaContainer parentGroup) {
        if(!(tc instanceof Person) || parentGroup == null) return;
        this.tc = tc;
        this.parentGroup = parentGroup;
        // Set name on tab
        tabPane.setTitleAt(0, tc.getName());
        // update data Sheet
        ds.setComponent(tc);
        // Display the photo
        photoPanel.setImage(tc.getProperty(Person.PHOTOGRAPH));
        // Get the notes text file
        notesEditor.loadComponentFile(tc, DataFiler.PERSONAL_NOTES, true);
        // Activity Summary view
        activitySummaryPeopleView.setPerson((Person)tc);
        // Browser
        browser.loadComponent(tc);
    }
}
