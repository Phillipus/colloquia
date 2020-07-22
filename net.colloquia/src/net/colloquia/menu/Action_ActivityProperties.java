package net.colloquia.menu;

import java.awt.event.*;

import net.colloquia.*;
import net.colloquia.datamodel.*;
import net.colloquia.datamodel.entities.*;
import net.colloquia.util.*;
import net.colloquia.views.*;
import net.colloquia.views.tree.*;

public class Action_ActivityProperties
extends MenuAction
implements MainMenuItem, ComponentSelectionListener
{
    protected ColloquiaComponent selComponent;

    public Action_ActivityProperties() {
        super(LanguageManager.getString("ACTION_43"), ColloquiaConstants.iconActivityLiveFolder,
            MainFrame.getInstance().statusBar);
        ColloquiaTree.getInstance().addComponentSelectionListener(this);
    }

    public void componentSelected(ColloquiaComponent selComponent,
                      ColloquiaContainer parentGroup, Activity currentActivity) {
        this.selComponent = selComponent;
        refresh();
    }

    public void refresh() {
        if(selComponent instanceof Activity) {
        	Activity A = (Activity)selComponent;
            setEnabled(A.isLive() || A.isCompleted());
        }
        else setEnabled(false);
    }

    /**
     * Triggered from the Menu.
     */
    public void actionPerformed(ActionEvent e) {
        if(selComponent == null) return;
        if(!(selComponent instanceof Activity)) return;
        Activity A = (Activity)selComponent;
        new Activity_PropertiesView(A);
    }

}