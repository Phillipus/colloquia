/**
 * @(#)JTreeTable.java	1.2 98/10/27
 *
 * Copyright 1997, 1998 by Sun Microsystems, Inc.,
 * 901 San Antonio Road, Palo Alto, California, 94303, U.S.A.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of Sun Microsystems, Inc. ("Confidential Information").  You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with Sun.
 */

package uk.ac.reload.dweezil.gui.treetable;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.util.EventObject;

import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.JViewport;
import javax.swing.LookAndFeel;
import javax.swing.UIManager;
import javax.swing.event.TableModelEvent;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeSelectionModel;
import javax.swing.tree.TreeCellRenderer;

import uk.ac.reload.dweezil.gui.tree.DweezilTree;

/**
 * This example shows how to create a simple JTreeTable component,
 * by using a JTree as a renderer (and editor) for the cells in a
 * particular column in the JTable.
 *
 * Modified by Phillip Beauvoir and Paul Sharples
 *
 * @author Philip Milne
 * @author Scott Violet
 * @author Phillip Beauvoir
 * @author Paul Sharples
 * @version $Id: JTreeTable.java,v 1.1 2005/03/14 17:08:21 phillipus Exp $
 */
public class JTreeTable extends JTable {
	
	/**
	 * A JTree used for the first column rendering
	 */
	private JTree _tree;
	
	/**
	 * Default Constructor
	 */
	public JTreeTable() {
		super();
	}

	/**
	 * Constructor
	 * @param treeTableModel
	 */
	public JTreeTable(TreeTableModel treeTableModel) {
		super();
		setTreeTableModel(treeTableModel);
	}
	
	/**
	 * Set the TreeTable Model
	 * @param treeTableModel
	 */
	public void setTreeTableModel(TreeTableModel treeTableModel) {
		// Set the Tree Model
		getTree().setModel(treeTableModel);
		
		// Install a tableModel representing the visible rows in the tree.
		super.setModel(new TreeTableModelAdapter(treeTableModel, getTree()));
		
		// MacOS X Bug fix Paul Sharples - the JTree and JTable row selections were originally
		// getting out of sync with each other.  This update now works on MacOS X.
		// See http://anoncvs.webfunds.org/cvs.php/scratch/treetable
		getTree().setSelectionModel(new DefaultTreeSelectionModel() {
			// Extend the implementation of the constructor, as if:
			/* public this() */
			{
				setSelectionModel(listSelectionModel);
			}
		});
		
		// Make the tree and table row heights the same.
		getTree().setRowHeight(getRowHeight());
		
		// Install the tree editor renderer and editor.
		setDefaultRenderer(TreeTableModel.class, (TableCellRenderer)getTree());
		setDefaultEditor(TreeTableModel.class, new TreeTableCellEditor());
		
		setShowGrid(false);
		setIntercellSpacing(new Dimension(0, 0));
	}
	
	/**
	 * Get the Treetable Model
	 * @return The TreeTableModel
	 */
	public TreeTableModel getTreeTableModel() {
		return (TreeTableModel)getTree().getModel();
	}
	
	/**
	 * Overridden to message super and forward the method to the tree.
	 * Since the tree is not actually in the component hieachy it will
	 * never receive this unless we forward it in this manner.
	 */
	public void updateUI() {
		super.updateUI();
		
		if(getTree() != null) {
		    getTree().updateUI();
		}
		
		// Use the tree's default foreground and background colors in the table
		LookAndFeel.installColorsAndFont(this, "Tree.background",
				"Tree.foreground", "Tree.font");
	}
	
	/* Workaround for BasicTableUI anomaly. Make sure the UI never tries to
	 * paint the editor. The UI currently uses different techniques to
	 * paint the renderers and editors and overriding setBounds() below
	 * is not the right thing to do for an editor. Returning -1 for the
	 * editing row in this case, ensures the editor is never painted.
	 */
	public int getEditingRow() {
		return(getColumnClass(editingColumn) == TreeTableModel.class) ? -1 : editingRow;
	}
	
	/**
	 * Overridden to pass the new rowHeight to the tree.
	 */
	public void setRowHeight(int rowHeight) {
		super.setRowHeight(rowHeight);
		if(getTree() != null && getTree().getRowHeight() != rowHeight) {
		    getTree().setRowHeight(getRowHeight());
		}
	}
	
	/**
	 * Returns the tree that is being used as the first column renderer.
	 * Implementors can over-ride this to return their own type of JTree which must
	 * implement TableCellRenderer.
	 * The Default is a DweezilTableTree.
	 */
	public JTree getTree() {
	    if(_tree == null) {
	        _tree = new DweezilTableTree();
	    }
	    return _tree;
	}
	
	/**
	 * Added by PB
	 * This stops deselection
	 * See http://forum.java.sun.com/thread.jsp?forum=57&thread=223661
	 * May have to do this:
	 * "After a bit more investigation, overriding the following JTable
	 * method in JTreeTable, resolves the painting issues that my last post refers to."
	 */
	public void tableChanged(TableModelEvent e) {
		revalidate();
		repaint();                 // Added 2004-11-22 because of repaint problems in getScrollableTracksViewportHeight()
		super.tableChanged(e);
	}
	
	// =============== Scrollable functions will match this to viewport ================
	
