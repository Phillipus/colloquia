package net.colloquia.menu;

import java.awt.event.*;

import javax.swing.*;

import net.colloquia.*;
import net.colloquia.datamodel.*;
import net.colloquia.datamodel.entities.*;
import net.colloquia.util.*;
import net.colloquia.views.*;
import net.colloquia.views.tree.*;

/**
 */
public class Action_ShowResources
extends JCheckBoxMenuItem
implements MainMenuItem, ComponentSelectionListener, ActionListener
{
    private ColloquiaComponent selComponent;
    private ColloquiaContainer parentGroup;

    public Action_ShowResources() {
        super(LanguageManager.getString("21_12"), Utils.getIcon(ColloquiaConstants.iconResource));
        ColloquiaTree.getInstance().addComponentSelectionListener(this);
        addActionListener(this);
    }

    public void componentSelected(ColloquiaComponent selComponent,
                      ColloquiaContainer parentGroup, Activity currentActivity) {
        this.selComponent = selComponent;
        this.parentGroup = parentGroup;
        refresh();
    }

    public void refresh() {
        setEnabled(selComponent instanceof Activity);
        if(selComponent instanceof Activity) {
            Activity A = (Activity)selComponent;
            setSelected(ColloquiaTree.getInstance().isResourcesVisible(A));
        }
        else setSelected(false);
    }

    public void actionPerformed(ActionEvent e) {
        if(selComponent instanceof Activity) {
            Activity A = (Activity)selComponent;
			ColloquiaTree.getInstance().displayResources(A, isSelected());
        }
    }
}