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

import javax.swing.Action;

/**
 * This Proxy Action is performed when a Main Menu Action (like Delete, or Save) is performed.
 * It wraps this MenuAction.
 *
 * @author Phillip Beauvoir
 * @version $Id: ProxyAction.java,v 1.3 2007/07/15 20:27:31 phillipus Exp $
 */
public abstract class ProxyAction
implements MenuActionListener
{
	/**
	 * The Menu Action we are listening for
	 */
    private MenuAction _proxyMenuAction;
	
	/**
	 * Constructor
	 * @param proxyMenuAction
	 */
	protected ProxyAction(MenuAction proxyMenuAction) {
		_proxyMenuAction = proxyMenuAction;
		proxyMenuAction.addMenuActionListener(this);
	}
	
	/**
	 * Call this to remove this ProxyAction as a listener from the main MenuAction
	 */
	public void removeListener() {
		_proxyMenuAction.removeMenuActionListener(this);
	}
	
	/**
	 * Add Listener to Menu Action
	 */
	public void addListener() {
		_proxyMenuAction.addMenuActionListener(this);
	}
	
	/**
	 * Set the Menu Item enabled or disabled
	 * @param enabled
	 */
	public void setEnabled(boolean enabled) {
		_proxyMenuAction.setEnabled(enabled);
	}
	
	/**
	 * Remove ourselves as listener and disable
	 */
	public void clear() {
		removeListener();
		setEnabled(false);
	}
	
	/**
	 * Set the Menu Item Text
	 * @param text
	 */
	public void setText(String text) {
		_proxyMenuAction.setText(text);
	}
	
	/**
	 * @return The Text
	 */
	public String getText() {
		return (String)_proxyMenuAction.getValue(Action.NAME);
	}
	
	/**
	 * Whether it is enabled
	 * @return
	 */
	public boolean isEnabled() {
		return _proxyMenuAction.isEnabled();
	}
	
	/**
	 * Get the MenuAction
	 * @return
	 */
	public MenuAction getMenuAction() {
		return _proxyMenuAction;
	}
}