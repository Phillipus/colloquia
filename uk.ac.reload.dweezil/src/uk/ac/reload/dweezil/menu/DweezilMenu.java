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

import java.awt.Component;
import java.util.Vector;

import javax.swing.Action;
import javax.swing.JMenu;

/**
 * A JMenu which allows us to add additional menu items
 *
 * @author Phillip Beauvoir
 * @version $Id: DweezilMenu.java,v 1.3 2007/07/15 20:27:31 phillipus Exp $
 */
public class DweezilMenu
extends JMenu
{
	
	/**
	 * Additional Items as a Vector of Component class objects
	 */
    private Vector _additionalItems;
	
    
    /**
     * Default Constructor
     */
    public DweezilMenu() {
        super();
    }
    
	/**
	 * Constructor
	 * @param s the text for the menu label
	 */
	public DweezilMenu(String s) {
		super(s);
	}
	
	/**
	 * Add an additional component to this menu
	 * @param c The component to add
	 */
	public void addAdditionalItem(Component c) {
		add(c);
		getAdditionalItems().add(c);
	}
	
	/**
	 * Add an additional Action to this menu
	 * @param action
	 */
	public void addAdditionalItem(Action action) {
		Component c = add(action);
		getAdditionalItems().add(c);
	}
	
	/**
	 * @return Additional Items as a Vector of Component class objects
	 */
	public Vector getAdditionalItems() {
		if(_additionalItems == null) {
			_additionalItems = new Vector();
		}
		return _additionalItems;
	}
	
	/**
	 * Remove all additional menu items
	 */
	public void removeAdditionalItems() {
		// Remove any menu items we created
		if(_additionalItems != null) {
			for(int i = 0; i < _additionalItems.size(); i++) {
				Component c = (Component) _additionalItems.elementAt(i);
				remove(c);
			}
			_additionalItems.clear();
		}
	}
	
}