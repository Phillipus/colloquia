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

import java.awt.Dimension;
import java.awt.event.ActionEvent;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;


/**
 * An extension of MenuAction that provides a drop-down PopupMenu.<br>
 * This can be used when the MenuAction is added to a Toolbar and a dropdown menu invoked
 * when the MenuAction's button is pressed.<br>
 * <br>
 * For example:  <br>
 * 	class MenuAction_Add extends PopupMenuAction {<br>
 *      public MenuAction_Add() {    <br>
 *          super("Add", ICON_ADD);    <br>
 *          getPopupMenu().add("Item 1");    <br>
 *          getPopupMenu().add("Item 2");    <br>
 *          getPopupMenu().addSeparator();   <br>
 *          getPopupMenu().add("Item 3");    <br>
 *      }    <br>
 *  }	 <br>
 *    <br>
 * 
 * @author Phillip Beauvoir
 * @version $Id: PopupMenuAction.java,v 1.3 2007/07/15 20:27:31 phillipus Exp $
 */
public class PopupMenuAction
extends MenuAction
{
    
    /**
     * The JPopupMenu
     */
    private JPopupMenu _popupMenu;
    
    
    /**
     * Default Constructor
     */
    public PopupMenuAction() {
        super();
    }
    
	/**
	 * Constructor for new MenuAction with no icon
	 * @param text The text to display
	 */
    public PopupMenuAction(String text) {
        super(text);
    }

    /**
     * @param text
     * @param iconPath
     */
    public PopupMenuAction(String text, String iconPath) {
        super(text, iconPath);
    }
    
	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent e) {
	    super.actionPerformed(e);
	    getPopupMenu().show(getButton(), 0, getButton().getHeight());
	}

	/**
     * @return Returns the JPopupMenu.
     */
    public JPopupMenu getPopupMenu() {
        if(_popupMenu == null) {
            _popupMenu = new JPopupMenu();

            // Workaround to stop first menu item being selected
			JMenuItem dummyItem = _popupMenu.add(new JMenuItem());
			dummyItem.setPreferredSize(new Dimension(0, 0));
        }
        return _popupMenu;
    }
    
    /**
     * @param menu The JPopupMenu to set.
     */
    public void setPopupMenu(JPopupMenu menu) {
        _popupMenu = menu;
    }
}
