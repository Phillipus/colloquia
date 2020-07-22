package net.colloquia.comms.tables;

import net.colloquia.comms.index.*;
import net.colloquia.datamodel.entities.*;
import net.colloquia.util.*;

/**
 * Table Data model to display e-mails for a Group of people on a Live LA
 */
public class GroupMessageTableModel
extends SingleMessageTableModel
{
    public GroupMessageTableModel() {
        columnNames = new String[] {
            "",
            "",
            "",
            LanguageManager.getString("16_8"),
            LanguageManager.getString("16_1"),
            LanguageManager.getString("16_2")
        };
    }

    public void loadMessageIndex(final Activity A) {
        index = new GroupMessageIndex(A.getGUID(), true);
        fireTableDataChanged();

        /*
        Thread thread = new Thread() {
            public void run() {
                index = new GroupMessageIndex(A.getGUID());
                fireTableDataChanged();
            }
        };
        thread.start();
        */
    }

}
