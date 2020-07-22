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

import javax.swing.ImageIcon;
import javax.swing.JInternalFrame;
import javax.swing.JMenuItem;

import uk.ac.reload.dweezil.menu.MenuAction;
import uk.ac.reload.dweezil.menu.MenuAction_InternalWindow;
import uk.ac.reload.dweezil.util.DweezilUIManager;

/**
 * Our version of a JInternalFrame<br>
 * We can precisely define the behaviour we need for our Internal frames, such
 * as a factory class for a Menu Item associated with this Frame.
 *
 * @author Phillip Beauvoir
 * @version $Id: DweezilInternalFrame.java,v 1.3 2007/07/15 20:27:30 phillipus Exp $
 */
public class DweezilInternalFrame
extends JInternalFrame
{
	/**
	 * The Menu Item that will be added to the Main Menu
	 */
    private JMenuItem _menuItem;
	
	/**
	 * Default Constructor
	 * */
	public DweezilInternalFrame() {
		this(null, null);
	}
	
	public DweezilInternalFrame(String title) {
		this(title, null);
	}

	/**
	 * Constructor
	 * @param title The Frame Title
	 */
	public DweezilInternalFrame(String title, String iconName) {
		super(title, true, true, true, true);
		setFrameIcon(iconName);
	}
	
	/**
	 * Over-ride this so we can set the Menu item as well
	 * @param title
	 */
	public void setTitle(String title) {
		super.setTitle(title);
		MenuAction menuAction = (MenuAction)getMenuItem().getAction();
		menuAction.setText(title);
	}
	
	/**
	 * Set the frame Icon - 16x16 icons
	 * @param iconPath The full path to the Icon
	 */
	public void setFrameIcon(String iconPath) {
		// Set application Icon
		if(iconPath != null) {
			ImageIcon icon = DweezilUIManager.getIcon(iconPath);
			if(icon != null) {
				setFrameIcon(icon);
			} 
		}
	}
	
	/**
	 * Get the MenuItem associated with this Internal Frame
	 * @return the JMenuItem
	 */
	public JMenuItem getMenuItem() {
		// Create it here so that we can get the correct title
		if(_menuItem == null){
			 _menuItem = new JMenuItem(new MenuAction_InternalWindow(this));
		}
		return _menuItem;
	}
}
