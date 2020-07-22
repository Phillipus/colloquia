package net.colloquia.comms.tables;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import javax.swing.table.*;

import net.colloquia.*;
import net.colloquia.comms.*;
import net.colloquia.comms.editors.*;
import net.colloquia.gui.widgets.*;
import net.colloquia.menu.*;
import net.colloquia.util.*;
import net.colloquia.views.*;
import net.colloquia.views.tree.*;
import net.colloquia.xml.*;

/**
 * Panel to display a list of e-mail messages for Single Messages or Group messages
 * Or Assignment Messages
 */
public abstract class MessageTablePanel
extends JPanel
implements ListSelectionListener, TableModelListener
{
    protected JTable table;
    protected MessageTableModel model;
    protected JTableHeader tableHeader;
    protected EMailViewer emailViewer;
    protected MessageTableRenderer tableRenderer;
    protected MessageTableHeaderRenderer headerRenderer;
    protected MessageTableClickHandler headerClickHandler;
    protected ClickHandler clickHandler;

    // Toolbar stuff
    protected JMenuBar menuBar;
    protected ColloquiaToolBar toolBar;

    protected Action_Compose actionCompose;
    protected Action_Edit actionEdit;
    protected Action_Reply actionReply;
    protected Action_Resend actionResend;
    protected Action_Delete actionDelete;
    protected Action_ViewAttachment actionViewAttachment;
    protected Action_Print actionPrint;

    protected PLabel title;

    // The last message number selected
    protected int lastMessageSelected = -1;

    public MessageTablePanel(EMailViewer emailViewer) {
        this.emailViewer = emailViewer;
        setLayout(new BorderLayout());

        table = new JTable();

        // Selection model and selection listener
        ListSelectionModel lsm = table.getSelectionModel();
        lsm.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        lsm.addListSelectionListener(this);

        // Listen to mouse-clicks on the table
        clickHandler = new ClickHandler();
        table.addMouseListener(clickHandler);

        // ToolBar
        initToolBar();
    }


    private void initToolBar() {
        // The Key Mask will be 'Meta' for Mac and 'Ctrl' for PC/Unix
        int keyMask = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        menuBar = new JMenuBar();
      	JMenu menu = menuBar.add(new JMenu(LanguageManager.getString("16_10")));
        panel.add(menuBar, BorderLayout.NORTH);
        toolBar = new ColloquiaToolBar();
        panel.add(toolBar, BorderLayout.CENTER);

        // Compose button
        actionCompose = new Action_Compose();
        toolBar.add(actionCompose);
        JMenuItem item = menu.add(actionCompose);

        // Edit button
        actionEdit = new Action_Edit();
        toolBar.add(actionEdit);
        item = menu.add(actionEdit);
        item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E, keyMask));

        // Reply button
        actionReply = new Action_Reply();
        toolBar.add(actionReply, false);
        item = menu.add(actionReply);

        // Resend button
        actionResend = new Action_Resend();
        toolBar.add(actionResend, false);
        item = menu.add(actionResend);

        // View Attachment button
        actionViewAttachment = new Action_ViewAttachment();
        toolBar.add(actionViewAttachment, false);
        item = menu.add(actionViewAttachment);

        // Print button
        actionPrint = new Action_Print();
        toolBar.add(actionPrint, false);
        item = menu.add(actionPrint);

        toolBar.addSeparator();
        menu.addSeparator();

        // Delete button
        actionDelete = new Action_Delete();
        toolBar.add(actionDelete, false);
        item = menu.add(actionDelete);
        // conflicts with Del on tree
        item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, KeyEvent.SHIFT_MASK));

        title = new PLabel("", ColloquiaConstants.boldFont13);
        title.setBorder(new EmptyBorder(0, 5, 0, 0));
        title.setForeground(Color.blue);
        panel.add(title, BorderLayout.SOUTH);

        add(panel, BorderLayout.NORTH);
    }

    /**
    * A row has been selected - this is triggered TWICE by the event.
    * Once for the row unselected and second for the new row, so we check for this
    */
    public void valueChanged(ListSelectionEvent e) {
        if(!e.getValueIsAdjusting()) updateView();
        // Store last message selected
        if(getTable().getSelectedRow() != -1) lastMessageSelected = getTable().getSelectedRow();
    }

    /**
    * This is triggered for the TableModelListener and has to be included for
    * when the underlying data changes (new People view, new Tree).
    */
    public void tableChanged(TableModelEvent e) {
        updateView();
    }

    /**
    * Update the buttons and display the message
    */
    protected void updateView() {
        MessageInfo mInfo = getSelectedMessage();

        // Edit button
        actionEdit.setEnabled(mInfo != null);

        // Reply button
        actionReply.setEnabled(mInfo == null ? false : !mInfo.isFromMe());

        // Resend button
        actionResend.setEnabled(mInfo == null ? false : mInfo.isFromMe() && mInfo.getState() == MessageInfo.SENT);

        // View Attachment
        actionViewAttachment.setEnabled(mInfo == null ? false : mInfo.hasAttachment());

        // Print button
        actionPrint.setEnabled(mInfo != null);

        // Delete button
      	actionDelete.setEnabled(mInfo != null);

        // Set seen flag if not already set
        if(mInfo != null && !mInfo.isSeen()) {
        	model.setSeen(mInfo, true);
            ColloquiaTree.getInstance().updateSelectedNode();
        }

        // Display the message
        emailViewer.showMessage(mInfo);
    }

    /**
    * Get the selected message or null if none selected
    */
    public MessageInfo getSelectedMessage() {
        int row = table.getSelectedRow();
        int numRows = model.getRowCount();

        // No row selected
        if(row == -1 || numRows == 0) return null;
        // Get the Message
        else return (MessageInfo)model.getValueAt(row, 0);
    }

    /**
    * Select a specific message on the table
    * This is called externally from INBOX/OUTBOX to jump to a specific message
    * In this message box
    */
    public void selectMessage(MessageInfo mInfo) {
        if(mInfo == null) return;

        // Now find it & scroll to it
        for(int i = 0; i < table.getRowCount(); i++) {
            MessageInfo mi = (MessageInfo)model.getValueAt(i, 0);
            if(mi.matches(mInfo)) {
                Rectangle bounds = table.getCellRect(i, 0, true);
                table.scrollRectToVisible(bounds);
                table.setRowSelectionInterval(i, i);     // Do this twice!!!
                table.setRowSelectionInterval(i, i);
                return;
            }
        }
    }

    /**
     * Select a message in the Table by its message number
     * 2002-10-18
     */
    public void selectMessage(int msgNumber) {
        // If selection is greater than number of messages select last message
        if(msgNumber >= model.getRowCount()) msgNumber = model.getRowCount() - 1;
        // Now check for no messages, or no selection
        if(msgNumber < 0) return;
        Rectangle bounds = table.getCellRect(msgNumber, 0, true);
        table.scrollRectToVisible(bounds);
        table.setRowSelectionInterval(msgNumber, msgNumber);
        table.setRowSelectionInterval(msgNumber, msgNumber);
    }

    /**
     * Mark a message as "Unread"
     */
    public void markMessage() {
		MessageInfo mInfo = getSelectedMessage();
        if(mInfo != null && !mInfo.isFromMe()) {
        	model.setSeen(mInfo, !mInfo.isSeen());
        	table.repaint();
        	ColloquiaTree.getInstance().updateSelectedNode();
        }
    }

    public MessageTableModel getModel() { return model; }
    public JTable getTable() { return table; }


    protected class Action_Compose extends MenuAction {
        public Action_Compose() {
            super(LanguageManager.getString("16_21"), ColloquiaConstants.iconNewMessage,
                MainFrame.getInstance().statusBar);
            setButtonText(LanguageManager.getString("BUT21"));
        }

        public void actionPerformed(ActionEvent e) {
            composeNewMessage();
        }
    }

    protected abstract void composeNewMessage();

    protected class Action_Reply extends MenuAction {
        public Action_Reply() {
            super(LanguageManager.getString("16_22"), ColloquiaConstants.iconReply,
                MainFrame.getInstance().statusBar);
            setButtonText(LanguageManager.getString("BUT22"));
        }

        public void actionPerformed(ActionEvent e) {
            replyToMessage();
        }
    }

    protected abstract void replyToMessage();


    protected class Action_Resend extends MenuAction {
        public Action_Resend() {
            super(LanguageManager.getString("16_113"), ColloquiaConstants.iconSendMessage);
            setButtonText(LanguageManager.getString("BUT31"));
        }

        public void actionPerformed(ActionEvent e) {
            resendMessage();
        }
    }

    protected abstract void resendMessage();

    protected class Action_ViewAttachment extends MenuAction {
        public Action_ViewAttachment() {
            super(LanguageManager.getString("14_11"), ColloquiaConstants.iconAttach);
            setButtonText(LanguageManager.getString("BUT19"));
        }

        public void actionPerformed(ActionEvent e) {
            showAttachments();
        }
    }

    /*
    * Show the Attachments View
    */
    protected void showAttachments() {
        MessageInfo mInfo = getSelectedMessage();
        if(mInfo != null) new AttachmentsDialog(MainFrame.getInstance(), mInfo, false);
    }

    protected class Action_Print extends MenuAction {
        public Action_Print() {
            super(LanguageManager.getString("PRINT"), ColloquiaConstants.iconPrint,
                MainFrame.getInstance().statusBar);
            setButtonText(LanguageManager.getString("BUT23"));
        }

        public void actionPerformed(ActionEvent e) {
            printMessage();
        }
    }

    protected void printMessage() {
        emailViewer.printMessage();
    }

    protected class Action_Edit extends MenuAction {
        public Action_Edit() {
            super(LanguageManager.getString("16_23"), ColloquiaConstants.iconEditMessage,
                MainFrame.getInstance().statusBar);
            setButtonText(LanguageManager.getString("BUT13"));
        }

        public void actionPerformed(ActionEvent e) {
            editMessage();
        }
    }

    protected void editMessage() {
        MessageInfo mInfo = getSelectedMessage();
        if(mInfo == null) return;

        // Get the state of the message
        int messageState = mInfo.getState();
        switch(messageState) {
            // View the message in a window
            case MessageInfo.SENT:
            case MessageInfo.RCVD:
            case MessageInfo.UNMATCHED:
                EMailFloatViewer.show(mInfo);
                break;

            // Re-edit the message
            case MessageInfo.DRAFT:
                EMailEditor.editMessage(mInfo);
                break;

            // If the message state is PENDING we have to set it back
            // To draft and remove it from the Outbox
            case MessageInfo.PENDING:
                model.setState(mInfo, MessageInfo.DRAFT);
                try {
                    Outbox.getInstance().deleteMessage(mInfo);
                }
                catch(XMLWriteException ex) {
                    ErrorHandler.showWarning("ERR10", ex, "ERR");
                }
                EMailEditor.editMessage(mInfo);
                break;
        }
    }

    protected class Action_Delete extends MenuAction {
        public Action_Delete() {
            super(LanguageManager.getString("DELETE"), ColloquiaConstants.iconDelete,
                MainFrame.getInstance().statusBar);
            setButtonText(LanguageManager.getString("BUT10"));
        }

        public void actionPerformed(ActionEvent e) {
            try {
                deleteMessages();
            }
            catch(Exception ex) {
                ErrorHandler.showWarning("ERR5", ex, "ERR");
            }
        }
    }


    /**
    * Manually Delete one or more messages
    */
    private void deleteMessages() throws XMLWriteException {
        // Check we wanna do this!
        int result = JOptionPane.showConfirmDialog
            (MainFrame.getInstance(),
            LanguageManager.getString("16_16"),
            LanguageManager.getString("16_17"),
            JOptionPane.YES_NO_OPTION);
        if(result != JOptionPane.YES_OPTION) return;

        // Delete selected
        MainFrame.getInstance().setCursor(ColloquiaConstants.waitCursor);
        model.deleteMessages(table.getSelectedRows());
        updateView();
        MainFrame.getInstance().setCursor(ColloquiaConstants.defaultCursor);
    }


    /**
    * Trap double-clicks on the table so we can either launch the e-mail
    * composer or the viewer.
    */
    protected class ClickHandler extends MouseAdapter {

        public void mouseClicked(MouseEvent e) {
            Component source = e.getComponent();
            if(source != table) return;
            int clicks = e.getClickCount();
            // Double-Click on message in table - launch Viewer or Editor
            if(clicks == 2) editMessage();
            // Single-Click on mark
            if(clicks == 1 && table.getSelectedColumn() == 2) markMessage();
		}
    }

    public Dimension getPreferredSize() {
        return new Dimension(260, 220);
    }

    public Dimension getMinimumSize() {
        return new Dimension(0, 0);
    }
}