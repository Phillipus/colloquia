package net.colloquia.comms.tables;

import net.colloquia.comms.index.*;
import net.colloquia.datamodel.entities.*;

/**
* Table Data model to display e-mails in the context of an Assignment
*/
public class AssignmentMessageTableModel
extends SingleMessageTableModel
{
    public AssignmentMessageTableModel() {
        super();
    }

    public void loadMessageIndex(final Activity A, final Person person) {
        index = new AssignmentMessageIndex(A.getGUID(), person.getGUID(), true);
        fireTableDataChanged();
        /*
        Thread thread = new Thread() {
            public void run() {
                index = new AssignmentMessageIndex(A.getGUID(), person.getGUID());
                fireTableDataChanged();
            }
        };
        thread.start();
        */
    }
}
