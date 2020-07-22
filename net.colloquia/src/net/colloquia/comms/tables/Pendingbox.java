package net.colloquia.comms.tables;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;

import javax.swing.*;
import javax.swing.event.*;

import net.colloquia.*;
import net.colloquia.comms.*;
import net.colloquia.comms.index.*;
import net.colloquia.comms.messages.*;
import net.colloquia.io.*;
import net.colloquia.menu.*;
import net.colloquia.util.*;
import net.colloquia.xml.*;

/**
 *
 */
public class Pendingbox extends MessageBox {
    private MessageInfo selectedMessage;
    //private EMailViewer emailViewer;

    // Toolbar stuff
    private Action_Delete actionDelete;

    private static Pendingbox instance = new Pendingbox();

    private Pendingbox() {
        super(new PendingboxTableModel());

        // Set the default cell renderer for any type object
        cellRenderer = new InboxTableRenderer();
        table.setDefaultRenderer(Object.class, cellRenderer);

        // PENDING!
        //emailViewer = new EMailViewer(MessageWindow.getInstance());

        // Column widths
        tableColumnModel.getColumn(0).setPreferredWidth(25);
        tableColumnModel.getColumn(0).setMaxWidth(25);
        tableColumnModel.getColumn(1).setPreferredWidth(18);
        tableColumnModel.getColumn(1).setMaxWidth(18);
        tableColumnModel.getColumn(2).setPreferredWidth(120);
        tableColumnModel.getColumn(3).setPreferredWidth(50);
        tableColumnModel.getColumn(4).setPreferredWidth(100);
        tableColumnModel.getColumn(5).setPreferredWidth(50);

        JScrollPane sp = new JScrollPane(table);
        sp.setColumnHeaderView(tableHeader);

        add(sp);

        // PENDING!
        //PSplitPane mainSplit = new PSplitPane(JSplitPane.VERTICAL_SPLIT);
        //mainSplit.setTopComponent(sp);
        //mainSplit.setBottomComponent(emailViewer);
        //add(mainSplit, BorderLayout.CENTER);
        //mainSplit.setDividerLocation(200);
    }

    public static Pendingbox getInstance() {
        return instance;
    }

    /**
    * Initialise and make the toolbar
    */
    protected void initToolBar() {
        // The Key Mask will be 'Meta' for Mac and 'Ctrl' for PC/Unix
        int keyMask = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
      	JMenu menu = menuBar.add(new JMenu(LanguageManager.getString("16_10")));
        panel.add(menuBar, BorderLayout.NORTH);
        panel.add(toolBar, BorderLayout.CENTER);

        // Delete button
        actionDelete = new Action_Delete(false);
        toolBar.add(actionDelete, false);
        JMenuItem item = menu.add(actionDelete);
        item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0));

        add(panel, BorderLayout.NORTH);
    }


    protected void jumpToMessage(MessageInfo mInfo) {}

    /**
    * Attempt to file the messages
    * Returns a vector of filed messages (empty if none filed)
    * This is called from Inbox
    */
    public Vector fileMessages() {
        Vector filed = new Vector();

        MessageIndex index = model.getMessageIndex();
        if(index == null) return filed;

        // We have to do Activities first because other pending messages depend
        // on it being there.  So we'll sort the MessagInfos
        MessageInfo[] mInfos = new MessageInfo[index.size()];
        int count = 0;
        for(int i = 0; i < index.size(); i++) {
            MessageInfo mInfo = index.getMessageAt(i);
            if(mInfo.getMessageType() == MessageInfo.ACTIVITY) mInfos[count++] = mInfo;
        }
        for(int i = 0; i < index.size(); i++) {
            MessageInfo mInfo = index.getMessageAt(i);
            if(mInfo.getMessageType() != MessageInfo.ACTIVITY) mInfos[count++] = mInfo;
        }

        // Now we'll attempt to file them
        for(int i = 0; i < mInfos.length; i++) {
            MessageInfo mEntry = mInfos[i];
            File file = new File(DataFiler.getPendingMailFolder(true) + mEntry.getFileName());
            MessageIn messageIn = MessageIn.getMessageIn(file);
            if(messageIn != null) {
                MessageInfo mInfo;
                try {
                    mInfo = messageIn.fileMessage(true);
                }
                catch(ColloquiaFileException ex) {
                    System.out.println("Error filing Pending file: " + ex);
                    continue;
                }
                if(mInfo != null && mInfo.getState() == MessageInfo.RCVD) {
                    try {
                        model.deleteMessage(mEntry);      // Delete local entry
                    }
                    catch(XMLWriteException ex) {
                        ErrorHandler.showWarning("ERR10", ex, "ERR");
                    }
                    filed.addElement(mInfo);          // Save for Inbox
                }
            }
        }

        updateView();
        return filed;
    }


    /**
    * Add a message to the pending bunch
    */
    public void addMessage(MessageInfo mInfo) throws XMLWriteException {
        model.addMessage(mInfo, false);
    }


    /**
    * A row has been selected
    */
    public void valueChanged(ListSelectionEvent e) {
        updateView();
    }


    private void updateView() {
        MessageInfo mInfo = getSelectedMessage();

        // Delete button
      	actionDelete.setEnabled(mInfo == null ? false : true);

        // Display the message
        // PENDING - have to get the message out of the cqm zip file
        // in the Pending folder! (And any attachments!)
        //if(mInfo != selectedMessage) emailViewer.showMessage(mInfo);

        selectedMessage = mInfo;
    }


    /**
    * Delete one or more messages
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
        updateView();
        setCursor(ColloquiaConstants.defaultCursor);
    }

    private class Action_Delete extends MenuAction {
        public Action_Delete(boolean type) {
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
