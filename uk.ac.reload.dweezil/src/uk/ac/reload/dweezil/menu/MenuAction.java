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

import java.awt.event.ActionEvent;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JButton;

import uk.ac.reload.dweezil.util.DweezilUIManager;

/**
 * The Main Menu Action for each Menu Item in the Application.
 * It extends AbstractAction and can be added to a Menu Bar, Toolbar or Menu Popup.
 * It is an abstract parent class for each menu item and button in the Application.
 *
 * It also has a JButton created for it.
 *
 * @author Phillip Beauvoir
 * @version $Id: MenuAction.java,v 1.3 2007/07/15 20:27:31 phillipus Exp $
 */
public class MenuAction
extends AbstractAction
{
	/**
	 * The main Icon
	 */
	private Icon _icon;
	
	/**
	 * The text for the menu action
	 */
	private String _menuText;
	
	/**
	 * The mnemonic character key code
	 */
	private char _mnemonic;
	
	/**
	 * The Button for this action.  This can be null for no button.
	 */
	private JButton _button;
	
	/**
	 * Default Constructor
	 */
	public MenuAction() {
		this("", null);
	}
	
	/**
	 * Constructor for new MenuAction with no icon
	 * @param text The text to display
	 */
	public MenuAction(String text) {
		this(text, null);
	}
	
	/**
	 * Constructor for new MenuAction
	 * @param text The text to display
	 * @param iconPath The full path of the icon.  If this is null no icon will be displayed
	 */
	public MenuAction(String text, String iconPath) {
		// Set text and mnemonic
	    parseText(text);
		
		// If we have an icon path, make an icon
		if(iconPath != null) {
			Icon icon = DweezilUIManager.getIcon(iconPath);
			if(icon != null) {
			    setMenuIcon(icon);
			}
		}
		
		// Create it now to be on the safe side
		getButton();
	}
	
    /**
     * @return Returns the mnemonic.
     */
    public char getMnemonic() {
        return _mnemonic;
    }
    
    /**
     * @param mnemonic The mnemonic to set.
     */
    public void setMnemonic(char mnemonic) {
        _mnemonic = mnemonic;
    }

    /**
	 * Set the Icon on the Menu
	 * @param icon The Icon to set
	 */
	public void setMenuIcon(Icon icon) {
	    _icon = icon;
		putValue(Action.SMALL_ICON, icon);
	}
	
	/**
	 * @return The Icon on the Menu or null
	 */
	public Icon getMenuIcon() {
	    return _icon;
	}
	
	/**
	 * Set the Icon for the Button
	 * @param icon
	 */
	public void setButtonIcon(Icon icon) {
	    getButton().setIcon(icon);
	}
	
	/**
	 * Sets the text for the button
	 * @param text The Text to set
	 */
	public void setButtonText(String text) {
	    getButton().setText(text);
	}
	
	/**
	 * Sets the text for the menu item
	 * @param text The Text to set
	 */
	public void setText(String text) {
	    parseText(text);
	    putValue(Action.NAME, getText());
	    getButton().setToolTipText(getText());
	}
	
	/**
	 * @return The Text for this Action
	 */
	public String getText() {
	    return _menuText;
	}
	
	/**
	 * Set the menu item / button enabled or disabled (greyed out)
	 * @param newValue enabled or disabled
	 */
	public void setEnabled(boolean newValue) {
	    getButton().setEnabled(newValue);
		super.setEnabled(newValue);
	}
	
	/**
	 * Get the JButton for this Menu Item
	 * @return The Button
	 */
	public JButton getButton() {
		if(_button == null) {
		    _button = new JButton();
			
			if(_icon != null) {
			    _button.setIcon(_icon);
			}
			
			_button.setEnabled(isEnabled());
			_button.addActionListener(this);
			_button.setToolTipText(getText());
			//_button.setContentAreaFilled(false);
			_button.setFocusPainted(false);
			
			// Set it thus so our windows don't lose the focus and therefore their menus
			_button.setFocusable(false);
		}
		
	    return _button;
	}
	
	/**
	 * Parse the text for mnemonic character and set text and mnemonic acccordingly
	 * @param menuText
	 */
	protected void parseText(String menuText) {
	    // Set mnemonic
	    char mnemonic = getMnemonic(menuText);
	    if(mnemonic != 0) {
	        setMnemonic(mnemonic);
	    }
	    
	    // Set text minus mnemonic
	    _menuText = getRemoveMnemonicText(menuText);
		// Menu text
		putValue(Action.NAME, _menuText);
	}
	
	/**
	 * Take the menu text and look for the '&' character and take the next following character
	 * as the menu mnemonic.
	 * @param text The Text to parse
	 * @return the mnemonic character if there is one, or 0 if not found
	 */
	public static char getMnemonic(String menuText) {
	    if(menuText == null) {
	        return 0;
	    }
	    
	    int index = menuText.indexOf("&");
	    
	    // No '&' character
	    if(index == -1) {
	        return 0;
	    }
	    
	    try {
	        return menuText.charAt(index + 1);
	    }
	    catch(IndexOutOfBoundsException ex) {
	        return 0;
	    }
	}
	
	/**
	 * Take the menu text and remove the '&' character.
	 * @param text The Text to parse
	 * @return the menuText with the & character removed
	 */
	public static String getRemoveMnemonicText(String menuText) {
	    if(menuText == null) {
	        return null;
	    }
	    
	    int index = menuText.indexOf("&");
	    
	    // No '&' character
	    if(index == -1) {
	        return menuText;
	    }
	    
	    try {
	        return menuText.substring(0, index) + menuText.substring(index + 1);
	    }
	    catch(IndexOutOfBoundsException ex) {
	        return menuText;
	    }
	}
	
	//============================== LISTENER EVENTS  ==========================
	
	/**
	 * List of Listeners
	 */
	private Vector listeners;
	
	/**
	 * Add a MenuActionListener Listener
	 * @param listener The MenuActionListener Listener
	 */
	public synchronized void addMenuActionListener(MenuActionListener listener) {
		if(listeners == null) {
		    listeners = new Vector();
		}
	    
	    if(!listeners.contains(listener)) {
		    listeners.addElement(listener);
		}
	}
	
	/**
	 * Remove a MenuActionListener Listener
	 * @param listener The MenuActionListener Listener
	 */
	public synchronized void removeMenuActionListener(MenuActionListener listener) {
		if(listeners != null) {
		    listeners.removeElement(listener);
		}
	}
	
	/**
	 * Over-ride to let Proxy listeners know
	 * Implementors will want to over-ride this and call super()
	 */
	public void actionPerformed(ActionEvent e) {
		if(listeners != null) {
		    DweezilMenuEvent menuEvent = new DweezilMenuEvent(this);
		    for(int i = listeners.size() - 1; i >= 0; i--) {
		        MenuActionListener listener = (MenuActionListener)listeners.elementAt(i);
		        listener.menuActionPerformed(menuEvent);
		    }
		}
	}
}

