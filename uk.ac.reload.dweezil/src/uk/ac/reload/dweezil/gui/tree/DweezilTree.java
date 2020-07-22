/**
 *  RELOAD TOOLS
 *
 *  Copyright (c) 2003 Oleg Liber, Bill Olivier, Phillip Beauvoir
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in
 *  all copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 *  Project Management Contact:
 *
 *  Oleg Liber
 *  Bolton University
 *  Deane Road
 *  Bolton BL3 5AB
 *  UK
 *
 *  e-mail:   o.liber@bolton.ac.uk
 *
 *
 *  Technical Contact:
 *
 *  Phillip Beauvoir
 *  e-mail:   p.beauvoir@dadabeatnik.com
 *
 *  Web:      http://www.reload.ac.uk
 *
 */

package uk.ac.reload.dweezil.gui.tree;

import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.dnd.Autoscroll;
import java.util.Enumeration;
import java.util.Vector;

import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

/**
 * A Tree based on JTree with convenience methods and Autoscroll support
 *
 * @author Phillip Beauvoir
 * @version $Id: DweezilTree.java,v 1.3 2007/07/15 20:27:31 phillipus Exp $
 */
public class DweezilTree
extends JTree
implements Autoscroll
{
	
	/**
	 * Default Constructor
	 */
	public DweezilTree() {
		super();
	}
	
	// ============================= Node management ===========================
	
	/**
	 * Determine root path
	 * @param path The path to determine if it is the Root Path
	 * @return True if path is the Root Path
	 */
	protected boolean isRootPath(TreePath path) {
		return isRootVisible() && getRowForPath(path) == 0;
	}
	
	/**
	 * Select a node on the Tree given the contained object
	 * @param anObject The Object in the node to select
	 * @return The node selected or null if not found
	 */
	public synchronized DweezilTreeNode selectNodeByObject(Object anObject) {
		if(anObject == null) {
		    return null;
		}
		
		DweezilTreeNode node = getNode(anObject);
		selectNode(node);
		return node;
	}
	
	/**
	 * Select a node on the tree and scroll to it
	 * @param node The node to select
	 */
	public synchronized void selectNode(final DefaultMutableTreeNode node) {
		if(node != null) {
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					TreePath path = new TreePath(node.getPath());
					scrollPathToVisible(path);     // This first
					setSelectionPath(path);        // Then this
				}
			});
		}
	}
	
    /**
     * Select the Root Node - this assumes that the Root Node is displayed
     */
    public void selectRootNode() {
        DweezilTreeModel treeModel = (DweezilTreeModel)getModel();
        if(treeModel != null) {
            DefaultMutableTreeNode node = treeModel.getRootNode();
			selectNode(node);
        }
    }

    /**
     * Select the first displayed Node after the Root Node
     */
    public void selectFirstNode() {
        DweezilTreeModel treeModel = (DweezilTreeModel)getModel();
        if(treeModel != null) {
            DefaultMutableTreeNode rootNode = treeModel.getRootNode();
            if(rootNode != null && rootNode.getChildCount() > 0) {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode)rootNode.getChildAt(0);
				selectNode(node);
            }
        }
    }

    /**
     * Get the currently selected node
	 * @return The currently selected node or null if none selected
	 */
	public DweezilTreeNode getSelectedNode() {
		TreePath selPath = getSelectionPath();
		return selPath == null ? null : (DweezilTreeNode)selPath.getLastPathComponent();
	}
	
    /**
     * Get the currently selected nodes
	 * @return The currently selected nodes or null if none selected
	 */
	public DweezilTreeNode[] getSelectedNodes() {
		TreePath[] selPaths = getSelectionPaths();
		if(selPaths == null) {
		    return null;
		}
		
		Vector v = new Vector();
		
		for(int i = 0; i < selPaths.length; i++) {
		    DweezilTreeNode node = (DweezilTreeNode)selPaths[i].getLastPathComponent();
		    v.add(node);
        }
		
		DweezilTreeNode[] nodes = new DweezilTreeNode[v.size()];
		v.copyInto(nodes);
		
		return nodes;
	}

	/**
	 * Get a node on the Tree given the contained object
	 * @param object The Object in the node to get
	 * @return A node on the tree given its contained object or null if not found.
	 */
	public DweezilTreeNode getNode(Object object) {
		if(object == null) {
		    return null;
		}
		
		Enumeration nodes = getNodes();
		if(nodes != null) {
			// Drill into and find it
			while(nodes.hasMoreElements()) {
			    DweezilTreeNode node = (DweezilTreeNode) nodes.nextElement();
			    Object o = node.getUserObject();
			    if(object.equals(o)) {
			        return node;
			    }
			}
		}

		return null;
	}
	
	/**
	 * @return A pre-order Enumeration of all nodes but not including the root node, or null.
	 */
	public Enumeration getNodes() {
		DweezilTreeModel treeModel = (DweezilTreeModel)getModel();
		if(treeModel == null) {
		    return null;
		}
		
		DefaultMutableTreeNode rootNode = treeModel.getRootNode();
		if(rootNode == null) {
		    return null;
		}
		
		return rootNode.preorderEnumeration();
	}
	
	/**
	 * Expand/Collapse a node on the tree given its contained object. Will only expands the top nodes.
	 * @param anObject The Object in the node to expand
	 * @param expand If true will expand the node, else will collapse it
	 */
	public void expandNode(Object anObject, boolean expand) {
		DweezilTreeNode node = getNode(anObject);
		expandNode(node, expand);
	}
	
	/**
	 * Expand/Collapse a node on the tree. Will only expands the top nodes.
	 * @param node The node to expand
	 * @param expand If true will expand the node, else will collapse it
	 */
	public void expandNode(DefaultMutableTreeNode node, boolean expand) {
		if(node != null) {
			TreePath path = new TreePath(node.getPath());
			// This works! - expandPath() doesn't always work
			int row = getRowForPath(path);
			if(row >= 0) {
				if(expand) {
				    expandRow(row);
				}
				else {
				    collapseRow(row);
				}
			}
			// Failsafe
			else {
				if(expand) {
				    expandPath(path);
				}
				else {
				    collapsePath(path);
				}
			}
		}
	}
	
	/**
	 * Scroll to a node on the tree
	 * @param node The node to scroll to
	 */
	public void scrollNodeToVisible(DefaultMutableTreeNode node) {
		if(node != null) {
			TreePath path = new TreePath(node.getPath());
			scrollPathToVisible(path);
		}
	}
	
	/**
	 * Update (repaint) the node that is currently selected
	 */
	public void updateSelectedNode() {
	    updateNode(getSelectedNode());
	}
	
	/**
	 * Update (repaint) the node
	 */
	public void updateNode(DweezilTreeNode node) {
		DweezilTreeModel treeModel = (DweezilTreeModel)getModel();
		if(treeModel != null) {
		    treeModel.nodeChanged(node);
		}
	}
	
	/**
	 * Update all nodes on the tree
	 */
	public void updateNodes() {
	    Enumeration nodes = getNodes();
	    if(nodes != null) {
	        while(nodes.hasMoreElements()) {
	            DweezilTreeNode node = (DweezilTreeNode)nodes.nextElement();
	            updateNode(node);
	        }
	    }
	}
	
	/**
	 * Expand/Contract the whole Tree.
	 * @param expand If <b>true</b> will expand the tree, if <b>false</b> will contract the tree.
	 */
	public void expandTree(boolean expand) {
		DweezilTreeModel treeModel = (DweezilTreeModel)getModel();
		if(treeModel != null) {
			DefaultMutableTreeNode rootNode = treeModel.getRootNode();
			if(rootNode == null) {
			    return;
			}
			
			DefaultMutableTreeNode theNode;
			Enumeration nodes;
			// If expanding, work forwards, else backwards.
			if(expand) {
			    nodes = rootNode.preorderEnumeration();   // Forwards
			}
			else {
			    nodes = rootNode.postorderEnumeration();  // Backwards
			}
			while(nodes.hasMoreElements()) {
				theNode = (DefaultMutableTreeNode)nodes.nextElement();
				expandNode(theNode, expand);
			}
		}
	}
	
	// ============================= Scroll support for Drag and Drop =======================

	/**
	 * Scroll support for Drag and Drop
	 * @return Insets
	 */
	public Insets getAutoscrollInsets() {
		Rectangle r = getVisibleRect();
		Dimension s = getSize();
		Insets insets = new Insets(r.y + 50,
				r.x,
				s.height - r.y - r.height + 50,
				s.width - r.x - r.width);
		return insets;
	}
	
	/**
	 * Scroll support for Drag and Drop
	 * @param cursorLocn
	 */
	public void autoscroll(Point cursorLocn) {
		//System.out.println("called autoscroll @ " + cursorLocn.x + ", " + cursorLocn.y);
		Rectangle v = getVisibleRect();
		int y = cursorLocn.y < (v.y + 50) ? cursorLocn.y - 40 : cursorLocn.y;
		Rectangle r = new Rectangle(0, y, getVisibleRect().width, 40);
		scrollRectToVisible(r);
	}
	
	// ============================= TREE STATE =======================
	
