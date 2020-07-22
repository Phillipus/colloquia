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
public class Action_ExportResources extends MenuAction implements ComponentSelectionListener {
    private ColloquiaComponent selComponent;
    private ColloquiaContainer parentGroup;
    static String name = LanguageManager.getString("ACTION_9");

    public Action_ExportResources() {
        super(name + "...", ColloquiaConstants.iconResource);
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
     * Exports the Resource entities in the currently selected node.
     * It does not export Resources in any sub-Resource folders.
     */
    private void export() {
        File fileName;
        Vector resources;

        // What component have we selected?  It must be a Resource folder or Activity
        if(selComponent instanceof ResourceGroup) resources = ((ResourceGroup)selComponent).getResources();
        else if(selComponent instanceof Activity) resources = ((Activity)selComponent).getResources();

        // Ask for a file name
        PFileChooser chooser = new PFileChooser();
        chooser.setDialogTitle(name);
        int returnVal = chooser.showSaveDialog(MainFrame.getInstance());
        if(returnVal != PFileChooser.APPROVE_OPTION) return;
        fileName = chooser.getSelectedFileAndStore();

        MainFrame.getInstance().setCursor(ColloquiaConstants.waitCursor);


        MainFrame.getInstance().setCursor(ColloquiaConstants.defaultCursor);
        MainFrame.getInstance().statusBar.setText(name + LanguageManager.getString("ACTION_9"));
    }
}
