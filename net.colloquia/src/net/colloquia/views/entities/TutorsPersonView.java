package net.colloquia.views.entities;

import javax.swing.*;
import javax.swing.event.*;

import net.colloquia.*;
import net.colloquia.comms.*;
import net.colloquia.comms.editors.*;
import net.colloquia.comms.index.*;
import net.colloquia.comms.tables.*;
import net.colloquia.datamodel.entities.*;
import net.colloquia.util.*;

/**
 * The view a tutor sees of a Person in a live Activity
 */
public class TutorsPersonView
extends PersonView
implements MessageView
{
    private SingleMessageTablePanel messageTablePanel;
    private AssignmentMessageTablePanel assignmentTablePanel;
    private EMailViewer emailViewer;
    private EMailViewer assignmentViewer;

    private static final TutorsPersonView instance = new TutorsPersonView();

    // Constructor
    private TutorsPersonView() {
        tabPane.addTab(LanguageManager.getString("12_2"), null, new JPanel());
        tabPane.addTab(LanguageManager.getString("12_1"), null, new JPanel());
        tabPane.addTab(LanguageManager.getString("12_5"), null, new JPanel());
        tabPane.addTab(LanguageManager.getString("2"), null, new JPanel());
        tabPane.addTab(LanguageManager.getString("1"), null, new JPanel());

        emailViewer = new EMailViewer();
        messageTablePanel = new SingleMessageTablePanel(emailViewer);

        assignmentViewer = new EMailViewer();
        assignmentTablePanel = new AssignmentMessageTablePanel(assignmentViewer);
    }

    public static TutorsPersonView getInstance() {
        return instance;
    }

    public void setComponent(ColloquiaComponent tc, ColloquiaContainer parentGroup) {
        if(!(tc instanceof Person) || parentGroup == null) return;

        MainFrame.getInstance().statusBar.setText(msgLoading + tc.getName() + ".......");
        super.setComponent(tc, parentGroup);
        reloadMessages();
        MainFrame.getInstance().statusBar.clearText();
    }

    /**
    * Select the Conversations Tab
    */
    private void selectConversationsTab() {
        tabPane.setSelectedIndex(1);
    }

    /**
    * Select the Assignment Tab
    */
    public void selectAssignmentTab() {
        tabPane.setSelectedIndex(3);
    }

    /** Click on tab pane */
    public void stateChanged(ChangeEvent e) {
        int tab = tabPane.getSelectedIndex();
        int mainSplitPos = mainSplit.getDividerLocation();
        int topSplitPos = topSplit.getDividerLocation();
        tabPane.setComponentAt(lastTabSelected, new JPanel());
        tabPane.setComponentAt(tab, mainPanel);

        switch(tab) {
            // Home
            case 0:
                topSplit.setRightComponent(ds);
                //mainSplit.setTopComponent(topSplit);
                mainSplit.setBottomComponent(browser);
                break;
            // Conversations
            case 1:
                topSplit.setRightComponent(messageTablePanel);
                //mainSplit.setTopComponent(messageTablePanel);
                mainSplit.setBottomComponent(emailViewer);
                break;
            // Person Notes
            case 2:
                topSplit.setRightComponent(notesEditor);
                //mainSplit.setTopComponent(notesEditor);
                mainSplit.setBottomComponent(browser);
                break;
            // Assignment
            case 3:
                topSplit.setRightComponent(assignmentTablePanel);
                //mainSplit.setTopComponent(assignmentTablePanel);
                mainSplit.setBottomComponent(assignmentViewer);
                break;
            // Activity summary
            case 4:
                topSplit.setRightComponent(activitySummaryPeopleView);
                //mainSplit.setTopComponent(new JPanel());
                mainSplit.setBottomComponent(browser);
                break;
        }
        if(topSplitPos != 0) topSplit.setDividerLocation(topSplitPos);
        if(mainSplitPos != 0) mainSplit.setDividerLocation(mainSplitPos);
        lastTabSelected = tab;
    }


    public void updateView() {
        setComponent(tc, parentGroup);
    }


    /**
    * Update the messages
    */
    public void reloadMessages() {
        Activity A = (Activity)parentGroup;
        Person person = (Person)tc;

        // Messages
        messageTablePanel.loadMessages(person, A);
        // Assignment
        assignmentTablePanel.loadMessages(person, A);
    }

    /**
    * Select a message on the messages table that matches mInfo
    * This could be either a Single Message or an Assignment Message
    */
    public void selectMessage(MessageInfo mInfo) {
        if(mInfo.getMessageType() == MessageInfo.SINGLE_MESSAGE) {
            selectConversationsTab();
            messageTablePanel.selectMessage(mInfo);
        }
        else if(mInfo.getMessageType() == MessageInfo.ASSIGNMENT_MESSAGE) {
            selectAssignmentTab();
            assignmentTablePanel.selectMessage(mInfo);
        }
    }

    /**
    * Auto-Edit a message on the messages table that matches mInfo
    * This could be either a Single Message or an Assignment Message
    */
    public void editMessage(MessageInfo mInfo) {
        MessageIndex index = null;

        selectMessage(mInfo);

        // Get the matching message
        if(mInfo.getMessageType() == MessageInfo.SINGLE_MESSAGE) {
            index = messageTablePanel.getModel().getMessageIndex();
        }
        else if(mInfo.getMessageType() == MessageInfo.ASSIGNMENT_MESSAGE) {
            index = assignmentTablePanel.getModel().getMessageIndex();
        }
        else return;

		if(index != null) {
        	MessageInfo mi = index.getMessage(mInfo);
        	if(mi != null) EMailEditor.editMessage(mi);
        }
    }
}

