package net.colloquia.menu;

import java.awt.event.*;
import java.io.*;
import java.util.*;

import net.colloquia.*;
import net.colloquia.datamodel.*;
import net.colloquia.datamodel.entities.*;
import net.colloquia.gui.widgets.*;
import net.colloquia.util.*;
import net.colloquia.views.tree.*;

/**
 */
public class Action_ExportPeople
extends MenuAction
implements ComponentSelectionListener
{
    private ColloquiaComponent selComponent;
    private ColloquiaContainer parentGroup;
    static String name = LanguageManager.getString("ACTION_8");

    public Action_ExportPeople() {
        super(name + "...", ColloquiaConstants.iconPerson);
        ColloquiaTree.getInstance().addComponentSelectionListener(this);
        setEnabled(false);
    }

    /**
    * Triggered from the Menu.  Calls {@link #export}. <p>
    */
    public void actionPerformed(ActionEvent e) {
        if(selComponent == null) return;
        export();
    }

    public void componentSelected(ColloquiaComponent selComponent,
                      ColloquiaContainer parentGroup, Activity currentActivity) {
        this.selComponent = selComponent;
        this.parentGroup = parentGroup;
    }

    /**
    * Exports the People entities in the currently selected node.
    */
    private void export() {
        File fileName;
        Vector people;

        // What component have we selected?  It must be a People folder or Activity
        if(selComponent instanceof PersonGroup) people = ((PersonGroup)selComponent).getPeople();
        // PENDING - should this be ACTIVE People??
        else if(selComponent instanceof Activity) people = ((Activity)selComponent).getAllPeople();

        // Ask for a file name
        PFileChooser chooser = new PFileChooser();
        chooser.setDialogTitle(name);
        int returnVal = chooser.showSaveDialog(MainFrame.getInstance());
        if(returnVal != PFileChooser.APPROVE_OPTION) return;
        fileName = chooser.getSelectedFileAndStore();

        MainFrame.getInstance().setCursor(ColloquiaConstants.waitCursor);


        MainFrame.getInstance().setCursor(ColloquiaConstants.defaultCursor);
        MainFrame.getInstance().statusBar.setText(name + LanguageManager.getString("DONE"));
    }
}
