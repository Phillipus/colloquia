package net.colloquia.menu;

import java.awt.event.*;

import net.colloquia.*;
import net.colloquia.util.*;

/**
 * Exits the program by calling {@link MainFrame#closeApp}.
 */
public class Action_Exit extends MenuAction {

    public Action_Exit() {
        super(LanguageManager.getString("EXIT"), null);
    }

    /**
     * Triggered from the Menu.  Calls {@link MainFrame#closeApp}. <p>
     */
    public void actionPerformed(ActionEvent e) {
        MainFrame.getInstance().closeApp();
    }
}

