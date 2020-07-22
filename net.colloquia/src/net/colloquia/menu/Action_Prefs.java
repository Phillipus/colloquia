package net.colloquia.menu;

import java.awt.event.*;

import net.colloquia.*;
import net.colloquia.prefs.*;
import net.colloquia.util.*;

/**
 * Shows the Preferences page.  Invoked from the Menu as a toggle.
 */
public class Action_Prefs
extends MenuAction
{

    public Action_Prefs() {
        super(LanguageManager.getString("MY_DETAILS"), ColloquiaConstants.iconMyDetails,
            MainFrame.getInstance().statusBar);
        setButtonText(LanguageManager.getString("BUT4"));
    }

    /**
     * Triggered from the Menu.  Calls {@link #show}. <p>
     */
    public void actionPerformed(ActionEvent e) {
        PrefsWindow.getInstance().showWindow();
    }
}
