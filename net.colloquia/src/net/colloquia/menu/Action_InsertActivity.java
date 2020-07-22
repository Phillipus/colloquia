package net.colloquia.menu;

import java.awt.event.*;

import net.colloquia.*;
import net.colloquia.datamodel.*;
import net.colloquia.datamodel.entities.*;
import net.colloquia.util.*;
import net.colloquia.views.tree.*;

/**
 * Inserts a Activity entity and node on the Tree.  This is called from the menu.
 */
public class Action_InsertActivity
extends MenuAction
implements ComponentSelectionListener
{
    private ColloquiaComponent selComponent;
    private ColloquiaContainer parentGroup;

    public Action_InsertActivity() {
        super(LanguageManager.getString("ACTION_35"), ColloquiaConstants.iconActivityLiveFolder);
        ColloquiaTree.getInstance().addComponentSelectionListener(this);
    }

    public void componentSelected(ColloquiaComponent selComponent,
                      ColloquiaContainer parentGroup, Activity currentActivity) {
        this.selComponent = selComponent;
        this.parentGroup = parentGroup;

        // In or on an Activity
        if(currentActivity != null) setEnabled(!currentActivity.isCompleted() &&
        	!currentActivity.isInvite());

        // Activity / Templates Folder
        else if(selComponent instanceof ActivityGroup) setEnabled(true);
        else if(selComponent instanceof TemplateGroup) setEnabled(true);

        else setEnabled(false);
    }

    /**
     * Insert new Activity at currently selected node point.  This is triggered
     * from the menu.
     */
    public void actionPerformed(ActionEvent e) {
        if(selComponent == null) return;

        // Have we selected or are below an Activity, Activity Group, Template Group?
        ColloquiaContainer group;
        if(selComponent instanceof ColloquiaContainer) group = (ColloquiaContainer)selComponent;
        else group = parentGroup;
        ComponentTransferManager.addNewComponent(ColloquiaComponent.ACTIVITY, group);
    }

}


