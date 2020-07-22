package net.colloquia.comms.tables;

import javax.swing.table.*;

import net.colloquia.comms.*;
import net.colloquia.comms.index.*;
import net.colloquia.util.*;
import net.colloquia.xml.*;

/**
* Table Data model to display e-mails in a table
* NOTE - Everytime we make a change to a MessageInfo element in the Index
*        We save the index and reload it.  This means that we have to refer
*        to a new bunch of objects
*/
public abstract class MessageTableModel
extends AbstractTableModel
{
    protected String[] columnNames;       // Over-ride this
    protected MessageIndex index;

    public abstract void setSeen(MessageInfo mInfo, boolean value);

    /**
    * Delete a message from the index
    * The index will be saved if successful
    */
    public void deleteMessage(MessageInfo mInfo) throws XMLWriteException {
        if(index == null) return;
        boolean result = index.deleteMessage(mInfo);

        if(result) {
            index.save();
            // Tell the table
            fireTableDataChanged();
            //fireTableRowsDeleted(0, getRowCount());
        }
    }

    /**
    * Delete a bunch of messages from the index
    * The index will be saved
    */
    public void deleteMessages(MessageInfo[] mInfo) throws XMLWriteException {
        if(index == null) return;
        boolean gotOne = false;

        for(int i = 0; i < mInfo.length; i++) {
            if(index.deleteMessage(mInfo[i])) gotOne = true;
        }

        if(gotOne) {
            index.save();
            // Tell the table
            fireTableDataChanged();
        }
    }

    /**
    * Delete a selected bunch of messages
    */
    public void deleteMessages(int[] rows) throws XMLWriteException {
        if(index == null) return;
        int msgCount = rows.length;
        if(msgCount == 0) return;

        // Gather messages to be deleted in an array
        MessageInfo[] delMsgs = new MessageInfo[msgCount];
        for(int i = 0; i < msgCount; i++)
            delMsgs[i] = index.getMessageAt(rows[i]);

        deleteMessages(delMsgs);
    }

    /**
    * Set a message's state and resave the index
    */
    public void setState(MessageInfo mInfo, int state) {
        if(index == null || mInfo == null) return;
        try {
            mInfo.setState(state);
            // Save the index
            index.save();
            // Tell the Table
            fireTableDataChanged();
        }
        catch(XMLWriteException ex) {
            ErrorHandler.showWarning("ERR6", ex, "ERR");
        }
    }

    public MessageInfo[] getUnreadMessages() {
        return index == null ? null : index.getUnreadMessages();
    }

    /**
    * Sort the message list and save it
    * This is called from MessageTableClickHandler
    */
    public void sortIndex(int column) {
        if(index == null) return;
        try {
            // Sort it
            index.sort(column);
            // Save the index
            index.save();
            // Tell the table
            fireTableDataChanged();
        }
        catch(XMLWriteException ex) {
            ErrorHandler.showWarning("ERR6", ex, "ERR");
        }
    }

    public MessageIndex getMessageIndex() {
        return index;
    }

    /** Returns true if there are any messages */
    public boolean hasMessages() {
        return index == null ? false : !index.isEmpty();
    }

    // getValueAt
    public Object getValueAt(int rowIndex, int columnIndex) {
        if(index == null) return null;
        else if(rowIndex < index.size()) {
            MessageInfo mInfo = index.getMessageAt(rowIndex);
            return mInfo;
        }
        else return null;
    }

    // getColumnCount
    public int getColumnCount() {
        return columnNames.length;
    }

    // getRowCount
    public int getRowCount() {
        return size();
    }

    public int size() {
        return index == null ? 0 : index.size();
    }

    public String getColumnName(int columnIndex) {
        return columnNames[columnIndex];
    }
}
