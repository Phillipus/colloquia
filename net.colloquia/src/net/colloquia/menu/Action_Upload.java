package net.colloquia.menu;

import java.awt.event.*;

import net.colloquia.*;
import net.colloquia.datamodel.*;
import net.colloquia.datamodel.entities.*;
import net.colloquia.util.*;
import net.colloquia.views.*;
import net.colloquia.views.tree.*;

/**
 */
public class Action_Upload
extends MenuAction
implements ComponentSelectionListener, MainMenuItem
{
    private ColloquiaComponent selComponent;
    private static String alias = LanguageManager.getString("ACTION_1");

    public Action_Upload() {
        super(alias, ColloquiaConstants.iconResource);
        ColloquiaTree.getInstance().addComponentSelectionListener(this);
    }

    public void componentSelected(ColloquiaComponent selComponent,
                      ColloquiaContainer parentGroup, Activity currentActivity) {
        this.selComponent = selComponent;
        refresh();
    }

    public void refresh() {
        // Can we send it?
        if(selComponent instanceof Resource) {
            setEnabled(selComponent.isMine());
        }
        else setEnabled(false);
    }

    /**
    */
    public void actionPerformed(ActionEvent e) {
        if(selComponent instanceof Resource && selComponent.isMine()) {
            new UploadFileDialog(selComponent);
        }
    }

}
