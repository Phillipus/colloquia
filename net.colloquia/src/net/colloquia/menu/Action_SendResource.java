package net.colloquia.menu;

import java.awt.event.*;

import net.colloquia.*;
import net.colloquia.comms.wizard.*;
import net.colloquia.datamodel.*;
import net.colloquia.datamodel.entities.*;
import net.colloquia.util.*;
import net.colloquia.views.tree.*;

/**
 * Launches the Send Resource Wizard to send a selected Resource.
 * This is invoked from the Menu.
 */
public class Action_SendResource
extends MenuAction
implements ComponentSelectionListener
{
    private ColloquiaComponent selComponent;
    private ColloquiaContainer parentGroup;
    private static String alias = LanguageManager.getString("SEND") + " " + LanguageManager.getString("8");

    public Action_SendResource() {
        super(alias, ColloquiaConstants.iconResource);
        ColloquiaTree.getInstance().addComponentSelectionListener(this);
    }

    public void componentSelected(ColloquiaComponent selComponent,
                      ColloquiaContainer parentGroup, Activity currentActivity) {
        this.selComponent = selComponent;
        this.parentGroup = parentGroup;
        setEnabled(isValid());
    }

    /**
    * Launches the Send Wizard to send a selected Resource.
    * Triggered from the Menu. <p>
    */
    public void actionPerformed(ActionEvent e) {
        if(isValid()) new SendResourceWizard((Resource)selComponent, parentGroup);
    }

    private boolean isValid() {
        if(!(selComponent instanceof Resource)) return false;
        if(parentGroup instanceof Activity) {
            Activity A = (Activity)parentGroup;
            if(!A.isMine()) return A.acceptsResources();
            else return true;
        }
        else return selComponent.isMine();
    }
}