package net.colloquia.comms.tables;

import net.colloquia.comms.*;
import net.colloquia.comms.index.*;
import net.colloquia.util.*;

public class PendingboxTableModel extends MessageBoxTableModel {

    public PendingboxTableModel() {
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
        index = new PendingboxMessageIndex();
        fireTableDataChanged();
    }

    // Over-ride to do nothing
    public void setSeen(MessageInfo mInfo, boolean value) {}
}
