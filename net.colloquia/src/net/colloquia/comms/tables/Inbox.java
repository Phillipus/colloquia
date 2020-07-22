package net.colloquia.comms.tables;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;

import net.colloquia.ColloquiaConstants;
import net.colloquia.MainFrame;
import net.colloquia.comms.MailFailureException;
import net.colloquia.comms.MessageInfo;
import net.colloquia.comms.MessageManager;
import net.colloquia.comms.messages.MessageIn;
import net.colloquia.datamodel.DataModel;
import net.colloquia.datamodel.entities.Activity;
import net.colloquia.datamodel.entities.Person;
import net.colloquia.datamodel.entities.Resource;
import net.colloquia.gui.widgets.PLabel;
import net.colloquia.io.DataFiler;
import net.colloquia.menu.MenuAction;
import net.colloquia.prefs.UserPrefs;
import net.colloquia.util.ErrorHandler;
import net.colloquia.util.LanguageManager;
import net.colloquia.util.Utils;
import net.colloquia.views.ViewPanel;
import net.colloquia.views.entities.ColloquiaView;
import net.colloquia.views.entities.MessageView;
import net.colloquia.views.tree.ColloquiaTree;
import net.colloquia.xml.XMLWriteException;

/**
 * The INBOX
 */
public class Inbox
extends MessageBox
{
    String unmatchedMsg = LanguageManager.getString("16_11");
    String importedMsg = LanguageManager.getString("16_12");

    private GetThread getThread;

    private boolean gotImportedMessages;
    private boolean gotUnmatchedMessages;
    private boolean matchedMessages;

    // For our listeners
    //private Vector newMessageList;

    // Toolbar stuff
    private Action_GetMail actionGetMail;
    private Action_ImportMessages actionImportMessages;
    private Action_GotoMessage actionGotoMessage;
    private Action_MarkAsRead actionMarkAsRead;
    private Action_Delete actionDelete;

    private static Inbox instance = new Inbox();

    private Inbox() {
        super(new InboxTableModel());

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


    public static Inbox getInstance() {
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

        // Get mail
        actionGetMail = new Action_GetMail();
        toolBar.add(actionGetMail);
        JMenuItem item = menu.add(actionGetMail);

        // Import Message
        actionImportMessages = new Action_ImportMessages();
        toolBar.add(actionImportMessages);
        item = menu.add(actionImportMessages);

        // Goto message
        actionGotoMessage = new Action_GotoMessage();
        toolBar.add(actionGotoMessage, false);
        item = menu.add(actionGotoMessage);

        // Mark as read
        actionMarkAsRead = new Action_MarkAsRead();
        toolBar.add(actionMarkAsRead, false);
        item = menu.add(actionMarkAsRead);

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
    * Set the seen flag once the message has been read in context
    */
    public void setSeen(MessageInfo mInfo, boolean value) {
        model.setSeen(mInfo, value);
    }

    /*
    public int countGroupMessages(Activity A) {
    	int sum = 0;

        Vector v = getMessages();
        for(int i = 0; i < v.size(); i++) {
        	MessageInfo mInfo = (MessageInfo)v.elementAt(i);
            if(mInfo.getMessageType() == MessageInfo.GROUP_MESSAGE) {
            	if(mInfo.getActivityID().equals(A.getGUID())) sum++;
            }
        }

        return sum;
    }

    public int countSingleMessages(Activity A, Person person) {
    	int sum = 0;

        Vector v = getMessages();
        for(int i = 0; i < v.size(); i++) {
        	MessageInfo mInfo = (MessageInfo)v.elementAt(i);
            if(mInfo.getMessageType() == MessageInfo.SINGLE_MESSAGE) {
            	if(mInfo.getActivityID().equals(A.getGUID()) && mInfo.getPersonID().equals(person.getGUID())) sum++;
            }
        }

        return sum;
    }

    public int countAssignmentMessages(Activity A, Person person) {
    	int sum = 0;

        Vector v = getMessages();
        for(int i = 0; i < v.size(); i++) {
        	MessageInfo mInfo = (MessageInfo)v.elementAt(i);
            if(mInfo.getMessageType() == MessageInfo.ASSIGNMENT_MESSAGE) {
            	if(mInfo.getActivityID().equals(A.getGUID()) && mInfo.getPersonID().equals(person.getGUID())) sum++;
            }
        }

        return sum;
    }
    */

    // =============================== GET MAIL ================================
    Vector newMessageList;

    public void getMail(Frame owner, boolean connect) {
        if(getThread != null) return;
        getThread = new GetThread(owner, connect);
        getThread.start();
    }

    private class GetThread
    extends Thread
    {
        boolean connect;
        Frame owner;

    	public GetThread(Frame owner, boolean connect) {
            this.owner = owner;
       		this.connect = connect;
        }

		public void run() {
            gotImportedMessages = false;
            gotUnmatchedMessages = false;
            matchedMessages = false;

            // For our listeners
            newMessageList = new Vector();

            // Get any new messages on the net
            if(connect) {
                try {
                    MessageManager.getMessages(owner);
                }
                catch(MailFailureException ex) {
                    if(ColloquiaConstants.DEBUG) ex.printStackTrace();
                    ErrorHandler.showWarning("16_13", ex, "EMAIL");
                }
            }

            setCursor(ColloquiaConstants.waitCursor);

            // Get any messages that might be in the Import Folder (if option is set)
            importMessages();

            // Now try and match any unmatched messages
            Vector filed = Pendingbox.getInstance().fileMessages();
            // Got some, add to Inbox
            if(!filed.isEmpty()) {
                for(int i = 0; i < filed.size(); i++) {
                    MessageInfo mInfo = (MessageInfo)filed.elementAt(i);
                    try {
                        addNewMessage(mInfo);
                    }
                    catch(XMLWriteException ex) {
                        if(ColloquiaConstants.DEBUG) ex.printStackTrace();
                        System.out.println("Error adding filed message to Inbox: " + ex);
                    }
                }
                matchedMessages = true;
            }

            // Save tree data
            try {
                DataModel.save();
            }
            catch(XMLWriteException ex) {
                setCursor(ColloquiaConstants.defaultCursor);
                if(ColloquiaConstants.DEBUG) ex.printStackTrace();
                ErrorHandler.showWarning("ERR2", ex, "ERR");
            }

            // Update
            Runnable updateThread = new Runnable() {
                public void run() {
                    fireNewMessagesRcvd(newMessageList);
                }
            };
            SwingUtilities.invokeLater(updateThread);

            setCursor(ColloquiaConstants.defaultCursor);

            displayInfoMessage(owner, newMessageList);

            getThread = null;
        }
    }

    private void displayInfoMessage(Component parent, Vector newMessageList) {
        int textMessages = 0;
        int activities = 0;
        int assignments = 0;
        int persons = 0;
        int resources = 0;
        int invites = 0;
        int acceptances = 0;
        int completions = 0;
        int reactivations = 0;
        int other = 0;

        // No new messages
        if(newMessageList.size() == 0) {
            String msg = LanguageManager.getString("16_97");
	        JOptionPane.showMessageDialog(parent, msg, LanguageManager.getString("EMAIL"),
                         JOptionPane.INFORMATION_MESSAGE);

        	return;
        }

        // Count messages
        for(int i = 0; i < newMessageList.size(); i++) {
        	MessageInfo mInfo = (MessageInfo)newMessageList.elementAt(i);
            switch(mInfo.getMessageType()) {
            	case MessageInfo.SINGLE_MESSAGE:
            	case MessageInfo.GROUP_MESSAGE:
            	case MessageInfo.ASSIGNMENT_MESSAGE:
                	textMessages++;
                	break;
            	case MessageInfo.ACTIVITY:
                	activities++;
                    other++;
                    break;
            	case MessageInfo.ASSIGNMENT:
                	assignments++;
                    other++;
                    break;
            	case MessageInfo.PERSON:
                	persons++;
                    other++;
                    break;
            	case MessageInfo.RESOURCE:
                	resources++;
                    other++;
                    break;
            	case MessageInfo.ACTIVITY_INVITE:
                	invites++;
                    other++;
                    break;
            	case MessageInfo.ACTIVITY_ACCEPT:
                	acceptances++;
                    other++;
                    break;
            	case MessageInfo.ACTIVITY_COMPLETE:
                	completions++;
                    other++;
                    break;
            	case MessageInfo.ACTIVITY_REACTIVATE:
                	reactivations++;
                    other++;
                    break;
                default:
                	other++;
                    break;
            }
        }

		String msg = LanguageManager.getString("16_108") + " " + textMessages + " " +
        	LanguageManager.getString("MESSAGES") + "\n\n";

        if(other > 0) {
            msg += LanguageManager.getString("16_116") + "\n";
            if(activities > 0) msg += activities + " " + LanguageManager.getString("16_100") + "\n";
            if(assignments > 0) msg += assignments + " " + LanguageManager.getString("16_102") + "\n";
            if(persons > 0) msg += persons + " " + LanguageManager.getString("16_103") + "\n";
            if(resources > 0) msg += resources + " " + LanguageManager.getString("16_104") + "\n";
            if(invites > 0) msg += invites + " " + LanguageManager.getString("16_105") + "\n";
            if(acceptances > 0) msg += acceptances + " " + LanguageManager.getString("16_106") + "\n";
            if(completions > 0) msg += completions + " " + LanguageManager.getString("16_111") + "\n";
            if(reactivations > 0) msg += reactivations + " " + LanguageManager.getString("16_112") + "\n";
        }

        // If we are not in the Message Centre
        if(parent != this) {

            String[] options = {
                LanguageManager.getString("OK"),
                LanguageManager.getString("MESSAGECENTRE"),
            };

            int val = JOptionPane.showOptionDialog(this,
            			msg,
                     	LanguageManager.getString("EMAIL"),
                        JOptionPane.OK_OPTION,
                        JOptionPane.DEFAULT_OPTION,
                     	Utils.getIcon(ColloquiaConstants.iconAppIcon),
                        options,
                        options[0]);

            if(val != JOptionPane.OK_OPTION) {
                MessageWindow.getInstance().showWindow();
                MessageWindow.getInstance().selectTab(0);  // Inbox
            }
        }

        else {
        	JOptionPane.showMessageDialog(parent,
            	msg, LanguageManager.getString("EMAIL"),
            	JOptionPane.INFORMATION_MESSAGE);
        }
    }


    /**
    * Imports messages from a specified folder to the Inbox when a user has saved their
    * Messages from their regular e-mail client before we got it.
    */
    private void importMessages() {
        String folder = UserPrefs.getUserPrefs().getProperty(UserPrefs.EMAIL_IMPORT_FOLDER);
        folder = DataFiler.addFileSepChar(folder);
        File file = new File(folder);
        if(!file.exists()) return;

        String[] files = file.list();

        for(int i = 0; i < files.length; i++ ) {
            importMessage(new File(folder + files[i]));
        }
    }

    /**
    * Imports a message from file to the Inbox
    */
    private void importMessage(File file) {
        // Determine whether it's a valid file
        String extension = ".cqm";
        if(!file.getName().toLowerCase().endsWith(extension)) return;

        // File the message
        try {
            MessageIn messageIn = MessageIn.getMessageIn(file);
            if(messageIn != null) {
                MessageInfo mInfo = messageIn.fileMessage(true);
                if(mInfo != null) {
                    // Unknown message
                    if(mInfo.getState() == MessageInfo.UNMATCHED) addUnmatchedMessage(mInfo);
                    // OK
                    else addNewMessage(mInfo);
                }
            }
        }
        catch(Exception ex) {
            if(ColloquiaConstants.DEBUG) ex.printStackTrace();
            System.out.println("Could not import message: " + ex);
        }

        gotImportedMessages = true;
    }


    /**
    * This is where MessageManager can add a new message to the Inbox / Pendingbox
    */
    public void addDeliveredMessage(MessageInfo mInfo) throws XMLWriteException {
        if(mInfo != null) {
            // Unknown message
            if(mInfo.getState() == MessageInfo.UNMATCHED) {
                addUnmatchedMessage(mInfo);
            }
            // OK
            else {
                addNewMessage(mInfo);
            }
        }
    }

    /**
    * Add a new message to the model and also to a Vector for our listeners
    */
    private void addNewMessage(MessageInfo mInfo) throws XMLWriteException {
        model.addMessage(mInfo, false);
        newMessageList.addElement(mInfo);
    }

    /**
    * Adds a reference to a message in Pending
    */
    private void addUnmatchedMessage(MessageInfo mInfo) {
        try {
            Pendingbox.getInstance().addMessage(mInfo);
        }
        catch(XMLWriteException ex) {
            if(ColloquiaConstants.DEBUG) ex.printStackTrace();
            System.out.println("Could not addUnmatchedMessage: " + ex);
        }

        gotUnmatchedMessages = true;
    }


    /**
    * Jump to a node & message when user d-clicks on a row
    * Same as for READBOX
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

            // A Single Message - go to the person who sent it
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

        // Set as Seen
        setSeen(mInfo, true);

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
    * Manually Mark selected messages as read - this will place them
    * in the ReadBox
    */
    private void markAsRead() {
    	MessageInfo[] mInfo = getSelectedMessages();
        if(mInfo == null) return;

        setCursor(ColloquiaConstants.waitCursor);
        for(int i = 0; i < mInfo.length; i++) {
        	setSeen(mInfo[i], true);
        }
        updateButtons();
        setCursor(ColloquiaConstants.defaultCursor);
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

    private void updateButtons() {
        int row = table.getSelectedRow();
        int numRows = model.getRowCount();
        boolean show = (row != -1 && numRows != 0);

        actionDelete.setEnabled(show);
      	actionGotoMessage.setEnabled(show);
      	actionMarkAsRead.setEnabled(show);

        fireTableChanged(model.getMessageIndex());
    }


    // ****************** BUTTONS ******************** \\

    /**
    * Handles button press - get new mail
    */
    private class Action_GetMail extends MenuAction {
        public Action_GetMail() {
            super(LanguageManager.getString("16_18"), ColloquiaConstants.iconGetMail);
            setButtonText(LanguageManager.getString("BUT6"));
        }

        public void actionPerformed(ActionEvent e) {
            getMail(MessageWindow.getInstance(), true);
        }
    }

    private class Action_ImportMessages extends MenuAction {
        public Action_ImportMessages() {
            super(LanguageManager.getString("16_19"), ColloquiaConstants.iconEdit);
            setButtonText(LanguageManager.getString("BUT7"));
        }

        public void actionPerformed(ActionEvent e) {
            getMail(MessageWindow.getInstance(), false);
        }
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

    private class Action_MarkAsRead extends MenuAction {
        public Action_MarkAsRead() {
            super(LanguageManager.getString("16_115"), ColloquiaConstants.iconStop);
            setButtonText(LanguageManager.getString("BUT32"));
        }

        public void actionPerformed(ActionEvent e) {
            markAsRead();
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
                if(ColloquiaConstants.DEBUG) ex.printStackTrace();
                ErrorHandler.showWarning("ERR10", ex, "ERR");
            }
        }
    }

    /// ============================ LISTENERS =================================
    private Vector ib_listeners = new Vector();

    public synchronized void addInboxListener(InboxListener listener) {
        if(!ib_listeners.contains(listener)) ib_listeners.addElement(listener);
    }

    public synchronized void removeInboxListener(InboxListener listener) {
        ib_listeners.removeElement(listener);
    }

    /**
    * Tell our listeners
    */
    public void fireNewMessagesRcvd(Vector v) {
        if(v.isEmpty()) return;
        MessageInfo[] mInfo = new MessageInfo[v.size()];
        v.copyInto(mInfo);

        for(int i = 0; i < ib_listeners.size(); i++) {
            InboxListener listener = (InboxListener)ib_listeners.elementAt(i);
            listener.newMessagesRecvd(mInfo);
        }
    }

}

