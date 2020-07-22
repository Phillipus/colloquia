package net.colloquia.util.undo;

public abstract class UndoEvent
{
    public abstract void undo();
    public abstract void redo();
    public abstract String getUndoEventName();
    public abstract String getRedoEventName();
}
