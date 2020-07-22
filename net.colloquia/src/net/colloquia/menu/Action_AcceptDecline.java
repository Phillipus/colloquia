package net.colloquia.menu;

import java.awt.event.*;

import net.colloquia.*;
import net.colloquia.comms.wizard.*;
import net.colloquia.datamodel.*;
import net.colloquia.datamodel.entities.*;
import net.colloquia.util.*;
import net.colloquia.views.*;
import net.colloquia.views.tree.*;

/**
 */
public class Action_AcceptDecline
extends MenuAction
implements MainMenuItem, ComponentSelectionListener
{
    private ColloquiaComponent selComponent;
    private ColloquiaContainer parentGroup;
    private Activity currentActivity;

    public Action_AcceptDecline() {
        super(LanguageManager.getString("ACTION_45"), ColloquiaConstants.iconActivityFuture);
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
        if(currentActivity != null) setEnabled(currentActivity.isInvite());
        else setEnabled(false);
    }

    /**
    */
    public void actionPerformed(ActionEvent e) {
        if(selComponent == null) return;
        if(currentActivity != null)  new AcceptActivityWizard(currentActivity);
    }

}
