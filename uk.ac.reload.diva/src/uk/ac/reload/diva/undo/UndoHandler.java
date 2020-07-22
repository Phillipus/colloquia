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

package uk.ac.reload.diva.undo;

import java.util.Stack;

/**
 * An Undo Handler
 *
 * @author Phillip Beauvoir
 * @version $Id: UndoHandler.java,v 1.3 2007/07/15 20:27:52 phillipus Exp $
 */
public class UndoHandler {
	/**
	 * The Undo Stack
	 */
	private Stack _undoStack;
	
	/**
	 * The Redo Stack
	 */
	private Stack _redoStack;
	
	/**
	 * The Listener parent
	 */
	private UndoListener _undoListener;
	
	/**
	 * Constructor
	 */
	public UndoHandler() {
		this(null);
	}
	
	public UndoHandler(UndoListener undoListener) {
		_undoListener = undoListener;
		_undoStack = new Stack();
		_redoStack = new Stack();
	}
	
	/**
	 * An Undoable Action occurred so add it to the Stack
	 * @param action
	 */
	public void addUndoableAction(UndoableAction action) {
		// Set Undo
		_undoStack.push(action);
		
		// Clear Redo
		_redoStack.removeAllElements();
		
		// Tell Listener
		if(_undoListener != null) _undoListener.undoableActionHappened(action);
	}
	
	/**
	 * Undo the last Action in the Stack
	 * @return The Action that took place or null if there isn't one
	 */
	public UndoableAction undoLastAction() {
		UndoableAction action = null;
		
		if(!_undoStack.empty()) {
			// Get the Action and Undo it
			action = (UndoableAction)_undoStack.pop();
			action.undo();
			
			// Put it on the Redo stack
			_redoStack.push(action);
		}
		
		return action;
	}
	
	/**
	 * Redo the last Action in the Stack
	 * @return The Action that took place or null if there isn't one
	 */
	public UndoableAction redoLastAction() {
		UndoableAction action = null;
		
		if(!_redoStack.empty()) {
			// Get the Action and Redo it
			action = (UndoableAction)_redoStack.pop();
			action.redo();
			
			// Put it on the Undo stack
			_undoStack.push(action);
		}
		
		return action;
	}
	
	/**
	 * Clear Stacks and menus
	 */
	public void clearAll() {
		_undoStack.clear();
		_redoStack.clear();
	}
	
	/**
	 * @return the Next Undoable Undo Action or null
	 */
	public UndoableAction nextUndoAction() {
		UndoableAction nextAction = null;
		if(!_undoStack.empty()) {
			nextAction = (UndoableAction) _undoStack.peek();
		}
		return nextAction;
	}
	
	/**
	 * @return the Next Redoable Undo Action or null
	 */
	public UndoableAction nextRedoAction() {
		UndoableAction nextAction = null;
		if(!_redoStack.empty()) {
			nextAction = (UndoableAction) _redoStack.peek();
		}
		return nextAction;
	}
}
