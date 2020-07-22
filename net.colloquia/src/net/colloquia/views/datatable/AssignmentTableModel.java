package net.colloquia.views.datatable;

import net.colloquia.datamodel.entities.*;

public class AssignmentTableModel
extends BaseTableModel
{
    public boolean isBorderHighlighted(int rowIndex, int columnIndex) {
        if(columnIndex != 0) return false;
        String key = tc.getTableRowKey(rowIndex);
        if(key.equalsIgnoreCase(ColloquiaComponent.LOCAL_FILE)) return true;
        if(key.equalsIgnoreCase(Assignment.ASSESSABLE)) return true;
        if(key.equalsIgnoreCase(ColloquiaComponent.URL)) return true;
        return false;
    }
}

