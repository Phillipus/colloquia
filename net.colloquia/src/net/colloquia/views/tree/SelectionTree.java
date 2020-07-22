package net.colloquia.views.tree;

import java.awt.*;
import java.util.*;

import javax.swing.*;
import javax.swing.tree.*;

import net.colloquia.*;
import net.colloquia.datamodel.entities.*;
import net.colloquia.util.*;


/**
* A Tree for the selection of Components
*/
public class SelectionTree
extends JTree
{
    private ColloquiaContainer parentGroup;
    private SelectionTreeModel treeModel;

    public SelectionTree(ColloquiaContainer parentGroup) {
        this.parentGroup = parentGroup;
        treeModel = new SelectionTreeModel(parentGroup);
        setModel(treeModel);
        // A new tree cell renderer
        SelectionTreeRenderer cellRenderer = new SelectionTreeRenderer();
        setCellRenderer(cellRenderer);
        // Make sure lines are visible
        putClientProperty("JTree.lineStyle", "Angled");
        setBackground(Color.white);
    }

    public ColloquiaComponent getSelectedComponent() {
        TreePath path = getSelectionPath();
        if(path != null) {
            ColloquiaTreeNode node = (ColloquiaTreeNode)path.getLastPathComponent();
            if(node != null) return node.getComponent();
        }
        return null;
    }

    public Vector getSelectedComponents() {
        Vector v = new Vector();
        TreePath[] paths = getSelectionPaths();
        if(paths != null) {
            for(int i = 0; i < paths.length; i++) {
                ColloquiaTreeNode node = (ColloquiaTreeNode)paths[i].getLastPathComponent();
                v.addElement(node.getComponent());
            }
        }
        return v;
    }


    /**
    * The Tree Model
    */
    private class SelectionTreeModel
    extends DefaultTreeModel
    {
        public SelectionTreeModel(ColloquiaContainer parentGroup) {
            super(new ColloquiaTreeNode(parentGroup));
            addComponents((ColloquiaTreeNode)root);
        }

        private void addComponents(ColloquiaTreeNode parentNode) {
            ColloquiaContainer group = (ColloquiaContainer)parentNode.getComponent();
            Vector members = group.getMembers();
            for(int i = 0; i < members.size(); i++) {
                ColloquiaComponent tc = (ColloquiaComponent)members.elementAt(i);
                ColloquiaTreeNode newNode = new ColloquiaTreeNode(tc);
                parentNode.add(newNode);
                if(tc instanceof ColloquiaContainer) addComponents(newNode);
            }
        }
    }


    /**
    * Renderer for the tree
    */
    private class SelectionTreeRenderer
    extends JLabel
    implements TreeCellRenderer
    {
        private ImageIcon icon;
        private ImageIcon iconPerson = Utils.getIcon(ColloquiaConstants.iconPerson);
        private ImageIcon iconResource = Utils.getIcon(ColloquiaConstants.iconResource);
        private ImageIcon iconFolder = Utils.getIcon(ColloquiaConstants.iconFolder);
        private ImageIcon iconOpenFolder = Utils.getIcon(ColloquiaConstants.iconOpenFolder);

        public SelectionTreeRenderer() {
            setOpaque(true);
            setFont(ColloquiaConstants.plainFont11);
        }

        public Component getTreeCellRendererComponent(JTree tree, Object value,
                boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {

            ColloquiaTreeNode selNode = (ColloquiaTreeNode)value;
            if(selNode == null) return this;
            ColloquiaComponent selComponent = selNode.getComponent();

            setText(selComponent.getName());

            setForeground(selected ? Color.white : Color.black);
            setBackground(selected ? ColloquiaConstants.color1 : Color.white);

            // FOR EACH COMPONENT TYPE
            switch(selComponent.getType()) {
                // PERSON
                case ColloquiaComponent.PERSON:
                    icon = iconPerson;
                    break;

                // RESOURCE
                case ColloquiaComponent.RESOURCE:
                    icon = iconResource;
                    break;

                default:
                    icon = expanded ? iconOpenFolder : iconFolder;
            }

            setIcon(icon);
            return this;
        }
    }

}


