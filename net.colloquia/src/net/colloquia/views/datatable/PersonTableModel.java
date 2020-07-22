package net.colloquia.views.datatable;

import javax.swing.*;

import net.colloquia.*;
import net.colloquia.datamodel.*;
import net.colloquia.datamodel.entities.*;
import net.colloquia.prefs.*;
import net.colloquia.util.*;
import net.colloquia.views.*;

public class PersonTableModel
extends BaseTableModel
{
    private PhotoPanel photoPanel;

    public PersonTableModel(PhotoPanel photoPanel) {
        this.photoPanel = photoPanel;
    }

    /**
     * The cell has been edited manually
     * Over-ridden to trap e-mail & photo entry
     */
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        String key, value;

        // Can't edit first/second row
        if(columnIndex != 2) return;

        // Put new value in data
        if(tc != null) {
            key = tc.getTableRowKey(rowIndex);
            value = aValue.toString().trim();

            // Can't put in duplicate e-mail address or myself
            if(key.equalsIgnoreCase(Person.EMAIL)) {
                // Check for Me FIRST - I cannot add Myself!
                UserPrefs prefs = UserPrefs.getUserPrefs();
                if(prefs.isMyEmailAddress(value)) {
                    String errMsg = LanguageManager.getString("7_5");
                    JOptionPane.showMessageDialog(MainFrame.getInstance(), errMsg,
                        LanguageManager.getString("7_4"), JOptionPane.WARNING_MESSAGE);
                    return;
                }
                // Check for duplicate
                Person checkPerson = DataModel.getPersonByEmailAddress(value);
                if(checkPerson != null) {
                    String errMsg = checkPerson.getName() + " " + LanguageManager.getString("7_3");
                    JOptionPane.showMessageDialog(MainFrame.getInstance(), errMsg,
                        LanguageManager.getString("7_4"), JOptionPane.WARNING_MESSAGE);
                    return;
                }
            }

            // Photograph
            else if(key.equalsIgnoreCase(Person.PHOTOGRAPH)) {
                photoPanel.setImage(value);
            }

            tc.putProperty(key, value, true);
        }
    }

    public boolean isBorderHighlighted(int rowIndex, int columnIndex) {
        if(columnIndex != 0) return false;
        String key = tc.getTableRowKey(rowIndex);
        if(key.equalsIgnoreCase(ColloquiaComponent.LOCAL_FILE)) return true;
        if(key.equalsIgnoreCase(Person.PHOTOGRAPH)) return true;
        if(key.equalsIgnoreCase(ColloquiaComponent.URL)) return true;
        return false;
    }
}


