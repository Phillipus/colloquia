package net.colloquia.views.entities;

import javax.swing.*;
import javax.swing.event.*;

import net.colloquia.*;
import net.colloquia.datamodel.entities.*;
import net.colloquia.util.*;

public class BasePersonView
extends PersonView
{
    private static final BasePersonView instance = new BasePersonView();

    // Constructor
    private BasePersonView() {
        tabPane.addTab(LanguageManager.getString("12_2"), null, mainPanel);
        tabPane.addTab(LanguageManager.getString("12_5"), null, new JPanel());
        tabPane.addTab(LanguageManager.getString("1"), null, new JPanel());
    }

    public static BasePersonView getInstance() {
        return instance;
    }

    /** Click on tab pane */
    public void stateChanged(ChangeEvent e) {
        int tab = tabPane.getSelectedIndex();
        int mainSplitPos = mainSplit.getDividerLocation();
        int topSplitPos = topSplit.getDividerLocation();
        tabPane.setComponentAt(lastTabSelected, new JPanel());
        tabPane.setComponentAt(tab, mainPanel);

        switch(tab) {
            case 0:
                topSplit.setRightComponent(ds);
                //mainSplit.setTopComponent(topSplit);
                //mainSplit.setBottomComponent(browser);
                break;
            case 1:
                topSplit.setRightComponent(notesEditor);
                //mainSplit.setTopComponent(notesEditor);
                //mainSplit.setBottomComponent(browser);
                break;
            case 2:
                topSplit.setRightComponent(activitySummaryPeopleView);
                //mainSplit.setBottomComponent(browser);
                //mainSplit.setTopComponent(new JPanel());
                //mainSplit.setBottomComponent(activitySummaryPeopleView);
                break;
        }
        if(topSplitPos != 0) topSplit.setDividerLocation(topSplitPos);
        if(mainSplitPos != 0) mainSplit.setDividerLocation(mainSplitPos);
        lastTabSelected = tab;
    }

    public void setComponent(ColloquiaComponent tc, ColloquiaContainer parentGroup) {
        if(!(tc instanceof Person) || parentGroup == null) return;

        MainFrame.getInstance().statusBar.setText(msgLoading + tc.getName() + ".......");
        super.setComponent(tc, parentGroup);
        MainFrame.getInstance().statusBar.clearText();
    }

    public void updateView() {
        setComponent(tc, parentGroup);
    }
}
