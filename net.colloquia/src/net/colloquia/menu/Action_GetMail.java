package net.colloquia.menu;

import java.awt.event.*;

import net.colloquia.*;
import net.colloquia.comms.tables.*;
import net.colloquia.util.*;
import net.colloquia.views.*;

/**
 *
 */
public class Action_GetMail
extends MenuAction
implements MainMenuItem
{
    public Action_GetMail() {
        super(LanguageManager.getString("16_18"), ColloquiaConstants.iconGetMail,
            MainFrame.getInstance().statusBar);
        setButtonText(LanguageManager.getString("BUT6"));
    }

    public void refresh() {

    }

    /**
     * Triggered from the Menu.
     */
    public void actionPerformed(ActionEvent e) {
        Inbox.getInstance().getMail(MainFrame.getInstance(), true);
    }
}
