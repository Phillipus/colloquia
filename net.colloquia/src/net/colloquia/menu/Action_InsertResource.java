package net.colloquia.menu;

import java.awt.event.*;

import net.colloquia.*;
import net.colloquia.datamodel.*;
import net.colloquia.datamodel.entities.*;
import net.colloquia.util.*;
import net.colloquia.views.tree.*;

/**
 * Inserts a Resource entity and node on the Tree.  This is called from the menu.
 */
public class Action_InsertResource
extends MenuAction
implements ComponentSelectionListener
{
    protected ColloquiaComponent selComponent;
    protected ColloquiaContainer parentGroup;
    protected ColloquiaComponent newComponent;

    public Action_InsertResource() {
        super(LanguageManager.getString("ACTION_38"), ColloquiaConstants.iconResource);
        ColloquiaTree.getInstance().addComponentSelectionListener(this);
    }

    public void componentSelected(ColloquiaComponent selComponent,
                      ColloquiaContainer parentGroup, Activity currentActivity) {
        this.selComponent = selComponent;
        this.parentGroup = parentGroup;

        // In or on an Activity
        if(currentActivity != null) setEnabled(!currentActivity.isCompleted() &&
        	!currentActivity.isInvite());

        // Resource Folder
        else if(selComponent instanceof ResourceGroup) setEnabled(true);
        else if(parentGroup instanceof ResourceGroup) setEnabled(true);

        else setEnabled(false);
    }

    /**
     * Insert new Resource at currently selected node point.  This is triggered
     * from the menu.
     */
    public void actionPerformed(ActionEvent e) {
        newComponent = null;
        if(selComponent == null) return;
        ColloquiaContainer group;
        if(selComponent instanceof ColloquiaContainer) group = (ColloquiaContainer)selComponent;
        else group = parentGroup;
        newComponent = ComponentTransferManager.addNewComponent(ColloquiaComponent.RESOURCE, group);
    }
}
