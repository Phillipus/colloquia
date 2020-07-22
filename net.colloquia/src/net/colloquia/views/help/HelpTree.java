package net.colloquia.views.help;

import java.awt.Color;
import java.awt.Component;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreePath;

import net.colloquia.ColloquiaConstants;
import net.colloquia.util.Utils;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

import uk.ac.reload.dweezil.gui.tree.DweezilTree;
import uk.ac.reload.dweezil.gui.tree.DweezilTreeModel;
import uk.ac.reload.dweezil.gui.tree.DweezilTreeNode;

class HelpTree
extends DweezilTree
implements TreeSelectionListener
{
    HelpTreeModel treeModel;
    HelpWindow helpWindow;

    String HELP_PATH = "/net/colloquia/resources/help/";

    public HelpTree(HelpWindow helpWindow) {
        this.helpWindow = helpWindow;
        treeModel = new HelpTreeModel();
        setModel(treeModel);

        // A new tree cell renderer
        HelpTreeRenderer cellRenderer = new HelpTreeRenderer();
        setCellRenderer(cellRenderer);

        // Make sure lines are visible
        putClientProperty("JTree.lineStyle", "Angled");

        // Add ourself as a tree selection listener
        addTreeSelectionListener(this);
    }

    /**
    * A selection has been made
    */
    public void valueChanged(TreeSelectionEvent event) {
        if(event == null) return;
        TreePath selPath = event.getPath();
        if(selPath == null) return;

        // Get selected node
        HelpTreeNode selectedNode = (HelpTreeNode)selPath.getLastPathComponent();
        if(selectedNode == null) return;

        // Get selected file
        URL url = selectedNode.getURL();
        helpWindow.setPage(url);
    }

    /**
    * Gets a local resource
    */
	public URL getResourceURL(String fileName) {
         return getClass().getResource(fileName);
    }

    class HelpTreeModel
    extends DweezilTreeModel {

        public HelpTreeModel() {
            super(new HelpTreeNode());
            try {
                URL index = getResourceURL(HELP_PATH + "index.xml");
                SAXBuilder builder = new SAXBuilder();
                Document source = builder.build(index);
                Element docRoot = source.getRootElement();
                String item = docRoot.getAttributeValue("file");
                String name = docRoot.getAttributeValue("name");
                URL url = getResourceURL(HELP_PATH + item);
                HelpTreeNode node = new HelpTreeNode(url, name);
                root = node;
                addNodes(HELP_PATH, node, docRoot);
            }
            catch(Exception ex) {
                System.out.println("HelpTreeModel error: " + ex);
                ex.printStackTrace();
            }
        }

        protected void addNodes(String folder, HelpTreeNode parentNode, Element parentElement) {
            java.util.List list = parentElement.getChildren("item");
            if(list != null) {
                for(int i = 0; i < list.size(); i++) {
                    Element childElement = (Element)list.get(i);
                    String item = childElement.getAttributeValue("file");
                    String name = childElement.getAttributeValue("name");
                    URL url = getResourceURL(folder + item);
                    HelpTreeNode newNode = new HelpTreeNode(url, name);
                    insertNode(newNode, parentNode);
                    if(childElement.getChildren().size() > 0) addNodes(folder + item + "/", newNode, childElement);
                }
            }
        }

        public void insertNode(DweezilTreeNode newNode, DweezilTreeNode parentNode) {
            int childCount = getChildCount(parentNode);
            insertNodeInto(newNode, parentNode, childCount);
        }
    }

    // ============================= RENDERER =======================
    /**
    * Renderer for the tree
    */
    class HelpTreeRenderer
    extends JLabel
    implements TreeCellRenderer
    {
        private ImageIcon icon;
        private ImageIcon iconLeaf = Utils.getIcon(ColloquiaConstants.iconHelpLeaf);
        private ImageIcon iconClosed = Utils.getIcon(ColloquiaConstants.iconHelpClosed);
        private ImageIcon iconOpen = Utils.getIcon(ColloquiaConstants.iconHelpOpen);

        public HelpTreeRenderer() {
            setOpaque(true);
            setFont(ColloquiaConstants.plainFont11);
        }

        public Component getTreeCellRendererComponent(JTree tree, Object value,
                boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {

            HelpTreeNode selNode = (HelpTreeNode)value;
            if(selNode == null) return this;

            setText(selNode.toString());

            if(selNode.isHiLited) setBorder(ColloquiaConstants.focusBorder);
            else setBorder(hasFocus ? ColloquiaConstants.focusBorder : ColloquiaConstants.noFocusBorder);

            if(hasFocus) {
                setForeground(selected ? Color.white : Color.black);
                setBackground(selected ? ColloquiaConstants.color1 : Color.white);
            }
            else {
                setForeground(Color.black);
                setBackground(selected ? Color.lightGray : Color.white);
            }

            if(leaf) icon = iconLeaf;
            else icon = expanded ? iconOpen : iconClosed;

            setIcon(icon);
            return this;
        }
    }
}
