package net.colloquia.menu;

import java.awt.event.*;

import net.colloquia.datamodel.*;
import net.colloquia.datamodel.entities.*;
import net.colloquia.util.*;
import net.colloquia.views.tree.*;

/**
 * Expands/Contracts a Tree Branch and its child nodes.
 * This is invoked from the Menu.
 */
public class Action_ExpandBranch
extends MenuAction
implements ComponentSelectionListener
{
    private boolean expand;
    private ColloquiaComponent selComponent;

    /**
     * Constructor for the action to be triggered.
     * @param expand If <b>true</b> will expand the node, if <b>false</b> will
     * contract the node.
     */
    public Action_ExpandBranch(boolean expand) {
        super(expand ? LanguageManager.getString("ACTION_4") : LanguageManager.getString("ACTION_5"), null);
        this.expand = expand;
        ColloquiaTree.getInstance().addComponentSelectionListener(this);
    }

    public void componentSelected(ColloquiaComponent selComponent,
                      ColloquiaContainer parentGroup, Activity currentActivity) {
        this.selComponent = selComponent;
        setEnabled(selComponent instanceof ColloquiaContainer);
    }

    /**
     * Triggered from the Menu.
     */
    public void actionPerformed(ActionEvent e) {
        if(selComponent == null) return;
        if(!(selComponent instanceof ColloquiaContainer)) return;
        ColloquiaTree.getInstance().expandBranch((ColloquiaContainer)selComponent, expand);
    }

}