	/**
	 * Added this to make table height match viewport height so we don't get blank greay area
	 * Adding this requires a repaint() in tableChanged() when a node is deleted
	 */
	public boolean getScrollableTracksViewportHeight() {
		if (getParent() instanceof JViewport) {
		    return (((JViewport)getParent()).getHeight() > getPreferredSize().height);
		}
		return false;
	}
	
	/**
	 * A DweezilTree that displays a JTree as cell renderer for the Table
	 */
	public class DweezilTableTree extends DweezilTree implements TableCellRenderer {
		
		/**
		 * Last table/tree row asked to renderer.
		 */
		protected int visibleRow;
		
		/**
		 * Constructor
		 */
		public DweezilTableTree() {
			super();
		}
		
		/**
		 * updateUI is overridden to set the colors of the Tree's renderer
		 * to match that of the table.
		 */
		public void updateUI() {
			super.updateUI();
			// Make the tree's cell renderer use the table's cell selection colors.
			TreeCellRenderer tcr = getCellRenderer();
			if(tcr instanceof DefaultTreeCellRenderer) {
				DefaultTreeCellRenderer dtcr = ((DefaultTreeCellRenderer) tcr);
				// For 1.1 uncomment this, 1.2 has a bug that will cause an
				// exception to be thrown if the border selection color is
				// null.
				// dtcr.setBorderSelectionColor(null);
				dtcr.setTextSelectionColor(UIManager.getColor("Table.selectionForeground"));
				dtcr.setBackgroundSelectionColor(UIManager.getColor("Table.selectionBackground"));
			}
		}
		
		/**
		 * Sets the row height of the tree, and forwards the row height to
		 * the table.
		 */
		public void setRowHeight(int rowHeight) {
			if(rowHeight > 0) {
				super.setRowHeight(rowHeight);
				if(JTreeTable.this != null && JTreeTable.this.getRowHeight() != rowHeight) {
					JTreeTable.this.setRowHeight(getRowHeight());
				}
			}
		}
		
		/**
		 * This is overridden to set the height to match that of the JTable.
		 */
		public void setBounds(int x, int y, int w, int h) {
			super.setBounds(x, 0, w, JTreeTable.this.getHeight());
		}
		
		/**
		 * Sublcassed to translate the graphics such that the last visible
		 * row will be drawn at 0,0.
		 */
		public void paint(Graphics g) {
			g.translate(0, -visibleRow * getRowHeight());
			super.paint(g);
		}
		
		/**
		 * TreeCellRenderer method. Overridden to update the visible row.
		 */
		public Component getTableCellRendererComponent(JTable table,
				Object value,
				boolean isSelected,
				boolean hasFocus,
				int row, int column) {
			if(isSelected) {
				setBackground(table.getSelectionBackground());
			}
			else {
				setBackground(table.getBackground());
				
			}
			visibleRow = row;
			return this;
		}
	}
	
	/**
	 * TreeTableCellEditor implementation. Component returned is the JTree.
	 */
	public class TreeTableCellEditor extends AbstractCellEditor implements TableCellEditor {
		
	    /* (non-Javadoc)
		 * @see javax.swing.table.TableCellEditor#getTableCellEditorComponent(javax.swing.JTable, java.lang.Object, boolean, int, int)
		 */
		public Component getTableCellEditorComponent(JTable table,
				Object value,
				boolean isSelected,
				int r, int c) {
			return getTree();
		}
		
		/**
		 * Overridden to return false, and if the event is a mouse event
		 * it is forwarded to the tree.<p>
		 * The behavior for this is debatable, and should really be offered
		 * as a property. By returning false, all keyboard actions are
		 * implemented in terms of the table. By returning true, the
		 * tree would get a chance to do something with the keyboard
		 * events. For the most part this is ok. But for certain keys,
		 * such as left/right, the tree will expand/collapse where as
		 * the table focus should really move to a different column. Page
		 * up/down should also be implemented in terms of the table.
		 * By returning false this also has the added benefit that clicking
		 * outside of the bounds of the tree node, but still in the tree
		 * column will select the row, whereas if this returned true
		 * that wouldn't be the case.
		 * <p>By returning false we are also enforcing the policy that
		 * the tree will never be editable (at least by a key sequence).
		 */
		public boolean isCellEditable(EventObject e) {
			if(e instanceof MouseEvent) {
				for(int counter = getColumnCount() - 1; counter >= 0;
				counter--) {
					if(getColumnClass(counter) == TreeTableModel.class) {                    	                    	                    	
						// Bug fix for MacOs X. 09-03-2004
						// The triangular arrows on JTreeTables in the MacOS L&F did not
						// expand or colapse the branches.  This is an update to address that.
						// See http://lists.apple.com/archives/java-dev/2002/Feb/15/jtreetablev12workaroundf.txt
						MouseEvent me = (MouseEvent)e;
						MouseEvent newME = new MouseEvent(getTree(), me.getID(),
								me.getWhen(), me.getModifiers(),
								me.getX() - getCellRect(0, counter, true).x,
								me.getY(), me.getClickCount(),
								me.isPopupTrigger());
						getTree().dispatchEvent(newME);
						newME = new MouseEvent(getTree(), MouseEvent.MOUSE_RELEASED,
								me.getWhen(), me.getModifiers(),
								me.getX() - getCellRect(0, counter, true).x,
								me.getY(), me.getClickCount(),
								me.isPopupTrigger());
						getTree().dispatchEvent(newME);
						
						break;
					}
				}
			}
			return false;
		}
	}
}
