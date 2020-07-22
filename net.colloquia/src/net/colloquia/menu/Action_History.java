package net.colloquia.menu;

import java.awt.event.*;

import net.colloquia.*;
import net.colloquia.util.*;
import net.colloquia.views.tree.*;

/**
 */
public class Action_History
extends MenuAction
{
    public static int BACK = 0;
    public static int FORWARD = 1;
    private int direction;

    public Action_History(int direction) {
        super(direction == FORWARD ? LanguageManager.getString("FORWARD") : LanguageManager.getString("BACK"),
              direction == FORWARD ? ColloquiaConstants.iconForward : ColloquiaConstants.iconBack,
              MainFrame.getInstance().statusBar);
        setButtonText(direction == BACK ? LanguageManager.getString("BUT1") : LanguageManager.getString("BUT2"));
        this.direction = direction;
    }

    /**
    * Triggered from the Menu.
    */
    public void actionPerformed(ActionEvent e) {
        if(direction == FORWARD) ColloquiaTree.getInstance().selectNextNodeInHistory();
        else ColloquiaTree.getInstance().selectPreviousNodeInHistory();
    }
}
