package net.colloquia.menu;

import java.awt.event.*;

import net.colloquia.*;
import net.colloquia.datamodel.*;
import net.colloquia.datamodel.entities.*;
import net.colloquia.util.*;
import net.colloquia.views.*;
import net.colloquia.views.tree.*;

/**
 * Pastes the component that is stored on the internal clipboard to the current
 * tree position.
 * @see Action_Copy
 */
public class Action_Paste
extends MenuAction
implements MainMenuItem, ComponentSelectionListener
{
    private ColloquiaComponent selComponent;

    public Action_Paste() {
        super(LanguageManager.getString("ACTION_42"), ColloquiaConstants.iconPaste,
            MainFrame.getInstance().statusBar);
        ColloquiaTree.getInstance().addComponentSelectionListener(this);
    }

    public void refresh() {
        // The current selected component must be a group
        if(!(selComponent instanceof ColloquiaContainer)) setEnabled(false);
        else setEnabled(ComponentTransferManager.isClipBoardPastable((ColloquiaContainer)selComponent));
    }

    /**
     * Triggered from the Menu.  Calls {@link #paste}. <p>
     */
    public void actionPerformed(ActionEvent e) {
        if(selComponent instanceof ColloquiaContainer) {
            ComponentTransferManager.pasteFromClipBoard((ColloquiaContainer)selComponent);
            // Reset toolbar / menu
            setEnabled(ComponentTransferManager.isClipBoardPastable((ColloquiaContainer)selComponent));
        }
    }

    public void componentSelected(ColloquiaComponent selComponent,
                      ColloquiaContainer parentGroup, Activity currentActivity) {
        this.selComponent = selComponent;
        refresh();
    }
}


