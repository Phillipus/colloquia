package net.colloquia.menu;

import java.awt.event.*;

import net.colloquia.*;
import net.colloquia.comms.tables.*;
import net.colloquia.util.*;
import net.colloquia.views.*;

/**
 *
 */
public class Action_ImportMail
extends MenuAction
implements MainMenuItem
{
    public Action_ImportMail() {
        super(LanguageManager.getString("16_19"), ColloquiaConstants.iconEdit,
            MainFrame.getInstance().statusBar);
        setButtonText(LanguageManager.getString("BUT7"));
    }

    public void refresh() {

    }

    /**
     * Triggered from the Menu.
     */
    public void actionPerformed(ActionEvent e) {
        Inbox.getInstance().getMail(MainFrame.getInstance(), false);
    }
}