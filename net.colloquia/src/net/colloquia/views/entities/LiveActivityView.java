package net.colloquia.views.entities;

import javax.swing.*;

import net.colloquia.*;
import net.colloquia.comms.*;
import net.colloquia.comms.editors.*;
import net.colloquia.comms.index.*;
import net.colloquia.comms.tables.*;
import net.colloquia.datamodel.entities.*;
import net.colloquia.gui.widgets.*;
import net.colloquia.util.*;

public class LiveActivityView
extends ActivityView
implements MessageView
{
    private EMailViewer emailViewer;
    private GroupMessageTablePanel messageTablePanel;

    private static final LiveActivityView instance = new LiveActivityView();

    private LiveActivityView() {
        emailViewer = new EMailViewer();
        messageTablePanel = new GroupMessageTablePanel(emailViewer);

        // Messages
        PSplitPane split = new PSplitPane(JSplitPane.VERTICAL_SPLIT);
        split.setTopComponent(messageTablePanel);
        split.setBottomComponent(emailViewer);
        tabPane.insertTab(LanguageManager.getString("12_1"), null, split, null, 0);
    }

    public static LiveActivityView getInstance() {
        return instance;
    }

    public void setComponent(ColloquiaComponent tc, ColloquiaContainer parentGroup) {
        MainFrame.getInstance().statusBar.setText(msgLoading + tc.getName() + ".......");
        super.setComponent(tc, parentGroup); // this first
        reloadMessages();
        MainFrame.getInstance().statusBar.clearText();
    }

    /**
    * Select the Conversations Tab
    */
    private void selectConversationsTab() {
        tabPane.setSelectedIndex(0);
    }

    /**
    * Update the messages Vector to reflect any changes made externally
    */
    public void reloadMessages() {
        messageTablePanel.loadMessages((Activity)tc);
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
