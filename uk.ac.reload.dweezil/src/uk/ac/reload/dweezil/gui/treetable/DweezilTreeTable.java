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

package uk.ac.reload.dweezil.gui.treetable;

import java.awt.Rectangle;
import java.util.Enumeration;

import javax.swing.SwingUtilities;
import javax.swing.event.TableModelEvent;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

/**
 * An extension of the JTreeTable
 *
 *
 * @author Phillip Beauvoir
 * @version $Id: DweezilTreeTable.java,v 1.3 2007/07/15 20:27:31 phillipus Exp $
 */
public class DweezilTreeTable
extends JTreeTable
{
	
    /**
     * Default Constructor
     */
    public DweezilTreeTable() {
        super();
    }
    
	/**
	 * Constructor
	 * @param treeTableModel The Table Model
	 */
	public DweezilTreeTable(TreeTableModel treeTableModel) {
		super(treeTableModel);
	}
	
	/**
	 * Over-ride this - see comment in TreeTableModelAdapter
	 * @param e
	 */
	public void tableChanged(TableModelEvent e) {
		revalidate();
		super.tableChanged(e);
	}
	
	/**
	 * Select a node on the tree and scroll to it.<p>
	 * This is some voodoo because of the way the TreeTabelModelAdapter calls
	 * fireTableDataChanged() when a node is added/deleted.
	 * So we have to put this on a thread.
	 * 
	 * @param node The node to select
	 */
	public synchronized void selectNode(final DefaultMutableTreeNode node) {
		if(node != null) {
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					TreePath path = new TreePath(node.getPath());
					getTree().setSelectionPath(path);
					Rectangle bounds = getCellRect(getTree().getRowForPath(path), 0, true);
					scrollRectToVisible(bounds);
					getTree().setSelectionPath(path);  // Need to do this twice!!
				}
			});
		}
	}
	
	/**
	 * @return The currently selected node or null
	 */
	public DefaultMutableTreeNode getSelectedNode() {
		TreePath selPath = getTree().getSelectionPath();
		if(selPath != null) {
		    return (DefaultMutableTreeNode)selPath.getLastPathComponent();
		}
		else {
		    return null;
		}
	}
	
	/**
	 * Update (repaint) the node that is currently selected
	 */
	public void updateSelectedNode() {
		if(getTreeTableModel() instanceof DefaultTreeModel) {
		    DefaultMutableTreeNode node = getSelectedNode();
		    if(node != null) {
		        ((DefaultTreeModel)getTreeTableModel()).nodeChanged(node);
		    }
		}
	}
	
	/**
	 * Gets a node on the tree given an Object
	 * @param object The object to search for
	 * @return The DefaultMutableTreeNode if found or null
	 */
	public DefaultMutableTreeNode getNode(Object object) {
		if(object == null) {
		    return null;
		}
		
		if(getTreeTableModel() instanceof DefaultTreeModel) {
		    Enumeration nodes = ((DefaultMutableTreeNode) ((DefaultTreeModel)getTreeTableModel()).getRoot()).preorderEnumeration();
		    // Drill into and find it
		    while(nodes.hasMoreElements()) {
		        DefaultMutableTreeNode node = (DefaultMutableTreeNode)nodes.nextElement();
		        Object o = node.getUserObject();
		        if(object.equals(o)) {
		            return node;
		        }
		    }
		}
		
		return null;
	}
	
}