package net.colloquia.menu;

import java.awt.event.*;

import net.colloquia.*;
import net.colloquia.datamodel.*;
import net.colloquia.datamodel.entities.*;
import net.colloquia.util.*;
import net.colloquia.views.tree.*;

/**
 * Inserts a Person entity and node on the Tree.  This is called from the menu.
 */
public class Action_InsertPerson
extends MenuAction
implements ComponentSelectionListener
{
    private ColloquiaComponent selComponent;
    private ColloquiaContainer parentGroup;

    public Action_InsertPerson() {
        super(LanguageManager.getString("ACTION_37"), ColloquiaConstants.iconPerson);
        ColloquiaTree.getInstance().addComponentSelectionListener(this);
    }

    public void componentSelected(ColloquiaComponent selComponent,
                      ColloquiaContainer parentGroup, Activity currentActivity) {
        this.selComponent = selComponent;
        this.parentGroup = parentGroup;

        // In or on an Activity
        if(currentActivity != null) {
            setEnabled(currentActivity.isMine() && !currentActivity.isCompleted()
                    && !currentActivity.isTemplate());
        }

        // People Folder
        else if(selComponent instanceof PersonGroup) setEnabled(true);
        else if(parentGroup instanceof PersonGroup) setEnabled(true);

        else setEnabled(false);
    }

    /**
     * Insert new Person at currently selected node point.  This is triggered
     * from the menu.
     */
    public void actionPerformed(ActionEvent e) {
        if(selComponent == null) return;
        ColloquiaContainer group;
        if(selComponent instanceof ColloquiaContainer) group = (ColloquiaContainer)selComponent;
        else group = parentGroup;
        ComponentTransferManager.addNewComponent(ColloquiaComponent.PERSON, group);
    }

}
