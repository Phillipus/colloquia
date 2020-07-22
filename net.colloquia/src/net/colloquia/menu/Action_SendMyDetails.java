package net.colloquia.menu;

import java.awt.event.*;

import net.colloquia.*;
import net.colloquia.comms.wizard.*;
import net.colloquia.datamodel.*;
import net.colloquia.datamodel.entities.*;
import net.colloquia.util.*;
import net.colloquia.views.tree.*;

/**
 * Send my details to people
 */
public class Action_SendMyDetails
extends MenuAction
implements ComponentSelectionListener
{
    private Activity currentActivity;
    private static String alias = LanguageManager.getString("SEND") +
                                    " " + LanguageManager.getString("MY_DETAILS");

    public Action_SendMyDetails() {
        super(alias, ColloquiaConstants.iconMyDetails);
        ColloquiaTree.getInstance().addComponentSelectionListener(this);
    }

    public void componentSelected(ColloquiaComponent selComponent,
                      ColloquiaContainer parentGroup, Activity currentActivity) {
        this.currentActivity = currentActivity;
    }

    public void actionPerformed(ActionEvent e) {
        new SendMyDetailsWizard(currentActivity);
    }
}
