package net.colloquia.menu;

import java.awt.event.*;

import net.colloquia.gui.*;
import net.colloquia.util.*;

/**
 * Shows the status window
 */
public class Action_StatusWindow extends MenuAction {
    public Action_StatusWindow() {
        super(LanguageManager.getString("STATUS_WINDOW"), null);
    }

    /**
    * Shows the status window when triggered from the View menu. <p>
    */
    public void actionPerformed(ActionEvent e) {
        StatusWindow.getInstance().showWindow();
    }
}