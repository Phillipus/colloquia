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

package uk.ac.reload.dweezil.dnd;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.dnd.*;
import java.awt.image.BufferedImage;

import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

/**
 * Delegate for JTree Drag and Drop providing support for drag image.
 *
 * @author Phillip Beauvoir
 * @author Paul Sharples
 * @version $Id: JTreeDragDropHandler.java,v 1.3 2007/07/15 20:27:30 phillipus Exp $
 */
public abstract class JTreeDragDropHandler
implements DragSourceListener, DragGestureListener, DropTargetListener
{
	/**
	 * The JTree to operate on
	 */
	private JTree _tree;
    
	/**
	 * Default Constructor
	 */
	protected JTreeDragDropHandler(JTree tree) {
		_tree = tree;
		
		// The Tree is a Drag and Drop Source and Target
		new DropTarget(_tree, this);
		DragSource dragSource = DragSource.getDefaultDragSource();
		dragSource.createDefaultDragGestureRecognizer(_tree, DnDConstants.ACTION_COPY_OR_MOVE, this);
	}
	
	/**
	 * @return The JTree that we are operating on.
	 */
	public JTree getTree() {
	    return _tree;
	}
	
	/**
	 * @return the tree node that has been picked up and dragged
	 */
	protected TreeNode getDragSourceTreeNode(DragGestureEvent event) {
		TreeNode node = null;
		Point location = event.getDragOrigin();
		TreePath dragPath = _tree.getPathForLocation(location.x, location.y);
		if(dragPath != null && _tree.isPathSelected(dragPath)) {
			node = (TreeNode)dragPath.getLastPathComponent();
		}
		return node;
	}
	
	/**
	 * @return the tree node that is being dragged over
	 */
	protected TreeNode getDragOverTreeNode(DropTargetDragEvent event) {
	    TreeNode node = null;
		Point location = event.getLocation();
		TreePath dragPath = _tree.getPathForLocation(location.x, location.y);
		if(dragPath != null) {
			node = (TreeNode)dragPath.getLastPathComponent();
		}
		return node;
	}
	
	/**
	 * @return a Drag Image
	 */
	public BufferedImage getDragImage(TreePath path, Point ptDragOrigin) {
		BufferedImage imgGhost;
		Point ptOffset = new Point();
		
		// Work out the offset of the drag point from the TreePath bounding rectangle origin
		Rectangle raPath = _tree.getPathBounds(path);
		ptOffset.setLocation(ptDragOrigin.x-raPath.x, ptDragOrigin.y-raPath.y);
		
		// Get the cell renderer (which is a JLabel) for the path being dragged
		JLabel lbl = (JLabel) _tree.getCellRenderer().getTreeCellRendererComponent
		(
		        _tree,                                           		// tree
				path.getLastPathComponent(),                    		// value
				false,                                          		// isSelected (dont want a colored background)
				_tree.isExpanded(path),                         		// isExpanded
				_tree.getModel().isLeaf(path.getLastPathComponent()), 	// isLeaf
				0,                                              		// row (not important for rendering)
				false                                         			// hasFocus (dont want a focus rectangle)
		);
		lbl.setSize((int)raPath.getWidth(), (int)raPath.getHeight());
		
		// Get a buffered image of the selection for dragging a ghost image
		imgGhost = new BufferedImage((int)raPath.getWidth(), (int)raPath.getHeight(), BufferedImage.TYPE_INT_ARGB_PRE);
		Graphics2D g2 = imgGhost.createGraphics();
		
		// Ask the cell renderer to paint itself into the BufferedImage
		g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC, 0.5f));		// Make the image ghostlike
		lbl.paint(g2);
		
		return imgGhost;
	}
	
	/**
	 * Give feedback on whether it's OK to drop here
	 * Over-ride this with something suitable
	 */
	public abstract boolean isDropOK(DropTargetDragEvent event);
	
}