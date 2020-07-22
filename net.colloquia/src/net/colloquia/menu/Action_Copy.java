package net.colloquia.menu;

import java.awt.event.*;

import net.colloquia.*;
import net.colloquia.datamodel.*;
import net.colloquia.datamodel.entities.*;
import net.colloquia.util.*;
import net.colloquia.views.*;
import net.colloquia.views.tree.*;

/**
 * Copies the selected component to the internal clipboard.  This can then be pasted
 * @see Action_Paste
 */
public class Action_Copy
extends MenuAction
implements MainMenuItem, ComponentSelectionListener
{
    private ColloquiaComponent selComponent;
    private ColloquiaContainer parentGroup;

    public Action_Copy() {
        super(LanguageManager.getString("ACTION_41"), ColloquiaConstants.iconCopy,
            MainFrame.getInstance().statusBar);
        ColloquiaTree.getInstance().addComponentSelectionListener(this);
    }

    public void refresh() {
        setEnabled(ComponentTransferManager.isCopyable(selComponent, parentGroup));
    }

    /**
     * Triggered from the Menu.  Calls {@link #copy}. <p>
     */
    public void actionPerformed(ActionEvent e) {
        ComponentTransferManager.copyToClipBoard(selComponent);
    }

    public void componentSelected(ColloquiaComponent selComponent,
                      ColloquiaContainer parentGroup, Activity currentActivity) {
        this.selComponent = selComponent;
        this.parentGroup = parentGroup;
        refresh();
    }
}

