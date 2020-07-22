package net.colloquia.comms.editors;

import java.awt.*;
import java.awt.event.*;
import java.util.*;

import javax.swing.*;

import net.colloquia.*;
import net.colloquia.comms.*;
import net.colloquia.comms.index.*;
import net.colloquia.comms.tables.*;
import net.colloquia.datamodel.*;
import net.colloquia.datamodel.entities.*;
import net.colloquia.gui.*;
import net.colloquia.gui.widgets.*;
import net.colloquia.io.*;
import net.colloquia.menu.*;
import net.colloquia.util.*;
import net.colloquia.views.*;
import net.colloquia.views.entities.*;
import net.colloquia.xml.*;


/**
 * Edits Messages for SINGLE MESSAGE & GROUP MESSAGES & ASSIGNMENT MESSAGES
 */
public abstract class EMailEditor
extends HTMLEditor
implements ChildWindow
{
    protected JFrame frame;
    protected CloseListener closeListener;
    protected KeyPressListener keyPressListener;
    protected JToolBar extraBar;
    protected PTextField subjectBox;
    protected JMenu fileMenu;

    protected MessageInfo mInfo;
    protected String fileName;

    protected boolean dirty;

    final static boolean ASYNCHRONOUS = true;

    // Instances of the edit session
    protected static Hashtable instances = new Hashtable();

    public EMailEditor() {
    	super(true);
    }


    /**
     * Query whether we have an instance of the Editor open with a specific
     * Message.  If we do, return a handle to this window so we can focus it, else null.
     */
    protected static JFrame getWindow(String fileName) {
        return (JFrame)instances.get(fileName);
    }

    protected static void registerWindow(String fileName, JFrame frame) {
        if(fileName != null) instances.put(fileName, frame);
    }

    protected static void unregisterWindow(String fileName) {
        if(fileName != null) instances.remove(fileName);
    }

    /**
     * Factory method for getting an Editor and loading it with mInfo to edit
     * We firstly check to see if we already have it open
     */
    public static void editMessage(MessageInfo mInfo) {
        if(mInfo == null) return;

        // See if we have it
        JFrame win = getWindow(mInfo.getFullFileName());
        if(win != null) {
            win.toFront();
            win.requestFocus();
            return;
        }

        // No - factory
        switch(mInfo.getMessageType()) {
            case MessageInfo.SINGLE_MESSAGE:
                new SingleMessageEditor(mInfo);
                break;

            case MessageInfo.ASSIGNMENT_MESSAGE:
                new AssignmentMessageEditor(mInfo);
                break;

            case MessageInfo.GROUP_MESSAGE:
                new GroupMessageEditor(mInfo);
                break;

            default:
                return;
        }
    }

    /**
     * Constructor for editing existing message
     */
    protected void loadMessage(MessageInfo mInfo) {
        init();
        // Get this message
        this.mInfo = mInfo;
        fileName = mInfo.getFullFileName();
        // Load it into the editor
        loadFile(fileName, true, ASYNCHRONOUS);
        // Set subject
        setSubject(mInfo.getSubject());
        // Register the window as open
        registerWindow(fileName, frame);
        // Put caret at top
        editor.setCaretPosition(0);
    }


    /**
     * Set things up
     */
    protected void init() {
        // Add to list of opened windows
        MainFrame.addchildWindow(this);

         // Enclose this in a frame
        frame = new JFrame();
        frame.getContentPane().add(this);

        // Title Bar and Icon
        frame.setIconImage(getFrameIcon());
        frame.setTitle(getFrameTitle());

        // Add a Close Window listener
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        closeListener = new CloseListener();
        frame.addWindowListener(closeListener);

        // Add some extra menu items
        fileMenu = new JMenu(LanguageManager.getString("FILE"));
        editMenuBar.add(fileMenu, 0);

        // Add an extra toolBar
        extraBar = new JToolBar();
        extraBar.setFloatable(false);
        editMenuPanel.add(extraBar, BorderLayout.SOUTH);

        // Add Subject box to toolBar
        extraBar.add(new JToolBar.Separator());
        extraBar.add(new PLabel(LanguageManager.getString("14_1")));
        extraBar.add(new JToolBar.Separator());
        subjectBox = new PTextField("");
        extraBar.add(subjectBox);
        // Add a key listener to the subject box
        keyPressListener = new KeyPressListener();
        subjectBox.addKeyListener(keyPressListener);

        // The Key Mask will be 'Meta' for Mac and 'Ctrl' for PC/Unix
        int keyMask = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();

        // Save as Draft
        Action_SaveDraftMessage actionSave = new Action_SaveDraftMessage();
        JMenuItem item = fileMenu.add(actionSave);
        item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, keyMask));
        editToolBar.add(actionSave, 0);

        // Put in Outbox
        Action_Outbox actionOutbox = new Action_Outbox();
        fileMenu.add(actionOutbox);
        editToolBar.add(actionOutbox, 1);

        // Send
        Action_SendMessage actionSend = new Action_SendMessage();
        fileMenu.add(actionSend);
        editToolBar.add(actionSend, 2);

        // Attachments
        Action_Attachment actionAttachment = new Action_Attachment();
        fileMenu.add(actionAttachment);
        editToolBar.add(actionAttachment, 3);

        // Exit
        fileMenu.addSeparator();
        Action_Exit actionExit = new Action_Exit();
        fileMenu.add(actionExit);

        editToolBar.add(new JToolBar.Separator(new Dimension(60, 20)), 4);

        // Remove View button
        editToolBar.remove(actionView);
        editMenuBar.remove(viewMenu);

        int width = Utils.getScreenWidth()/3 * 2;
        int height = Utils.getScreenHeight()/2;
        Utils.centreWindow(frame, width, height);
        frame.setVisible(true);
    }

    protected abstract Image getFrameIcon();
    protected abstract String getFrameTitle();


    /**
     * Set the subject
     */
    public void setSubject(String subject) {
        subjectBox.setText(subject);
    }

    /**
     * Set the subject - with Re:
     */
    protected void setReSubject(String subject) {
        String re = "re:";

        // Remove any previous re:s
        subject = subject.trim();
        while(subject.toLowerCase().startsWith(re))
                subject = subject.substring(re.length());

        subjectBox.setText("Re: " + subject.trim());
    }

    /**
     * Set the original quoted message
     * mInfo is the message we're replying to
     */
    protected void setQuoteMessage(MessageInfo mInfo) {
        // Get message this refers to
        String fileName = mInfo.getFullFileName();
        // Re:
        String from = mInfo.getFrom();
        Person person = DataModel.getPersonByEmailAddress(from);
        if(person != null) from = person.getName();
        String quote = "<p></p><p><i>" + from + " " + LanguageManager.getString("14_5") + "</i></p>";

        // Load it into the editor, **synchronously**
        loadFile(fileName, true, false);

        //Action a = new HTMLEditorKit.InsertHTMLTextAction(
        //    "Action Name", "<p><i>plooper</i></p>", HTML.Tag.BODY, HTML.Tag.P);
        //a.actionPerformed(new ActionEvent(editor, 0, null));

        // Add a bit
        int offset = 1;  // !!!!!!!!!!
        insertHTMLText(offset, quote);

        editor.setCaretPosition(offset);
        editor.grabFocus();

        dirty = false; // (because inserting text makes it true)
    }

    /**
     * Closing the window...
     */
    protected class CloseListener extends WindowAdapter {
        public void windowClosing(WindowEvent e) {
            close();
        }
    }

    /**
     * The Message editor is being closed - check whether we should
     * 1. Store as a draft Message
     * 2. Put in the OutBox
     * 3. Send now
     * 3. Abandon the Message
     * 4. Resume the Message
     */
    public void close() {
        int val;

        String[] options = {
            LanguageManager.getString("SAVE"),
            LanguageManager.getString("OUTBOX"),
            LanguageManager.getString("SEND"),
            LanguageManager.getString("ABANDON"),
            LanguageManager.getString("RESUME")
        };

        if(dirty) {
            val = JOptionPane.showOptionDialog(this, LanguageManager.getString("14_6"),
                     LanguageManager.getString("14_7"), JOptionPane.OK_CANCEL_OPTION, JOptionPane.DEFAULT_OPTION,
                     Utils.getIcon(ColloquiaConstants.iconAppIcon), options, options[0]);
        }
        else val = -1;

        switch(val) {
            // X Close
            case -1:
                dispose();
                break;
            // Draft
            case 0:
                try {
                    saveMessage();
                }
                catch(ColloquiaFileException ex) {
                    ErrorHandler.showWarning(this, "ERR14", ex, "ERR");
                    return;
                }
                dispose();
                break;
            // Outbox
            case 1:
                try {
                    sendMessage(false);
                }
                catch(ColloquiaFileException ex) {
                    ErrorHandler.showWarning(this, "ERR14", ex, "ERR");
                    return;
                }
                dispose();
                break;
            // Send now
            case 2:
                try {
                    sendMessage(true);
                }
                catch(ColloquiaFileException ex) {
                    ErrorHandler.showWarning(this, "ERR14", ex, "ERR");
                    return;
                }
                dispose();
                break;
            // Abandon
            case 3:
                dispose();
                break;
            // Resume
            case 4:
                // Do nothing
                break;
        }
    }

    /**
     * Close this window:
     * Remove from list in MainFrame, remove listener here and remove
     * from Hashtable of instances.
     */
    public void dispose() {
        MainFrame.removeChildWindow(this);
        frame.removeWindowListener(closeListener);
        if(fileName != null) unregisterWindow(fileName);

        extraBar.removeAll();
        extraBar = null;
        subjectBox.removeKeyListener(keyPressListener);
        subjectBox = null;
        fileMenu.removeAll();

        super.dispose();

        frame.dispose();
    }

    protected void editHappened() {
        dirty = true;
    }

    /**
     * Saves the current message as draft
     * It will then have a fileName if not already
     */
    protected void saveMessage() throws ColloquiaFileException {
        updateMessageInfo();

        // Do we have a fileName?
        // No, so get a new one
        if(fileName == null) {
            // Get the folder to save the document in
            String folder = mInfo.getFolderForMessage(true);
            String shortName = Utils.generateHTMLMessageFileName(folder);
            String longName = folder + shortName;
            mInfo.setFileName(shortName);
            // Now that we have saved it and it is in the table
            // we have to register it as an opened window
            registerWindow(longName, frame);
            fileName = longName;
        }

        // Save the HTMLdocument
        HTMLEditor.saveHTMLFile(fileName, editor.getDocument());

        // Discover the Message Index
        MessageIndex index = MessageIndex.getMessageIndex(mInfo, true);

        // Add an entry to the index
        index.addMessage(mInfo, true);

        // Re-Save the index as an XML file
        try {
            index.save();
        }
        catch(XMLWriteException ex) {
            ErrorHandler.showWarning(this, "ERR6", ex, "ERR");
        }

        // We now have to tell the current view so that this message shows up
        // In the underlying table if it is on display
        updateView();

        dirty = false;
    }

    /**
     * Update the MessageInfo
     */
    protected void updateMessageInfo() {
        // Subject
        String subject = subjectBox.getText();
        if(subject.trim().equals("")) subject = LanguageManager.getString("14_17");
        mInfo.setSubject(subject);
    }

    /**
     * Send the message by putting it in the Outbox
     */
    protected void sendMessage(boolean sendNow) throws ColloquiaFileException {
        // Set to pending
        mInfo.setState(MessageInfo.PENDING);

        // Save (we might not have done so yet even if not dirty)
        saveMessage();

        // Send to the Outbox
        Outbox.getInstance().addMessage(frame, mInfo, sendNow);
    }


    /*
     * Show the Attachments View
     */
    protected void showAttachments() {
        new AttachmentsDialog(frame, mInfo, true);
        dirty = true;
    }

    /**
     * A key listener for the subject box
     */
    protected class KeyPressListener extends KeyAdapter {
        public void keyReleased(KeyEvent e) {
            dirty = true;
        }
    }

    /**
     * Update the current view so any new/changed messages show up in the table
     */
    protected void updateView() {
        ColloquiaView currentView = ViewPanel.getInstance().getCurrentView();
        if(currentView instanceof MessageView) {
        	((MessageView)currentView).reloadMessages();
    	    ((MessageView)currentView).selectMessage(mInfo);
        }
    }


    protected class Action_Exit extends MenuAction {
        public Action_Exit() {
            super(LanguageManager.getString("EXIT"), null);
        }

        public void actionPerformed(ActionEvent e) {
            close();
        }
    }

    protected class Action_SaveDraftMessage extends MenuAction {
        public Action_SaveDraftMessage() {
            super(LanguageManager.getString("14_8"), ColloquiaConstants.iconSave);
            setButtonText(LanguageManager.getString("BUT3"));
        }

        public void actionPerformed(ActionEvent e) {
            try {
                saveMessage();
            }
            catch(ColloquiaFileException ex) {
                ErrorHandler.showWarning(EMailEditor.this, "ERR14", ex, "ERR");
            }
        }
    }

    protected class Action_Outbox extends MenuAction {
        public Action_Outbox() {
            super(LanguageManager.getString("14_9"), ColloquiaConstants.iconOutbox);
            setButtonText(LanguageManager.getString("BUT20"));
        }

        public void actionPerformed(ActionEvent e) {
            try {
                sendMessage(false);
            }
            catch(ColloquiaFileException ex) {
                ErrorHandler.showWarning(EMailEditor.this, "ERR14", ex, "ERR");
                return;
            }
            dispose();
        }
    }


    protected class Action_SendMessage extends MenuAction {
        public Action_SendMessage() {
            super(LanguageManager.getString("14_10"), ColloquiaConstants.iconSendMessage);
            setButtonText(LanguageManager.getString("BUT14"));
        }

        public void actionPerformed(ActionEvent e) {
            try {
                sendMessage(true);
            }
            catch(ColloquiaFileException ex) {
                ErrorHandler.showWarning(EMailEditor.this, "ERR14", ex, "ERR");
                return;
            }
            dispose();
        }
    }

    protected class Action_Attachment extends MenuAction {
        public Action_Attachment() {
            super(LanguageManager.getString("14_11"), ColloquiaConstants.iconAttach);
            setButtonText(LanguageManager.getString("BUT19"));
        }

        public void actionPerformed(ActionEvent e) {
            showAttachments();
        }
    }
}

