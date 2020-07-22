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

public class StudentsPersonView
extends PersonView
implements MessageView
{
    private EMailViewer emailViewer;
    private SingleMessageTablePanel messageTablePanel;

    private static final StudentsPersonView instance = new StudentsPersonView();

    // Constructor
    private StudentsPersonView() {
        tabPane.addTab(LanguageManager.getString("12_2"), null, new JPanel());
        tabPane.addTab(LanguageManager.getString("12_1"), null, new JPanel());
        tabPane.addTab(LanguageManager.getString("12_5"), null, new JPanel());
        tabPane.addTab(LanguageManager.getString("1"), null, new JPanel());

        emailViewer = new EMailViewer();
        messageTablePanel = new SingleMessageTablePanel(emailViewer);
    }

    public static StudentsPersonView getInstance() {
        return instance;
    }

    public void setComponent(ColloquiaComponent tc, ColloquiaContainer parentGroup) {
        if(!(tc instanceof Person) || parentGroup == null) return;

        MainFrame.getInstance().statusBar.setText(msgLoading + tc.getName() + ".......");
        super.setComponent(tc, parentGroup);
        // Update e-mail messages
        reloadMessages();
        MainFrame.getInstance().statusBar.clearText();
    }

    public void updateView() {
        setComponent(tc, parentGroup);
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
                //mainSplit.setTopComponent(messageTablePanel); // Lister
                mainSplit.setBottomComponent(emailViewer);    // Editor
                break;
            // Personal Notes
            case 2:
                topSplit.setRightComponent(notesEditor);
                //mainSplit.setTopComponent(notesEditor);
                mainSplit.setBottomComponent(browser);
                break;
            // Activity summary
            case 3:
                topSplit.setRightComponent(activitySummaryPeopleView);
                //mainSplit.setTopComponent(new JPanel());
                mainSplit.setBottomComponent(browser);
                break;
        }
        if(topSplitPos != 0) topSplit.setDividerLocation(topSplitPos);
        if(mainSplitPos != 0) mainSplit.setDividerLocation(mainSplitPos);
        lastTabSelected = tab;
    }


    /**
    * Update the messages
    */
    public void reloadMessages() {
        messageTablePanel.loadMessages((Person)tc, (Activity)parentGroup);
    }

    /**
    Select the Conversations Tab
    */
    private void selectConversationsTab() {
        tabPane.setSelectedIndex(1);
    }

    /**
    * Select a message on the messages table that matches mInfo
    */
    public void selectMessage(MessageInfo mInfo) {
        selectConversationsTab();
        messageTablePanel.selectMessage(mInfo);
    }

    /**
    * Auto-Edit a message on the messages table that matches mInfo
    */
    public void editMessage(MessageInfo mInfo) {
        selectMessage(mInfo);
        // Get the matching message
        MessageIndex index = messageTablePanel.getModel().getMessageIndex();
        if(index != null) {
        	MessageInfo mi = index.getMessage(mInfo);
        	if(mi != null) EMailEditor.editMessage(mi);
        }
    }
}
