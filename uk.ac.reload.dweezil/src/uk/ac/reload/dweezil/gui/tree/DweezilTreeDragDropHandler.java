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

import java.awt.Point;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceContext;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.dnd.DragSourceEvent;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Timer;
import javax.swing.tree.TreePath;

import uk.ac.reload.dweezil.dnd.DNDUtils;
import uk.ac.reload.dweezil.dnd.JTreeDragDropHandler;

/**
 * Delegate for DweezilTree Drag and Drop adding support for highlighting of tree nodes,
 * and automatic node hover expansion.
 *
 * @author Phillip Beauvoir
 * @author Paul Sharples
 * @version $Id: DweezilTreeDragDropHandler.java,v 1.3 2007/07/15 20:27:31 phillipus Exp $
 */
public abstract class DweezilTreeDragDropHandler
extends JTreeDragDropHandler
{
    /**
	 * The previous highlighted node
	 */
	protected DweezilTreeNode _prevHilitedNode;
	
	/**
	 * The last dragged path
	 */
	private TreePath _pathLast;
	
	/**
	 * A Timer for opening hovered over node
	 */
	private Timer _timerHover;
	
	/**
	 * Default Constructor
	 */
	protected DweezilTreeDragDropHandler(DweezilTree tree) {
		super(tree);
		
		final DweezilTree ftree = tree;
		
		// Set up Timer for hover action
		_timerHover = new Timer(1000, new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				if(ftree.isRootPath(_pathLast)) {
				    return;
				}
				
				if(ftree.isCollapsed(_pathLast)) {
				    ftree.expandPath(_pathLast);
				}
				//else {
				//    ftree.collapsePath(_pathLast);
				//}
			}
		});
		
		_timerHover.setRepeats(false);	// Set timer to one-shot mode
	}
	
	/**
	 * Highlight a node on a tree as we drag over it.  It's up to the Tree Renderer
	 * to actually draw the highlight.
	 * @param node  The node we are highlighting
	 * @param hilite True or false
	 */
	protected void hiliteNode(DweezilTreeNode node, boolean hilite) {
		if(node == null) {
		    return;
		}
		
		if(_prevHilitedNode != null && node != _prevHilitedNode) {
			_prevHilitedNode.isHiLited = false;
		}
		
		if(node.isHiLited != hilite) {
			node.isHiLited = hilite;
			getTree().repaint();
			_prevHilitedNode = node;
		}
	}
	
	
	/* (non-Javadoc)
	 * @see java.awt.dnd.DragSourceListener#dragExit(java.awt.dnd.DragSourceEvent)
	 */
	public void dragExit(DragSourceEvent event) {
		DragSourceContext context = event.getDragSourceContext();
		context.setCursor(DragSource.DefaultCopyNoDrop);
		hiliteNode(_prevHilitedNode, false);
	}
	
	/* (non-Javadoc)
	 * @see java.awt.dnd.DragSourceListener#dropActionChanged(java.awt.dnd.DragSourceDragEvent)
	 */
	public void dropActionChanged(DragSourceDragEvent event) {
	    
	}
	
	/* (non-Javadoc)
	 * @see java.awt.dnd.DragSourceListener#dragOver(java.awt.dnd.DragSourceDragEvent)
	 */
	public void dragOver(DragSourceDragEvent event) {

	}
	
	/* (non-Javadoc)
	 * @see java.awt.dnd.DragSourceListener#dragEnter(java.awt.dnd.DragSourceDragEvent)
	 */
	public void dragEnter(DragSourceDragEvent event) {
		DragSourceContext context = event.getDragSourceContext();
		
		int action = event.getDropAction();
		
		if((action & DnDConstants.ACTION_COPY) != 0) {
		    context.setCursor(DragSource.DefaultCopyDrop);
		}
		else if((action & DnDConstants.ACTION_MOVE) != 0) {
		    context.setCursor(DragSource.DefaultMoveDrop);
		}
		else {
		    context.setCursor(DragSource.DefaultCopyNoDrop);
		}
	}
	
	/* (non-Javadoc)
	 * @see java.awt.dnd.DragSourceListener#dragDropEnd(java.awt.dnd.DragSourceDropEvent)
	 */
	public void dragDropEnd(DragSourceDropEvent event) {
		hiliteNode(_prevHilitedNode, false);
	}
	
	/* (non-Javadoc)
	 * @see java.awt.dnd.DropTargetListener#dragEnter(java.awt.dnd.DropTargetDragEvent)
	 */
	public void dragEnter(DropTargetDragEvent event) {
		if(isDropOK(event)) {
		    event.acceptDrag(DNDUtils.getCorrectDropContext(event));
		}
		else {
		    event.rejectDrag();
		}
	}
	
	/**
	 * We are being dragged over - select the Node if OK
	 * Over-ride and call super if need be
	 */
	public void dragOver(DropTargetDragEvent event) {
		boolean ok = isDropOK(event);
		
		if(ok) {
			event.acceptDrag(DNDUtils.getCorrectDropContext(event));
			DweezilTreeNode node = (DweezilTreeNode)getDragOverTreeNode(event);
			hiliteNode(node, true);
		}
		else {
			event.rejectDrag();
			//hiliteNode(prevHilitedNode, false);
		}
		
		// Do the Hover over expand/collapse thing
		Point pt = event.getLocation();
		TreePath path = getTree().getClosestPathForLocation(pt.x, pt.y);
		if(path != _pathLast) {
			_pathLast = path;
			_timerHover.restart();
		}
	}
	
	/* (non-Javadoc)
	 * @see java.awt.dnd.DropTargetListener#dragExit(java.awt.dnd.DropTargetEvent)
	 */
	public void dragExit(DropTargetEvent event) {
		hiliteNode(_prevHilitedNode, false);
	}
	
	/* (non-Javadoc)
	 * @see java.awt.dnd.DropTargetListener#dropActionChanged(java.awt.dnd.DropTargetDragEvent)
	 */
	public void dropActionChanged(DropTargetDragEvent event) {
		if(isDropOK(event)) {
		    event.acceptDrag(DNDUtils.getCorrectDropContext(event));
		}
		else {
		    event.rejectDrag();
		}
	}
	
	/* (non-Javadoc)
	 * @see java.awt.dnd.DropTargetListener#drop(java.awt.dnd.DropTargetDropEvent)
	 */
	public void drop(DropTargetDropEvent event) {
	
	}
	
	/* (non-Javadoc)
	 * @see java.awt.dnd.DragGestureListener#dragGestureRecognized(java.awt.dnd.DragGestureEvent)
	 */
	public void dragGestureRecognized(DragGestureEvent event) {
	    
	}
}