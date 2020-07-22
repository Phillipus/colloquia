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

package uk.ac.reload.dweezil.gui;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JToolBar;

import uk.ac.reload.dweezil.menu.MenuAction;


/**
 * Our version of a JToolBar
 *
 * @author Phillip Beauvoir
 * @version $Id: DweezilToolBar.java,v 1.3 2007/07/15 20:27:30 phillipus Exp $
 */
public class DweezilToolBar
extends JToolBar
{
	
	/**
	 * Default Constructor with light grey background.
	 */
	public DweezilToolBar() {
		init();
	}
	
	/**
	 * Constructor where background colour cam be specified.
	 * @param color The background colour of the toolbar
	 */
	public DweezilToolBar(Color color) {
		init();
		setBackground(color);
	}
	
	/**
	 * Do some initialising.
	 */
	private void init() {
		setFloatable(false);
		// Keep this in to ensure rollovers work
		setRollover(true);
	}
	
	/**
	 * Add a MenuAction to the Toolbar.  This will add the MenuAction's button.
	 * @param a The MenuAction to add
	 */
	public void add(MenuAction a) {
		super.add(a.getButton());
	}
	
	/**
	 * Add a MenuAction to the Toolbar at position index.  This will add the MenuAction's button.
	 * @param a The MenuAction to add
	 * @param index The posistion where to add it
	 */
	public void add(MenuAction a, int index) {
		super.add(a.getButton(), index);
	}
	
	/**
	 * Add a MenuAction to the Toolbar.  This will add the MenuAction's button.
	 * @param a The MenuAction to add
	 * @param enabled Whether the button is enabled (grey) or not
	 */
	public void add(MenuAction a, boolean enabled) {
		add(a);
		a.setEnabled(enabled);
	}
	
	/**
	 * Remove a MenuAction from the Toolbar.  This will remove the MenuAction's button.
	 * @param a The MenuAction to remove
	 */
	public void remove(MenuAction a) {
		super.remove(a.getButton());
	}
	
	/**
	 * This ensures that we can be made small
	 * @return
	 */
	public Dimension getMinimumSize() {
		return new Dimension(0, 0);
	}
}
