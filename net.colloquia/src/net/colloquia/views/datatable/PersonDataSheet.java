package net.colloquia.views.datatable;

import net.colloquia.*;
import net.colloquia.datamodel.entities.*;
import net.colloquia.gui.widgets.*;
import net.colloquia.io.*;
import net.colloquia.views.*;

/**
* A Panel that holds the table for displaying metadata for an object.
*/
public class PersonDataSheet
extends BaseDataSheet
{
    private PhotoPanel photoPanel;

    public PersonDataSheet(PhotoPanel photoPanel) {
        super(new PersonTableModel(photoPanel));
        this.photoPanel = photoPanel;
    }

    protected void tableClicked(String key) {
        super.tableClicked(key);
        if(key.equalsIgnoreCase(Person.PHOTOGRAPH)) setPhoto();
    }

    private void setPhoto() {
        MainFrame.getInstance().setCursor(ColloquiaConstants.waitCursor);
        PFileChooser chooser = new PFileChooser();
        ColloquiaFileFilter filter = new ColloquiaFileFilter();
        filter.addExtension("gif");
        filter.addExtension("jpg");
        filter.setDescription("JPEG and GIF Image Files");
        chooser.setFileFilter(filter);
        int returnVal = chooser.showOpenDialog(MainFrame.getInstance());
        MainFrame.getInstance().setCursor(ColloquiaConstants.defaultCursor);
        if(returnVal != PFileChooser.APPROVE_OPTION) return;
        String fileName = chooser.getSelectedFileAndStore().toString();
        tc.putProperty(Person.PHOTOGRAPH, fileName, true);
        tableModel.fireTableDataChanged();
        photoPanel.setImage(fileName);
    }

}