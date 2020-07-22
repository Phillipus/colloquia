package net.colloquia.comms.tables;

import net.colloquia.comms.*;
import net.colloquia.comms.index.*;
import net.colloquia.util.*;


public class SentboxTableModel
extends MessageBoxTableModel
{

    public SentboxTableModel() {
        columnNames = new String[] {
            "",
            "",
            LanguageManager.getString("16_26"),
            LanguageManager.getString("0"),
            LanguageManager.getString("16_1"),
            LanguageManager.getString("16_2")
        };
        loadMessageIndex();
    }

    public void loadMessageIndex() {
        index = new SentboxMessageIndex();
        fireTableDataChanged();
    }

    // Over-ride to do nothing
    public void setSeen(MessageInfo mInfo, boolean value) {}
}
