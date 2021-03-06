package net.colloquia.menu;

import java.awt.event.*;

import net.colloquia.*;
import net.colloquia.datamodel.*;
import net.colloquia.datamodel.entities.*;
import net.colloquia.util.*;
import net.colloquia.views.tree.*;

/**
 * Inserts a PersonGroup entity and node on the Tree.  This is called from the menu.
 */
public class Action_InsertPersonGroup
extends MenuAction
implements ComponentSelectionListener
{
    private ColloquiaComponent selComponent;
    private ColloquiaContainer parentGroup;

    public Action_InsertPersonGroup() {
        super(LanguageManager.getString("ACTION_2"), ColloquiaConstants.iconFolder);
        ColloquiaTree.getInstance().addComponentSelectionListener(this);
    }

    public void componentSelected(ColloquiaComponent selComponent,
                      ColloquiaContainer parentGroup, Activity currentActivity) {
        this.selComponent = selComponent;
        this.parentGroup = parentGroup;
        setEnabled(selComponent instanceof PersonGroup);
    }

    /**
     * Insert new PersonGroup at currently selected node point.  This is triggered
     * from the menu.
     */
    public void actionPerformed(ActionEvent e) {
        if(selComponent == null) return;
        ColloquiaContainer group;
        if(selComponent instanceof ColloquiaContainer) group = (ColloquiaContainer)selComponent;
        else group = parentGroup;
        ComponentTransferManager.addNewComponent(ColloquiaComponent.PERSON_GROUP, group);
    }

}
