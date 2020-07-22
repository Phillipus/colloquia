package net.colloquia.menu;

import java.awt.event.*;

import net.colloquia.*;
import net.colloquia.util.*;
import net.colloquia.views.*;

/**
 * Shows the wonderful 'About' dialog box for all to see! <br>
 * Displays the version, the build number and the build date.
 */
public class Action_About
extends MenuAction
{
    static String name = LanguageManager.getString("ABOUT");

    public Action_About() {
        super(name + "...", null, MainFrame.getInstance().statusBar);
    }

    /**
     * Shows the dialog box when triggered from the Help menu. <p>
     */
    public void actionPerformed(ActionEvent e) {
        new AboutBox();
    }

}

