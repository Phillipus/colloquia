package net.colloquia.menu;

import java.awt.event.*;

import net.colloquia.*;
import net.colloquia.comms.index.*;
import net.colloquia.comms.tables.*;
import net.colloquia.util.*;
import net.colloquia.views.*;

/**
 *
 */
public class Action_SendMail
extends MenuAction
implements MainMenuItem, MessageBoxListener
{
    public Action_SendMail() {
        super(LanguageManager.getString("16_25"), ColloquiaConstants.iconSendMail,
            MainFrame.getInstance().statusBar);
        setButtonText(LanguageManager.getString("BUT9"));
        Outbox.getInstance().addMessageBoxListener(this);
        refresh();
    }

    public void refresh() {
    	setEnabled(Outbox.getInstance().hasMessages());
    }

    public void messageboxChanged(MessageIndex index) {
    	setEnabled(!index.isEmpty());
    }

    /**
    * Triggered from the Menu.
    */
    public void actionPerformed(ActionEvent e) {
        Outbox.getInstance().sendAllMail(MainFrame.getInstance());
    }
}