package net.colloquia.comms.tables;

import net.colloquia.comms.*;
import net.colloquia.comms.index.*;
import net.colloquia.util.*;
import net.colloquia.xml.*;

public class InboxTableModel
extends MessageBoxTableModel
{

    /**
     * Constructor
     */
    public InboxTableModel() {
        columnNames = new String[] {
            "",
            "",
            LanguageManager.getString("16_8"),
            LanguageManager.getString("0"),
            LanguageManager.getString("16_1"),
            LanguageManager.getString("16_2")
        };
        loadMessageIndex();
    }

    public void loadMessageIndex() {
        index = new InboxMessageIndex();
        fireTableDataChanged();
    }

    /**
    * Move the message pointer to the Readbox once the message has been read in context
    * This is called from elsewhere when the corresponding message is read
    * Or from Inbox if it's been seen
    * Thus, we have to treat mInfo as a template because of the outside call
    */
    public void setSeen(MessageInfo mInfo, boolean value) {
        // Do we have a copy of this message here?
        // We shall use mInfo as a template
        MessageInfo mi = index.getMessage(mInfo);
        if(mi != null && value == true) {
            try {
                mi.setSeenFlag(value);

                // Remove and add to Readbox
                Readbox.getInstance().addMessage(mi);
                deleteMessage(mi);

                // Tell the table (NOT - 03-12-01)
                //fireTableDataChanged();
            }
            catch(XMLWriteException ex) {
                ErrorHandler.showWarning("ERR6", ex, "ERR");
            }
        }
    }

}

