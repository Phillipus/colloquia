package net.colloquia.menu;

import java.awt.event.*;

import net.colloquia.util.*;
import net.colloquia.views.tree.*;

/**
 * Expands/Contracts every node on the Tree.
 * This is invoked from the Menu.
 */
public class Action_ExpandTree extends MenuAction {
    private boolean expand;

    /**
     * Constructor for the action to be triggered.
     * @param expand If <b>true</b> will expand the tree, if <b>false</b> will
     * contract the tree.
     */
    public Action_ExpandTree(boolean expand) {
        super(expand ? LanguageManager.getString("ACTION_6") : LanguageManager.getString("ACTION_7"), null);
        this.expand = expand;
    }

    /**
     * Triggered from the Menu.  Calls {@link #expandTree}. <p>
     */
    public void actionPerformed(ActionEvent e) {
        ColloquiaTree.getInstance().expandTree(expand);
    }

}
