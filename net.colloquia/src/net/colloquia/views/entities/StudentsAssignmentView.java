package net.colloquia.views.entities;

import javax.swing.*;
import javax.swing.event.*;

import net.colloquia.*;
import net.colloquia.comms.*;
import net.colloquia.comms.editors.*;
import net.colloquia.comms.index.*;
import net.colloquia.comms.tables.*;
import net.colloquia.datamodel.*;
import net.colloquia.datamodel.entities.*;
import net.colloquia.util.*;

/**
 * The Student's view of an Assignment that has been received from a Tutor
 */
public class StudentsAssignmentView
extends AssignmentView
implements MessageView
{
    private AssignmentMessageTablePanel assignmentTablePanel;
    private EMailViewer assignmentViewer;

    private static final StudentsAssignmentView instance = new StudentsAssignmentView();

    private StudentsAssignmentView() {
        mainSplit.setTopComponent(ds);
        mainSplit.setBottomComponent(browser);
        tabPane.addTab(LanguageManager.getString("2"), null, mainPanel);
        tabPane.addTab(LanguageManager.getString("12_3"), null, new JPanel());
        tabPane.addTab(LanguageManager.getString("12_10"), null, new JPanel());
        tabPane.addTab(LanguageManager.getString("12_5"), null, new JPanel());
        tabPane.addChangeListener(this);

        assignmentViewer = new EMailViewer();
        assignmentTablePanel = new AssignmentMessageTablePanel(assignmentViewer);
   }

    public static StudentsAssignmentView getInstance() {
        return instance;
    }

    public void setComponent(ColloquiaComponent tc, ColloquiaContainer parentGroup) {
        if(!(tc instanceof Assignment) || parentGroup == null) return;

        MainFrame.getInstance().statusBar.setText(msgLoading + tc.getName() + ".......");
        super.setComponent(tc, parentGroup);
        reloadMessages();
        MainFrame.getInstance().statusBar.clearText();
    }

    /** Click on tab pane */
    public void stateChanged(ChangeEvent e) {
        int tab = tabPane.getSelectedIndex();
        int dividerLocation = mainSplit.getDividerLocation();
        tabPane.setComponentAt(lastTabSelected, new JPanel());
        tabPane.setComponentAt(tab, mainPanel);

        switch(tab) {
            // Datasheet
            case 0:
                mainSplit.setTopComponent(ds);
                mainSplit.setBottomComponent(browser);
                break;
            // Description
            case 1:
                mainSplit.setTopComponent(descriptionEditor);
                mainSplit.setBottomComponent(browser);
                break;
            // Messages
            case 2:
                mainSplit.setTopComponent(assignmentTablePanel);
                mainSplit.setBottomComponent(assignmentViewer);
                break;
            // Personal notes
            case 3:
                mainSplit.setTopComponent(notesEditor);
                mainSplit.setBottomComponent(browser);
                break;
        }
        if(dividerLocation != 0) mainSplit.setDividerLocation(dividerLocation);
        lastTabSelected = tab;
    }

    /**
    *
    */
    public void reloadMessages() {
        Activity A = (Activity)parentGroup;
        Person tutor = DataModel.getPersonByEmailAddress(A.getSubmitter());
        assignmentTablePanel.loadMessages(tutor, A);
    }

    /**
    * Select Message tab
    */
    public void selectMessagePanel() {
        tabPane.setSelectedIndex(2);
    }


    public void selectMessage(MessageInfo mInfo) {
        selectMessagePanel();
        assignmentTablePanel.selectMessage(mInfo);
    }

    public void editMessage(MessageInfo mInfo) {
        selectMessage(mInfo);
        // Get the matching message
        MessageIndex index = assignmentTablePanel.getModel().getMessageIndex();
        if(index != null) {
        	MessageInfo mi = index.getMessage(mInfo);
        	if(mi != null) EMailEditor.editMessage(mi);
        }
    }
}

