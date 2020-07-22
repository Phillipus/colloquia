package net.colloquia.comms.tables;

import java.awt.*;
import java.awt.event.*;
import java.util.*;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;

import net.colloquia.comms.*;
import net.colloquia.comms.index.*;
import net.colloquia.gui.widgets.*;

/**
* Viewer base class for e-mails. Inbox, Outbox, Pending box
* Allows viewing, filing and deleting.
*/
public abstract class MessageBox
extends JPanel
implements ListSelectionListener
{
    protected JTable table;
    protected JTableHeader tableHeader;
    protected MessageBoxTableModel model;
    protected MessageTableRenderer cellRenderer;
    protected MessageTableHeaderRenderer headerRenderer;
    protected MessageTableClickHandler headerClickHandler;
    protected ClickHandler clickHandler;
    protected ColloquiaToolBar toolBar;
    protected JMenuBar menuBar;
    protected TableColumnModel tableColumnModel;

    protected MessageBox(MessageBoxTableModel model) {
        setLayout(new BorderLayout());
        this.model = model;
        table = new JTable(model);
        table.setRowHeight(20);

        tableHeader = table.getTableHeader();
        tableHeader.setResizingAllowed(true);
        tableColumnModel = tableHeader.getColumnModel();

        // Selection model and selection listener
        ListSelectionModel lsm = table.getSelectionModel();
        lsm.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        lsm.addListSelectionListener(this);

        // Set the renderer for the column header
        TableColumn current;
        headerRenderer = new MessageBoxTableHeaderRenderer(0, model);
        for(Enumeration cols = tableColumnModel.getColumns(); cols.hasMoreElements();) {
            current = (TableColumn)cols.nextElement();
            // Assign our custom header-renderer to each column
            current.setHeaderRenderer(headerRenderer);
        }

        toolBar = new ColloquiaToolBar();
        menuBar = new JMenuBar();

        initToolBar();

        // Listen to mouse-click on the table header
        headerClickHandler = new MessageTableClickHandler(table);
        tableHeader.addMouseListener(headerClickHandler);

        // Listen to mouse-clicks on the table
        clickHandler = new ClickHandler();
        table.addMouseListener(clickHandler);

        //System.out.println("Created View: " + getClass());
    }

    protected abstract void jumpToMessage(MessageInfo mInfo);
    protected abstract void initToolBar();

    /**
    * Refresh Messages
    */
    public void reloadMessages() {
        model.loadMessageIndex();
    }

    protected class ClickHandler extends MouseAdapter {
        public void mouseClicked(MouseEvent e) {
            Component source = e.getComponent();
            if(source != table) return;
            int clicks = e.getClickCount();
            // Double-Click on message in table - jump to selected message
            if(clicks == 2) jumpToMessage(getSelectedMessage());
		}
    }

    /**
    * Get the selected message or null if none selected
    */
    protected MessageInfo getSelectedMessage() {
        int row = table.getSelectedRow();
        int numRows = model.getRowCount();

        // No row selected
        if(row == -1 || numRows == 0) return null;
        // Get the Message
        else return (MessageInfo)model.getValueAt(row, 0);
    }

    /**
    * Get any selected messages or null if none selected
    */
    protected MessageInfo[] getSelectedMessages() {
        int[] rows = table.getSelectedRows();
        int numRows = rows.length;
        if(numRows == 0) return null;

        // Gather messages in an array
        MessageInfo[] mInfo = new MessageInfo[numRows];
        for(int i = 0; i < numRows; i++)
            mInfo[i] = model.getMessageIndex().getMessageAt(rows[i]);

        return mInfo;
    }

    public boolean hasMessages() {
    	MessageIndex index = model.getMessageIndex();
    	return index == null ? false : !index.isEmpty();
    }

    public void gotoNextMessage() {
        if(hasMessages()) {
        	MessageInfo mInfo = model.getMessageIndex().getMessageAt(0);
	        if(mInfo != null) jumpToMessage(mInfo);
        }
    }

    public Vector getMessages() {
    	return model.getMessageIndex().getMessages();
    }

    /// ============================ LISTENERS =================================
    private Vector mb_listeners = new Vector();

    public synchronized void addMessageBoxListener(MessageBoxListener listener) {
        if(!mb_listeners.contains(listener)) mb_listeners.addElement(listener);
    }

    public synchronized void removeMessageBoxListener(MessageBoxListener listener) {
        mb_listeners.removeElement(listener);
    }

    /**
    * Tell our listeners
    */
    public void fireTableChanged(MessageIndex index) {
        if(index == null) return;
        for(int i = 0; i < mb_listeners.size(); i++) {
            MessageBoxListener listener = (MessageBoxListener)mb_listeners.elementAt(i);
            listener.messageboxChanged(index);
        }
    }

}
