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
 * Launches the Send Activity Wizard to send a selected Activity.
 * This is invoked from the Menu.
 */
public class Action_SendActivity
extends MenuAction
implements ComponentSelectionListener, MainMenuItem
{
    private ColloquiaComponent selComponent;
    private static String alias = LanguageManager.getString("ACTION_44");

    public Action_SendActivity() {
        super(alias, ColloquiaConstants.iconSendActivity, MainFrame.getInstance().statusBar);
        setButtonText(LanguageManager.getString("BUT14"));
        ColloquiaTree.getInstance().addComponentSelectionListener(this);
    }

    public void componentSelected(ColloquiaComponent selComponent,
                      ColloquiaContainer parentGroup, Activity currentActivity) {
        this.selComponent = selComponent;
        refresh();
    }

    public void refresh() {
        // Can we send it?
        if(selComponent instanceof Activity) {
            Activity A = (Activity)selComponent;
            setEnabled(A.isSendable() && !A.getAllPeople().isEmpty());
        }
        else setEnabled(false);
    }

    /**
    * Launches the Send Wizard to send a selected Activity.
    * Triggered from the Menu. <p>
    */
    public void actionPerformed(ActionEvent e) {
        if(selComponent instanceof Activity) {
            new SendActivityWizard((Activity)selComponent);
        }
    }
}
