package net.colloquia.menu;

import java.awt.event.*;

import net.colloquia.*;
import net.colloquia.util.*;
import net.colloquia.util.undo.*;

/**
 * Redo cut/paste/move of nodes
 */
public class        Action_Redo
extends             MenuAction
implements          UndoListener
{

    public Action_Redo() {
        super(LanguageManager.getString("REDO"), null, MainFrame.getInstance().statusBar);
        setEnabled(false);
        UndoManager.addUndoListener(this);
    }

    /**
     * Triggered from the Menu.
     */
    public void actionPerformed(ActionEvent e) {
        UndoManager.redoLastAction();
    }

    public void historyCleared() {
        clearMenu();
    }

    public void clearMenu() {
        setEnabled(false);
        setText(LanguageManager.getString("REDO"));
    }

    public void undoableEventHappened(UndoEvent event) {
        clearMenu();
    }

    public void undoHappened(UndoEvent event, UndoEvent nextEvent) {
        setEnabled(true);
        setText(event.getRedoEventName());
    }

    public void redoHappened(UndoEvent event, UndoEvent nextEvent) {
        if(nextEvent == null) clearMenu();
        else setText(nextEvent.getRedoEventName());
    }

}