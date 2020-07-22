package net.colloquia.menu;

import java.awt.event.*;

import javax.swing.*;

import net.colloquia.*;
import net.colloquia.datamodel.*;
import net.colloquia.datamodel.entities.*;
import net.colloquia.util.*;
import net.colloquia.views.tree.*;

/**
 * Renames a node and its component on the tree.
 * This is invoked from the Edit menu.
 */
public class    Action_Rename
extends         MenuAction
implements      ComponentSelectionListener
{
    private static String alias = LanguageManager.getString("RENAME");
    private ColloquiaComponent selComponent;

    public Action_Rename()
    {
        super(alias, null);
        ColloquiaTree.getInstance().addComponentSelectionListener(this);
    }

    /**
     * Triggered from the Menu.  Calls {@link #rename}. <p>
     */
    public void actionPerformed(ActionEvent e) {
        rename();
    }

    public void componentSelected(ColloquiaComponent selComponent,
                      ColloquiaContainer parentGroup, Activity currentActivity) {
        this.selComponent = selComponent;
        // Are we a core group?
        if(parentGroup.getName().equalsIgnoreCase("Root")) setEnabled(false);
        else setEnabled(selComponent.isMine());
    }

    /**
     * Over-ride this so we can set the editable state of the tree too
     * Thus allowing in-line renames
     */
    public void setEnabled(boolean newValue) {
        super.setEnabled(newValue);
        //ColloquiaTree.getInstance().setEditable(newValue);
    }

    /**
     * Renames the selected node and its component on the tree with a dialog box
     */
    private void rename() {
        if(selComponent == null) return;

        // Save old name
        String oldName = selComponent.getName();

        // Ask for a new name
        String newName = (String)JOptionPane.showInputDialog(MainFrame.getInstance(),
                        LanguageManager.getString("ACTION_14"), alias, JOptionPane.QUESTION_MESSAGE,
                        null, null, oldName);

        selComponent.rename(newName);

        // Notify tree
        ColloquiaTree.getInstance().updateSelectedNode();
    }

}


