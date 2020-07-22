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
 * The SENTBOX
 *
 *
 */
public class Sentbox
extends MessageBox
implements TableModelListener
{
    // Toolbar stuff
    private Action_Delete actionDelete;
    private Action_GotoMessage actionJump;

    private static Sentbox instance = new Sentbox();

    private Sentbox() {
        super(new SentboxTableModel());

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
        cellRenderer = new SentboxTableRenderer();
        table.setDefaultRenderer(Object.class, cellRenderer);

        JScrollPane sp = new JScrollPane(table);
        sp.setColumnHeaderView(tableHeader);
        add(sp, BorderLayout.CENTER);

        // Listen to table data changes
        model.addTableModelListener(this);

        setPreferredSize(new Dimension(200, 200));
        setMinimumSize(new Dimension(0, 0));
    }

    public static Sentbox getInstance() {
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

        // Jump button (equivalent) to double-clicking
        actionJump = new Action_GotoMessage();
        toolBar.add(actionJump, false);
        JMenuItem item = menu.add(actionJump);

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


    /**
    * Adds a Message
    */
    public void addMessage(MessageInfo mInfo) {
        if(mInfo == null) return;

        // Save the message
        try {
            model.addMessage(mInfo, false);
        }
        catch(XMLWriteException ex) {
            ErrorHandler.showWarning("ERR8", ex, "ERR");
        }
    }


    /**
    * Put back to draft and jump to a message to edit it when user d-clicks on it
    */
    protected void jumpToMessage(MessageInfo mInfo) {
        Activity A;
        Person person;
        if(mInfo == null) return;
        table.setCursor(ColloquiaConstants.waitCursor);

        // Select the node to jump to
        // The TreeModel will Select the Node then the TVA will select the view
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
        if(cv instanceof MessageView) ((MessageView)cv).selectMessage(mInfo);

        table.setCursor(ColloquiaConstants.defaultCursor);
        if((MainFrame.getInstance().getExtendedState() & JFrame.ICONIFIED) == 1) {
            MainFrame.getInstance().setState(JFrame.NORMAL);
        }
        MainFrame.getInstance().toFront();
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
            actionJump.setEnabled(false);
        }
      	else {
            actionDelete.setEnabled(true);
            actionJump.setEnabled(true);
        }

        fireTableChanged(model.getMessageIndex());
    }

    /**
    * Manually Delete one or more messages
    */
    private void deleteMessages() throws XMLWriteException {
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

    private class Action_GotoMessage extends MenuAction {
        public Action_GotoMessage() {
            super(LanguageManager.getString("16_20"), ColloquiaConstants.iconForward);
            setButtonText(LanguageManager.getString("BUT8"));
        }

        public void actionPerformed(ActionEvent e) {
            jumpToMessage(getSelectedMessage());
        }
    }

    private class Action_Delete extends MenuAction {
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