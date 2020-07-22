package net.colloquia.views.datatable;

import javax.swing.table.*;

import net.colloquia.datamodel.*;
import net.colloquia.datamodel.entities.*;
import net.colloquia.util.*;

/**
 * The table model for ColloquiaComponent Datasheet tables.
 */
public class BaseTableModel
extends AbstractTableModel
{
    protected String columnNames[] = {" ", "", LanguageManager.getString("7_2")};
    protected ColloquiaComponent tc;

    public void setComponent(ColloquiaComponent tc) {
        this.tc = tc;
    }

    public ColloquiaComponent getComponent() {
    	return tc;
    }

    /**
    * The cell has been edited manually
    * Default action
    */
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        if(aValue == null) return;

        // Can't edit first/second row
        if(columnIndex != 2) return;

        // Put new value in data
        if(tc != null) {
            String key = tc.getTableRowKey(rowIndex);
            //String value = aValue.toString().trim();
            tc.putProperty(key, aValue.toString(), true);
        }
    }

    // getValueAt
    public Object getValueAt(int rowIndex, int columnIndex) {
        if(tc == null) return "";
        switch(columnIndex) {
            case 0:
                String key = tc.getTableRowKey(rowIndex);
                if(key.equals(ColloquiaComponent.URL) && tc.isExternalBrowser()) return "*";
                else return "";

            case 1:
                return tc.getTableRowName(rowIndex);

            case 2:
                String value = tc.getTableRowValue(rowIndex);
                if(value.equalsIgnoreCase("ME")) value = LanguageManager.getString("ME");
                else if(value.equalsIgnoreCase("YES")) value = LanguageManager.getString("YES");
                else if(value.equalsIgnoreCase("NO")) value = LanguageManager.getString("NO");

                else if(tc.getTableRowKey(rowIndex).equalsIgnoreCase(ColloquiaComponent.SUBMITTER)) {
                    Person person = DataModel.getPersonByEmailAddress(value);
                    if(person != null) value = person.getName();
                }

                return value;

            default:
                return "";
        }
    }

    public boolean isCellEditable(int rowIndex, int columnIndex) {
        if(columnIndex != 2) return false;
       	FieldValue fv = tc.getTableFieldValue(rowIndex);
        if(fv == null) return false;
        if(!tc.isMine()) return fv.isEditable;
        if(fv.key.equalsIgnoreCase(ColloquiaComponent.SUBMITTER)) return false;
        if(fv.key.equalsIgnoreCase(Assignment.ASSESSABLE)) return false;
        return true;
    }

    public String getColumnName(int columnIndex) {
        return columnNames[columnIndex];
    }

    // getColumnCount
    public int getColumnCount() {
        return columnNames.length;
    }

    // getRowCount
    public int getRowCount() {
        if(tc == null) return 0;
        else return tc.getTableRowCount();
    }

    public boolean isBorderHighlighted(int rowIndex, int columnIndex) {
    	return false;
    }

    public String getToolTipText(int rowIndex, int columnIndex) {
        switch(columnIndex) {
            case 0:
                String key = tc.getTableRowKey(rowIndex);
                if(key.equals(ColloquiaComponent.URL)) return LanguageManager.getString("4_10");
                if(key.equals(ColloquiaComponent.LOCAL_FILE)) return LanguageManager.getString("7_1");
                if(key.equals(Assignment.ASSESSABLE)) return LanguageManager.getString("9_7");
                return null;

            default:
                return null;
        }
    }
}
