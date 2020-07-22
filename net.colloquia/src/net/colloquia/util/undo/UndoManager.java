package net.colloquia.util.undo;

import java.util.*;

public final class UndoManager
{
    private static Stack undoStack = new Stack();
    private static Stack redoStack = new Stack();
    private static Vector listeners = new Vector();

    public static void addUndoEvent(UndoEvent event) {
        undoStack.push(event);
        fireUndoableEventHappened(event);
        redoStack.removeAllElements();
    }

    public static void undoLastAction() {
        UndoEvent nextEvent;

        if(!undoStack.empty()) {
            UndoEvent event = (UndoEvent)undoStack.pop();
            event.undo();

            if(undoStack.empty()) nextEvent = null;
            else nextEvent = (UndoEvent)undoStack.peek();

            fireUndoHappened(event, nextEvent);
            redoStack.push(event);
        }
    }

    public static void redoLastAction() {
        UndoEvent nextEvent;

        if(!redoStack.empty()) {
            UndoEvent event = (UndoEvent)redoStack.pop();
            event.redo();

            if(redoStack.empty()) nextEvent = null;
            else nextEvent = (UndoEvent)redoStack.peek();

            fireRedoHappened(event, nextEvent);
            undoStack.push(event);
        }
    }

    public static void clearUndoHistory() {
        undoStack.removeAllElements();
        redoStack.removeAllElements();
        fireHistoryCleared();
    }

    public static synchronized void addUndoListener(UndoListener listener) {
        if(!listeners.contains(listener)) listeners.addElement(listener);
    }

    public static void removeUndoListener(UndoListener listener) {
        listeners.removeElement(listener);
    }

    private static void fireUndoableEventHappened(UndoEvent event) {
        for(int i = 0; i < listeners.size(); i++) {
            UndoListener listener = (UndoListener)listeners.elementAt(i);
            listener.undoableEventHappened(event);
        }
    }

    private static void fireUndoHappened(UndoEvent event, UndoEvent nextEvent) {
        for(int i = 0; i < listeners.size(); i++) {
            UndoListener listener = (UndoListener)listeners.elementAt(i);
            listener.undoHappened(event, nextEvent);
        }
    }

    private static void fireRedoHappened(UndoEvent event, UndoEvent nextEvent) {
        for(int i = 0; i < listeners.size(); i++) {
            UndoListener listener = (UndoListener)listeners.elementAt(i);
            listener.redoHappened(event, nextEvent);
        }
    }

    private static void fireHistoryCleared() {
        for(int i = 0; i < listeners.size(); i++) {
            UndoListener listener = (UndoListener)listeners.elementAt(i);
            listener.historyCleared();
        }
    }
}