package net.colloquia.menu;

import java.awt.event.*;

import net.colloquia.*;
import net.colloquia.util.*;
import net.colloquia.views.*;

public class Action_HotInfo
extends MenuAction
{
    public Action_HotInfo() {
        super(LanguageManager.getString("ACTION_3"), ColloquiaConstants.iconAppIcon,
            MainFrame.getInstance().statusBar);
    }

    /**
     * Triggered from the Menu.
     */
    public void actionPerformed(ActionEvent e) {
    	new HotInfo();
    }

}