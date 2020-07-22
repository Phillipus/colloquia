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

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JPopupMenu;
import javax.swing.JTree;
import javax.swing.tree.TreePath;


/**
 * Delegate for JTree JPopupMenu handling
 *
 * @author Phillip Beauvoir
 * @version $Id: DweezilTreePopupHandler.java,v 1.3 2007/07/15 20:27:31 phillipus Exp $
 */
public class DweezilTreePopupHandler
{
    /**
     * The Tree
     */
    private JTree _tree;
    
    /**
     * The PopupMenu
     */
    private JPopupMenu _popupMenu;
    
	/**
	 * Constructor
	 */
	public DweezilTreePopupHandler(JTree tree) {
	    _tree = tree;
	    
		// Listen to mouse-clicks for the Popup Menu
	    tree.addMouseListener(new MouseAdapter() {
			// PC
			public void mouseReleased(MouseEvent e) {
				checkPopupTrigger(e);
			}
			
			// Mac
			public void mousePressed(MouseEvent e) {
				checkPopupTrigger(e);
			}
		});
	}
	
	/**
	 * Check to see if we have triggered the popup menu.
	 * @param e The MouseEvent that has been triggered.
	 */
	protected void checkPopupTrigger(MouseEvent e) {
		if(e.isPopupTrigger()) {
			TreePath selPath = getTree().getPathForLocation(e.getX(), e.getY());
			if(selPath == null) {
			    return;
			}
			// Select the node
			getTree().setSelectionPath(selPath);
			// Show the popup menu
			_popupMenu.show(getTree(), e.getX(), e.getY());
		}
	}
	
	/**
	 * Set the PopupMenu
	 * @param popupMenu
	 */
	public void setPopupMenu(JPopupMenu popupMenu) {
	    _popupMenu = popupMenu;
	}
	
    /**
     * @return The PopupMenu
     */
    public JPopupMenu getPopupMenu() {
        if(_popupMenu  == null) {
            _popupMenu = new JPopupMenu();
        }
        
        return _popupMenu;
    }

    /**
     * @return Returns the tree.
     */
    public JTree getTree() {
        return _tree;
    }
}