package net.colloquia.menu;

import java.awt.event.*;

import net.colloquia.datamodel.*;
import net.colloquia.datamodel.entities.*;
import net.colloquia.util.*;
import net.colloquia.views.*;
import net.colloquia.views.tree.*;

/**
 * Shows the Properties dialog for the currently selected node.
 * Invoked from the Menu as a toggle.
 * @see PropertiesDialog
 */
public class Action_ViewProperties
extends MenuAction
implements ComponentSelectionListener
{
    private ColloquiaComponent selComponent;

    public Action_ViewProperties() {
        super(LanguageManager.getString("PROPERTIES"), null);
        ColloquiaTree.getInstance().addComponentSelectionListener(this);
    }

    public void componentSelected(ColloquiaComponent selComponent,
                      ColloquiaContainer parentGroup, Activity currentActivity) {
        this.selComponent = selComponent;
    }

    /**
     * Triggered from the Menu.  Creates a new {@link PropertiesDialog}. <p>
     */
    public void actionPerformed(ActionEvent e) {
        if(selComponent == null) return;
        new PropertiesDialog(selComponent);
    }
}
