package net.colloquia.util.undo;

public interface UndoListener {
    void undoableEventHappened(UndoEvent event);
    void undoHappened(UndoEvent event, UndoEvent nextEvent);
    void redoHappened(UndoEvent event, UndoEvent nextEvent);
    void historyCleared();
}
