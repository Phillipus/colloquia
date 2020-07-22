package net.colloquia.comms.tables;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.event.*;

import net.colloquia.*;
import net.colloquia.comms.*;
import net.colloquia.datamodel.*;
import net.colloquia.datamodel.entities.*;
import net.colloquia.gui.widgets.*;
import net.colloquia.menu.*;
import net.colloquia.util.*;
import net.colloquia.views.*;
import net.colloquia.views.entities.*;
import net.colloquia.views.tree.*;
import net.colloquia.xml.*;

/**
 * The READBOX
 */
public class Readbox
extends MessageBox
{
    // Toolbar stuff
    private Action_GotoMessage actionGotoMessage;
    private Action_Delete actionDelete;

    private static Readbox instance = new Readbox();

    private Readbox() {
        super(new ReadboxTableModel());

        // Column widths
        tableColumnModel.getColumn(0).setPreferredWidth(25);
        tableColumnModel.getColumn(0).setMaxWidth(25);
        tableColumnModel.getColumn(1).setPreferredWidth(18);
        tableColumnModel.getColumn(1).setMaxWidth(18);
        tableColumnModel.getColumn(2).setPreferredWidth(120);
        tableColumnModel.getColumn(3).setPreferredWidth(50);
        tableColumnModel.getColumn(4).setPreferredWidth(100);
        tableColumnModel.getColumn(5).setPreferredWidth(50);

        // Set the default cell renderer for any type object
        cellRenderer = new InboxTableRenderer();
        table.setDefaultRenderer(Object.class, cellRenderer);

        JScrollPane sp = new JScrollPane(table);
        sp.setColumnHeaderView(tableHeader);
        add(sp, BorderLayout.CENTER);

        setPreferredSize(new Dimension(200, 300));
        setMinimumSize(new Dimension(0, 0));
    }


    public static Readbox getInstance() {
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

        // Goto message
        actionGotoMessage = new Action_GotoMessage();
        toolBar.add(actionGotoMessage, false);
        JMenuItem item = menu.add(actionGotoMessage);

        toolBar.addSeparator();
        menu.addSeparator();

        // Delete button
        actionDelete = new Action_Delete();
        toolBar.add(actionDelete, false);
        item = menu.add(actionDelete);
        item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0));

        // Message
        toolBar.addSeparator();
        toolBar.add(new PLabel(LanguageManager.getString("16_109"), ColloquiaConstants.color1));

        add(panel, BorderLayout.NORTH);
    }

    /*
    */
    public void addMessage(MessageInfo mInfo) throws XMLWriteException {
        model.addMessage(mInfo, false);
    }

    /**
    * Jump to a node & message when user d-clicks on a row
    * Same as for INBOX
    */
    protected void jumpToMessage(MessageInfo mInfo) {
        Activity A;
        Person person;

        // Get the Message
        if(mInfo == null) return;

        table.setCursor(ColloquiaConstants.waitCursor);
        MainFrame.getInstance().setCursor(ColloquiaConstants.waitCursor);

        ColloquiaTree tree = ColloquiaTree.getInstance();

        // Now find out which node to jump to
        switch(mInfo.getMessageType()) {
            // Activity / Group Message - jump to the Activity node
            case MessageInfo.ACTIVITY:
            case MessageInfo.ACTIVITY_INVITE:
            case MessageInfo.ACTIVITY_COMPLETE:
            case MessageInfo.ACTIVITY_REACTIVATE:
            case MessageInfo.GROUP_MESSAGE:
                A = (Activity)DataModel.getComponent(mInfo.getActivityID());
                tree.selectNodeByObject(A);
                break;

            // A Single Message - go to the person who sent it (in the LA)
            case MessageInfo.SINGLE_MESSAGE:
            case MessageInfo.ACTIVITY_ACCEPT:
                A = (Activity)DataModel.getComponent(mInfo.getActivityID());
                person = (Person)DataModel.getComponent(mInfo.getPersonID());
                if(person == null) tree.selectNodeByObject(A);
                else tree.selectNode(person, A);
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

            // A person
            case MessageInfo.PERSON:
                person = (Person)DataModel.getComponent(mInfo.getPersonID());
                tree.selectNodeByObject(person);
                break;

            // A Resource
            case MessageInfo.RESOURCE:
                Resource resource = (Resource)DataModel.getComponent(mInfo.getComponentID());
                tree.selectNodeByObject(resource);
                break;
        }

        // Get the current view so we can switch to the right tab and message
        // But only for actual text messages
        switch(mInfo.getMessageType()) {
            case MessageInfo.GROUP_MESSAGE:
            case MessageInfo.SINGLE_MESSAGE:
            case MessageInfo.ASSIGNMENT_MESSAGE:
    	    	ColloquiaView cv = ViewPanel.getInstance().getCurrentView();
	    	    if(cv instanceof MessageView) ((MessageView)cv).selectMessage(mInfo);
                break;
        }

        table.setCursor(ColloquiaConstants.defaultCursor);
        MainFrame.getInstance().setCursor(ColloquiaConstants.defaultCursor);
        if((MainFrame.getInstance().getExtendedState() & JFrame.ICONIFIED) == 1) {
            MainFrame.getInstance().setState(JFrame.NORMAL);
        }
        MainFrame.getInstance().toFront();
    }

    /**
    * Manually Delete one or more messages
    */
    protected void deleteMessages() throws XMLWriteException {
        // Check we wanna do this!
        int result = JOptionPane.showConfirmDialog
            (this,
            LanguageManager.getString("16_16"),
            LanguageManager.getString("16_17"),
            JOptionPane.YES_NO_OPTION);
        if(result != JOptionPane.YES_OPTION) return;

        // Delete selected
        setCursor(ColloquiaConstants.waitCursor);
        model.deleteMessages(table.getSelectedRows());
        updateButtons();
        setCursor(ColloquiaConstants.defaultCursor);
    }

    /*
    * This is called from another index
    */
    public void deleteMessages(MessageInfo[] mInfo) throws XMLWriteException {
        model.deleteMessages(mInfo);
    }

    /*
    * This is called from another index
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

    protected void updateButtons() {
        int row = table.getSelectedRow();
        int numRows = model.getRowCount();
        boolean show = (row != -1 && numRows != 0);
        actionDelete.setEnabled(show);
      	actionGotoMessage.setEnabled(show);
        fireTableChanged(model.getMessageIndex());
    }


    // ****************** BUTTONS ******************** \\

    protected class Action_GotoMessage extends MenuAction {
        public Action_GotoMessage() {
            super(LanguageManager.getString("16_20"), ColloquiaConstants.iconForward);
            setButtonText(LanguageManager.getString("BUT8"));
        }

        public void actionPerformed(ActionEvent e) {
            jumpToMessage(getSelectedMessage());
        }
    }

    protected class Action_Delete extends MenuAction {
        public Action_Delete() {
            super(LanguageManager.getString("DELETE"), ColloquiaConstants.iconDelete);
            setButtonText(LanguageManager.getString("BUT10"));
        }

        public void actionPerformed(ActionEvent e) {
            try {
                deleteMessages();
            }
            catch(XMLWriteException ex) {
                ErrorHandler.showWarning("ERR10", ex, "ERR");
            }
        }
    }

}
