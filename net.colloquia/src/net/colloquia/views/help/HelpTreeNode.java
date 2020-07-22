package net.colloquia.views.help;

import java.net.*;

import uk.ac.reload.dweezil.gui.tree.*;

class HelpTreeNode
extends DweezilTreeNode
{
    private String treeName;

    public HelpTreeNode() {}

    public HelpTreeNode(URL url, String treeName) {
        super(url);
        this.treeName = treeName;
    }

    public URL getURL() {
        return (URL)getUserObject();
    }

    public String toString() {
        return treeName;
    }
}
