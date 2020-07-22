package net.colloquia.comms.tables;

import net.colloquia.comms.*;
import net.colloquia.xml.*;


/**
* The table model for Inbox, OutBox and PendingBox
* It inherits some stuff from MessageTableModel and other stuff is over-ridden
*/
public abstract class MessageBoxTableModel
extends MessageTableModel
{
    public abstract void loadMessageIndex();

    public void addMessage(MessageInfo mInfo, boolean replace) throws XMLWriteException {
        index.addMessage(mInfo, replace);
        index.save();
        fireTableRowsInserted(getRowCount(), getRowCount());
    }

}
