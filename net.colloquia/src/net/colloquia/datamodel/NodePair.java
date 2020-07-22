package net.colloquia.datamodel;

import java.io.*;

import net.colloquia.datamodel.entities.*;

/**
 * A convenience class to contain a ColloquiaComponent and its parent
 * It has to implement Serializable for drag and drop to work
 *
 * @author Phillip Beauvoir
 * @version 2003-11-17
 */
public class NodePair
implements Serializable
{
    /**
     * The ColloquiaComponent
     * This is transient to stop drag and drop wanting serializable members
     */
    transient protected ColloquiaComponent _child;

    /**
     * The ColloquiaContainer
     * This is transient to stop drag and drop wanting serializable members
     */
    transient protected ColloquiaContainer _parent;

    /**
     * Constructor
     */
    public NodePair(ColloquiaComponent child, ColloquiaContainer parent) {
        _child = child;
        _parent = parent;
    }

    public ColloquiaComponent getChild() {
        return _child;
    }

    public ColloquiaContainer getParent() {
        return _parent;
    }
}