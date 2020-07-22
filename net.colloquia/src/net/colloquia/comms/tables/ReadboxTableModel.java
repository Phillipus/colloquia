package net.colloquia.comms.tables;

import net.colloquia.comms.*;
import net.colloquia.comms.index.*;
import net.colloquia.util.*;

public class ReadboxTableModel
extends MessageBoxTableModel
{
    public ReadboxTableModel() {
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
        index = new ReadboxMessageIndex();
        fireTableDataChanged();
    }

    public void setSeen(MessageInfo mInfo, boolean value) {}
}
