package net.colloquia.menu;

import java.awt.event.*;

import net.colloquia.*;
import net.colloquia.datamodel.*;
import net.colloquia.datamodel.entities.*;
import net.colloquia.util.*;
import net.colloquia.views.*;
import net.colloquia.views.tree.*;

/**
 * Cuts the selected tree node and copies it to the internal clipboard.
 */
public class Action_Cut
extends MenuAction
implements MainMenuItem, ComponentSelectionListener
{
    private ColloquiaComponent selComponent;
    private ColloquiaContainer parentGroup;

    public Action_Cut() {
        super(LanguageManager.getString("ACTION_40"), ColloquiaConstants.iconCut,
            MainFrame.getInstance().statusBar);
        ColloquiaTree.getInstance().addComponentSelectionListener(this);
    }

    public void componentSelected(ColloquiaComponent selComponent,
                      ColloquiaContainer parentGroup, Activity currentActivity) {
        this.selComponent = selComponent;
        this.parentGroup = parentGroup;
        refresh();
    }

    public void refresh() {
        setEnabled(ComponentTransferManager.isRemovable(selComponent, parentGroup));
    }

    /**
     * Triggered from the Menu.
     */
    public void actionPerformed(ActionEvent e) {
        ComponentTransferManager.cut(selComponent, parentGroup);
    }
}

