package net.colloquia.datamodel;

import java.util.*;

import net.colloquia.datamodel.entities.*;
import net.colloquia.util.undo.*;
import net.colloquia.views.tree.*;

/**
 * The connecting link between the DataModel and the ColloquiaTreeModel / ColloquiaTree
 * and the Current View
 * DataModel ----> DataToViewAdaptor ----> ColloquiaTreeModel ----> ColloquiaTree
 *                                   ----> Current View
 */
public class DataToViewAdaptor implements DataListener {

    /**
     * New Data, New Tree
     * Do any clearing or initialising here
     */
    public void newDataCreated() {
        // Tell the Tree
        ColloquiaTree.getInstance().newTree();
        // Expand Tree
        ColloquiaTree.getInstance().expandTree(true);
        // Select Activities Node
        ColloquiaTree.getInstance().selectNodeByObject(DataModel.getActivityGroup());
        // Clear copy
        ComponentTransferManager.clearClipBoard();
        // Clear copy
        UndoManager.clearUndoHistory();
    }


    /**
     * Add component to Activity - Person, Resource, Activity or Assignment
     */
    public void componentAdded(ColloquiaComponent tc, ColloquiaContainer group) {
        ColloquiaTree.getInstance().addComponent(tc, group);
    }

    /**
     * Remove component from Group - Person, Resource, Activity or Assignment
     */
    public void componentRemoved(ColloquiaComponent tc, ColloquiaContainer group) {
        ColloquiaTree.getInstance().removeComponent(tc, group);
    }

    public void componentOrderChanged(Vector members, ColloquiaContainer group) {
        ColloquiaTree.getInstance().componentOrderChanged(members, group);
    }
}
