package net.colloquia.views.tree;

import net.colloquia.datamodel.entities.*;
import uk.ac.reload.dweezil.gui.tree.*;

/**
* A tree node that contains a ColloquiaComponent.
*/
public class ColloquiaTreeNode
extends DweezilTreeNode
{
    public boolean showPeople = true;
    public boolean showResources = true;
    public boolean showAssignment = true;

    private ColloquiaComponent tc;

    public ColloquiaTreeNode(ColloquiaComponent tc) {
        super(tc);
        this.tc = tc;
    }

    public ColloquiaComponent getComponent() {
        return tc;
    }

    public String getName() {
        return tc.getName();
    }

    public String getGUID() {
        return tc.getGUID();
    }

}
