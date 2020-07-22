package net.colloquia.menu;

import java.awt.event.*;

import net.colloquia.util.*;
import net.colloquia.views.help.*;


/**
 * Invokes the Help Window from the Menu or by pressing F1.
 */
public class Action_Help extends MenuAction {
    public Action_Help() {
        super(LanguageManager.getString("HELP"), null);
    }

    /**
     * Triggered from the Menu or F1 key.  Shows the Help Window. <p>
     */
    public void actionPerformed(ActionEvent e) {
        HelpWindow.getInstance().showHelp();
    }

}
