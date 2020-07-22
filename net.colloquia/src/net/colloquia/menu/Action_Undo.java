package net.colloquia.menu;

import java.awt.event.*;

import net.colloquia.*;
import net.colloquia.util.*;
import net.colloquia.util.undo.*;

/**
 * Undo cut/paste/move of nodes
 */
public class        Action_Undo
extends             MenuAction
implements          UndoListener
{

    public Action_Undo() {
        super(LanguageManager.getString("UNDO"), null,
            MainFrame.getInstance().statusBar);
        setEnabled(false);
        UndoManager.addUndoListener(this);
    }

    public void actionPerformed(ActionEvent e) {
        UndoManager.undoLastAction();
    }

    public void historyCleared() {
        clearMenu();
    }

    public void clearMenu() {
        setEnabled(false);
        setText(LanguageManager.getString("UNDO"));
    }

    public void undoableEventHappened(UndoEvent event) {
        setEnabled(true);
        setText(event.getUndoEventName());
    }

    public void undoHappened(UndoEvent event, UndoEvent nextEvent) {
        if(nextEvent == null) clearMenu();
        else setText(nextEvent.getUndoEventName());
    }

    public void redoHappened(UndoEvent event, UndoEvent nextEvent) {
        setEnabled(true);
        setText(event.getUndoEventName());
    }

}
