package net.colloquia.comms.tables;

import java.awt.*;
import java.awt.event.*;
import java.util.*;

import javax.swing.*;
import javax.swing.event.*;

import net.colloquia.*;
import net.colloquia.comms.*;
import net.colloquia.comms.index.*;
import net.colloquia.comms.messages.*;
import net.colloquia.datamodel.*;
import net.colloquia.datamodel.entities.*;
import net.colloquia.gui.widgets.*;
import net.colloquia.io.*;
import net.colloquia.menu.*;
import net.colloquia.util.*;
import net.colloquia.views.*;
import net.colloquia.views.entities.*;
import net.colloquia.views.tree.*;
import net.colloquia.xml.*;

/**
 * The Outbox.
 *
 *
 */
public class Outbox
extends MessageBox
implements TableModelListener
{
    private SendThread sendThread;

    // Toolbar stuff
    private Action_SendMail actionSendMail;
    private Action_Delete actionDelete;
    private Action_Edit actionEdit;

    private static Outbox instance = new Outbox();

    private Outbox() {
        super(new OutboxTableModel());

        // Column widths
        tableColumnModel.getColumn(0).setPreferredWidth(25);
        tableColumnModel.getColumn(0).setMaxWidth(25);
        tableColumnModel.getColumn(1).setPreferredWidth(18);
        tableColumnModel.getColumn(1).setMaxWidth(18);
        tableColumnModel.getColumn(2).setPreferredWidth(120);
        tableColumnModel.getColumn(3).setPreferredWidth(50);
        tableColumnModel.getColumn(4).setPreferredWidth(100);

        // Set the default cell renderer for any type object
        cellRenderer = new OutboxTableRenderer();
        table.setDefaultRenderer(Object.class, cellRenderer);

        JScrollPane sp = new JScrollPane(table);
        sp.setColumnHeaderView(tableHeader);
        add(sp, BorderLayout.CENTER);

        // Listen to table data changes
        model.addTableModelListener(this);

        setPreferredSize(new Dimension(200, 200));
        setMinimumSize(new Dimension(0, 0));
    }

    public static Outbox getInstance() {
        return instance;
    }

    /**
    Initialise and make the toolbar
    */
    protected void initToolBar() {
        // The Key Mask will be 'Meta' for Mac and 'Ctrl' for PC/Unix
        int keyMask = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
      	JMenu menu = menuBar.add(new JMenu(LanguageManager.getString("16_10")));
        panel.add(menuBar, BorderLayout.NORTH);
        panel.add(toolBar, BorderLayout.CENTER);

        // Send mail
        actionSendMail = new Action_SendMail();
        toolBar.add(actionSendMail, false);
        JMenuItem item = menu.add(actionSendMail);

        // Edit button (equivalent) to double-clicking
        actionEdit = new Action_Edit();
        toolBar.add(actionEdit, false);
        item = menu.add(actionEdit);

        toolBar.addSeparator();
        menu.addSeparator();

        // Delete button
        actionDelete = new Action_Delete();
        toolBar.add(actionDelete, false);
        item = menu.add(actionDelete);
        item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0));

        // Message
        toolBar.addSeparator();
        toolBar.add(new PLabel(LanguageManager.getString("16_110"), ColloquiaConstants.color1));

        add(panel, BorderLayout.NORTH);
    }


    /**
    * Adds a Message to the outbox
    * and then try to send it if sendNow is true
    */
    public void addMessage(Frame owner, MessageInfo mInfo, boolean sendNow) {
        if(mInfo == null) return;

        // Save the message
        try {
            model.addMessage(mInfo, false);
        }
        catch(XMLWriteException ex) {
            ErrorHandler.showWarning("ERR8", ex, "ERR");
        }

        if(sendNow) {
            // Send a single file
            Vector msgsToSend = new Vector();
            msgsToSend.addElement(mInfo);
            if(sendThread != null) sendThread.waitFor();
            sendThread = new SendThread(owner, msgsToSend);
            sendThread.start();
        }
    }

    /**
    * Sends all pending mail in the Outbox
    */
    public void sendAllMail(Frame owner) {
        MessageIndex index = model.getMessageIndex();
        if(index != null) {
        	Vector msgsToSend = index.getMessages();
        	// Any to send?
        	if(!msgsToSend.isEmpty()) {
	            if(sendThread != null) sendThread.waitFor();
    	        sendThread = new SendThread(owner, msgsToSend);
	            sendThread.start();
            }
        }
    }


    /**
    * Send the messages on a Thread
    */
    private class SendThread
    extends WaitThread
    {
        private Frame owner;
        private Vector msgsToSend;

    	public SendThread(Frame owner, Vector msgsToSend) {
            this.owner = owner;
        	this.msgsToSend = msgsToSend;
        }

        public void run() {
            // We have to force a save so that all data is valid
            try {
            	DataFiler.saveAll();
            }
            catch(ColloquiaFileException ex) {
	            ErrorHandler.showWarning("ERR2", ex, "ERR");
    	        return;
            }
	        Vector msgs = MessageManager.sendMessages(owner, msgsToSend);
    	    checkSendResults(msgs);
        }
    }

    /**
    * The MessageManager will have set some valid/invalid addresses in the MessageOuts
    * So we need to sort it all out by updating the MessageOuts
    */
    private void checkSendResults(Vector msgs) {
        Vector badAddresses = new Vector();

        // Go thru the results
        for(int i = 0; i < msgs.size(); i++) {
            MessageOut messageOut = (MessageOut)msgs.elementAt(i);
            MessageInfo mInfo = messageOut.updateMessageInfo();

            // Any duffers?
            Vector v = messageOut.getAllInvalidAddresses();
            for(int j = 0; j < v.size(); j++) {
            	badAddresses.addElement(v.elementAt(j));
            }

            // Has the message been sent?
            if(mInfo.getState() == MessageInfo.SENT) {
                // Tell the source
                MessageIndex.updateMessageInfo(mInfo);
                // Delete from Outbox and put in Sentbox
                try {
                    Sentbox.getInstance().addMessage(mInfo);
                    model.deleteMessage(mInfo);
                }
                catch(XMLWriteException ex) {
                    ErrorHandler.showWarning("ERR10", ex, "ERR");
                }
            }
        }

        // Set buttons
        updateButtons();

        ColloquiaTree.getInstance().reselectCurrentNode();
        ColloquiaTree.getInstance().repaint();
        // Tell the underlying view
        //updateUnderlyingView();

        // Report any duffers and remove them from MessageInfos
        if(!badAddresses.isEmpty()) {
    	    String list = "";

        	for(int i = 0; i < badAddresses.size(); i++) {
                String badMail = (String)badAddresses.elementAt(i);
            	list += badMail + "\n";
            }

            JOptionPane.showMessageDialog(MainFrame.getInstance(),
                LanguageManager.getString("16_93") + "\n\n" + list,
                LanguageManager.getString("ERROR"),
                JOptionPane.ERROR_MESSAGE);

        }
    }


    /**
    * Update the current view so any new messages or sent dates or back to draft
    * show up in any displayed table or view
    */
    private void updateUnderlyingView() {
        ColloquiaView underlyingView = ViewPanel.getInstance().getCurrentView();
        // If it's a Message View
        if(underlyingView instanceof MessageView) ((MessageView)underlyingView).reloadMessages();
    }


    /**
    * Set a bunch of selected messages back to draft
    */
    private void setMessagesToDraft() {
        int[] rowsSelected = table.getSelectedRows();

        // Get messages - BACKWARDS!
        for(int i = rowsSelected.length - 1; i >= 0; i--) {
            MessageInfo mInfo = (MessageInfo)model.getValueAt(rowsSelected[i], 0);
            setMessageToDraft(mInfo);
        }
    }


    /**
    * Set a Message back to draft and remove the message from the Outbox
    */
    public void setMessageToDraft(MessageInfo mInfo) {
        if(mInfo == null) return;

        // Don't do this if it's a RESEND
        if(mInfo.getMessageType() != MessageInfo.RESEND) {
	        // Set to draft
    	    mInfo.setState(MessageInfo.DRAFT);

	        // Set the corresponding message to this
    	    MessageIndex.updateMessageInfo(mInfo);
        }

        // Delete from Outbox
        try {
            model.deleteMessage(mInfo);
        }
        catch(XMLWriteException ex) {
            ErrorHandler.showWarning("ERR10", ex, "ERR");
        }

        // Set buttons
        updateButtons();

        // Tell the underlying view
        updateUnderlyingView();
    }


    /**
    * Put back to draft and jump to a message to edit it when user d-clicks on it
    */
    protected void jumpToMessage(MessageInfo mInfo) {
        Activity A;
        Person person;

        // Get the Message
        if(mInfo == null) return;

        table.setCursor(ColloquiaConstants.waitCursor);

        // Set the message back to draft
        setMessageToDraft(mInfo);

        // Select the node to jump to
        ColloquiaTree tree = ColloquiaTree.getInstance();
        switch(mInfo.getMessageType()) {
            case MessageInfo.SINGLE_MESSAGE:
                A = (Activity)DataModel.getComponent(mInfo.getActivityID());
                person = (Person)DataModel.getComponent(mInfo.getPersonID());
                if(person == null) tree.selectNodeByObject(A);
                else tree.selectNode(person, A);
                break;

            case MessageInfo.ACTIVITY:
			case MessageInfo.ACTIVITY_INVITE:
            case MessageInfo.GROUP_MESSAGE:
            case MessageInfo.ACTIVITY_ACCEPT:
            case MessageInfo.ACTIVITY_COMPLETE:
            case MessageInfo.ACTIVITY_REACTIVATE:
                A = (Activity)DataModel.getComponent(mInfo.getActivityID());
                tree.selectNodeByObject(A);
                break;

            // This will depend on whether I'm a Student or Tutor
            // If I'm the tutor, select Person
            // If I'm the student, select Assignment
            case MessageInfo.ASSIGNMENT_MESSAGE:
                A = (Activity)DataModel.getComponent(mInfo.getActivityID());
                person = (Person)DataModel.getComponent(mInfo.getPersonID());
                if(A != null && person != null) {
                    if(A.isMine()) tree.selectNode(person, A);
                    else tree.selectNode(A.getAssignment(), A);
                }
                break;

            case MessageInfo.RESOURCE:
                Resource resource = (Resource)DataModel.getComponent(mInfo.getComponentID());
                tree.selectNodeByObject(resource);
                break;
        }

        // Get the current view so we can switch to the right tab and message
        ColloquiaView cv = ViewPanel.getInstance().getCurrentView();
        if(cv instanceof MessageView) ((MessageView)cv).editMessage(mInfo);

        table.setCursor(ColloquiaConstants.defaultCursor);
        if((MainFrame.getInstance().getExtendedState() & JFrame.ICONIFIED) == 1) {
            MainFrame.getInstance().setState(JFrame.NORMAL);
        }
        MainFrame.getInstance().toFront();
    }


    /*
    * This is called from another MessageTable when the corresponding message is deleted
    */
    public void deleteMessages(MessageInfo[] mInfo) throws XMLWriteException {
        model.deleteMessages(mInfo);
    }

    /*
    * This is called from the MessageTable when the corresponding message is deleted
    */
    public void deleteMessage(MessageInfo mInfo) throws XMLWriteException {
        model.deleteMessage(mInfo);
    }


    /**
    * A row has been selected
    */
    public void valueChanged(ListSelectionEvent e) {
        updateButtons();
    }

    /**
    * The table data has changed
    */
    public void tableChanged(TableModelEvent e) {
        updateButtons();
    }

    private void updateButtons() {
        int row = table.getSelectedRow();
        int numRows = model.getRowCount();
        // No row selected
        if(row == -1 || numRows == 0) {
            actionDelete.setEnabled(false);
            actionEdit.setEnabled(false);
        }
      	else {
            actionDelete.setEnabled(true);
            actionEdit.setEnabled(true);
        }

        if(numRows == 0) actionSendMail.setEnabled(false);
        else actionSendMail.setEnabled(true);

        fireTableChanged(model.getMessageIndex());
    }


    /**
    * Handles button press - Edits selected message
    */
    private class Action_Edit extends MenuAction {
        public Action_Edit() {
            super(LanguageManager.getString("16_24"), ColloquiaConstants.iconEditMessage);
            setButtonText(LanguageManager.getString("BUT13"));
        }

        public void actionPerformed(ActionEvent e) {
            jumpToMessage(getSelectedMessage());
        }
    }

    /**
    Handles button press - sends all pending mail
    */
    private class Action_SendMail extends MenuAction {
        public Action_SendMail() {
            super(LanguageManager.getString("16_25"), ColloquiaConstants.iconSendMail);
            setButtonText(LanguageManager.getString("BUT14"));
        }

        public void actionPerformed(ActionEvent e) {
            sendAllMail(MessageWindow.getInstance());
        }
    }

    /**
    * Button press - Put message(s) back to draft if we delete them here
    */
    private class Action_Delete extends MenuAction {
        public Action_Delete() {
            super(LanguageManager.getString("DELETE"), ColloquiaConstants.iconDelete);
            setButtonText(LanguageManager.getString("BUT10"));
        }

        public void actionPerformed(ActionEvent e) {
            setMessagesToDraft();
        }
    }

}