//	/**
//	 * Write the expanded node tree state to File
//	 * @param file
//	 */
//	public void writeState(File file) {
//		Element docRoot = new Element("tree_state");
//		Document doc = new Document(docRoot);
//		Element element = new Element("expanded_nodes");
//		element.setText(getExpandedNodes());
//		docRoot.addContent(element);
//		try {
//			XMLUtils.write2XMLFile(doc, file);
//			dirtyTree = false;
//		}
//		catch(IOException ex) {
//			System.out.println("writeState Error: " + ex);
//		}
//	}
//	
//	/**
//	 * Load the expanded node tree state to File
//	 * @param file
//	 */
//	public void loadState(File file) {
//		String nodeSet = null;
//		
//		if(file.exists()) {
//			Document source = null;
//			try {
//				source = XMLUtils.readXMLFile(file);
//				Element element = source.getRootElement().getChild("expanded_nodes");
//				if(element != null) nodeSet = element.getText();
//				expandedNodes(nodeSet);
//			}
//			catch(Exception ex) {
//				System.out.println("loadState Error: " + ex);
//			} 
//		}
//		// Reset this since listeners will have set it true
//		dirtyTree = false;
//	}
	
//	/**
//	 * @return All the expanded nodes on the Tree as a String of node numbers
//	 */
//	public String getExpandedNodes() {
//		int count = 0;
//		StringBuffer sb = new StringBuffer();
//		Enumeration nodes = ((DweezilTreeModel)getModel()).getRootNode().preorderEnumeration();
//		while(nodes.hasMoreElements()) {
//			DweezilTreeNode node = (DweezilTreeNode)nodes.nextElement();
//			if(node.isExpanded) {
//				sb.append(":");
//				sb.append(count);
//			}
//			count++;
//		}
//		sb.append(":");
//		return sb.toString();
//	}
//	
//	/**
//	 * Expand node on a tree given an expansion String
//	 * @param nodeSet
//	 */
//	public void expandedNodes(String nodeSet) {
//		if(nodeSet != null) {
//			int count = 0;
//			Enumeration nodes = ((DweezilTreeModel)getModel()).getRootNode().preorderEnumeration();
//			while(nodes.hasMoreElements()) {
//				DweezilTreeNode node = (DweezilTreeNode)nodes.nextElement();
//				if(nodeSet.indexOf(":" + count + ":") != -1) expandNode(node, true);
//				count++;
//			}
//		}
//	}
	
}