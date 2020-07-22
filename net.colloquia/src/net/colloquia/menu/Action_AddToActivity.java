package net.colloquia.menu;

import java.awt.event.*;

import net.colloquia.*;
import net.colloquia.datamodel.*;
import net.colloquia.datamodel.entities.*;
import net.colloquia.util.*;
import net.colloquia.views.*;
import net.colloquia.views.tree.*;

/**
 * Inserts Activity members
 */
public class Action_AddToActivity
extends MenuAction
implements MainMenuItem, ComponentSelectionListener
{
    private ColloquiaComponent selComponent;
    private ColloquiaContainer parentGroup;
    private Activity currentActivity;

    public Action_AddToActivity() {
        super(LanguageManager.getString("ACTION_20"), ColloquiaConstants.iconAddPeopleResources);
        ColloquiaTree.getInstance().addComponentSelectionListener(this);
    }

    public void componentSelected(ColloquiaComponent selComponent,
                      ColloquiaContainer parentGroup, Activity currentActivity) {
        this.selComponent = selComponent;
        this.parentGroup = parentGroup;
        this.currentActivity = currentActivity;
        refresh();
    }

    public void refresh() {
        // In or on an Activity
        if(currentActivity != null) setEnabled(!currentActivity.isCompleted() &&
        	!currentActivity.isInvite());
        else setEnabled(false);
    }

    /**
    */
    public void actionPerformed(ActionEvent e) {
        if(selComponent == null) return;
        if(currentActivity != null) {
            if(!currentActivity.isCompleted()) {
                ComponentTransferManager.addToActivity(currentActivity);
            }
        }
    }

}

