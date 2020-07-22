package net.colloquia.views.datatable;

import net.colloquia.datamodel.entities.*;

/**
* A Panel that holds the table for displaying metadata for an object.
*/
public class AssignmentDataSheet
extends BaseDataSheet
{

    public AssignmentDataSheet() {
        super(new AssignmentTableModel());
    }

    protected void tableClicked(String key) {
        super.tableClicked(key);
        if(key.equalsIgnoreCase(Assignment.ASSESSABLE)) setAssessable();
    }

    private void setAssessable() {
        // Can't set if not editable
        if(tc.isMine()) {
            Assignment ass = (Assignment)tc;
            ass.setAssessable(!ass.isAssessable(), true);
            tableModel.fireTableDataChanged();
        }
    }
}
