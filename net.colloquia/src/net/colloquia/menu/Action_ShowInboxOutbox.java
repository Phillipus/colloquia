package net.colloquia.menu;

import java.awt.event.*;

import net.colloquia.*;
import net.colloquia.comms.tables.*;
import net.colloquia.util.*;

/**
 * Shows the Inbox/Outbox page.  Invoked from the Menu as a toggle.
 */
public class Action_ShowInboxOutbox extends MenuAction {
    private static String alias = LanguageManager.getString("MESSAGECENTRE");

    public Action_ShowInboxOutbox() {
        super(alias, ColloquiaConstants.iconInOutbox, MainFrame.getInstance().statusBar);
        setButtonText(LanguageManager.getString("BUT5"));
    }

    /**
     * Triggered from the Menu.  Calls {@link #showInboxOutbox}. <p>
     */
    public void actionPerformed(ActionEvent e) {
        MessageWindow.getInstance().showWindow();
    }
}
