package net.colloquia.prefs;

import java.awt.event.*;

import javax.swing.*;

import net.colloquia.*;
import net.colloquia.gui.widgets.*;
import net.colloquia.menu.*;
import net.colloquia.util.*;
import net.colloquia.views.browser.*;

/**
* A Browser for viewing my homepage
*/
public class PrefsMeBrowser
extends BrowserPanel
{
    GotoHomeAction action_GotoHome;
    SetURLAction action_SetURL;
    PTextField tfHomePage;

    public PrefsMeBrowser(PTextField tfHomePage) {
        toolBar.add(action_GotoHome = new GotoHomeAction(), 0);
        toolBar.add(action_SetURL = new SetURLAction(), 1);
        toolBar.add(new JToolBar.Separator(), 2);
        this.tfHomePage = tfHomePage;
    }

    protected class SetURLAction extends MenuAction {
        public SetURLAction() {
            super(LanguageManager.getString("4_44"), ColloquiaConstants.iconSet);
            setStatusBar(statusBar);
            setButtonText(LanguageManager.getString("BUT27"));
        }

        public void actionPerformed(ActionEvent e) {
            setURL();
        }
    }

    protected void setURL() {
        String loc = txtField.getText();
        if(loc == null || loc.length() == 0) return;

        String msg = LanguageManager.getString("4_45");
        int result = JOptionPane.showConfirmDialog
            (this,
            msg,
            LanguageManager.getString("4_46"),
            JOptionPane.YES_NO_OPTION);
        if(result != JOptionPane.YES_OPTION) return;

        tfHomePage.setText(loc);
    }

    /**
    * Goto the home page
    */
    protected class GotoHomeAction extends MenuAction {
        public GotoHomeAction() {
            super(LanguageManager.getString("4_47"), ColloquiaConstants.iconHome);
            setStatusBar(statusBar);
            setButtonText(LanguageManager.getString("BUT30"));
        }

        public void actionPerformed(ActionEvent e) {
            gotoHome();
        }
    }

    public void gotoHome() {
        setCurrentLocation(tfHomePage.getText());
    }
}
