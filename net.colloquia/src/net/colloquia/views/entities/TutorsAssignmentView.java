package net.colloquia.views.entities;

import javax.swing.*;
import javax.swing.event.*;

import net.colloquia.*;
import net.colloquia.datamodel.entities.*;
import net.colloquia.util.*;

/*
 The tutor's view of an assignment
 */
public class TutorsAssignmentView extends AssignmentView {
    private static final TutorsAssignmentView instance = new TutorsAssignmentView();

    private TutorsAssignmentView() {
        mainSplit.setTopComponent(ds);
        mainSplit.setBottomComponent(browser);
        tabPane.addTab(LanguageManager.getString("2"), null, mainPanel);
        tabPane.addTab(LanguageManager.getString("12_3"), null, new JPanel());
        tabPane.addTab(LanguageManager.getString("12_5"), null, new JPanel());
        tabPane.addChangeListener(this);
   }

    public static TutorsAssignmentView getInstance() {
        return instance;
    }

    public void setComponent(ColloquiaComponent tc, ColloquiaContainer parentGroup) {
        if(!(tc instanceof Assignment) || parentGroup == null) return;

        MainFrame.getInstance().statusBar.setText(msgLoading + tc.getName() + ".......");
        super.setComponent(tc, parentGroup);
        MainFrame.getInstance().statusBar.clearText();
    }

    /** Click on tab pane */
    public void stateChanged(ChangeEvent e) {
        int tab = tabPane.getSelectedIndex();
        int dividerLocation = mainSplit.getDividerLocation();
        tabPane.setComponentAt(lastTabSelected, new JPanel());
        tabPane.setComponentAt(tab, mainPanel);

        switch(tab) {
            case 0:
                mainSplit.setTopComponent(ds);
                mainSplit.setBottomComponent(browser);
                break;
            case 1:
                mainSplit.setTopComponent(descriptionEditor);
                mainSplit.setBottomComponent(browser);
                break;
            case 2:
                mainSplit.setTopComponent(notesEditor);
                mainSplit.setBottomComponent(browser);
                break;
        }
        if(dividerLocation != 0) mainSplit.setDividerLocation(dividerLocation);
        lastTabSelected = tab;
    }

}
