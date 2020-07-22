package net.colloquia.comms.tables;

import net.colloquia.comms.*;
import net.colloquia.comms.index.*;
import net.colloquia.datamodel.entities.*;
import net.colloquia.util.*;
import net.colloquia.xml.*;

/**
 * Table Data model to display e-mails in the context of a Person on the
 * 'Conversations' tab
 */
public class SingleMessageTableModel
extends MessageTableModel
{
    public SingleMessageTableModel() {
        columnNames = new String[] {
            "",
            "",
            "",
            LanguageManager.getString("16_1"),
            LanguageManager.getString("16_2")
        };
    }

    public void loadMessageIndex(final Activity A, final Person person) {
        index = new SingleMessageIndex(A.getGUID(), person.getGUID(), true);
        fireTableDataChanged();
        /*
        Thread thread = new Thread() {
            public void run() {
                index = new SingleMessageIndex(A.getGUID(), person.getGUID());
                fireTableDataChanged();
            }
        };
        thread.start();
        */
    }

    /**
    * Store the fact that we've read a particular message in the index
    * and re-save the message index
    */
    public void setSeen(MessageInfo mInfo, boolean value) {
        if(mInfo != null) {
            try {
                // Set it in the index
                mInfo.setSeenFlag(value);
                // Save the index
                index.save();
                // Tell the Table (NOT - 03-12-01)
                //fireTableDataChanged();
            }
            catch(XMLWriteException ex) {
                ErrorHandler.showWarning("ERR6", ex, "ERR");
            }
        }
        // Tell the Inbox anyway so that it can set its copy of the message there
        if(value == true) Inbox.getInstance().setSeen(mInfo, value);
    }

    public void deleteMessage(MessageInfo mInfo) throws XMLWriteException {
        super.deleteMessage(mInfo);
        // Now we have to tell the Inbox and Outbox in case we have a copy there
        Inbox.getInstance().deleteMessage(mInfo);
        Outbox.getInstance().deleteMessage(mInfo);
    }

    public void deleteMessages(MessageInfo[] mInfo) throws XMLWriteException {
        super.deleteMessages(mInfo);
        // Now we have to tell the Inbox and Outbox in case we have a copy there
        Inbox.getInstance().deleteMessages(mInfo);
        Outbox.getInstance().deleteMessages(mInfo);
    }
}
