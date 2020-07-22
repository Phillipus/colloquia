package net.colloquia.comms.tables;

import net.colloquia.comms.*;
import net.colloquia.comms.index.*;
import net.colloquia.util.*;

public class OutboxTableModel
extends MessageBoxTableModel
{

    public OutboxTableModel() {
        columnNames = new String[] {
            "",
            "",
            LanguageManager.getString("16_26"),
            LanguageManager.getString("0"),
            LanguageManager.getString("16_1")
        };
        loadMessageIndex();
    }

    public void loadMessageIndex() {
        index = new OutboxMessageIndex();
        fireTableDataChanged();
    }

    // Over-ride to do nothing
    public void setSeen(MessageInfo mInfo, boolean value) {}
}
