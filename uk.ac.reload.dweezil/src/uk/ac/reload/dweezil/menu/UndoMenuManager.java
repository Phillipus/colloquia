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

package uk.ac.reload.dweezil.menu;

import uk.ac.reload.diva.undo.UndoHandler;
import uk.ac.reload.diva.undo.UndoListener;
import uk.ac.reload.diva.undo.UndoableAction;
import uk.ac.reload.dweezil.Messages;

/**
 * An Undo Manager for handling the Undo/Redo Menu items.
 *
 * @author Phillip Beauvoir
 * @version $Id: UndoMenuManager.java,v 1.3 2007/07/15 20:27:31 phillipus Exp $
 */
public class UndoMenuManager implements UndoListener, MenuActionListener {
	
	/**
	 * The Undo Handler
	 */
	private UndoHandler undoHandler;
	
	/**
	 * Flag to set whether we have the focus
	 */
	private boolean hasFocus = false;
	
	/**
	 * The Undo Menu Action
	 */
	private MenuAction _actionUndo;
	
	/**
	 * The Redo Menu Action
	 */
	private MenuAction _actionRedo;
	
	/**
	 * Constructor
	 */
	public UndoMenuManager(MenuAction actionUndo, MenuAction actionRedo) {
		undoHandler = new UndoHandler(this);
		_actionUndo = actionUndo;
		_actionRedo = actionRedo;
	}
	
	/**
	 * The Undo or Redo Menu Item was performed
	 */
	public void menuActionPerformed(DweezilMenuEvent event) {
		if(hasFocus && event.getSource() == _actionUndo) {
			undoLastAction();
		}
		else if(hasFocus && event.getSource() == _actionRedo) {
			redoLastAction();
		}
	}
	
	/**
	 * An Undoable Action occurred
	 */
	public void undoableActionHappened(UndoableAction action) {
		if(hasFocus) setMenus();
	}
	
	/**
	 * Set the menu items according to the Undo/Redo stacks
	 */
	private void setMenus() {
		UndoableAction undoAction = undoHandler.nextUndoAction();
		if(undoAction == null) {
			_actionUndo.setText(Messages.getString("uk.ac.reload.dweezil.UndoMenuManager.0")); //$NON-NLS-1$
			_actionUndo.setEnabled(false);
		}
		else {
			_actionUndo.setText(Messages.getString("uk.ac.reload.dweezil.UndoMenuManager.0") + " " + undoAction.getName()); //$NON-NLS-1$ //$NON-NLS-2$
			_actionUndo.setEnabled(true);
		}
		
		UndoableAction redoAction = undoHandler.nextRedoAction();
		if(redoAction == null) {
			_actionRedo.setText(Messages.getString("uk.ac.reload.dweezil.UndoMenuManager.1")); //$NON-NLS-1$
			_actionRedo.setEnabled(false);
		}
		else {
			_actionRedo.setText(Messages.getString("uk.ac.reload.dweezil.UndoMenuManager.1") + " " + redoAction.getName()); //$NON-NLS-1$ //$NON-NLS-2$
			_actionRedo.setEnabled(true);
		}
	}
	
	/**
	 * Undo the last Action in the Stack
	 */
	private void undoLastAction() {
		undoHandler.undoLastAction();
		if(hasFocus) setMenus();
	}
	
	/**
	 * Redo the last Action in the Stack
	 */
	private void redoLastAction() {
		undoHandler.redoLastAction();
		if(hasFocus) setMenus();
	}
	
	/**
	 * The Focus has been gained
	 */
	public void setFocusGained() {
		//System.out.println("Focus Gained");
		hasFocus = true;
		_actionUndo.addMenuActionListener(this);
		_actionRedo.addMenuActionListener(this);
		setMenus();
	}
	
	/**
	 * The Focus has been lost
	 */
	public void setFocusLost() {
		//System.out.println("Focus Lost");
		hasFocus = false;
		_actionUndo.removeMenuActionListener(this);
		_actionRedo.removeMenuActionListener(this);
		
		_actionUndo.setText(Messages.getString("uk.ac.reload.dweezil.UndoMenuManager.0")); //$NON-NLS-1$
		_actionUndo.setEnabled(false);
		_actionRedo.setText(Messages.getString("uk.ac.reload.dweezil.UndoMenuManager.1")); //$NON-NLS-1$
		_actionRedo.setEnabled(false);
	}
	
	/**
	 * Clean up
	 */
	public void cleanup() {
		_actionUndo.removeMenuActionListener(this);
		_actionRedo.removeMenuActionListener(this);
	}
	
	/**
	 * @return the Undo Handler
	 */
	public UndoHandler getUndoHandler() {
		return undoHandler;
	}
}
